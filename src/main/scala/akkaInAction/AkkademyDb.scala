package akkaInAction

import akka.actor.Actor
import akka.event.Logging

import scala.collection.mutable


/**
  * Author: fei2
  * Date:  19-3-20 下午7:38
  * Description:
  * Refer To:
  */
class AkkademyDb extends Actor{

  val map = new mutable.HashMap[String,Object]
  val log = Logging(context.system,this)


  override def receive = {
    case SetRequest(key,value) => {
      log.info("receiver SetRequest - key: {} value {}",key,value)
      map.put(key,value)
    }
    case o => log.info("received unknown message:{}",o)
  }
}
