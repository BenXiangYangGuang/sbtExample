package akkaStream

/**
  * Author: fei2
  * Date:  18-7-30 上午10:44
  * Description:每30秒通过http请求拉取csv 数据转化为 json 发送到 kafka
  * Refer To:https://developer.lightbend.com/docs/alpakka/current/examples/csv-samples.html
  */
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.model.headers.Accept
import akka.http.scaladsl.model.{HttpRequest, HttpResponse, MediaRanges}
import akka.stream.alpakka.csv.scaladsl.{CsvParsing, CsvToMap}
import akka.stream.scaladsl.{Keep, Sink, Source}
import akka.util.ByteString
import spray.json.{DefaultJsonProtocol, JsValue, JsonWriter}

import scala.concurrent.duration.DurationInt

object FetchHttpEvery30SecondsAndConvertCsvToJson
  extends ActorSystemAvailable
    with App
    with DefaultJsonProtocol{
  //TODO 需要本地kafka服务
  val httpRequest = HttpRequest(uri = "https://www.nasdaq.com/screening/companies-by-name.aspx?exchange=NASDAQ&render=download")
    .withHeaders(Accept(MediaRanges.`text/*`))
  //抽取请求中的数据
  def extractEntityData(response: HttpResponse) :Source[ByteString, _] =
    response match {

      case HttpResponse(OK, _, entity, _) => {
              println(entity.dataBytes)
              entity.dataBytes   //返回一个Source[ByteString,Any]数据源
            }
      case notOkResponse =>
        Source.failed(new  RuntimeException(s"illegal response $notOkResponse"))
  }
  //判断length!=0  然后 byteString => String
  def cleanseCsvData(csvData:Map[String,ByteString]):Map[String, String] =
    csvData
      .filterNot { case (key, _) => key.isEmpty }
      .mapValues(_.utf8String)

  def toJson(map: Map[String, String])(
            implicit jsWriter: JsonWriter[Map[String,String]]): JsValue = jsWriter.write(map)

    Source
      .tick(1.seconds,30.seconds,httpRequest) //一秒钟初始化一个发送请求，30秒发送一次；
    .mapAsync(1)(Http().singleRequest(_)) // 返回一个Future[HttResponse]
    .flatMapConcat(extractEntityData)   //抽取请求返回体中的数据流
      //List(ByteString(73, 83, 84, 82), ByteString(73, 110, 118, 101, 115, 116, 97, 114, 32, 72, 111, 108, 100, 105, 110, 103, 32, 67, 111, 114, 112, 111, 114, 97, 116, 105, 111, 110), ByteString(50, 55, 46, 50), ByteString(36, 50, 54, 48, 46, 51, 52, 77), ByteString(50, 48, 49, 52), ByteString(70, 105, 110, 97, 110, 99, 101), ByteString(77, 97, 106, 111, 114, 32, 66, 97, 110, 107, 115), ByteString(104, 116, 116, 112, 115, 58, 47, 47, 119, 119, 119, 46, 110, 97, 115, 100, 97, 113, 46, 99, 111, 109, 47, 115, 121, 109, 98, 111, 108, 47, 105, 115, 116, 114), ByteString())
      .via(CsvParsing.lineScanner())  //将每一行转换成为成为list[ByteString]中的一个元素
      //Map( -> ByteString(), Sector -> ByteString(72, 101, 97, 108, 116, 104, 32, 67, 97, 114, 101), Name -> ByteString(76, 105, 102, 101, 80, 111, 105, 110, 116, 32, 72, 101, 97, 108, 116, 104, 44, 32, 73, 110, 99, 46), industry -> ByteString(72, 111, 115, 112, 105, 116, 97, 108, 47, 78, 117, 114, 115, 105, 110, 103, 32, 77, 97, 110, 97, 103, 101, 109, 101, 110, 116), Symbol -> ByteString(76, 80, 78, 84), IPOyear -> ByteString(110, 47, 97), LastSale -> ByteString(54, 52, 46, 52, 50, 53), Summary Quote -> ByteString(104, 116, 116, 112, 115, 58, 47, 47, 119, 119, 119, 46, 110, 97, 115, 100, 97, 113, 46, 99, 111, 109, 47, 115, 121, 109, 98, 111, 108, 47, 108, 112, 110, 116), MarketCap -> ByteString(36, 50, 46, 52, 57, 66))
      .via(CsvToMap.toMap())  //用流的第一行作为csv格式转化为json格式的key
      .map(cleanseCsvData)  //去除为空的key;(-> ByteString()).一条记录都在一个map里面。
      //{"Sector":"n/a","Name":"WisdomTree Emerging Markets Corporate Bond Fund","industry":"n/a","Symbol":"EMCB","IPOyear":"n/a","LastSale":"68.61","Summary Quote":"https://www.nasdaq.com/symbol/emcb","MarketCap":"$41.17M"}
      .map(toJson)
      //{"Sector":"Health Care","Name":"Zealand Pharma A/S","industry":"Major Pharmaceuticals","Symbol":"ZEAL","IPOyear":"2017","LastSale":"14.77","Summary Quote":"https://www.nasdaq.com/symbol/zeal","MarketCap":"$453.25M"}
      .map(_.compactPrint) //JsValue to string
      .map(elem =>
        println(elem))
      .runWith(Sink.ignore)


  wait(1.hour)
  terminateActorSystem()

}
