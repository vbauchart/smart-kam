package com.smartkam.project;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@Controller
@RequestMapping("/projects/{projectId}/families/{familyId}")
class FamilyController {

    private final FamilyService familyService;

    FamilyController(FamilyService familyService) {
        this.familyService = familyService;
    }

    // -------------------------------------------------------------------------
    // GET — full page
    // -------------------------------------------------------------------------

    @GetMapping
    String detail(@PathVariable Long projectId,
                  @PathVariable Long familyId,
                  Model model) {
        model.addAttribute("view", familyService.getView(projectId, familyId));
        return "families/detail";
    }

    // -------------------------------------------------------------------------
    // POST — HTMX endpoints, all return the recalc fragment
    // -------------------------------------------------------------------------

    @PostMapping("/references/{refId}/base")
    String updateBase(@PathVariable Long projectId,
                      @PathVariable Long familyId,
                      @PathVariable Long refId,
                      @RequestParam("base_price") BigDecimal value,
                      Model model) {
        model.addAttribute("view", familyService.updateBasePrice(projectId, familyId, refId, value));
        return "families/_recalc-fragment :: recalc";
    }

    @PostMapping("/references/{refId}/rd")
    String updateRd(@PathVariable Long projectId,
                    @PathVariable Long familyId,
                    @PathVariable Long refId,
                    @RequestParam("rd_value") BigDecimal value,
                    Model model) {
        model.addAttribute("view", familyService.updateRdAmortization(projectId, familyId, refId, value));
        return "families/_recalc-fragment :: recalc";
    }

    @PostMapping("/references/{refId}/pkg")
    String updatePkg(@PathVariable Long projectId,
                     @PathVariable Long familyId,
                     @PathVariable Long refId,
                     @RequestParam("pkg_value") BigDecimal value,
                     Model model) {
        model.addAttribute("view", familyService.updatePackaging(projectId, familyId, refId, value));
        return "families/_recalc-fragment :: recalc";
    }

    @PostMapping("/sheets/{sheetId}/status")
    String updateStatus(@PathVariable Long projectId,
                        @PathVariable Long familyId,
                        @PathVariable Long sheetId,
                        @RequestParam("status") ModificationStatus status,
                        Model model) {
        model.addAttribute("view", familyService.updateSheetStatus(projectId, familyId, sheetId, status));
        return "families/_recalc-fragment :: recalc";
    }

    @PostMapping("/sheets/{sheetId}/references/{refId}/applies")
    String updateApplies(@PathVariable Long projectId,
                         @PathVariable Long familyId,
                         @PathVariable Long sheetId,
                         @PathVariable Long refId,
                         @RequestParam("applies") boolean applies,
                         Model model) {
        model.addAttribute("view", familyService.updateMatrixApplies(projectId, familyId, sheetId, refId, applies));
        return "families/_recalc-fragment :: recalc";
    }
}
