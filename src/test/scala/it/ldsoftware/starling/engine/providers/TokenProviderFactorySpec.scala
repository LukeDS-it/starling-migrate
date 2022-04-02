package it.ldsoftware.starling.engine.providers

import akka.actor.ActorSystem
import com.typesafe.config.ConfigFactory
import it.ldsoftware.starling.engine.ProcessContext
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class TokenProviderFactorySpec extends AnyWordSpec with Matchers with ScalaFutures {

  // language=JSON
  private val config =
    """{
      |  "tokenProviders":  [
      |    {"type":  "DummyTokenProvider", "alias": "dummy", "config": {"output":  "token"}}
      |  ]
      |}""".stripMargin
  "getTokenProviders" should {

    "build the correct token provider from a configuration" in {
      val c = ConfigFactory.parseString(config)
      val pc = ProcessContext(ActorSystem("test"))
      val providers = TokenProviderFactory.getTokenProviders(c, pc)

      providers should have size 1

      val provider = providers.head
      provider should be(a[DummyTokenProvider])
      provider.name shouldBe "dummy"
      provider.token.futureValue shouldBe "token"
    }

  }

}
