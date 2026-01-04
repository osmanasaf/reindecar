package com.reindecar.repository.vehicle;

import com.reindecar.entity.vehicle.Vehicle;
import com.reindecar.entity.vehicle.VehicleStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    @Query("SELECT v FROM Vehicle v WHERE v.plateNumber = :plateNumber AND v.deleted = false")
    Optional<Vehicle> findByPlateNumberAndDeletedFalse(String plateNumber);

    @Query("SELECT CASE WHEN COUNT(v) > 0 THEN true ELSE false END FROM Vehicle v WHERE v.plateNumber = :plateNumber AND v.deleted = false")
    boolean existsByPlateNumberAndDeletedFalse(String plateNumber);

    @Query("SELECT CASE WHEN COUNT(v) > 0 THEN true ELSE false END FROM Vehicle v WHERE v.vinNumber = :vinNumber AND v.deleted = false")
    boolean existsByVinNumberAndDeletedFalse(String vinNumber);

    @Query("SELECT v FROM Vehicle v WHERE v.status = :status AND v.deleted = false")
    Page<Vehicle> findByStatusAndDeletedFalse(VehicleStatus status, Pageable pageable);

    @Query("SELECT v FROM Vehicle v WHERE v.branchId = :branchId AND v.deleted = false")
    Page<Vehicle> findByBranchIdAndDeletedFalse(Long branchId, Pageable pageable);

    @Query("SELECT v FROM Vehicle v WHERE v.status = :status AND v.branchId = :branchId AND v.deleted = false")
    Page<Vehicle> findByStatusAndBranchIdAndDeletedFalse(VehicleStatus status, Long branchId, Pageable pageable);

    @Query("SELECT v FROM Vehicle v WHERE v.deleted = false")
    Page<Vehicle> findAllActive(Pageable pageable);

    @Query("SELECT v FROM Vehicle v WHERE v.id = :id AND v.deleted = false")
    Optional<Vehicle> findByIdAndNotDeleted(Long id);
}
