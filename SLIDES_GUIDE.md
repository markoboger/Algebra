# Using the Monad Example in Slides

This document provides quick reference for incorporating the monad example into SE08 lecture slides.

## Quick Start

1. **Demo the three approaches live:**
   ```bash
   sbt "runMain monad.Demo"
   ```

2. **Show interactive exploration:**
   ```bash
   sbt console
   scala> :load src/main/scala/monad/worksheet.sc
   ```

3. **Run tests to prove equivalence:**
   ```bash
   sbt "testOnly monad.*"
   ```

## Slide-Ready Code Snippets

### Java Style (Slide: The Problem)

```scala
def countFullBottles(crate: Crate | Null): Int =
  var count = 0
  if crate != null then
    val packs = crate.packs  // Fixed: was crate2.packs!
    if packs != null then
      packs.foreach { pack =>
        if pack != null then
          val bottles = pack.bottles
          if bottles != null then
            bottles.foreach { bottle =>
              if bottle != null && !bottle.empty then
                count += 1
  count
```

**Teaching points:**
- Five levels of nesting
- Easy to make mistakes (like the crate2 bug)
- Difficult to read and maintain
- Performance is good but code quality is poor

### Exception Style (Slide: A Better Way?)

```scala
def countFullBottles(crate: Crate | Null): Int =
  var count = 0
  for pack <- crate.packs do
    try
      for bottle <- pack.bottles do
        try
          if bottle != null && !bottle.empty then
            count += 1
        catch case e: MissingDataException => ()
    catch case e: MissingDataException => ()
  count
```

**Teaching points:**
- Less nesting than null checks
- Still needs defensive coding
- Exceptions are expensive (stack traces, object creation)
- Exceptions should be for exceptional cases, not control flow

### Monad Style (Slide: The Functional Solution)

```scala
// Define monadic types with map/flatMap
case class MonadicPack[T](bottles: List[Option[T]]):
  def map[U](f: T => U): MonadicPack[U] = 
    MonadicPack(bottles.map(_.map(f)))
  
  def flatMap[U](f: T => MonadicPack[U]): MonadicPack[U] = 
    MonadicPack(bottles.flatMap {
      case Some(b) => f(b).bottles
      case None => List(None)
    })

// Use it with for-comprehensions
def countFullBottles(crate: MonadicCrate[Bottle]): Int =
  (for
    pack <- crate.presentPacks
    bottle <- pack.present
    if !bottle.empty
  yield bottle).length
```

**Teaching points:**
- Flat code, no nesting
- Type-safe: Option[T] vs null
- Composable: map/flatMap enable for-comprehensions
- Compiler-checked: can't forget to handle None

## Comparison Table for Slides

| Aspect | Java | Exception | Monad |
|--------|------|-----------|-------|
| Nesting | 🔴 Deep | 🟡 Medium | 🟢 Flat |
| Type Safe | 🔴 No | 🔴 No | 🟢 Yes |
| Performance | 🟢 Fast | 🔴 Slow | 🟢 Fast |
| Readable | 🔴 Poor | 🟡 OK | 🟢 Excellent |
| Bug-Prone | 🔴 Yes | 🟡 Maybe | 🟢 No |

## Key Teaching Moments

### 1. The Bug (Slide 26 Fixed)

**Original (buggy):**
```scala
if crate != null then
  val packs = crate2.packs  // Wrong variable!
```

**Fixed:**
```scala
if crate != null then
  val packs = crate.packs  // Correct
```

**Point:** Easy to make copy-paste errors with deeply nested code.

### 2. Why Map and FlatMap?

**map:** Transform contents without changing structure
```scala
pack.map(bottle => bottle.consume())
// List(Some(b1), None, Some(b2)) stays same shape
```

**flatMap:** Transform and flatten nested structures
```scala
crate.flatMap(pack => pack.bottles)
// Flattens crate → packs → bottles
```

**Point:** These enable for-comprehensions!

### 3. Monad Laws

For-comprehensions work because map/flatMap obey three laws:

1. **Left Identity:** `pure(a).flatMap(f) ≡ f(a)`
2. **Right Identity:** `m.flatMap(pure) ≡ m`
3. **Associativity:** Chaining flatMaps is associative

**Point:** Laws guarantee predictable, refactorable code.

### 4. For-Comprehension Desugaring

**What you write:**
```scala
for
  pack <- crate.presentPacks
  bottle <- pack.present
  if !bottle.empty
yield bottle
```

**What the compiler generates:**
```scala
crate.presentPacks
  .flatMap(pack =>
    pack.present
      .withFilter(bottle => !bottle.empty)
      .map(bottle => bottle)
  )
```

**Point:** For-comprehensions are just syntax sugar!

## Live Coding Suggestions

### Demo 1: Show the Bug
1. Show slide 26's original code with `crate2.packs`
2. Explain it compiles but will crash or give wrong results
3. Show the fixed version in JavaStyle.scala

### Demo 2: Build a Simple Monad
1. Start with `case class Box[T](value: T)`
2. Add `def map[U](f: T => U): Box[U] = Box(f(value))`
3. Show how this enables transformation
4. Add flatMap to handle Box[Box[T]]
5. Demo with for-comprehension

### Demo 3: Compare All Three
1. Run `sbt "runMain monad.Demo"`
2. Show identical outputs
3. Highlight code complexity differences
4. Run tests to prove equivalence

## Exercises for Students

1. **Add a new operation:** Implement `findFirstFullBottle` in all three styles
2. **Fix intentional bugs:** Introduce bugs and have students find them
3. **Extend the monad:** Add `filter` or `forEach` methods
4. **Test monad laws:** Write tests for left identity, right identity, associativity
5. **Real-world example:** Apply the pattern to database queries or API calls

## Common Questions

**Q: Why not just use Option everywhere?**
A: We do in MonadStyle! But to show the pattern, we implement map/flatMap on our domain types too.

**Q: Is this practical or just academic?**
A: Very practical! Scala, Haskell, Rust's Result type, Java's Optional, and many APIs use this pattern.

**Q: When should I use exceptions vs Option?**
A: Use Option for expected missing data. Use exceptions for truly unexpected errors.

**Q: Are monads hard?**
A: The name is scary, but the pattern is simple: types with map/flatMap that obey three laws.

## Files Reference

- `Model.scala` - Domain types (Bottle, Pack, Crate)
- `JavaStyle.scala` - Null-check implementation
- `ExceptionStyle.scala` - Exception-based implementation  
- `MonadStyle.scala` - Monadic implementation with laws
- `Demo.scala` - Side-by-side demonstration
- `worksheet.sc` - Interactive exploration
- `*Test.scala` - Comprehensive tests
- `README.md` - Full documentation

## Integration with Existing Slides

This example fits naturally after:
- Slide 22: Basic monad introduction
- Slide 23: Bottle/Pack/Crate model
- Slide 25: Good case with nested foreach
- Slide 26: Bad case with null checks (now fixed!)

Suggested new slide flow:
1. Show the problem (JavaStyle)
2. Show exception approach (better but still issues)
3. Introduce monads conceptually
4. Show MonadStyle implementation
5. Demonstrate equivalence with tests
6. Explain monad laws
7. Live coding demo
