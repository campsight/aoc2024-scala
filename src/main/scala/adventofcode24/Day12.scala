package adventofcode24

import scala.io.Source
import scala.collection.mutable
import java.io.File
import scala.math.BigInt
import scala.util.control.Breaks._

object Day12 {
  def timeExecution[T](block: => T): (T, Long) = {
    val start = System.nanoTime()
    val result = block
    val end = System.nanoTime()
    (result, end - start)
  }

  def main(args: Array[String]): Unit = {
    val year = 2024
    val day = 12
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
    // Parse the input into a 2D grid
    def parseMap(filePath: String): Array[Array[Char]] = {
      Source.fromFile(filePath).getLines().map(_.toArray).toArray
    }

    // Directions for up, down, left, right
    val directions = Seq((-1, 0), (1, 0), (0, -1), (0, 1))

    // Check if a position is within bounds
    def inBounds(grid: Array[Array[Char]], x: Int, y: Int): Boolean = {
      x >= 0 && x < grid.length && y >= 0 && y < grid(0).length
    }

    // Perform Flood Fill to calculate area and perimeter for a region
    def floodFill(
        grid: Array[Array[Char]],
        x: Int,
        y: Int,
        plant: Char,
        visited: mutable.Set[(Int, Int)]
    ): (Int, Int) = {
      val queue = mutable.Queue((x, y))
      visited.add((x, y))

      var area = 0
      var perimeter = 0

      while (queue.nonEmpty) {
        val (cx, cy) = queue.dequeue()
        area += 1

        for ((dx, dy) <- directions) {
          val nx = cx + dx
          val ny = cy + dy

          if (inBounds(grid, nx, ny)) {
            if (grid(nx)(ny) == plant && !visited.contains((nx, ny))) {
              visited.add((nx, ny))
              queue.enqueue((nx, ny))
            } else if (grid(nx)(ny) != plant) {
              perimeter += 1 // Edge of the region
            }
          } else {
            perimeter += 1 // Edge of the grid
          }
        }
      }

      (area, perimeter)
    }

    // Calculate total fencing cost
    def calculateFencingCost(grid: Array[Array[Char]]): Int = {
      val visited = mutable.Set[(Int, Int)]()
      var totalCost = 0

      for (x <- grid.indices; y <- grid(0).indices) {
        if (!visited.contains((x, y))) {
          val plant = grid(x)(y)
          val (area, perimeter) = floodFill(grid, x, y, plant, visited)
          // println(s"Found $plant with area $area and perimeter $perimeter")
          totalCost += area * perimeter
        }
      }

      totalCost
    }

    // Solve Part 1
    val (result1, time1) = timeExecution {
      // solve part 1 here
      val gardenMap = parseMap(inputFileName)
      calculateFencingCost(gardenMap)
    }

    // Print the result
    println(
      f"The result for Day $day, part 1 is: $result1 (solved in ${time1 / 1e6}%.2f ms)"
    )

    // Solve part 2
    def floodFillRegion(
        grid: Array[Array[Char]],
        x: Int,
        y: Int,
        plant: Char,
        visited: mutable.Set[(Int, Int)]
    ): Set[(Int, Int)] = {
      val queue = mutable.Queue((x, y))
      visited.add((x, y))

      val region = mutable.Set((x, y))

      while (queue.nonEmpty) {
        val (cx, cy) = queue.dequeue()

        for ((dx, dy) <- Seq((-1, 0), (1, 0), (0, -1), (0, 1))) { // Up, Down, Left, Right
          val nx = cx + dx
          val ny = cy + dy

          if (
            inBounds(grid, nx, ny) && grid(nx)(ny) == plant && !visited
              .contains((nx, ny))
          ) {
            visited.add((nx, ny))
            queue.enqueue((nx, ny))
            region.add((nx, ny))
          }
        }
      }

      region.toSet
    }

    def countSides(grid: Array[Array[Char]], region: Set[(Int, Int)]): Int = {
      val directions =
        Seq((-1, 0), (1, 0), (0, -1), (0, 1)) // Up, Down, Left, Right
      val uniqueSides =
        mutable.Set[(Int, Int, Int, Int)]() // Canonical corner with direction

      for ((cy, cx) <- region) {
        for ((dy, dx) <- directions) {
          val ny = cy + dy
          val nx = cx + dx

          // If neighbor is outside the region, it's a potential boundary
          if (!region.contains((ny, nx))) {
            // Canonical corner detection
            var cyCanonical = cy
            var cxCanonical = cx
            while (
              region.contains(
                (cyCanonical + dx, cxCanonical + dy)
              ) && // Continue along the boundary
              !region.contains(
                (cyCanonical + dy, cxCanonical + dx)
              ) // Stop if direction changes
            ) {
              cyCanonical += dx
              cxCanonical += dy
            }

            // Add to unique sides
            if (!uniqueSides.contains((cyCanonical, cxCanonical, dy, dx))) {
              uniqueSides.add((cyCanonical, cxCanonical, dy, dx))
            }
          }
        }
      }

      uniqueSides.size
    }

    // Calculate total fencing cost using sides
    def calculateTotalFencingCost(grid: Array[Array[Char]]): Int = {
      val visited = mutable.Set[(Int, Int)]()
      var totalCost = 0

      for (x <- grid.indices; y <- grid(0).indices) {
        if (!visited.contains((x, y))) {
          val plant = grid(x)(y)
          val region = floodFillRegion(grid, x, y, plant, visited)
          val sides = countSides(grid, region)
          // println(s"plant: $plant, region: $region; sides: $sides")
          totalCost = totalCost + (region.size * sides)
        }
      }

      totalCost
    }

    val (result2, time2) = timeExecution {
      // solve part 2 here
      calculateTotalFencingCost(parseMap(inputFileName))
    }

    // Print the result
    println(
      f"The result for Day $day, part 2 is: $result2 (solved in ${time2 / 1e6}%.2f ms)"
    )

  }

}
