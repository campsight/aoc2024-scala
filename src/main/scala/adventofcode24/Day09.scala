package adventofcode24

import scala.io.Source
import scala.collection.mutable
import java.io.File
import scala.math.BigInt
import scala.util.control.Breaks._

object Day09 {
  def timeExecution[T](block: => T): (T, Long) = {
    val start = System.nanoTime()
    val result = block
    val end = System.nanoTime()
    (result, end - start)
  }

  def main(args: Array[String]): Unit = {
    val year = 2024
    val day = 9
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

    // Read the grid from the input file
    val input = Source.fromFile(inputFileName).getLines().next()

    // Prep
    case class Segment(value: String, position: Int, length: Int)

    // Parse the disk map
    def parseDiskMap(diskMap: String): mutable.ListBuffer[Segment] = {
      val segments = mutable.ListBuffer[Segment]()
      var pos = 0
      var fileId = 0

      // Ensure the input string has an even length
      val paddedDiskMap =
        if (diskMap.length % 2 == 1) diskMap + "0" else diskMap

      for (i <- paddedDiskMap.indices by 2) {
        val fileLength = paddedDiskMap(i).asDigit
        val freeSpaceLength = paddedDiskMap(i + 1).asDigit

        if (fileLength > 0) {
          segments += Segment(fileId.toString, pos, fileLength)
          fileId += 1 // Increment file ID for the next file
        }
        pos += fileLength
        if (freeSpaceLength > 0) {
          segments += Segment(".", pos, freeSpaceLength)
        }
        pos += freeSpaceLength
      }

      segments
    }

    // Simulate the compaction process
    def compactDisk(
        segments: mutable.ListBuffer[Segment]
    ): mutable.ListBuffer[Segment] = {
      var lastFileIndex =
        segments.lastIndexWhere(_.value != ".") // Updated comparison
      var freeSpaceIndex =
        segments.indexWhere(_.value == ".") // Updated comparison

      while (freeSpaceIndex >= 0 && lastFileIndex > freeSpaceIndex) {
        val freeSpace = segments(freeSpaceIndex)
        val lastFile = segments(lastFileIndex)

        if (freeSpace.length >= lastFile.length) {
          // Move entire file to the free space
          segments(freeSpaceIndex) =
            Segment(lastFile.value, freeSpace.position, lastFile.length)
          if (freeSpace.length > lastFile.length) {
            // Update the remaining free space
            segments.insert(
              freeSpaceIndex + 1,
              Segment(
                ".",
                freeSpace.position + lastFile.length,
                freeSpace.length - lastFile.length
              )
            )
            // Remove the last file segment
            segments.remove(lastFileIndex + 1)
          } else {
            // Remove the last file segment
            segments.remove(lastFileIndex)
          }

        } else {
          // Partially move the file to fill the free space
          segments(freeSpaceIndex) =
            Segment(lastFile.value, freeSpace.position, freeSpace.length)
          segments(lastFileIndex) = Segment(
            lastFile.value,
            lastFile.position,
            lastFile.length - freeSpace.length
          )
        }

        // Recalculate indices to account for list changes
        lastFileIndex = segments.lastIndexWhere(_.value != ".")
        freeSpaceIndex = segments.indexWhere(_.value == ".")
      }

      segments
    }

    // Calculate the checksum
    def calculateChecksum(segments: mutable.ListBuffer[Segment]): BigInt = {
      segments.filter(_.value != ".").foldLeft(BigInt(0)) { (sum, segment) =>
        val fileId = BigInt(segment.value.toInt) // Convert file ID to BigInt
        val positions = BigInt(segment.position) until BigInt(
          segment.position + segment.length
        )
        sum + positions.map(_ * fileId).sum
      }
    }

    // Solve Part 1
    val (result1, time1) = timeExecution {
      // solve part 1 here
      val segments = parseDiskMap(input)
      val compactedSegments = compactDisk(segments)
      val checksum = calculateChecksum(compactedSegments)
      checksum
    }

    // Print the result
    println(
      f"The result for Day $day, part 1 is: $result1 (solved in ${time1 / 1e6}%.2f ms)"
    )

    // Solve part 2
    def compactDiskWholeFiles(
        segments: mutable.ListBuffer[Segment]
    ): mutable.ListBuffer[Segment] = {
      // Get all file segments sorted by file ID in descending order
      val files = segments
        .filter(_.value != ".")
        .sortBy(_.value.toInt)(Ordering[Int].reverse)

      for (file <- files) {
        // Find the first free space large enough to fit the file
        val freeSpaceIndex = segments.indexWhere(segment =>
          segment.value == "." && segment.length >= file.length && segment.position < file.position
        )

        if (freeSpaceIndex >= 0) {
          val freeSpace = segments(freeSpaceIndex)

          // Move the file to the free space
          segments(freeSpaceIndex) =
            Segment(file.value, freeSpace.position, file.length)

          // Update remaining free space at the destination if any
          if (freeSpace.length > file.length) {
            segments.insert(
              freeSpaceIndex + 1,
              Segment(
                ".",
                freeSpace.position + file.length,
                freeSpace.length - file.length
              )
            )
          }

          // Replace the original file position with free space
          segments(segments.indexOf(file)) =
            Segment(".", file.position, file.length)
        }
      }

      segments
    }

    val input2 = Source.fromFile(inputFileName).getLines().next()

    val (result2, time2) = timeExecution {
      // solve part 2 here
      val segments = parseDiskMap(input2)
      // println(segments)
      val compactedSegments = compactDiskWholeFiles(segments)
      // println(compactedSegments)
      val checksum = calculateChecksum(compactedSegments)
      checksum
    }

    // Print the result
    println(
      f"The result for Day $day, part 2 is: $result2 (solved in ${time2 / 1e6}%.2f ms)"
    )

  }

}
