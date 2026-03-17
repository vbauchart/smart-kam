package com.smartkam.project;

import com.smartkam.pricing.PricingEngine;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

@Service
class ProjectService {

    private final ProjectRepository projectRepository;
    private final ProductFamilyRepository familyRepository;
    private final PriceBreakdownRepository priceBreakdownRepository;

    ProjectService(ProjectRepository projectRepository,
                   ProductFamilyRepository familyRepository,
                   PriceBreakdownRepository priceBreakdownRepository) {
        this.projectRepository = projectRepository;
        this.familyRepository = familyRepository;
        this.priceBreakdownRepository = priceBreakdownRepository;
    }

    List<Project> findAll() {
        return projectRepository.findAll();
    }

    Project findById(Long id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Project not found: " + id));
    }

    List<ProductFamily> findFamiliesWithDetails(Long projectId) {
        return familyRepository.findByProjectIdWithDetails(projectId);
    }

    @Transactional
    PriceBreakdown updateBasePrice(Long referenceId, BigDecimal newBase) {
        PriceBreakdown pb = priceBreakdownRepository.findByReferenceId(referenceId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "PriceBreakdown not found for reference: " + referenceId));
        pb.setBasePrice(newBase);
        BigDecimal sop = PricingEngine.computeInitialSop(
                newBase, pb.getRdAmortization(), pb.getPackaging());
        pb.setSopInitial(sop);
        pb.setUpdatedAt(OffsetDateTime.now());
        return priceBreakdownRepository.save(pb);
    }
}
