# Monad Teaching Example

Three approaches to navigating nested, possibly-missing data, demonstrating the progression from imperative to functional style.

## Part A: Navigation Scenario

**Task**: Given a crate, a pack index, and a bottle index, get the drink's name.
**Challenge**: Each step can fail (pack missing, bottle missing, bottle empty).

### 1. Java Style

Null-based accessors, deep nesting of checks.

```scala
def getDrinkName(crate: JavaCrate | Null, packIdx: Int, bottleIdx: Int): String | Null =
  if crate != null then
    val pack = crate.pack(packIdx)
    if pack != null then
      val bottle = pack.bottle(bottleIdx)
      if bottle != null then
        if bottle.content.isDefined then
          bottle.content.get.name
        else null
      else null
    else null
  else null
```

### 2. Exception Style

Domain exceptions, single try-catch.

```scala
def getDrinkName(crate: ExceptionCrate, packIdx: Int, bottleIdx: Int): String | Null =
  try
    val pack = crate.pack(packIdx)          // throws NoSuchPackException
    val bottle = pack.bottle(bottleIdx)    // throws NoSuchBottleException
    bottle.content.map(_.name)
      .getOrElse(throw EmptyBottleException())
  catch
    case _: NoSuchPackException | _: NoSuchBottleException | _: EmptyBottleException => null
```

### 3. Monad Style

Option-based accessors, single for-comprehension.

```scala
def getDrinkName(crate: MonadicCrate, packIdx: Int, bottleIdx: Int): Option[String] =
  for
    pack <- crate.pack(packIdx)       // Option.flatMap
    bottle <- pack.bottle(bottleIdx)  // Option.flatMap
    drink <- bottle.content           // Option.map
  yield drink.name
```

**Desugars to:**
```scala
crate.pack(packIdx).flatMap { pack =>
  pack.bottle(bottleIdx).flatMap { bottle =>
    bottle.content.map { drink =>
      drink.name
    }
  }
}
```

This genuinely calls `Option.flatMap` and `Option.map`.

## Part B: Domain Monads

**Pack[T]** is a lawful monad with proper flatMap.

```scala
case class Pack[T](items: List[T]):
  def flatMap[U](f: T => Pack[U]): Pack[U] = 
    Pack(items.flatMap(t => f(t).items))
```

**For-comprehension:**
```scala
for
  x <- pack              // Pack.flatMap
  y <- f(x)             // returns Pack[U]
yield y
```

Calls `Pack.flatMap` on our domain type.

**Crate[T]** is a functor (not a monad) with map and flatten operations. A monad would require `flatMap[U](f: T => Crate[U]): Crate[U]`, but our domain doesn't support that semantics cleanly.

## Part C: Teaching Maybe

A minimal monad (Just/Empty) that doesn't shadow stdlib.

```scala
sealed trait Maybe[+T]:
  def flatMap[U](f: T => Maybe[U]): Maybe[U]
  def map[U](f: T => U): Maybe[U]

case class Just[T](value: T) extends Maybe[T]
case object Empty extends Maybe[Nothing]
```

With `toMaybe` extension, navigation reads cleanly:
```scala
for
  crate <- crateOpt.toMaybe
  pack <- crate.pack(i).toMaybe
  bottle <- pack.bottle(j).toMaybe
  drink <- bottle.content.toMaybe
yield drink.name
```

Demonstrates that Option itself is just a monad.

## Monad Laws

All tested with ScalaCheck property-based tests:

1. **Left identity**: `pure(a).flatMap(f) ≡ f(a)`
2. **Right identity**: `m.flatMap(pure) ≡ m`
3. **Associativity**: `m.flatMap(f).flatMap(g) ≡ m.flatMap(x => f(x).flatMap(g))`

Laws verified for:
- **Pack[T]**: Full monad (5 monad + 2 functor laws)
- **Crate[T]**: Functor only (2 functor laws)
- **Maybe[T]**: Full monad (5 monad + 2 functor laws)

## Testing

```bash
sbt test  # All tests pass
```

- **5 equivalence tests**: All three navigation styles produce identical results
- **12 property-based law tests**: Pack (monad), Crate (functor), Maybe (monad) laws

## Comparison

| Aspect | Java | Exception | Monad |
|--------|------|-----------|-------|
| **Nesting** | Deep (5 levels) | Single try | Flat |
| **Composition** | Manual | Manual | Automatic (flatMap) |
| **Type safety** | No (null) | No | Yes (Option) |
| **Performance** | Good | Poor | Good |

## Key Insights

1. **Option.flatMap is the real monad**: The navigation for-comprehension desugars to `Option.flatMap`, demonstrating monadic composition.

2. **Domain monads are separate**: Pack[T] is a lawful monad. Crate[T] is a functor (not a full monad—that would require `flatMap[U](f: T => Crate[U]): Crate[U]`, which doesn't fit our domain cleanly).

3. **Laws enable reasoning**: Monad laws guarantee that `m.flatMap(f).flatMap(g)` can be refactored to `m.flatMap(x => f(x).flatMap(g))` without changing behavior.

4. **Scaling**: The aggregate operation (total volume) shows how monadic style scales: `packs.flatMap(_.bottles).flatMap(_.content).map(_.volumeMl).sum`.
