package com.github.tototoshi.json2tsv

import org.json4s._
import org.json4s.native.JsonMethods._

trait Parser {

  private def extract(path: String, json: JValue): String = {
    if (path.contains(".")) {
      extract(path.split("\\.").drop(1).mkString("."), json \ path.split("\\.").head)
    } else {
      (json \ path match {
        case JString(s) => s
        case JDouble(d) => d
        case JDecimal(d) => d
        case JInt(i) => i
        case JBool(b) => b
        case jo @ JObject(o) => compact(render(jo))
        case ja @ JArray(a) => compact(render(ja))
        case JNothing | JNull => ""
        case x => x
      }).toString
    }
  }

  def parseLine(paths: Seq[String], line: String): Seq[String] = {
    for {
      path <- paths
    } yield {
      extract(path, parse(line))
    }
  }

}
