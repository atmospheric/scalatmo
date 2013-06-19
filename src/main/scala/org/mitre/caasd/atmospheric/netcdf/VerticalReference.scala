package org.mitre.caasd.atmospheric.netcdf

// Copyright 2013, The MITRE Corporation.  All rights reserved.

import org.mitre.caasd.atmospheric.math.Aviation._

abstract class VerticalReference {
  def getObject = this
  val verticalVariable: String
  def verticalTransform(altitude: Array[Double], latitude: Array[Double],
    longitude: Array[Double]) = altitude
}

object GeometricAltitude extends VerticalReference {
  val verticalVariable = "Geopotential_height"
  override def verticalTransform(altitude: Array[Double], 
      latitude: Array[Double], longitude: Array[Double]): Array[Double] =
    (altitude, latitude).zipped.map(geometricToGeopotential(_, _))
}

object GeopotentialAltitude extends VerticalReference {
  val verticalVariable = "Geopotential_height"
}

object Pressure extends VerticalReference {
  val verticalVariable = "Pressure"
}

object PressureAltitudeStandard extends VerticalReference {
  val verticalVariable = "Pressure"
  override def verticalTransform(altitude: Array[Double], 
      latitude: Array[Double], longitude: Array[Double]) =
    (altitude,latitude).zipped.map(altitudeToPressure)
}
