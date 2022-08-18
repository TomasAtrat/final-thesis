package com.gmail.tomasatrat.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gmail.tomasatrat.backend.data.entity.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
}
