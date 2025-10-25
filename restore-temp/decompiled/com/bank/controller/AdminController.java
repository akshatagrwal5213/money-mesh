/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.http.ResponseEntity
 *  org.springframework.security.access.prepost.PreAuthorize
 *  org.springframework.web.bind.annotation.GetMapping
 *  org.springframework.web.bind.annotation.RequestMapping
 *  org.springframework.web.bind.annotation.RestController
 */
package com.bank.controller;

import com.bank.model.Account;
import com.bank.model.AppUser;
import com.bank.model.Customer;
import com.bank.repository.AccountRepository;
import com.bank.repository.AppUserRepository;
import com.bank.repository.CardRepository;
import com.bank.repository.CustomerRepository;
import com.bank.repository.PendingTransferRepository;
import com.bank.repository.TransactionRepository;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value={"/api/admin"})
@PreAuthorize(value="hasRole('ADMIN')")
public class AdminController {
    @Autowired
    private AppUserRepository userRepository;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private CardRepository cardRepository;
    @Autowired
    private PendingTransferRepository pendingTransferRepository;
    private static volatile /* synthetic */ int[] $SWITCH_TABLE$com$bank$model$TransactionType;

    public AdminController() {
        throw new Error("Unresolved compilation problems: \n\tType mismatch: cannot convert from ResponseEntity<Object> to ResponseEntity<List<AppUser>>\n\tType mismatch: cannot convert from ResponseEntity<Object> to ResponseEntity<List<Customer>>\n\tType mismatch: cannot convert from ResponseEntity<Object> to ResponseEntity<List<Account>>\n\tType mismatch: cannot convert from Object to List\n\tThe method getAccount() is undefined for the type Object\n\tThe method getId() is undefined for the type Object\n\tThe method getCardNumber() is undefined for the type Object\n\tThe method getType() is undefined for the type Object\n\tThe method getType() is undefined for the type Object\n\tThe method getExpiryDate() is undefined for the type Object\n\tType mismatch: cannot convert from Object to List\n\tThe method getId() is undefined for the type Object\n\tThe method getFromAccount() is undefined for the type Object\n\tThe method getToAccount() is undefined for the type Object\n\tThe method getAmount() is undefined for the type Object\n\tThe method getAmount() is undefined for the type Object\n\tThe method getStatus() is undefined for the type Object\n\tThe method getStatus() is undefined for the type Object\n\tThe method getCreatedAt() is undefined for the type Object\n\tType mismatch: cannot convert from Map<String,Number & Comparable<?> & Constable & ConstantDesc> to Map<String,Double>\n\tType mismatch: cannot convert from ResponseEntity<Map<String,Double>> to ResponseEntity<Map<String,Object>>\n\tType mismatch: cannot convert from Object to List\n\tThe method getRole() is undefined for the type Object\n\tThe method getId() is undefined for the type Object\n\tThe method getUsername() is undefined for the type Object\n");
    }

    @GetMapping(value={"/users"})
    public ResponseEntity<List<AppUser>> getAllUsers() {
        throw new Error("Unresolved compilation problem: \n\tType mismatch: cannot convert from ResponseEntity<Object> to ResponseEntity<List<AppUser>>\n");
    }

    @GetMapping(value={"/customers"})
    public ResponseEntity<List<Customer>> getAllCustomers() {
        throw new Error("Unresolved compilation problem: \n\tType mismatch: cannot convert from ResponseEntity<Object> to ResponseEntity<List<Customer>>\n");
    }

    @GetMapping(value={"/accounts"})
    public ResponseEntity<List<Account>> getAllAccounts() {
        throw new Error("Unresolved compilation problem: \n\tType mismatch: cannot convert from ResponseEntity<Object> to ResponseEntity<List<Account>>\n");
    }

    @GetMapping(value={"/transactions"})
    public ResponseEntity<List<Map<String, Object>>> getAllTransactions() {
        throw new Error("Unresolved compilation problem: \n");
    }

    @GetMapping(value={"/cards"})
    public ResponseEntity<List<Map<String, Object>>> getAllCards() {
        throw new Error("Unresolved compilation problems: \n\tType mismatch: cannot convert from Object to List\n\tThe method getAccount() is undefined for the type Object\n\tThe method getId() is undefined for the type Object\n\tThe method getCardNumber() is undefined for the type Object\n\tThe method getType() is undefined for the type Object\n\tThe method getType() is undefined for the type Object\n\tThe method getExpiryDate() is undefined for the type Object\n");
    }

    @GetMapping(value={"/pending-transfers"})
    public ResponseEntity<List<Map<String, Object>>> getAllPendingTransfers() {
        throw new Error("Unresolved compilation problems: \n\tType mismatch: cannot convert from Object to List\n\tThe method getId() is undefined for the type Object\n\tThe method getFromAccount() is undefined for the type Object\n\tThe method getToAccount() is undefined for the type Object\n\tThe method getAmount() is undefined for the type Object\n\tThe method getAmount() is undefined for the type Object\n\tThe method getStatus() is undefined for the type Object\n\tThe method getStatus() is undefined for the type Object\n\tThe method getCreatedAt() is undefined for the type Object\n");
    }

    @GetMapping(value={"/dashboard-stats"})
    public ResponseEntity<Map<String, Object>> getDashboardStats() {
        throw new Error("Unresolved compilation problems: \n\tType mismatch: cannot convert from Map<String,Number & Comparable<?> & Constable & ConstantDesc> to Map<String,Double>\n\tType mismatch: cannot convert from ResponseEntity<Map<String,Double>> to ResponseEntity<Map<String,Object>>\n");
    }

    @GetMapping(value={"/users-with-details"})
    public ResponseEntity<List<Map<String, Object>>> getUsersWithDetails() {
        throw new Error("Unresolved compilation problems: \n\tType mismatch: cannot convert from Object to List\n\tThe method getRole() is undefined for the type Object\n\tThe method getId() is undefined for the type Object\n\tThe method getUsername() is undefined for the type Object\n");
    }
}
