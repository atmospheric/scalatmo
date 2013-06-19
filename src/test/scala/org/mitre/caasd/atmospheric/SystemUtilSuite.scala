package org.mitre.caasd.atmospheric

// Copyright 2013, The MITRE Corporation.  All rights reserved.

import org.scalatest.FunSuite
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.matchers.ShouldMatchers._

import org.mitre.caasd.atmospheric.SystemUtil._

@RunWith(classOf[JUnitRunner])
class SystemUtilSuite extends FunSuite with TestUtil {
  trait TestDataset {
    val empty = getClass.getResource("/emptyfile.txt").toString
    val rap = getClass.getResource("/rap_20121022_0000_000.grb2").toString
  }

  // No good independent way to test these...
  //  test("Computer") {
  //    println("OS = " + SystemUtil.computer)
  //  } 
  //  
  //  test("getHostname") {
  //    println(SystemUtil.hostname)
  //  }

  test("isHadoop") {
    SystemUtil.isHadoop should equal(false)
  }

  test("Fullfile") {
    val str = SystemUtil.fullfile("C:", "one", "two.scala")
    str should equal("C:" + filesep + "one" + filesep + "two.scala")
  }

  test("exist- nonempty") {
    new TestDataset {
      exist(rap) should equal(true)
    }
  }

  test("exist- nonempty but below minimum size") {
    new TestDataset {
      exist(rap, 50000000) should equal(false)
    }
  }

  test("exist - empty") {
    new TestDataset {
      exist(empty) should equal(false)
    }
  }

  test("exist - empty but gte min size of 0") {
    new TestDataset {
      println(empty)
      exist(empty, 0) should equal(true)
    }
  }

  test("exist- nonexistent directory") {
    exist("nonexistentDir/myNonexistentFile") should equal(false)
  }
}
