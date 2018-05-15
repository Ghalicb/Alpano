/**
 * 
 */
package ch.epfl.alpano;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * @author Jean Chambras (271630)
 * @author Aymeri Servanin (272661)
 *
 */
public class Interval2DTest01 {

    @Test
    public void testiXAndiYStdCase() {
        Interval2D i1 = new Interval2D(new Interval1D(4, 7),
                new Interval1D(8, 11));
        assertEquals(new Interval1D(4, 7), i1.iX());
        assertEquals(new Interval1D(8, 11), i1.iY());

    }

    @Test
    public void testiXAndiYNonStdCase() {
        Interval2D i1 = new Interval2D(new Interval1D(0, 0),
                new Interval1D(0, 0));
        assertEquals(new Interval1D(0, 0), i1.iX());
        assertEquals(new Interval1D(0, 0), i1.iY());

    }

    @Test
    public void testContainsStdCase() {
        Interval2D i1 = new Interval2D(new Interval1D(4, 7),
                new Interval1D(8, 11));
        Interval2D i2 = new Interval2D(new Interval1D(0, 0),
                new Interval1D(0,0));
        assertEquals(true, i1.contains(5, 9));
        assertEquals(false, i1.contains(11, 9));
        assertEquals(false, i1.contains(111, 99));
        assertEquals(true, i2.contains(0, 0) );

    }
    
    @Test
    public void testSizeStdCase(){
        Interval2D i1 = new Interval2D(new Interval1D(4, 7),
                new Interval1D(8, 11));
        Interval2D i2 = new Interval2D(new Interval1D(0, 0),
                new Interval1D(0, 0));
        assertEquals(1, i2.size());
    }
    @Test
    public void testSizeNonStdCase(){
        Interval2D i1 = new Interval2D(new Interval1D(0, 0),
                new Interval1D(6, 9));
        assertEquals(4, i1.size());
    }
    
    @Test
    public void testSizeOfIntersectionWithStdCase(){
        Interval2D i1 = new Interval2D(new Interval1D(5, 7),
                new Interval1D(6, 9));
        assertEquals(4, i1.sizeOfIntersectionWith(new Interval2D(new Interval1D(6, 7),
                new Interval1D(8, 9))));
    }
    @Test
    public void testSizeOfIntersectionWithNonStdCase(){
        Interval2D i1 = new Interval2D(new Interval1D(0, 0),
                new Interval1D(6, 9));
        assertEquals(2, i1.sizeOfIntersectionWith(new Interval2D(new Interval1D(0, 2),
                new Interval1D(8, 9))));
    }
    @Test 
    public void testBoundingUnionStdCase(){
        Interval2D i1 = new Interval2D(new Interval1D(5, 7),
                new Interval1D(6, 9));
        Interval2D i2 = new Interval2D(new Interval1D(8, 11),
                new Interval1D(2, 8));
        assertEquals(new Interval2D(new Interval1D(5,11),
                new Interval1D(2, 9)), i1.boundingUnion(i2));
    }
    @Test
    public void testIsUnionableWith(){
        Interval2D i1 = new Interval2D(new Interval1D(5, 7),
                new Interval1D(6, 9));
        Interval2D i2 = new Interval2D(new Interval1D(5, 7),
                new Interval1D(2, 8));
        Interval2D i3 = new Interval2D(new Interval1D(9, 11),
                new Interval1D(14, 20));
        assertEquals(true, i1.isUnionableWith(i2));
        assertEquals(false, i1.isUnionableWith(i3));
    }
}
