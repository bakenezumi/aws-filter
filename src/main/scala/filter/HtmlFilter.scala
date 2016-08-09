package filter

trait HtmlFilterBase {
  import io.Source

  // テスト時はオーバーライドできるように（ファイルからとかプロキシ使うときとか）
  def fromURL(url: String, encode: String = "UTF-8") = Source.fromURL(url, encode)
  def fromGzip(url: String, encode:String = "UTF-8" ) = {
    import java.net.URL
    import java.io.BufferedInputStream
    import java.util.zip.GZIPInputStream
    Source.fromInputStream(
      new GZIPInputStream(
        new BufferedInputStream(
           new URL(url).openStream()
        )
      ), encode)
  }
  def proxy(url: String) = {
    try {
      fromURL(url).mkString
    } catch {
      case _: java.nio.charset.MalformedInputException => fromGzip(url).mkString
      case e => throw e
    }
  }

  val proxyUrl = "./proxy?url="
  val cssProxyUrl = "./css-proxy?url="
  def absoluteFilter(url: String) = {
    import scala.collection.JavaConverters._
    import org.jsoup.Jsoup

    val doc = Jsoup.connect(url).get
    // クロスサイト回避のためにproxyを介す
    for (e <- doc.select("link").asScala) if (e.attr("href") != "") e.attr("href", cssProxyUrl + e.absUrl("href"))
    // クロスサイト回避のためにproxyを介す
    for (e <- doc.select("script").asScala) if (e.attr("src") != "") e.attr("src", proxyUrl + e.absUrl("src"))
    for (e <- doc.select("a").asScala) e.attr("href", e.absUrl("href"))
    for (e <- doc.select("img").asScala) e.attr("src", e.absUrl("src"))
    doc.toString
  }

