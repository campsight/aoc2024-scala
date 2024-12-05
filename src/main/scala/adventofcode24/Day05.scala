package adventofcode24

import scala.io.Source
import scala.collection.mutable.ListBuffer
import java.io.File
import scala.math.BigInt

object Day05 {
  def timeExecution[T](block: => T): (T, Long) = {
    val start = System.nanoTime()
    val result = block
    val end = System.nanoTime()
    (result, end - start)
  }

  def main(args: Array[String]): Unit = {
    val year = 2024
    val day = 5
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
    val input = Source.fromFile(inputFile).getLines().mkString("\n")
    Source.fromFile(inputFile).close()

    val (rules, updates) = parseInput(input)

    // Function to check if an update is valid based on the rules
    def isValidUpdate(update: List[Int]): Boolean = {
      val position = update.zipWithIndex.toMap
      rules.filter { case (x, y) => update.contains(x) && update.contains(y) }
        .forall { case (x, y) => position(x) < position(y) }
    }

    // Function to get the middle page of an update
    def middlePage(update: List[Int]): Int = {
      update(update.length / 2)
    }
    
    // Solve Part 1
    val (result1, time1) = timeExecution {
        // solve part 1 here
        val validUpdates = updates.filter(isValidUpdate)
        validUpdates.map(middlePage).sum
    }
    // Print the result
    println(f"The result for Day $day, part 1 is: $result1 (solved in ${time1 / 1e6}%.2f ms)") 


    // Solve part 2
    def buildRulesMap(rules: List[(Int, Int)]): Map[Int, Set[Int]] = {
        // Convert the list of rules into a map where each key points to the pages it precedes
        rules.groupBy(_._1).map { case (key, pairs) => key -> pairs.map(_._2).toSet }
    }

    def reorderUpdateWithRulesMap(update: List[Int], rulesMap: Map[Int, Set[Int]]): List[Int] = {
        update.sortWith { (a, b) =>
            if (rulesMap.getOrElse(a, Set()).contains(b)) true // a must come before b
            else if (rulesMap.getOrElse(b, Set()).contains(a)) false // b must come before a
            else false // Keep the original order if no direct rule applies
        }
    }

    val (result2, time2) = timeExecution {
        val rulesMap = buildRulesMap(rules)

        val incorrectUpdates = updates.filterNot(isValidUpdate)

        // Reorder updates and calculate the sum of middle pages
        incorrectUpdates
            .map(update => reorderUpdateWithRulesMap(update, rulesMap))
            .map(middlePage)
            .sum
    }

    // Print the result
    println(f"The result for Day $day, part 2 is: $result2 (solved in ${time2 / 1e6}%.2f ms)")

  }

  def parseInput(input: String): (List[(Int, Int)], List[List[Int]]) = {
    // Split input into rules and updates
    val sections = input.split("\n\n")
    val rules = sections(0).split("\n").map(_.split('|')).map { case Array(x, y) => (x.toInt, y.toInt) }.toList
    val updates = sections(1).split("\n").map(_.split(',').map(_.toInt).toList).toList
    (rules, updates)
  }

}

