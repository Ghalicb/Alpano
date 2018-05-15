package ch.epfl.alpano.gui;

/**
 * Represents an element that converts Strings representing numbers with a certain number of 
 * decimals into Integers and vice-versa
 *
 * @author Ghali Chraibi (262251)
 * @author Niels Poulsen (270494)
 */

import static ch.epfl.alpano.Preconditions.checkArgument;

import java.math.BigDecimal;
import java.math.RoundingMode;

import javafx.util.StringConverter;

public final class FixedPointStringConverter extends StringConverter<Integer> {

	/** The number of decimals of the numbers that are manipulated */
	private final int numberOfDecimals_;

	/**
	 * Creates a new element that converts Strings representing decimal numbers
	 * to Integers, and vice-versa
	 * 
	 * @param numberOfDecimals
	 *            the number of decimals in the numbers manipulated
	 */
	public FixedPointStringConverter(int numberOfDecimals) {
		checkArgument(numberOfDecimals > 0);
		numberOfDecimals_ = numberOfDecimals;
	}

	@Override
	public String toString(Integer integer) {
	    if (integer != null) {
	        return new BigDecimal(integer).movePointLeft(numberOfDecimals_).stripTrailingZeros().toPlainString();
	    } else {
	        return "";
	    }     
	}

	@Override
	public Integer fromString(String string) {
		return new BigDecimal(string).movePointRight(numberOfDecimals_).setScale(0, RoundingMode.HALF_UP)
				.intValueExact();
	}

}
