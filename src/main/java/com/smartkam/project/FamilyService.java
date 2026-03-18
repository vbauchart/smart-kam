package com.smartkam.project;

import com.smartkam.pricing.ModificationImpact;
import com.smartkam.pricing.PricingEngine;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
class FamilyService {

    private final ProductFamilyRepository     familyRepository;
    private final ModificationSheetRepository sheetRepository;
    private final PriceBreakdownRepository    priceBreakdownRepository;
    private final SheetApplicationRepository  applicationRepository;

    FamilyService(ProductFamilyRepository familyRepository,
                  ModificationSheetRepository sheetRepository,
                  PriceBreakdownRepository priceBreakdownRepository,
                  SheetApplicationRepository applicationRepository) {
        this.familyRepository       = familyRepository;
        this.sheetRepository        = sheetRepository;
        this.priceBreakdownRepository = priceBreakdownRepository;
        this.applicationRepository  = applicationRepository;
    }

    // -------------------------------------------------------------------------
    // Read
    // -------------------------------------------------------------------------

    @Transactional(readOnly = true)
    FamilyView getView(Long projectId, Long familyId) {
        ProductFamily family = familyRepository.findByIdWithRefs(familyId)
                .orElseThrow(() -> new IllegalArgumentException("Family not found: " + familyId));
        List<ProductReference> refs = family.getReferences();
        List<ModificationSheet> sheets = sheetRepository.findByProjectIdWithDetails(projectId);
        return compute(family, refs, sheets);
    }

    // -------------------------------------------------------------------------
    // Write — each returns a fresh view after persisting
    // -------------------------------------------------------------------------

    FamilyView updateBasePrice(Long projectId, Long familyId, Long refId, BigDecimal value) {
        PriceBreakdown pb = loadPb(refId);
        pb.setBasePrice(value);
        pb.setSopInitial(PricingEngine.computeInitialSop(value, pb.getRdAmortization(), pb.getPackaging()));
        pb.setUpdatedAt(OffsetDateTime.now());
        priceBreakdownRepository.save(pb);
        return getView(projectId, familyId);
    }

    FamilyView updateRdAmortization(Long projectId, Long familyId, Long refId, BigDecimal value) {
        PriceBreakdown pb = loadPb(refId);
        pb.setRdAmortization(value);
        pb.setSopInitial(PricingEngine.computeInitialSop(pb.getBasePrice(), value, pb.getPackaging()));
        pb.setUpdatedAt(OffsetDateTime.now());
        priceBreakdownRepository.save(pb);
        return getView(projectId, familyId);
    }

    FamilyView updatePackaging(Long projectId, Long familyId, Long refId, BigDecimal value) {
        PriceBreakdown pb = loadPb(refId);
        pb.setPackaging(value);
        pb.setSopInitial(PricingEngine.computeInitialSop(pb.getBasePrice(), pb.getRdAmortization(), value));
        pb.setUpdatedAt(OffsetDateTime.now());
        priceBreakdownRepository.save(pb);
        return getView(projectId, familyId);
    }

    FamilyView updateSheetStatus(Long projectId, Long familyId, Long sheetId, ModificationStatus status) {
        ModificationSheet sheet = sheetRepository.findById(sheetId)
                .orElseThrow(() -> new IllegalArgumentException("Sheet not found: " + sheetId));
        sheet.setStatus(status);
        sheet.setUpdatedAt(OffsetDateTime.now());
        sheetRepository.save(sheet);
        return getView(projectId, familyId);
    }

