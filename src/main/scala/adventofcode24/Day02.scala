package adventofcode24

import scala.io.Source
import scala.collection.mutable.ListBuffer
import java.io.File

object Day02 {
  def timeExecution[T](block: => T): (T, Long) = {
    val start = System.nanoTime()
    val result = block
    val end = System.nanoTime()
    (result, end - start)
  }

  def main(args: Array[String]): Unit = {
    val year = 2024
    val day = 2
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
    
    // Proceed with reading the input
    val inputLines = scala.io.Source.fromFile(inputFileName).getLines().toList

    // Read and parse the input
    val reports = parseInput(inputLines)
      
    // Solve Part 1
    val result = reports.count(isSafeReport)

    // Print the result
    println(s"The result for Day $day, part 1 is: $result")

    // Solve Part 2
    // Measure time for isSafeWithDampener
    val (result2, time1) = timeExecution {
      reports.count(isSafeWithDampener)
    }
    // Print the result
    println(f"The result for Day $day, part 2 is: $result2 (solved in ${time1 / 1e6}%.2f ms)")

    // Optimized solution
    val (result2opt, time2) = timeExecution {
      reports.count(isSafeWithDampenerOpt)
    }
    println(f"The result for Day $day, part 2 (optimised) is: $result2opt (solved in ${time2 / 1e6}%.2f ms)")

    // Compare results of both methods
    val discrepancies = reports.zipWithIndex.collect {
      case (report, idx) if !isSafeWithDampenerOpt(report) && isSafeWithDampener(report) =>
        (idx + 1, inputLines(idx)) // +1 to make indices 1-based for reporting
    }

    // Print the first 10 discrepancies
    println("First 10 discrepancies (1-based line indices):")
    discrepancies.take(10).foreach { case (lineIdx, line) =>
      println(s"Line $lineIdx: $line")
      println(f"Normal method: ${isSafeWithDampener(reports(lineIdx - 1))}")
      println(f"Optima method: ${isSafeWithDampenerOpt(reports(lineIdx - 1))}")
    }

    // Summary of discrepancies
    println(s"Total discrepancies: ${discrepancies.size}")
  }

  def parseInput(lines: List[String]): List[List[Int]] = {
    // Filter out empty lines and split each line into integers
    lines
      .filter(_.trim.nonEmpty) // Remove any blank lines
      .map(_.split("\\s+").map(_.toInt).toList) // Split on spaces and convert to integers
  }

  def isSafeReport(levels: List[Int]): Boolean = {
    // Calculate the differences between adjacent levels
    val differences = levels.sliding(2).map { case Seq(a, b) => b - a }.toList

    // Check if all differences are within bounds (1 to 3 or -1 to -3)
    val withinBounds = differences.forall(diff => diff >= -3 && diff <= 3)

    // Check if all differences are consistently increasing or decreasing
    val allIncreasing = differences.forall(_ > 0)
    val allDecreasing = differences.forall(_ < 0)

    withinBounds && (allIncreasing || allDecreasing)
  }

  def isSafeWithDampener(levels: List[Int]): Boolean = {
    // Check if already safe
    if (isSafeReport(levels)) return true

    // Try removing each level and check if the resulting report is safe
    levels.indices.exists { i =>
      val modifiedLevels = levels.take(i) ++ levels.drop(i + 1)
      isSafeReport(modifiedLevels)
    }
  }

  def isSafeWithDampenerOpt(levels: List[Int]): Boolean = {
    val differences = levels.sliding(2).map { case Seq(a, b) => b - a }.toList

    // Identify out-of-bounds differences
    val outOfBounds = differences.zipWithIndex.filter { case (diff, _) => diff < -3 || diff > 3 }

    // Early exit: more than two differences out of bounds
    if (outOfBounds.size > 2) return false

    // If no out-of-bounds differences, the report is safe
    if (outOfBounds.isEmpty) {
      if (differences.forall(_ > 0) || differences.forall(_ < 0)) { return true }

      // Check for a single transition (increasing to decreasing or vice versa)
      val transitionIdx = differences.zipWithIndex.find {
        case (diff, idx) =>
          idx < differences.size - 1 && diff >= 0 && differences(idx + 1) <= 0 ||
          idx < differences.size - 1 && diff <= 0 && differences(idx + 1) >= 0
      }.map(_._2)

      var result = false
      transitionIdx match {
        case Some(idx) =>
          // Remove either the level before or after the transition
          val before = levels.take(idx) ++ levels.drop(idx + 1)
          val on = levels.take(idx + 1) ++ levels.drop(idx + 2)
          val after = levels.take(idx + 2) ++ levels.drop(idx + 3)
          result = isSafeReport(before) || isSafeReport(on) || isSafeReport(after)
        case None =>
          // No valid transition fix; unsafe
          result = false
      }
      return result
    }

    // If exactly one out-of-bounds difference, check adjacent levels
    if (outOfBounds.size == 1) {
      val idx = outOfBounds.head._2
      val before = levels.take(idx) ++ levels.drop(idx + 1)
      val after = levels.take(idx + 1) ++ levels.drop(idx + 2)
      return isSafeReport(before) || isSafeReport(after)
    }

    // If exactly two out-of-bounds differences, ensure they are adjacent
    if (outOfBounds.size == 2) {
      val idx1 = outOfBounds(0)._2
      val idx2 = outOfBounds(1)._2
      if (idx2 == idx1 + 1) {
        // Remove the overlapping level between the two differences
        val candidate = levels.take(idx2) ++ levels.drop(idx2 + 1)
        return isSafeReport(candidate)
      }
    }

    // Default case: unsafe
    false
  }
}
