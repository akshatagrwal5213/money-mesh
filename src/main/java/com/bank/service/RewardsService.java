package com.bank.service;

import com.bank.dto.*;
import com.bank.exception.InsufficientFundsException;
import com.bank.exception.ResourceNotFoundException;
import com.bank.model.*;
import com.bank.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class RewardsService {
    
    @Autowired
    private RewardPointsRepository rewardPointsRepository;
    
    @Autowired
    private CustomerTierInfoRepository customerTierInfoRepository;
    
    @Autowired
    private CashbackRepository cashbackRepository;
    
    @Autowired
    private PointsRedemptionRepository pointsRedemptionRepository;
    
    @Autowired
    private ReferralBonusRepository referralBonusRepository;
    
    @Autowired
    private LoyaltyOfferRepository loyaltyOfferRepository;
    
    @Autowired
    private MilestoneRewardRepository milestoneRewardRepository;
    
    @Autowired
    private CustomerRepository customerRepository;
    
    // Award points to customer
    public void awardPoints(Long customerId, Integer points, RewardCategory category, String description) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
        
        RewardPoints rewardPoints = new RewardPoints();
        rewardPoints.setCustomer(customer);
        rewardPoints.setPoints(points);
        rewardPoints.setCategory(category);
        rewardPoints.setDescription(description);
        rewardPoints.setEarnedDate(LocalDate.now());
        rewardPoints.setExpiryDate(LocalDate.now().plusYears(1)); // 1 year expiry
        rewardPoints.setIsExpired(false);
        rewardPoints.setIsRedeemed(false);
        
        rewardPointsRepository.save(rewardPoints);
        
        // Update tier info
        updateTierInfo(customerId, points);
        
        // Check for milestones
        checkAndAwardMilestones(customerId);
    }
    
    // Get total points for customer
    public Integer getTotalPoints(Long customerId) {
        Integer total = rewardPointsRepository.getTotalPoints(customerId);
        return total != null ? total : 0;
    }
    
    // Get active (non-expired, non-redeemed) points
    public Integer getActivePoints(Long customerId) {
        Integer active = rewardPointsRepository.getActivePoints(customerId);
        return active != null ? active : 0;
    }
    
    // Get tier info for customer
    public CustomerTierInfo getTierInfo(Long customerId) {
        return customerTierInfoRepository.findByCustomerId(customerId)
                .orElse(null);
    }
    
    // Initialize tier info for new customer
    public CustomerTierInfo initializeTierInfo(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
        
        Optional<CustomerTierInfo> existing = customerTierInfoRepository.findByCustomerId(customerId);
        if (existing.isPresent()) {
            return existing.get();
        }
        
        CustomerTierInfo tierInfo = new CustomerTierInfo();
        tierInfo.setCustomer(customer);
        tierInfo.setTierLevel(CustomerTierLevel.SILVER);
        tierInfo.setTotalPoints(0);
        tierInfo.setActivePoints(0);
        tierInfo.setTierStartDate(LocalDate.now());
        tierInfo.setNextTierThreshold(10000); // Gold tier threshold
        tierInfo.setBenefits("1x cashback, Standard processing");
        tierInfo.setInterestRateDiscount(0.0);
        tierInfo.setFreeRestructuring(false);
        tierInfo.setPrepaymentFeeWaiver(false);
        tierInfo.setCashbackMultiplier(1.0);
        
        return customerTierInfoRepository.save(tierInfo);
    }
    
    // Update tier info when points are awarded
    private void updateTierInfo(Long customerId, Integer pointsAdded) {
        CustomerTierInfo tierInfo = customerTierInfoRepository.findByCustomerId(customerId)
                .orElseGet(() -> initializeTierInfo(customerId));
        
        Integer totalPoints = tierInfo.getTotalPoints() + pointsAdded;
        Integer activePoints = getActivePoints(customerId);
        
        tierInfo.setTotalPoints(totalPoints);
        tierInfo.setActivePoints(activePoints);
        
        // Check for tier upgrade
        CustomerTierLevel newTier = determineTierLevel(totalPoints);
        if (newTier != tierInfo.getTierLevel()) {
            upgradeTier(tierInfo, newTier);
        }
        
        customerTierInfoRepository.save(tierInfo);
    }
    
    // Determine tier level based on total points
    private CustomerTierLevel determineTierLevel(Integer points) {
        if (points >= 200000) return CustomerTierLevel.DIAMOND;
        if (points >= 50000) return CustomerTierLevel.PLATINUM;
        if (points >= 10000) return CustomerTierLevel.GOLD;
        return CustomerTierLevel.SILVER;
    }
    
    // Upgrade tier and award bonus
    private void upgradeTier(CustomerTierInfo tierInfo, CustomerTierLevel newTier) {
        tierInfo.setTierLevel(newTier);
        tierInfo.setTierUpgradeDate(LocalDate.now());
        
        // Set tier benefits
        switch (newTier) {
            case GOLD:
                tierInfo.setNextTierThreshold(50000);
                tierInfo.setBenefits("1.5x cashback, Priority processing, 0.5% rate discount");
                tierInfo.setInterestRateDiscount(0.5);
                tierInfo.setCashbackMultiplier(1.5);
                awardPoints(tierInfo.getCustomer().getId(), 1000, RewardCategory.TIER_UPGRADE, 
                        "Gold tier upgrade bonus");
                break;
            case PLATINUM:
                tierInfo.setNextTierThreshold(200000);
                tierInfo.setBenefits("2x cashback, Premium processing, 1% rate discount, Free restructuring, Fee waivers");
                tierInfo.setInterestRateDiscount(1.0);
                tierInfo.setFreeRestructuring(true);
                tierInfo.setPrepaymentFeeWaiver(true);
                tierInfo.setCashbackMultiplier(2.0);
                awardPoints(tierInfo.getCustomer().getId(), 5000, RewardCategory.TIER_UPGRADE, 
                        "Platinum tier upgrade bonus");
                break;
            case DIAMOND:
                tierInfo.setNextTierThreshold(0);
                tierInfo.setBenefits("3x cashback, VIP processing, 1.5% rate discount, Free restructuring, All fee waivers, Dedicated RM");
                tierInfo.setInterestRateDiscount(1.5);
                tierInfo.setFreeRestructuring(true);
                tierInfo.setPrepaymentFeeWaiver(true);
                tierInfo.setCashbackMultiplier(3.0);
                awardPoints(tierInfo.getCustomer().getId(), 10000, RewardCategory.TIER_UPGRADE, 
                        "Diamond tier upgrade bonus");
                break;
            default:
                break;
        }
        
        tierInfo.setLastReviewDate(LocalDate.now());
    }
    
    // Award cashback
    public void awardCashback(Long customerId, Double transactionAmount, Double basePercentage, String description) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
        
        CustomerTierInfo tierInfo = getTierInfo(customerId);
        Double multiplier = tierInfo != null ? tierInfo.getCashbackMultiplier() : 1.0;
        
        Double effectivePercentage = basePercentage * multiplier;
        Double cashbackAmount = transactionAmount * effectivePercentage / 100;
        
        Cashback cashback = new Cashback();
        cashback.setCustomer(customer);
        cashback.setTransactionAmount(transactionAmount);
        cashback.setCashbackAmount(cashbackAmount);
        cashback.setCashbackPercentage(effectivePercentage);
        cashback.setCategory(RewardCategory.CASHBACK);
        cashback.setTransactionDate(LocalDate.now());
        cashback.setCreditDate(LocalDate.now());
        cashback.setIsCredited(true);
        cashback.setTierMultiplier(multiplier);
        cashback.setDescription(description);
        
        cashbackRepository.save(cashback);
    }
    
    // Redeem points
    public RedemptionResponse redeemPoints(Long customerId, RedemptionRequest request) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
        
        Integer activePoints = getActivePoints(customerId);
        if (activePoints < request.getPointsToRedeem()) {
            throw new InsufficientFundsException("Insufficient points for redemption");
        }
        
        // Calculate cash value (1 point = ₹0.25)
        Double cashValue = request.getPointsToRedeem() * 0.25;
        
        PointsRedemption redemption = new PointsRedemption();
        redemption.setCustomer(customer);
        redemption.setRedemptionType(request.getRedemptionType());
        redemption.setPointsRedeemed(request.getPointsToRedeem());
        redemption.setCashValue(cashValue);
        redemption.setRedemptionDate(LocalDate.now());
        redemption.setStatus("PENDING");
        redemption.setBeneficiaryDetails(request.getBeneficiaryDetails());
        redemption.setDescription("Redeemed " + request.getPointsToRedeem() + " points for " + request.getRedemptionType());
        
        PointsRedemption saved = pointsRedemptionRepository.save(redemption);
        
        // Mark oldest points as redeemed
        List<RewardPoints> availablePoints = rewardPointsRepository.findByCustomerIdAndIsExpiredFalse(customerId);
        availablePoints = availablePoints.stream()
                .filter(p -> !p.getIsRedeemed())
                .sorted((a, b) -> a.getEarnedDate().compareTo(b.getEarnedDate()))
                .collect(Collectors.toList());
        
        Integer remainingToRedeem = request.getPointsToRedeem();
        for (RewardPoints points : availablePoints) {
            if (remainingToRedeem <= 0) break;
            
            if (points.getPoints() <= remainingToRedeem) {
                points.setIsRedeemed(true);
                points.setRedeemedDate(LocalDate.now());
                remainingToRedeem -= points.getPoints();
            }
        }
        rewardPointsRepository.saveAll(availablePoints);
        
        // Update tier info
        updateTierInfo(customerId, 0); // Recalculate active points
        
        return convertToRedemptionResponse(saved);
    }
    
    // Generate referral code
    public ReferralDto generateReferralCode(Long customerId, ReferralRequest request) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
        
        String referralCode = "REF" + customer.getId() + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        
        ReferralBonus referral = new ReferralBonus();
        referral.setReferrer(customer);
        referral.setReferralCode(referralCode);
        referral.setReferredEmail(request.getReferredEmail());
        referral.setReferredPhone(request.getReferredPhone());
        referral.setStatus(ReferralStatus.PENDING);
        referral.setReferralDate(LocalDate.now());
        referral.setBonusPoints(500);
        referral.setBonusCredited(false);
        
        ReferralBonus saved = referralBonusRepository.save(referral);
        
        return convertToReferralDto(saved);
    }
    
    // Get referral history
    public List<ReferralDto> getReferralHistory(Long customerId) {
        List<ReferralBonus> referrals = referralBonusRepository.findByReferrerIdOrderByReferralDateDesc(customerId);
        return referrals.stream()
                .map(this::convertToReferralDto)
                .collect(Collectors.toList());
    }
    
    // Process referral when new customer registers
    public void processReferralRegistration(String referralCode, Long newCustomerId) {
        Optional<ReferralBonus> referralOpt = referralBonusRepository.findByReferralCode(referralCode);
        if (!referralOpt.isPresent()) {
            return;
        }
        
        ReferralBonus referral = referralOpt.get();
        if (referral.getStatus() != ReferralStatus.PENDING) {
            return;
        }
        
        Customer newCustomer = customerRepository.findById(newCustomerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
        
        referral.setReferred(newCustomer);
        referral.setStatus(ReferralStatus.REGISTERED);
        referral.setRegistrationDate(LocalDate.now());
        
        referralBonusRepository.save(referral);
    }
    
    // Qualify referral (e.g., after first transaction)
    public void qualifyReferral(Long customerId) {
        List<ReferralBonus> referrals = referralBonusRepository.findByStatus(ReferralStatus.REGISTERED);
        
        for (ReferralBonus referral : referrals) {
            if (referral.getReferred() != null && referral.getReferred().getId().equals(customerId)) {
                referral.setStatus(ReferralStatus.QUALIFIED);
                referral.setQualificationDate(LocalDate.now());
                
                // Award bonus to referrer
                awardPoints(referral.getReferrer().getId(), referral.getBonusPoints(), 
                        RewardCategory.REFERRAL, "Referral bonus for " + referral.getReferredEmail());
                
                referral.setBonusCredited(true);
                referral.setBonusCreditDate(LocalDate.now());
                referral.setStatus(ReferralStatus.REWARDED);
                
                referralBonusRepository.save(referral);
            }
        }
    }
    
    // Check and award milestones
    private void checkAndAwardMilestones(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
        
        // Check FIRST_TRANSACTION milestone
        checkMilestone(customer, MilestoneType.FIRST_TRANSACTION, 1.0, 100);
        
        // Other milestones would be checked based on transaction counts, amounts, etc.
        // This is a simplified version
    }
    
    // Check specific milestone
    private void checkMilestone(Customer customer, MilestoneType milestoneType, Double value, Integer bonusPoints) {
        Optional<MilestoneReward> existing = milestoneRewardRepository
                .findByCustomerIdAndMilestoneType(customer.getId(), milestoneType);
        
        if (existing.isPresent()) {
            return; // Milestone already achieved
        }
        
        MilestoneReward milestone = new MilestoneReward();
        milestone.setCustomer(customer);
        milestone.setMilestoneType(milestoneType);
        milestone.setAchievedDate(LocalDate.now());
        milestone.setBonusPoints(bonusPoints);
        milestone.setMilestoneValue(value);
        milestone.setIsCredited(false);
        milestone.setDescription("Achieved " + milestoneType.name());
        
        milestoneRewardRepository.save(milestone);
        
        // Award bonus points
        awardPoints(customer.getId(), bonusPoints, RewardCategory.MILESTONE, 
                "Milestone bonus: " + milestoneType.name());
        
        milestone.setIsCredited(true);
        milestone.setCreditedDate(LocalDate.now());
        milestoneRewardRepository.save(milestone);
    }
    
    // Get milestone progress
    public List<MilestoneDto> getMilestoneProgress(Long customerId) {
        List<MilestoneReward> milestones = milestoneRewardRepository
                .findByCustomerIdOrderByAchievedDateDesc(customerId);
        
        return milestones.stream()
                .map(this::convertToMilestoneDto)
                .collect(Collectors.toList());
    }
    
    // Get active offers for customer
    public List<LoyaltyOffer> getActiveOffers(Long customerId) {
        return loyaltyOfferRepository.findActiveOffers(customerId, LocalDate.now());
    }
    
    // Expire old points (scheduled job)
    public void expireOldPoints() {
        List<RewardPoints> expiredPoints = rewardPointsRepository.findExpiredPoints(LocalDate.now());
        
        for (RewardPoints points : expiredPoints) {
            points.setIsExpired(true);
        }
        
        rewardPointsRepository.saveAll(expiredPoints);
    }
    
    // Conversion methods
    private RedemptionResponse convertToRedemptionResponse(PointsRedemption redemption) {
        RedemptionResponse response = new RedemptionResponse();
        response.setId(redemption.getId());
        response.setRedemptionType(redemption.getRedemptionType());
        response.setPointsRedeemed(redemption.getPointsRedeemed());
        response.setCashValue(redemption.getCashValue());
        response.setRedemptionDate(redemption.getRedemptionDate());
        response.setTransactionReference(redemption.getTransactionReference());
        response.setStatus(redemption.getStatus());
        response.setProcessedDate(redemption.getProcessedDate());
        response.setBeneficiaryDetails(redemption.getBeneficiaryDetails());
        response.setDescription(redemption.getDescription());
        return response;
    }
    
    private ReferralDto convertToReferralDto(ReferralBonus referral) {
        ReferralDto dto = new ReferralDto();
        dto.setId(referral.getId());
        dto.setReferralCode(referral.getReferralCode());
        dto.setReferredEmail(referral.getReferredEmail());
        dto.setReferredPhone(referral.getReferredPhone());
        dto.setStatus(referral.getStatus());
        dto.setReferralDate(referral.getReferralDate());
        dto.setRegistrationDate(referral.getRegistrationDate());
        dto.setQualificationDate(referral.getQualificationDate());
        dto.setBonusCreditDate(referral.getBonusCreditDate());
        dto.setBonusPoints(referral.getBonusPoints());
        dto.setBonusCredited(referral.getBonusCredited());
        return dto;
    }
    
    private MilestoneDto convertToMilestoneDto(MilestoneReward milestone) {
        MilestoneDto dto = new MilestoneDto();
        dto.setId(milestone.getId());
        dto.setMilestoneType(milestone.getMilestoneType());
        dto.setAchievedDate(milestone.getAchievedDate());
        dto.setBonusPoints(milestone.getBonusPoints());
        dto.setMilestoneValue(milestone.getMilestoneValue());
        dto.setIsCredited(milestone.getIsCredited());
        dto.setCreditedDate(milestone.getCreditedDate());
        dto.setDescription(milestone.getDescription());
        return dto;
    }
    
    public TierInfoDto convertToTierDto(CustomerTierInfo tierInfo) {
        TierInfoDto dto = new TierInfoDto();
        dto.setId(tierInfo.getId());
        dto.setTierLevel(tierInfo.getTierLevel());
        dto.setTotalPoints(tierInfo.getTotalPoints());
        dto.setActivePoints(tierInfo.getActivePoints());
        dto.setTierStartDate(tierInfo.getTierStartDate());
        dto.setTierUpgradeDate(tierInfo.getTierUpgradeDate());
        dto.setNextTierThreshold(tierInfo.getNextTierThreshold());
        dto.setBenefits(tierInfo.getBenefits());
        dto.setInterestRateDiscount(tierInfo.getInterestRateDiscount());
        dto.setFreeRestructuring(tierInfo.getFreeRestructuring());
        dto.setPrepaymentFeeWaiver(tierInfo.getPrepaymentFeeWaiver());
        dto.setCashbackMultiplier(tierInfo.getCashbackMultiplier());
        return dto;
    }
}