    FamilyView updateMatrixApplies(Long projectId, Long familyId,
                                    Long sheetId, Long refId, boolean applies) {
        SheetApplication app = applicationRepository.findBySheetIdAndReferenceId(sheetId, refId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Application not found for sheet " + sheetId + " / ref " + refId));
        app.setApplies(applies);
        applicationRepository.save(app);
        return getView(projectId, familyId);
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    private PriceBreakdown loadPb(Long refId) {
        return priceBreakdownRepository.findByReferenceId(refId)
                .orElseThrow(() -> new IllegalArgumentException("PriceBreakdown not found for ref " + refId));
    }

    private FamilyView compute(ProductFamily family,
                               List<ProductReference> refs,
                               List<ModificationSheet> sheets) {
        // Build sheet rows with ordered applications
        List<SheetRow> sheetRows = sheets.stream().map(sheet -> {
            SheetImpact impact = sheet.getImpact();
            List<RefApply> applications = refs.stream().map(ref -> {
                boolean applies = sheet.getApplications().stream()
                        .filter(a -> a.getReference().getId().equals(ref.getId()))
                        .map(SheetApplication::isApplies)
                        .findFirst().orElse(false);
                return new RefApply(ref.getId(), applies);
            }).toList();
            return new SheetRow(sheet, impact, applications);
        }).toList();

        // Section ① — SOP initials (already stored in DB, just read them)
        List<BigDecimal> sopInitials = refs.stream()
                .map(r -> r.getPriceBreakdown().getSopInitial())
                .toList();

        // Section ③ — Rule 2: compute updated prices per ref
        List<BigDecimal> updatedBases = new ArrayList<>();
        List<BigDecimal> updatedRds   = new ArrayList<>();
        List<BigDecimal> updatedPkgs  = new ArrayList<>();
        List<BigDecimal> sopUpdated   = new ArrayList<>();

        for (ProductReference ref : refs) {
            PriceBreakdown pb = ref.getPriceBreakdown();

            List<ModificationImpact> impacts = sheets.stream().map(sheet -> {
                SheetImpact si = sheet.getImpact();
                boolean applies = sheetRows.stream()
                        .filter(sr -> sr.sheet().getId().equals(sheet.getId()))
                        .flatMap(sr -> sr.applications().stream())
                        .filter(a -> a.refId().equals(ref.getId()))
                        .map(RefApply::applies)
                        .findFirst().orElse(false);
                return ModificationImpact.of(
                        si != null ? si.getPartPrice()       : BigDecimal.ZERO,
                        si != null ? si.getTefAmortization() : BigDecimal.ZERO,
                        si != null ? si.getPackagingImpact() : BigDecimal.ZERO,
                        applies,
                        sheet.getStatus() == ModificationStatus.VALIDATED
                );
            }).toList();

            BigDecimal updBase = PricingEngine.computeUpdatedBase(pb.getBasePrice(), impacts);
            BigDecimal updRd   = PricingEngine.computeUpdatedRdAmortization(pb.getRdAmortization(), impacts);
            BigDecimal updPkg  = PricingEngine.computeUpdatedPackaging(pb.getPackaging(), impacts);
            BigDecimal sop     = PricingEngine.computeUpdatedSop(updBase, updRd, updPkg);

            updatedBases.add(updBase);
            updatedRds.add(updRd);
            updatedPkgs.add(updPkg);
            sopUpdated.add(sop);
        }

        List<BigDecimal> deltas = new ArrayList<>();
        for (int i = 0; i < refs.size(); i++) {
            deltas.add(sopUpdated.get(i).subtract(sopInitials.get(i)));
        }

        // Section ④ — Rules 3 & 4 projection (use updated packaging)
        List<BigDecimal> pkgs = updatedPkgs;
        int sopYear = family.getProject().getSopDate() != null
                ? family.getProject().getSopDate().getYear() : 2014;
        List<ProjectionRow> projection = buildProjection(updatedRds, pkgs, sopUpdated, sopYear,
                family.getProductivityRate(), family.getProductivityYears(), family.getRdDropYear());

        return new FamilyView(family, family.getProject(), refs,
                sopInitials, sheetRows,
                updatedBases, updatedRds, sopUpdated, deltas,
                projection);
    }

    // package-private for testability
    static List<ProjectionRow> buildProjection(List<BigDecimal> updatedRds,
                                                 List<BigDecimal> pkgs,
                                                 List<BigDecimal> sopUpdated,
                                                 int sopYear,
                                                 BigDecimal productivityRate,
                                                 int productivityYears,
                                                 int rdDropYear) {
        List<ProjectionRow> rows = new ArrayList<>();
        List<BigDecimal> current = new ArrayList<>(sopUpdated);
        // Track per-ref R&D — drops to zero after tombée
        List<BigDecimal> currentRds = new ArrayList<>(updatedRds);

        rows.add(new ProjectionRow("SOP", sopYear, "", List.copyOf(current), ""));

        int lastYear = Math.max(productivityYears, rdDropYear) + 1;
        for (int y = 1; y <= lastYear; y++) {
            int yr = sopYear + y;
            String event = "";
            String cls   = "stable-row";

            // Tombée des rondelles — can happen during or after productivity
            boolean tombee = (y == rdDropYear);
            if (tombee) {
                for (int i = 0; i < current.size(); i++) {
                    current.set(i, PricingEngine.applyTombeeDesRondelles(current.get(i), currentRds.get(i)));
                    currentRds.set(i, BigDecimal.ZERO);
                }
                event = "Tombée des rondelles R&D";
                cls   = "drop-row";
            }

            // Productivity — applied every year within the productivity period
            boolean prod = (y <= productivityYears && productivityRate.signum() != 0);
            if (prod) {
                for (int i = 0; i < current.size(); i++) {
                    current.set(i, PricingEngine.applyProductivity(
                            current.get(i), currentRds.get(i), pkgs.get(i), productivityRate));
                }
                String prodLabel = "Productivité " +
                        productivityRate.multiply(BigDecimal.valueOf(100)).toPlainString() + "% / an";
                if (tombee) {
                    event += " + " + prodLabel;
                } else {
                    event = prodLabel;
                    cls   = "prod-row";
                }
            }

            // Emit rows: show productivity years, tombée year, one year after each
            if (y <= productivityYears) {
                rows.add(new ProjectionRow("SOP+" + y, yr, event, List.copyOf(current), cls));
            } else if (y == productivityYears + 1 && y != rdDropYear) {
                rows.add(new ProjectionRow("SOP+" + y, yr, "Prix stable", List.copyOf(current), "stable-row"));
                if (rdDropYear > productivityYears + 2) {
                    rows.add(new ProjectionRow("", 0, "", null, "ellipsis-row"));
                }
            } else if (y == rdDropYear) {
                rows.add(new ProjectionRow("SOP+" + y, yr, event, List.copyOf(current), cls));
            } else if (y == rdDropYear + 1) {
                rows.add(new ProjectionRow("SOP+" + y, yr, "Prix stable (sans rondelles)", List.copyOf(current), "stable-row"));
            }
        }

        return rows;
    }
}
