package ch.epfl.alpano;

/**
 * Contains methods which can be used to check that a precondition is satisfied
 *
 * @author Ghali Chraibi (262251)
 * @author Niels Poulsen (270494)
 */

public interface Preconditions {

    /**
     * Checks that an argument is satisfied, and if it isn't throws an
     * IllegalArgumentException
     * 
     * @param b
     *            the argument that must be checked
     * @throws IllegalArgumentException
     *             if b is not satisfied
     */
    static void checkArgument(boolean b) {
        if (!b) {
            throw new IllegalArgumentException();
        }
    }

    /**
     * Checks that an argument is satisfied, and if it isn't throws an
     * IllegalArgumentException with a message attached
     * 
     * @param b
     *            the argument to be checked
     * @param message
     *            the message to be attached
     * @throws IllegalArgumentException
     *             if b is not satisfied
     */
    static void checkArgument(boolean b, String message) {
        if (!b) {
            throw new IllegalArgumentException(message);
        }
    }

}
