import akka.actor.{ActorSystem, Props}
import akka.util.Timeout
import akkaInAction.ScalaPongActor
import org.scalatest.{FunSpecLike, Matchers}
import akka.pattern.ask

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._

/**
  * Author: fei2
  * Date:  19-3-25 下午7:12
  * Description:
  * Refer To:
  */
class PongActorTest extends FunSpecLike with Matchers{
  val  system = ActorSystem()
  implicit val  timeout = Timeout(5 seconds)
  val pongActor = system.actorOf(Props(classOf[ScalaPongActor]))
  describe("Pong actor"){
    it("should respond with Pong"){
      val future = pongActor ? "Ping" //uses the implict thimeout
      //真正接收到结果之前阻塞线程
      val result = Await.result(future.mapTo[String],1 second)
      assert(result == "Pong")
    }
    it("should fial on unknown message"){
      val future = pongActor ? "unknown"
      intercept[Exception]{
        Await.result(future.mapTo[String],1 second)
      }

    }
  }
  def askPong(message: String):Future[String] = (pongActor ? message).mapTo[String]

  describe("FutureExamples"){
    import scala.concurrent.ExecutionContext.Implicits.global
    it("should print to console"){
      (pongActor ? "Ping").onSuccess({
        case x: String => println("replied with: " +x)
      })
      Thread.sleep(100)
    }
  }
  //比较复杂,使用下面链式调用
  val futureFuture: Future[Future[String]] =
    askPong("Ping").map(x => {
      askPong(x)
    })

  val f: Future[String] = askPong("Ping").flatMap(x => askPong("Ping"))

  //处理失败情况
  askPong("causeError").onFailure({
    case e: Exception => println("Got a exception")
  })
  //从失败中恢复
  val f2 = askPong("causeError").recover({
    case t: Exception => "default"
  })
  //异步地从失败中恢复
  //1.重试某个操作
  //2.没有缓存命中时,需要调用另一个操作的时间
  askPong("causeError").recoverWith({
    case t: Exception => askPong("Ping")
  })
}
