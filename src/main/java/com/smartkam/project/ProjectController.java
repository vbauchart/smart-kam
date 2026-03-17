package com.smartkam.project;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@Controller
@RequestMapping("/projects")
class ProjectController {

    private final ProjectService projectService;
    private final ProductReferenceRepository referenceRepository;

    ProjectController(ProjectService projectService,
                      ProductReferenceRepository referenceRepository) {
        this.projectService = projectService;
        this.referenceRepository = referenceRepository;
    }

    @GetMapping
    String list(Model model) {
        model.addAttribute("projects", projectService.findAll());
        return "projects/list";
    }

    @GetMapping("/{id}")
    String detail(@PathVariable Long id, Model model) {
        model.addAttribute("project", projectService.findById(id));
        model.addAttribute("families", projectService.findFamiliesWithDetails(id));
        return "projects/detail";
    }

    @PostMapping("/{id}/references/{refId}/base-price")
    String updateBasePrice(@PathVariable Long id,
                           @PathVariable Long refId,
                           @RequestParam("base_price") BigDecimal basePrice,
                           Model model) {
        PriceBreakdown pb = projectService.updateBasePrice(refId, basePrice);
        model.addAttribute("ref", referenceRepository.findByIdWithFamily(refId).orElseThrow());
        model.addAttribute("pb", pb);
        model.addAttribute("projectId", id);
        return "projects/_price-row :: price-row";
    }
}
