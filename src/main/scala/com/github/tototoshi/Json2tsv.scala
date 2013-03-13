package com.github.tototoshi.json2tsv

import java.io.{ InputStream, BufferedInputStream, FileInputStream }
import java.util.zip.GZIPInputStream
import resource._
import org.json4s._
import org.json4s.native.JsonMethods._
import scala.io.Source

object Json2TSV {

  def extract(path: String, json: JValue): String = {
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

  def parseInputStream(paths: Seq[String], in: InputStream): Unit = {
    for {
      pw <- managed(new java.io.PrintWriter(System.out))
      line <- Source.fromInputStream(in).getLines
    } {
      if (pw.checkError) {
        System.exit(0)
      }
      pw.println(parseLine(paths, line).mkString("\t"))
    }
  }

  def parseFile(paths: Seq[String], file: String): Unit = {
    if (file.endsWith(".gz")) {
      parseGzipFile(paths, file)
    } else {
      parseTxtFile(paths, file)
    }
  }

  def parseTxtFile(paths: Seq[String], file: String): Unit = {
    for {
      fin <- managed(new FileInputStream(file))
    } {
      parseInputStream(paths, fin)
    }
  }

  def parseGzipFile(paths: Seq[String], file: String): Unit = {
    for {
      fin <- managed(new FileInputStream(file))
      bin <- managed(new BufferedInputStream(fin))
      gin <- managed(new GZIPInputStream(bin))
    } {
      parseInputStream(paths, gin)
    }
  }

  def main(args: Array[String]): Unit = {

    case class Config(paths: List[String], files: List[String])

    val parser = new scopt.immutable.OptionParser[Config]("jsontools", "0.1-SNAPSHOT") {
      def options = Seq(
        opt("p", "path", "path to field") { (v: String, c: Config) => c.copy(paths = c.paths ::: v :: Nil) },
        arglist("<files>...", "file") { (v: String, c: Config) => c.copy(files = c.files ::: v :: Nil) }
      )
    }

    val parsed = parser.parse(args, Config(Nil, Nil))

    parsed match {
      case Some(c) => {
        val paths = c.paths
        val files = c.files
        files match {
          case Nil => parseInputStream(paths, System.in)
          case fs => fs.foreach(f => parseFile(paths, f))
        }
      }
      case None => { /* do nothing */ }
    }

  }
}
