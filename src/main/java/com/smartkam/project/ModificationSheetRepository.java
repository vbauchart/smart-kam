package com.smartkam.project;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

interface ModificationSheetRepository extends JpaRepository<ModificationSheet, Long> {

    @Query("""
            SELECT DISTINCT ms FROM ModificationSheet ms
            LEFT JOIN FETCH ms.impact
            LEFT JOIN FETCH ms.applications ma
            LEFT JOIN FETCH ma.reference
            WHERE ms.project.id = :projectId
            ORDER BY ms.id
            """)
    List<ModificationSheet> findByProjectIdWithDetails(@Param("projectId") Long projectId);
}
