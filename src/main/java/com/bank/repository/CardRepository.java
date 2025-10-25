package com.bank.repository;

import com.bank.model.Account;
import com.bank.model.Card;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CardRepository
extends JpaRepository<Card, Long> {
    public Card findByCardNumber(String var1);

    public Card findByAccount_Id(Long var1);
    
    // Module 3 additions
    List<Card> findByAccount(Account account);
}
