package com.reindecar.repository.pricing;

import com.reindecar.entity.pricing.KmPackage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface KmPackageRepository extends JpaRepository<KmPackage, Long> {

    @Query("SELECT kp FROM KmPackage kp WHERE kp.active = true")
    List<KmPackage> findActivePackages();

    @Query("SELECT kp FROM KmPackage kp WHERE kp.id = :id AND kp.active = true")
    Optional<KmPackage> findByIdAndActive(Long id);
}
