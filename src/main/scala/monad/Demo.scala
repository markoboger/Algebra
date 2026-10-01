package monad

/** Side-by-side demonstration of three approaches. */
object Demo:
  
  def main(args: Array[String]): Unit =
    println("=" * 70)
    println("Monad Teaching Example: Three Approaches")
    println("=" * 70)
    
    testNavigation()
    testAggregate()
    demonstrateDomainMonads()
    
    println("\n" + "=" * 70)
    println("Summary:")
    println("  Java:      Deep nesting, null checks at every level")
    println("  Exception: Try-catch, model throws at boundaries")
    println("  Monad:     For-comprehension, Pack.flatMap / Crate.flatMap")
    println("=" * 70)
  
  def testNavigation(): Unit =
    println("\n--- Navigation: crate → pack(0) → bottle(1) → drink.name ---")
    
    val beer = Drink("Beer", 500)
    val wine = Drink("Wine", 750)
    
    // Java style
    val javaPack = JavaPack(Array(Bottle(Some(beer)), Bottle(Some(wine)), null))
    val javaCrate = JavaCrate(Array(javaPack, null))
    val javaResult = JavaStyle.getDrinkName(javaCrate, 0, 1)
    
    // Exception style
    val exPack = ExceptionPack(Array(Bottle(Some(beer)), Bottle(Some(wine))))
    val exCrate = ExceptionCrate(Array(exPack))
    val exResult = ExceptionStyle.getDrinkName(exCrate, 0, 1)
    
    // Monad style
    val monadPacks = Array(
      Pack(List(Bottle(Some(beer)), Bottle(Some(wine)))),
      Pack(List.empty)
    )
    val monadCrate = MonadicCrateOfPacks(monadPacks)
    val monadResult = MonadStyle.getDrinkName(monadCrate, 0, 1)
    
    println(s"  Java Style:      $javaResult")
    println(s"  Exception Style: $exResult")
    println(s"  Monad Style:     $monadResult")
  
  def testAggregate(): Unit =
    println("\n--- Aggregate: Total Volume of All Full Bottles ---")
    
    val beer = Drink("Beer", 500)
    val wine = Drink("Wine", 750)
    val water = Drink("Water", 330)
    
    // Java style
    val javaPack1 = JavaPack(Array(Bottle(Some(beer)), Bottle(Some(wine))))
    val javaPack2 = JavaPack(Array(Bottle(None), Bottle(Some(water))))
    val javaCrate = JavaCrate(Array(javaPack1, javaPack2))
    val javaTotal = JavaStyle.totalVolume(javaCrate)
    
    // Exception style
    val exPack1 = ExceptionPack(Array(Bottle(Some(beer)), Bottle(Some(wine))))
    val exPack2 = ExceptionPack(Array(Bottle(None), Bottle(Some(water))))
    val exCrate = ExceptionCrate(Array(exPack1, exPack2))
    val exTotal = ExceptionStyle.totalVolume(exCrate)
    
    // Monad style
    val monadPacks = Array(
      Pack(List(Bottle(Some(beer)), Bottle(Some(wine)))),
      Pack(List(Bottle(None), Bottle(Some(water))))
    )
    val monadCrate = MonadicCrateOfPacks(monadPacks)
    val monadTotal = MonadStyle.totalVolume(monadCrate)
    
    println(s"  Java Style:      ${javaTotal}ml")
    println(s"  Exception Style: ${exTotal}ml")
    println(s"  Monad Style:     ${monadTotal}ml")
  
  def demonstrateDomainMonads(): Unit =
    println("\n--- Domain Monads: Pack[T] and Crate[T] ---")
    
    // Pack monad
    val pack = Pack(List(1, 2, 3))
    val doubled = pack.map(_ * 2)
    println(s"  Pack.map: $pack -> $doubled")
    
    val expanded = pack.flatMap(x => Pack(List(x, x * 10)))
    println(s"  Pack.flatMap: $pack -> $expanded")
    
    // Crate monad
    val crate = Crate(List(1, 2, 3))
    val crateDoubled = crate.map(_ * 2)
    println(s"  Crate.map: $crate -> $crateDoubled")
    
    val crateExpanded = crate.flatMap(x => Crate(List(x, x * 10)))
    println(s"  Crate.flatMap: $crate -> $crateExpanded")
    
    // For-comprehension calling Pack.flatMap
    val packResult = for
      x <- pack                        // Pack.flatMap
      y <- Pack(List(x, x + 1))       // Pack.map
    yield y * 2
    println(s"  Pack for-comprehension: $packResult")
    
    // For-comprehension calling Crate.flatMap (nested scenario)
    val bottlePack = Pack(List(Bottle(Some(Drink("Beer", 500))), Bottle(None)))
    val crateOfPacks = Crate(List(bottlePack, Pack(List(Bottle(Some(Drink("Wine", 750)))))))
    
    val volumes = for
      pack <- crateOfPacks            // Crate.flatMap
      bottle <- pack.toCrate          // Crate.flatMap (Pack converted to Crate)
      drink <- bottle.content match
        case Some(d) => Crate.pure(d)
        case None => Crate.empty
    yield drink.volumeMl
    
    println(s"  Crate for-comprehension: volumes = ${volumes.items}")
