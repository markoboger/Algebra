package monad

/** Monad-style: Pack[A] and Crate[A] are genuine lawful container monads.
 *
 * Design note: Both Pack and Crate are independent sequence-like containers,
 * each with lawful flatMap and unit. When nesting (Crate[Pack[Bottle]]),
 * we use .toCrate to convert a Pack into a Crate, enabling the for-comprehension
 * to call Crate.flatMap throughout. This avoids the pitfall of re-chunking flatMap
 * that breaks right identity.
 */

/** Pack[A]: a lawful monad container (sequence of items).
 *
 * Monad laws under structural equality (verified by ScalaCheck):
 * 1. Left identity: Pack.pure(a).flatMap(f) == f(a)
 * 2. Right identity: m.flatMap(Pack.pure) == m
 * 3. Associativity: m.flatMap(f).flatMap(g) == m.flatMap(x => f(x).flatMap(g))
 */
case class Pack[A](items: List[A]):
  
  def map[B](f: A => B): Pack[B] =
    Pack(items.map(f))
  
  def flatMap[B](f: A => Pack[B]): Pack[B] =
    Pack(items.flatMap(a => f(a).items))
  
  def withFilter(p: A => Boolean): Pack[A] =
    Pack(items.filter(p))
  
  def isEmpty: Boolean = items.isEmpty
  
  /** Convert this Pack's items into a Crate. */
  def toCrate: Crate[A] = Crate(items)

object Pack:
  def pure[A](a: A): Pack[A] = Pack(List(a))
  def empty[A]: Pack[A] = Pack(List.empty)

/** Crate[A]: a lawful monad container (sequence of items, conceptually).
 *
 * Monad laws under structural equality (verified by ScalaCheck):
 * 1. Left identity: Crate.pure(a).flatMap(f) == f(a)
 * 2. Right identity: m.flatMap(Crate.pure) == m
 * 3. Associativity: m.flatMap(f).flatMap(g) == m.flatMap(x => f(x).flatMap(g))
 *
 * Internally represented as List[A] to ensure lawfulness; not List[Pack[A]]
 * to avoid re-chunking issues that break right identity.
 */
case class Crate[A](items: List[A]):
  
  def map[B](f: A => B): Crate[B] =
    Crate(items.map(f))
  
  def flatMap[B](f: A => Crate[B]): Crate[B] =
    Crate(items.flatMap(a => f(a).items))
  
  def withFilter(p: A => Boolean): Crate[A] =
    Crate(items.filter(p))
  
  def isEmpty: Boolean = items.isEmpty

object Crate:
  def pure[A](a: A): Crate[A] = Crate(List(a))
  def empty[A]: Crate[A] = Crate(List.empty)
  
  /** Convert Option to Crate (Some → pure, None → empty). */
  def fromOption[A](opt: Option[A]): Crate[A] = opt match
    case Some(a) => pure(a)
    case None => empty

/** Monad-style bottle with Option. */
case class MonadBottle(content: Option[Drink])

object MonadStyle:
  
  /** Aggregate: total volume using genuine monad flatMap over Crate.
   *
   * Demonstrates for-comprehension calling Crate.flatMap throughout:
   *   for pack <- crate                    desugars to Crate.flatMap
   *       bottle <- pack.toCrate           desugars to Crate.flatMap
   *       drink <- Crate.fromOption(...)   desugars to Crate.map (last generator)
   *   yield drink.volumeMl
   */
  def totalVolume(crate: Crate[Pack[MonadBottle]]): Int =
    val result = for
      pack <- crate                         // Crate[Pack[MonadBottle]].flatMap
      bottle <- pack.toCrate                // Crate[MonadBottle].flatMap (via Pack.toCrate)
      drink <- Crate.fromOption(bottle.content)  // Crate[Drink].map (map because last generator)
    yield drink.volumeMl
    
    result.items.sum
