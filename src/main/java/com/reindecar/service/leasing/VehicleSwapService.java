package com.reindecar.service.leasing;

import com.reindecar.common.exception.BusinessException;
import com.reindecar.common.exception.ErrorCode;
import com.reindecar.common.valueobject.Money;
import com.reindecar.dto.leasing.VehicleSwapRequest;
import com.reindecar.dto.leasing.VehicleSwapResponse;
import com.reindecar.entity.leasing.VehicleSwap;
import com.reindecar.entity.rental.Rental;
import com.reindecar.entity.vehicle.Vehicle;
import com.reindecar.repository.leasing.VehicleSwapRepository;
import com.reindecar.repository.rental.RentalRepository;
import com.reindecar.repository.vehicle.VehicleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class VehicleSwapService {

    private final VehicleSwapRepository swapRepository;
    private final RentalRepository rentalRepository;
    private final VehicleRepository vehicleRepository;

    @Transactional
    public VehicleSwapResponse swapVehicle(Long rentalId, VehicleSwapRequest request, String processedBy) {
        Rental rental = getRentalOrThrow(rentalId);
        Vehicle newVehicle = getVehicleOrThrow(request.newVehicleId());
        
        Long oldVehicleId = rental.getVehicleId();
        
        if (oldVehicleId.equals(request.newVehicleId())) {
            throw new BusinessException(ErrorCode.INVALID_OPERATION, 
                "New vehicle cannot be the same as current vehicle");
        }
        
        LocalDate swapDate = request.swapDate() != null ? request.swapDate() : LocalDate.now();
        
        Money priceDifference = request.priceDifference() != null 
            ? Money.of(request.priceDifference(), Money.DEFAULT_CURRENCY)
            : Money.zero(Money.DEFAULT_CURRENCY);
        
        VehicleSwap swap = VehicleSwap.create(
            rentalId,
            oldVehicleId,
            request.newVehicleId(),
            swapDate,
            request.reason(),
            request.currentKm(),
            0,
            priceDifference,
            request.notes(),
            processedBy
        );
        
        VehicleSwap saved = swapRepository.save(swap);
        log.info("Vehicle swapped for rental {}: {} -> {}", rentalId, oldVehicleId, request.newVehicleId());
        
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<VehicleSwapResponse> getSwapHistory(Long rentalId) {
        getRentalOrThrow(rentalId);
        return swapRepository.findByRentalIdOrderBySwapDateDesc(rentalId)
            .stream()
            .map(this::toResponse)
            .toList();
    }

    @Transactional(readOnly = true)
    public VehicleSwapResponse getById(Long id) {
        return toResponse(getSwapOrThrow(id));
    }

    @Transactional(readOnly = true)
    public long getSwapCount(Long rentalId) {
        return swapRepository.countByRentalId(rentalId);
    }

    private Rental getRentalOrThrow(Long rentalId) {
        return rentalRepository.findById(rentalId)
            .orElseThrow(() -> new BusinessException(ErrorCode.RENTAL_NOT_FOUND, rentalId.toString()));
    }

    private Vehicle getVehicleOrThrow(Long vehicleId) {
        return vehicleRepository.findById(vehicleId)
            .orElseThrow(() -> new BusinessException(ErrorCode.VEHICLE_NOT_FOUND, vehicleId.toString()));
    }

    private VehicleSwap getSwapOrThrow(Long id) {
        return swapRepository.findById(id)
            .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND, "Swap not found: " + id));
    }

    private VehicleSwapResponse toResponse(VehicleSwap swap) {
        return new VehicleSwapResponse(
            swap.getId(),
            swap.getRentalId(),
            swap.getOldVehicleId(),
            swap.getNewVehicleId(),
            swap.getSwapDate(),
            swap.getReason(),
            swap.getOldVehicleKm(),
            swap.getNewVehicleKm(),
            swap.getPriceDifference() != null ? swap.getPriceDifference().getAmount() : null,
            swap.getPriceDifference() != null ? swap.getPriceDifference().getCurrency() : null,
            swap.getNotes(),
            swap.getProcessedBy(),
            swap.getCreatedAt()
        );
    }
}
