package it.ldsoftware.starling.engine.util

import com.typesafe.config.ConfigFactory
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class ReflectionFactorySpec extends AnyWordSpec with Matchers {

  "getCredentials" should {

    "return a pair of nulls when no credentials are specified" in {
      val config =
        s"""
           |
           |""".stripMargin
      ReflectionFactory.getCredentials(ConfigFactory.parseString(config)) shouldBe (null, null)
    }

    "return plain text username and password" in {
      val config =
        s"""
           |jdbc-url: "url"
           |credentials = {
           |  type = "plain"
           |  user = "user"
           |  pass = "pass"
           |}
           |""".stripMargin

      ReflectionFactory.getCredentials(ConfigFactory.parseString(config)) shouldBe ("user", "pass")
    }

    "return only username if no password is specified" in {
      val config =
        s"""
           |jdbc-url: "url"
           |credentials = {
           |  type = "plain"
           |  user = "user"
           |}
           |""".stripMargin

      ReflectionFactory.getCredentials(ConfigFactory.parseString(config)) shouldBe ("user", null)
    }

    "return environment variables" in {
      val config =
        """
           |jdbc-url: "url"
           |credentials = {
           |  type = "env"
           |  user = "UNIT_DB_USER"
           |  pass = "UNIT_DB_PASS"
           |}
           |""".stripMargin

      ReflectionFactory.getCredentials(ConfigFactory.parseString(config)) shouldBe ("user", "pass")
    }

    "take credentials from a file" in {
      val res = getClass.getClassLoader.getResource("unit-reflection-vars.conf").getFile
      val config =
        s"""
          |jdbc-url: "url"
          |credentials = {
          |  type = "file"
          |  file = "$res"
          |}
          |""".stripMargin

      ReflectionFactory.getCredentials(ConfigFactory.parseString(config)) shouldBe ("user", "pass")
    }

  }

}
