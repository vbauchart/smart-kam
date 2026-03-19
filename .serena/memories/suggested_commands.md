# Suggested Commands

## Build & Run
- `mvn clean compile` — compile the project
- `mvn spring-boot:run` — run the app (requires PostgreSQL running)
- `./run_local.sh` — starts Docker Compose (PostgreSQL + pgAdmin) then runs the app
- `docker compose up -d` — start PostgreSQL and pgAdmin containers

## Testing
- `mvn test` — run all tests (unit + integration via Testcontainers)
- `mvn test -pl . -Dtest=FamilyPagesIntegrationTest` — run specific test class
- `mvn test -pl . -Dtest=PricingEngineTest` — run pricing unit tests

## Database
- PostgreSQL: `localhost:5432`, db=smartkam, user=smartkam, password=smartkam
- pgAdmin: `localhost:5050`, email=admin@smartkam.local, password=admin
- Flyway migrations in `src/main/resources/db/migration/` (V1–V5)

## Git
- Main branch: `develop`
- `git status`, `git log --oneline -10`

## System Utils
- `ls`, `cd`, `grep`, `find` — standard Linux commands
