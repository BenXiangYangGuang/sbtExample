package akkaStream.alpkafka.producer

import akka.Done
import akka.actor.ActorSystem
import akka.kafka.ProducerSettings
import akka.kafka.scaladsl.Producer
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Source
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.StringSerializer

import scala.concurrent.Future

/**
  * Author: fei2
  * Date:  18-7-18 下午2:23
  * Description:alp akka producer example
  * Refer To:
  */
object AkkaKafkaProducer extends App {

  val system = ActorSystem("akkaStream")
  implicit val ec = system.dispatcher
  //物化值
  implicit val materialer = ActorMaterializer.create(system)
  val config = system.settings.config.getConfig("akka.kafka.producer")
  //producerSettings 有两种创建方式1.通过程序代码 2.通过application.conf 配置文件
  val producerSettings =
    ProducerSettings(config,new StringSerializer,new StringSerializer)
    .withBootstrapServers("localhost:9092")

  val done: Future[Done] =
    Source(1 to 100)
        .map(_.toString)
        .map(value => new ProducerRecord[String,String]("topic1",value))
        .runWith(Producer.plainSink(producerSettings))
}
