package com.reindecar.repository.leasing;

import com.reindecar.entity.leasing.VehicleSwap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VehicleSwapRepository extends JpaRepository<VehicleSwap, Long> {

    List<VehicleSwap> findByRentalIdOrderBySwapDateDesc(Long rentalId);

    List<VehicleSwap> findByOldVehicleIdOrNewVehicleIdOrderBySwapDateDesc(Long oldVehicleId, Long newVehicleId);

    long countByRentalId(Long rentalId);
}
