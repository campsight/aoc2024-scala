package adventofcode24

import scala.io.Source
import scala.collection.mutable
import java.io.File
import scala.math.BigInt
import scala.util.control.Breaks._

object Day10 {
  def timeExecution[T](block: => T): (T, Long) = {
    val start = System.nanoTime()
    val result = block
    val end = System.nanoTime()
    (result, end - start)
  }

  def main(args: Array[String]): Unit = {
    val year = 2024
    val day = 10
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

    // Read from the input file
    // Parse the topographic map
    def parseMap(filePath: String): Array[Array[Int]] = {
      Source
        .fromFile(filePath)
        .getLines()
        .map(_.map(_.asDigit).toArray)
        .toArray
    }

    // Perform BFS to calculate the score for a single trailhead
    def bfs(map: Array[Array[Int]], start: (Int, Int)): Int = {
      val rows = map.length
      val cols = map(0).length
      val directions =
        Seq((0, 1), (1, 0), (0, -1), (-1, 0)) // Up, down, left, right
      val visited = mutable.Set[(Int, Int)]()
      val queue = mutable.Queue[(Int, Int)]()
      val reachableNines = mutable.Set[(Int, Int)]()

      queue.enqueue(start)
      visited.add(start)

      while (queue.nonEmpty) {
        val (x, y) = queue.dequeue()
        val currentHeight = map(x)(y)

        for ((dx, dy) <- directions) {
          val nx = x + dx
          val ny = y + dy

          if (
            nx >= 0 && nx < rows && ny >= 0 && ny < cols && !visited.contains(
              (nx, ny)
            )
          ) {
            val nextHeight = map(nx)(ny)
            if (nextHeight == currentHeight + 1) {
              queue.enqueue((nx, ny))
              visited.add((nx, ny))
              if (nextHeight == 9) {
                reachableNines.add((nx, ny))
              }
            }
          }
        }
      }

      reachableNines.size
    }

    // Calculate the total score of all trailheads
    def calculateTotalScore(map: Array[Array[Int]]): Int = {
      val rows = map.length
      val cols = map(0).length
      var totalScore = 0

      for (x <- 0 until rows; y <- 0 until cols if map(x)(y) == 0) {
        totalScore += bfs(map, (x, y))
      }

      totalScore
    }

    val map = parseMap(inputFileName)

    // Solve Part 1
    val (result1, time1) = timeExecution {
      // solve part 1 here
      calculateTotalScore(map)
    }

    // Print the result
    println(
      f"The result for Day $day, part 1 is: $result1 (solved in ${time1 / 1e6}%.2f ms)"
    )

    // Solve part 2
    def dfs(
        map: Array[Array[Int]],
        start: (Int, Int),
        visited: mutable.Set[(Int, Int)]
    ): Int = {
      val rows = map.length
      val cols = map(0).length
      val directions =
        Seq((0, 1), (1, 0), (0, -1), (-1, 0)) // Up, down, left, right

      val stack = mutable.Stack[(Int, Int, mutable.Set[(Int, Int)])]()
      stack.push((start._1, start._2, mutable.Set(start)))

      var trails = 0

      while (stack.nonEmpty) {
        val (x, y, pathVisited) = stack.pop()
        val currentHeight = map(x)(y)

        var trailEnded = true

        for ((dx, dy) <- directions) {
          val nx = x + dx
          val ny = y + dy

          if (
            nx >= 0 && nx < rows && ny >= 0 && ny < cols && !pathVisited
              .contains((nx, ny))
          ) {
            val nextHeight = map(nx)(ny)
            if (nextHeight == currentHeight + 1) {
              trailEnded = false
              val newPathVisited = pathVisited.clone()
              newPathVisited.add((nx, ny))
              stack.push((nx, ny, newPathVisited))
            }
          }
        }

        if (trailEnded && currentHeight == 9) {
          trails += 1
        }
      }

      trails
    }

    def calculateTotalRating(map: Array[Array[Int]]): Int = {
      val rows = map.length
      val cols = map(0).length
      var totalRating = 0

      for (x <- 0 until rows; y <- 0 until cols if map(x)(y) == 0) {
        totalRating += dfs(map, (x, y), mutable.Set())
      }

      totalRating
    }

    val (result2, time2) = timeExecution {
      // solve part 2 here
      calculateTotalRating(map)
    }

    // Print the result
    println(
      f"The result for Day $day, part 2 is: $result2 (solved in ${time2 / 1e6}%.2f ms)"
    )

  }

}
