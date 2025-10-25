package com.bank.repository;

import com.bank.model.Biller;
import com.bank.model.BillerCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BillerRepository extends JpaRepository<Biller, Long> {
    List<Biller> findByActiveTrue();
    List<Biller> findByCategoryAndActiveTrue(BillerCategory category);
}
