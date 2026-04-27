package com.smartqueue.business.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.smartqueue.business.dto.BusinessResponse;
import com.smartqueue.business.dto.CounterRequest;
import com.smartqueue.business.dto.CounterResponse;
import com.smartqueue.business.dto.CreateBusinessRequest;
import com.smartqueue.business.dto.QueueRequest;
import com.smartqueue.business.dto.QueueResponse;
import com.smartqueue.business.entity.Business;
import com.smartqueue.business.entity.Counter;
import com.smartqueue.business.entity.CounterStatus;
import com.smartqueue.business.entity.Queue;
import com.smartqueue.business.exception.BusinessOperationException;
import com.smartqueue.business.exception.ResourceNotFoundException;
import com.smartqueue.business.repository.BusinessRepository;
import com.smartqueue.business.repository.CounterRepository;
import com.smartqueue.business.repository.QueueRepository;

@Service
public class BusinessService {

	private final BusinessRepository repository;
	private final CounterRepository counterRepository;
	private final QueueRepository queueRepository;

	public BusinessService(BusinessRepository repository, CounterRepository counterRepository,
			QueueRepository queueRepository) {
		this.repository = repository;
		this.counterRepository = counterRepository;
		this.queueRepository = queueRepository;
	}

	// ================= BUSINESS =================

	public BusinessResponse createBusiness(CreateBusinessRequest request, String userId) {

		repository.findByUserId(userId).ifPresent(b -> {
			throw new BusinessOperationException("User already has a business");
		});

		Business business = new Business();
		business.setName(request.getName());
		business.setLocation(request.getLocation());
		business.setUserId(userId);

		Business saved = repository.save(business);

		return new BusinessResponse(saved.getId(), saved.getName(), saved.getLocation());
	}

	public Business getById(Long id) {
		return repository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Business not found with ID: " + id));
	}

	public BusinessResponse getBusinessByUser(String userId) {

	    Business business = repository.findByUserId(userId)
	            .orElseThrow(() -> new ResourceNotFoundException("No business found"));

	    return new BusinessResponse(
	            business.getId(),
	            business.getName(),
	            business.getLocation()
	    );
	}
	// ================= COUNTER =================

	public void addCounter(Long businessId, CounterRequest request, String userId) {

		Business business = getById(businessId);

		validateOwnership(business, userId);

		Counter counter = new Counter();
		counter.setName(request.getName());
		counter.setAvgServiceTime(request.getAvgServiceTime());
		counter.setStatus(CounterStatus.OPEN);
		counter.setBusiness(business);

		counterRepository.save(counter);
	}

//	public List<CounterResponse> getCounters(Long businessId, String userId) {
//
//		Business business = getById(businessId);
//
//		validateOwnership(business, userId);
//
//		return counterRepository.findByBusinessId(business.getId()).stream().map(this::mapToResponse).toList();
//	}
	
	public List<CounterResponse> getCounters(Long businessId, String userId) {

	    Business business = getById(businessId);

	    // 🔥 OPTIONAL: Only validate if userId is present (safety)
	    // But DO NOT block normal users

	    List<Counter> counters = counterRepository.findByBusinessId(business.getId());

	    if (counters.isEmpty()) {
	        throw new ResourceNotFoundException("No counters available for this business");
	    }

	    return counters.stream()
	            .map(this::mapToResponse)
	            .toList();
	}
	
	// ================= USER: GET ALL BUSINESSES =================

	public List<BusinessResponse> getAllBusinesses() {

	    return repository.findAll()
	            .stream()
	            .map(b -> new BusinessResponse(
	                    b.getId(),
	                    b.getName(),
	                    b.getLocation()
	            ))
	            .toList();
	}
	
	public List<CounterResponse> getCountersForUser(Long businessId) {

	    Business business = getById(businessId);

	    List<Counter> counters = counterRepository.findByBusinessId(business.getId());

	    if (counters.isEmpty()) {
	        throw new ResourceNotFoundException("No counters available for this business");
	    }

	    return counters.stream()
	            .map(this::mapToResponse)
	            .toList();
	}

	public List<QueueResponse> getQueuesForUser(Long businessId) {

	    Business business = getById(businessId);

	    List<Queue> queues = queueRepository.findByBusinessId(businessId);

	    if (queues.isEmpty()) {
	        throw new ResourceNotFoundException("No queues available for this business");
	    }

	    return queues.stream()
	            .map(q -> new QueueResponse(
	                    q.getId(),
	                    q.getServiceName(),
	                    q.getPriorityType().name()
	            ))
	            .toList();
	}
	// ================= NEW FEATURES =================

