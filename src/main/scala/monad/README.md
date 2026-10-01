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
case class Pack[T](bottles: List[T]):
  def map[U](f: T => U): Pack[U] = Pack(bottles.map(f))
  def flatMap[U](f: T => Pack[U]): Pack[U] = 
    Pack(bottles.flatMap(b => f(b).bottles))
  def withFilter(p: T => Boolean): Pack[T] = Pack(bottles.filter(p))
  def foreach[U](f: T => U): Unit = bottles.foreach(f)

case class Crate[T](packs: List[Pack[T]]):
  def map[U](f: T => U): Crate[U] = Crate(packs.map(_.map(f)))
  def flatMap[U](f: T => Pack[U]): Crate[U] = 
    Crate(packs.map(_.flatMap(f)))
  def withFilter(p: T => Boolean): Crate[T] = 
    Crate(packs.map(_.withFilter(p)))
  def foreach[U](f: T => U): Unit = packs.foreach(_.foreach(f))

def countFullBottles(crate: Crate[Option[Bottle]]): Int =
  var count = 0
  for
    bottleOpt <- crate         // Crate.foreach -> bottleOpt: Option[Bottle]
    bottle <- bottleOpt        // Option.foreach -> bottle: Bottle
    if bottle.full
  do
    count += 1
  count
```

**Characteristics:**
- Pack[T] and Crate[T] are lawful monads (verified by tests)
- For-comprehensions call **Crate.foreach** on our domain type
- Option[T] for possibly-missing elements (type-safe)
- Flat code, no nesting
- Compiler-checked and refactorable (monad laws guarantee composition)

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
