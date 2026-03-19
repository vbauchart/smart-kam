# Code Style & Conventions

## Java
- Package-private classes by default (no `public` on entities, services, controllers within a module)
- Records for DTOs/value objects: `FamilyView`, `SheetRow`, `RefApply`, `ProjectionRow`, `UpdatedPrices`
- JPA entities use field access with private fields + explicit getters/setters (no Lombok)
- Repository interfaces: package-private, extend `JpaRepository`
- Service classes: `@Service @Transactional`, read methods annotated `@Transactional(readOnly = true)`
- Constructor injection (no `@Autowired`)
- `BigDecimal` for all monetary/rate values, scale=3 for prices, scale=4 for rates
- Static utility methods in `PricingEngine` (no Spring bean)

## SQL / Flyway
- snake_case table and column names
- Migrations numbered V1, V2, V3... with descriptive names
- Seed data included in migrations

## Templates
- Thymeleaf with `th:` attributes, HTMX `hx-post`/`hx-target`/`hx-swap`
- Alpine.js `x-data`/`x-show` for inline editing toggle
- Bootstrap 5 for styling
- Fragment-based HTMX responses (no full page reload)

## Testing
- Integration tests: Testcontainers with PostgreSQL 16-alpine
- Unit tests: JUnit 5, AssertJ assertions
- Test class naming: `*Test` for unit, `*IntegrationTest` for integration
- `@TestMethodOrder(MethodOrderer.OrderAnnotation.class)` for ordered integration tests
