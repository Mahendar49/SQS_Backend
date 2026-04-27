package com.smartqueue.queue.service;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.smartqueue.queue.client.BusinessClient;
import com.smartqueue.queue.dto.ApiResponse;
import com.smartqueue.queue.dto.CounterResponse;
import com.smartqueue.queue.dto.JoinQueueResponse;
import com.smartqueue.queue.dto.QueueDashboard;
import com.smartqueue.queue.dto.UserQueueDashboard;
import com.smartqueue.queue.entity.Cancellation;
import com.smartqueue.queue.entity.NotificationType;
import com.smartqueue.queue.entity.PriorityType;
import com.smartqueue.queue.entity.QueueToken;
import com.smartqueue.queue.entity.TokenStatus;
import com.smartqueue.queue.exception.BadRequestException;
import com.smartqueue.queue.exception.ForbiddenException;
import com.smartqueue.queue.repository.CancellationRepository;
import com.smartqueue.queue.repository.QueueTokenRepository;
import com.smartqueue.queue.repository.StandbyUserRepository;
import com.smartqueue.queue.util.AuthRoleUtil;
import com.smartqueue.queue.util.PriorityUtil;

@Service
public class QueueService {

	private final QueueTokenRepository repository;
	private final BusinessClient businessClient;
	private final CancellationRepository cancellationRepository;
	private final NotificationService notificationService;
	private final StandbyUserRepository standbyRepository; // 🔥 FIXED MISSING DEPENDENCY

	private static final Logger log = LoggerFactory.getLogger(QueueService.class);

	private final AnalyticsService analyticsService; // 🔥 ADDED

	public QueueService(QueueTokenRepository repository, BusinessClient businessClient,
			CancellationRepository cancellationRepository, NotificationService notificationService,
			StandbyUserRepository standbyRepository, AnalyticsService analyticsService) { // 🔥 UPDATED

		this.repository = repository;
		this.businessClient = businessClient;
		this.cancellationRepository = cancellationRepository;
		this.notificationService = notificationService;
		this.standbyRepository = standbyRepository;
		this.analyticsService = analyticsService;
	}

	// ================= JOIN =================

	public JoinQueueResponse joinQueue(Long userId, Long queueId, PriorityType priorityType) {

		boolean exists = repository.existsByUserIdAndQueueIdAndStatus(userId, queueId, TokenStatus.WAITING);

		if (exists) {
			return new JoinQueueResponse(false, null, null, "Already in queue");
		}

		QueueToken token = addToQueue(userId, queueId, priorityType);

		analyticsService.trackJoin(queueId); // 🔥 ADDED

		notificationService.notifyIfImportant(userId, "Joined queue", token.getPosition(), token.getCounterId(),
				NotificationType.QUEUE_JOIN);

		rebalanceQueue(queueId, token.getCounterId()); // 🔥 IMPORTANT
		smartRebalance(queueId);

		log.info("User {} joined queue {}", userId, queueId);

		return new JoinQueueResponse(true, token.getId(), token.getPosition(), "Joined");
	}

	// ================= ADD =================

	private QueueToken addToQueue(Long userId, Long queueId, PriorityType priorityType) {

		ApiResponse<List<CounterResponse>> response = businessClient.getCounters(queueId);

		List<CounterResponse> counters = response.getData();

		if (counters == null || counters.isEmpty()) {
			throw new RuntimeException("No counters available");
		}

		Long counterId = counters.get(0).getId();

		List<QueueToken> tokens = repository.findByQueueIdAndCounterIdAndStatus(queueId, counterId,
				TokenStatus.WAITING);

		QueueToken token = new QueueToken();
		token.setUserId(userId);
		token.setQueueId(queueId);
		token.setCounterId(counterId);
		token.setStatus(TokenStatus.WAITING);
		token.setPriorityType(priorityType);
		token.setCreatedAt(LocalDateTime.now());

		tokens.add(token);

		tokens.sort((a, b) -> {
			int p1 = PriorityUtil.getPriorityWeight(a.getPriorityType());
			int p2 = PriorityUtil.getPriorityWeight(b.getPriorityType());
			if (p1 != p2)
				return p1 - p2;
			return a.getCreatedAt().compareTo(b.getCreatedAt());
		});

		for (int i = 0; i < tokens.size(); i++) {
			tokens.get(i).setPosition(i + 1);
		}

		repository.saveAll(tokens);

		return token;
	}

	// ================= COMPLETE =================

