package akkaInAction

import akka.actor.{Actor, Status}

import scala.concurrent.Future

/**
  * Author: fei2
  * Date:  19-3-25 下午7:18
  * Description:
  * Refer To:
  */
class ScalaPongActor extends Actor{
  override def receive: Receive = {
    case "Ping" => sender() ! "Pong"
    case _ => sender() ! Status.Failure(new Exception("unknown message"))
  }
}
