package com.bank.service;

import com.bank.dto.CardActivationRequest;
import com.bank.dto.CardBlockRequest;
import com.bank.dto.CardDetailsResponse;
import com.bank.dto.CardLimitsUpdateRequest;
import com.bank.dto.CardRequestDto;
import com.bank.exception.ResourceNotFoundException;
import com.bank.model.*;
import com.bank.repository.AccountRepository;
import com.bank.repository.AppUserRepository;
import com.bank.repository.CardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CardManagementService {

    @Autowired
    private CardRepository cardRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private AppUserRepository userRepository;

    @Autowired
    private AuditService auditService;

    private static final SecureRandom random = new SecureRandom();

    /**
     * Issue a new card
     */
    @Transactional
    public CardDetailsResponse issueCard(CardRequestDto request, String username) {
        // Verify account ownership
        Account account = accountRepository.findById(request.getAccountId())
            .orElseThrow(() -> new ResourceNotFoundException("Account not found"));
        
        verifyAccountOwnership(account, username);

        // Create card
        Card card = new Card();
        card.setCardNumber(generateCardNumber(request.getCardProvider()));
        card.setCvv(generateCVV());
        card.setExpiryDate(LocalDate.now().plusYears(3));
        card.setIssuedDate(LocalDate.now());
        card.setType(request.getCardType());
        card.setProvider(request.getCardProvider());
        card.setStatus(CardStatus.PENDING);
        card.setAccount(account);
        
        // Set limits
        card.setCardLimit(request.getCardLimit() != null ? request.getCardLimit() : 100000.0);
        card.setDailyLimit(request.getDailyLimit() != null ? request.getDailyLimit() : 50000.0);
        card.setMonthlyLimit(request.getMonthlyLimit() != null ? request.getMonthlyLimit() : 200000.0);
        
        // Set holder name
        card.setHolderName(request.getHolderName() != null ? 
            request.getHolderName() : account.getCustomer().getName());
        
        // Set features
        card.setContactlessEnabled(request.getContactlessEnabled());
        card.setInternationalEnabled(request.getInternationalEnabled());

        card = cardRepository.save(card);

        // Audit log
        auditService.logAction(username, "CARD_ISSUED", 
            "Issued " + request.getCardType() + " card ending in " + card.getCardNumber().substring(12));

        return mapToDetailsResponse(card);
    }

    /**
     * Activate a card
     */
    @Transactional
    public CardDetailsResponse activateCard(CardActivationRequest request, String username) {
        Card card = cardRepository.findById(request.getCardId())
            .orElseThrow(() -> new ResourceNotFoundException("Card not found"));

        verifyCardOwnership(card, username);

        // Verify CVV and last 4 digits
        if (!card.getCvv().equals(request.getCvv())) {
            throw new IllegalArgumentException("Invalid CVV");
        }
        if (!card.getCardNumber().endsWith(request.getLast4Digits())) {
            throw new IllegalArgumentException("Invalid card number");
        }

        if (card.getStatus() != CardStatus.PENDING) {
            throw new IllegalStateException("Card is not in pending state");
        }

        card.setStatus(CardStatus.ACTIVE);
        cardRepository.save(card);

        // Audit log
        auditService.logAction(username, "CARD_ACTIVATED", 
            "Activated card ending in " + card.getCardNumber().substring(12));

        return mapToDetailsResponse(card);
    }

    /**
     * Block a card
     */
    @Transactional
    public CardDetailsResponse blockCard(CardBlockRequest request, String username) {
        Card card = cardRepository.findById(request.getCardId())
            .orElseThrow(() -> new ResourceNotFoundException("Card not found"));

        verifyCardOwnership(card, username);

        if (request.getReportLost()) {
            card.setStatus(CardStatus.LOST);
        } else if (request.getReportStolen()) {
            card.setStatus(CardStatus.STOLEN);
        } else {
            card.setStatus(CardStatus.BLOCKED);
        }

        cardRepository.save(card);

        // Audit log
        auditService.logAction(username, "CARD_BLOCKED", 
            "Blocked card ending in " + card.getCardNumber().substring(12) + 
            ". Reason: " + request.getReason());

        return mapToDetailsResponse(card);
    }

    /**
     * Unblock a card
     */
    @Transactional
    public CardDetailsResponse unblockCard(Long cardId, String username) {
        Card card = cardRepository.findById(cardId)
            .orElseThrow(() -> new ResourceNotFoundException("Card not found"));

        verifyCardOwnership(card, username);

        if (card.getStatus() != CardStatus.BLOCKED) {
            throw new IllegalStateException("Card is not blocked");
        }

        // Check if card is not expired
        if (LocalDate.now().isAfter(card.getExpiryDate())) {
            card.setStatus(CardStatus.EXPIRED);
            throw new IllegalStateException("Card has expired and cannot be unblocked");
        }

        card.setStatus(CardStatus.ACTIVE);
        cardRepository.save(card);

        // Audit log
        auditService.logAction(username, "CARD_UNBLOCKED", 
            "Unblocked card ending in " + card.getCardNumber().substring(12));

        return mapToDetailsResponse(card);
    }

    /**
     * Update card limits
     */
    @Transactional
    public CardDetailsResponse updateCardLimits(CardLimitsUpdateRequest request, String username) {
        Card card = cardRepository.findById(request.getCardId())
            .orElseThrow(() -> new ResourceNotFoundException("Card not found"));

        verifyCardOwnership(card, username);

        if (request.getCardLimit() != null) {
            card.setCardLimit(request.getCardLimit());
        }
        if (request.getDailyLimit() != null) {
            card.setDailyLimit(request.getDailyLimit());
        }
        if (request.getMonthlyLimit() != null) {
            card.setMonthlyLimit(request.getMonthlyLimit());
        }

        cardRepository.save(card);

        // Audit log
        auditService.logAction(username, "CARD_LIMITS_UPDATED", 
            "Updated limits for card ending in " + card.getCardNumber().substring(12));

        return mapToDetailsResponse(card);
    }

    /**
     * Toggle contactless payments
     */
    @Transactional
    public CardDetailsResponse toggleContactless(Long cardId, Boolean enabled, String username) {
        Card card = cardRepository.findById(cardId)
            .orElseThrow(() -> new ResourceNotFoundException("Card not found"));

        verifyCardOwnership(card, username);

        card.setContactlessEnabled(enabled);
        cardRepository.save(card);

        // Audit log
        auditService.logAction(username, "CARD_CONTACTLESS_TOGGLE", 
            (enabled ? "Enabled" : "Disabled") + " contactless for card ending in " + 
            card.getCardNumber().substring(12));

        return mapToDetailsResponse(card);
    }

    /**
     * Toggle international transactions
     */
    @Transactional
    public CardDetailsResponse toggleInternational(Long cardId, Boolean enabled, String username) {
        Card card = cardRepository.findById(cardId)
            .orElseThrow(() -> new ResourceNotFoundException("Card not found"));

        verifyCardOwnership(card, username);

        card.setInternationalEnabled(enabled);
        cardRepository.save(card);

        // Audit log
        auditService.logAction(username, "CARD_INTERNATIONAL_TOGGLE", 
            (enabled ? "Enabled" : "Disabled") + " international transactions for card ending in " + 
            card.getCardNumber().substring(12));

        return mapToDetailsResponse(card);
    }

    /**
     * Get card details
     */
    public CardDetailsResponse getCardDetails(Long cardId, String username) {
        Card card = cardRepository.findById(cardId)
            .orElseThrow(() -> new ResourceNotFoundException("Card not found"));

        verifyCardOwnership(card, username);

        return mapToDetailsResponse(card);
    }

    /**
     * Get all cards for account
     */
    public List<CardDetailsResponse> getCardsForAccount(Long accountId, String username) {
        Account account = accountRepository.findById(accountId)
            .orElseThrow(() -> new ResourceNotFoundException("Account not found"));

        verifyAccountOwnership(account, username);

        List<Card> cards = cardRepository.findByAccount(account);
        return cards.stream()
            .map(this::mapToDetailsResponse)
            .collect(Collectors.toList());
    }

    /**
     * Get all cards for user
     */
    public List<CardDetailsResponse> getUserCards(String username) {
        AppUser user = userRepository.findByUsername(username)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Customer customer = user.getCustomer();
        if (customer == null) {
            return List.of();
        }

        List<Account> accounts = accountRepository.findByCustomer(customer);
        return accounts.stream()
            .flatMap(account -> cardRepository.findByAccount(account).stream())
            .map(this::mapToDetailsResponse)
            .collect(Collectors.toList());
    }

    /**
     * Generate card number based on provider
     */
    private String generateCardNumber(CardProvider provider) {
        StringBuilder cardNumber = new StringBuilder();
        
        // First digit(s) based on provider
        switch (provider) {
            case VISA:
                cardNumber.append("4");
                break;
            case MASTERCARD:
                cardNumber.append("5");
                break;
            case RUPAY:
                cardNumber.append("6");
                break;
            case AMERICAN_EXPRESS:
                cardNumber.append("37");
                break;
        }
        
        // Generate remaining digits
        int remainingDigits = 16 - cardNumber.length();
        for (int i = 0; i < remainingDigits; i++) {
            cardNumber.append(random.nextInt(10));
        }
        
        return cardNumber.toString();
    }

    /**
     * Generate CVV
     */
    private String generateCVV() {
        int cvv = 100 + random.nextInt(900);
        return String.valueOf(cvv);
    }

    /**
     * Verify card ownership
     */
    private void verifyCardOwnership(Card card, String username) {
        verifyAccountOwnership(card.getAccount(), username);
    }

    /**
     * Verify account ownership
     */
    private void verifyAccountOwnership(Account account, String username) {
        AppUser user = userRepository.findByUsername(username)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (account.getCustomer() == null || 
            !account.getCustomer().getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized access to account");
        }
    }

    /**
     * Map Card to CardDetailsResponse
     */
    private CardDetailsResponse mapToDetailsResponse(Card card) {
        CardDetailsResponse response = new CardDetailsResponse();
        response.setId(card.getId());
        
        // Mask card number for security
        String cardNum = card.getCardNumber();
        response.setCardNumber(maskCardNumber(cardNum));
        response.setLast4Digits(cardNum.substring(12));
        
        response.setExpiryDate(card.getExpiryDate());
        response.setIssuedDate(card.getIssuedDate());
        response.setCardType(card.getType());
        response.setCardStatus(card.getStatus());
        response.setCardProvider(card.getProvider());
        response.setCardLimit(card.getCardLimit());
        response.setDailyLimit(card.getDailyLimit());
        response.setMonthlyLimit(card.getMonthlyLimit());
        response.setAvailableLimit(card.getCardLimit()); // Can be calculated based on usage
        response.setHolderName(card.getHolderName());
        response.setAccountId(card.getAccount().getId());
        response.setAccountNumber(card.getAccount().getAccountNumber());
        response.setContactlessEnabled(card.getContactlessEnabled());
        response.setInternationalEnabled(card.getInternationalEnabled());

        return response;
    }

    /**
     * Mask card number for security
     */
    private String maskCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.length() < 16) {
            return cardNumber;
        }
        return cardNumber.substring(0, 4) + " **** **** " + cardNumber.substring(12);
    }
}
