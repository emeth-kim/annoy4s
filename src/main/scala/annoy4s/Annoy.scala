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
      val annoyIndex = metric match {
        case Angular => Annoy.annoyLib.createAngular(dimension)
        case Euclidean => Annoy.annoyLib.createEuclidean(dimension)
      }
      Annoy.annoyLib.load(annoyIndex, path)
      Annoy.annoyLib.verbose(annoyIndex, verbose)
      model = new AnnoyModel(idToIndex, indexToId, annoyIndex, dimension)
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
  annoyIndex: Pointer,
  dimension: Int
) {
  def close() = {
    Annoy.annoyLib.deleteIndex(annoyIndex)
  }

  def query(vector: Seq[Float], maxReturnSize: Int) = {
    val result = Array.fill(maxReturnSize)(-1)
    val distances = Array.fill(maxReturnSize)(-1.0f)
    Annoy.annoyLib.getNnsByVector(annoyIndex, vector.toArray, maxReturnSize, -1, result, distances)
    result.toList.filter(_ != -1).map(indexToId.apply).zip(distances.toSeq)
  }

  def query(id: Int, maxReturnSize: Int) = {
    idToIndex.get(id).map { index =>
      val result = Array.fill(maxReturnSize)(-1)
      val distances = Array.fill(maxReturnSize)(-1.0f)
      Annoy.annoyLib.getNnsByItem(annoyIndex, index, maxReturnSize, -1, result, distances)
      result.toList.filter(_ != -1).map(indexToId.apply).zip(distances.toSeq)
    }
  }
}

object Annoy {

  val annoyLib = Native.loadLibrary("annoy", classOf[AnnoyLibrary]).asInstanceOf[AnnoyLibrary]

  def build(iterator: Iterator[(Int, Array[Float])],
    dimension: Int, numOfTrees: Int, metric: Metric = Angular,
    outputFile: String = "annoy-index", verbose: Boolean = false, cleanup: AnnoySingletonLoader => Unit = {l => }): AnnoySingletonLoader = {

    val annoyIndex = metric match {
      case Angular => annoyLib.createAngular(dimension)
      case Euclidean => annoyLib.createEuclidean(dimension)
    }

    annoyLib.verbose(annoyIndex, verbose)

    var ids = new ListBuffer[Int]()

    var index = 0
    iterator.foreach { case (id, vector) =>
      Annoy.annoyLib.addItem(annoyIndex, index, vector)
      index += 1
      ids += id
    }

    annoyLib.build(annoyIndex, numOfTrees)
    annoyLib.save(annoyIndex, outputFile)
    annoyLib.deleteIndex(annoyIndex)

    val idsList = ids.result()
    val loader = AnnoySingletonLoader(idsList, dimension, metric, outputFile, verbose)
    cleanup(loader)
    loader
  }

}

sealed trait Metric
case object Angular extends Metric
case object Euclidean extends Metric