	// 🔹 Update Counter
	public void updateCounter(Long businessId, Long counterId, CounterRequest request, String userId) {

		Business business = getById(businessId);
		validateOwnership(business, userId);

		Counter counter = counterRepository.findByIdAndBusinessId(counterId, businessId)
				.orElseThrow(() -> new ResourceNotFoundException("Counter not found"));

		counter.setName(request.getName());
		counter.setAvgServiceTime(request.getAvgServiceTime());

		counterRepository.save(counter);
	}

	// 🔹 Open / Close Counter
	public void updateCounterStatus(Long businessId, Long counterId, CounterStatus status, String userId) {

		Business business = getById(businessId);
		validateOwnership(business, userId);

		Counter counter = counterRepository.findByIdAndBusinessId(counterId, businessId)
				.orElseThrow(() -> new ResourceNotFoundException("Counter not found"));

		counter.setStatus(status);

		counterRepository.save(counter);
	}

	// 🔹 Delete Counter
	public void deleteCounter(Long businessId, Long counterId, String userId) {

		Business business = getById(businessId);
		validateOwnership(business, userId);

		Counter counter = counterRepository.findByIdAndBusinessId(counterId, businessId)
				.orElseThrow(() -> new ResourceNotFoundException("Counter not found"));

		counterRepository.delete(counter);
	}

	// 🔹 Get ONLY ACTIVE (OPEN) Counters
	public List<CounterResponse> getActiveCounters(Long businessId, String userId) {

		Business business = getById(businessId);
		validateOwnership(business, userId);

		return counterRepository.findByBusinessIdAndStatus(businessId, CounterStatus.OPEN).stream()
				.map(this::mapToResponse).toList();
	}

	// ================= HELPER METHODS =================
	public BusinessResponse getBusinessResponse(Long id, String userId) {

	    Business business = getById(id);

	    validateOwnership(business, userId);

	    return new BusinessResponse(
	            business.getId(),
	            business.getName(),
	            business.getLocation()
	    );
	}
	public BusinessResponse getBusinessResponseForUSer(Long id) {

	    Business business = getById(id);

	    return new BusinessResponse(
	            business.getId(),
	            business.getName(),
	            business.getLocation()
	    );
	}
	private void validateOwnership(Business business, String userId) {
		if (!business.getUserId().equals(userId)) {
			throw new RuntimeException("Unauthorized access");
		}
	}

	private CounterResponse mapToResponse(Counter c) {
		return new CounterResponse(c.getId(), c.getName(), c.getAvgServiceTime());
	}

	public void createQueue(Long businessId, Long counterId, QueueRequest request, String userId) {

	    Business business = getById(businessId);
	    validateOwnership(business, userId);

	    Counter counter = counterRepository.findByIdAndBusinessId(counterId, businessId)
	            .orElseThrow(() -> new ResourceNotFoundException("Counter not found"));

	    Queue queue = new Queue();
	    queue.setServiceName(request.getServiceName());

	    queue.setPriorityType(request.getPriorityType());

	    queue.setBusiness(business);
	    queue.setCounter(counter);

	    queueRepository.save(queue);
	}

	public List<QueueResponse> getQueues(Long businessId, String userId) {

	    Business business = getById(businessId);
	    validateOwnership(business, userId);

	    return queueRepository.findByBusinessId(businessId)
	            .stream()
	            .map(q -> new QueueResponse(
	                    q.getId(),
	                    q.getServiceName(),
	                    q.getPriorityType().name()
	            ))
	            .toList();
	}
	
	public List<QueueResponse> getQueuesByCounter(Long businessId, Long counterId, String userId) {

	    // 1️⃣ Check business exists
	    Business business = getById(businessId);
	    validateOwnership(business, userId);

	    // 2️⃣ 🔥 IMPORTANT: Check counter exists under this business
	    Counter counter = counterRepository.findByIdAndBusinessId(counterId, businessId)
	            .orElseThrow(() -> new ResourceNotFoundException(
	                    "Counter not found with id: " + counterId + " for this business"
	            ));

	    // 3️⃣ Fetch queues
	    List<Queue> queues = queueRepository.findByCounterId(counter.getId());

	    // 4️⃣ Map response
	    return queues.stream()
	            .map(q -> new QueueResponse(
	                    q.getId(),
	                    q.getServiceName(),
	                    q.getPriorityType().name()
	            ))
	            .toList();
	}

	public void deleteQueue(Long businessId, Long queueId, String userId) {

	    Business business = getById(businessId);
	    validateOwnership(business, userId);

	    Queue queue = queueRepository.findByIdAndBusinessId(queueId, businessId)
	            .orElseThrow(() -> new ResourceNotFoundException("Queue not found"));

	    queueRepository.delete(queue);
	}

}