	public String completeToken(Long tokenId, Long userId) {

    QueueToken token = repository.findById(tokenId)
            .orElseThrow(() -> new BadRequestException("Token not found"));

    // 🔐 SECURITY CHECK
    if (!token.getUserId().equals(userId)) {
        throw new ForbiddenException("You are not allowed to complete this token");
    }

    // 🔥 VALIDATION
    if (token.getStatus() == TokenStatus.COMPLETED) {
        throw new BadRequestException("Token already completed");
    }

    if (token.getStatus() == TokenStatus.CANCELLED) {
        throw new BadRequestException("Cannot complete cancelled token");
    }

    // 🔥 UPDATE STATUS
    token.setStatus(TokenStatus.COMPLETED);
    repository.save(token);

    // 🔥 ANALYTICS
    analyticsService.trackServed(token.getQueueId());

    long eta = calculateETA(
            token.getQueueId(),
            token.getCounterId(),
            token.getPosition() != null ? token.getPosition() : 1
    );

    analyticsService.trackWaitTime(token.getQueueId(), eta);

    // 🔥 QUEUE FLOW
    serveNext(token.getQueueId(), token.getCounterId());
    promoteFromStandby(token.getQueueId());
    smartRebalance(token.getQueueId());

    log.info("Token {} completed by user {}", tokenId, userId);

    return "Token completed successfully";
}
	// ================= CANCEL =================

	public String cancelToken(Long tokenId, String reason, Long requesterUserId, String requesterRoles) {

		QueueToken token = repository.findById(tokenId).orElseThrow(() -> new BadRequestException("Token not found"));

		boolean isOwner = token.getUserId().equals(requesterUserId);
		boolean isAdmin = AuthRoleUtil.parseRoles(requesterRoles).contains("ADMIN");

		if (!isOwner && !isAdmin) {
			throw new ForbiddenException("You are not allowed to cancel this token");
		}

		// 🔥 VALIDATION (IMPORTANT)
		if (token.getStatus() == TokenStatus.CANCELLED) {
			throw new BadRequestException("Token already cancelled");
		}

		if (token.getStatus() == TokenStatus.COMPLETED) {
			throw new BadRequestException("Cannot cancel completed token");
		}

		// 🔥 UPDATE STATUS
		token.setStatus(TokenStatus.CANCELLED);
		repository.save(token);

		// 🔥 SAVE CANCEL RECORD
		Cancellation c = new Cancellation();
		c.setTokenId(tokenId);
		c.setUserId(token.getUserId());
		c.setReason(reason);
		cancellationRepository.save(c);

		// 🔥 REST OF FLOW
		analyticsService.trackCancel(token.getQueueId());

		notificationService.notifyIfImportant(token.getUserId(), "Token cancelled", null, null,
				NotificationType.CANCELLED);

		rebalanceQueue(token.getQueueId(), token.getCounterId());
		serveNext(token.getQueueId(), token.getCounterId());
		promoteFromStandby(token.getQueueId());
		smartRebalance(token.getQueueId());

		return "Cancelled";
	}

	// ================= SERVE NEXT =================

	private void serveNext(Long queueId, Long counterId) {

		repository.findTopByQueueIdAndCounterIdAndStatusOrderByPositionAsc(queueId, counterId, TokenStatus.WAITING)
				.ifPresent(next -> {

					next.setStatus(TokenStatus.SERVING);
					repository.save(next);

					notificationService.notifyIfImportant(next.getUserId(), "🔥 Your turn now!", next.getPosition(),
							counterId, NotificationType.TURN_ALERT);
				});
	}

	// ================= REBALANCE =================

	private void rebalanceQueue(Long queueId, Long counterId) {

		List<QueueToken> tokens = repository.findByQueueIdAndCounterIdAndStatus(queueId, counterId,
				TokenStatus.WAITING);

		tokens.sort((a, b) -> {
			int p1 = PriorityUtil.getPriorityWeight(a.getPriorityType());
			int p2 = PriorityUtil.getPriorityWeight(b.getPriorityType());
			if (p1 != p2)
				return p1 - p2;
			return a.getCreatedAt().compareTo(b.getCreatedAt());
		});

		for (int i = 0; i < tokens.size(); i++) {

			tokens.get(i).setPosition(i + 1);

			notificationService.notifyIfImportant(tokens.get(i).getUserId(), "Position updated",
					tokens.get(i).getPosition(), tokens.get(i).getCounterId(), NotificationType.GENERAL);
		}

		repository.saveAll(tokens);
	}

	// ================= SMART REBALANCE =================

