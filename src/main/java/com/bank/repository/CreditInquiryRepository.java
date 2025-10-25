package com.bank.repository;

import com.bank.model.CreditInquiry;
import com.bank.model.InquiryType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface CreditInquiryRepository extends JpaRepository<CreditInquiry, Long> {
    
    // Find all inquiries for a customer
    List<CreditInquiry> findByCustomerId(Long customerId);
    
    // Find inquiries by customer and type
    List<CreditInquiry> findByCustomerIdAndInquiryType(Long customerId, InquiryType inquiryType);
    
    // Find recent inquiries
    @Query("SELECT ci FROM CreditInquiry ci WHERE ci.customer.id = :customerId AND ci.inquiryDate >= :sinceDate ORDER BY ci.inquiryDate DESC")
    List<CreditInquiry> findRecentInquiries(
        @Param("customerId") Long customerId,
        @Param("sinceDate") LocalDate sinceDate
    );
    
    // Count hard inquiries in the last X months
    @Query("SELECT COUNT(ci) FROM CreditInquiry ci WHERE ci.customer.id = :customerId AND ci.inquiryType = 'HARD' AND ci.inquiryDate >= :sinceDate")
    Long countHardInquiriesSince(
        @Param("customerId") Long customerId,
        @Param("sinceDate") LocalDate sinceDate
    );
    
    // Find hard inquiries in last 6 months
    @Query("SELECT ci FROM CreditInquiry ci WHERE ci.customer.id = :customerId AND ci.inquiryType = 'HARD' AND ci.inquiryDate >= :sinceDate ORDER BY ci.inquiryDate DESC")
    List<CreditInquiry> findRecentHardInquiries(
        @Param("customerId") Long customerId,
        @Param("sinceDate") LocalDate sinceDate
    );
    
    // Find inquiries by customer ordered by date
    List<CreditInquiry> findByCustomerIdOrderByInquiryDateDesc(Long customerId);
}
