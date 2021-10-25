package company.vk.data

import breeze.linalg._

import java.io._

object Main {
  def split_xy(denseMatrix: DenseMatrix[Double]): (DenseMatrix[Double], DenseVector[Double]) = {
    Tuple2(denseMatrix(::, 0 to -2), denseMatrix(::, -1).toDenseVector)
  }

  def cv(train_matrix: DenseMatrix[Double], cvSplits: Int): Double = {
    var sumMse = 0.0
    for (train_test <- CV.split(train_matrix, cvSplits)) {
      val (train, test) = train_test
      val (train_x, train_y) = split_xy(train)
      val (test_x, test_y) = split_xy(test)
      val reg = LinearRegression.fit(train_x, train_y)
      sumMse += Losses.MSE(reg.predict(test_x), test_y)
    }
    sumMse / cvSplits
  }

  def fit(train_matrix: DenseMatrix[Double], addIntercept: Boolean): LinearRegression = {
    val (train_x, train_y) = split_xy(train_matrix)
    LinearRegression.fit(train_x, train_y, addIntercept = addIntercept)
  }

  def parseArgs(args: Array[String]): (String, String, Char, Int, Boolean) = {
    if (args.length != 5) {
      throw new Exception("You should provide input as run train_path test_path separator cv_splits");
    }
    if (args(2).length != 1) {
      throw new Exception(s"Wrong separator ${args(2)}")
    }
    val separator = args(2)(0)
    val cvSplits = args(3).toInt
    val addIntercept = args(4).toBoolean
    val (input_path, output_path) = (args(0), args(1))
    Tuple5(input_path, output_path, separator, cvSplits, addIntercept)
  }

  def main(args: Array[String]): Unit = {
    val (input_path, output_path, separator, cvSplits, addIntercept) = parseArgs(args)

    val train_matrix = csvread(new File(input_path), separator);
    val test_matrix = csvread(new File(output_path), separator);
    if (train_matrix.cols != test_matrix.cols) {
      throw new Exception(s"train.cols != test.cols [${train_matrix.cols} != ${test_matrix.cols}]");
    }

    val meanMse = cv(train_matrix, cvSplits)
    println(s"mse on cv = ${meanMse}")

    val regression = fit(train_matrix, addIntercept)
    val (test_x, test_y) = split_xy(test_matrix)
    println(s"mse on test = ${Losses.MSE(test_y, regression.predict(test_x))}")
  }
}
