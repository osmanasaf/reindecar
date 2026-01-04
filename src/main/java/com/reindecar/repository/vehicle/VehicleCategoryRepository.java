package com.reindecar.repository.vehicle;

import com.reindecar.entity.vehicle.VehicleCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VehicleCategoryRepository extends JpaRepository<VehicleCategory, Long> {

    @Query("SELECT vc FROM VehicleCategory vc WHERE vc.code = :code")
    Optional<VehicleCategory> findByCode(String code);

    @Query("SELECT CASE WHEN COUNT(vc) > 0 THEN true ELSE false END FROM VehicleCategory vc WHERE vc.code = :code")
    boolean existsByCode(String code);

    @Query("SELECT vc FROM VehicleCategory vc WHERE vc.active = true ORDER BY vc.sortOrder")
    List<VehicleCategory> findAllActiveOrderBySortOrder();
}
