package com.bank.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bank.dto.AnalyticsRequest;
import com.bank.dto.AnalyticsResponse;
import com.bank.exception.ResourceNotFoundException;
import com.bank.model.Account;
import com.bank.model.Customer;
import com.bank.model.Transaction;
import com.bank.model.TransactionAnalytics;
import com.bank.model.TransactionType;
import com.bank.repository.AccountRepository;
import com.bank.repository.CustomerRepository;
import com.bank.repository.TransactionAnalyticsRepository;
import com.bank.repository.TransactionRepository;

@Service
@Transactional
public class AnalyticsService {
    
    @Autowired
    private TransactionAnalyticsRepository analyticsRepository;
    
    @Autowired
    private TransactionRepository transactionRepository;
    
    @Autowired
    private CustomerRepository customerRepository;
    
    @Autowired
    private AccountRepository accountRepository;
    
    public AnalyticsResponse getTransactionAnalytics(String username, AnalyticsRequest request) {
        Customer customer = customerRepository.findByUser_Username(username)
            .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
        
        LocalDate startDate = request.getStartDate() != null ? request.getStartDate() : LocalDate.now().minusMonths(1);
        LocalDate endDate = request.getEndDate() != null ? request.getEndDate() : LocalDate.now();
        
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);
        
        // Get all transactions for the customer's accounts
        List<Account> accounts = accountRepository.findByCustomer(customer);
        List<Transaction> allTransactions = new ArrayList<>();
        
        for (Account account : accounts) {
            List<Transaction> transactions = transactionRepository
                .findByAccountAndDateBetween(account, startDateTime, endDateTime);
            allTransactions.addAll(transactions);
        }
        
