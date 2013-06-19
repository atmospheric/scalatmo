import org.mitre.caasd.atmospheric.netcdf.dataset._
import ucar.nc2.dataset.ProjectionCT

object dev {
  println("Welcome to the Scala worksheet")       //> Welcome to the Scala worksheet

  // ---------------------
  // Working
  //val rap = Netcdf(getClass.getResource("/rap_20121022_0000_000.grb2").toString)
  val rap = Netcdf(getClass.getResource("/HRRR.pressure.3km60min.2011-09-06T12_00_00fc00_00.EWR.grib2").toString)
                                                  //> SLF4J: Failed to load class "org.slf4j.impl.StaticLoggerBinder".
                                                  //| SLF4J: Defaulting to no-operation (NOP) logger implementation
                                                  //| SLF4J: See http://www.slf4j.org/codes.html#StaticLoggerBinder for further de
                                                  //| tails.
                                                  //| rap  : org.mitre.caasd.atmospheric.netcdf.dataset.Netcdf = org.mitre.caasd.a
                                                  //| tmospheric.netcdf.dataset.Netcdf@49f4bcf7
 
  //rap.globalAttributes.map(x => println(x))
  //val gfs = Netcdf(getClass.getResource("/gfs_20121113_0000_000.grb2").toString)
  //gfs.modelRunDate
  rap.variables("u-component_of_wind_isobaric").coordSystem.verticalAxis.getName()
                                                  //> res0: String = isobaric
   
  rap.variables.filter(x => x._2.unitsSou != null).map(x => println(x._2) )
                                                  //> Visibility_surface m
                                                  //| u-component_of_wind_height_above_ground ???
                                                  //| Geopotential_height_isobaric m
                                                  //| u-component_of_wind_isobaric ???
                                                  //| Temperature_surface K
                                                  //| Geopotential_height_cloud_base m
                                                  //| Pressure_surface Pa
                                                  //| Geopotential_height_surface m
                                                  //| u-component_of_current_height_above_ground ???
                                                  //| Dew-point_temperature_isobaric K
                                                  //| v-component_of_current_height_above_ground ???
                                                  //| Dew-point_temperature_height_above_ground K
                                                  //| v-component_of_wind_height_above_ground ???
                                                  //| v-component_of_wind_isobaric ???
                                                  //| Potential_temperature_height_above_ground K
                                                  //| Wind_speed_gust_surface ???
                                                  //| Relative_humidity_isobaric ???
                                                  //| Temperature_height_above_ground K
                                                  //| Temperature_isobaric K
                                                  //| Relative_humidity_height_above_ground ???
                                                  //| res1: scala.collection.immutable.Iterable[Unit] = List((), (), (), (), (), (
                                                  //| ), (), (), (), (), (), (), (), (), (), (), (), (), (), ())
                                               
  //rap.variables.filter(x => x._2.unitsSou == null).map(x => println(x._2) )
  
  //gfs.variables.filter(x => x._2.unitsSou != null).map(x => println(x._2) )
  //gfs.variables("Relative_humidity").data.slice(0,0).slice(0,0).slice(0,0)

  //gfs.variables.filter(x => x._2.unitsSou == null).map(x => println(x._2) )
   
  //var gs = d.gd.getGridsets.get(3).getGrids.get(0)
  
  //gs.getShape
  
  //d.modelRunDate
  //d.variableNames take 20
  //d.nc.findVariable("Pressure").getAttributes()
  //d.transforms("Lambert_Conformal")
    
  //d.variables.get("foo")
  //d.transforms.get("Lambert_Conformal")
   
  //d.variables.map(_.toString)
    
  //val p = d.variables("Pressure")
  //p.shape
  //p.coordSystem.latLonToXy((Array(50.0), Array(-90.0)))
  
  //p.nonSingletonDim
  
  //val st = d.variables("Temperature_surface")
  //st.nonSingletonDim
  
  //st.ncVar.getDimensions
  //st.data.getShape
  //st.data.slice(1,20).slice(1,20)
   //p.projection.latLonToProj(-80.0,50.0)
   
  
  // -----------------------------
  //p.coordSystem.alignTrueNorth(Array(0.0),Array(0.0),Array(0.0))
  
  
  
  //p.attributes
  //p.ncVar.getCoordinateSystem
  //p.transform
   
  //d.load("wind")
  
  //var proj = d.netcdf.getCoordinateSystems.get(1).getProjection()
  //var xy = proj.latLonToProj(50, 150)
  //d.location
  
  // CAching experiments.
  //var p = d.netcdf.findVariable("Pressure").read()
  // p.setCaching(true);
  
 // var p = d.netcdf.findVariable("Pressure").read()
  //var nd = p.copyToNDJavaArray()
  
  
}