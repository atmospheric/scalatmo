package org.mitre.caasd.atmospheric.math

// Copyright 2013, The MITRE Corporation.  All rights reserved.

import org.mitre.caasd.atmospheric.TestUtil
import org.mitre.caasd.atmospheric.math.Aviation._
import org.scalatest.FunSuite
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.matchers.ShouldMatchers._
import scala.math._



@RunWith(classOf[JUnitRunner])
class AviationSuite extends FunSuite with TestUtil {
  test("Geopotential to Geometric") {
    val alt = (3000.0 until 12000 by 1000).toArray
    val lat = (0.0.until(90.0) by 10.0).toArray
    val res = (alt, lat).zipped.map(geopotentialToGeometric)
    val matlabResults = Array(3009.49, 4012.66, 5014.32, 6013.91, 7011.29, 8006.80, 9001.29, 9995.96, 10992.22, 11991.47)
    res should equalWithTolerance(matlabResults, 10e-2)
  }

  test("GeometricToGeopotential - identity") {
    val alt = (3000.0 until 12000 by 1000).toArray
    val lat = (0.0.until(90.0) by 10.0).toArray
    val zgp = (alt, lat).zipped.map(geopotentialToGeometric)
    val andBack = (zgp, lat).zipped.map(geometricToGeopotential)
    andBack should equalWithTolerance(alt, 10e-2)
  }

  test("polar2cartesian") {
    val theta = (0.0 to (2 * Pi) by (Pi / 6))
    val rho = Array(1.0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1)
    val a = sqrt(3) / 2
    val (u, v) = (theta, rho).zipped.map(polar2cartesian).unzip
    u.toArray should equalWithTolerance(Array(0, 0.5, a, 1, a, 0.5, 0, -0.5, -a, -1, -a, -0.5, 0))
    v.toArray should equalWithTolerance(Array(1, a, 0.5, 0, -0.5, -a, -1, -a, -0.5, 0, 0.5, a, 1))
  }

  test("cartesian2polar") {
    val theta = (0.0 to (2 * Pi) by (Pi / 6))
    val rho = Array(1.0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1)
    val a = sqrt(3) / 2
    val u = Array(0, 0.5, a, 1, a, 0.5, 0, -0.5, -a, -1, -a, -0.5, 0)
    val v = Array(1, a, 0.5, 0, -0.5, -a, -1, -a, -0.5, 0, 0.5, a, 1)
    val (thetaNew, rhoNew) = (u, v).zipped.map(cartesian2polar).unzip
    rhoNew.toArray should equalWithTolerance(rho)
    thetaNew.toArray should equalWithTolerance(theta.map(_ % (2 * Pi)).toArray)
  }

  test("polar2cartesian, cartesian2polar - identity") {
    val u = IndexedSeq(8.4072, 2.5428, -8.1428, 2.4352, -9.2926, 3.4998, 1.9660, -2.5108)
    val v = IndexedSeq(3.5166, 8.3083, -5.8526, 5.4972, 9.1719, -2.8584, 7.5720, -7.5373)
    val (theta, rho) = (u, v).zipped.map(cartesian2polar).unzip
    val (u2, v2) = (theta, rho).zipped.map(polar2cartesian).unzip
    u2.toArray should equalWithTolerance(u.toArray)
    v2.toArray should equalWithTolerance(v.toArray)
  }
}
