package com.bank.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Generated;

@Entity
@Table(name="otp_verification")
public class OtpVerification {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name="user_id")
    private AppUser user;
    @Column(nullable=false)
    private String email;
    @Column(name="otp_code", nullable=false, length=6)
    private String otpCode;
    @Enumerated(value=EnumType.STRING)
    @Column(name="otp_type", nullable=false)
    private OtpType otpType;
    @Column(name="expires_at", nullable=false)
    private LocalDateTime expiresAt;
    @Column(nullable=false)
    private Boolean verified = false;
    @Column(name="created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(this.expiresAt);
    }

    @Generated
    public Long getId() {
        return id;
    }

    @Generated
    public AppUser getUser() {
        return user;
    }

    @Generated
    public String getEmail() {
        return email;
    }

    @Generated
    public String getOtpCode() {
        return otpCode;
    }

    @Generated
    public OtpType getOtpType() {
        return otpType;
    }

    @Generated
    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    @Generated
    public Boolean getVerified() {
        return verified;
    }

    @Generated
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    @Generated
    public void setId(Long id) {
        this.id = id;
    }

    @Generated
    public void setUser(AppUser user) {
        this.user = user;
    }

    @Generated
    public void setEmail(String email) {
        this.email = email;
    }

    @Generated
    public void setOtpCode(String otpCode) {
        this.otpCode = otpCode;
    }

    @Generated
    public void setOtpType(OtpType otpType) {
        this.otpType = otpType;
    }

    @Generated
    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    @Generated
    public void setVerified(Boolean verified) {
        this.verified = verified;
    }

    @Generated
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Generated
    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof OtpVerification)) {
            return false;
        }
        OtpVerification other = (OtpVerification)o;
        if (!other.canEqual(this)) {
            return false;
        }
        Long this$id = this.getId();
        Long other$id = other.getId();
        if (this$id == null ? other$id != null : !((Object)this$id).equals(other$id)) {
            return false;
        }
        Boolean this$verified = this.getVerified();
        Boolean other$verified = other.getVerified();
        if (this$verified == null ? other$verified != null : !((Object)this$verified).equals(other$verified)) {
            return false;
        }
        AppUser this$user = this.getUser();
        AppUser other$user = other.getUser();
        if (this$user == null ? other$user != null : !this$user.equals(other$user)) {
            return false;
        }
        String this$email = this.getEmail();
        String other$email = other.getEmail();
        if (this$email == null ? other$email != null : !this$email.equals(other$email)) {
            return false;
        }
        String this$otpCode = this.getOtpCode();
        String other$otpCode = other.getOtpCode();
        if (this$otpCode == null ? other$otpCode != null : !this$otpCode.equals(other$otpCode)) {
            return false;
        }
        OtpType this$otpType = this.getOtpType();
        OtpType other$otpType = other.getOtpType();
        if (this$otpType == null ? other$otpType != null : !((Object)((Object)this$otpType)).equals((Object)other$otpType)) {
            return false;
        }
        LocalDateTime this$expiresAt = this.getExpiresAt();
        LocalDateTime other$expiresAt = other.getExpiresAt();
        if (this$expiresAt == null ? other$expiresAt != null : !((Object)this$expiresAt).equals(other$expiresAt)) {
            return false;
        }
        LocalDateTime this$createdAt = this.getCreatedAt();
        LocalDateTime other$createdAt = other.getCreatedAt();
        return !(this$createdAt == null ? other$createdAt != null : !((Object)this$createdAt).equals(other$createdAt));
    }

    @Generated
    protected boolean canEqual(Object other) {
        return other instanceof OtpVerification;
    }

    @Generated
    @Override
    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Long $id = this.getId();
        result = result * 59 + ($id == null ? 43 : ((Object)$id).hashCode());
        Boolean $verified = this.getVerified();
        result = result * 59 + ($verified == null ? 43 : ((Object)$verified).hashCode());
        AppUser $user = this.getUser();
        result = result * 59 + ($user == null ? 43 : $user.hashCode());
        String $email = this.getEmail();
        result = result * 59 + ($email == null ? 43 : $email.hashCode());
        String $otpCode = this.getOtpCode();
        result = result * 59 + ($otpCode == null ? 43 : $otpCode.hashCode());
        OtpType $otpType = this.getOtpType();
        result = result * 59 + ($otpType == null ? 43 : ((Object)((Object)$otpType)).hashCode());
        LocalDateTime $expiresAt = this.getExpiresAt();
        result = result * 59 + ($expiresAt == null ? 43 : ((Object)$expiresAt).hashCode());
        LocalDateTime $createdAt = this.getCreatedAt();
        result = result * 59 + ($createdAt == null ? 43 : ((Object)$createdAt).hashCode());
        return result;
    }

    @Generated
    @Override
    public String toString() {
        return "OtpVerification(id=" + this.getId() + ", user=" + String.valueOf(this.getUser()) + ", email=" + this.getEmail() + ", otpCode=" + this.getOtpCode() + ", otpType=" + String.valueOf((Object)this.getOtpType()) + ", expiresAt=" + String.valueOf(this.getExpiresAt()) + ", verified=" + this.getVerified() + ", createdAt=" + String.valueOf(this.getCreatedAt()) + ")";
    }

    @Generated
    public OtpVerification() {
    }

    @Generated
    public OtpVerification(Long id, AppUser user, String email, String otpCode, OtpType otpType, LocalDateTime expiresAt, Boolean verified, LocalDateTime createdAt) {
        this.id = id;
        this.user = user;
        this.email = email;
        this.otpCode = otpCode;
        this.otpType = otpType;
        this.expiresAt = expiresAt;
        this.verified = verified;
        this.createdAt = createdAt;
    }

    public static enum OtpType {
        LOGIN,
        RESET_PASSWORD,
        VERIFY_EMAIL,
        TRANSACTION,
        MFA_EMAIL,
        MFA_SMS;

    }
}
