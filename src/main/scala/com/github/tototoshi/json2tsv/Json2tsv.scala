package com.github.tototoshi.json2tsv

import java.io.{ InputStream, BufferedInputStream, FileInputStream }
import java.util.zip.GZIPInputStream
import resource._

import scala.io.Source


object Json2TSV extends Parser {


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

    val parser = new scopt.OptionParser[Config]("json2tsv") {
      head("json2tsv", "0.1-SNAPSHOT")
      opt[String]('p', "path") action { (v: String, c: Config) => c.copy(paths = c.paths ::: v :: Nil) } text("path to the field")
      arg[String]("<files>...") optional() unbounded() action { (v: String, c: Config) => c.copy(files = c.files :+ v) }
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
