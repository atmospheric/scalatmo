package org.mitre.caasd.atmospheric.math

// Copyright 2013, The MITRE Corporation.  All rights reserved.

sealed abstract class ExtrapMethod
case object ExtrapNaN extends ExtrapMethod
case object ExtrapNearest extends ExtrapMethod
case object ExtrapZero extends ExtrapMethod