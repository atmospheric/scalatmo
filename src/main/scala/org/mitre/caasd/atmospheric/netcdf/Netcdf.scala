package org.mitre.caasd.atmospheric.netcdf

// Copyright 2013, The MITRE Corporation.  All rights reserved.

import org.eintr.loglady.Logging
import java.net.URL
import org.joda.time.DateTime
import org.joda.time.format.ISODateTimeFormat
import org.joda.time.DateTimeZone.UTC

import scala.collection.JavaConverters.asScalaBufferConverter
import scala.math.{ floor, ceil }

import ucar.nc2.dataset.NetcdfDataset
import ucar.nc2.dt.grid.GridDataset
import ucar.nc2.time.CalendarDateUnit

import org.mitre.caasd.atmospheric.math._
import org.mitre.caasd.atmospheric.math.Interp._
import org.mitre.caasd.atmospheric.netcdf._
import org.mitre.caasd.atmospheric.netcdf.MitreArchive._

/** Wraps Java Netcdf libraries, providing additional functionality. */
class Netcdf(val location: String) extends Logging {
  log.debug("Netcdf loaded: " + location)
  
  def printVar() {
    variables.filter(x => x._2.unitsSou != null).map(x => println(x._2) )
  }
  
  /** ucar.nc2.NetcdfDataset Object */
  val nc = NetcdfDataset.openDataset(location)

  /** ucar.nc2.dt.GridDataset */
  val gd = new GridDataset(nc)

  /** Global Attributes mapped by name. */
  lazy val globalAttributes = nc.getGlobalAttributes.asScala.map(x => (x.getName(), x)).toMap

  /** Variables mapped by name, each with their associated Grid. */
  lazy val variables = (for {
    x <- gd.getGridsets.asScala.toSeq
    y <- x.getGrids.asScala
  } yield {
    val singleVar = Variable(y, CoordSystem(x.getGeoCoordSystem))
    (singleVar.name, singleVar)
  }).toMap

  /** Date when the model was run */
  lazy val modelRunDate = {
    var dateUnits = nc.findVariable("time").getUnitsString
    var ncDate = CalendarDateUnit.of("gregorian",dateUnits).getBaseCalendarDate
    new DateTime(ncDate.getMillis(),UTC)
  }

  /** Outlook, in hours */
  lazy val forecastOutlook = nc.findVariable("time").readScalarInt

  /** When the forecast is effective */
  lazy val effectiveDate = modelRunDate.plusHours(forecastOutlook)

  /** Get a List[String] of sorted variable names. */
  lazy val variableNames: List[String] = variables.keys.toList.sorted

  /** Constructor from DateTime */
  def this(date: DateTime, outlook: Int = 0, source: ForecastType = RUCRR) =
    this(getFilename(date, outlook, source).getOrElse(""))

  /** Constructor from java.net.URL */
  def this(url: URL) = this(url.toString())
}

/** Companion object as Dataset factory. */
object Netcdf {
  def apply(location: String) = new Netcdf(location)
  /*def apply(date: DateTime) = new Netcdf(date)
  def apply(date: DateTime, outlook: Int, source: ForecastType) = new Netcdf(date, outlook, source)*/
}
