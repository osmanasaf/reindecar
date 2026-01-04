package com.reindecar.repository.branch;

import com.reindecar.entity.branch.Branch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BranchRepository extends JpaRepository<Branch, Long> {

    Optional<Branch> findByCode(String code);

    @Query("SELECT b FROM Branch b WHERE b.deleted = false")
    Page<Branch> findAllActive(Pageable pageable);

    @Query("SELECT b FROM Branch b WHERE b.deleted = false AND b.active = true")
    Page<Branch> findAllActiveAndEnabled(Pageable pageable);

    @Query("SELECT b FROM Branch b WHERE b.deleted = false AND b.city = :city")
    Page<Branch> findByCity(String city, Pageable pageable);

    @Query("SELECT b FROM Branch b WHERE b.deleted = false AND b.id = :id")
    Optional<Branch> findByIdAndNotDeleted(Long id);

    boolean existsByCodeAndDeletedFalse(String code);
}
