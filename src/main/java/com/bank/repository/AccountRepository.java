package com.bank.repository;

import com.bank.model.Account;
import com.bank.model.Customer;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository
extends JpaRepository<Account, Long> {
    // Find account by account number
    Optional<Account> findByAccountNumber(String accountNumber);
    
    // Find all accounts for a customer
    List<Account> findByCustomer(Customer customer);
    
    // Find account by customer's username
    Optional<Account> findFirstByCustomer_User_Username(String username);
}
