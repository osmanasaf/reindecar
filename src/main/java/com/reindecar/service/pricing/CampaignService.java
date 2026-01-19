package com.reindecar.service.pricing;

import com.reindecar.common.exception.BusinessException;
import com.reindecar.common.exception.ErrorCode;
import com.reindecar.dto.pricing.CampaignResponse;
import com.reindecar.dto.pricing.CreateCampaignRequest;
import com.reindecar.entity.pricing.Campaign;
import com.reindecar.entity.pricing.RentalType;
import com.reindecar.repository.pricing.CampaignRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CampaignService {

    private final CampaignRepository campaignRepository;

    @Transactional
    public CampaignResponse create(CreateCampaignRequest request) {
        validateDateRange(request.validFrom(), request.validTo());

        Campaign campaign = Campaign.create(
            request.name(),
            request.description(),
            request.discountType(),
            request.discountValue(),
            request.applicableRentalTypes(),
            request.validFrom(),
            request.validTo()
        );

        if (request.minTermMonths() != null) {
            campaign.setMinTermMonths(request.minTermMonths());
        }
        if (request.categoryId() != null) {
            campaign.setCategoryId(request.categoryId());
        }

        Campaign saved = campaignRepository.save(campaign);
        log.info("Campaign created: {}", saved.getName());
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<CampaignResponse> findAll() {
        return campaignRepository.findAll().stream()
            .map(this::toResponse)
            .toList();
    }

    @Transactional(readOnly = true)
    public List<CampaignResponse> findActive() {
        return campaignRepository.findByActiveTrue().stream()
            .map(this::toResponse)
            .toList();
    }

    @Transactional(readOnly = true)
    public List<CampaignResponse> findActiveCampaignsForDate(LocalDate date) {
        return campaignRepository.findActiveCampaigns(date).stream()
            .map(this::toResponse)
            .toList();
    }

    @Transactional(readOnly = true)
    public CampaignResponse findById(Long id) {
        return toResponse(getCampaignOrThrow(id));
    }

    @Transactional(readOnly = true)
    public List<Campaign> findApplicableCampaigns(RentalType rentalType, Long categoryId, LocalDate date) {
        return campaignRepository.findApplicableCampaigns(rentalType, categoryId, date);
    }

    @Transactional
    public void activate(Long id) {
        Campaign campaign = getCampaignOrThrow(id);
        campaign.activate();
        campaignRepository.save(campaign);
        log.info("Campaign activated: {}", campaign.getName());
    }

    @Transactional
    public void deactivate(Long id) {
        Campaign campaign = getCampaignOrThrow(id);
        campaign.deactivate();
        campaignRepository.save(campaign);
        log.info("Campaign deactivated: {}", campaign.getName());
    }

    @Transactional
    public void delete(Long id) {
        Campaign campaign = getCampaignOrThrow(id);
        campaignRepository.delete(campaign);
        log.info("Campaign deleted: {}", campaign.getName());
    }

    private Campaign getCampaignOrThrow(Long id) {
        return campaignRepository.findById(id)
            .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND, "Campaign not found: " + id));
    }

    private void validateDateRange(LocalDate validFrom, LocalDate validTo) {
        if (validTo.isBefore(validFrom)) {
            throw new BusinessException(ErrorCode.INVALID_PARAMETER, "Valid to date must be after valid from date");
        }
    }

    private CampaignResponse toResponse(Campaign campaign) {
        return new CampaignResponse(
            campaign.getId(),
            campaign.getName(),
            campaign.getDescription(),
            campaign.getDiscountType(),
            campaign.getDiscountValue(),
            campaign.getApplicableRentalTypes(),
            campaign.getValidFrom(),
            campaign.getValidTo(),
            campaign.getMinTermMonths(),
            campaign.getCategoryId(),
            campaign.isActive()
        );
    }
}
