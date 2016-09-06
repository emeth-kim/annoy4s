package annoy4s.spark

import annoy4s.{AnnoySingletonLoader, Angular, Metric, Annoy}
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{Row, DataFrame}

object SparkAnnoy {

  /**
    * To build AnnoyIndex, all data should be collected to driver.
    * After building AnnoyIndex, this broadcasts the file of AnnoyIndex to all the executors by sc.addFile
    *
    * NOTE: This will consume as much memory as the largest partition in this RDD.
    */
  def build(dataset: RDD[(Int, Array[Float])], rank: Int, numOfTrees: Int, metric: Metric = Angular, verbose: Boolean = false): AnnoySingletonLoader = {
    val sc = dataset.sparkContext
    Annoy.build(dataset.toLocalIterator, rank, numOfTrees, verbose = verbose, cleanup = {l => sc.addFile(l.path)})
  }

  def build(dataset: DataFrame, rank: Int, numOfTree: Int, metric: Metric = Angular, verbose: Boolean = false): AnnoySingletonLoader = {
    val rdd = dataset.map { case Row(id: Int, features: Seq[_]) =>
      (id, features.asInstanceOf[Seq[Float]].toArray)
    }
    build(rdd, rank, numOfTree, metric, verbose)
  }

  private def _getAllNns(dataset: RDD[Int], loader: AnnoySingletonLoader, n: Int): RDD[(Int, Array[(Int, Float)])] = {
    dataset.flatMap(id => loader.getModel.query(id, n).map((id, _)))
  }

  /**
    *  Distributed computing all Nns
    *  This will uses the file of AnnoyIndex building on the Driver.
    */
  def getAllNns(dataset: RDD[(Int, Array[Float])], loader: AnnoySingletonLoader, n: Int): RDD[(Int, Array[(Int, Float)])] = {
    _getAllNns(dataset.keys, loader, n)
  }

  def getAllNns(dataset: DataFrame, loader: AnnoySingletonLoader, n: Int): DataFrame = {
    import dataset.sqlContext.implicits._
    val rdd = dataset.map(_.getInt(0))
    _getAllNns(rdd, loader, n).toDF("id", "nns")
  }

}
