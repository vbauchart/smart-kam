package com.smartkam.project;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

interface SheetApplicationRepository extends JpaRepository<SheetApplication, Long> {

    Optional<SheetApplication> findBySheetIdAndReferenceId(Long sheetId, Long referenceId);
}
