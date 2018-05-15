/**
 * 
 */
package ch.epfl.alpano;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * @author Jean Chambras (271630)
 * @author Aymeri Servanin (272661)
 *
 */
public class Interval1DTest01 {

    @Test(expected = IllegalArgumentException.class)
    public void testException() {
        Interval1D i1 = new Interval1D(50,0);
    }
    
    @Test
    public void HandlesSingleton(){
        Interval1D i1 = new Interval1D(0,0);
        assertTrue(i1.contains(0));
        assertFalse(i1.contains(50));
    }
    
    @Test
    public void testSize(){
        Interval1D i1 = new Interval1D(2,3);
        assertEquals(2,i1.size(),0);
    }
    @Test
    public void testSingletonSize(){
        Interval1D i1 = new Interval1D(0,0);
        assertEquals(1,i1.size(),0);
    }
    @Test
    public void testIntersectionSizeOnStdCase(){
        Interval1D i1 = new Interval1D(3,5);
        Interval1D i2 = new Interval1D(4,6);
        assertEquals(2,i1.sizeOfIntersectionWith(i2),0);
    }
    
    @Test
    public void testIntersectionSizeOnNTCase(){
        Interval1D i1 = new Interval1D(0,0);
        Interval1D i2 = new Interval1D(0,0);
        assertEquals(1,i1.sizeOfIntersectionWith(i2),0);
        assertEquals(1,i1.sizeOfIntersectionWith(i1),0);
        
        Interval1D i3 = new Interval1D(0,10);
        Interval1D i4 = new Interval1D(20,30);
        assertEquals(0,i3.sizeOfIntersectionWith(i4),0);
        
        Interval1D i5 = new Interval1D(2,3);
        Interval1D i6 = new Interval1D(1,5);
        assertEquals(2,i6.sizeOfIntersectionWith(i5),0);
        
        Interval1D i7 = new Interval1D(4,6);
        Interval1D i8 = new Interval1D(3,5);
        assertEquals(2,i7.sizeOfIntersectionWith(i8),0);
        
    }
    @Test
    public void testboundingUnionStdCase(){
        Interval1D i1 = new Interval1D(1,9);
        Interval1D i2 = new Interval1D(8,78);
        assertEquals(new Interval1D(1, 78),i1.boundingUnion(i2));

    }

    @Test
    public void testboundingUnionNonStdCase(){
        Interval1D i1 = new Interval1D(0,0);
        Interval1D i2 = new Interval1D(8,78);
        Interval1D i3 = new Interval1D(0,0);

        assertEquals(new Interval1D(0, 78),i1.boundingUnion(i2));
        assertEquals(new Interval1D(0, 0),i1.boundingUnion(i3));
        assertEquals(new Interval1D(8, 78),i2.boundingUnion(i2));


    }
    @Test
    public void testIsUnionableWithStdCase(){
        Interval1D i1 = new Interval1D(3,190);
        Interval1D i2 = new Interval1D(8,78);
        Interval1D i3 = new Interval1D(79,200);
        Interval1D i4 = new Interval1D(202,203);

        assertEquals(true, i1.isUnionableWith(i2));
        assertEquals(true, i2.isUnionableWith(i3));
        assertEquals(false, i3.isUnionableWith(i4));
    }
    @Test
    public void testIsUnionableWithNonStdCase(){
        Interval1D i1 = new Interval1D(0,0);
        Interval1D i2 = new Interval1D(0,0);
        Interval1D i3 = new Interval1D(79,200);
        Interval1D i4 = new Interval1D(1,1);

        assertEquals(true, i2.isUnionableWith(i2));
        assertEquals(false, i1.isUnionableWith(i3));
    }
    
    @Test 
    public void testUnionWithStdCase(){
        Interval1D i1 = new Interval1D(45,60);
        Interval1D i2 = new Interval1D(50,62);
        

        assertEquals(new Interval1D(45,62), i1.union(i2));
    }
    @Test(expected = IllegalArgumentException.class)
    public void testUnionWithException(){
        Interval1D i1 = new Interval1D(45,60);
        Interval1D i2 = new Interval1D(62,66);
        i1.union(i2);
    }
    

    @Test 
    public void testUnionWithNonStdCase(){
        Interval1D i1 = new Interval1D(0,0);
        Interval1D i2 = new Interval1D(0,0);
        assertEquals(new Interval1D(0,0), i1.union(i2));
    }
    
    
}
