package com.reindecar.service.rental;

import com.reindecar.common.dto.PageResponse;
import com.reindecar.dto.rental.*;
import com.reindecar.entity.rental.Rental;
import com.reindecar.entity.rental.RentalStatus;
import com.reindecar.exception.rental.RentalNotFoundException;
import com.reindecar.mapper.rental.RentalMapper;
import com.reindecar.repository.rental.RentalRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class RentalService {

    private final RentalRepository rentalRepository;
    private final RentalMapper rentalMapper;
    private final CreateRentalUseCase createRentalUseCase;

    public PageResponse<RentalResponse> getAllRentals(Pageable pageable) {
        log.info("Fetching all rentals");
        Page<Rental> rentals = rentalRepository.findAll(pageable);
        return PageResponse.of(rentals.map(rentalMapper::toResponse));
    }

    public PageResponse<RentalResponse> getActiveRentals(Pageable pageable) {
        log.info("Fetching active rentals");
        Page<Rental> rentals = rentalRepository.findActiveRentals(pageable);
        return PageResponse.of(rentals.map(rentalMapper::toResponse));
    }

    public PageResponse<RentalResponse> getOverdueRentals(Pageable pageable) {
        log.info("Fetching overdue rentals");
        Page<Rental> rentals = rentalRepository.findOverdueRentals(pageable);
        return PageResponse.of(rentals.map(rentalMapper::toResponse));
    }

    public RentalResponse getRentalById(Long id) {
        log.info("Fetching rental by id: {}", id);
        Rental rental = findRentalByIdOrThrow(id);
        return rentalMapper.toResponse(rental);
    }

    @Transactional
    public RentalResponse createRental(CreateRentalRequest request, String createdBy) {
        log.info("Creating rental");
        Rental rental = createRentalUseCase.execute(request, createdBy);
        return rentalMapper.toResponse(rental);
    }

    @Transactional
    public RentalResponse reserveRental(Long id) {
        log.info("Reserving rental: {}", id);
        Rental rental = findRentalByIdOrThrow(id);
        rental.reserve();
        rentalRepository.save(rental);
        return rentalMapper.toResponse(rental);
    }

    @Transactional
    public RentalResponse activateRental(Long id, ActivateRentalRequest request) {
        log.info("Activating rental: {}", id);
        Rental rental = findRentalByIdOrThrow(id);
        rental.activate(request.startKm());
        rentalRepository.save(rental);
        return rentalMapper.toResponse(rental);
    }

    @Transactional
    public RentalResponse startReturn(Long id) {
        log.info("Starting return for rental: {}", id);
        Rental rental = findRentalByIdOrThrow(id);
        rental.startReturn();
        rentalRepository.save(rental);
        return rentalMapper.toResponse(rental);
    }

    @Transactional
    public RentalResponse completeRental(Long id, CompleteRentalRequest request) {
        log.info("Completing rental: {}", id);
        Rental rental = findRentalByIdOrThrow(id);
        rental.complete(request.actualReturnDate(), request.endKm(), rental.getExtraKmCharge());
        rentalRepository.save(rental);
        return rentalMapper.toResponse(rental);
    }

    @Transactional
    public void cancelRental(Long id) {
        log.info("Cancelling rental: {}", id);
        Rental rental = findRentalByIdOrThrow(id);
        rental.cancel();
        rentalRepository.save(rental);
    }

    private Rental findRentalByIdOrThrow(Long id) {
        return rentalRepository.findById(id)
            .orElseThrow(() -> new RentalNotFoundException(id));
    }
}
