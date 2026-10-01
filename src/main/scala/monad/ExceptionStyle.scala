package monad

/** Exception-based error handling approach.
 *
 * In this style, domain accessors throw exceptions when data is missing,
 * and client code uses try-catch to handle errors. This is how an experienced
 * Java developer would structure exception-based error handling.
 */

class MissingBottleException extends Exception("Bottle is missing")
class MissingPackException extends Exception("Pack is missing")

/** A pack where accessing bottles may throw. */
case class ExceptionPack(private val bottles: List[Bottle | Null]):
  def getBottles: List[Bottle] =
    if bottles == null then throw MissingBottleException()
    // Filter out nulls and return valid bottles
    bottles.filter(_ != null).asInstanceOf[List[Bottle]]

/** A crate where accessing packs may throw. */
case class ExceptionCrate(private val packs: List[ExceptionPack | Null]):
  def getPacks: List[ExceptionPack] =
    if packs == null then throw MissingPackException()
    // Filter out nulls and return valid packs
    packs.filter(_ != null).asInstanceOf[List[ExceptionPack]]

object ExceptionStyle:
  
  /** Count full bottles, catching exceptions when data is missing. */
  def countFullBottles(crate: ExceptionCrate): Int =
    var count = 0
    try
      val packs = crate.getPacks
      for pack <- packs do
        try
          val bottles = pack.getBottles
          for bottle <- bottles do
            if bottle.full then count += 1
        catch
          case _: MissingBottleException => 
            // Skip bottles in this pack that are missing
            ()
    catch
      case _: MissingPackException => 
        // Skip packs that are missing
        ()
    
    count
  
  /** Consume all accessible bottles. */
  def consumeAll(crate: ExceptionCrate): List[Bottle] =
    val consumed = scala.collection.mutable.ListBuffer[Bottle]()
    try
      val packs = crate.getPacks
      for pack <- packs do
        try
          val bottles = pack.getBottles
          for bottle <- bottles do
            consumed += bottle.consume()
        catch
          case _: MissingBottleException => 
            // Skip bottles in this pack that are missing
            ()
    catch
      case _: MissingPackException => 
        // Skip packs that are missing
        ()
    
    consumed.toList

