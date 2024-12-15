package adventofcode24

import scala.io.Source
import scala.collection.mutable
import java.io.File
import breeze.linalg._
import scala.math.BigInt
import scala.util.control.Breaks._
import scala.annotation.tailrec
import scala.math.Numeric.Implicits._

object Day15 {
  def timeExecution[T](block: => T): (T, Long) = {
    val start = System.nanoTime()
    val result = block
    val end = System.nanoTime()
    (result, end - start)
  }

  def main(args: Array[String]): Unit = {
    val year = 2024
    val day = 15
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
    case class Position(row: Int, col: Int)

    def parseInput(filePath: String): (Array[Array[Char]], String) = {
      val lines = Source.fromFile(filePath).getLines().toList
      val grid = lines.takeWhile(_.nonEmpty).map(_.toCharArray).toArray
      val moves = lines
        .dropWhile(_.nonEmpty)
        .mkString("")
        .replaceAll("\\s", "") // Combine moves into one string
      (grid, moves)
    }

    def simulateMoves(grid: Array[Array[Char]], moves: String): Int = {
      val directions = Map(
        '^' -> (-1, 0),
        'v' -> (1, 0),
        '<' -> (0, -1),
        '>' -> (0, 1)
      )

      var robotPos = findRobot(grid)

      moves.foreach { move =>
        val dir = directions(move)
        if (tryMoveRobot(grid, robotPos, dir)) {
          grid(robotPos.row)(robotPos.col) = '.' // Clear old robot position
          robotPos = Position(robotPos.row + dir._1, robotPos.col + dir._2)
          grid(robotPos.row)(robotPos.col) = '@' // Update robot position
        }
      }

      calculateGPS(grid)
    }

    def findRobot(grid: Array[Array[Char]]): Position = {
      for (row <- grid.indices; col <- grid(row).indices) {
        if (grid(row)(col) == '@') return Position(row, col)
      }
      throw new IllegalStateException("Robot not found")
    }

    def tryMoveRobot(
        grid: Array[Array[Char]],
        robotPos: Position,
        dir: (Int, Int)
    ): Boolean = {
      var newRow = robotPos.row + dir._1
      var newCol = robotPos.col + dir._2
      var boxes = List.empty[Position]

      // Follow the direction and collect all consecutive boxes
      while (grid(newRow)(newCol) == 'O') {
        boxes = boxes :+ Position(newRow, newCol)
        newRow += dir._1
        newCol += dir._2
      }

      // Check if the move is valid (empty space at the end of all boxes)
      if (grid(newRow)(newCol) == '.') {
        // Move boxes
        boxes.reverse.foreach { box =>
          grid(box.row + dir._1)(box.col + dir._2) = 'O'
          grid(box.row)(box.col) = '.'
        }
        true // Move is valid
      } else {
        false // Move is invalid
      }
    }

    def calculateGPS(grid: Array[Array[Char]]): Int = {
      grid.zipWithIndex.foldLeft(0) { case (sum, (row, rowIndex)) =>
        sum + row.zipWithIndex.collect { case ('O', colIndex) =>
          100 * rowIndex + colIndex
        }.sum
      }
    }

    def printGrid(grid: Array[Array[Char]]): Unit = {
      grid.foreach(row => println(row.mkString("")))
      println()
    }

    // Solve Part 1
    val (result1, time1) = timeExecution {
      // solve part 1 here
      val (grid, moves) =
        parseInput(inputFileName) // Replace with your file path
      println("Initial State:")
      printGrid(grid)

      val gpsSum = simulateMoves(grid, moves)

      println("Final State:")
      printGrid(grid)
      gpsSum
    }

    // Print the result
    println(
      f"The result for Day $day, part 1 is: $result1 (solved in ${time1 / 1e6}%.2f ms)"
    )

    // Solve part 2
    def expandWarehouse(grid: Array[Array[Char]]): Array[Array[Char]] = {
      grid.map { row =>
        row.flatMap {
          case '#'   => "##"
          case 'O'   => "[]"
          case '.'   => ".."
          case '@'   => "@."
          case other => other.toString
        }.toArray
      }
    }

    // Part 2 GPS calculation for wide boxes
    def calculateGPSPart2(grid: Array[Array[Char]]): Int = {
      grid.zipWithIndex.foldLeft(0) { case (sum, (row, rowIndex)) =>
        sum + row
          .sliding(2)
          .zipWithIndex
          .collect { case (Array('[', ']'), colIndex) =>
            100 * rowIndex + colIndex
          }
          .sum
      }
    }

    // Part 2 Simulation Logic for 2x1 boxes
    def simulateMovesPart2(grid: Array[Array[Char]], moves: String): Int = {
      // Find the robot's initial position
      var robotPos = findRobot(grid)

      // Process each move
      moves.foreach { m =>
        m match {
          case '>' =>
            robotPos = performMoveHorz(grid, 1, robotPos)
          case '<' =>
            robotPos = performMoveHorz(grid, -1, robotPos)
          case '^' =>
            robotPos = performMoveVert(grid, -1, robotPos)
          case 'v' =>
            robotPos = performMoveVert(grid, 1, robotPos)
          case _ => println(s"Unknown move: $m") // Debugging: unexpected move
        }

        // Debugging: Print the grid after each move
        //println(s"Move: $m")
        //printGrid(grid)
      }

      // Calculate the GPS coordinates of all boxes
      calculateGPSPart2(grid)
    }

    // Horizontal movement check
    def canMoveHorz(
        grid: Array[Array[Char]],
        dir: Int,
        pos: Position
    ): Boolean = {
      grid(pos.row)(pos.col + dir) match {
        case '#'       => false
        case '.'       => true
        case '[' | ']' => canMoveHorz(grid, dir, Position(pos.row, pos.col + dir))
        case _         => false
      }
    }

// Vertical movement check
    def canMoveVert(
        grid: Array[Array[Char]],
        dir: Int,
        pos: Position
    ): Boolean = {
      grid(pos.row + dir)(pos.col) match {
        case '#' => false
        case '.' => true
        case '[' =>
          canMoveVert(grid, dir, Position(pos.row + dir, pos.col)) && 
          canMoveVert(grid, dir, Position(pos.row + dir,pos.col+1))
        case ']' =>
          canMoveVert(grid, dir, Position(pos.row + dir, pos.col)) && 
          canMoveVert(grid, dir, Position(pos.row + dir,pos.col-1))
        case _ => false
      }
    }

// Horizontal movement execution
    def doMoveHorz(grid: Array[Array[Char]], dir: Int, pos: Position): Unit = {
      val temp = grid(pos.row)(pos.col)

      if (grid(pos.row)(pos.col + dir) != '.') {
        doMoveHorz(grid, dir, Position(pos.row, pos.col + dir))
      }

      grid(pos.row)(pos.col + dir) = temp
      grid(pos.row)(pos.col) = '.'
    }

// Vertical movement execution
    def doMoveVert(grid: Array[Array[Char]], dir: Int, pos: Position): Unit = {
      val temp = grid(pos.row)(pos.col)

      if (grid(pos.row + dir)(pos.col) == '[') {
        doMoveVert(grid, dir, Position(pos.row + dir, pos.col))
        doMoveVert(grid, dir, Position(pos.row + dir, pos.col + 1))
      } else if (grid(pos.row + dir)(pos.col) == ']') {
        doMoveVert(grid, dir, Position(pos.row + dir, pos.col))
        doMoveVert(grid, dir, Position(pos.row + dir, pos.col - 1))
      }

      grid(pos.row + dir)(pos.col) = temp
      grid(pos.row)(pos.col) = '.'
    }

    // Perform horizontal move
    def performMoveHorz(
        grid: Array[Array[Char]],
        dir: Int,
        pos: Position
    ): Position = {
      if (canMoveHorz(grid, dir, pos)) {
        doMoveHorz(grid, dir, pos)
        Position(pos.row, pos.col + dir) // Update the robot's position
      } else {
        pos // No movement
      }
    }

    // Perform vertical move
    def performMoveVert(
        grid: Array[Array[Char]],
        dir: Int,
        pos: Position
    ): Position = {
      if (canMoveVert(grid, dir, pos)) {
        doMoveVert(grid, dir, pos)
        Position(pos.row + dir, pos.col) // Update the robot's position
      } else {
        pos // No movement
      }
    }

    val (result2, time2) = timeExecution {
      // solve part 2 here
      val (grid, moves) =
        parseInput(inputFileName) // Replace with your file path
      val expandedGrid = expandWarehouse(grid.map(_.clone))
      println("Initial State (Part 2):")
      printGrid(expandedGrid)
      val gpsPos = simulateMovesPart2(expandedGrid, moves)
      println("Final State (Part 2):")
      printGrid(expandedGrid)
      gpsPos
    }

    // Print the result
    println(
      f"The result for Day $day, part 2 is: $result2 (solved in ${time2 / 1e6}%.2f ms)"
    )

  }

}
