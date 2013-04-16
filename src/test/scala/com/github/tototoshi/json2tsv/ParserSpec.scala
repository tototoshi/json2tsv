package com.github.tototoshi.json2tsv

import org.scalatest._
import org.scalatest.matchers.ShouldMatchers

class ParserSpec extends FunSpec with ShouldMatchers {

  val parser = new Parser {}

  describe("Parser") {

    it("should parse a json line") {
      val data = """{"foo": "abc", "bar": { "piyo": 3 }}"""
      val actual = parser.parseLine(Seq("foo", "bar.piyo"), data)
      actual.mkString should be("abc3")
    }

  }
}
