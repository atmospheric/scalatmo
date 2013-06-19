package org.mitre.caasd.atmospheric.math

// Copyright 2013, The MITRE Corporation.  All rights reserved.

//import scala.language.implicitConversions

/**
 * Interpolation Methods
 *
 * @author Chris Wynnyk
 *
 */
trait Interp {
  implicit def Int2Double(value : Int) : Double = value.toDouble
  implicit def Double2DoubleArray(value : Double) : Array[Double] = Array(value)
  implicit def Int2DoubleArray(value : Int) : Array[Double] = Array(value.toDouble)

  private def evaluateSinglePoint(x: Seq[Double], fval: Seq[Double], pt: Double,
    interpMethod: InterpMethod, extrapMethod: ExtrapMethod): Double = {

    def interpLinear(p1: Double, p2: Double, v1: Double, v2: Double, x: Double): Double =
      v1 + (p2 - x) * (v2 - v1) / (p2 - p1)

    def interpNearest(p1: Double, p2: Double, v1: Double, v2: Double, x: Double): Double =
      if ((p2 - x) < (x - p1)) p2
      else p1

    val ptIdx = x.indexOf(pt)
    if (ptIdx != -1) fval(ptIdx) // Within range, on a point
    else {
      val ceilEntryIdx = x.indexWhere(pt < _)
      ceilEntryIdx match {
        // Less than first element
        case 0 => extrapMethod match {
          case ExtrapNaN => Double.NaN
          case ExtrapZero => 0.0
          case ExtrapNearest => fval.head
        }
        // Greater than last element
        case -1 => extrapMethod match {
          case ExtrapNaN => Double.NaN
          case ExtrapZero => 0.0
          case ExtrapNearest => fval.last
        }
        // Within range, not on a point
        case _ => interpMethod match {
          case InterpLinear => interpLinear(x(ceilEntryIdx - 1), x(ceilEntryIdx),
            fval(ceilEntryIdx), fval(ceilEntryIdx - 1), pt)
          case InterpNearest => interpNearest(x(ceilEntryIdx - 1), x(ceilEntryIdx),
            fval(ceilEntryIdx), fval(ceilEntryIdx - 1), pt)
          case InterpFloor => fval(ceilEntryIdx - 1)
          case InterpCeil => fval(ceilEntryIdx)
        }
      }
    }
  }

  /**
   * 1 Dimensional Interpolation
   *
   * @param x
   * @param fval
   * @param xi
   * @param interpMethod InterpLinear, InterpNearest
   * @param extrapMethod ExtrapNearest, ExtrapNaN, ExtrapZero
   *
   * @note Defaults to Linear interpolation, NaN for out of bounds.
   */
  def interp1(
    x: Array[Double],
    fval: Array[Double],
    xi: Array[Double],
    interpMethod: InterpMethod = InterpLinear,
    extrapMethod: ExtrapMethod = ExtrapNaN): Array[Double] = {

    def isIncreasing[T](x: Iterable[T])(implicit ord: Ordering[T]): Boolean = {
      (x, x.tail).zipped.forall(ord.lt(_, _))
    }

    val xLen = x.length
    val fLen = fval.length
    if (xLen == 0 || fLen == 0)
      throw new IllegalArgumentException("interp1: x and fval must be nonEmpty.")
    
    val isIncr = isIncreasing(x)
    if (xLen != fLen)
      throw new IllegalArgumentException("interp1: x must be same size as fval")
    else {
      if (isIncreasing(x))
        xi.map(evaluateSinglePoint(x, fval, _, interpMethod, extrapMethod))
      else {
        // Allow case of monotonically decreasing numbers (Supports GFS).
        val xrev = x.reverse
        val frev = fval.reverse
        if (isIncreasing(xrev))
          xi.map(evaluateSinglePoint(xrev, frev, _, interpMethod, extrapMethod))
        else
          throw new IllegalArgumentException("interp1: x must be monotonic")
      }
    }
  }
}

object Interp extends Interp
