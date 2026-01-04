package com.reindecar.entity.contract;

import com.reindecar.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "contract_terms")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ContractTerm extends BaseEntity {

    @Column(nullable = false, name = "template_id")
    private Long templateId;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private boolean required = false;

    @Column(nullable = false)
    private int sortOrder;

    public static ContractTerm create(
            Long templateId,
            String title,
            String content,
            boolean isRequired,
            int sortOrder) {
        
        ContractTerm term = new ContractTerm();
        term.templateId = templateId;
        term.title = title;
        term.content = content;
        term.required = isRequired;
        term.sortOrder = sortOrder;
        return term;
    }

    public boolean isRequired() {
        return required;
    }
}
