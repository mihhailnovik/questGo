package fm.wars.goquest.client.gtp

import java.io.{BufferedReader, FileWriter, InputStream, InputStreamReader, PrintWriter}
import java.util.concurrent.{ArrayBlockingQueue, TimeUnit}
import java.util.function.Consumer

import com.typesafe.scalalogging.LazyLogging
import fm.wars.goquest.client.config.WarsFmConfig
import fm.wars.goquest.client.gtp.Commands.{CLEAR_BOARD, GENMOVE, PLAY, TIME_SETTINGS}
import fm.wars.goquest.client.model.Domain.Player.Player

import scala.annotation.tailrec
import scala.collection.mutable
import scala.sys.process.{Process, ProcessIO}
import scala.util.{Failure, Success, Try}

class GtpLeela extends GtpGameIO with LazyLogging {
  val QUEUE_SIZE = 10
  private val outputMessages = new ArrayBlockingQueue[String](QUEUE_SIZE)
  private val inputMessages = new ArrayBlockingQueue[String](QUEUE_SIZE)

  startLeela()

  override def genMove(playerColor: Player): TimedGtpResult = {
    val genMovePattern = "= [\\w][\\d]".r
    requestTimed(s"$GENMOVE ${playerColor.toString.toLowerCase}", result =>
      genMovePattern.findFirstMatchIn(result).isDefined || result.contains("resign") || result.contains("pass"))
  }

  override def playMove(player: Player, coordinate: GtpAction): GtpResult =
    request(s"$PLAY ${player.toString.toLowerCase} ${coordinate.actionCommand}")

  override def clearBoard(): GtpResult = request(CLEAR_BOARD)

  override def komi(komi: Int): GtpResult = request(s"${Commands.KOMI} $komi")

  override def timeSettings(mainTime: Int, byoYomiTime: Int, byoYomiStones: Int): GtpResult =
    request(s"$TIME_SETTINGS $mainTime $byoYomiTime $byoYomiStones")

  override def name(): GtpResult = request(Commands.NAME)

  private def request(command: String, accept: String => Boolean = _.contains("= ")) = {
    sendCommand(command)
    waitResponse(accept, 30000).toEither
  }

  private def requestTimed(command: String, accept: String => Boolean = _.contains("= ")) = {
    val currTime = System.currentTimeMillis()
    sendCommand(command)
    val response = waitResponse(accept, 30000).toEither
    TimedGtpResult(System.currentTimeMillis() - currTime, response)
  }

  override def showBoard(): GtpResult = ???

  import java.io.FileWriter



  private def startLeela() = {
    val fw = new FileWriter("/tmp/errorLog.txt", true) // TODO do it through log4j
    logger.info("Starting leela...")
    val process = Process(getCommand)
    val io = new ProcessIO(leelaWriter, leelaReader,
      error => { // this is mostly leela debug log... might just ignore or
        val buffer = new BufferedReader(new InputStreamReader(error))
        buffer.lines().forEach({ line =>
          fw.write(line)
          fw.write("\n")
          fw.flush()
        })
      }
    ) // XXX maybe better way for error stream ?
    process.run(io)
    this.name() match {
      case Right(_) => logger.info("Leela started")
      case Left(er) => throw new RuntimeException(s"cannot start leela [$er]")
    }

  }

  private def sendCommand(command: String): Unit = inputMessages.put(command)

  private def isError(response : String): Boolean = response.startsWith("?")

  private def getCommand : String = {
    val command = new mutable.StringBuilder()
    command.append(WarsFmConfig.leelaPath).append(" ")
      .append("--gtp").append(" ")
      .append("--weights").append(" ").append(WarsFmConfig.leelaWeights)
    for (seed <- WarsFmConfig.leelaSeed) {
      command.append(" ")
        .append("--seed").append(" ").append(seed)
    }
    val result = command.toString
    logger.debug(s"Command = $result")
    result
  }

  @tailrec
  private def waitResponse(accept: String => Boolean, maxDuration : Long): Try[String] = {
    val currTime = System.currentTimeMillis()
    Option(outputMessages.poll(maxDuration, TimeUnit.MILLISECONDS)) match {
      case Some(response) if accept(response) =>
        Success(response.replace("= ", ""))
      case Some(response) if isError(response) =>
        Failure(new RuntimeException(s"Failed to process command ${response.substring(1)}"))
      case Some(_) =>
        val finished = System.currentTimeMillis()
        waitResponse(accept, maxDuration - (finished - currTime))
      case None => Failure(new RuntimeException("Timeout waiting response from Leela"))
    }
  }


  private def leelaWriter(output: java.io.OutputStream) = {
    val pw = new PrintWriter(output)
    while (true) {
      val command = inputMessages.take
      logger.info(s"GameController -> Leela :$command")
      pw.println(command)
      pw.flush()
    }
  }

  private def leelaReader(input: InputStream) = {
    val buffer = new BufferedReader(new InputStreamReader(input))
    buffer.lines().forEach({ line =>
      logger.info(s"Leela -> GameController $line")
      Option(line) match {
        case Some(result) => outputMessages.put(result)
        case None => logger.warn("Reader is finished")
      }
    })
  }
}
