package org.mitre.caasd.atmospheric.netcdf

// Copyright 2013, The MITRE Corporation.  All rights reserved.

import org.joda.time.{DateTime,Hours}
import org.joda.time.DateTimeZone.UTC
import org.joda.time.format.DateTimeFormat
import scala.collection.JavaConverters.asScalaBufferConverter
import org.mitre.caasd.atmospheric.SystemUtil

/**
 * Archive Filename Lookup
 *
 * @author Chris Wynnyk
 */
trait Archive extends SystemUtil {
  sealed abstract class ForecastType
  case object GFS extends ForecastType
  case object RUCRR extends ForecastType

  /**
   * 'effectiveDate' is the time at which the forecast is effective (valid), where:
   *    effectiveDate = timeForecastCreated + outlookInHours
   *
   * Returns None if file does not exist.
   */
  def getFilename(effectiveDate: DateTime, outlookInHours: Int = 0, source: ForecastType = RUCRR) : Option[String] = {
     val basedir = computer match {
      case Windows => "\\\\samba\\data"
      case _ => "/data"
    }

    def gfs(d: DateTime, outlookInHours: Int): String = {
      val fmt = DateTimeFormat.forPattern("yyyyMMdd_HH00").withZone(UTC)
      val datestr = fmt.print(d)
      val year = datestr.substring(0, 4)
      val month = datestr.substring(4, 6)
      val day = datestr.substring(6, 8)
      val fcst = "%03d".format(outlookInHours)
      fullfile(basedir, "gfs", year, year + month + day,
        "gfs_" + datestr + "_" + fcst + ".grb2")
    }

    def ruc(d: DateTime, outlookInHours: Int): String = {
      val fmt = DateTimeFormat.forPattern("yyyyMMdd_HH00").withZone(UTC)
      val datestr = fmt.print(d)
      val year = datestr.substring(0, 4)
      val month = datestr.substring(4, 6)
      val day = datestr.substring(6, 8)
      val fcst = "%03d".format(outlookInHours)
      val rapTransition = new DateTime(2012,5,2,0,0,UTC)
      val prefix = {
        if (d.isBefore(rapTransition)) "ruc"
        else "rap"
      }
      fullfile(basedir, "ruc_wind_data", year, year + month + day,
        prefix + "_" + datestr + "_" + fcst + ".grb2")
    }

    // Check inputs
    if (outlookInHours < 0)
      throw new IllegalArgumentException("outlookInHours must be positive")
    
    // Convert to modelRunDate
    val modelRunDate = effectiveDate.minusHours(outlookInHours)
    val nearestModelRunDate = source match {
      case RUCRR => modelRunDate.hourOfDay.roundHalfFloorCopy // Nearest Hour
      case GFS => {// Round to nearest 6-hour increment.
        val mrd = modelRunDate.hourOfDay.roundFloorCopy.plusHours(3) 
        mrd.minusHours(mrd.getHourOfDay % 6)
      }
    }
    
    // Construct filename
    val filename = source match {
      case RUCRR => ruc(nearestModelRunDate, outlookInHours)
      case GFS => gfs(nearestModelRunDate, outlookInHours)
    }

    // Return filename if it exists, otherwise empty string.
    val minSize = 100000 // 100 KB
    if (SystemUtil.exist(filename, minSize)) Some(filename)
    else None
  }
}

object Archive extends Archive
