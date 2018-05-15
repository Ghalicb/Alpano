package ch.epfl.alpano;

/**
 * Test the class GeoPoint
 *
 * @author Ghali Chraibi (262251)
 * @author Niels Poulsen (270494)
 */

import static org.junit.Assert.assertEquals;
import static ch.epfl.alpano.Math2.PI2;
import static java.lang.Math.PI;
import static java.lang.Math.toRadians;

import org.junit.Test;

public class GeoPointTest {

    // Test Constructor

    @Test(expected = IllegalArgumentException.class)
    public void testConstructionFailLatitude() {
        GeoPoint g1 = new GeoPoint(PI / 2, PI);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructionFailLongitude() {
        GeoPoint g1 = new GeoPoint(PI2, PI / 3);
    }

    // distanceTo

    @Test
    public void isDistanceToWorking1() {
        GeoPoint g1 = new GeoPoint(toRadians(6.56410), toRadians(46.54234));
        GeoPoint g2 = new GeoPoint(toRadians(7.81415), toRadians(46.52350));

        double expectedDistance = 96040;

        assertEquals(expectedDistance, g2.distanceTo(g1), 500);
    }

    @Test
    public void isDistanceToWorking2() {

        GeoPoint g1 = new GeoPoint(toRadians(6.631), toRadians(46.521));
        GeoPoint g2 = new GeoPoint(toRadians(37.623), toRadians(55.753));

        double expectedDistance = 2370000;

        assertEquals(expectedDistance, g2.distanceTo(g1), 5000);
    }

    @Test
    public void isDistanceToWorking3() {

        GeoPoint g1 = new GeoPoint(toRadians(6.56732), toRadians(46.51775));
        GeoPoint g2 = new GeoPoint(toRadians(8.00529), toRadians(46.57760));

        double expectedDistance = 110490;

        assertEquals(expectedDistance, g2.distanceTo(g1), 400);
    }

    // azimuthTo

    @Test
    public void isAzimuthTestWorking1() {

        GeoPoint g1 = new GeoPoint(toRadians(6.631), toRadians(46.521));
        GeoPoint g2 = new GeoPoint(toRadians(37.623), toRadians(55.753));

        double expectedAzimuth = toRadians(52.95);

        assertEquals(expectedAzimuth, g1.azimuthTo(g2), 1E-4);
    }

    @Test
    public void isAzimuthTestWorking2() {

        GeoPoint g1 = new GeoPoint(toRadians(6.56732), toRadians(46.51775));
        GeoPoint g2 = new GeoPoint(toRadians(8.00529), toRadians(46.57760));

        double expectedAzimuth = toRadians(86.67);

        assertEquals(expectedAzimuth, g1.azimuthTo(g2), 0.02);
    }

    // toString

    @Test
    public void toStringTest() {
        GeoPoint g1 = new GeoPoint(toRadians(7.6543), toRadians(54.3210));
        assertEquals("(7.6543,54.3210)", g1.toString());
    }

}
