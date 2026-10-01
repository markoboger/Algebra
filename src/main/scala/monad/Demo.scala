package monad

/** Side-by-side demonstration of three approaches to totalVolume. */
object Demo:
  
  def main(args: Array[String]): Unit =
    println("=" * 70)
    println("Monad Teaching Example: totalVolume Across Three Approaches")
    println("=" * 70)
    
    testAggregate()
    demonstrateDomainMonads()
    
    println("\n" + "=" * 70)
    println("Summary:")
    println("  Java:      Nested for-loops, null checks")
    println("  Exception: For-loops, model throws / client catches")
    println("  Monad:     For-comprehension, Crate.flatMap with Option")
    println("=" * 70)
  
  def testAggregate(): Unit =
    println("\n--- Aggregate: Total Volume of All Full Bottles ---")
    
    val beer = Drink("Beer", 500)
    val wine = Drink("Wine", 750)
    val water = Drink("Water", 330)
    
    // Java style: null for empty bottles
    val javaPack1 = JavaPack(List(JavaBottle(beer), JavaBottle(wine)))
    val javaPack2 = JavaPack(List(JavaBottle(null), JavaBottle(water)))
    val javaCrate = JavaCrate(List(javaPack1, javaPack2))
    val javaTotal = JavaStyle.totalVolume(javaCrate)
    
    // Exception style: throws for empty bottles
    val exPack1 = ExceptionPack(List(ExceptionBottle(Some(beer)), ExceptionBottle(Some(wine))))
    val exPack2 = ExceptionPack(List(ExceptionBottle(None), ExceptionBottle(Some(water))))
    val exCrate = ExceptionCrate(List(exPack1, exPack2))
    val exTotal = ExceptionStyle.totalVolume(exCrate)
    
    // Monad style: Option for empty bottles
    val monadPack1 = Pack(List(MonadBottle(Some(beer)), MonadBottle(Some(wine))))
    val monadPack2 = Pack(List(MonadBottle(None), MonadBottle(Some(water))))
    val monadCrate = Crate(List(monadPack1, monadPack2))
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
    val beer = Drink("Beer", 500)
    val wine = Drink("Wine", 750)
    val bottlePack = Pack(List(MonadBottle(Some(beer)), MonadBottle(None)))
    val crateOfPacks = Crate(List(bottlePack, Pack(List(MonadBottle(Some(wine))))))
    
    val volumes = for
      pack <- crateOfPacks                      // Crate.flatMap
      bottle <- pack.toCrate                    // Crate.flatMap
      drink <- Crate.fromOption(bottle.content) // Crate.map
    yield drink.volumeMl
    
    println(s"  Crate for-comprehension: volumes = ${volumes.items}")
