package annoy

trait ANN {

  def addItem(item: S, w: Array[T]): Unit

  def build(q: S): Unit

  def save(filename: String): Boolean

  def unload(): Unit

  def load(filename: String): Boolean

  def getDistance(i: S, j: S): T

  def getNnsByItem(item: S, n: S, k: S): Array[(S, T)]

  def getNnsByVector(w: Array[T], n: S, k: S): Array[(S, T)]

  def getNItems: S

  def verbose(v: Boolean): Unit

  def getItem(item: S): Array[T]

}
