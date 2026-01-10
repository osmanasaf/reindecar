package com.reindecar.repository.pricing;

import com.reindecar.entity.pricing.KmBundle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KmBundleRepository extends JpaRepository<KmBundle, Long> {

    List<KmBundle> findByActiveTrue();
}
