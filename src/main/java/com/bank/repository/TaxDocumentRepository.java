package com.bank.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bank.model.TaxDocument;
import com.bank.model.TaxDocumentType;

@Repository
public interface TaxDocumentRepository extends JpaRepository<TaxDocument, Long> {
    
    List<TaxDocument> findByCustomerId(Long customerId);
    
    List<TaxDocument> findByCustomerIdAndFinancialYear(Long customerId, String financialYear);
    
    List<TaxDocument> findByCustomerIdAndDocumentType(Long customerId, TaxDocumentType documentType);
    
    List<TaxDocument> findByCustomerIdAndFinancialYearAndDocumentType(Long customerId, String financialYear, TaxDocumentType documentType);
}
