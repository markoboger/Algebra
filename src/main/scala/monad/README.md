# Monad Teaching Example

This package provides a pedagogical comparison of three approaches to handling missing data in Scala, designed for teaching functional programming concepts.

## Overview

The example uses a simple domain: bottles, packs (groups of bottles), and crates (groups of packs). Some bottles or packs may be missing, and we need to count full bottles or consume them all.

## Three Implementations

### 1. Java Style (`JavaStyle.scala`)

**Imperative approach with null checks**

- Uses `null` to represent missing data
- Requires deeply nested `if != null` checks
- Prone to bugs (e.g., the original slide had `crate2.packs` instead of `crate.packs`)
- Verbose and hard to maintain

```scala
if crate != null then
  if packs != null then
    packs.foreach { pack =>
      if pack != null then
        // ...deeply nested...
```

### 2. Exception Style (`ExceptionStyle.scala`)

**Exception-based error handling**

- Throws exceptions for missing data
- Uses try-catch blocks to handle errors
- Less nested than null checks
- But: exceptions are expensive and semantically wrong for "expected" missing data

```scala
try
  if bottle == null then
    throw MissingDataException("Bottle is null")
  // process bottle
catch
  case e: MissingDataException =>
    // handle error
```

### 3. Monad Style (`MonadStyle.scala`)

**Functional approach with monads**

- Uses `Option[T]` (Some/None) instead of null
- Implements map/flatMap on domain types
- Enables flat, readable for-comprehensions
- Type-safe: compiler enforces handling missing cases
- Composable: monad laws guarantee predictable composition

```scala
for
  pack <- crate.presentPacks
  bottle <- pack.present
  if !bottle.empty
yield bottle
```

## Monad Laws

The implementation includes documentation and tests for the three monad laws:

1. **Left Identity**: `pure(a).flatMap(f) ≡ f(a)`
2. **Right Identity**: `m.flatMap(pure) ≡ m`
3. **Associativity**: `m.flatMap(f).flatMap(g) ≡ m.flatMap(x => f(x).flatMap(g))`

These laws ensure that monadic composition is predictable and refactorable.

## Files

- `Model.scala` - Shared domain model (Bottle, Pack, Crate)
- `JavaStyle.scala` - Java-style null checks (fixes the bug from slide 26)
- `ExceptionStyle.scala` - Exception-based error handling
- `MonadStyle.scala` - Monadic approach with Option and for-comprehensions
- `Demo.scala` - Runnable demonstration of all three approaches
- `worksheet.sc` - Interactive Scala worksheet
- `MonadComparisonTest.scala` - Tests verifying identical behavior
- `MonadLawsTest.scala` - Tests verifying monad laws

## Running

```bash
# Compile
sbt compile

# Run tests
sbt test

# Run demo
sbt "runMain monad.Demo"

# Interactive exploration
sbt console
scala> :load src/main/scala/monad/worksheet.sc
```

## Key Insights

1. **Null checks scale poorly**: Nesting depth matches data structure depth
2. **Exceptions are for exceptional cases**: Not for normal control flow
3. **Monads enable composition**: flatMap allows chaining without nesting
4. **Type safety matters**: Option[T] makes missing data explicit in types
5. **For-comprehensions are sugar**: They desugar to map/flatMap/withFilter

## Pedagogical Notes

This example is designed for slides and teaching:

- All three approaches handle the same test cases fairly
- Each implementation is compact and slide-friendly
- Comments explain the trade-offs and issues
- Tests demonstrate equivalence and correctness
- Monad laws are checked to build theoretical understanding

The monad style is not "magic" - it's a design pattern that enables composition through well-defined laws. By implementing map/flatMap on domain types and using Option for missing values, we get flat, readable code that the compiler can verify.

## Comparison Summary

| Aspect | Java Style | Exception Style | Monad Style |
|--------|-----------|----------------|-------------|
| Nesting | Deep (pyramid of doom) | Moderate | Flat |
| Type Safety | No (null everywhere) | No | Yes (Option[T]) |
| Performance | Good | Poor (exceptions) | Good |
| Readability | Poor (nested ifs) | Moderate | Excellent |
| Composition | Manual | Manual | Automatic |
| Error Handling | Defensive checking | Try-catch | Type-driven |

## Connection to Slides

This example builds on the incomplete code from lecture slides 23-27:

- **Slide 23**: Basic Bottle/Pack/Crate with half-baked map/flatMap
- **Slide 25**: Good case with nested foreach
- **Slide 26**: Bad case with five nested null checks and a **bug** (uses `crate2.packs` instead of `crate.packs`)
- **Slide 27**: Attempt at generic PackT[T]/CrateT[T] with home-made Option

This implementation:

- ✅ Fixes the bug from slide 26
- ✅ Properly develops the monad structure
- ✅ Uses standard Option correctly (or explains a teaching Option without shadowing)
- ✅ Implements complete map/flatMap/withFilter
- ✅ Provides fair comparisons of all three approaches
- ✅ Includes runnable code and tests
- ✅ Documents monad laws with examples
