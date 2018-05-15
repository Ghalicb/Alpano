package ch.epfl.alpano.summit;

/**
 *
 * @author Ghali Chraibi (262251)
 * @author Niels Poulsen (270494)
 */

import static org.junit.Assert.*;
import static java.lang.Math.*;

import org.junit.Test;
import ch.epfl.alpano.GeoPoint;

public class SummitTest {

    @Test
    public void SummitDisplayTest() {
        Summit s1 = new Summit("Mont des cochons",
                new GeoPoint(PI / 5, -PI / 2), 4600);
        assertEquals("Mont des cochons (36.0000,-90.0000) 4600", s1.toString());
    }

    @Test
    public void SummitBigDisplayTest() {
        Summit s1 = new Summit("Mont des cochons",
                new GeoPoint(PI / 5, -PI / 2), 4600);
        Summit s2 = new Summit("Arène légendaire",
                new GeoPoint(64 * PI / 75, -7 * PI / 41), 2612);
        assertEquals("Mont des cochons (36.0000,-90.0000) 4600", s1.toString());
        assertEquals(
                "Mont des cochons (36.0000,-90.0000) 4600"
                        + "Arène légendaire (153.6000,-30.7317) 2612",
                s1.toString() + s2.toString());
    }

    @Test
    public void SummitDisplayStrangeFormatTest() {
        Summit s1 = new Summit("EVEREST", new GeoPoint(11*PI/17, PI/2), 1905394928);
        assertEquals("EVEREST (116.4706,90.0000) 1905394928", s1.toString());
        
    }

}
