package akkaStream

/**
  * Author: fei2
  * Date:  18-7-17 下午7:07
  * Description:
  * Refer To:
  */
object Main extends App {
  val ages = Seq(42, 75, 29, 230,78)
  println(s"The oldest person is ${ages.max}")

  val a = Seq("Symbol","Name","LastSale","MarketCap","IPOyear","Sector","industry","Summary Quote")
  a.foreach(x => {print(x.getBytes().toList)})
}