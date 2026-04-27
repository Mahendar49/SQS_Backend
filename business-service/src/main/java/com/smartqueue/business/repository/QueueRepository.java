package com.smartqueue.business.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.smartqueue.business.entity.Queue;

public interface QueueRepository extends JpaRepository<Queue, Long> {
	Optional<Queue> findByIdAndBusinessId(Long id, Long businessId);

	List<Queue> findByCounterId(Long counterId);
	
	List<Queue> findByBusinessId(Long businessId);
}