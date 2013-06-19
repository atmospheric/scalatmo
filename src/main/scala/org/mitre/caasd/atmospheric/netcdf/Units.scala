package org.mitre.caasd.atmospheric.netcdf

// Copyright 2013, The MITRE Corporation.  All rights reserved.

import org.mitre.caasd.systemofunits.si.systems.sibaseunits.DimensionlessUnits.DIMENSIONLESS
import org.mitre.caasd.systemofunits.si.systems.SIUnits
import org.mitre.caasd.systemofunits.si.{ SIBaseDimension, SIPrefix, SIUnit, SIUnitConverter, SIUnitSymbol }
import org.mitre.caasd.systemofunits.units.quantity.Quantity

/**
 * Climate Forecast (CF 1.0) unit conversion to SystemOfUnit units.
 *
 * Tested against GFS and RAP data files.
 * Undocumented unit strings map to type "unknown"
 */
trait Units {
  // Define new Unit, "Percent"
  trait Percent extends Quantity[Percent]
  val percent = new SIUnit[Percent](new SIUnitSymbol("%", 1, SIPrefix.NONE),
    SIBaseDimension.DIMENSIONLESS, new SIUnitConverter[Percent](0.01))

  // Define new Unit, "Unknown"
  trait Unknown extends Quantity[Unknown]
  val unknown = new SIUnit[Unknown](new SIUnitSymbol("???", 1, SIPrefix.NONE),
    SIBaseDimension.DIMENSIONLESS, new SIUnitConverter[Unknown](1))

  val cfUnits = Map(
    "Dobson" -> unknown,
    "fraction" -> DIMENSIONLESS,
    "gpm" -> SIUnits.METER,
    "J kg-1" -> SIUnits.JOULE.divide(SIUnits.KILOGRAM),
    "kg kg-1" -> SIUnits.KILOGRAM.divide(SIUnits.KILOGRAM),
    "kg m-2" -> SIUnits.KILOGRAM.divide(SIUnits.METERS_SQUARED),
    "kg m-2 s-1" -> SIUnits.KILOGRAM.divide(SIUnits.METERS_SQUARED.times(SIUnits.SECOND)),
    "K" -> SIUnits.KELVIN,
    "m" -> SIUnits.METER,
    "m s" -> SIUnits.METER.times(SIUnits.SECOND),
    "m s-1" -> SIUnits.METERS_PER_SECOND,
    "Pa" -> SIUnits.PASCAL,
    "Pa s-1" -> SIUnits.PASCAL.divide(SIUnits.SECOND),
    "percent" -> percent,
    "s" -> SIUnits.SECOND,
    "s-1" -> SIUnits.METERS_PER_SECOND.divide(SIUnits.METER),
    "W/m2" -> SIUnits.WATT.divide(SIUnits.METERS_SQUARED),
    "W m-2" -> SIUnits.WATT.divide(SIUnits.METERS_SQUARED),
    "Unknown" -> unknown,
    "" -> DIMENSIONLESS)

  def cfToSou(name: String, unit: String) =
    cfUnits.getOrElse(unit, unknown)
}

object Units extends Units