  val galMap = Map(
    ('あ' -> "ぁ"), ('い' -> "ﾚヽ"), ('う' -> "ぅ"), ('え' -> "ぇ"), ('お' -> "ぉ"),
    ('か' -> "ｶゝ"), ('き' -> "(ｷ"), ('く' -> "＜"), ('け' -> "ﾚﾅ"), ('こ' -> "〓"),
    ('さ' -> "､ﾅ"), ('し' -> "ι"), ('す' -> "￡"), ('せ' -> "世"), ('そ' -> "ξ"),
    ('た' -> "ﾅﾆ"), ('ち' -> "干"), ('つ' -> "⊃"), ('て' -> "τ"), ('と' -> "ー⊂"),
    ('な' -> "ﾅょ"), ('に' -> "(ﾆ"), ('ぬ' -> "ゐ"), ('ね' -> "ね"), ('の' -> "＠"),
    ('は' -> ""), ('ひ' -> "ひ"), ('ふ' -> "､ζ､"), ('へ' -> "∧"), ('ほ' -> "ﾚま"),
    ('ま' -> "ま"), ('み' -> "彡"), ('む' -> "￡ヽ"), ('め' -> "×"), ('も' -> "м○"),
    ('や' -> "ゃ"), ('ゆ' -> "ゅ"), ('よ' -> "ょ"),
    ('ら' -> "ζ"), ('り' -> "L|"), ('る' -> "ゑ"), ('れ' -> "яё"), ('ろ' -> "з"),
    ('わ' -> "ゎ"), ('を' -> "ぉ"),('ん' -> "ω"),
    ('が' -> "ｶゞ"), ('ぎ' -> "(ｷ″"), ('ぐ' -> "＜″"), ('げ' -> "ﾚﾅ″"), ('ご' -> "ご"),
    ('ざ' -> "､ﾅ″"), ('じ' -> "ι″"), ('ず' -> "ず"), ('ぜ' -> "世″"), ('ぞ' -> "ξ″"),
    ('だ' -> "ﾅﾆ″"), ('ぢ' -> "ち"), ('づ' -> "⊃″"), ('で' -> "τ″"), ('ど' -> "ー⊂″"),
    ('ば' -> "l￡″"), ('び' -> "ひ″"), ('ぶ' -> "､ζ､″"), ('べ' -> "∧″"), ('ぼ' -> "ﾚま″"),
    ('ぱ' -> "l￡°"), ('ぴ' -> "ひ°"), ('ぷ' -> "､ζ､°"), ('ぺ' -> "∧°"), ('ぽ' -> "ﾚま°"),
    ('ア' -> "了"), ('イ' -> "ｨ"), ('ウ' -> "ｩ"), ('エ' -> "工"), ('オ' -> "才"),
    ('カ' -> "ヵ"), ('キ' -> "≠"), ('ク' -> "勹"), ('ケ' -> "ヶ"), ('コ' -> "］"),
    ('サ' -> "廾"), ('シ' -> "ﾞ/"), ('ス' -> "ｽ"), ('セ' -> "ｾ"), ('ソ' -> "｀／"),
    ('タ' -> "勺"), ('チ' -> "于"), ('ツ' -> "\"/"), ('テ' -> "〒"), ('ト' -> "├"),
    ('ナ' -> "ﾅ"), ('ニ' -> "二"), ('ヌ' -> "ﾇ"), ('ネ' -> "ﾈ"), ('ノ' -> "丿"),
    ('ハ' -> "'`"), ('ヒ' -> "匕"), ('フ' -> "┐"), ('ヘ' -> "∧"), ('ホ' -> "朮"),
    ('マ' -> "ﾏ"), ('ミ' -> "彡"), ('ム' -> "厶"), ('メ' -> "×"), ('モ' -> "ﾓ"),
    ('ヤ' -> "ﾔ"), ('ユ' -> "ﾕ"), ('ヨ' -> "∋"),
    ('ラ' -> "ﾗ"), ('リ' -> "└|"), ('ル' -> "｣ﾚ"), ('レ' -> "∠"), ('ロ' -> "□"),
    ('ワ' -> "ﾜ"), ('ヲ' -> "ｦ"),('ン' -> "ﾝ"),
    ('ガ' -> "ヵ″"), ('ギ' -> "≠″"), ('グ' -> "勹″"), ('ゲ' -> "ヶ″"), ('ゴ' -> "］″"),
    ('ザ' -> "廾″"), ('ジ' -> "ﾞ/″"), ('ズ' -> "ｽ″"), ('ゼ' -> "ｾ″"), ('ゾ' -> "｀／″"),
    ('ダ' -> "勺″"), ('ヂ' -> "于″"), ('ヅ' -> "\"/″"), ('デ' -> "〒″"), ('ド' -> "├″"),
    ('バ' -> "ﾊ〃"), ('ビ' -> "ﾋ〃"), ('ブ' -> "ﾌ〃"), ('ベ' -> "∧″"), ('ボ' -> "朮″"),
    ('パ' -> "ﾉヽ°"), ('ピ' -> "ｔ°"), ('プ' -> "ﾌo"), ('ペ' -> "∧°"), ('ポ' -> "朮°"),
    ('ー' -> "→")
  )

  val galgoUrl = "./galgo-filter?url="
  def galgoFilter(url: String) = {
    import scala.collection.JavaConverters._
    import org.jsoup.Jsoup

    val doc = Jsoup.connect(url).get
    // クロスサイト回避のためにproxyを介す
    for (e <- doc.select("link").asScala) if (e.attr("href") != "") e.attr("href", cssProxyUrl + e.absUrl("href"))
    // クロスサイト回避のためにproxyを介す
    for (e <- doc.select("script").asScala) if (e.attr("src") != "") e.attr("src", proxyUrl + e.absUrl("src"))
    // リンク先もギャル語（似非再帰 モナドっぽい）
    for (e <- doc.select("a").asScala) e.attr("href", galgoUrl + e.absUrl("href"))
    // イメージは遅そうだからそのまま
    for (e <- doc.select("img").asScala) e.attr("src", e.absUrl("src"))
    doc.toString.flatMap(c =>  galMap.getOrElse(c, c.toString))
  }



}

class HtmlFilter extends HtmlFilterBase