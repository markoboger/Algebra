# Monad Teaching Example

Three approaches to the same aggregation scenario: **Java-style** (nulls), **Exception-style** (throws/catches), and **Monad-style** (genuine container monads).

## Central Example: totalVolume

**Task**: Sum the volume of all drinks across a crate of packs of bottles (some may be empty/missing).

**Challenge**: Navigate multiple levels (Crate → Pack → Bottle → Drink) where any level can be missing/empty.

### Three Approaches

1. **Java-style**: Nested for-loops with null checks
2. **Exception-style**: For-loops with exception-based bounds checking
3. **Monad-style**: Single for-comprehension calling `Pack.flatMap` and `Crate.flatMap`

## Design

**Pack[A]** and **Crate[A]** are lawful monad containers (like List). Both have `flatMap` and `pure` (unit), satisfying monad laws under structural equality.

**Key point**: For-comprehensions call `Pack.flatMap` and `Crate.flatMap`, not List or Option. When nesting (`Crate[Pack[Bottle]]`), we use `.toCrate` conversion to enable `for pack <- crate; bottle <- pack.toCrate yield ...` calling `Crate.flatMap` legitimately.

## Run

```bash
sbt "runMain monad.Demo"   # Demo: side-by-side comparison
sbt test                    # All tests pass
```

## Tests

- **6 monad law properties** (ScalaCheck with generated functions):
  - Pack: left identity, right identity, associativity
  - Crate: left identity, right identity, associativity
- **5 equivalence tests**: All three styles yield identical results on happy path, empty bottles, missing packs, empty crate, and null bottles.
