package company.vk.data

import breeze.linalg.{DenseMatrix, min}
import breeze.numerics.ceil

object CV {
  def split(x: DenseMatrix[Double], n: Int): Seq[(DenseMatrix[Double], DenseMatrix[Double])] = {
    if (n > x.rows) {
      throw new Exception("CV num > x.rows")
    }
    val testGroupSize = ceil(x.rows.doubleValue / n).intValue
    for (i <- 0 until n) yield {
      val (low, high) = (i * testGroupSize,  min((i + 1) * testGroupSize, x.rows - 1))
      val testSet = low to high
      val trainSet = (0 until x.rows).filter(el => !testSet.contains(el))
//      println(i, testGroupSize, low, high)
//      println(trainSet.toList)//.mkString(","))
//      println(testSet.toList)//.mkString(","))
      Tuple2(x(trainSet, ::).toDenseMatrix, x(testSet, ::))
    }
  }
}
