package adventofcode24

import scala.io.Source
import scala.collection.mutable.ListBuffer
import java.io.File
import scala.math.BigInt
import scala.util.matching.Regex

object Day03 {
  def timeExecution[T](block: => T): (T, Long) = {
    val start = System.nanoTime()
    val result = block
    val end = System.nanoTime()
    (result, end - start)
  }

  def main(args: Array[String]): Unit = {
    val year = 2024
    val day = 3
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

    // Define the regular expression for valid mul instructions
    val mulRegex: Regex = """mul\((\d{1,3}),(\d{1,3})\)""".r
    val doRegex: Regex = """\bdo\(\)\b""".r
    val dontRegex: Regex = """\bdon't\(\)\b""".r

    // Solve Part 1
    val (result1, time1) = timeExecution {
        // solve part 1 here
        inputLines.flatMap { line =>
            mulRegex.findAllMatchIn(line).map { m =>
                val x = BigInt(m.group(1)) // Convert to BigInt
                val y = BigInt(m.group(2)) // Convert to BigInt
                x * y // Product is BigInt
            }
            }.sum // BigInt summation
    }
    // Print the result
    println(f"The result for Day $day, part 1 is: $result1 (solved in ${time1 / 1e6}%.2f ms)") 

    // Solve part 2
    val data = Source.fromFile(inputFileName).mkString
    // Define a single regex for all instructions
    val combinedRegex: Regex = """mul\((\d+),(\d+)\)|(do\(\))|(don\'t\(\))""".r
    // Initialize variables
    var total = 0
    var doMul = true

    val (result2, time2) = timeExecution {
        // Process each match in the input
        combinedRegex.findAllMatchIn(data).foreach { m =>
            if (m.group(3) != null) {
                // do()
                doMul = true
            } else if (m.group(4) != null) {
                // don't()
                doMul = false
            } else if (doMul && m.group(1) != null && m.group(2) != null) {
                // mul(X, Y)
                val x = m.group(1).toInt
                val y = m.group(2).toInt
                total += x * y
            }
        }
        total
    }

    // Print the result
    println(f"The result for Day $day, part 2 is: $result2 (solved in ${time2 / 1e6}%.2f ms)")
  }

  def parseInput(lines: List[String]): List[List[Int]] = {
    // Filter out empty lines and split each line into integers
    lines
      .filter(_.trim.nonEmpty) // Remove any blank lines
      .map(_.split("\\s+").map(_.toInt).toList) // Split on spaces and convert to integers
  }

}
