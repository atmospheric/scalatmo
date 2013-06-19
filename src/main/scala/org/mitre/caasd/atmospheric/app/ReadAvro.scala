package org.mitre.caasd.atmospheric.app

// Copyright 2013, The MITRE Corporation.  All rights reserved.

import com.twitter.scalding._
import scalding.avro.TypedUnpackedAvroSource
import org.apache.avro.Schema
import org.apache.hadoop.util.ToolRunner
import org.apache.hadoop.conf.Configuration
import org.eintr.loglady.Logging

object WordCountAvroJob {
  def main(args: Array[String]) {
    ToolRunner.run(new Configuration, new Tool, args);
  }
}

class WordCountAvroJob(args : Args) extends Job(args) with Logging {
  log.info("Started Avro Job.")
  val typedText : TypedPipe[String] = TypedPipe.from(TextLine( args("input")))
  val records = typedText
    .map{ rec => rec}
  log.info("Completed processing records for Avro Job.")
  
  records.write(Tsv( args("output")))
  log.info("Completed Avro Job.")
}