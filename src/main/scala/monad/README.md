# Monad Teaching Example

Three approaches to the same aggregation scenario: **Java-style** (nulls), **Exception-style** (throws/catches), and **Monad-style** (genuine container monads).

## Central Example: totalVolume

**Task**: Sum the volume of all drinks across a crate of packs of bottles (some may be empty).

**Challenge**: Navigate multiple levels (Crate → Pack → Bottle → Drink) where bottles can be empty.

### Three Approaches

Each style uses its own idiomatic bottle type:
1. **Java-style**: `JavaBottle(drink: Drink | Null)` — nested for-loops with null checks
2. **Exception-style**: `ExceptionBottle` with accessor that throws `EmptyBottleException` — model throws, client catches
3. **Monad-style**: `MonadBottle(content: Option[Drink])` — for-comprehension calling `Crate.flatMap` with `Crate.fromOption`

## Design

**Pack[A]** and **Crate[A]** are lawful monad containers (like List). Both have `flatMap` and `pure` (unit), satisfying monad laws under structural equality.

**Key point**: The for-comprehension calls `Crate.flatMap` throughout (via `pack.toCrate` conversion). The last generator before `yield` desugars to `map`.

## Run

```bash
sbt "runMain monad.Demo"   # Demo: side-by-side comparison
sbt test                    # All tests pass
```

## Tests

- **6 monad law properties** (ScalaCheck with generated functions):
  - Pack: left identity, right identity, associativity
  - Crate: left identity, right identity, associativity
- **4 equivalence tests**: All three styles yield identical results (happy path, all empty bottles, pack with no bottles, empty crate)

## Comparison

| Aspect | Java | Exception | Monad |
|--------|------|-----------|-------|
| **Empty bottle** | null drink | throws `EmptyBottleException` | `None` |
| **Control flow** | Nested for-loops | For-loops + try/catch | Single for-comprehension |
| **Composition** | Manual null checks | Model throws, client catches | Automatic (flatMap) |
