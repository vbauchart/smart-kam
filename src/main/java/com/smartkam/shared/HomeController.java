package com.smartkam.shared;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
class HomeController {

    private final JdbcTemplate jdbc;

    HomeController(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @GetMapping("/")
    String home(Model model) {
        model.addAttribute("projectCount",
                jdbc.queryForObject("SELECT COUNT(*) FROM project", Long.class));
        model.addAttribute("familyCount",
                jdbc.queryForObject("SELECT COUNT(*) FROM product_family", Long.class));
        model.addAttribute("sheetCount",
                jdbc.queryForObject("SELECT COUNT(*) FROM modification_sheet", Long.class));
        model.addAttribute("openSheetCount",
                jdbc.queryForObject("SELECT COUNT(*) FROM modification_sheet WHERE status = 'OPEN'", Long.class));
        return "index";
    }
}
