package org.mitre.caasd.atmospheric.worksheets

import ucar.nc2.dataset.NetcdfDataset;
import ucar.nc2.dataset.NetcdfDatasetInfo;
import org.mitre.caasd.atmospheric.netcdf.dataset._
import ucar.nc2.time.CalendarDateUnit

object Devel {
  println("Welcome to the Scala worksheet")       //> Welcome to the Scala worksheet
  //val a = NetcdfDataset.openDataset(getClass.getResource("/rap_20121022_0000_000.grb2").toString)
  //val  a = new NetcdfDatasetInfo("C:/workspace/atmospheric/atmospheric-core/src/test/resources/rap_20121022_0000_000.grb2")
  //a.writeXML
  
  //val x = Array(3.0,3,1,1,5)
  //val y = Array(1.0,2,3,4,5)
  //val z = Array.fill(x.length)(Double.NaN)
  
  //x.indices.groupBy(i=> x(i)).foreach {
	//	case (xi, is) => println(is.toArray)//is.foreach(i => z(i) = y(i)*2)
  //}
  //z
  
  val a = Netcdf(getClass.getResource("/gfs_20121113_0000_000.grb2").toString)
                                                  //> SLF4J: Failed to load class "org.slf4j.impl.StaticLoggerBinder".
                                                  //| SLF4J: Defaulting to no-operation (NOP) logger implementation
                                                  //| SLF4J: See http://www.slf4j.org/codes.html#StaticLoggerBinder for further de
                                                  //| tails.
                                                  //| a  : org.mitre.caasd.atmospheric.netcdf.dataset.Netcdf = org.mitre.caasd.atm
                                                  //| ospheric.netcdf.dataset.Netcdf@4311771e
  val v = a.nc.findVariable("time")               //> v  : ucar.nc2.Variable =    int time(time=1);
                                                  //|      :units = "Hour since 2012-11-13T00:00:00Z";
                                                  //|      :standard_name = "time";
                                                  //|      :_CoordinateAxisType = "Time";
                                                  //| 
  CalendarDateUnit.of("gregorian",v.getUnitsString).getBaseCalendarDate
                                                  //> res0: ucar.nc2.time.CalendarDate = 2012-11-13T00:00:00Z
}