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

import org.scalatest._

class AnnoySpec extends FlatSpec with Matchers {
  
  def getEuclideanInput: Iterator[(Int, Array[Float])] = {
    Iterator(
      (10, Array(1.0f, 1.0f)),
      (11, Array(2.0f, 1.0f)),
      (12, Array(2.0f, 2.0f)),
      (13, Array(3.0f, 2.0f)))
  }
  
  def checkEuclideanResult(res: Option[Seq[(Int, Float)]]) = {
    res.get.map(_._1) shouldBe Seq(10, 11, 12, 13)
    res.get.map(_._2).zip(Seq(0.0f, 1.0f, 1.414f, 2.236f)).foreach{
      case (a, b) => a shouldBe b +- 0.001f
    }
  }
  
  "Annoy" should "create/load and query Euclidean file index" in {
    val loader = Annoy.build(getEuclideanInput, 2, 10, Euclidean)
    checkEuclideanResult(loader.getModel.query(10, 4))
    loader.close()
  }
  
  def getAngularInput: Iterator[(Int, Array[Float])] = {
    Iterator(
      (10, Array(2.0f, 0.0f)),
      (11, Array(1.0f, 1.0f)),
      (12, Array(0.0f, 3.0f)),
      (13, Array(-5.0f, 0.0f)))
  }
  
  def checkAngularResult(res: Option[Seq[(Int, Float)]]) = {
    res.get.map(_._1) shouldBe Seq(10, 11, 12, 13)
    res.get.map(_._2).zip(Seq(0.0f, 0.765f, 1.414f, 2.0f)).foreach{
      case (a, b) => a shouldBe b +- 0.001f
    }
  }
  
  it should "create/load and query Angular file index" in {
    val loader = Annoy.build(getAngularInput, 2, 10, Angular)
    checkAngularResult(loader.getModel.query(10, 4))
    loader.close()
  }
  
}
