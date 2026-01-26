package com.reindecar.service.pricing;

import com.reindecar.common.valueobject.Money;
import com.reindecar.dto.pricing.ExtraItemTypeRequest;
import com.reindecar.dto.pricing.ExtraItemTypeResponse;
import com.reindecar.dto.pricing.RentalExtraItemRequest;
import com.reindecar.dto.pricing.RentalExtraItemResponse;
import com.reindecar.entity.pricing.ExtraItemType;
import com.reindecar.entity.pricing.RentalExtraItem;
import com.reindecar.repository.pricing.ExtraItemTypeRepository;
import com.reindecar.repository.pricing.RentalExtraItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ExtraItemService {

    private final ExtraItemTypeRepository typeRepository;
    private final RentalExtraItemRepository itemRepository;

    public List<ExtraItemTypeResponse> findAllTypes() {
        return typeRepository.findByActiveTrueOrderBySortOrderAsc()
            .stream()
            .map(this::toTypeResponse)
            .toList();
    }

    @Transactional
    public ExtraItemTypeResponse createType(ExtraItemTypeRequest request) {
        if (typeRepository.existsByCode(request.code().toUpperCase())) {
            throw new IllegalArgumentException("Bu kod zaten kullanılıyor: " + request.code());
        }

        Money defaultAmount = request.defaultAmount() != null
            ? Money.of(request.defaultAmount(), request.currency())
            : null;

        ExtraItemType type = ExtraItemType.create(
            request.code(),
            request.name(),
            request.description(),
            defaultAmount,
            request.calculationType()
        );

        if (request.sortOrder() != null) {
            type.setSortOrder(request.sortOrder());
        }

        type = typeRepository.save(type);
        log.info("Ek kalem türü oluşturuldu: code={}", request.code());

        return toTypeResponse(type);
    }

    @Transactional
    public ExtraItemTypeResponse updateType(Long id, ExtraItemTypeRequest request) {
        ExtraItemType type = typeRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Kalem türü bulunamadı: " + id));

        Money defaultAmount = request.defaultAmount() != null
            ? Money.of(request.defaultAmount(), request.currency())
            : null;

        type.update(request.name(), request.description(), defaultAmount, request.calculationType());

        if (request.sortOrder() != null) {
            type.setSortOrder(request.sortOrder());
        }

        type = typeRepository.save(type);
        log.info("Ek kalem türü güncellendi: id={}", id);

        return toTypeResponse(type);
    }

    @Transactional
    public void deactivateType(Long id) {
        ExtraItemType type = typeRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Kalem türü bulunamadı: " + id));

        type.deactivate();
        typeRepository.save(type);
        log.info("Ek kalem türü deaktif edildi: id={}", id);
    }

    public List<RentalExtraItemResponse> findByRentalId(Long rentalId) {
        return itemRepository.findByRentalId(rentalId)
            .stream()
            .map(this::toItemResponse)
            .toList();
    }

    @Transactional
    public RentalExtraItemResponse addItemToRental(Long rentalId, RentalExtraItemRequest request) {
        Money amount = Money.of(request.amount(), request.currency());

        RentalExtraItem item;
        if (request.itemTypeId() != null) {
            ExtraItemType type = typeRepository.findById(request.itemTypeId())
                .orElseThrow(() -> new IllegalArgumentException("Kalem türü bulunamadı: " + request.itemTypeId()));

            item = RentalExtraItem.createFromType(rentalId, request.itemTypeId(), amount, request.calculationType());
        } else {
            if (request.customName() == null || request.customName().isBlank()) {
                throw new IllegalArgumentException("Serbest kalem için ad zorunludur");
            }
            item = RentalExtraItem.createCustom(rentalId, request.customName(), request.description(), amount, request.calculationType());
        }

        item = itemRepository.save(item);
        log.info("Kiralamaya ek kalem eklendi: rentalId={}, itemId={}", rentalId, item.getId());

        return toItemResponse(item);
    }

    @Transactional
    public void removeItemFromRental(Long itemId) {
        RentalExtraItem item = itemRepository.findById(itemId)
            .orElseThrow(() -> new IllegalArgumentException("Ek kalem bulunamadı: " + itemId));

        itemRepository.delete(item);
        log.info("Kiralamadan ek kalem silindi: itemId={}", itemId);
    }

    public Money calculateTotalExtraItems(Long rentalId, Money baseRentalPrice, int termMonths) {
        List<RentalExtraItem> items = itemRepository.findByRentalId(rentalId);

        return items.stream()
            .map(item -> item.calculateTotal(baseRentalPrice, termMonths))
            .reduce(Money.zero(), Money::add);
    }

    private ExtraItemTypeResponse toTypeResponse(ExtraItemType type) {
        return new ExtraItemTypeResponse(
            type.getId(),
            type.getCode(),
            type.getName(),
            type.getDescription(),
            type.getDefaultAmount() != null ? type.getDefaultAmount().getAmount() : null,
            type.getDefaultAmount() != null ? type.getDefaultAmount().getCurrency() : "TRY",
            type.getCalculationType(),
            type.getSortOrder(),
            type.isActive()
        );
    }

    private RentalExtraItemResponse toItemResponse(RentalExtraItem item) {
        String name;
        if (item.isPredefined() && item.getItemTypeId() != null) {
            name = typeRepository.findById(item.getItemTypeId())
                .map(ExtraItemType::getName)
                .orElse("Bilinmeyen Kalem");
        } else {
            name = item.getCustomName();
        }

        return new RentalExtraItemResponse(
            item.getId(),
            item.getRentalId(),
            item.getItemTypeId(),
            name,
            item.getDescription(),
            item.getAmount().getAmount(),
            item.getAmount().getCurrency(),
            item.getCalculationType(),
            null
        );
    }
}
