package org.mitre.caasd;

import org.mitre.caasd.atmospheric.experimental.SApp;

import scala.collection.immutable.Map;
import org.mitre.caasd.atmospheric.netcdf.*;

public class JavaExample {

		public static void main(String[] args) {
        System.out.println("Java Testing: Starting now");
        System.out.println(SApp.helloJava());
       
        /*
         * Testing our API as finalized.
         */
        Netcdf nc = new Netcdf("C:/workspace/atmospheric/atmospheric-core/src/test/resources/HRRR.pressure.3km60min.2011-09-06T12_00_00fc00_00.EWR.grib2");
        /*double [] lat = {40.4277,38.4707,39.6531,41.1621,39.9875,38.6766};
        double [] lon = {-75.9392,-76.5074,-72.8821,-76.125,-76.1822,-73.6701};
        double [] alt = {7328.95,5408.49,4194.35,2581.90,6951.39,2326.05,872.15,7087.39};
        Map<String,double[]> res = Interp3D.apply().interpWind(nc,lat,lon,alt);
        double [] u = res.apply("u-component_of_wind_isobaric");
        double [] v = res.apply("v-component_of_wind_isobaric");
        */
        double [] lat = {40.7746031340106};
        double [] lon = {-74.1174281764852};
        double [] alt = {482.7}; 
        Map<String,double[]> res = Interp3D.apply().interpWind(nc, lat, lon, alt);
        double [] u = res.apply("u-component_of_wind_isobaric");
        double [] v = res.apply("v-component_of_wind_isobaric");
        
        for (int i = 0; i < u.length; i++) {
        	System.out.println(u[i] * 1.94384449 + " " + v[i] * 1.94384449);
        }

        System.out.println("Java testing: Complete");
    }
}
