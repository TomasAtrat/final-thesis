package com.gmail.tomasatrat.backend.repositories;

import com.gmail.tomasatrat.backend.data.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, String> {

}