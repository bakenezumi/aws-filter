import org.scalatest._
import filter._

class HtmlFilterSpec extends FlatSpec with Matchers with HtmlFilterBase{
  import io.Source

  override def fromURL(url: String, encode: String = "UTF-8") = Source.fromFile(url, encode)

  "HtmlFilter.proxy" should "ok" in {
    val url = "src/test/scala/test.html"
    val ret = proxy(url)
    println(ret)
    ret != null
  }

  "HtmlFilter.absoluteFilter" should "ok" in {
    val url = "http://qiita.com/bakenezumi/items/4026bbd4117be598a251"
    val ret = absoluteFilter(url)
    println(ret)
    ret != null
  }

  "HtmlFilter.galgoFilter" should "ok" in {
    val url = "http://qiita.com/bakenezumi/items/4026bbd4117be598a251"
    val ret = galgoFilter(url)
    println(ret)
    ret != null
  }
  
}
