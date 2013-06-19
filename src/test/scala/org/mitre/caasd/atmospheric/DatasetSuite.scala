package org.mitre.caasd.atmospheric

// Copyright 2013, The MITRE Corporation.  All rights reserved.

import org.scalatest.FunSuite
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.matchers.ShouldMatchers._
import org.joda.time.{ DateTime, DateTimeZone }
import org.mitre.caasd.atmospheric._
import org.mitre.caasd.atmospheric.netcdf.MitreArchive._
import org.mitre.caasd.atmospheric.netcdf._

/**
 * Test the Dataset class against local RAP/GFS files.
 */
@RunWith(classOf[JUnitRunner])
class DatasetSuite extends FunSuite with TestUtil {
  val rap = Netcdf(getClass.getResource("/rap_20121022_0000_000.grb2").toString)
  val gfs = Netcdf(getClass.getResource("/gfs_20121113_0000_000.grb2").toString)
  val hrrrA = Netcdf(getClass.getResource("/HRRR.pressure.3km60min.2011-09-06T12_00_00fc00_00.EWR.grib2").toString)
  val hrrrB = Netcdf(getClass.getResource("/HRRR.pressure.3km60min.2011-09-06T13_00_00fc00_00.EWR.grib2").toString)
  val lat = Array(30, 40, 50) // Latitude N
  val lon = Array(-80, -90, -100) // Longitude W
  val alt = Array(10000, 20000, 30000).map(_ * 0.3048) // Meters
  val temperature = "Temperature_hybrid"

  test("RAP Variables name access") {
    assert(rap.variableNames.head === "Baseflow-Groundwater_Runoff_surface_0_Hour_Accumulation")
    rap.variables
  }

  test("GFS Variables name access") {
    assert(gfs.variableNames.head === "5-Wave_Geopotential_Height_Anomaly_isobaric")
    gfs.variables
  }
/*
  test("RUC/RAP 3D Temperature interpolation.") {
    val temps = rap.interpolate(lat, lon, alt, List(temperature))
    temps(temperature) should equalWithTolerance(Array(282.0863, 259.6047, 227.3621), 10e-1) // MATLAB
  }

  test("RUC/RAP 3D Temperature interpolation outside vertical bounds (Clamp results to nearest).") {
    val temps = rap.interpolate(lat, lon, Array(0, 0, 1000000), List(temperature))
    temps(temperature) should equalWithTolerance(Array(296.8112, 292.2149, 222.1595), 10e-3) // MATLAB
  }

  test("RUC/RAP 2D Surface Temperature interpolation.") {
    val temps = rap.interpolate(lat, lon, alt, List("Temperature_surface"))
    temps("Temperature_surface").toArray should equalWithTolerance(Array(301.1207, 290.2144, 276.9816), 10e-1) // MATLAB
  }*/

  test("Variable Units.") {
//    rap.variables.filter(x => x._2.unitsSou != null).map(x => println(x._2) )
//    println("GFS")
//    gfs.variables.map(x => println(x._2) )
  }
  
  // FUTURE TESTS:
  //   Test wind with correction factor.  (Decide how to call this??)
  //   Test interpolation of boolean.
  //   Test 3D Pressure interpolation (difficult since non-increasing with elevation).
  //   Test outside of lateral bounds.

}
