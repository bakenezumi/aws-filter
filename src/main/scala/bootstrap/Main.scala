package bootstrap

import filter.HtmlFilter
import org.jsoup.{Connection, Jsoup}

object Main {

  def main(args: Array[String]): Unit = {

    // 初期化処理
    val runtime = System.getenv("AWS_LAMBDA_RUNTIME_API")

    def errorHandler(e: Exception): Unit = {
      val message =
        s"""
           |{
           |  "errorMessage": "${e.getMessage}",
           |  "errorType": "${e.getClass.getName}"
           |}
           |""".stripMargin
      Jsoup
        .connect(s"http://$runtime/2018-06-01/runtime/init/error")
        .method(Connection.Method.POST)
        .requestBody(message)
        .execute()
    }

    try {
      println(s"runtime: $runtime")
    } catch {
      case e: Exception => errorHandler(e)
    }

    val requestConnection = Jsoup
      .connect(s"http://$runtime/2018-06-01/runtime/invocation/next")
      .ignoreContentType(true)

    val filter: String => String = new HtmlFilter().proxy _

    // イベントループ
    while (true) {
      // イベントデータの取得
      val (body, requestId) = {
        val request = requestConnection.execute()
        (request.body(), request.header("lambda-runtime-aws-request-id"))
      }

      val response = filter(body)

      try {
        // レスポンスデータの返却
        Jsoup
          .connect(
            s"http://$runtime/2018-06-01/runtime/invocation/$requestId/response")
          .method(Connection.Method.POST)
          .requestBody(response)
          .execute()
      } catch {
        case e: Exception =>
          errorHandler(e)
      }
    }
  }
}
