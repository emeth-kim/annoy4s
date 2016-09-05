// Copyright (c) 2016 pishen
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package annoy4s

import com.sun.jna._

import scala.collection.mutable.ListBuffer

case class AnnoySingletonLoader(ids: Seq[Int], dimension: Int, metric: Metric, path: String, verbose: Boolean) {

  var model: AnnoyModel = _

  def getModel: AnnoyModel = {
    if (model == null) {
      val idToIndex = ids.zipWithIndex.toMap
      val indexToId = ids
      val annoy = new annoy4j.Annoy(dimension, if (metric == Angular) annoy4j.Metric.Angular else annoy4j.Metric.Euclidean)
      annoy.load(path)
      annoy.verbose(verbose)
      model = new AnnoyModel(idToIndex, indexToId, annoy)
    }
    model
  }

  def close(): Unit = {
    if (model != null) {
      model.close()
    }
  }

}

class AnnoyModel(
  idToIndex: Map[Int, Int],
  indexToId: Seq[Int],
  annoy: annoy4j.Annoy
) {

  def close(): Unit = {
    annoy.close()
  }

  def query(vector: Seq[Float], maxReturnSize: Int): Seq[(Int, Float)] = {
    val r = annoy.getNnsByVector(vector.toArray, maxReturnSize)
    r.getResult.filter(_ != -1).map(indexToId.apply).zip(r.getDistances)
  }

  def query(id: Int, maxReturnSize: Int): Option[Seq[(Int, Float)]] = {
    idToIndex.get(id).map { index =>
      val r = annoy.getNnsByItem(index, maxReturnSize)
      r.getResult.filter(_ != -1).map(indexToId.apply).zip(r.getDistances)
    }
  }
}

object Annoy {

  def build(iterator: Iterator[(Int, Array[Float])],
    dimension: Int, numOfTrees: Int, metric: Metric = Angular,
    outputFile: String = "annoy-index", verbose: Boolean = false, cleanup: AnnoySingletonLoader => Unit = {l => }): AnnoySingletonLoader = {

    val annoy = new annoy4j.Annoy(dimension, if (metric == Angular) annoy4j.Metric.Angular else annoy4j.Metric.Euclidean)
    annoy.verbose(verbose)

    var ids = new ListBuffer[Int]()

    var index = 0
    iterator.foreach { case (id, vector) =>
      annoy.addItem(index, vector)
      index += 1
      ids += id
    }

    annoy.build(numOfTrees)
    annoy.save(outputFile)
    annoy.close()

    val idsList = ids.result()
    val loader = AnnoySingletonLoader(idsList, dimension, metric, outputFile, verbose)
    cleanup(loader)
    loader
  }

}

sealed trait Metric
case object Angular extends Metric
case object Euclidean extends Metric

