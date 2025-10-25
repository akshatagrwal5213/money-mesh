package com.bank.model;

public enum QrCodeType {
    STATIC,            // Static QR code for receiving payments
    DYNAMIC,           // Dynamic QR code with amount
    MERCHANT,          // Merchant QR code
    PERSONAL           // Personal QR code
}
