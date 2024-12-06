package adventofcode24

import scala.io.Source
import scala.collection.mutable.ListBuffer
import java.io.File
import scala.math.BigInt
import scala.util.matching.Regex

object Day06{
  def timeExecution[T](block: => T): (T, Long) = {
    val start = System.nanoTime()
    val result = block
    val end = System.nanoTime()
    (result, end - start)
  }

  def main(args: Array[String]): Unit = {
    val year = 2024
    val day = 6
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
    
    val directions = List((-1, 0), (0, 1), (1, 0), (0, -1)) // Up, Right, Down, Left

    def parseInput(filename: String): (Array[Array[Char]], (Int, Int), Int) = {
      val grid = Source.fromFile(filename).getLines().toArray.map(_.toCharArray)
      val (startRow, startCol) = grid.zipWithIndex.flatMap { case (row, r) =>
        row.zipWithIndex.collectFirst {
          case ('^', c) => (r, c)
        }
      }.head
      grid(startRow)(startCol) = '.' // Clear the starting position on the grid
      (grid, (startRow, startCol), 0) // Start facing "Up" (index 0 in directions)
    }

    def isInsideGrid(pos: (Int, Int), grid: Array[Array[Char]]): Boolean = {
      val (row, col) = pos
      row >= 0 && row < grid.length && col >= 0 && col < grid(row).length
    }

    def simulatePatrol(grid: Array[Array[Char]], start: (Int, Int), startDir: Int): Set[(Int, Int)] = {
      val visited = scala.collection.mutable.Set[(Int, Int)](start)
      var (currentPos, currentDir) = (start, startDir)

      while (true) {
        val (dr, dc) = directions(currentDir)
        val nextPos = (currentPos._1 + dr, currentPos._2 + dc)

        if (!isInsideGrid(nextPos, grid)) return visited.toSet // Guard leaves the grid
        if (grid(nextPos._1)(nextPos._2) == '#') {
          // Obstacle: Turn right
          currentDir = (currentDir + 1) % directions.length
        } else {
          // Move forward
          currentPos = nextPos
          visited += currentPos
        }
      }

      visited.toSet // This should never be reached
    }
    
    var visitedPositions: Set[(Int, Int)] = Set.empty
    val (grid, start, startDir) = parseInput(inputFileName)

    // Solve Part 1
    val (result1, time1) = timeExecution {
        // solve part 1 here
        visitedPositions = simulatePatrol(grid, start, startDir)   
        visitedPositions.size
    }
    // Print the result
    println(f"The result for Day $day, part 1 is: $result1 (solved in ${time1 / 1e6}%.2f ms)") 

    // Solve part 2
    visitedPositions = visitedPositions - start

    def simulateWithLoopDetection(
      grid: Array[Array[Char]],
      start: (Int, Int),
      startDir: Int
    ): Boolean = {
      val visitedStates = scala.collection.mutable.Set[(Int, Int, Int)]()
      var (currentPos, currentDir) = (start, startDir)

      while (true) {
        if (visitedStates.contains((currentPos._1, currentPos._2, currentDir))) {
          return true // Loop detected
        }
        visitedStates.add((currentPos._1, currentPos._2, currentDir))

        val (dr, dc) = directions(currentDir)
        val nextPos = (currentPos._1 + dr, currentPos._2 + dc)

        if (!isInsideGrid(nextPos, grid)) return false // Guard leaves the grid
        if (grid(nextPos._1)(nextPos._2) == '#') {
          // Obstacle: Turn right
          currentDir = (currentDir + 1) % directions.length
        } else {
          // Move forward
          currentPos = nextPos
        }
      }
      false // This should never be reached
    }

    def findLoopObstructionPositions(grid: Array[Array[Char]], start: (Int, Int), startDir: Int, originalVisited: Set[(Int, Int)]): Int = {
      val obstructionCandidates = originalVisited.toList
      val loopCausingPositions = scala.collection.mutable.Set[(Int, Int)]()

      for (pos <- obstructionCandidates) {
        // Temporarily add an obstacle
        grid(pos._1)(pos._2) = '#'

        // Check if this causes a loop
        if (simulateWithLoopDetection(grid, start, startDir)) {
          loopCausingPositions.add(pos)
        }

        // Remove the obstacle
        grid(pos._1)(pos._2) = '.'
      }

      loopCausingPositions.size
    }

    val (result2, time2) = timeExecution {
        findLoopObstructionPositions(grid, start, startDir, visitedPositions)
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

