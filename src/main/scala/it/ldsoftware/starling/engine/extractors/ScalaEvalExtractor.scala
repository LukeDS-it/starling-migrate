package it.ldsoftware.starling.engine.extractors

import com.typesafe.config.Config
import it.ldsoftware.starling.engine._
import it.ldsoftware.starling.engine.extractors.ScalaEvalExtractor.CallToFunction
import it.ldsoftware.starling.extensions.IOExtensions.FileFromStringExtensions

import javax.script.ScriptEngineManager
import scala.concurrent.{ExecutionContext, Future}

class ScalaEvalExtractor(script: String, override val config: Config, override val initialValue: Extracted = Map())(
    implicit val ec: ExecutionContext
) extends Extractor {

  override def doExtract(): Future[Seq[ExtractionResult]] =
    Future(new ScriptEngineManager().getEngineByName("scala"))
      .map { engine =>
        engine.put("initialValue", initialValue)
        engine.eval(script)
        engine.eval(CallToFunction).asInstanceOf[Extracted]
      }
      .map { it =>
        Seq(Right(it))
      }

  override def toPipedExtractor(data: Extracted): Extractor =
    new ScalaEvalExtractor(script, config, data)

}

object ScalaEvalExtractor extends ExtractorBuilder {

  private val CallToFunction = "produce(initialValue.asInstanceOf[Map[String, Any]])"

  private val WrapperFunction =
    """
      |def produce(data: Map[String, Any]): Map[String, Any] = {
      | %s
      |}
      |""".stripMargin

  override def apply(config: Config, pc: ProcessContext): Extractor = {
    val script = config.getString("type") match {
      case "inline" => String.format(WrapperFunction, config.getString("script"))
      case "file"   => config.getString("file").readFile
      case x        => throw new Error(s"Cannot handle script of type $x")
    }

    new ScalaEvalExtractor(script, config)(pc.executionContext)
  }

}
