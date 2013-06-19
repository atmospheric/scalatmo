package org.mitre.caasd.atmospheric

// Copyright 2013, The MITRE Corporation.  All rights reserved.

import org.scalatest.matchers.{ Matcher, MatchResult }
import org.scalatest.matchers.ShouldMatchers._

trait TestUtil {
  val NaN = Double.NaN;

  implicit def Int2Double(value: Int): Double = value.toDouble
  implicit def Double2Array(value: Double): Array[Double] = Array(value)
  implicit def ArrayInt2ArrayDouble(value: Array[Int]): Array[Double] = value.map(_.toDouble)

  def equalWithTolerance(right: Array[Double], tol: Double = 10e-8) = {

    def pretty(a: Array[Double]) = a.mkString("Array(", ",", ")")

    Matcher { (left: Array[Double]) =>
      MatchResult(
        (left zip right) forall { case (a, b) => a <= b + tol && a >= b - tol || (a.isNaN && b.isNaN) },
        pretty(left) + " did not equal " + pretty(right) + " with tolerance " + tol,
        pretty(left) + " equaled " + pretty(right) + " with tolerance " + tol)
    }
  }
}
