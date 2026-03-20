package com.smartkam.shared;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;
import java.util.Map;

/**
 * Populates sidebar navigation data for every page.
 * <ul>
 *   <li>{@code sidebarProjects} — lightweight list of all projects (id, name)</li>
 *   <li>{@code sidebarFamilies} — families of the active project, if a projectId is in the URL</li>
 *   <li>{@code activeProjectId} — id of the currently viewed project (or null)</li>
 *   <li>{@code activeFamilyId} — id of the currently viewed family (or null)</li>
 * </ul>
 */
@ControllerAdvice
class SidebarAdvice {

    private final JdbcTemplate jdbc;

    SidebarAdvice(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @ModelAttribute("sidebarProjects")
    List<Map<String, Object>> sidebarProjects() {
        return jdbc.queryForList("SELECT id, name FROM project ORDER BY name");
    }

    @ModelAttribute("sidebarFamilies")
    List<Map<String, Object>> sidebarFamilies(HttpServletRequest request) {
        Long projectId = extractPathId(request, "projects");
        if (projectId == null) return List.of();
        return jdbc.queryForList(
                "SELECT id, code, designation FROM product_family WHERE project_id = ? ORDER BY code",
                projectId);
    }

    @ModelAttribute("activeProjectId")
    Long activeProjectId(HttpServletRequest request) {
        return extractPathId(request, "projects");
    }

    @ModelAttribute("activeFamilyId")
    Long activeFamilyId(HttpServletRequest request) {
        return extractPathId(request, "families");
    }

    /** Extracts the numeric path segment right after the given segment name. */
    private static Long extractPathId(HttpServletRequest request, String segment) {
        String path = request.getRequestURI();
        String[] parts = path.split("/");
        for (int i = 0; i < parts.length - 1; i++) {
            if (parts[i].equals(segment)) {
                try {
                    return Long.parseLong(parts[i + 1]);
                } catch (NumberFormatException e) {
                    return null;
                }
            }
        }
        return null;
    }
}
