
import akka.actor.ActorSystem
import akka.testkit.TestActorRef
import akka.util.Timeout
import akkaInAction.{AkkademyDb, SetRequest}
import org.scalatest.{FunSpecLike, Matchers}
import scala.concurrent.duration._

/**
  * Author: fei2
  * Date:  19-3-20 下午7:48
  * Description:
  * Refer To:
  */
class AkkademyDbSpec extends FunSpecLike with Matchers{
  implicit val system = ActorSystem()
  implicit val timeout = Timeout(5 seconds)

  describe("akkademyDb") {
    describe("given SetRequest") {
      it("should place key/value into map"){
        val actorRef = TestActorRef(new AkkademyDb)
        actorRef ! SetRequest("key","value")
        val akkademyDb = actorRef.underlyingActor
        akkademyDb.map.get("key") should equal(Some("value"))
      }
    }
  }
}
