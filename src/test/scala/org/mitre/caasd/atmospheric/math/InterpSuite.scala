package org.mitre.caasd.atmospheric.math

// Copyright 2013, The MITRE Corporation.  All rights reserved.

import org.mitre.caasd.atmospheric.TestUtil
import org.scalatest.FunSuite
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.matchers.ShouldMatchers._

import org.mitre.caasd.atmospheric.math._

@RunWith(classOf[JUnitRunner])
class InterpSuite extends FunSuite with TestUtil {
  trait TestSimpleInterpolator {
    val x = Array(-5.0, 0, 2, 3, 4, 10, 20)
    val y = Array(-50.0, 0, 2, 3, 4, 10, 40)
    val xi = Array(-10.0, -5, 0, 10, 12, 20, 21)
  }

  test("interpLinear, extrapNaN") {
    new TestSimpleInterpolator {
      val yi = Interp.interp1(x, y, xi)
      yi should equalWithTolerance(Array(Double.NaN, -50, 0, 10, 16, 40, Double.NaN))
    }
  }

  test("interpLinear, extrapZero") {
    new TestSimpleInterpolator {
      val yi = Interp.interp1(x, y, xi, extrapMethod = ExtrapZero)
      yi should equalWithTolerance(Array(0.0, -50, 0, 10, 16, 40, 0))
    }
  }

  test("interpLinear, extrapNearest") {
    new TestSimpleInterpolator {
      val yi = Interp.interp1(x, y, xi, extrapMethod = ExtrapNearest)
      yi should equalWithTolerance(Array(-50.0, -50, 0, 10, 16, 40, 40))
    }
  }

  test("interpNearest, extrapNaN") {
    new TestSimpleInterpolator {
      val yi = Interp.interp1(x, y, xi, interpMethod = InterpNearest)
      yi should equalWithTolerance(Array(Double.NaN, -50, 0, 10, 10, 40, Double.NaN))
    }
  }

  test("interpNearest, extrapZero") {
    new TestSimpleInterpolator {
      val yi = Interp.interp1(x, y, xi, interpMethod = InterpNearest,
        extrapMethod = ExtrapZero)
      yi should equalWithTolerance(Array(0.0, -50, 0, 10, 10, 40, 0))
    }
  }

  test("interpNearest, extrapNearest") {
    new TestSimpleInterpolator {
      val yi = Interp.interp1(x, y, xi, interpMethod = InterpNearest,
        extrapMethod = ExtrapNearest)
      yi should equalWithTolerance(Array(-50, -50, 0, 10, 10, 40, 40))
    }
  }

  test("interpFloor, extrapNearest") {
    new TestSimpleInterpolator {
      val yi = Interp.interp1(x, y, xi, interpMethod = InterpFloor,
        extrapMethod = ExtrapNearest)
      yi should equalWithTolerance(Array(-50, -50, 0, 10, 10, 40, 40))
    }
  }

  test("interpCeil, extrapNearest") {
    new TestSimpleInterpolator {
      val yi = Interp.interp1(x, y, xi, interpMethod = InterpCeil,
        extrapMethod = ExtrapNearest)
      yi should equalWithTolerance(Array(-50, -50, 0, 10, 40, 40, 40))
    }
  }

  //---------------------------------------------------------------------------
  // Boundary conditions
  //---------------------------------------------------------------------------
  test("singlePoint") {
    new TestSimpleInterpolator {
      val yi = Interp.interp1(x, y, Array(1.0))
      yi should equalWithTolerance(Array(1.0))
    }
  }

  test("Index with decreasing entries should work.") {
    new TestSimpleInterpolator {
      val yi = Interp.interp1(Array(1, 0.0), Array(1.0, 2.0), Array(0.5))
      yi.toArray should equalWithTolerance(Array(1.5))
    }
  }

  test("emptyInput") {
    new TestSimpleInterpolator {
      val yi = Interp.interp1(x, y, Array())
      yi.toArray === List()
    }
  }

  //---------------------------------------------------------------------------
  // Error conditions
  //---------------------------------------------------------------------------

  test("Index x with duplicate entries should fail.") {
    intercept[IllegalArgumentException] {
      val yi = Interp.interp1(Array(1, 0.2, 3, 3, 4), Array(1.0, 2, 3, 4, 5), Array())
    }
  }

  test("Empty x or empty fval should fail.") {
    intercept[IllegalArgumentException] {
      val yi = Interp.interp1(Array(), Array(1.0, 2, 3, 4, 5), Array())
    }
  }

  test("Entries of unequal length should fail.") {
    intercept[IllegalArgumentException] {
      val yi = Interp.interp1(Array(1.0, 2.0), Array(1.0, 2, 3, 4, 5), Array())
    }
  }
}
