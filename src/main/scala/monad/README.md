# Monad Teaching Example

Three approaches to the same navigation scenario: **Java-style** (nulls), **Exception-style** (throws/catches), and **Monad-style** (genuine container monads).

## Design

**Pack[A]** and **Crate[A]** are lawful monad containers (like List). Both have `flatMap` and `pure` (unit), satisfying monad laws under structural equality.

**Key point**: For-comprehensions call `Pack.flatMap` and `Crate.flatMap`, not List or Option. When nesting (`Crate[Pack[Bottle]]`), we use `.toCrate` conversion to enable `for pack <- crate; bottle <- pack.toCrate yield ...` calling `Crate.flatMap` legitimately.

## Run

```bash
sbt run   # Demo: side-by-side comparison
sbt test  # All tests pass (monad laws + equivalence)
```

## Tests

- **6 monad law properties** (ScalaCheck with generated functions):
  - Pack: left identity, right identity, associativity
  - Crate: left identity, right identity, associativity
- **6 equivalence tests**: All three styles yield identical results on happy/missing/empty cases.
