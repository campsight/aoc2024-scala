package adventofcode24

import scala.io.Source
import scala.collection.mutable
import java.io.File
import breeze.linalg._
import scala.math.BigInt
import scala.util.control.Breaks._
import scala.annotation.tailrec
import scala.math.Numeric.Implicits._

object Day13 {
  def timeExecution[T](block: => T): (T, Long) = {
    val start = System.nanoTime()
    val result = block
    val end = System.nanoTime()
    (result, end - start)
  }

  def main(args: Array[String]): Unit = {
    val year = 2024
    val day = 13
    val inputFileName = f"${year}_day${day}%02d_input.txt"

    // Check if the file exists, if not, download it
    val inputFile = new File(inputFileName)
    if (!inputFile.exists()) {
      println(s"Input file $inputFileName not found. Downloading...")
      AOCHelper.downloadInput(year, day, inputFileName)
    }

    // Validate the file is now present
    if (!inputFile.exists()) {
      throw new RuntimeException(
        s"Failed to download the input file: $inputFileName"
      )
    }

    // Prepare for Part 1
    def calculateMinimumTokens(
        data: List[((Int, Int), (Int, Int), (BigInt, BigInt))]
    ): BigInt = {
      var totalTokens: BigInt = BigInt(0)

      data.foreach { case ((ax, ay), (bx, by), (px, py)) =>
        // Compute determinants
        val D = BigInt(ax) * BigInt(by) - BigInt(ay) * BigInt(bx)
        if (D == 0) {
          /* println(
            s"No solution: determinant is zero for Ax=$ax, Bx=$bx, Ay=$ay, By=$by"
          ) */
        } else {
          val Dx = (px * BigInt(by)) - (py * BigInt(bx))
          val Dy = (BigInt(ax) * py) - (BigInt(ay) * px)

          // Compute solutions
          val a = Dx / D
          val b = Dy / D

          // Validate the solution
          if (
            a * ax + b * bx == px && a * ay + b * by == py && a >= 0 && b >= 0
          ) {
            val cost = a * 3 + b
            /* println(
              s"Valid solution for Px=$px, Py=$py: a=$a, b=$b, cost=$cost"
            ) */
            totalTokens += cost
          } else {
            // println(s"Invalid solution for Px=$px, Py=$py")
          }
        }
      }

      totalTokens
    }

    def parseInput(
        file: String
    ): List[((Int, Int), (Int, Int), (BigInt, BigInt))] = {
      val source = scala.io.Source.fromFile(file)
      val data = source
        .getLines()
        .filter(_.nonEmpty) // Remove blank lines
        .grouped(3) // Group every 3 non-empty lines
        .map { lines =>
          val buttonA = lines(0).split("[ ,]+") // Split by spaces and commas
          val buttonB = lines(1).split("[ ,]+")
          val prize = lines(2).split("[ ,]+")

          val ax = buttonA(2).drop(2).toInt // Extract value after "X+"
          val ay = buttonA(3).drop(2).toInt // Extract value after "Y+"
          val bx = buttonB(2).drop(2).toInt // Extract value after "X+"
          val by = buttonB(3).drop(2).toInt // Extract value after "Y+"
          val px = BigInt(prize(1).drop(2)) // Extract value after "X="
          val py = BigInt(prize(2).drop(2)) // Extract value after "Y="

          ((ax, ay), (bx, by), (px, py))
        }
        .toList
      source.close()
      data
    }

    // Solve Part 1
    val (result1, time1) = timeExecution {
      // solve part 1 here
      val machines = parseInput(inputFileName)
      //println(machines)
      calculateMinimumTokens(machines)
    }

    // Print the result
    println(
      f"The result for Day $day, part 1 is: $result1 (solved in ${time1 / 1e6}%.2f ms)"
    )

    // Solve part 2

    val (result2, time2) = timeExecution {
      // solve part 2 here
      val machines = parseInput(inputFileName)
      val offset = BigInt("10000000000000")
      val updatedData = machines.map { case ((ax, ay), (bx, by), (px, py)) =>
        ((ax, ay), (bx, by), (px + offset, py + offset))
      }
      //println(updatedData)
      calculateMinimumTokens(updatedData)
    }

    // Print the result
    println(
      f"The result for Day $day, part 2 is: $result2 (solved in ${time2 / 1e6}%.2f ms)"
    )

  }

}
