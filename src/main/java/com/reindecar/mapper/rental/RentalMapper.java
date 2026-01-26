package com.reindecar.mapper.rental;

import com.reindecar.dto.rental.RentalResponse;
import com.reindecar.entity.rental.Rental;
import com.reindecar.entity.rental.RentalDriver;
import com.reindecar.repository.rental.RentalDriverRepository;
import org.springframework.stereotype.Component;

@Component
public class RentalMapper {

    private final RentalDriverRepository rentalDriverRepository;

    public RentalMapper(RentalDriverRepository rentalDriverRepository) {
        this.rentalDriverRepository = rentalDriverRepository;
    }

    public RentalResponse toResponse(Rental rental) {
        if (rental == null) {
            return null;
        }

        return new RentalResponse(
            rental.getId(),
            rental.getRentalNumber(),
            rental.getRentalType(),
            rental.getStatus(),
            rental.getVehicleId(),
            rental.getCustomerId(),
            getPrimaryDriverId(rental),
            rental.getBranchId(),
            rental.getReturnBranchId(),
            rental.getStartDate(),
            rental.getEndDate(),
            rental.getActualReturnDate(),
            rental.getTotalDays(),
            rental.getStartKm(),
            rental.getEndKm(),
            rental.getTotalKm(),
            rental.getKmPackageId(),
            rental.getCustomIncludedKm(),
            toAmount(rental.getCustomExtraKmPrice()),
            toAmount(rental.getDailyPrice()),
            toAmount(rental.getTotalPrice()),
            toAmount(rental.getDiscountAmount()),
            toAmount(rental.getExtraKmCharge()),
            toAmount(rental.getGrandTotal()),
            rental.getDailyPrice() != null ? rental.getDailyPrice().getCurrency() : null,
            rental.isOverdue(),
            rental.getOverdueDays(),
            rental.getNotes(),
            rental.getCreatedAt()
        );
    }

    private java.math.BigDecimal toAmount(com.reindecar.common.valueobject.Money money) {
        return money != null ? money.getAmount() : null;
    }

    private Long getPrimaryDriverId(Rental rental) {
        if (rental == null || rental.getId() == null) {
            return null;
        }
        return rentalDriverRepository.findByRentalIdAndPrimaryTrue(rental.getId())
            .map(RentalDriver::getDriverId)
            .orElse(null);
    }
}
