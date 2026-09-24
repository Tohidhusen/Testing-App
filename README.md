# Testing App — Debugging Notes & Lessons Learned

This README documents the real bugs found and fixed while getting
`EmployeeRepositoryTest` working with `@DataJpaTest` + Testcontainers on
Spring Boot 4.1.1. Keeping this around as a reference for the next time
something like this bites.

## Stack

- Spring Boot 4.1.1 (Spring Framework 7, Hibernate 7)
- Spring Data JPA + PostgreSQL (via Testcontainers)
- JUnit 5 / AssertJ
- Java 25

---

## Mistake 1 — Wrong class imported due to a name collision

```java
// Wrong — this is Testcontainers' own internal utility class
import org.testcontainers.utility.TestcontainersConfiguration;
```

Testcontainers ships a class with the **exact same simple name**
(`TestcontainersConfiguration`) used for its own global settings. IDE
auto-import picked that one instead of the project's own Spring
`@TestConfiguration` class that declares the Postgres container bean.

Because the wrong class was imported, `@Import(TestcontainersConfiguration.class)`
silently pointed at the wrong config — no compile error, no obvious runtime
error, just a container bean that never got registered.

**Lesson:** when an import for a project-local class doesn't look right
(especially with generic names like `Config`, `Constants`,
`TestcontainersConfiguration`), always check the package. A same-named
library class is an easy trap.

```java
// Correct
import com.TestingApp.test.TestcontainersConfiguration;
```

---

## Mistake 2 — Config class not `public`

```java
// Wrong
@TestConfiguration
class TestcontainersConfiguration { ... }
```

The class lived in `com.TestingApp.test`, but was referenced via `@Import`
from `com.TestingApp.test.Repository` — a different package. A
package-private class can't be referenced from outside its own package at
all, so once the import above was fixed, this became a hard compile error.

**Lesson:** any class you intend to `@Import` across packages must be `public`.

```java
// Correct
@TestConfiguration
public class TestcontainersConfiguration { ... }
```

---

## Mistake 3 — `@DataJpaTest` silently swapped in an in-memory database

`AutoConfigureTestDatabase` was imported but never actually applied to the
test class. By default, `@DataJpaTest` **replaces your datasource with an
embedded database** if one is on the classpath — and `h2` was a test
dependency. Result: tests were quietly running against H2, not the
Testcontainers Postgres instance, even after Mistakes 1 and 2 were fixed.
No error, no warning in the test output — just tests that "pass" without
testing what you think they're testing.

**Lesson:** if you're using Testcontainers with `@DataJpaTest`, you
*must* opt out of the auto-replacement:

```java
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
```

An unused import is often a sign that something was intended but forgotten.

---

## Mistake 4 — `postgres:latest` broke on `TimeZone=Asia/Calcutta`

Switching the container image tag from a pinned version to `postgres:latest`
pulled in a newer PostgreSQL major version that rejects the legacy timezone
alias `Asia/Calcutta` (still reported as the JVM's default zone name on many
systems in India, instead of the canonical `Asia/Kolkata`):

so switch to postgres:16-alpine solve this problem
