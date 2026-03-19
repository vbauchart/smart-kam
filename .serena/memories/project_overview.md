# Project Overview — Torchebald (Smart KAM)

**Purpose**: Key Account Management tool for the automotive industry. Manages product pricing with families, references, modification sheets, and multi-year projections (productivity, tombée des rondelles R&D).

**Tech Stack**:
- Java 21, Spring Boot 3.3.5, Spring Modulith 1.2.5
- Thymeleaf + HTMX + Alpine.js (server-rendered with inline editing)
- PostgreSQL 16 (Docker Compose), Flyway migrations (V1–V5)
- Spring Data JPA, Spring MVC
- Apache POI 5.3.0 (Excel oracle tests)
- Testcontainers (PostgreSQL) for integration tests
- Maven build system

**Modules** (Spring Modulith):
- `com.smartkam.pricing` — Pure Java pricing engine (4 rules: initial SOP, updated SOP, productivity, tombée rondelles). No Spring beans, static methods.
- `com.smartkam.project` — JPA entities, repositories, services, controllers for projects, families, references, modification sheets.
- `com.smartkam.shared` — HomeController, layout.

**Key Business Concepts**:
- Product families (PF1–PF7) with references, each having a PriceBreakdown (base, R&D, packaging → SOP)
- Modification sheets with status (OPEN/VALIDATED/CANCELED) and impact matrix
- Per-family productivity params (rate, years, rdDropYear)
- What-if simulation: test OPEN sheet impact without persisting
