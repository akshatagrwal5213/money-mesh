package com.bank.controller;

import com.bank.dto.*;
import com.bank.model.CustomerTierInfo;
import com.bank.model.LoyaltyOffer;
import com.bank.service.RewardsService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/rewards")
@CrossOrigin(origins = "*")
public class RewardsController {
    
    @Autowired
    private RewardsService rewardsService;
    
    // Get total points for customer
    @GetMapping("/points/{customerId}")
    public ResponseEntity<Map<String, Object>> getPoints(@PathVariable Long customerId) {
        Integer totalPoints = rewardsService.getTotalPoints(customerId);
        Integer activePoints = rewardsService.getActivePoints(customerId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("customerId", customerId);
        response.put("totalPoints", totalPoints);
        response.put("activePoints", activePoints);
        response.put("pointValue", activePoints * 0.25); // 1 point = ₹0.25
        
        return ResponseEntity.ok(response);
    }
    
    // Get tier information
    @GetMapping("/tier/{customerId}")
    public ResponseEntity<Map<String, Object>> getTierInfo(@PathVariable Long customerId) {
        CustomerTierInfo tierInfo = rewardsService.getTierInfo(customerId);
        
        if (tierInfo == null) {
            tierInfo = rewardsService.initializeTierInfo(customerId);
        }
        
        TierInfoDto dto = rewardsService.convertToTierDto(tierInfo);
        
        Map<String, Object> response = new HashMap<>();
        response.put("tierInfo", dto);
        response.put("benefits", parseBenefits(dto.getBenefits()));
        
        return ResponseEntity.ok(response);
    }
    
    // Initialize tier for new customer
    @PostMapping("/tier/{customerId}/initialize")
    public ResponseEntity<Map<String, Object>> initializeTier(@PathVariable Long customerId) {
        CustomerTierInfo tierInfo = rewardsService.initializeTierInfo(customerId);
        TierInfoDto dto = rewardsService.convertToTierDto(tierInfo);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Tier initialized successfully");
        response.put("tierInfo", dto);
        
        return ResponseEntity.ok(response);
    }
    
    // Redeem points
    @PostMapping("/redeem")
    public ResponseEntity<Map<String, Object>> redeemPoints(
            @RequestParam Long customerId,
            @Valid @RequestBody RedemptionRequest request) {
        
        RedemptionResponse response = rewardsService.redeemPoints(customerId, request);
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "Points redeemed successfully");
        result.put("redemption", response);
        result.put("pointsRedeemed", response.getPointsRedeemed());
        result.put("cashValue", response.getCashValue());
        
        return ResponseEntity.ok(result);
    }
    
    // Get redemption history
    @GetMapping("/redemption-history/{customerId}")
    public ResponseEntity<Map<String, Object>> getRedemptionHistory(@PathVariable Long customerId) {
        // This would require a method in RewardsService to fetch history
        Map<String, Object> response = new HashMap<>();
        response.put("customerId", customerId);
        response.put("message", "Redemption history endpoint");
        
        return ResponseEntity.ok(response);
    }
    
    // Generate referral code
    @PostMapping("/referral/generate")
    public ResponseEntity<Map<String, Object>> generateReferralCode(
            @RequestParam Long customerId,
            @Valid @RequestBody ReferralRequest request) {
        
        ReferralDto referral = rewardsService.generateReferralCode(customerId, request);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Referral code generated");
        response.put("referral", referral);
        response.put("referralCode", referral.getReferralCode());
        response.put("bonusPoints", referral.getBonusPoints());
        
        return ResponseEntity.ok(response);
    }
    
    // Get referral history
    @GetMapping("/referrals/{customerId}")
    public ResponseEntity<Map<String, Object>> getReferralHistory(@PathVariable Long customerId) {
        List<ReferralDto> referrals = rewardsService.getReferralHistory(customerId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("customerId", customerId);
        response.put("referrals", referrals);
        response.put("totalReferrals", referrals.size());
        response.put("successfulReferrals", referrals.stream().filter(r -> r.getBonusCredited()).count());
        
        return ResponseEntity.ok(response);
    }
    
    // Process referral registration (called when new customer signs up with referral code)
    @PostMapping("/referral/register")
    public ResponseEntity<Map<String, String>> processReferralRegistration(
            @RequestParam String referralCode,
            @RequestParam Long newCustomerId) {
        
        rewardsService.processReferralRegistration(referralCode, newCustomerId);
        
        Map<String, String> response = new HashMap<>();
        response.put("success", "true");
        response.put("message", "Referral registration processed");
        
        return ResponseEntity.ok(response);
    }
    
    // Qualify referral (called after first transaction)
    @PostMapping("/referral/qualify/{customerId}")
    public ResponseEntity<Map<String, String>> qualifyReferral(@PathVariable Long customerId) {
        rewardsService.qualifyReferral(customerId);
        
        Map<String, String> response = new HashMap<>();
        response.put("success", "true");
        response.put("message", "Referral qualified and bonus awarded");
        
        return ResponseEntity.ok(response);
    }
    
    // Get milestone progress
    @GetMapping("/milestones/{customerId}")
    public ResponseEntity<Map<String, Object>> getMilestoneProgress(@PathVariable Long customerId) {
        List<MilestoneDto> milestones = rewardsService.getMilestoneProgress(customerId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("customerId", customerId);
        response.put("milestones", milestones);
        response.put("totalAchieved", milestones.size());
        response.put("totalBonus", milestones.stream()
                .filter(MilestoneDto::getIsCredited)
                .mapToInt(MilestoneDto::getBonusPoints)
                .sum());
        
        return ResponseEntity.ok(response);
    }
    
    // Get active offers
    @GetMapping("/offers/{customerId}")
    public ResponseEntity<Map<String, Object>> getActiveOffers(@PathVariable Long customerId) {
        List<LoyaltyOffer> offers = rewardsService.getActiveOffers(customerId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("customerId", customerId);
        response.put("offers", offers);
        response.put("totalOffers", offers.size());
        
        return ResponseEntity.ok(response);
    }
    
    // Get cashback history
    @GetMapping("/cashback-history/{customerId}")
    public ResponseEntity<Map<String, Object>> getCashbackHistory(@PathVariable Long customerId) {
        // This would require a method in RewardsService
        Map<String, Object> response = new HashMap<>();
        response.put("customerId", customerId);
        response.put("message", "Cashback history endpoint");
        
        return ResponseEntity.ok(response);
    }
    
    // Award points (Admin/System endpoint)
    @PostMapping("/admin/award-points")
    public ResponseEntity<Map<String, String>> awardPoints(
            @RequestParam Long customerId,
            @RequestParam Integer points,
            @RequestParam String category,
            @RequestParam String description) {
        
        rewardsService.awardPoints(customerId, points, 
                com.bank.model.RewardCategory.valueOf(category), description);
        
        Map<String, String> response = new HashMap<>();
        response.put("success", "true");
        response.put("message", points + " points awarded successfully");
        
        return ResponseEntity.ok(response);
    }
    
    // Expire old points (Scheduled job endpoint)
    @PostMapping("/admin/expire-points")
    public ResponseEntity<Map<String, String>> expireOldPoints() {
        rewardsService.expireOldPoints();
        
        Map<String, String> response = new HashMap<>();
        response.put("success", "true");
        response.put("message", "Old points expired successfully");
        
        return ResponseEntity.ok(response);
    }
    
    // Helper method to parse benefits string
    private List<String> parseBenefits(String benefits) {
        if (benefits == null || benefits.isEmpty()) {
            return List.of();
        }
        return List.of(benefits.split(","));
    }
}
