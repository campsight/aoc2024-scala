package adventofcode24

import scala.io.Source
import scala.collection.mutable.ListBuffer
import java.io.File
import scala.math.BigInt
import scala.util.matching.Regex
import scala.collection.immutable.ArraySeq
import scala.util.control.Breaks._

object Day07 {
  def timeExecution[T](block: => T): (T, Long) = {
    val start = System.nanoTime()
    val result = block
    val end = System.nanoTime()
    (result, end - start)
  }

  def main(args: Array[String]): Unit = {
    val year = 2024
    val day = 7
    val inputFileName = f"${year}_day${day}%02d_input.txt"

    // Check if the file exists, if not, download it
    val inputFile = new File(inputFileName)
    if (!inputFile.exists()) {
      println(s"Input file $inputFileName not found. Downloading...")
      AOCHelper.downloadInput(year, day, inputFileName)
    }

    // Validate the file is now present
    if (!inputFile.exists()) {
      throw new RuntimeException(s"Failed to download the input file: $inputFileName")
    }    
    
    // Read the grid from the input file
    val inputLines = Source.fromFile(inputFileName).getLines().toList

    // Prep
    case class Operator(name: String, operation: (BigInt, BigInt) => BigInt)

    // Define the operators
    val add = Operator("+", _ + _)
    val mul = Operator("*", _ * _)
    val concat = Operator("concat", (a: BigInt, b: BigInt) => BigInt(a.toString + b.toString))

    // Create the list of operators for each part
    val operators = Array(
      Array(add, mul),          // Part 1 operators
      Array(add, mul, concat)   // Part 2 operators
    )

    // Generate Cartesian product (equivalent to itertools.product in Python)
    def cartesianProduct[T](lists: Seq[Seq[T]]): Seq[Seq[T]] =
      lists.foldLeft(Seq(Seq.empty[T])) { (acc, list) =>
        for {
          seq <- acc
          elem <- list
        } yield seq :+ elem
      }

    def doOps(total: BigInt, nums: Seq[BigInt], ops: Array[Operator]): Boolean = {
      val nOps = nums.length - 1
      val opCombinations = cartesianProduct(Seq.fill(nOps)(ops.toSeq))

      opCombinations.exists { combination =>
        var cur = nums.head
        var valid = true

        combination.zip(nums.tail).foreach { case (op, num) =>
          cur = op.operation(cur, num)
          if (cur > total) {
            valid = false
          }
        }

        valid && cur == total
      }
    }

    var ans = Array(BigInt(0), BigInt(0))

    for (line <- inputLines) {
      val parts = line.split(":")
      val total = BigInt(parts(0).trim)
      val nums = parts(1).trim.split(" ").map(BigInt(_)).toSeq
      0
    }

    // Solve Part 1
    val (result1, time1) = timeExecution {
        var ans = BigInt(0)
        // solve part 1 here
        for (line <- inputLines) {
          val parts = line.split(":")
          val total = BigInt(parts(0).trim)
          val nums = parts(1).trim.split(" ").map(BigInt(_)).toSeq
          if (doOps(total, nums, operators(0))) {
            ans += total
          }
        }
        ans
    }
    // Print the result
    println(f"The result for Day $day, part 1 is: $result1 (solved in ${time1 / 1e6}%.2f ms)") 

    // Solve part 2

    val (result2, time2) = timeExecution {
        var ans = BigInt(0)
        // solve part 1 here
        for (line <- inputLines) {
          val parts = line.split(":")
          val total = BigInt(parts(0).trim)
          val nums = parts(1).trim.split(" ").map(BigInt(_)).toSeq
          if (doOps(total, nums, operators(1))) {
            ans += total
          }
        }
        ans
    }

    // Print the result
    println(f"The result for Day $day, part 2 is: $result2 (solved in ${time2 / 1e6}%.2f ms)")

  }
}
