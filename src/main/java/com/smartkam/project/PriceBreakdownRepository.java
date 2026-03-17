package com.smartkam.project;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

interface PriceBreakdownRepository extends JpaRepository<PriceBreakdown, Long> {

    Optional<PriceBreakdown> findByReferenceId(Long referenceId);
}
