package com.smartqueue.queue.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.smartqueue.queue.entity.Cancellation;

public interface CancellationRepository extends JpaRepository<Cancellation, Long> {

}
