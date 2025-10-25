package com.bank.service;

import com.bank.dto.UpiPaymentRequest;
import com.bank.dto.UpiPaymentResponse;
import com.bank.exception.InsufficientFundsException;
import com.bank.exception.ResourceNotFoundException;
import com.bank.model.*;
import com.bank.repository.AccountRepository;
import com.bank.repository.TransactionRepository;
import com.bank.repository.UpiTransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class UpiService {
    
    @Autowired
    private UpiTransactionRepository upiTransactionRepository;
    
    @Autowired
    private AccountRepository accountRepository;
    
    @Autowired
    private TransactionRepository transactionRepository;
    
    @Transactional
    public UpiPaymentResponse initiateUpiPayment(String username, UpiPaymentRequest request) {
        // Find sender's account
        Account senderAccount = accountRepository.findFirstByCustomer_User_Username(username)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found for user: " + username));
        
        // Validate sufficient balance
        if (senderAccount.getBalance() < request.getAmount()) {
            throw new InsufficientFundsException("Insufficient balance for UPI payment");
        }
        
        // Create UPI transaction
        UpiTransaction upiTransaction = new UpiTransaction();
        upiTransaction.setAccount(senderAccount);
        upiTransaction.setUpiTransactionId("UPI" + UUID.randomUUID().toString().replace("-", "").substring(0, 16).toUpperCase());
        upiTransaction.setUpiId(request.getUpiId());
        upiTransaction.setReceiverUpiId(request.getReceiverUpiId());
        upiTransaction.setProvider(request.getProvider());
        upiTransaction.setAmount(request.getAmount());
        upiTransaction.setStatus(PaymentStatus.INITIATED);
        upiTransaction.setDescription(request.getDescription());
        upiTransaction.setRemarks(request.getRemarks());
        upiTransaction.setReferenceNumber("REF" + System.currentTimeMillis());
        
        // Deduct amount from sender
        senderAccount.setBalance(senderAccount.getBalance() - request.getAmount());
        accountRepository.save(senderAccount);
        
        // Save UPI transaction
        upiTransaction.setStatus(PaymentStatus.SUCCESS);
        UpiTransaction saved = upiTransactionRepository.save(upiTransaction);
        
        // Create corresponding bank transaction record
        Transaction transaction = new Transaction();
        transaction.setAccount(senderAccount);
        transaction.setType(TransactionType.WITHDRAWAL);
        transaction.setAmount(request.getAmount());
        transaction.setDescription("UPI Payment to " + request.getReceiverUpiId());
        transactionRepository.save(transaction);
        
        // Build response
        UpiPaymentResponse response = new UpiPaymentResponse();
        response.setUpiTransactionId(saved.getUpiTransactionId());
        response.setUpiId(saved.getUpiId());
        response.setReceiverUpiId(saved.getReceiverUpiId());
        response.setAmount(saved.getAmount());
        response.setProvider(saved.getProvider());
        response.setStatus(saved.getStatus());
        response.setReferenceNumber(saved.getReferenceNumber());
        response.setDescription(saved.getDescription());
        response.setTransactionDate(saved.getTransactionDate());
        response.setMessage("UPI payment processed successfully");
        
        return response;
    }
    
    public UpiPaymentResponse getUpiTransactionDetails(String upiTransactionId) {
        UpiTransaction transaction = upiTransactionRepository.findByUpiTransactionId(upiTransactionId)
                .orElseThrow(() -> new ResourceNotFoundException("UPI transaction not found: " + upiTransactionId));
        
        UpiPaymentResponse response = new UpiPaymentResponse();
        response.setUpiTransactionId(transaction.getUpiTransactionId());
        response.setUpiId(transaction.getUpiId());
        response.setReceiverUpiId(transaction.getReceiverUpiId());
        response.setAmount(transaction.getAmount());
        response.setProvider(transaction.getProvider());
        response.setStatus(transaction.getStatus());
        response.setReferenceNumber(transaction.getReferenceNumber());
        response.setDescription(transaction.getDescription());
        response.setTransactionDate(transaction.getTransactionDate());
        
        return response;
    }
    
    public Page<UpiPaymentResponse> getUpiTransactionHistory(String username, Pageable pageable) {
        Account account = accountRepository.findFirstByCustomer_User_Username(username)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found for user: " + username));
        
        return upiTransactionRepository.findByAccountId(account.getId(), pageable)
                .map(this::convertToResponse);
    }
    
    public List<UpiTransaction> getUpiTransactionsByStatus(String username, PaymentStatus status) {
        Account account = accountRepository.findFirstByCustomer_User_Username(username)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found for user: " + username));
        
        return upiTransactionRepository.findByAccountIdAndStatus(account.getId(), status);
    }
    
    public boolean verifyUpiId(String upiId) {
        // Basic UPI ID validation (format: username@provider)
        if (upiId == null || !upiId.contains("@")) {
            return false;
        }
        
        String[] parts = upiId.split("@");
        return parts.length == 2 && parts[0].length() > 0 && parts[1].length() > 0;
    }
    
    private UpiPaymentResponse convertToResponse(UpiTransaction transaction) {
        UpiPaymentResponse response = new UpiPaymentResponse();
        response.setUpiTransactionId(transaction.getUpiTransactionId());
        response.setUpiId(transaction.getUpiId());
        response.setReceiverUpiId(transaction.getReceiverUpiId());
        response.setAmount(transaction.getAmount());
        response.setProvider(transaction.getProvider());
        response.setStatus(transaction.getStatus());
        response.setReferenceNumber(transaction.getReferenceNumber());
        response.setDescription(transaction.getDescription());
        response.setTransactionDate(transaction.getTransactionDate());
        
        return response;
    }
}
