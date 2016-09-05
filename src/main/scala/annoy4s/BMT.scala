package annoy4s

import scala.util.Random

object BMT extends App {
  val dimension = 50
  val numItems = 1000000
  val numTrials = 1

  var s = System.currentTimeMillis()
  val vectors = (0 until numItems).map { i =>
    (i, Array.fill(dimension)(Random.nextFloat()))
  }.toSeq
  println(s"array build ${System.currentTimeMillis() - s}")

  s = System.currentTimeMillis()
  val loader = Annoy.build(vectors.iterator, dimension, 10, Euclidean)
  println(s"ann build ${System.currentTimeMillis() - s}")

  s = System.currentTimeMillis()
  (0 until numTrials).foreach { trial =>
    (0 until numItems).foreach { i =>
      loader.getModel.query(i, 10)
    }
  }
  println(s"query ${System.currentTimeMillis() - s}")
  loader.close()
}
