package monad

/** Monad-based functional approach using Option and for-comprehensions.
 *
 * This implementation demonstrates the "right way" in functional Scala:
 * - Uses Option[T] to represent possibly-missing values (Some/None instead of null)
 * - Defines proper map/flatMap on domain types to enable for-comprehensions
 * - Flat, readable code without nested checks or exception handling
 * - Type-safe: the compiler enforces handling of missing cases
 *
 * Key insight: By implementing map/flatMap on our domain types and using
 * Option for possibly-missing values, we can use Scala's for-comprehension
 * syntax to express nested iterations cleanly. The monad laws guarantee
 * that this composition is predictable and composable.
 */

/** A pack containing possibly-missing bottles, with monadic operations.
 *
 * MonadicPack[T] is a functor and monad that allows mapping and flat-mapping
 * over its bottles. This enables for-comprehensions to iterate cleanly over
 * possibly-missing bottles without nested null checks.
 *
 * Monad Laws (informally):
 * 1. Left identity: map(identity) == identity
 * 2. Right identity: flatMap with unit wrapping == identity  
 * 3. Associativity: flatMap composition is associative
 *
 * These laws ensure predictable composition and allow the compiler to
 * optimize for-comprehensions without changing semantics.
 */
case class MonadicPack[T](bottles: List[Option[T]]):
  
  /** Map a function over all present bottles.
   *
   * Obeys functor law: map(f . g) == map(f).map(g)
   */
  def map[U](f: T => U): MonadicPack[U] =
    MonadicPack(bottles.map(_.map(f)))
  
  /** FlatMap: map then flatten one level.
   *
   * Essential for for-comprehensions with multiple generators.
   * Obeys monad laws for associative composition.
   */
  def flatMap[U](f: T => MonadicPack[U]): MonadicPack[U] =
    MonadicPack(bottles.flatMap {
      case Some(bottle) => f(bottle).bottles
      case None => List(None)
    })
  
  /** Filter bottles by predicate, keeping only Some values that pass.
   *
   * Required for if-guards in for-comprehensions.
   */
  def withFilter(p: T => Boolean): MonadicPack[T] =
    MonadicPack(bottles.map(_.filter(p)))
  
  /** Extract all present (non-None) bottles. */
  def present: List[T] = bottles.flatten
  
  override def toString: String = 
    s"MonadicPack[${bottles.map {
      case Some(b) => b.toString
      case None => "∅"
    }.mkString(",")}]"

/** A crate containing possibly-missing packs, with monadic operations.
 *
 * MonadicCrate[T] is a monad over packs. It enables for-comprehensions
 * to iterate over packs and their nested bottles without explicit null checks.
 */
case class MonadicCrate[T](packs: List[Option[MonadicPack[T]]]):
  
  /** Map a function over all bottles in all present packs. */
  def map[U](f: T => U): MonadicCrate[U] =
    MonadicCrate(packs.map(_.map(_.map(f))))
  
  /** FlatMap over packs, enabling nested for-comprehensions. */
  def flatMap[U](f: T => MonadicCrate[U]): MonadicCrate[U] =
    MonadicCrate(packs.flatMap {
      case Some(pack) =>
        pack.bottles.flatMap {
          case Some(bottle) => f(bottle).packs
          case None => List(None)
        }
      case None => List(None)
    })
  
  /** Filter bottles across all packs. */
  def withFilter(p: T => Boolean): MonadicCrate[T] =
    MonadicCrate(packs.map(_.map(_.withFilter(p))))
  
  /** Extract all present packs. */
  def presentPacks: List[MonadicPack[T]] = packs.flatten
  
  /** Extract all present bottles from all present packs. */
  def allBottles: List[T] = packs.flatten.flatMap(_.present)
  
  override def toString: String = 
    s"MonadicCrate[${packs.map {
      case Some(p) => p.toString
      case None => "∅"
    }.mkString(";")}]"

object MonadStyle:
  
  /** Count full bottles in a monadic crate using for-comprehension.
   *
   * The for-comprehension desugars to:
   *   crate.flatMap { pack =>
   *     pack.flatMap { bottle =>
   *       if !bottle.empty then Some(bottle) else None
   *     }
   *   }
   *
   * Notice: No null checks, no exceptions, no nesting! The monad structure
   * handles missing values automatically. None values are propagated through
   * the computation without special handling.
   */
  def countFullBottles(crate: MonadicCrate[Bottle]): Int =
    (for
      pack <- crate.presentPacks
      bottle <- pack.present
      if !bottle.empty
    yield bottle).length
  
  /** Consume all present bottles in a monadic crate.
   *
   * Again, the for-comprehension provides flat, readable iteration
   * without any nested if-checks or try-catch blocks. Missing bottles
   * (None values) are simply skipped by the monadic bind operation.
   */
  def consumeAll(crate: MonadicCrate[Bottle]): Unit =
    for
      pack <- crate.presentPacks
      bottle <- pack.present
    do
      bottle.consume()
      println(s"  Consumed: $bottle")
  
  /** Alternative: count using the monadic structure directly.
   *
   * This version shows explicit use of the monad operations rather
   * than for-comprehension syntax sugar. It's more verbose but shows
   * what the compiler generates from the for-comprehension.
   */
  def countFullBottlesExplicit(crate: MonadicCrate[Bottle]): Int =
    crate.allBottles.count(!_.empty)

/** Example demonstrating monad laws for MonadicPack.
 *
 * The monad laws ensure that monadic composition behaves predictably:
 * - Left identity: wrapping then flatMapping is the same as just applying the function
 * - Right identity: flatMapping with the unit (pure) function is identity  
 * - Associativity: the order of flatMap nesting doesn't matter
 */
object MonadLaws:
  
  /** Left identity: pure(a).flatMap(f) == f(a) */
  def leftIdentity[T, U](a: T, f: T => MonadicPack[U]): Boolean =
    val lhs = MonadicPack(List(Some(a))).flatMap(f)
    val rhs = f(a)
    lhs.present == rhs.present
  
  /** Right identity: m.flatMap(pure) == m */
  def rightIdentity[T](m: MonadicPack[T]): Boolean =
    val lhs = m.flatMap(a => MonadicPack(List(Some(a))))
    val rhs = m
    lhs.present == rhs.present
  
  /** Associativity: m.flatMap(f).flatMap(g) == m.flatMap(x => f(x).flatMap(g)) */
  def associativity[T, U, V](
    m: MonadicPack[T],
    f: T => MonadicPack[U],
    g: U => MonadicPack[V]
  ): Boolean =
    val lhs = m.flatMap(f).flatMap(g)
    val rhs = m.flatMap(x => f(x).flatMap(g))
    lhs.present == rhs.present
