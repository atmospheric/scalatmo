package org.mitre.caasd.atmospheric.netcdf

// Copyright 2013, The MITRE Corporation.  All rights reserved.

import org.eintr.loglady.Logging
import org.mitre.caasd.atmospheric.netcdf.Units.cfToSou
import scala.collection.JavaConverters._
import ucar.nc2.dt.GridDatatype

/**
 * Variable wraps the ucar.nc2.dt.gds with additional functionality.
 *
 * @note
 *   - Immutable object
 *   - Lazy maps for efficient data access
 *   - Units standardized to use SystemOfUnits.
 */
class Variable(val gds: GridDatatype, val coordSystem: CoordSystem) extends Logging {
  /** Attributes mapped by name. */
  lazy val attributes = gds.getAttributes.asScala.map(x => (x.getName(), x)).toMap

  lazy val unitsNetcdf = gds.getUnitsString

  lazy val unitsSou = cfToSou(name, unitsNetcdf)

  lazy val name: String = gds.getName

  /** NetcdfVariable object */
  lazy val ncVar = gds.getVariable

  /** Variable shape */
  lazy val shape = gds.getShape

  /** Non-Singleton Dimensions */
  lazy val nonSingletonDim = this.shape.filter(_ > 1).length

  /** Data - Read in, removing the singleton 'Time' dimension. */
  lazy val data = {
    log.debug("Loaded variable: " + this.name)
    ncVar.read.reduce
  }

  /** To String */
  override def toString() = name + " " + unitsSou
}

/** Companion object as a factory. */
object Variable {
  def apply(gds: ucar.nc2.dt.GridDatatype, coordSystem: CoordSystem) = new Variable(gds, coordSystem)
}
