package com.reindecar.service.pricing;

import com.reindecar.common.exception.BusinessException;
import com.reindecar.common.exception.ErrorCode;
import com.reindecar.dto.pricing.CreateSeasonRequest;
import com.reindecar.dto.pricing.SeasonResponse;
import com.reindecar.dto.pricing.UpdateSeasonRequest;
import com.reindecar.entity.pricing.Season;
import com.reindecar.mapper.pricing.SeasonMapper;
import com.reindecar.repository.pricing.SeasonRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class SeasonService {

    private final SeasonRepository seasonRepository;
    private final SeasonMapper seasonMapper;

    @Transactional
    public SeasonResponse create(CreateSeasonRequest request) {
        validateDateRange(request.startDate(), request.endDate());
        validateNoOverlap(request.startDate(), request.endDate(), null);

        Season season = seasonMapper.toEntity(request);

        Season saved = seasonRepository.save(season);
        log.info("Season created: {}", saved.getName());
        return seasonMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<SeasonResponse> findAll() {
        return seasonRepository.findAll().stream()
            .map(seasonMapper::toResponse)
            .toList();
    }

    @Transactional(readOnly = true)
    public List<SeasonResponse> findActive() {
        return seasonRepository.findByActiveTrue().stream()
            .map(seasonMapper::toResponse)
            .toList();
    }

    @Transactional(readOnly = true)
    public SeasonResponse findById(Long id) {
        return seasonMapper.toResponse(getSeasonOrThrow(id));
    }

    @Transactional(readOnly = true)
    public Optional<Season> findActiveSeasonForDate(LocalDate date) {
        return seasonRepository.findActiveSeasonForDate(date);
    }

    @Transactional
    public SeasonResponse update(Long id, UpdateSeasonRequest request) {
        Season season = getSeasonOrThrow(id);

        if (request.startDate() != null || request.endDate() != null) {
            LocalDate newStart = request.startDate() != null ? request.startDate() : season.getStartDate();
            LocalDate newEnd = request.endDate() != null ? request.endDate() : season.getEndDate();
            validateDateRange(newStart, newEnd);
            validateNoOverlap(newStart, newEnd, id);
        }

        seasonMapper.updateEntity(season, request);
        
        Season saved = seasonRepository.save(season);
        log.info("Season updated: {}", saved.getName());
        return seasonMapper.toResponse(saved);
    }

    @Transactional
    public void delete(Long id) {
        Season season = getSeasonOrThrow(id);
        seasonRepository.delete(season);
        log.info("Season deleted: {}", season.getName());
    }

    @Transactional
    public void activate(Long id) {
        Season season = getSeasonOrThrow(id);
        season.activate();
        seasonRepository.save(season);
    }

    @Transactional
    public void deactivate(Long id) {
        Season season = getSeasonOrThrow(id);
        season.deactivate();
        seasonRepository.save(season);
    }

    private Season getSeasonOrThrow(Long id) {
        return seasonRepository.findById(id)
            .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND, "Season not found: " + id));
    }

    private void validateDateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate != null && endDate != null && endDate.isBefore(startDate)) {
            throw new BusinessException(ErrorCode.INVALID_PARAMETER, "End date must be after start date");
        }
    }

    private void validateNoOverlap(LocalDate startDate, LocalDate endDate, Long excludeId) {
        List<Season> overlapping = seasonRepository.findOverlappingSeasons(startDate, endDate);
        
        if (excludeId != null) {
            overlapping = overlapping.stream()
                .filter(s -> !s.getId().equals(excludeId))
                .toList();
        }

        if (!overlapping.isEmpty()) {
            throw new BusinessException(ErrorCode.DUPLICATE_ENTITY, 
                "Season overlaps with existing season: " + overlapping.get(0).getName());
        }
    }
}
