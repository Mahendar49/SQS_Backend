package com.smartqueue.business.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
@Entity
public class Queue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String serviceName;

    @Enumerated(EnumType.STRING)
    private PriorityType priorityType;

    // 🔥 IMPORTANT: LINK TO COUNTER (NOT JUST BUSINESS)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "counter_id")
    private Counter counter;

    // 🔥 Keep business (optional but useful)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "business_id")
    private Business business;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public PriorityType getPriorityType() {
		return priorityType;
	}

	public void setPriorityType(PriorityType priorityType) {
		this.priorityType = priorityType;
	}

	public Counter getCounter() {
		return counter;
	}

	public void setCounter(Counter counter) {
		this.counter = counter;
	}

	public Business getBusiness() {
		return business;
	}

	public void setBusiness(Business business) {
		this.business = business;
	}

    // getters/setters
}