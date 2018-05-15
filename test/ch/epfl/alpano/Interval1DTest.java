package ch.epfl.alpano;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * 
 *
 * @author Ghali Chraibi (262251)
 * @author Niels Poulsen (270494)
 */

public class Interval1DTest {

	@Test
	public void containsTrueOnNonTrivialInterval(){
		Interval1D i=new Interval1D(8, 12);
		assertTrue(i.contains(10));
	}

	@Test
	public void containsFalseOnNonTrivialInterval(){
		Interval1D i=new Interval1D(8, 12);
		assertTrue(!i.contains(13));
	}
	
	@Test
	public void containsWorksAtLimit(){
		Interval1D i=new Interval1D(8, 127);
		assertTrue(i.contains(127));
	}
	
	@Test
	public void sizeReturnsRightSize(){
		Interval1D i=new Interval1D(8, 17);
		assertEquals(10,i.size(),0);
	}
	
	@Test
	public void sizeOfIntWorksOnNonTrivial(){
		Interval1D i1=new Interval1D(8, 17);
		Interval1D i2=new Interval1D(12, 24);
		assertEquals(6, i1.sizeOfIntersectionWith(i2),0);
	}
	
	@Test
	public void sizeOfIntWorksOnTrivial(){
		Interval1D i1=new Interval1D(8, 17);
		Interval1D i2=new Interval1D(19, 24);
		assertEquals(0, i1.sizeOfIntersectionWith(i2),0);
	}
	
	@Test
	public void boundingUnionWorksOnTrivial(){
		Interval1D i1=new Interval1D(8, 17);
		Interval1D i2=new Interval1D(19, 24);
		Interval1D bu=new Interval1D(8, 24);
		assertEquals(bu, i1.boundingUnion(i2));
	}
	
	@Test
	public void boundingUnionWorksOnOtherTrivial(){
		Interval1D i1=new Interval1D(-8, 27);
		Interval1D i2=new Interval1D(19, 24);
		Interval1D bu=new Interval1D(-8, 27);
		assertEquals(bu, i1.boundingUnion(i2));
	}
	
	@Test 
	public void isUnionableWithWorksOnUnionableIntervals(){
		Interval1D i1=new Interval1D(-8, 27);
		Interval1D i2=new Interval1D(19, 37);
		assertTrue(i1.isUnionableWith(i2));
	}
	
	@Test 
	public void isUnionableWithWorksOnNonUnionableIntervals(){
		Interval1D i1=new Interval1D(-8, 18);
		Interval1D i2=new Interval1D(20, 37);
		assertTrue(!i1.isUnionableWith(i2));
	}
	
	@Test 
	public void isUnionableWithWorksOnLimit(){
		Interval1D i1=new Interval1D(-8, 18);
		Interval1D i2=new Interval1D(18, 37);
		assertTrue(i1.isUnionableWith(i2));
	}
	
	@Test
	public void unionWorksWhenUnionable(){
		Interval1D i1=new Interval1D(-8, 18);
		Interval1D i2=new Interval1D(18, 37);
		Interval1D u=new Interval1D(-8, 37);
		assertEquals(u, i1.union(i2));
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void unionThrowsExceptionWhenNotUnionable(){
		Interval1D i1=new Interval1D(-8, 18);
		Interval1D i2=new Interval1D(20, 37);
		i1.union(i2);
	}
	
	@Test
	public void equalsWorksWhenTrue(){
		Interval1D i1=new Interval1D(-8, 18);
		Interval1D i2=new Interval1D(-8, 18);
		assertTrue(i1.equals(i2));
	}
	
	@Test
	public void equalsWorksWhenFalse(){
		Interval1D i1=new Interval1D(-8, 18);
		Interval1D i2=new Interval1D(-8, 10);
		assertTrue(!i1.equals(i2));
	}
	
	@Test
	public void toStringWorksOnNormalArray(){
		Interval1D i1=new Interval1D(-8, 18);
		assertEquals("[-8..18]", i1.toString());
	}
}
