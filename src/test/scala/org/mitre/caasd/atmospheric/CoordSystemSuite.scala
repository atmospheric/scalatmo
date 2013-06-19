package org.mitre.caasd.atmospheric

// Copyright 2013, The MITRE Corporation.  All rights reserved.

import org.scalatest.FunSuite
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.matchers.ShouldMatchers._
import ucar.nc2.dt.grid.GridDataset
import org.mitre.caasd.atmospheric.netcdf._

@RunWith(classOf[JUnitRunner])
class CoordSystemSuite extends FunSuite with TestUtil {
  trait TestDataset {
    val rap = GridDataset.open(getClass.getResource("/rap_20121022_0000_000.grb2").toString)
    val gfs = GridDataset.open(getClass.getResource("/gfs_20121113_0000_000.grb2").toString)
    val rucCs = CoordSystem(rap.getGridsets.get(0).getGeoCoordSystem)
    val gfsCs = CoordSystem(gfs.getGridsets.get(0).getGeoCoordSystem)
  }

  test("RUC/RAP Corner points transform test") {
    new TestDataset {
      // Based on known boundary coordinates from Matlab netcdf exploration.
      val cornerLat = Array(16.2811, 17.3403, 55.4813, 54.1724) // Latitude N
      val cornerLon = Array(-126.1380, -69.0380, -57.3811, -139.8561) // Longitude W
      val (x, y) = rucCs.latLonToXy(cornerLat, cornerLon)
      x should equalWithTolerance(Array(0, 450, 450, 0), 10e-3)
      y should equalWithTolerance(Array(0, 0, 336, 336), 10e-3)
    }
  }

  test("RUC/RAP Center points transform test") {
    new TestDataset {
      val lat = Array(17.5827, 21.7799, 33.7030) // Latitude N
      val lon = Array(-125.2870, -124.8397, -127.8030) // Longitude W
      val (x, y) = rucCs.latLonToXy(lat, lon)
      x should equalWithTolerance(Array(9, 20, 21.5), 10e-3) // Results from MATLAB
      y should equalWithTolerance(Array(9, 42, 142.5), 10e-3)
    }
  }

  test("RUC/RAP Out of bounds, should be NaN") {
    new TestDataset {
      val oobLat = Array(16, 18, 55)
      val oobLon = Array(-120, -50, -30)
      val (x, y) = rucCs.latLonToXy(oobLat, oobLon)
      x should equalWithTolerance(Array(47.4455006, NaN, NaN),10e-5)
      y should equalWithTolerance(Array(NaN, 44.07137719, NaN),10e-5)
    }
  }

  test("RUC/RAP Transform inverse/self-consistency test") {
    new TestDataset {
      val lat = Array(25.3413, 38.9232, 43.1145, 50.2139, NaN) // Latitude N
      val lon = Array(-110.23, -90, -80, -70, NaN) // Longitude W
      val (x, y) = rucCs.latLonToXy(lat, lon)
      val (newLat, newLon) = rucCs.xyToLatLon(x, y)
      newLat should equalWithTolerance(lat)
      newLon should equalWithTolerance(lon)
    }
  }

  // Global Forecast System Tests
  test("GFS Corner points transform test") {
    new TestDataset {
      // Based on known boundary coordinates from Matlab netcdf exploration.
      val cornerLat = Array(-90, 90, 90, -90, -90, 90) // Latitude N
      val cornerLon = Array(0, 0, 359.5, 359.5, -360, -180) // Longitude W
      val (x, y) = gfsCs.latLonToXy(cornerLat, cornerLon)
      x should equalWithTolerance(Array(0, 0, 719, 719, 0, 360), 10e-3)
      y should equalWithTolerance(Array(360, 0, 0, 360, 360, 0), 10e-3)
    }
  }

  test("GFS Center points transform test") {
    new TestDataset {
      val lat = Array(-90, 21.5, 33.5) // Latitude N
      val lon = Array(-125.5, -124.5, 100.0) // Longitude W
      val (x, y) = gfsCs.latLonToXy(lat, lon)
      x should equalWithTolerance(Array(469, 471, 200), 10e-3) // Results from MATLAB
      y should equalWithTolerance(Array(360, 137, 113), 10e-3)
    }
  }

  test("GFS Out of bounds, should be NaN") {
    new TestDataset {
      val oobLat = Array(95, -95, -500)
      val oobLon = Array(10, 350, 120)
      val (x, y) = gfsCs.latLonToXy(oobLat, oobLon)
      x should equalWithTolerance(Array(20, 700, 240))
      y should equalWithTolerance(Array(NaN, NaN, NaN))
    }
  }

  test("GFS Transform inverse/self-consistency test") {
    new TestDataset {
      val lat = Array(25.3413, 38.9232, 43.1145, 50.2139, NaN) // Latitude N
      val lon = Array(180.23, 90, 80, 70, NaN) // Longitude W
      val (x, y) = gfsCs.latLonToXy(lat, lon)
      val (newLat, newLon) = gfsCs.xyToLatLon(x, y)
      newLat should equalWithTolerance(lat)
      newLon should equalWithTolerance(lon)
    }
  }
}
