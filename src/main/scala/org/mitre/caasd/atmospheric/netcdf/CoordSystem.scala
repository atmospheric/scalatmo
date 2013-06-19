package org.mitre.caasd.atmospheric.netcdf

// Copyright 2013, The MITRE Corporation.  All rights reserved.

import org.mitre.caasd.atmospheric.math.Interp._
import scala.collection.JavaConverters._
import scala.math.{ sin, cos, toRadians }
import ucar.nc2.dt.GridCoordSystem
import ucar.nc2.dt.GridDatatype
import ucar.nc2.dataset.CoordinateAxis1D;

/**
 * Transforms between (latitude, longitude) and xy grid index.
 * Indexing starts at 0.
 * @todo Update this to use the ucar.unidata.geoloc.projection class
 */
class CoordSystem(val gcs: GridCoordSystem) {

  private lazy val projection = gcs.getProjection
  private lazy val hasProjection = !gcs.isLatLon

  lazy val verticalAxis = gcs.getVerticalAxis()
  
  private lazy val axes = gcs.getCoordinateAxes.asScala.map(x =>
    (x.getShortName, (x.asInstanceOf[CoordinateAxis1D]).getCoordValues())).toMap

  lazy val xLen =
    if (hasProjection) axes("x").length
    else axes("lon").length

  lazy val yLen =
    if (hasProjection) axes("y").length
    else axes("lat").length

  private lazy val xIndex = (0.0 until xLen by 1.0).toArray
  private lazy val yIndex = (0.0 until yLen by 1.0).toArray

  private def normZeroToThreeSixty(x: Array[Double]): Array[Double] =
    x.map(a => (a + 360.0) % 360.0)

  private def normNegOneEightyToOneEighty(x: Array[Double]): Array[Double] =
    x.map(a => ((a + 180) % 360.0) - 180.0)

  /**
   * Similar to the Netcdf method: GridCoordinateSystem.findXYindexFromLatLonBounded()
   * except that we return a Double instead of Int.  This is enables linear interpolation.
   *
   * Double.NaN if out of bounds.
   */
  def latLonToXy(latlon: (Array[Double], Array[Double])): (Array[Double], Array[Double]) = latlon match {
    case (Array(), Array()) => (Array(), Array())
    case (lat, lon) => {
      if (hasProjection) {
        val xy = projection.latLonToProj(Array(lat, normNegOneEightyToOneEighty(lon)))
        val xi = interp1(axes("x"), xIndex, xy(0))
        val yi = interp1(axes("y"), yIndex, xy(1))
        (xi.toArray, yi.toArray)
      } else {
        val xi = interp1(axes("lon"), xIndex, normZeroToThreeSixty(lon))
        val yi = interp1(axes("lat"), yIndex, lat)
        (xi, yi)
      }
    }
  }

  /**
   * Similar to the Netcdf method: GridCoordinateSystem.getLatLon()
   * except that we return a Double instead of Int.  This enables linear interpolation.
   *
   * Double.NaN if out of bounds.
   */
  def xyToLatLon(xy: (Array[Double], Array[Double])): (Array[Double], Array[Double]) = xy match {
    case (Array(), Array()) => (Array(), Array())
    case (x, y) =>
      if (hasProjection) {
        val xi = interp1(xIndex, axes("x"), x)
        val yi = interp1(yIndex, axes("y"), y)
        val latlon = projection.projToLatLon(Array(xi.toArray, yi.toArray))
        (latlon(0), normNegOneEightyToOneEighty(latlon(1)))
      } else {
        val lon = interp1(xIndex, axes("lon"), x)
        val lat = interp1(yIndex, axes("lat"), y)
        (lat.toArray, lon.toArray)
      }
  }

  def alignTrueNorth(
    uComponentOfWind: IndexedSeq[Double],
    vComponentOfWind: IndexedSeq[Double],
    longitude: IndexedSeq[Double]): (IndexedSeq[Double], IndexedSeq[Double]) = {

    if (!this.hasProjection) (uComponentOfWind, vComponentOfWind)
    else {
      val param = projection.getProjectionParameters.asScala.map {
        x => (x.getName, if (x.isString) 0.0 else x.getNumericValue())
      }.toMap
      val originLat = param("latitude_of_projection_origin").toDouble
      val meridianAlign = param("longitude_of_central_meridian").toDouble
      val rotConst = sin(originLat.toRadians)

      def rotate(u: Double, v: Double, long: Double): (Double, Double) = {
        val angle = rotConst * (long - meridianAlign).toRadians
        val cosAng = cos(angle)
        val sinAng = sin(angle)
        (u * cosAng + v * sinAng, -u * sinAng + v * cosAng)
      }
      
      (uComponentOfWind, vComponentOfWind, normZeroToThreeSixty(longitude.toArray)).zipped.map(rotate).unzip
    }
  }
}

/** Companion object as a factory. */
object CoordSystem {
  def apply(gcs: ucar.nc2.dt.GridCoordSystem) = new CoordSystem(gcs)
}
