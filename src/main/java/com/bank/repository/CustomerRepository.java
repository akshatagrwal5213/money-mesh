package com.bank.repository;

import com.bank.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerRepository
extends JpaRepository<Customer, Long> {
    Optional<Customer> findByUser_Username(String username);
    Optional<Customer> findByEmail(String email);
}
