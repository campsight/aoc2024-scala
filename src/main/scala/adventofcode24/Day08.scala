
package adventofcode24

import scala.io.Source
import scala.collection.mutable
import java.io.File
import scala.math.BigInt
import scala.util.control.Breaks._

object Day08 {
  def timeExecution[T](block: => T): (T, Long) = {
    val start = System.nanoTime()
    val result = block
    val end = System.nanoTime()
    (result, end - start)
  }

  def main(args: Array[String]): Unit = {
    val year = 2024
    val day = 8
    val inputFileName = f"${year}_day${day}%02d_input-test.txt"

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
    val input = Source.fromFile(inputFileName).getLines().toArray

    // Prep
    // Parse antenna positions and their frequencies
    case class Position(x: Int, y: Int) {
      def +(that: Position): Position = Position(this.x + that.x, this.y + that.y)
      def within(limits: (Int, Int)): Boolean =
        x >= 0 && y >= 0 && x < limits._1 && y < limits._2
    }
    val antennas = mutable.Map[Char, mutable.ListBuffer[Position]]()

    for (y <- input.indices; x <- input(y).indices) {
      val char = input(y)(x)
      if (char.isLetterOrDigit) {
        antennas.getOrElseUpdate(char, mutable.ListBuffer()) += Position(x, y)
      }
    }

    // Calculate antinodes
    val antinodes = mutable.Set[Position]()

    // Helper function to check if antinode is within map bounds
    def antinodesWithinBounds(input: Array[String], pos: Position): Boolean = {
      pos.x >= 0 && pos.x < input(0).length && pos.y >= 0 && pos.y < input.length
    }
    
    // Solve Part 1
    val (result1, time1) = timeExecution {
        // solve part 1 here
        // println(s"Antennas: $antennas")
        antennas.foreach { case (frequency, positions) =>
          for {
            i <- positions.indices
            j <- i + 1 until positions.size
          } {
            val p1 = positions(i)
            val p2 = positions(j)

            // Calculate antinodes
            val antinode1 = Position(2 * p1.x - p2.x, 2 * p1.y - p2.y)
            val antinode2 = Position(2 * p2.x - p1.x, 2 * p2.y - p1.y)

            // Add antinodes within bounds
            if (antinodesWithinBounds(input, antinode1)) {
              antinodes += antinode1
              println(s"Frequency $frequency - node $i with $j : added node $antinode1")
            }
            if (antinodesWithinBounds(input, antinode2)) {
              antinodes += antinode2
              println(s"Frequency $frequency - node $i with $j :  added node $antinode2")
            }
          }
          
        }
        antinodes.size
    }

    // Print the result
    println(f"The result for Day $day, part 1 is: $result1 (solved in ${time1 / 1e6}%.2f ms)") 

    // Solve part 2
    // Set to store unique antinode coordinates
    val antinodeCoords = mutable.Set[Position]()
    val mapLimits = (input(0).length, input.length)

    val (result2, time2) = timeExecution {
        antennas.foreach { case (_, antennaCoords) =>
          for {
            coord1 <- antennaCoords
            coord2 <- antennaCoords if coord1 != coord2
          } {
            // Calculate direction vector (dx, dy)
            val d = Position(coord2.x - coord1.x, coord2.y - coord1.y)
            var antinode = coord1

            // Generate antinodes along the line
            breakable {
              while (true) {
                antinode += d
                if (!antinode.within(mapLimits)) {
                  // Stop if the antinode goes out of bounds
                  break
                }
                antinodeCoords += antinode
              }
            }
          }
        }
        antinodeCoords.size
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

