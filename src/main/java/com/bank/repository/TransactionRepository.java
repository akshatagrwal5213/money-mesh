package com.bank.repository;

import com.bank.model.Account;
import com.bank.model.Transaction;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TransactionRepository
extends JpaRepository<Transaction, Long> {
    public Page<Transaction> findByAccount_AccountNumber(String var1, Pageable var2);

    @Query(value = "SELECT t FROM Transaction t ORDER BY t.date DESC")
    public List<Transaction> findAllByOrderByTransactionDateDesc();
    
    // New methods for Module 2
    Page<Transaction> findByAccount(Account account, Pageable pageable);
    
    List<Transaction> findByAccountAndDateBetween(Account account, LocalDateTime startDate, LocalDateTime endDate);
    
    Optional<Transaction> findTopByAccountOrderByDateDesc(Account account);
}
