package org.mitre.caasd.atmospheric.netcdf

// Copyright 2013, The MITRE Corporation.  All rights reserved.

import org.mitre.caasd.atmospheric.math._
import org.mitre.caasd.atmospheric.math.Interp._

// Interp3()
class Interp3 {
  private var verticalReference : VerticalReference = GeometricAltitude;
  private var lateralInterpMethod: InterpMethod = InterpLinear;
  private var verticalInterpMethod: InterpMethod = InterpLinear;
  private var temporalInterpMethod: InterpMethod = InterpLinear;
  private var lateralExtrapMethod: ExtrapMethod = ExtrapNearest;
  private var verticalExtrapMethod: ExtrapMethod = ExtrapNearest;
  private var temporalExtrapMethod: ExtrapMethod = ExtrapNearest;
  
  def setVerticalReference(verRef : VerticalReference) : this.type = {
    verticalReference = verRef
    this
  }
  def setLateralInterpMethod(latMethod : InterpMethod) : this.type = {
    lateralInterpMethod = latMethod
    this
  }
  def setVerticalInterpMethod(vertMethod : InterpMethod) : this.type = {
    verticalInterpMethod = vertMethod 
    this
  }
  def setTemporalInterpMethod(tempMethod : InterpMethod) : this.type = {
    temporalInterpMethod = tempMethod
    this
  }
 
  def Builder() {
    
  }
  
  class Interpolator private () {
    
  }   
  
  def interpolate(
    latitude: Array[Double],
    longitude: Array[Double],
    altitude: Array[Double]) {
    
  }
  
  
}
/*
trait InterpOptions {
  var verticalReference : VerticalReference = GeometricAltitude;
  var lateralInterpMethod: InterpMethod = InterpLinear;
  var verticalInterpMethod: InterpMethod = InterpLinear;
  var temporalInterpMethod: InterpMethod = InterpLinear;
  var lateralExtrapMethod: ExtrapMethod = ExtrapNearest;
  var verticalExtrapMethod: ExtrapMethod = ExtrapNearest;
  var temporalExtrapMethod: ExtrapMethod = ExtrapNearest;

  def setVerticalReference(verRef : VerticalReference) : this.type = {
    verticalReference = verRef
    this
  }
  def setLateralInterpMethod(latMethod : InterpMethod) : this.type = {
    lateralInterpMethod = latMethod
    this
  }
  def setVerticalInterpMethod(vertMethod : InterpMethod) : this.type = {
    verticalInterpMethod = vertMethod 
    this
  }
  def setTemporalInterpMethod(tempMethod : InterpMethod) : this.type = {
    temporalInterpMethod = tempMethod
    this
  }  
}

*/