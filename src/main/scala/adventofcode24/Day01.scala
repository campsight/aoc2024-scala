package adventofcode24

import scala.io.Source
import scala.collection.mutable.ListBuffer
import java.io.File

object Day01 {
  def main(args: Array[String]): Unit = {
    val year = 2024
    val day = 1
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

    // Parse the input into two lists
    val (list1, list2) = parseInput(inputLines)

    // Sort both lists
    val sortedList1 = list1.sorted
    val sortedList2 = list2.sorted

    // Subtract the lists and take the absolute value
    val subtractedList = sortedList1.zip(sortedList2).map { case (a, b) => math.abs(a - b) }

    // Compute the sum of the resulting list
    val result = subtractedList.sum

    // Print the result
    println(s"The result for Day 1, part 1 is: $result")

    // Count occurrences of each number in list2
    val list2Counts = list2.groupBy(identity).mapValues(_.size)

    // Compute the sum for list1 elements based on occurrences in list2
    val result2 = list1.map { num =>
        val occurrences = list2Counts.getOrElse(num, 0)
        num * occurrences
    }.sum

    // Print the result
    println(s"The result for Day 1, part 2 is: $result2")
  }

  def parseInput(lines: List[String]): (List[Int], List[Int]) = {
    val list1 = ListBuffer[Int]()
    val list2 = ListBuffer[Int]()

    lines.filter(_.trim.nonEmpty).foreach { line =>
        val parts = line.split("\\s+")
        list1 += parts(0).toInt
        list2 += parts(1).toInt
    }

    (list1.toList, list2.toList)
  }
}
