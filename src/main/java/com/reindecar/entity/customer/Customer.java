package com.reindecar.entity.customer;

import com.reindecar.common.entity.AuditableEntity;
import com.reindecar.common.valueobject.CreditScore;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "customers")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "customer_type", discriminatorType = DiscriminatorType.STRING)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class Customer extends AuditableEntity {

    @Column(nullable = false, unique = true)
    private UUID publicId;

    @Enumerated(EnumType.STRING)
    @Column(name = "customer_type", insertable = false, updatable = false)
    private CustomerType customerType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CustomerStatus status;

    @Column(length = 20, unique = true)
    private String phone;

    @Column(length = 100, unique = true)
    private String email;

    @Column(length = 500)
    private String address;

    @Column(length = 50)
    private String city;

    @Column(length = 1000)
    private String notes;

    @Column(nullable = false)
    private boolean blacklisted = false;

    @Column(length = 500)
    private String blacklistReason;

    @Embedded
    private CreditScore creditScore;

    protected Customer(CustomerType customerType) {
        this.publicId = UUID.randomUUID();
        this.customerType = customerType;
        this.status = CustomerStatus.ACTIVE;
        this.blacklisted = false;
        this.creditScore = CreditScore.defaultScore();
    }

    public void blacklist(String reason) {
        this.blacklisted = true;
        this.blacklistReason = reason;
        this.status = CustomerStatus.BLACKLISTED;
    }

    public void removeFromBlacklist() {
        this.blacklisted = false;
        this.blacklistReason = null;
        this.status = CustomerStatus.ACTIVE;
    }

    public void activate() {
        if (!this.blacklisted) {
            this.status = CustomerStatus.ACTIVE;
        }
    }

    public void deactivate() {
        if (!this.blacklisted) {
            this.status = CustomerStatus.INACTIVE;
        }
    }

    protected void setContactInfo(String phone, String email, String address, String city) {
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.city = city;
    }

    public void updateCreditScore(Integer newScore) {
        if (this.creditScore == null) {
            this.creditScore = CreditScore.of(newScore);
        } else {
            this.creditScore.updateScore(newScore);
        }
    }

    public CreditScore.CreditRating getCreditRating() {
        return creditScore != null ? creditScore.getRating() : null;
    }

    public Integer getCreditScoreValue() {
        return creditScore != null ? creditScore.getScore() : null;
    }

    public boolean isBlacklisted() {
        return blacklisted;
    }

    public boolean isActive() {
        return status == CustomerStatus.ACTIVE;
    }

    public abstract String getDisplayName();
}
