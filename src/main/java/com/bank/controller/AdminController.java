package com.bank.controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bank.model.Account;
import com.bank.model.AppUser;
import com.bank.model.Card;
import com.bank.model.Customer;
import com.bank.model.PendingTransfer;
import com.bank.model.Transaction;
import com.bank.repository.AccountRepository;
import com.bank.repository.AppUserRepository;
import com.bank.repository.CardRepository;
import com.bank.repository.CustomerRepository;
import com.bank.repository.PendingTransferRepository;
import com.bank.repository.TransactionRepository;

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

    @GetMapping(value={"/users"})
    public ResponseEntity<List<AppUser>> getAllUsers() {
        List<AppUser> users = this.userRepository.findAll();
        return ResponseEntity.ok(users);
    }

    @GetMapping(value={"/customers"})
    public ResponseEntity<List<Customer>> getAllCustomers() {
        List<Customer> customers = this.customerRepository.findAll();
        return ResponseEntity.ok(customers);
    }

    // Note: /accounts endpoint moved to AdminAccountController
    // This endpoint is deprecated - use GET /api/admin/accounts from AdminAccountController instead
    @GetMapping(value={"/accounts-legacy"})
    public ResponseEntity<List<Account>> getAllAccountsLegacy() {
        List<Account> accounts = this.accountRepository.findAll();
        return ResponseEntity.ok(accounts);
    }

    @GetMapping(value={"/transactions"})
    public ResponseEntity<List<Map<String, Object>>> getAllTransactions() {
        List<Transaction> transactions = this.transactionRepository.findAllByOrderByTransactionDateDesc();
        List<Map<String, Object>> result = transactions.stream().map(t -> {
            String uiType;
            Account account = t.getAccount();
            Customer customer = account != null ? account.getCustomer() : null;
            if (t.getType() == null) {
                uiType = "UNKNOWN";
            } else {
                switch (t.getType()) {
                    case DEPOSIT:
                        uiType = "DEPOSIT";
                        break;
                    case WITHDRAW:
                        uiType = "WITHDRAWAL";
                        break;
                    case TRANSFER_IN:
                    case TRANSFER_OUT:
                        uiType = "TRANSFER";
                        break;
                    default:
                        uiType = t.getType().name();
                }
            }
            return Map.of(
                "id", t.getId(),
                "amount", t.getAmount(),
                "type", uiType,
                "date", t.getDate(),
                "account", account != null
                    ? Map.of(
                        "accountNumber", account.getAccountNumber(),
                        "customer", customer != null
                            ? Map.of("name", customer.getName())
                            : Map.of()
                    )
                    : Map.of("accountNumber", "", "customer", Map.of())
            );
        }).collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    @GetMapping(value={"/cards"})
    public ResponseEntity<List<Map<String, Object>>> getAllCards() {
        List<Card> cards = this.cardRepository.findAll();
        List<Map<String, Object>> result = cards.stream().map(card -> {
            Account account = card.getAccount();
            Customer customer = account != null ? account.getCustomer() : null;
            return Map.of(
                "id", card.getId(),
                "cardNumber", card.getCardNumber(),
                "cardType", card.getType() != null ? card.getType().name() : "",
                "expiryDate", card.getExpiryDate(),
                "customer", Map.of(
                    "name", customer != null ? customer.getName() : "",
                    "email", customer != null ? customer.getEmail() : ""
                ),
                "account", account != null
                    ? Map.of(
                        "accountNumber", account.getAccountNumber(),
                        "balance", account.getBalance()
                    )
                    : Map.of()
            );
        }).collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    @GetMapping(value={"/pending-transfers"})
    public ResponseEntity<List<Map<String, Object>>> getAllPendingTransfers() {
        List<PendingTransfer> pendingTransfers = this.pendingTransferRepository.findAll();
        List<Map<String, Object>> result = pendingTransfers.stream().map(pt -> {
            Map<String, Object> transfer = new java.util.HashMap<>();
            transfer.put("id", pt.getId());
            transfer.put("fromAccountNumber", pt.getFromAccount());
            transfer.put("toAccountNumber", pt.getToAccount());
            transfer.put("amount", pt.getAmount() != null ? pt.getAmount().doubleValue() : 0.0);
            transfer.put("status", pt.getStatus() != null ? pt.getStatus().name() : "");
            transfer.put("createdAt", pt.getCreatedAt());
            return transfer;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    @GetMapping(value={"/dashboard-stats"})
    public ResponseEntity<Map<String, Object>> getDashboardStats() {
        long totalUsers = this.userRepository.count();
        long totalCustomers = this.customerRepository.count();
        long totalAccounts = this.accountRepository.count();
        long totalTransactions = this.transactionRepository.count();
        long totalCards = this.cardRepository.count();
        long pendingTransfers = this.pendingTransferRepository.count();
        Double totalSystemBalance = this.accountRepository.findAll().stream()
            .mapToDouble(Account::getBalance)
            .sum();
        Map<String, Object> stats = Map.of(
            "totalUsers", totalUsers,
            "totalCustomers", totalCustomers,
            "totalAccounts", totalAccounts,
            "totalTransactions", totalTransactions,
            "totalCards", totalCards,
            "pendingTransfers", pendingTransfers,
            "totalSystemBalance", totalSystemBalance
        );
        return ResponseEntity.ok(stats);
    }

    @GetMapping(value={"/users-with-details"})
    public ResponseEntity<List<Map<String, Object>>> getUsersWithDetails() {
        List<AppUser> users = this.userRepository.findAll();
        List<Map<String, Object>> userDetails = users.stream().map(user -> {
            String role = user.getRole();
            boolean isAdmin = "ROLE_ADMIN".equals(role) || "ADMIN".equals(role);
            Map<String, Object> userDto = Map.of(
                "id", user.getId(),
                "username", user.getUsername(),
                "roles", new String[]{role != null ? role : "ROLE_CUSTOMER"}
            );
            if (isAdmin) {
                return Map.of(
                    "user", userDto,
                    "customer", Map.of(),
                    "accounts", List.of(),
                    "cards", List.of(),
                    "accountCount", 0,
                    "cardCount", 0,
                    "totalBalance", 0.0,
                    "isAdmin", true
                );
            }
            return Map.of(
                "user", userDto,
                "customer", Map.of("name", "Customer Profile", "email", "N/A"),
                "accounts", List.of(),
                "cards", List.of(),
                "accountCount", 0,
                "cardCount", 0,
                "totalBalance", 0.0,
                "isAdmin", false
            );
        }).collect(Collectors.toList());
        return ResponseEntity.ok(userDetails);
    }
}
