package org.mitre.caasd.atmospheric

// Copyright 2013, The MITRE Corporation.  All rights reserved.

import org.scalatest.FunSuite
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.matchers.ShouldMatchers._
import org.joda.time.{ DateTime, DateTimeZone }
import org.mitre.caasd.atmospheric._
import org.mitre.caasd.atmospheric.netcdf.Archive._
import org.mitre.caasd.atmospheric.netcdf._
import org.mitre.caasd.atmospheric.netcdf.{GeopotentialAltitude,GeometricAltitude}

/**
 * Test the Dataset against local HRRR files with known interpolation result values.
 */
@RunWith(classOf[JUnitRunner])
class AccuracySuite extends FunSuite with TestUtil {
  
  def time[R](block: => R): R = {
    val t0 = System.nanoTime()
    val result = block    // call-by-name
    val t1 = System.nanoTime()
    println("Elapsed time: " + (t1 - t0)/1000000.0 + "ms")
    result
  }
  
  /**
   * 
   * Inputs:
   *   Lat - Degrees N
   *   Lon - Degrees E
   *   Alt - Feet
   *   resGeopotU - Knots
   *   resGeopotV - Knots
   */
  val hrrrA = time { Netcdf(getClass.getResource("/HRRR.pressure.3km60min.2011-09-06T12_00_00fc00_00.EWR.grib2").toString) }
  val hrrrB = Netcdf(getClass.getResource("/HRRR.pressure.3km60min.2011-09-06T13_00_00fc00_00.EWR.grib2").toString)
  
  val lat = Array(40.4277,38.4707,39.6531,41.1621,39.9875,38.6766,
      38.4961,39.263,39.1132,38.798,39.7916,40.7127,39.1217,39.1468,
      37.6244,37.1222,38.4509,38.5876,40.2685,41.7847,41.6787,39.2894,
      38.2024,40.8195,40.7966,40.7032,40.7184,37.5296,40.4078,39.3163)

  val lon = Array(-75.9392,-76.5074,-72.8821,-76.125,-76.1822,-73.6701,
      -72.5281,-74.4172,-73.4865,-76.232,-72.2327,-74.2956,-73.6013,-76.8172,
      -72.954,-73.2569,-76.3991,-74.3748,-75.3708,-74.2678,-75.0056,-74.9245,
      -76.0963,-75.7231,-76.8973,-72.3816,-73.7315,-72.3369,-76.1824,-72.3945)

  val alt = Array(24045.1,17744.4,13761,8470.8,22806.4,7631.4,2861.4,23252.6,
      20464.9,21741.2,19619.8,13152.4,12332.1,24668.1,10205.4,24621.7,23883.1,
      25715.7,15663.5,19434.2,28575.9,13875,2740.5,26135.7,19304.5,11297.1,
      29913.1,7501,19921.1,18544.7)
      
  val metersToFeet = 3.2808399
  val msToKnots = 1.94384449
  
  test("HRRR using Geopotential") {
    val resGeopotU = Array(54.8022,36.0881,32.7346,10.9797,53.9866,25.6605,
      19.7404,47.545,31.8877,42.5459,32.6612,43.4305,30.1042,48.884,13.544,
      23.0917,41.1235,37.7256,44.7735,56.6852,52.5014,34.6507,10.1163,55.2007,
      49.7997,38.4808,49.4037,15.1438,51.7736,26.4623)
 
    val resGeopotV = Array(26.6481,25.0499,17.9506,3.6011,25.0619,14.4783,
      13.72,24.359,19.0769,24.2431,13.9998,23.7416,19.3637,26.8388,18.3349,
      12.1302,25.6799,24.8451,23.4284,27.5329,34.9999,21.3665,14.2157,28.9745,
      28.0972,17.2492,28.4552,9.2734,24.692,20.1183)
    
    val uWind = "u-component_of_wind_isobaric"
    val vWind = "v-component_of_wind_isobaric"
    val res = Interp3D().withVerticalReference(GeopotentialAltitude)
      .interp(hrrrA,lat,lon,alt.map(_ / metersToFeet),List(uWind,vWind))
    val (u,v) =  hrrrB.variables(uWind).coordSystem.alignTrueNorth(
        res(uWind).toArray,
        res(vWind).toArray, lon)
    u.toArray[Double].map(_ * msToKnots) should equalWithTolerance(resGeopotU, 10e-1)
    v.toArray[Double].map(_ * msToKnots) should equalWithTolerance(resGeopotV, 10e-1)
    
    //(u.toArray[Double],resGeopotU).zipped.map((a,b) => a - b).foldLeft(0)(_+_)
  }
  
  test("HRRR using Geometric") {
    val resGeomU = Array(54.8138,36.0543,32.7545,10.9216,53.9714,25.6625,19.7308,
        47.4714,31.8376,42.4865,32.6154,43.3593,30.095,48.8234,13.544,23.1081,
        41.1084,37.72,44.7554,56.641,52.5414,34.6467,10.106,55.2268,49.7814,
        38.4596,49.3441,15.1579,51.7208,26.4547)
 
    val resGeomV = Array(26.6353,25.0709,17.9689,3.5829,25.0534,14.4787,13.7267,
        24.2944,19.0709,24.2083,14.0243,23.7241,19.3582,26.8621,18.3135,12.217,
        25.664,24.8269,23.4234,27.5219,34.8754,21.3925,14.2068,28.9467,28.1004,
        17.2498,28.4342,9.2688,24.6694,20.0869)
  
    val uWind = "u-component_of_wind_isobaric"
    val vWind = "v-component_of_wind_isobaric"
    //val res = time { hrrrA.interpolate(lat,lon,alt,List(uWind,vWind), GeometricAltitude) }
    val res = time { Interp3D().interpWind(hrrrA,lat,lon,alt.map(_ / metersToFeet))}
    res(uWind).map(_ * msToKnots) should equalWithTolerance(resGeomU, 10e-1)
    res(vWind).map(_ * msToKnots) should equalWithTolerance(resGeomV, 10e-1)
  }  
}
