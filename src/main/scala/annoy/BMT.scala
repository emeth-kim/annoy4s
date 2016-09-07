package annoy

/**
  * native: build 260 ms
  * native: query 2046 ms
  * scala build 9816 ms
  * scala query 18187 ms
  *
  * build: 37 times slower than native
  * query: 8 times slower thant native
  *
  * at this time...
  */
object BMT extends App {
  val dimension = 50
  val numItems = 10000
  val numTrials = 1

  var s = System.currentTimeMillis()
  val vectors = (0 until numItems).map { i =>
    (i, Array.fill(dimension)(scala.util.Random.nextFloat()))
  }.toSeq

  s = System.currentTimeMillis()
  val native = new NativeAnnoyIndex(dimension)
  vectors.foreach { case (i, v) =>
    native.addItem(i, v)
  }
  native.build(-1)
  println(s"native: build ${System.currentTimeMillis() - s} ms")

  s = System.currentTimeMillis()
  (0 until numTrials).foreach { trial =>
    (0 until numItems).foreach { item =>
      native.getNnsByItem(item, 100, -1)
    }
  }
  println(s"native: query ${System.currentTimeMillis() - s} ms")

  s = System.currentTimeMillis()
  val scalaVersion = new AnnoyIndex(dimension)
  vectors.foreach { case (i, v) =>
    scalaVersion.addItem(i, v)
  }
  scalaVersion.build(-1)
  println(s"scala build ${System.currentTimeMillis() - s} ms")

  s = System.currentTimeMillis()
  (0 until numTrials).foreach { trial =>
    (0 until numItems).foreach { item =>
      scalaVersion.getNnsByItem(item, 100, -1)
    }
  }
  println(s"scala query ${System.currentTimeMillis() - s} ms")
}
