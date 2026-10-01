# Monad Teaching Example

Three pedagogical implementations of the same bottle/pack/crate inventory scenario, comparing imperative null-checks, exception-based error handling, and monadic composition.

## The Domain

- **Bottle**: Can be full or empty
- **Pack**: Container of bottles
- **Crate**: Container of packs

Some bottles or packs may be missing in the data.

## Three Implementations

### 1. Java Style (`JavaStyle.scala`)

Imperative with manual null checks at every level.

```scala
if crate != null then
  val packs = crate.packs  // Fixed bug from slide 26!
  if packs != null then
    // ... nested checks continue
```

**Characteristics:**
- Uses `null` for missing elements
- Deep nesting (5 levels for bottle access)
- While loops and mutable counters (idiomatic Java in Scala)
- Verbose and error-prone

### 2. Exception Style (`ExceptionStyle.scala`)

Domain accessors throw exceptions when data is missing.

```scala
try
  val packs = crate.getPacks  // throws if missing
  for pack <- packs do
    try
      val bottles = pack.getBottles  // throws if missing
```

**Characteristics:**
- Accessors throw domain exceptions for missing data
- Try-catch blocks structure error handling
- Less nesting than null checks
- Expensive (exception creation, stack unwinding)

### 3. Monad Style (`MonadStyle.scala`)

Lawful monads (Pack, Crate) with Option for missing values.

```scala
for
  pack <- crate.packs
  bottleOpt <- pack.bottles
  bottle <- bottleOpt  // Option.flatMap
  if bottle.full
yield bottle
```

**Characteristics:**
- Pack and Crate are lawful monads (map/flatMap/withFilter)
- For-comprehensions desugar to domain types' flatMap/map
- Option[T] for possibly-missing elements
- Flat, type-safe, composable

## Monad Laws

Pack[T] and Crate[T] satisfy the monad laws (verified by property-based tests):

1. **Left identity**: `pure(a).flatMap(f) ≡ f(a)`
2. **Right identity**: `m.flatMap(pure) ≡ m`
3. **Associativity**: `m.flatMap(f).flatMap(g) ≡ m.flatMap(x => f(x).flatMap(g))`

These laws guarantee predictable composition and enable safe refactoring.

## Running

```bash
# Compile
sbt compile

# Run all tests (includes property-based monad law tests)
sbt test

# Run demo
sbt "runMain monad.Demo"
```

## Files

- `Model.scala` - Shared Bottle type
- `JavaStyle.scala` - Null-based (fixes slide 26 bug)
- `ExceptionStyle.scala` - Exception-based
- `MonadStyle.scala` - Monadic with lawful Pack/Crate
- `Demo.scala` - Side-by-side demonstration
- `EquivalenceTest.scala` - Tests proving identical results
- `MonadLawsTest.scala` - Property-based law verification

## Key Insight

By making domain types (Pack, Crate) proper monads, for-comprehensions work naturally and are guaranteed by the monad laws to compose predictably. The compiler checks types (Option[T] vs null) and the laws ensure refactorability.

## Comparison

| Aspect | Java | Exception | Monad |
|--------|------|-----------|-------|
| Missing data | `null` | throws | `Option[T]` |
| Nesting | Deep (5 levels) | Moderate | Flat |
| Type safety | No | No | Yes |
| Compiler help | No | No | Yes (types + laws) |
| Performance | Good | Poor | Good |
| Refactorable | No | Moderate | Yes (by laws) |
