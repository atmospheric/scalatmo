package org.mitre.caasd.atmospheric.netcdf

// Copyright 2013, The MITRE Corporation.  All rights reserved.

import org.mitre.caasd.atmospheric.math._
import org.joda.time.DateTime
import org.mitre.caasd.atmospheric.math.Interp._

object Interpolate4D {

  /**
   * 4D Interpolation for Netcdf gridded variables.
   *
   * <p>
   * Assumptions:
   *  - Each unique column is non-uniform in the vertical dimension.
   *  - After applying the transform, the grid is laterally uniform in XY.
   *  - Each variable may have a unique lateral transform.
   *  - Some variables may be 2D in space, need to handle robustly.
   * </p>
   *
   * <p>
   * Method:
   *  - Convert altitude to Z using verticalReference
   *  - Iterate over each variable
   *    - Iterate over each file (time dimension)
   *      - Convert lat/lon to X, Y
   *      - Interpolate laterally
   *         - Interpolate vertically
   * </p>
   * 
   */
  def interpolate(
    ncA: Netcdf,
    ncB: Netcdf,
    latitude: Array[Double],
    longitude: Array[Double],
    altitude: Array[Double],
    time: Array[DateTime],
    varnames: List[String]): Map[String, Array[Double]] = {
    
    /*      
      // Temporal interpolation
      val x0 = ncA.effectiveDate.getMillis()
      val x1 = ncB.effectiveDate.getMillis()      
      val y0 = interpSingleFile(ncA)
      val y1 = interpSingleFile(ncB)      
      time.indices.map(i => interp1(Array(x0, x1), Array(y0(i), y1(i)), 
        time(i).getMillis(), temporalInterpMethod)).flatten.toArray
    }
    
    // Iterate over all variables
    (for { v <- varnames } yield (v, interpSingleVariable(v))).toMap*/
    Map("placeholder" -> Array(0.0))
  }

  
  /** 
   * Load then interpolate.  Assumes all dates are within a single hour.
   */
  def managedInterp(
    latitude: Array[Double],
    longitude: Array[Double],
    altitude: Array[Double],
    time: Array[DateTime],
    varnames: List[String],
    verticalReference: VerticalReference = GeometricAltitude,
    lateralInterpMethod: InterpMethod = InterpLinear,
    verticalInterpMethod: InterpMethod = InterpLinear,
    temporalInterpMethod: InterpMethod = InterpLinear,
    verticalExtrapMethod: ExtrapMethod = ExtrapNearest): Map[String, Array[Double]] = {
        
    // Place bacon here
    
    Map("placeholderResult" -> Array(0.0))
  }
  
  /*
   * Next Steps:
   * - Finish moving the stuff below to the function above.
   * - Write interpolation tests:
   *    - Multiple variables
   *    - Multiple times
   *    - Various interpolation methods
   *    - Performance (rough order of magnitude)
   * 	- Verify all conversions, for all file types.
   * 	- Validate against AtmosphericToolbox Results
   *    - Robustly handle strange stuff (mixing ratios, etc.)
   * 
   * - Develop RucFusion code.
   * - Develop AtmosphericInterpolation code.
   * - Replace AtmoToolbox Hadoop interp with Scala library interp.
   * 
   * - Explore using AVRO schema in Scala
   * - Example calling Scala from Java
   * - Example calling Scala from Matlab
   * - Visualization via D3 ? 
   */
  
  
  /*
   
    // These methods should go somewhere else and have option to nearest 6 hour.
    def dateFloorToHour(d: DateTime) = {
      d.hourOfDay.roundFloorCopy
    }

    def dateCeilToHour(d: DateTime) = {
      d.hourOfDay.roundCeilingCopy
    }

    def dateRoundToHour(d: Array[DateTime]) = {
      d.map(_.hourOfDay.roundHalfFloorCopy)
    }


      val n = lat.length;
      val output =  varnames.foreach( x => (x, Array.fill(n)(Double.NaN) ))

      adjustedTime.indices.groupBy(i => adjustedTime(i)).foreach {
        case (uTime, is) => {
          // Indexing to select points associated having uTime
          val lats = is.map(i => lat(i)).toArray
          val lons = is.map(i => lon(i)).toArray
          val alts = is.map(i => alt(i)).toArray

          val res =
            try {
              // Load date for unique time, interpolate
              val nc = Netcdf(uTime)
              nc.interpolate(lats, lons, alts, varnames, verticalReference,
                lateralInterpMethod, verticalInterpMethod, verticalExtrapMethod)
            } catch {
              // If anything goes wrong, we complain and return all NaN
              case e: Exception => {
                println("Failed to load time: " + uTime)
                varnames.foreach( x => (x, Array.fill(is.length)(Double.NaN) ))
              }
            }
          
        }
      }

      Map("placeholderResult" -> Array(0.0))
    }

    // Warn for huge jobs (evaluating a large number of hours).
    //  Priority: Low

    // If all are within 1 sec of on-the-hour, optimize (change interp method to "nearest").
    //   Priority: Low

    // Linear interp requires evaluating hour before and after: duplicate array.
    temporalInterpMethod match {
      case InterpLinear => {
        var adjTime = time;
        interpOnTheHour(latitude, longitude, altitude, adjTime)
      }
      case _ => {
        //        var adjTime = temporalInterpMethod match {
        //          case Interp
        //        }
        var adjTime = time;
        interpOnTheHour(latitude, longitude, altitude, adjTime)
      }
    }
    
    Map("placeholderResult" -> Array(0.0))
  } 
    */
}