	private void smartRebalance(Long queueId) {

		ApiResponse<List<CounterResponse>> response = businessClient.getCounters(queueId);

		List<CounterResponse> counters = response.getData();
		if (counters == null || counters.size() < 2)
			return;

		Long heavy = null, light = null;
		long max = Long.MIN_VALUE, min = Long.MAX_VALUE;

		for (CounterResponse c : counters) {

			long count = repository.countByQueueIdAndCounterIdAndStatus(queueId, c.getId(), TokenStatus.WAITING);

			long load = count * c.getAvgServiceTime();

			if (load > max) {
				max = load;
				heavy = c.getId();
			}
			if (load < min) {
				min = load;
				light = c.getId();
			}
		}

		if (heavy == null || light == null || heavy.equals(light))
			return;

		List<QueueToken> heavyList = repository.findByQueueIdAndCounterIdAndStatus(queueId, heavy, TokenStatus.WAITING);

		if (heavyList.size() <= 1)
			return;

		for (int i = heavyList.size() - 1; i >= 0; i--) {

			QueueToken t = heavyList.get(i);

			if (t.getPriorityType() != PriorityType.EMERGENCY) {

				t.setCounterId(light);
				repository.save(t);

				notificationService.notifyIfImportant(t.getUserId(), "Moved to another counter", t.getPosition(), light,
						NotificationType.GENERAL);
				break;
			}
		}

		rebalanceQueue(queueId, heavy);
		rebalanceQueue(queueId, light);
	}

	// ================= STANDBY PROMOTION =================

	private void promoteFromStandby(Long queueId) {

		var standbyList = standbyRepository.findByQueueIdOrderByJoinedAtAsc(queueId);

		if (standbyList.isEmpty())
			return;

		var next = standbyList.get(0);
		standbyRepository.delete(next);

		QueueToken token = addToQueue(next.getUserId(), queueId, PriorityType.NORMAL);

		notificationService.notifyIfImportant(next.getUserId(), "Moved from standby", token.getPosition(),
				token.getCounterId(), NotificationType.STANDBY_CALL);
	}

	// ================= ETA =================

	public long calculateETA(Long queueId, Long counterId, Integer position) {

		if (position == null)
			position = 1;

		ApiResponse<List<CounterResponse>> response = businessClient.getCounters(queueId);

		List<CounterResponse> counters = response.getData();

		if (counters == null || counters.isEmpty()) {
			throw new RuntimeException("No counters available");
		}

		CounterResponse counter = counters.stream().filter(c -> c.getId().equals(counterId)).findFirst()
				.orElseThrow(() -> new RuntimeException("Counter not found"));

		int avgServiceTime = counter.getAvgServiceTime();

		return (long) (position - 1) * avgServiceTime;
	}

	public UserQueueDashboard getUserDashboard(Long userId, Long queueId) {

	    QueueToken token = repository
	            .findTopByUserIdAndQueueIdAndStatusInOrderByCreatedAtDesc(
	                    userId,
	                    queueId,
	                    List.of(TokenStatus.WAITING, TokenStatus.SERVING)
	            )
	            .orElseThrow(() -> new RuntimeException("User not in active queue"));

	    UserQueueDashboard dto = new UserQueueDashboard();

	    // 🔹 BASIC INFO
	    dto.setTokenId(token.getId());
	    dto.setPosition(token.getPosition());
	    dto.setStatus(token.getStatus().name());
	    dto.setCounterId(token.getCounterId());

	    // 🔥 ADD HERE (RIGHT AFTER POSITION)
	    dto.setUsersAhead(token.getPosition() - 1);
	    dto.setNearTurn(token.getPosition() <= 2);

	    // 🔹 ETA
	    long eta = calculateETA(
	            token.getQueueId(),
	            token.getCounterId(),
	            token.getPosition()
	    );

	    dto.setEtaSeconds(eta);

	    // 🔹 MESSAGE
	    dto.setMessage(
	            token.getStatus() == TokenStatus.SERVING
	                    ? "🔥 It's your turn now!"
	                    : "Please wait"
	    );

	    return dto;
	}

	public QueueDashboard getQueueDashboard(Long queueId) {

    List<QueueToken> waiting =
            repository.findByQueueIdAndStatusOrderByPositionAsc(
                    queueId, TokenStatus.WAITING
            );

    List<QueueToken> serving =
            repository.findByQueueIdAndStatusOrderByPositionAsc(
                    queueId, TokenStatus.SERVING
            );

    QueueDashboard dto = new QueueDashboard();

    dto.setQueueId(queueId);
    dto.setTotalWaiting(waiting.size());
    dto.setTotalServing(serving.size());

    if (!serving.isEmpty()) {
        dto.setCurrentServingToken(serving.get(0).getId());
    }

    // 🔥 REAL ETA CALCULATION
    long totalWait = 0;
    for (QueueToken t : waiting) {
        totalWait += calculateETA(queueId, t.getCounterId(), t.getPosition());
    }

    dto.setAvgWaitTime(
            waiting.isEmpty() ? 0 : totalWait / waiting.size()
    );

    return dto;
}
}
