package adventofcode24

import sttp.client3._
import java.io.{File, PrintWriter}

object AOCHelper {
  def downloadInput(year: Int, day: Int, outputFileName: String): Unit = {
    val sessionKey = "53616c7465645f5f0d51daae474dbb3b8099cab33097f8bd1fe46a203cd222203445c1369501813ad05ba9aeefbd1a7847a06f17541bd2d98813caad14f84123"
    val url = s"https://adventofcode.com/$year/day/$day/input"
    val headers = Map("Cookie" -> s"session=$sessionKey")
    val backend = HttpURLConnectionBackend()

    val request = basicRequest
      .get(uri"$url")
      .headers(headers)

    request.send(backend) match {
      case response if response.code.isSuccess =>
        saveToFile(outputFileName, response.body.getOrElse(""))
        println(s"Input for Day $day saved to $outputFileName")
      case response =>
        println(s"Failed to fetch input: ${response.code} - ${response.body}")
    }
  }

  private def saveToFile(fileName: String, content: String): Unit = {
    val file = new File(fileName)
    val writer = new PrintWriter(file)
    try {
      writer.write(content)
    } finally {
      writer.close()
    }
  }
}
