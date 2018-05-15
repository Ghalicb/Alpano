package ch.epfl.alpano;

/**
 * Represents a two-dimensional interval of integers
 *
 * @author Ghali Chraibi (262251)
 * @author Niels Poulsen (270494)
 */

import static ch.epfl.alpano.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;

import java.util.Objects;

public final class Interval2D {

    /** The cartesian products first interval */
    private final Interval1D iX_;

    /** The cartesian products second interval */
    private final Interval1D iY_;

    /**
     * Creates the cartesian product of two intervals
     * 
     * @param iX
     *            the first interval in the cartesian product
     * @param iY
     *            the second interval in the cartesian product
     * @throws NullPointerException
     *             if one of the intervals is empty
     */
    public Interval2D(Interval1D iX, Interval1D iY) {
        iX_ = requireNonNull(iX, "First interval empty");
        iY_ = requireNonNull(iY, "Second interval empty");
    }

    /**
     * Gives the cartesian products first interval
     * 
     * @return the cartesian products first interval
     */
    public Interval1D iX() {
        return iX_;
    }

    /**
     * Gives the cartesian products second interval
     * 
     * @return the cartesian products second interval
     */
    public Interval1D iY() {
        return iY_;
    }

    /**
     * Checks if an element (x,y) is contained in the two-dimensional interval
     * 
     * @param x
     *            the element that has to be contained in the intervals first
     *            dimension
     * @param y
     *            the element that has to be contained in the intervals second
     *            dimension
     * @return true if (x,y) is contained in the two-dimensional interval, false
     *         if it isn't
     */
    public boolean contains(int x, int y) {
        return (iX_.contains(x) && iY_.contains(y));
    }

    /**
     * Gives the number of elements in the two-dimensional interval
     * 
     * @return the number of elements in the two-dimensional interval
     */
    public int size() {
        return (iX_.size()) * (iY_.size());
    }

    /**
     * Gives the number of elements in the intersection of two two-dimensional
     * intervals (this and that)
     * 
     * @param that
     *            the two-dimensional interval of which we want the number of
     *            elements intersecting with this
     * @return the number of elements both in this and in that
     */
    public int sizeOfIntersectionWith(Interval2D that) {
        return iX_.sizeOfIntersectionWith(that.iX_)
                * iY_.sizeOfIntersectionWith(that.iY_);
    }

    /**
     * Gives the bounding union of two two-dimensional intervals (a unique
     * two-dimensional interval that covers both intervals)
     * 
     * @param that
     *            another Interval2D
     * @return the bounding union of this and that
     */
    public Interval2D boundingUnion(Interval2D that) {
        return new Interval2D(iX_.boundingUnion(that.iX()),
                iY_.boundingUnion(that.iY()));
    }

    /**
     * Tests if two two-dimensional intervals are "unionable" (the union of the
     * two intervals form a unique interval)
     * 
     * @param that
     *            another Interval2D
     * @return true when the bounding union of this and that is equal to the
     *         real union and false otherwise
     */
    public boolean isUnionableWith(Interval2D that) {
        return (this.size() + that.size()
                - this.sizeOfIntersectionWith(that) == this.boundingUnion(that)
                        .size());
    }

    /**
     * Gives the union between two two-dimensional intervals
     * 
     * @param that
     *            another Interval2D
     * @return the union (a unique two-dimensional interval) between this and
     *         that
     * @throws IllegalArgumentException
     *             if this and that are not unionable
     */
    public Interval2D union(Interval2D that) {
        checkArgument(this.isUnionableWith(that),
                "The two-dimensional intervals are not unionable");
        return this.boundingUnion(that);
    }

    @Override
    public boolean equals(Object thatO) {
        if (thatO == null) {
            return false;
        } else {
            if (thatO.getClass() != getClass()) {
                return false;
            } else {
                Interval2D i = (Interval2D) thatO;
                return (iX_.equals(i.iX_)) && (iY_.equals(i.iY_));
            }
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(iX_, iY_);
    }

    @Override
    public String toString() {
        return (iX_.toString() + "x" + iY_.toString());
    }

}
