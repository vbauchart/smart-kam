package com.smartkam.project;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

interface ProductFamilyRepository extends JpaRepository<ProductFamily, Long> {

    @Query("""
            SELECT DISTINCT pf FROM ProductFamily pf
            LEFT JOIN FETCH pf.references pr
            LEFT JOIN FETCH pr.priceBreakdown
            WHERE pf.project.id = :projectId
            ORDER BY pf.code
            """)
    List<ProductFamily> findByProjectIdWithDetails(@Param("projectId") Long projectId);
}
