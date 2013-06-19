package org.mitre.caasd.atmospheric.experimental

// Copyright 2013, The MITRE Corporation.  All rights reserved.

import org.mitre.caasd.atmospheric.math.{InterpMethod, InterpLinear};
import org.mitre.caasd.atmospheric.netcdf._;

/*
 * Our HelloWorld App, for use in testing compiled Scala jars from Java and Matlab.
 */

class SHelloWorld {
  def printMessage() = {
    println("Hello from Scala!")
  }
}

object SHelloWorld {
  def main(arguments: Array[String]) = {
    new SHelloWorld().printMessage()
  }
}

object SApp {
  def helloJava():String = "Hello from Scala!"
}

/*
 * Below is one failed attempt at a fluent interface that is both Java/Scala friendly.
 * 
 *  The problem with this implementation is two-fold:
 *  1) Lots of boilerplate, not immediately apparent what it does
 *  2) When you go to use the parameters, you have to cast. Painful.
 */
trait Parameters {
  def parameters: Map[String, Any] = Map.empty
}

trait InterpParameters extends Parameters {
  var _verticalMethod: Option[InterpMethod] = Some(InterpLinear)
  var _horizontalMethod: Option[Int] = Some(1)
  var _verticalReference: Option[VerticalReference] = Some(GeometricAltitude)
  
  def verticalMethod(newVerticalMethod: InterpMethod): this.type = {
    _verticalMethod = Some(newVerticalMethod); this
  } 
  
  def horizontalMethod(newHorizontalMethod: Int): this.type = {
    _horizontalMethod = Some(newHorizontalMethod); this
  }
  
  def verticalReference(newVerticalReference: VerticalReference): this.type = {
    _verticalReference = Some(newVerticalReference); this
  }
  
  override def parameters = super.parameters ++
    _verticalMethod.map("verticalMethod" -> _) ++
    _horizontalMethod.map("horizontalMethod" -> _) ++
    _verticalReference.map("verticalReference" -> _)
}

class InterpMe extends InterpParameters {
  
  def interp(x: Int, y: Int)  {
    println(this)
    println("x = " + x)
    println("y = " + y)
  }
  
  override def toString() : String = {
    "VerticalMethod = " + this.parameters("verticalMethod") + "\n" +
    "HorizontalMethod = " + this.parameters("horizontalMethod") + "\n" +
    "VerticalReference = " + this.parameters("verticalReference")
    
  }
}

object InterpMe {
  def apply() = new InterpMe
}
