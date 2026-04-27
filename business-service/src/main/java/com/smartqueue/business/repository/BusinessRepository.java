package com.smartqueue.business.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.smartqueue.business.entity.Business;

public interface BusinessRepository extends JpaRepository<Business, Long> {
	Optional<Business> findByUserId(String userId);
}