package org.mitre.caasd.atmospheric

// Copyright 2013, The MITRE Corporation.  All rights reserved.

import java.io.File

/**
 * Utility Methods
 */
trait SystemUtil {

  sealed abstract class OperatingSystem
  case object Windows extends OperatingSystem
  case object Unix extends OperatingSystem
  case object Mac extends OperatingSystem
  case object UnknownOS extends OperatingSystem

  /** Our operating system */
  lazy val computer: OperatingSystem =
    java.lang.System.getProperty("os.name").toLowerCase match {
      case str if (str.contains("windows")) => Windows
      case str if (str.contains("nix")) => Unix
      case str if (str.contains("mac")) => Mac
      case _ => UnknownOS
    }

  lazy val hostname: String = java.net.InetAddress.getLocalHost().getHostName()

  lazy val isHadoop: Boolean = (computer == Unix) && hostname.toLowerCase.contains("hadoop")

  lazy val filesep: String = java.lang.System.getProperty("file.separator")

  /** Build a filename from the list of directories. */
  def fullfile(dir: String*): String = dir.reduce(_ + filesep + _)

  /** Check if a file exists and is of a minimum size. */
  def exist(filename: String, minBytes: Long = 1): Boolean = {
    try {
      val formattedFilename = filename.replaceAll("^file:/", computer match {
        case Windows => ""
        case _ => "/"
      })
      val f = new File(formattedFilename)
      f.exists && f.isFile && (f.length >= minBytes)
    } catch {
      case _: Throwable => false
    }
  }
}

object SystemUtil extends SystemUtil
