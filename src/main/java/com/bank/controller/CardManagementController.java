package com.bank.controller;

import com.bank.dto.*;
import com.bank.service.CardManagementService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cards")
@CrossOrigin(origins = "*")
public class CardManagementController {

    @Autowired
    private CardManagementService cardManagementService;

    /**
     * Issue a new card
     */
    @PostMapping("/issue")
    public ResponseEntity<?> issueCard(
            @Valid @RequestBody CardRequestDto request,
            Authentication authentication) {
        try {
            String username = authentication.getName();
            CardDetailsResponse response = cardManagementService.issueCard(request, username);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Activate a card
     */
    @PostMapping("/activate")
    public ResponseEntity<?> activateCard(
            @Valid @RequestBody CardActivationRequest request,
            Authentication authentication) {
        try {
            String username = authentication.getName();
            CardDetailsResponse response = cardManagementService.activateCard(request, username);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Block a card
     */
    @PostMapping("/block")
    public ResponseEntity<?> blockCard(
            @Valid @RequestBody CardBlockRequest request,
            Authentication authentication) {
        try {
            String username = authentication.getName();
            CardDetailsResponse response = cardManagementService.blockCard(request, username);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Unblock a card
     */
    @PostMapping("/{cardId}/unblock")
    public ResponseEntity<?> unblockCard(
            @PathVariable Long cardId,
            Authentication authentication) {
        try {
            String username = authentication.getName();
            CardDetailsResponse response = cardManagementService.unblockCard(cardId, username);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Update card limits
     */
    @PutMapping("/limits")
    public ResponseEntity<?> updateCardLimits(
            @Valid @RequestBody CardLimitsUpdateRequest request,
            Authentication authentication) {
        try {
            String username = authentication.getName();
            CardDetailsResponse response = cardManagementService.updateCardLimits(request, username);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Toggle contactless payments
     */
    @PutMapping("/{cardId}/contactless")
    public ResponseEntity<?> toggleContactless(
            @PathVariable Long cardId,
            @RequestBody Map<String, Boolean> request,
            Authentication authentication) {
        try {
            String username = authentication.getName();
            Boolean enabled = request.get("enabled");
            CardDetailsResponse response = cardManagementService.toggleContactless(
                cardId, enabled, username);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Toggle international transactions
     */
    @PutMapping("/{cardId}/international")
    public ResponseEntity<?> toggleInternational(
            @PathVariable Long cardId,
            @RequestBody Map<String, Boolean> request,
            Authentication authentication) {
        try {
            String username = authentication.getName();
            Boolean enabled = request.get("enabled");
            CardDetailsResponse response = cardManagementService.toggleInternational(
                cardId, enabled, username);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Get card details
     */
    @GetMapping("/{cardId}")
    public ResponseEntity<?> getCardDetails(
            @PathVariable Long cardId,
            Authentication authentication) {
        try {
            String username = authentication.getName();
            CardDetailsResponse response = cardManagementService.getCardDetails(cardId, username);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Get all cards for account
     */
    @GetMapping("/account/{accountId}")
    public ResponseEntity<?> getCardsForAccount(
            @PathVariable Long accountId,
            Authentication authentication) {
        try {
            String username = authentication.getName();
            List<CardDetailsResponse> cards = cardManagementService.getCardsForAccount(
                accountId, username);
            return ResponseEntity.ok(cards);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Get all cards for user
     */
    @GetMapping
    public ResponseEntity<?> getUserCards(Authentication authentication) {
        try {
            String username = authentication.getName();
            List<CardDetailsResponse> cards = cardManagementService.getUserCards(username);
            return ResponseEntity.ok(cards);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
