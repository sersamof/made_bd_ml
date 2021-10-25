package company.vk.data

import breeze.linalg.DenseVector
import breeze.stats.mean

object Losses {
  def MSE(x: DenseVector[Double], y: DenseVector[Double]): Double = {
    val diff = x - y
    mean(diff * diff)
  }
}
