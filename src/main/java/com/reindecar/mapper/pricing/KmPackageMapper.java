package com.reindecar.mapper.pricing;

import com.reindecar.common.valueobject.Money;
import com.reindecar.dto.pricing.CreateKmPackageRequest;
import com.reindecar.dto.pricing.KmPackageResponse;
import com.reindecar.entity.pricing.KmPackage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface KmPackageMapper {

    @Mapping(target = "extraKmPrice", source = "extraKmPrice", qualifiedByName = "moneyToAmount")
    @Mapping(target = "currency", source = "extraKmPrice", qualifiedByName = "moneyToCurrency")
    @Mapping(target = "global", source = "global")
    @Mapping(target = "categoryName", ignore = true)
    KmPackageResponse toResponse(KmPackage kmPackage);

    @Named("moneyToAmount")
    default java.math.BigDecimal moneyToAmount(Money money) {
        return money != null ? money.getAmount() : null;
    }

    @Named("moneyToCurrency")
    default String moneyToCurrency(Money money) {
        return money != null ? money.getCurrency() : "TRY";
    }

    default KmPackage toEntity(CreateKmPackageRequest request) {
        boolean isUnlimited = request.unlimited() != null && request.unlimited();
        return KmPackage.create(
            request.name(),
            request.includedKm(),
            Money.tl(request.extraKmPrice()),
            request.applicableTypes(),
            isUnlimited,
            request.categoryId()
        );
    }

    default KmPackageResponse toResponseWithCategoryName(KmPackage kmPackage, String categoryName) {
        return new KmPackageResponse(
            kmPackage.getId(),
            kmPackage.getName(),
            kmPackage.getIncludedKm(),
            kmPackage.getExtraKmPrice() != null ? kmPackage.getExtraKmPrice().getAmount() : null,
            kmPackage.getExtraKmPrice() != null ? kmPackage.getExtraKmPrice().getCurrency() : "TRY",
            kmPackage.getApplicableTypes(),
            kmPackage.isUnlimited(),
            kmPackage.isActive(),
            kmPackage.getCategoryId(),
            categoryName,
            kmPackage.isGlobal()
        );
    }
}
