package org.mitre.caasd.atmospheric.netcdf

// Copyright 2013, The MITRE Corporation.  All rights reserved.

import org.eintr.loglady.Logging
import org.mitre.caasd.atmospheric.math._
import org.mitre.caasd.atmospheric.math.Interp._

/*
 * The Interp3D function provides interpolation on a single Netcdf file.
 * 
 * Key aspects of this function: Immutable, with a fluent API. 
 * 
 * Usage (Scala):
 * <code>
 * Netcdf nc = Netcdf("my_file.grib2")
 * Array[Double] latitude = Array(50.0, 60, 70)
 * Array[Double] longitude = Array(-80.0, -85, -90)
 * Array[Double] altitude = Array(3000.0, 4000, 5000)
 * res = Interp3D.verticalReference(GeometricAltitude)
 *         .withLateralInterp(InterpNearest)
 *         .withVerticalInterp(InterpNearest)
 *         .interp(nc, latitude, longitude, altitude)
 * </code>
 *           
 */
class Interp3D(verticalReference: VerticalReference = GeometricAltitude,
    lateralInterp: InterpMethod = InterpLinear,
    lateralExtrap: ExtrapMethod = ExtrapNaN,
    verticalInterp: InterpMethod = InterpLinear,
    verticalExtrap: ExtrapMethod = ExtrapNearest) extends Logging {
  
  def withVerticalReference(newReference: VerticalReference) = {
    new Interp3D(
        newReference, 
        this.lateralInterp, 
        this.lateralExtrap, 
        this.verticalInterp, 
        this.verticalExtrap)
  }
  
  def withLateralInterp(newExtrap: InterpMethod) = {
    new Interp3D(
        this.verticalReference, 
        newExtrap, 
        this.lateralExtrap, 
        this.verticalInterp, 
        this.verticalExtrap)
  }
  
  def withLateralExtrap(newExtrap: ExtrapMethod) = {
    new Interp3D(
      this.verticalReference,
      this.lateralInterp, 
      newExtrap,
      this.verticalInterp, 
      this.verticalExtrap)
  }

  def withVerticalInterp(newInterp: InterpMethod) = {
    new Interp3D(
        this.verticalReference, 
        this.lateralInterp, 
        this.lateralExtrap, 
        newInterp, 
        this.verticalExtrap)
  }
  
  def withVerticalExtrap(newExtrap: ExtrapMethod) = {
    new Interp3D(
      this.verticalReference,
      this.lateralInterp, 
      this.lateralExtrap,
      this.verticalInterp, 
      newExtrap)
  }
  
  def interp(
    nc: Netcdf,
    latitude: Array[Double],
    longitude: Array[Double],
    altitude: Array[Double],
    varnames: List[String]): Map[String, Array[Double]] = {
     
    /*
    log.debug("Interp3D with the following settings:")
    log.debug("VerticalReference: " + this.verticalReference)
    log.debug("LateralInterp: " + this.lateralInterp)
    log.debug("LateralExtrap: " + this.lateralExtrap)
    log.debug("VerticalInterp: " + this.verticalInterp)
    log.debug("VerticalExtrap: " + this.verticalExtrap)
    */
    
    log.debug("Scala interpolating %d points.", latitude.length)
    
    // Vertical transform is independent of file and variable.
    val z = verticalReference.verticalTransform(altitude, latitude, longitude)

    def interpSingleVariable(varname: String): Array[Double] = {
  		val variable = nc.variables.getOrElse(varname,
  		  nc.variables.getOrElse(varname + "_hybrid", 
  		    nc.variables.getOrElse(varname + "_pressure",
  		      nc.variables.getOrElse(varname + "_isobaric", {
  		        nc.printVar()
  		        null
  		      }))))
  		val xLen = variable.coordSystem.xLen
  		val yLen = variable.coordSystem.yLen
  		
  		// Set vertical reference frame
  		val vv = verticalReference.verticalVariable
  		val vertVar = nc.variables.getOrElse(vv,
  		  nc.variables.getOrElse(vv + "_hybrid", 
  		    nc.variables.getOrElse(vv + "_pressure",
  		      nc.variables.getOrElse(vv + "_isobaric", null))))

  		// Convert all (lat,lon) to (x,y), providing a uniformly spaced lateral grid.  
  		val (x, y) = variable.coordSystem.latLonToXy((latitude, longitude))

  		// Convert fancy UCAR Array to simple Java 1-D Array
  		implicit def ncArray2ArrayDouble(arr: ucar.ma2.Array): Array[Double] =
  			arr.get1DJavaArray(java.lang.Double.TYPE).asInstanceOf[Array[Double]]

    		// 1D interpolation in vertical dimension.
    		def interpInZ(xi: Int, yi: Int, zi: Double): Double = {
    			variable.nonSingletonDim match {
		    		case 1 => variable.data.slice(0, 0).slice(0, 0)(0)
		    		case 2 => variable.data.slice(0, yi).slice(0, xi)(0)
		    		case 3 => {
		    			val z = vertVar.data.slice(1, yi).slice(1, xi)
	  					val zVal = variable.data.slice(1, yi).slice(1, xi)
		    				interp1(z, zVal, zi, verticalInterp, verticalExtrap)(0)
		    		}
		    		case x => throw new IllegalArgumentException(
	    				x + "-dimensional interpolation not yet supported.")
	    		}
	    	}

    	// 2D lateral interpolation in X, Y plane
    	def interpInXY(x: Double, y: Double, z: Double): Double = {
    		val x0 = x.floor.toInt
				val x1 = x0 + 1
				val y0 = y.floor.toInt
				val y1 = y0 + 1

				if ((x0 < 0) || (y0 < 0) || x1 >= xLen || y1 >= yLen) Double.NaN
				else {
					val y0res = interp1(
						Array(x0, x1),
						Array(interpInZ(x0, y0, z), interpInZ(x1, y0, z)),
						Array(x),
						lateralInterp)
					val y1res = interp1(Array(x0, x1),
						Array(interpInZ(x0, y1, z), interpInZ(x1, y1, z)),
						Array(x),
						lateralInterp)
						interp1(Array(y0, y1), Array(y0res(0), y1res(0)), Array(y), lateralInterp)(0)
				}
    	}

    	// Iterate over all points
    	(x, y, z).zipped.map(interpInXY)
    }
      
    // Iterate over all variables
    (for { v <- varnames } yield (v, interpSingleVariable(v))).toMap
  }
  
  def interpWind(
    nc: Netcdf,
    latitude: Array[Double],
    longitude: Array[Double],
    altitude: Array[Double]): Map[String, Array[Double]] = 
  {
    val uWind = "u-component_of_wind_isobaric"
    val vWind = "v-component_of_wind_isobaric"
    val res = interp(nc,latitude,longitude,altitude,List(uWind,vWind))
    val (u,v) =  nc.variables(uWind).coordSystem.alignTrueNorth(
        res(uWind).toArray, res(vWind).toArray, longitude)
    Map(uWind -> u.toArray, vWind -> v.toArray)    
  }
}

object Interp3D {
  def apply() = new Interp3D
}
