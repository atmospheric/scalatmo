package org.mitre.caasd.atmospheric.math

// Copyright 2013, The MITRE Corporation.  All rights reserved.

import scala.math.{ atan2, cos, Pi, pow, sin, sqrt, toRadians }

/**
 * Aviation Methods
 *
 * @author CWYNNYK
 */
trait Aviation {
  val gravityStandard = 9.80665 // m/s2
  val gravitySeaLevel = 9.80616 // m/s2
  val radiusEarthSemiMajorAxis = 6378137.0 // m, also known as a
  val radiusEarthSemiMinorAxis = 6356752.3142 // m, also known as b

  /**
   * For reference and magic numbers, see 'docs/GeometricHeightReference.pdf'
   */
  def geopotentialToGeometric(geopotential: Double, degreesLatitude: Double) = {
    val cosr = cos(2 * degreesLatitude.toRadians)
    val rlat = radiusEarthSemiMajorAxis / (1.006803 - 0.006706 * pow(sin(degreesLatitude.toRadians), 2)) // meters
    val glat = gravitySeaLevel * (1 - 0.002637236 * cosr + 0.000005821355 * cosr * cosr) // m/sec^2
    val gr = glat * rlat / gravityStandard // meters
    geopotential * rlat / (gr - geopotential)
  }

  def geometricToGeopotential(geometric: Double, degreesLatitude: Double) = {
    val cosr = cos(2 * degreesLatitude.toRadians)
    val rlat = radiusEarthSemiMajorAxis / (1.006803 - 0.006706 * pow(sin(degreesLatitude.toRadians), 2)) // meters
    val glat = gravitySeaLevel * (1 - 0.002637236 * cosr + 0.000005821355 * cosr * cosr) // m/sec^2
    val gr = glat * rlat / gravityStandard // meters
    (geometric * gr) / (rlat + geometric)
  }

  /**
   * theta - clockwise displacement angle in radians, 0 = true north
   * rho - distance from origin to point
   */
  def cartesian2polar(x: Double, y: Double): (Double, Double) = {
    val rho = sqrt(x * x + y * y)
    val theta = (atan2(x, y) + 2 * Pi) % (2 * Pi)
    (theta, rho)
  }

  /**
   * theta - clockwise displacement angle in radians, 0 = true north
   * rho - distance from origin to point
   */
  def polar2cartesian(theta: Double, rho: Double): (Double, Double) = {
    val x = sin(theta) * rho
    val y = cos(theta) * rho
    (x, y)
  }

  def ISApressure = 1013.25 // hPa
  
  /** 
   * altitude in feet.
   */
  def altitudeToPressure(pressureAltitude: Double, refPressure: Double) : Double = 
    ISApressure * pow( (1 - pressureAltitude/145366.45), (1/0.190284) )
  
  
  // Speed conversions.
  private val a0 = 661.47 // knots 

  private def pressureRatioDelta(pressureAltitudeFeet: Double): Double =
    pow((1 - pressureAltitudeFeet / 145366.45), (1 / 0.190284)) // Unitless

  def speedOfSound(pressureAltitudeFeet: Double): Double =
    if (pressureAltitudeFeet > 36000) 573.0
    else 29.06 * sqrt(518.7 - 3.57 * pressureAltitudeFeet / 1000)

  // @todo: Replace with Taylor series expansion to avoid expensive pow() calls
  def kias2mach(kias: Double, pressureAltitudeFeet: Double): Double = {
    val delta = pressureRatioDelta(pressureAltitudeFeet)
    sqrt(5 * (pow((pow((1 + pow((kias / a0), 2) / 5), (7 / 2)) - 1) / delta, (2 / 7)) - 1))
  }

  def kias2ktas(kias: Double, pressureAltitudeFeet: Double): Double =
    mach2ktas(kias2mach(kias, pressureAltitudeFeet), pressureAltitudeFeet)

  def ktas2kias(ktas: Double, pressureAltitudeFeet: Double): Double = {
    mach2kias(ktas2mach(ktas, pressureAltitudeFeet), pressureAltitudeFeet)
  }

  def ktas2mach(ktas: Double, pressureAltitudeFeet: Double): Double =
    ktas / speedOfSound(pressureAltitudeFeet)

  def mach2ktas(mach: Double, pressureAltitudeFeet: Double): Double =
    mach * speedOfSound(pressureAltitudeFeet)

  def mach2kias(mach: Double, pressureAltitudeFeet: Double): Double = {
    val delta = pressureRatioDelta(pressureAltitudeFeet)
    mach * a0 * sqrt(delta) * (1 + (1 / 8) * (1 - delta) * mach * mach +
      (3 / 640) * (1 - 10 * delta + 9 * delta * delta) * pow(mach, 4))
  }
}

object Aviation extends Aviation