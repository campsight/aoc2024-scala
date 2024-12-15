package adventofcode24

import scala.io.Source
import scala.collection.mutable
import java.io.File
import breeze.linalg._
import scala.math.BigInt
import scala.util.control.Breaks._
import scala.annotation.tailrec
import scala.math.Numeric.Implicits._

object Day14 {
  def timeExecution[T](block: => T): (T, Long) = {
    val start = System.nanoTime()
    val result = block
    val end = System.nanoTime()
    (result, end - start)
  }

  def main(args: Array[String]): Unit = {
    val year = 2024
    val day = 14
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
    case class Robot(position: (Int, Int), velocity: (Int, Int))

    def parseInput(filePath: String): List[Robot] = {
      Source.fromFile(filePath).getLines().toList.map { line =>
        val parts = line.split(" ")
        val position =
          parts(0).stripPrefix("p=").split(",").map(_.toInt) match {
            case Array(x, y) => (x, y)
          }
        val velocity =
          parts(1).stripPrefix("v=").split(",").map(_.toInt) match {
            case Array(vx, vy) => (vx, vy)
          }
        Robot(position, velocity)
      }
    }

    def wrap(value: Int, bound: Int): Int = {
      // Adjust to handle negative values
      ((value % bound) + bound) % bound
    }

    def simulate(
        robots: List[Robot],
        dimensions: (Int, Int),
        iterations: Int
    ): Int = {
      val (width, height) = dimensions

      val finalPositions = robots.map { robot =>
        val (px, py) = robot.position
        val (vx, vy) = robot.velocity

        // Debug: Initial robot state
        // println(s"Initial Robot: position=($px, $py), velocity=($vx, $vy)")

        // Update the position after `iterations` steps
        val finalX = wrap(px + vx * iterations, width)
        val finalY = wrap(py + vy * iterations, height)

        // Debug: Final robot position
        // println(s"Final Robot: position=($finalX, $finalY)")

        (finalX, finalY)
      }

      // Determine quadrants for robots not in the middle
      val middleX = (width - 1) / 2
      val middleY = (height - 1) / 2

      val quadrants = Array(0, 0, 0, 0)
      finalPositions.foreach { case (x, y) =>
        if (x != middleX && y != middleY) {
          val quadrant = if (x < middleX) {
            if (y < middleY) 0 else 1
          } else {
            if (y < middleY) 2 else 3
          }
          quadrants(quadrant) += 1
        }
      }

      // Debug: Quadrant counts
      // println(s"Quadrants: ${quadrants.mkString(", ")}")

      // Calculate the safety factor as the product of quadrants
      quadrants.product
    }

    // Solve Part 1
    val (result1, time1) = timeExecution {
      // solve part 1 here
      val robots = parseInput(inputFileName)
      val dimensions = (101, 103)
      // val dimensions = (11, 7)
      val iterations = 100
      simulate(robots, dimensions, iterations)
    }

    // Print the result
    println(
      f"The result for Day $day, part 1 is: $result1 (solved in ${time1 / 1e6}%.2f ms)"
    )

    // Solve part 2
    def simulatePositions(
        robots: List[Robot],
        dimensions: (Int, Int),
        steps: Int
    ): List[(Int, Int)] = {
      val (width, height) = dimensions
      robots.map { robot =>
        val (px, py) = robot.position
        val (vx, vy) = robot.velocity
        (wrap(px + vx * steps, width), wrap(py + vy * steps, height))
      }
    }

    def computeDensity(
        positions: List[(Int, Int)],
        dimensions: (Int, Int)
    ): Int = {
      val (width, height) = dimensions
      val field = Array.fill(height)(Array.fill(width)(0))

      // Fill the field with robot positions
      positions.foreach { case (x, y) =>
        field(y)(x) += 1
      }

      // Calculate density: count robots adjacent horizontally in rows with >= 4 robots
      field.map { row =>
        val count = row.count(_ > 0)
        if (count >= 4) {
          row.sliding(2).count { case Array(a, b) => a > 0 && b > 0 }
        } else {
          0
        }
      }.sum
    }

    def printField(robots: List[(Int, Int)], dimensions: (Int, Int)): Unit = {
      val (width, height) = dimensions
      val field = Array.fill(height, width)('.')

      // Place robots on the field
      robots.foreach { case (x, y) =>
        field(y)(x) = if (field(y)(x) == '.') '1' else (field(y)(x) + 1).toChar
      }

      // Print the field
      println("Field:")
      field.foreach(row => println(row.mkString("")))
    }

    def findEasterEggStep(robots: List[Robot], dimensions: (Int, Int)): Int = {
      val maxIterations = dimensions._1 * dimensions._2
      var maxDensity = 0
      var bestStep = 0

      for (step <- 0 until maxIterations) {
        val positions = simulatePositions(robots, dimensions, step)
        val density = computeDensity(positions, dimensions)

        // If we find a denser configuration, print the field and update the best step
        if (density > maxDensity) {
          maxDensity = density
          bestStep = step
          println(s"Dense configuration found at step: $step")
          printField(positions, dimensions)
        }
      }

      bestStep
    }

    val (result2, time2) = timeExecution {
      // solve part 2 here
      val robots = parseInput(inputFileName)
      val dimensions = (101, 103)
      findEasterEggStep(robots, dimensions)
    }

    // Print the result
    println(
      f"The result for Day $day, part 2 is: $result2 (solved in ${time2 / 1e6}%.2f ms)"
    )

  }

}
