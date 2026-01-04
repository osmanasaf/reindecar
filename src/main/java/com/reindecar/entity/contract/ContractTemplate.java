package com.reindecar.entity.contract;

import com.reindecar.common.entity.BaseEntity;
import com.reindecar.entity.pricing.RentalType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "contract_templates")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ContractTemplate extends BaseEntity {

    @Column(nullable = false, unique = true, length = 50)
    private String code;

    @Column(nullable = false, length = 200)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RentalType rentalType;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private boolean active = true;

    @Column(nullable = false)
    private int templateVersion = 1;

    @Column(nullable = false)
    private Instant createdAt;

    public static ContractTemplate create(
            String code,
            String name,
            RentalType rentalType,
            String content) {
        
        ContractTemplate template = new ContractTemplate();
        template.code = code;
        template.name = name;
        template.rentalType = rentalType;
        template.content = content;
        template.active = true;
        template.templateVersion = 1;
        template.createdAt = Instant.now();
        return template;
    }

    public void updateContent(String newContent) {
        this.content = newContent;
        this.templateVersion++;
    }

    public void activate() {
        this.active = true;
    }

    public void deactivate() {
        this.active = false;
    }

    public boolean isActive() {
        return active;
    }
}
