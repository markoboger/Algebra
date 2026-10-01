package monad

/** Monad-based functional approach with lawful domain monads.
 *
 * Design: Crate is a monad over packs, Pack is a monad over bottles.
 * - Crate[T] holds packs, where each pack contains T values
 * - Pack[T] holds bottles of type T
 * - `for pack <- crate; bottle <- pack` calls Crate.flatMap and Pack.flatMap
 *
 * Key insight: We use nested monads. Crate is a monad of Packs,
 * and we flatMap over both levels.
 */
object MonadStyle:
  
  /** A pack is a monadic container of bottles.
   *
   * Pack[T] is a lawful monad.
   */
  case class Pack[T](bottles: List[T]):
    
    def map[U](f: T => U): Pack[U] =
      Pack(bottles.map(f))
    
    def flatMap[U](f: T => Pack[U]): Pack[U] =
      Pack(bottles.flatMap(b => f(b).bottles))
    
    def withFilter(p: T => Boolean): Pack[T] =
      Pack(bottles.filter(p))
    
    def foreach[U](f: T => U): Unit =
      bottles.foreach(f)
  
  object Pack:
    def pure[T](value: T): Pack[T] = Pack(List(value))
  
  /** A crate is a monadic container of packs.
   *
   * Crate is a monad over Pack[T]. When you iterate,
   * you get Pack[T] values (not T values directly).
   *
   * `for pack <- crate` calls Crate.foreach or Crate.flatMap.
   */
  case class Crate[T](packs: List[Pack[T]]):
    
    def map[U](f: T => U): Crate[U] =
      Crate(packs.map(_.map(f)))
    
    /** FlatMap for Crate.
     *
     * Takes a function that maps each bottle to a Pack,
     * applies it within each pack, and collects results.
     */
    def flatMap[U](f: T => Pack[U]): Crate[U] =
      Crate(packs.map(_.flatMap(f)))
    
    def withFilter(p: T => Boolean): Crate[T] =
      Crate(packs.map(_.withFilter(p)))
    
    /** Foreach over bottles (not packs).
     *
     * When you write `for bottle <- crate`, this is called.
     */
    def foreach[U](f: T => U): Unit =
      packs.foreach(_.foreach(f))
  
  object Crate:
    def pure[T](value: T): Crate[T] = Crate(List(Pack.pure(value)))
  
  /** Count full bottles using for-comprehension.
   *
   * When we write `for bottleOpt <- crate`, it desugars to:
   *   crate.foreach(bottleOpt => ...)
   *
   * This calls Crate.foreach on our domain type, which internally
   * iterates packs.
   */
  def countFullBottles(crate: Crate[Option[Bottle]]): Int =
    var count = 0
    for
      bottleOpt <- crate         // Crate.foreach -> bottleOpt: Option[Bottle]
      bottle <- bottleOpt        // Option.foreach -> bottle: Bottle  
      if bottle.full
    do
      count += 1
    count
  
  /** Consume all bottles. */
  def consumeAll(crate: Crate[Option[Bottle]]): List[Bottle] =
    val consumed = scala.collection.mutable.ListBuffer[Bottle]()
    for
      bottleOpt <- crate
      bottle <- bottleOpt
    do
      consumed += bottle.consume()
    consumed.toList