        return calculateAnalytics(allTransactions);
    }
    
    private AnalyticsResponse calculateAnalytics(List<Transaction> transactions) {
        AnalyticsResponse response = new AnalyticsResponse();
        
        BigDecimal totalIncome = BigDecimal.ZERO;
        BigDecimal totalExpenses = BigDecimal.ZERO;
        BigDecimal totalTransfers = BigDecimal.ZERO;
        Map<String, BigDecimal> categoryBreakdown = new HashMap<>();
        
        for (Transaction txn : transactions) {
            BigDecimal amount = BigDecimal.valueOf(txn.getAmount());

            // Categorize based on transaction type
            TransactionType type = txn.getType();
            if (type != null) {
                switch (type) {
                    case DEPOSIT -> totalIncome = totalIncome.add(amount);
                    case WITHDRAWAL, WITHDRAW -> totalExpenses = totalExpenses.add(amount);
                    case TRANSFER_IN, TRANSFER_OUT -> totalTransfers = totalTransfers.add(amount);
                    default -> {
                        // leave as-is for unknown types
                    }
                }
            }
            
            // Category breakdown (using description as category proxy)
            String category = txn.getDescription() != null ? txn.getDescription() : "UNCATEGORIZED";
            categoryBreakdown.put(category, categoryBreakdown.getOrDefault(category, BigDecimal.ZERO).add(amount));
        }
        
        response.setTotalIncome(totalIncome);
        response.setTotalExpenses(totalExpenses);
        response.setTotalTransfers(totalTransfers);
        response.setNetSavings(totalIncome.subtract(totalExpenses));
        response.setTransactionCount(transactions.size());
        
        if (!transactions.isEmpty()) {
            double totalAmount = transactions.stream()
                .mapToDouble(Transaction::getAmount)
                .sum();
            response.setAverageTransactionAmount(
                BigDecimal.valueOf(totalAmount / transactions.size())
            );
        } else {
            response.setAverageTransactionAmount(BigDecimal.ZERO);
        }
        
        // Top category
        if (!categoryBreakdown.isEmpty()) {
            Map.Entry<String, BigDecimal> topEntry = categoryBreakdown.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .orElse(null);
            if (topEntry != null) {
                response.setTopCategory(topEntry.getKey());
                response.setTopCategoryAmount(topEntry.getValue());
            }
        }
        
        response.setCategoryBreakdown(categoryBreakdown);
        response.setDailyTransactions(calculateDailyTransactions(transactions));
        
        return response;
    }
    
    private List<AnalyticsResponse.DailyTransaction> calculateDailyTransactions(List<Transaction> transactions) {
        Map<String, List<Transaction>> transactionsByDate = transactions.stream()
            .collect(Collectors.groupingBy(txn -> 
                txn.getDate().toLocalDate().format(DateTimeFormatter.ISO_LOCAL_DATE)));
        
        return transactionsByDate.entrySet().stream()
            .map(entry -> {
                double dayTotal = entry.getValue().stream()
                    .mapToDouble(Transaction::getAmount)
                    .sum();
                return new AnalyticsResponse.DailyTransaction(
                    entry.getKey(),
                    BigDecimal.valueOf(dayTotal),
                    entry.getValue().size()
                );
            })
            .sorted(Comparator.comparing(AnalyticsResponse.DailyTransaction::getDate))
            .collect(Collectors.toList());
    }
    
    public TransactionAnalytics saveAnalytics(String username, LocalDate periodStart, LocalDate periodEnd) {
        Customer customer = customerRepository.findByUser_Username(username)
            .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
        
        // Check if analytics already exists for this period
        Optional<TransactionAnalytics> existing = analyticsRepository
            .findByCustomer_User_UsernameAndPeriodStartAndPeriodEnd(username, periodStart, periodEnd);
        
        TransactionAnalytics analytics;
        if (existing.isPresent()) {
            analytics = existing.get();
        } else {
            analytics = new TransactionAnalytics(customer, periodStart, periodEnd);
        }
        
        LocalDateTime startDateTime = periodStart.atStartOfDay();
        LocalDateTime endDateTime = periodEnd.atTime(23, 59, 59);
        
        // Calculate and update analytics
        List<Account> accounts = accountRepository.findByCustomer(customer);
        List<Transaction> allTransactions = new ArrayList<>();
        
        for (Account account : accounts) {
            List<Transaction> transactions = transactionRepository
                .findByAccountAndDateBetween(account, startDateTime, endDateTime);
            allTransactions.addAll(transactions);
        }
        
        BigDecimal totalIncome = BigDecimal.ZERO;
        BigDecimal totalExpenses = BigDecimal.ZERO;
        BigDecimal totalTransfers = BigDecimal.ZERO;
        Map<String, BigDecimal> categoryTotals = new HashMap<>();
        
        for (Transaction txn : allTransactions) {
            BigDecimal amount = BigDecimal.valueOf(txn.getAmount());

            TransactionType type = txn.getType();
            if (type != null) {
                switch (type) {
                    case DEPOSIT -> totalIncome = totalIncome.add(amount);
                    case WITHDRAWAL, WITHDRAW -> totalExpenses = totalExpenses.add(amount);
                    case TRANSFER_IN, TRANSFER_OUT -> totalTransfers = totalTransfers.add(amount);
                    default -> {
                        // unknown or other types: no-op
                    }
                }
            }
            
            String category = txn.getDescription() != null ? txn.getDescription() : "UNCATEGORIZED";
            categoryTotals.put(category, categoryTotals.getOrDefault(category, BigDecimal.ZERO).add(amount));
        }
        
        analytics.setTotalIncome(totalIncome);
        analytics.setTotalExpenses(totalExpenses);
        analytics.setTotalTransfers(totalTransfers);
        analytics.setTransactionCount(allTransactions.size());
        
        if (!allTransactions.isEmpty()) {
            double totalAmount = allTransactions.stream()
                .mapToDouble(Transaction::getAmount)
                .sum();
            analytics.setAverageTransactionAmount(
                BigDecimal.valueOf(totalAmount / allTransactions.size())
            );
        }
        
        if (!categoryTotals.isEmpty()) {
            Map.Entry<String, BigDecimal> topEntry = categoryTotals.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .orElse(null);
            if (topEntry != null) {
                analytics.setTopCategory(topEntry.getKey());
                analytics.setTopCategoryAmount(topEntry.getValue());
            }
        }
        
        return analyticsRepository.save(analytics);
    }
    
    public List<TransactionAnalytics> getAllAnalytics(String username) {
        return analyticsRepository.findByCustomer_User_UsernameOrderByPeriodStartDesc(username);
    }
    
    public Map<String, Object> getAccountSummary(String username) {
        Customer customer = customerRepository.findByUser_Username(username)
            .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
        
        List<Account> accounts = accountRepository.findByCustomer(customer);
        
        Map<String, Object> summary = new HashMap<>();
        double totalBalance = accounts.stream()
            .mapToDouble(Account::getBalance)
            .sum();
        
        summary.put("totalAccounts", accounts.size());
        summary.put("totalBalance", totalBalance);
        summary.put("accounts", accounts.stream()
            .map(acc -> Map.of(
                "accountNumber", acc.getAccountNumber(),
                "balance", acc.getBalance()
            ))
            .collect(Collectors.toList()));
        
        return summary;
    }
}
