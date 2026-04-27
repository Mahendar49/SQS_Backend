package com.smartqueue.queue.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.smartqueue.queue.entity.StandbyUser;

public interface StandbyUserRepository extends JpaRepository<StandbyUser, Long> {

    List<StandbyUser> findByQueueIdOrderByJoinedAtAsc(Long queueId);

    void deleteByUserIdAndQueueId(Long userId, Long queueId);
}