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

    @Query("SELECT kp FROM KmPackage kp WHERE kp.categoryId = :categoryId AND kp.active = true")
    List<KmPackage> findByCategoryIdAndActiveTrue(Long categoryId);

    @Query("SELECT kp FROM KmPackage kp WHERE kp.categoryId IS NULL AND kp.active = true")
    List<KmPackage> findGlobalActivePackages();

    @Query("SELECT kp FROM KmPackage kp WHERE (kp.categoryId = :categoryId OR kp.categoryId IS NULL) AND kp.active = true ORDER BY kp.categoryId DESC NULLS LAST")
    List<KmPackage> findAvailableForCategory(Long categoryId);
}
