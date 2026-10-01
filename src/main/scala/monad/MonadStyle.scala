package monad

/** Monad-style: accessors return Option, composition via for-comprehension.
 *
 * Part A: Navigation using Option.flatMap/map
 * Part B: Custom domain monads Pack[T] and Crate[T]
 * Part C: Teaching Maybe[T] monad
 */

// ===== Part A: Navigation with Option =====

class MonadicCrate(packs: Array[MonadicPack]):
  def pack(index: Int): Option[MonadicPack] =
    if index >= 0 && index < packs.length then Some(packs(index)) else None
  
  def allPacks: List[MonadicPack] = packs.toList

class MonadicPack(bottles: Array[Bottle]):
  def bottle(index: Int): Option[Bottle] =
    if index >= 0 && index < bottles.length then Some(bottles(index)) else None
  
  def allBottles: List[Bottle] = bottles.toList

object MonadStyle:
  
  /** Navigate: crate → pack(i) → bottle(j) → drink.name
   *
   * Single for-comprehension desugars to Option.flatMap/map:
   *   crate.pack(packIdx).flatMap { pack =>
   *     pack.bottle(bottleIdx).flatMap { bottle =>
   *       bottle.content.map { drink =>
   *         drink.name
   *       }
   *     }
   *   }
   *
   * This calls Option.flatMap and Option.map, demonstrating monadic composition.
   */
  def getDrinkName(crate: MonadicCrate, packIdx: Int, bottleIdx: Int): Option[String] =
    for
      pack <- crate.pack(packIdx)       // Option.flatMap
      bottle <- pack.bottle(bottleIdx)  // Option.flatMap
      drink <- bottle.content           // Option.map
    yield drink.name
  
  /** Aggregate: total volume using flatMap over collections and Options.
   *
   * Scales naturally: flatMap over packs, flatMap over bottles, flatMap over content.
   */
  def totalVolume(crate: MonadicCrate): Int =
    crate.allPacks
      .flatMap(_.allBottles)           // List.flatMap
      .flatMap(_.content)              // List.flatMap over Option content
      .map(_.volumeMl)                 // List.map
      .sum

// ===== Part B: Custom Domain Monads =====

/** Pack[T] is a lawful monad (container of items).
 *
 * Monad laws verified by tests with structural equality:
 * 1. Left identity: Pack.pure(a).flatMap(f) ≡ f(a)
 * 2. Right identity: m.flatMap(Pack.pure) ≡ m
 * 3. Associativity: m.flatMap(f).flatMap(g) ≡ m.flatMap(x => f(x).flatMap(g))
 */
case class Pack[T](items: List[T]):
  
  def map[U](f: T => U): Pack[U] =
    Pack(items.map(f))
  
  /** FlatMap: for x <- pack; y <- f(x) yield ...
   *
   * Stays within Pack: f returns Pack[U], result is Pack[U].
   */
  def flatMap[U](f: T => Pack[U]): Pack[U] =
    Pack(items.flatMap(t => f(t).items))
  
  def withFilter(p: T => Boolean): Pack[T] =
    Pack(items.filter(p))
  
  def foreach[U](f: T => U): Unit =
    items.foreach(f)

object Pack:
  def pure[T](value: T): Pack[T] = Pack(List(value))

/** Crate[T] is a functor (not a full monad).
 *
 * It has map but not a lawful monad flatMap. A monad would require
 * flatMap[U](f: T => Crate[U]): Crate[U], but our domain doesn't
 * support that semantics cleanly.
 *
 * Instead, we provide:
 * - map: transform all items
 * - flatten: extract all items into a single Pack
 * - bottles: extract all items as a List
 */
case class Crate[T](packs: List[Pack[T]]):
  
  def map[U](f: T => U): Crate[U] =
    Crate(packs.map(_.map(f)))
  
  /** Extract all items from all packs as a List. */
  def bottles: List[T] =
    packs.flatMap(_.items)
  
  /** Extract all items from all packs as a single Pack. */
  def flatten: Pack[T] =
    Pack(packs.flatMap(_.items))

object Crate:
  /** Lift a pack into a crate. */
  def apply[T](pack: Pack[T]): Crate[T] = Crate(List(pack))

// ===== Part C: Teaching Maybe Monad =====

/** Maybe[T]: a teaching monad (doesn't shadow stdlib).
 *
 * Laws identical to Option, verified by tests.
 */
sealed trait Maybe[+T]:
  def map[U](f: T => U): Maybe[U] = this match
    case Just(value) => Just(f(value))
    case Empty => Empty
  
  def flatMap[U](f: T => Maybe[U]): Maybe[U] = this match
    case Just(value) => f(value)
    case Empty => Empty
  
  def withFilter(p: T => Boolean): Maybe[T] = this match
    case Just(value) if p(value) => this
    case _ => Empty

case class Just[T](value: T) extends Maybe[T]
case object Empty extends Maybe[Nothing]

object Maybe:
  def pure[T](value: T): Maybe[T] = Just(value)
  
  /** Convert Option to Maybe. */
  extension [T](opt: Option[T])
    def toMaybe: Maybe[T] = opt match
      case Some(value) => Just(value)
      case None => Empty
  
  /** Demonstrate: navigation using Maybe (mirror of Option version).
   *
   * Desugars to:
   *   crateOpt.toMaybe.flatMap { crate =>
   *     crate.pack(packIdx).toMaybe.flatMap { pack =>
   *       pack.bottle(bottleIdx).toMaybe.flatMap { bottle =>
   *         bottle.content.toMaybe.map(_.name)
   *       }
   *     }
   *   }
   */
  def navigateWithMaybe(
    crateOpt: Option[MonadicCrate],
    packIdx: Int,
    bottleIdx: Int
  ): Maybe[String] =
    for
      crate <- crateOpt.toMaybe           // Maybe.flatMap
      pack <- crate.pack(packIdx).toMaybe  // Maybe.flatMap
      bottle <- pack.bottle(bottleIdx).toMaybe // Maybe.flatMap
      drink <- bottle.content.toMaybe     // Maybe.map
    yield drink.name
