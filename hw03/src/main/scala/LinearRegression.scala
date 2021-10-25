package company.vk.data

import breeze.linalg.{DenseMatrix, DenseVector, norm, normalize}

class LinearRegression(val weights: DenseVector[Double] = null, val addIntercept: Boolean = false) {
  def predict(x: DenseMatrix[Double]): DenseVector[Double] = {
    if (weights == null) {
      throw new Exception("Regression is not fitted")
    }
    if (addIntercept) {
      LinearRegression.prependOnes(x) * weights
    } else {
      x * weights
    }
  }
}

object LinearRegression {
  def prependOnes(x: DenseMatrix[Double]): DenseMatrix[Double] = {
    DenseMatrix.horzcat(DenseMatrix.ones[Double](x.rows, 1), x)
  }

  def fit(x: DenseMatrix[Double], y: DenseVector[Double],
          addIntercept: Boolean = false, batchSize: Int = 128,
          learningRate: Double = 0.001, tol: Double = 0.0001,
          maxEpochs: Int = 10000
         ): LinearRegression = {
    val trainX = if (addIntercept) prependOnes(x) else x
    //    val cov = DenseMatrix.zeros[Double](trainX.cols, trainX.cols) + (trainX.t * trainX)
    //    val scaled = DenseVector.zeros[Double](trainX.cols) + (trainX.t * y)
    //    new LinearRegression(cov \ scaled, addIntercept)
    var weights = DenseVector.zeros[Double](trainX.cols)
    var newWeights = DenseVector.zeros[Double](trainX.cols)
    var curEpoch = 1;
    while ((curEpoch <= maxEpochs) && (curEpoch == 1 || norm(newWeights - weights) > tol)) {
      for (i <- Range(0, trainX.rows).grouped(batchSize)) {
        weights = newWeights
        val bx = trainX(i, ::)
        val gradW = - 2.0 * bx.t * (y(i) - bx * weights) / i.length.doubleValue
        newWeights = weights - learningRate * gradW
        curEpoch += 1
      }
    }
    new LinearRegression(weights, addIntercept)
  }
}
