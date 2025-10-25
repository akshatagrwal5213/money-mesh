package com.bank.service;

import com.bank.dto.QrCodeRequest;
import com.bank.dto.QrCodeResponse;
import com.bank.exception.ResourceNotFoundException;
import com.bank.model.Account;
import com.bank.model.QrCode;
import com.bank.model.QrCodeType;
import com.bank.repository.AccountRepository;
import com.bank.repository.QrCodeRepository;
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
public class QrCodeService {
    
    @Autowired
    private QrCodeRepository qrCodeRepository;
    
    @Autowired
    private AccountRepository accountRepository;
    
    @Transactional
    public QrCodeResponse generateQrCode(String username, QrCodeRequest request) {
        Account account = accountRepository.findFirstByCustomer_User_Username(username)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found for user: " + username));
        
        // Create QR code entity
        QrCode qrCode = new QrCode();
        qrCode.setAccount(account);
        qrCode.setQrCodeId("QR" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase());
        qrCode.setType(request.getType());
        qrCode.setAmount(request.getAmount());
        qrCode.setMerchantName(request.getMerchantName() != null ? request.getMerchantName() : account.getCustomer().getName());
        qrCode.setMerchantVpa(request.getMerchantVpa() != null ? request.getMerchantVpa() : generateUpiId(username));
        qrCode.setActive(true);
        qrCode.setMaxUsageLimit(request.getMaxUsageLimit());
        
        // Set expiry for dynamic QR codes
        if (request.getType() == QrCodeType.DYNAMIC && request.getValidityMinutes() != null) {
            qrCode.setExpiryDate(LocalDateTime.now().plusMinutes(request.getValidityMinutes()));
        }
        
        // Generate QR data (UPI payment string)
        String qrData = generateQrData(qrCode);
        qrCode.setQrData(qrData);
        
        QrCode saved = qrCodeRepository.save(qrCode);
        
        // Build response
        QrCodeResponse response = new QrCodeResponse();
        response.setQrCodeId(saved.getQrCodeId());
        response.setType(saved.getType());
        response.setQrData(saved.getQrData());
        response.setAmount(saved.getAmount());
        response.setMerchantName(saved.getMerchantName());
        response.setGeneratedDate(saved.getGeneratedDate());
        response.setExpiryDate(saved.getExpiryDate());
        response.setActive(saved.isActive());
        response.setUsageCount(saved.getUsageCount());
        response.setMessage("QR code generated successfully");
        
        return response;
    }
    
    public QrCodeResponse getQrCodeDetails(String qrCodeId) {
        QrCode qrCode = qrCodeRepository.findByQrCodeId(qrCodeId)
                .orElseThrow(() -> new ResourceNotFoundException("QR code not found: " + qrCodeId));
        
        return convertToResponse(qrCode);
    }
    
    public Page<QrCodeResponse> getQrCodeHistory(String username, Pageable pageable) {
        Account account = accountRepository.findFirstByCustomer_User_Username(username)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found for user: " + username));
        
        return qrCodeRepository.findByAccountId(account.getId(), pageable)
                .map(this::convertToResponse);
    }
    
    public List<QrCodeResponse> getActiveQrCodes(String username) {
        Account account = accountRepository.findFirstByCustomer_User_Username(username)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found for user: " + username));
        
        return qrCodeRepository.findByAccountIdAndIsActiveTrue(account.getId())
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public void deactivateQrCode(String qrCodeId) {
        QrCode qrCode = qrCodeRepository.findByQrCodeId(qrCodeId)
                .orElseThrow(() -> new ResourceNotFoundException("QR code not found: " + qrCodeId));
        
        qrCode.setActive(false);
        qrCodeRepository.save(qrCode);
    }
    
    @Transactional
    public QrCodeResponse scanQrCode(String qrCodeId) {
        QrCode qrCode = qrCodeRepository.findByQrCodeId(qrCodeId)
                .orElseThrow(() -> new ResourceNotFoundException("QR code not found: " + qrCodeId));
        
        // Validate QR code
        if (!qrCode.isActive()) {
            throw new IllegalStateException("QR code is inactive");
        }
        
        if (qrCode.getExpiryDate() != null && qrCode.getExpiryDate().isBefore(LocalDateTime.now())) {
            qrCode.setActive(false);
            qrCodeRepository.save(qrCode);
            throw new IllegalStateException("QR code has expired");
        }
        
        if (qrCode.getMaxUsageLimit() != null && qrCode.getUsageCount() >= qrCode.getMaxUsageLimit()) {
            qrCode.setActive(false);
            qrCodeRepository.save(qrCode);
            throw new IllegalStateException("QR code usage limit exceeded");
        }
        
        // Increment usage count
        qrCode.setUsageCount(qrCode.getUsageCount() + 1);
        qrCodeRepository.save(qrCode);
        
        return convertToResponse(qrCode);
    }
    
    private String generateQrData(QrCode qrCode) {
        // Generate UPI payment string format
        StringBuilder upiString = new StringBuilder("upi://pay?");
        upiString.append("pa=").append(qrCode.getMerchantVpa());
        upiString.append("&pn=").append(qrCode.getMerchantName().replace(" ", "%20"));
        
        if (qrCode.getAmount() != null) {
            upiString.append("&am=").append(qrCode.getAmount());
        }
        
        upiString.append("&cu=INR");
        upiString.append("&tn=").append("Payment%20via%20QR%20Code");
        
        return upiString.toString();
    }
    
    private String generateUpiId(String username) {
        // Generate UPI ID in format: username@bankname
        return username.toLowerCase() + "@bankingsystem";
    }
    
    private QrCodeResponse convertToResponse(QrCode qrCode) {
        QrCodeResponse response = new QrCodeResponse();
        response.setQrCodeId(qrCode.getQrCodeId());
        response.setType(qrCode.getType());
        response.setQrData(qrCode.getQrData());
        response.setAmount(qrCode.getAmount());
        response.setMerchantName(qrCode.getMerchantName());
        response.setGeneratedDate(qrCode.getGeneratedDate());
        response.setExpiryDate(qrCode.getExpiryDate());
        response.setActive(qrCode.isActive());
        response.setUsageCount(qrCode.getUsageCount());
        
        return response;
    }
}
