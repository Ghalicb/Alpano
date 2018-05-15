package ch.epfl.alpano.gui;

import static org.junit.Assert.*;

import java.util.EnumMap;
import java.util.Map;
import static  ch.epfl.alpano.gui.UserParameter.*;
import org.junit.Test;

/**
 * 
 *
 * @author Ghali Chraibi (262251)
 * @author Niels Poulsen (270494)
 */

public class PanoramaUserParametersTest {
	
	@Test(expected=NullPointerException.class)
	public void cantInstantiateWithEmptyMap() {
		Map<UserParameter, Integer> panorama=null;
		PanoramaUserParameters p=new PanoramaUserParameters(panorama);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void cantInstantiateWithHalfFullMap1() {
		Map<UserParameter, Integer> panorama = new EnumMap<>(UserParameter.class);
		PanoramaUserParameters p=new PanoramaUserParameters(panorama);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void cantInstantiateWithHalfFullMap2() {
		Map<UserParameter, Integer> panorama = new EnumMap<>(UserParameter.class);
		panorama.put(OBSERVER_LONGITUDE, -5);
		panorama.put(OBSERVER_LATITUDE, -5);
		panorama.put(OBSERVER_ELEVATION, -5);
		panorama.put(CENTER_AZIMUTH, -5);
		panorama.put(HORIZONTAL_FIELD_OF_VIEW, -5);
		panorama.put(MAX_DISTANCE, -5);
		panorama.put(WIDTH, -5);
		panorama.put(HEIGHT, -5);
		PanoramaUserParameters p=new PanoramaUserParameters(panorama);
	}
	
	@Test
	public void sanitizesCorrectly1() {
		Map<UserParameter, Integer> panorama = new EnumMap<>(UserParameter.class);
		panorama.put(OBSERVER_LONGITUDE, -5);
		panorama.put(OBSERVER_LATITUDE, -5);
		panorama.put(OBSERVER_ELEVATION, -5);
		panorama.put(CENTER_AZIMUTH, -5);
		panorama.put(HORIZONTAL_FIELD_OF_VIEW, -5);
		panorama.put(MAX_DISTANCE, -5);
		panorama.put(WIDTH, -5);
		panorama.put(HEIGHT, -5);
		panorama.put(SUPER_SAMPLING_EXPONENT, -1);
		PanoramaUserParameters p=new PanoramaUserParameters(panorama);
		assertEquals(60000, p.observerLongitude(), 0);
		assertEquals(450000, p.observerLatitude(), 0);
		assertEquals(300, p.observerElevation(), 0);
		assertEquals(0, p.centerAzimuth(), 0);
		assertEquals(1,p.horizontalFieldOfView(), 0);
		assertEquals(10,p.maxDistance() , 0);
		assertEquals(30,p.width() , 0);
		assertEquals(10,p.height() , 0);
		assertEquals(0, p.superSamplingExponent() , 0);
	}

	@Test
	public void sanitizesCorrectly2() {
		Map<UserParameter, Integer> panorama = new EnumMap<>(UserParameter.class);
		panorama.put(OBSERVER_LONGITUDE, 100000000);
		panorama.put(OBSERVER_LATITUDE, 100000000);
		panorama.put(OBSERVER_ELEVATION, 100000000);
		panorama.put(CENTER_AZIMUTH, 100000000);
		panorama.put(HORIZONTAL_FIELD_OF_VIEW, 100000000);
		panorama.put(MAX_DISTANCE, 100000000);
		panorama.put(WIDTH, 100000000);
		panorama.put(HEIGHT, 100000000);
		panorama.put(SUPER_SAMPLING_EXPONENT, 100000000);
		PanoramaUserParameters p=new PanoramaUserParameters(panorama);
		assertEquals(120000, p.observerLongitude(), 0);
		assertEquals(480000, p.observerLatitude(), 0);
		assertEquals(10000, p.observerElevation(), 0);
		assertEquals(359, p.centerAzimuth(), 0);
		assertEquals(360,p.horizontalFieldOfView(), 0);
		assertEquals(600,p.maxDistance() , 0);
		assertEquals(16000,p.width() , 0);
		assertEquals(4000,p.height() , 0);
		assertEquals(2, p.superSamplingExponent() , 0);
	}

}
