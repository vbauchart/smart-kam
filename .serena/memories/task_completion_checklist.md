# Task Completion Checklist

When a coding task is completed, verify:

1. **Compile**: `mvn clean compile` passes without errors
2. **Tests**: `mvn test` — all tests pass (unit + integration via Testcontainers)
3. **No manual app launch**: always validate via Testcontainers integration tests, never start the app manually
4. **Backward compatibility**: check that changes to symbols don't break references (use `find_referencing_symbols`)
5. **Flyway**: if schema changes are needed, create a new migration file V{N+1}__description.sql
