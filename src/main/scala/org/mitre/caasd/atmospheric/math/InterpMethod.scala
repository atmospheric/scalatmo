package org.mitre.caasd.atmospheric.math

// Copyright 2013, The MITRE Corporation.  All rights reserved.

sealed abstract class InterpMethod
case object InterpLinear extends InterpMethod
case object InterpNearest extends InterpMethod
case object InterpFloor extends InterpMethod
case object InterpCeil extends InterpMethod
