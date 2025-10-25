package com.bank.service;

import com.bank.dto.BillPaymentRequest;
import com.bank.dto.BillPaymentResponse;
import com.bank.exception.InsufficientFundsException;
import com.bank.exception.ResourceNotFoundException;
import com.bank.model.*;
import com.bank.repository.AccountRepository;
import com.bank.repository.BillPaymentRepository;
import com.bank.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class BillPaymentService {
    
    @Autowired
    private BillPaymentRepository billPaymentRepository;
    
    @Autowired
    private AccountRepository accountRepository;
    
    @Autowired
    private TransactionRepository transactionRepository;
    
    @Transactional
    public BillPaymentResponse payBill(String username, BillPaymentRequest request) {
        // Find customer's account
        Account account = accountRepository.findFirstByCustomer_User_Username(username)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found for user: " + username));
        
        // Calculate total amount
        Double convObj = request.getConvenienceFee();
        double convenienceFee = convObj != null ? convObj : 0.0;
        double totalAmount = request.getBillAmount() + convenienceFee;
        
        // Validate sufficient balance
        if (account.getBalance() < totalAmount) {
            throw new InsufficientFundsException("Insufficient balance to pay bill");
        }
        
        // Create bill payment record
        BillPayment billPayment = new BillPayment();
        billPayment.setAccount(account);
        billPayment.setBillPaymentId("BILL" + UUID.randomUUID().toString().replace("-", "").substring(0, 14).toUpperCase());
        billPayment.setBillType(request.getBillType());
        billPayment.setBillerName(request.getBillerName());
        billPayment.setBillerCode(request.getBillerCode());
        billPayment.setConsumerNumber(request.getConsumerNumber());
        billPayment.setBillAmount(request.getBillAmount());
        billPayment.setConvenienceFee(convenienceFee);
        billPayment.setTotalAmount(totalAmount);
        billPayment.setPaymentStatus(PaymentStatus.INITIATED);
        billPayment.setPaymentMethod(request.getPaymentMethod());
        billPayment.setDueDate(request.getDueDate());
        billPayment.setRemarks(request.getRemarks());
        billPayment.setAutoPayEnabled(request.isEnableAutoPay());
        billPayment.setPaymentDate(LocalDateTime.now());
        billPayment.setTransactionId("TXN" + System.currentTimeMillis());
        billPayment.setReferenceNumber("REF" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase());
        billPayment.setReceiptNumber("RCP" + System.currentTimeMillis());
        
        // Deduct amount from account
        account.setBalance(account.getBalance() - totalAmount);
        accountRepository.save(account);
        
        // Update payment status to success
        billPayment.setPaymentStatus(PaymentStatus.SUCCESS);
        BillPayment saved = billPaymentRepository.save(billPayment);
        
        // Create corresponding transaction record
        Transaction transaction = new Transaction();
        transaction.setAccount(account);
        transaction.setType(TransactionType.WITHDRAWAL);
        transaction.setAmount(totalAmount);
        transaction.setDescription(request.getBillType() + " Bill Payment - " + request.getBillerName());
        transactionRepository.save(transaction);
        
        // Build response
        return buildResponse(saved, "Bill payment processed successfully");
    }
    
    public BillPaymentResponse getBillPaymentDetails(String billPaymentId) {
        BillPayment billPayment = billPaymentRepository.findByBillPaymentId(billPaymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Bill payment not found: " + billPaymentId));
        
        return buildResponse(billPayment, null);
    }
    
    public Page<BillPaymentResponse> getBillPaymentHistory(String username, Pageable pageable) {
        Account account = accountRepository.findFirstByCustomer_User_Username(username)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found for user: " + username));
        
        return billPaymentRepository.findByAccountId(account.getId(), pageable)
                .map(bp -> buildResponse(bp, null));
    }
    
    public List<BillPaymentResponse> getBillPaymentsByType(String username, BillType billType) {
        Account account = accountRepository.findFirstByCustomer_User_Username(username)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found for user: " + username));
        
        return billPaymentRepository.findByAccountIdAndBillType(account.getId(), billType)
                .stream()
                .map(bp -> buildResponse(bp, null))
                .collect(Collectors.toList());
    }
    
    public List<BillPaymentResponse> getBillPaymentsByStatus(String username, PaymentStatus status) {
        Account account = accountRepository.findFirstByCustomer_User_Username(username)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found for user: " + username));
        
        return billPaymentRepository.findByAccountIdAndPaymentStatus(account.getId(), status)
                .stream()
                .map(bp -> buildResponse(bp, null))
                .collect(Collectors.toList());
    }
    
    public List<BillPaymentResponse> getAutoPayBills(String username) {
        Account account = accountRepository.findFirstByCustomer_User_Username(username)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found for user: " + username));
        
        return billPaymentRepository.findByAccountIdAndIsAutoPayEnabledTrue(account.getId())
                .stream()
                .map(bp -> buildResponse(bp, null))
                .collect(Collectors.toList());
    }
    
    @Transactional
    public void toggleAutoPay(String billPaymentId, boolean enableAutoPay) {
        BillPayment billPayment = billPaymentRepository.findByBillPaymentId(billPaymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Bill payment not found: " + billPaymentId));
        
        billPayment.setAutoPayEnabled(enableAutoPay);
        billPaymentRepository.save(billPayment);
    }
    
    private BillPaymentResponse buildResponse(BillPayment billPayment, String message) {
        BillPaymentResponse response = new BillPaymentResponse();
        response.setBillPaymentId(billPayment.getBillPaymentId());
        response.setBillType(billPayment.getBillType());
        response.setBillerName(billPayment.getBillerName());
        response.setConsumerNumber(billPayment.getConsumerNumber());
        response.setBillAmount(billPayment.getBillAmount());
        response.setConvenienceFee(billPayment.getConvenienceFee());
        response.setTotalAmount(billPayment.getTotalAmount());
        response.setPaymentStatus(billPayment.getPaymentStatus());
        response.setPaymentMethod(billPayment.getPaymentMethod());
        response.setDueDate(billPayment.getDueDate());
        response.setPaymentDate(billPayment.getPaymentDate());
        response.setReferenceNumber(billPayment.getReferenceNumber());
        response.setTransactionId(billPayment.getTransactionId());
        response.setReceiptNumber(billPayment.getReceiptNumber());
        response.setMessage(message);
        
        return response;
    }
}
