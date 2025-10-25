package com.bank.controller;

import com.bank.dto.*;
import com.bank.model.BillType;
import com.bank.model.PaymentStatus;
import com.bank.service.BillPaymentService;
import com.bank.service.QrCodeService;
import com.bank.service.UpiService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {
    
    @Autowired
    private UpiService upiService;
    
    @Autowired
    private QrCodeService qrCodeService;
    
    @Autowired
    private BillPaymentService billPaymentService;
    
    // ============ UPI Payment Endpoints ============
    
    @PostMapping("/upi/initiate")
    public ResponseEntity<UpiPaymentResponse> initiateUpiPayment(
            @Valid @RequestBody UpiPaymentRequest request,
            Authentication authentication) {
        String username = authentication.getName();
        UpiPaymentResponse response = upiService.initiateUpiPayment(username, request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    
    @GetMapping("/upi/{transactionId}")
    public ResponseEntity<UpiPaymentResponse> getUpiTransaction(@PathVariable String transactionId) {
        UpiPaymentResponse response = upiService.getUpiTransactionDetails(transactionId);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/upi/history")
    public ResponseEntity<Page<UpiPaymentResponse>> getUpiHistory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication) {
        String username = authentication.getName();
        Pageable pageable = PageRequest.of(page, size);
        Page<UpiPaymentResponse> history = upiService.getUpiTransactionHistory(username, pageable);
        return ResponseEntity.ok(history);
    }
    
    @GetMapping("/upi/verify/{upiId}")
    public ResponseEntity<Boolean> verifyUpiId(@PathVariable String upiId) {
        boolean isValid = upiService.verifyUpiId(upiId);
        return ResponseEntity.ok(isValid);
    }
    
    // ============ QR Code Endpoints ============
    
    @PostMapping("/qr/generate")
    public ResponseEntity<QrCodeResponse> generateQrCode(
            @Valid @RequestBody QrCodeRequest request,
            Authentication authentication) {
        String username = authentication.getName();
        QrCodeResponse response = qrCodeService.generateQrCode(username, request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    
    @GetMapping("/qr/{qrCodeId}")
    public ResponseEntity<QrCodeResponse> getQrCode(@PathVariable String qrCodeId) {
        QrCodeResponse response = qrCodeService.getQrCodeDetails(qrCodeId);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/qr/history")
    public ResponseEntity<Page<QrCodeResponse>> getQrCodeHistory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication) {
        String username = authentication.getName();
        Pageable pageable = PageRequest.of(page, size);
        Page<QrCodeResponse> history = qrCodeService.getQrCodeHistory(username, pageable);
        return ResponseEntity.ok(history);
    }
    
    @GetMapping("/qr/active")
    public ResponseEntity<List<QrCodeResponse>> getActiveQrCodes(Authentication authentication) {
        String username = authentication.getName();
        List<QrCodeResponse> activeQrCodes = qrCodeService.getActiveQrCodes(username);
        return ResponseEntity.ok(activeQrCodes);
    }
    
    @PutMapping("/qr/{qrCodeId}/deactivate")
    public ResponseEntity<String> deactivateQrCode(@PathVariable String qrCodeId) {
        qrCodeService.deactivateQrCode(qrCodeId);
        return ResponseEntity.ok("QR code deactivated successfully");
    }
    
    @PostMapping("/qr/{qrCodeId}/scan")
    public ResponseEntity<QrCodeResponse> scanQrCode(@PathVariable String qrCodeId) {
        QrCodeResponse response = qrCodeService.scanQrCode(qrCodeId);
        return ResponseEntity.ok(response);
    }
    
    // ============ Bill Payment Endpoints ============
    
    @PostMapping("/bills/pay")
    public ResponseEntity<BillPaymentResponse> payBill(
            @Valid @RequestBody BillPaymentRequest request,
            Authentication authentication) {
        String username = authentication.getName();
        BillPaymentResponse response = billPaymentService.payBill(username, request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    
    @GetMapping("/bills/{billPaymentId}")
    public ResponseEntity<BillPaymentResponse> getBillPayment(@PathVariable String billPaymentId) {
        BillPaymentResponse response = billPaymentService.getBillPaymentDetails(billPaymentId);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/bills/history")
    public ResponseEntity<Page<BillPaymentResponse>> getBillPaymentHistory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication) {
        String username = authentication.getName();
        Pageable pageable = PageRequest.of(page, size);
        Page<BillPaymentResponse> history = billPaymentService.getBillPaymentHistory(username, pageable);
        return ResponseEntity.ok(history);
    }
    
    @GetMapping("/bills/type/{billType}")
    public ResponseEntity<List<BillPaymentResponse>> getBillPaymentsByType(
            @PathVariable BillType billType,
            Authentication authentication) {
        String username = authentication.getName();
        List<BillPaymentResponse> payments = billPaymentService.getBillPaymentsByType(username, billType);
        return ResponseEntity.ok(payments);
    }
    
    @GetMapping("/bills/status/{status}")
    public ResponseEntity<List<BillPaymentResponse>> getBillPaymentsByStatus(
            @PathVariable PaymentStatus status,
            Authentication authentication) {
        String username = authentication.getName();
        List<BillPaymentResponse> payments = billPaymentService.getBillPaymentsByStatus(username, status);
        return ResponseEntity.ok(payments);
    }
    
    @GetMapping("/bills/autopay")
    public ResponseEntity<List<BillPaymentResponse>> getAutoPayBills(Authentication authentication) {
        String username = authentication.getName();
        List<BillPaymentResponse> autoPayBills = billPaymentService.getAutoPayBills(username);
        return ResponseEntity.ok(autoPayBills);
    }
    
    @PutMapping("/bills/{billPaymentId}/autopay")
    public ResponseEntity<String> toggleAutoPay(
            @PathVariable String billPaymentId,
            @RequestParam boolean enable) {
        billPaymentService.toggleAutoPay(billPaymentId, enable);
        return ResponseEntity.ok("Auto-pay " + (enable ? "enabled" : "disabled") + " successfully");
    }
}
