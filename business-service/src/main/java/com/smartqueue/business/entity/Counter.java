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
public class Counter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private Integer avgServiceTime;

    @Enumerated(EnumType.STRING)
    private CounterStatus status;

    // 🔥 IMPORTANT FIXES
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "business_id", nullable = false)
    private Business business;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getAvgServiceTime() {
		return avgServiceTime;
	}

	public void setAvgServiceTime(Integer avgServiceTime) {
		this.avgServiceTime = avgServiceTime;
	}

	public CounterStatus getStatus() {
		return status;
	}

	public void setStatus(CounterStatus status) {
		this.status = status;
	}

	public Business getBusiness() {
		return business;
	}

	public void setBusiness(Business business) {
		this.business = business;
	}

    // getters/setters
}