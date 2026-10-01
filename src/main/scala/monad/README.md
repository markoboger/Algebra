# Monad Teaching Example

Three approaches to the same aggregation scenario: **Java-style** (nulls), **Exception-style** (throws/catches), and **Monad-style** (genuine container monads).

## Central Example: totalVolume

**Task**: Sum the volume of all drinks across a crate of packs of bottles (some may be empty/missing).

**Challenge**: Navigate multiple levels (Crate → Pack → Bottle → Drink) where any level can be missing/empty.

### Three Approaches

1. **Java-style**: Nested for-loops with null checks at every level
2. **Exception-style**: For-loops, exceptions thrown/caught for empty bottles
3. **Monad-style**: Single for-comprehension calling `Crate.flatMap` throughout

## Design

**Pack[A]** and **Crate[A]** are lawful monad containers (like List). Both have `flatMap` and `pure` (unit), satisfying monad laws under structural equality.

**Key point**: The for-comprehension calls `Crate.flatMap` throughout (via `pack.toCrate` conversion), not List or Option. The last generator before `yield` desugars to `map`.

## Run

```bash
sbt "runMain monad.Demo"   # Demo: side-by-side comparison
sbt test                    # All tests pass
```

## Tests

- **6 monad law properties** (ScalaCheck with generated functions):
  - Pack: left identity, right identity, associativity
  - Crate: left identity, right identity, associativity
- **6 equivalence tests**: All three styles yield identical results (happy path, empty bottles, missing packs, empty crate, null crate, null bottles)

## Comparison

| Aspect | Java | Exception | Monad |
|--------|------|-----------|-------|
| **Missing elements** | null | throws/catch | empty container |
| **Control flow** | Nested for-loops | For-loops + try/catch | Single for-comprehension |
| **Composition** | Manual checks | Manual iteration | Automatic (flatMap) |
