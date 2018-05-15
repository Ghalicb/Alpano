package ch.epfl.alpano;

import ch.epfl.alpano.PanoramaParameters;
import static java.lang.Math.*;
import static org.junit.Assert.*;

import org.junit.Test;

/**
 * 
 *
 *
 */

public class PanoramaParametersTest {
	
	PanoramaParameters pp=new PanoramaParameters(new GeoPoint(0,0),1000,PI/12, PI/3, 100000, 8, 8);
	
	@Test
	public void xForAzimuthWorksOnLimitCases() {
		assertEquals(0, pp.xForAzimuth((23.0000000000000001*PI)/12.0), 1e-10); //works
		assertEquals(3.5, pp.xForAzimuth(PI/12.0), 1e-10); //works
		assertEquals(7, pp.xForAzimuth(2.999999999999999*PI/12.0), 1e-10); //works
	}
	
	@Test
	public void azimuthForXWorksOnLimitCases() {
		assertEquals(23*PI/12, pp.azimuthForX(0), 1e-5);//works
		assertEquals(3*PI/12, pp.azimuthForX(7), 1e-5);//works
	}
	
	@Test
	public void xForAzimuthWorksOnGenericCases(){
		assertEquals(1, pp.xForAzimuth(23*PI/12.0 + PI/21.0), 1e-10);
	}
	
	@Test 
	public void azimuthForXWorksOnGenericCases(){
		assertEquals(23*PI/12 + PI/21, pp.azimuthForX(1), 1e-5);
	}
	
	@Test
	public void yForAltitudeWorksOnLimitCases() {
		assertEquals(3.5, pp.yForAltitude(0), 1e-5);
		assertEquals(0, pp.yForAltitude(pp.verticalFieldOfView()/2), 1e-5);
		assertEquals(7, pp.yForAltitude(-pp.verticalFieldOfView()/2), 1e-5);
	}
	
	@Test
	public void yForAltitudeWorksOnGenericCases() {
		assertEquals(2, pp.yForAltitude(3*PI/42), 1e-5);
		assertEquals(5, pp.yForAltitude(-3*PI/42), 1e-5);
	}
	
	@Test
	public void altitudeForYWorksOnLimitCases() {
		assertEquals(PI/6, pp.altitudeForY(0), 1e-5);
		assertEquals(0, pp.altitudeForY(3.5), 1e-5);
		assertEquals(-PI/6, pp.altitudeForY(7), 1e-5);
	}
	
	@Test
	public void altitudeForYWorksOnGenericCases() {
		assertEquals(PI/21, pp.altitudeForY(2.5), 1e-5);
		assertEquals(-PI/21, pp.altitudeForY(4.5), 1e-5);
		assertEquals(-5*PI/42, pp.altitudeForY(6), 1e-5);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void xForAzimuthThrowsWellOne(){
		pp.xForAzimuth(22.99999*PI/12);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void xForAzimuthThrowsWellTwo(){
		pp.xForAzimuth(3*PI/12);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void xForAzimuthThrowsWellThree(){
		pp.xForAzimuth(37*PI/12);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void azimuthForXThrowsWellOne(){
		pp.azimuthForX(7.01);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void azimuthForXThrowsWellTwo(){
		pp.azimuthForX(-2);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void yForAltitudeThrowsWellOne(){
		pp.yForAltitude(PI/5);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void yForAltitudeThrowsWellTwo(){
		pp.yForAltitude(-PI/5);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void yForAltitudeThrowsWellThree(){
		pp.yForAltitude(PI);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void altitudeForYThrowsWellOne(){
		pp.altitudeForY(-0.0001);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void altitudeForYThrowsWellTwo(){
		pp.altitudeForY(-1);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void altitudeForYThrowsWellThree(){
		pp.altitudeForY(7.05);
	}
	
}


