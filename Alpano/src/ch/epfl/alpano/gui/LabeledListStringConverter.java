package ch.epfl.alpano.gui;

/**
 * Represents an entity that converts Strings to Integers, and vice-versa
 *
 * @author Ghali Chraibi (262251)
 * @author Niels Poulsen (270494)
 */

import static ch.epfl.alpano.Preconditions.checkArgument;

import java.util.ArrayList;
import java.util.List;

import javafx.util.StringConverter;

public final class LabeledListStringConverter extends StringConverter<Integer> {

	/** The list of Strings corresponding to the integer that is their index */
	private final List<String> strings_;

	/**
	 * Creates a new LabeledListStringConverter, that can convert Strings to
	 * Integers and vice-versa
	 * 
	 * @param strings
	 *            the strings representing the integer corresponding to their
	 *            position (the first string corresponds to the integer 0, the
	 *            n-th to the integer n)
	 */
	public LabeledListStringConverter(String... strings) {
		strings_ = new ArrayList<>();
		for (String s : strings)
			strings_.add(s);
	}

	@Override
	public String toString(Integer integer) {
		checkArgument(integer < strings_.size(), "Integer not in LabeledListStringConverter");
		return strings_.get(integer);
	}

	@Override
	public Integer fromString(String string) {
		checkArgument(strings_.contains(string), "String not in LabeledListStringConverter");
		return strings_.indexOf(string);
	}

}
