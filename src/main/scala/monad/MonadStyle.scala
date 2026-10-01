package monad

/** Monad-based functional approach with lawful monads.
 *
 * Design: Pack and Crate are List-like containers (lawful monads).
 * Missing elements are represented by Option at the leaves.
 * For-comprehensions desugar to the domain types' map/flatMap/withFilter.
 *
 * Key insight: By making our domain types proper monads, we can compose
 * them naturally using for-comprehensions, and the monad laws guarantee
 * predictable, refactorable code.
 */
object MonadStyle:
  
  /** A pack is a container of bottles (possibly missing, represented as Option).
   *
   * Pack[T] is a lawful monad (functor + flatMap + unit/pure).
   * It behaves like List but emphasizes that it's a domain container.
   *
   * Monad laws:
   * 1. Left identity: Pack.pure(a).flatMap(f) ≡ f(a)
   * 2. Right identity: m.flatMap(Pack.pure) ≡ m
   * 3. Associativity: m.flatMap(f).flatMap(g) ≡ m.flatMap(x => f(x).flatMap(g))
   */
  case class Pack[T](bottles: List[T]):
    
    /** Map over all bottles. Functor law: map(f . g) = map(f).map(g) */
    def map[U](f: T => U): Pack[U] =
      Pack(bottles.map(f))
    
    /** FlatMap: map then flatten. Essential for for-comprehensions. */
    def flatMap[U](f: T => Pack[U]): Pack[U] =
      Pack(bottles.flatMap(b => f(b).bottles))
    
    /** Filter bottles by predicate. Needed for if-guards in for. */
    def withFilter(p: T => Boolean): Pack[T] =
      Pack(bottles.filter(p))
    
    /** For pattern matching in for-comprehensions. */
    def foreach[U](f: T => U): Unit =
      bottles.foreach(f)
  
  object Pack:
    /** Monad unit/pure: wrap a single value. */
    def pure[T](value: T): Pack[T] = Pack(List(value))
  
  /** A crate is a container of packs (possibly missing, represented as Option).
   *
   * Crate[T] is a lawful monad that flattens over its packs.
   * For-comprehensions over crates naturally iterate over all bottles.
   */
  case class Crate[T](packs: List[Pack[T]]):
    
    /** Map over all bottles in all packs. */
    def map[U](f: T => U): Crate[U] =
      Crate(packs.map(_.map(f)))
    
    /** FlatMap: map then flatten one level of pack structure. */
    def flatMap[U](f: T => Pack[U]): Crate[U] =
      Crate(packs.map(pack => pack.flatMap(f)))
    
    /** Filter bottles across all packs. */
    def withFilter(p: T => Boolean): Crate[T] =
      Crate(packs.map(_.withFilter(p)))
    
    /** For pattern matching in for-comprehensions. */
    def foreach[U](f: T => U): Unit =
      packs.foreach(_.foreach(f))
  
  object Crate:
    /** Monad unit/pure: wrap a single value in a crate. */
    def pure[T](value: T): Crate[T] = Crate(List(Pack.pure(value)))
  
  /** Count full bottles using for-comprehension.
   *
   * The for-comprehension desugars to:
   *   crate.flatMap { pack =>
   *     pack.flatMap { bottleOpt =>
   *       bottleOpt.withFilter(_.isDefined)
   *                .map(_.get)
   *                .withFilter(_.full)
   *                .map(b => Pack.pure(b))
   *     }
   *   }
   *
   * This uses Crate.flatMap, Pack.flatMap, Option.map, etc.
   */
  def countFullBottles(crate: Crate[Option[Bottle]]): Int =
    val result = for
      pack <- crate.packs           // iterate packs: List
      bottleOpt <- pack.bottles     // iterate bottles: List[Option[Bottle]]
      bottle <- bottleOpt           // unwrap Option: Option.flatMap
      if bottle.full                // filter: withFilter
    yield bottle
    
    result.length
  
  /** Consume all bottles using for-comprehension.
   *
   * Again, the for desugars to the monadic operations on our types.
   */
  def consumeAll(crate: Crate[Option[Bottle]]): List[Bottle] =
    val consumed = for
      pack <- crate.packs
      bottleOpt <- pack.bottles
      bottle <- bottleOpt
    yield bottle.consume()
    
    consumed
