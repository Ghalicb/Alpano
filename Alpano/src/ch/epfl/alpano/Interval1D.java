package ch.epfl.alpano;

/**
 * Represents a unidimensional interval of integers
 *
 * @author Ghali Chraibi (262251)
 * @author Niels Poulsen (270494)
 */
import static ch.epfl.alpano.Preconditions.checkArgument;
import static java.lang.Math.max;
import static java.lang.Math.min;

import java.util.Locale;
import java.util.Objects;

public final class Interval1D {

    /** The lower bound of the interval */
    private final int includedFrom_;

    /** The upper bound of the interval */
    private final int includedTo_;

    /**
     * Forms an interval characterized by its lower and upper bound
     * 
     * @param includedFrom
     *            the lower bound
     * @param includedTo
     *            the upper bound
     * @throws IllegalArgumentException
     *             if the lower bound given is bigger than the upper one
     */
    public Interval1D(int includedFrom, int includedTo) {
        checkArgument(includedFrom <= includedTo,
                "Lower bound bigger than upper bound !");

        includedFrom_ = includedFrom;
        includedTo_ = includedTo;
    }

    /**
     * Gives the lower bound of this
     * 
     * @return the lower bound of this
     */
    public int includedFrom() {
        return includedFrom_;
    }

    /**
     * Gives the upper bound of this
     * 
     * @return the upper bound of this
     */
    public int includedTo() {
        return includedTo_;
    }

    /**
     * Checks if an integer is contained in this
     * 
     * @param v
     *            an integer
     * @return true when v belongs to this and false otherwise
     */
    public boolean contains(int v) {
        return ((v >= includedFrom_) && (v <= includedTo_));
    }

    /**
     * Gives the cardinality of this (the number of element it contains)
     * 
     * @return the cardinality of this
     */
    public int size() {
        return includedTo_ - includedFrom_ + 1;
    }

    /**
     * Finds the size of the intersection between two intervals
     * 
     * @param that
     *            another Interval1D
     * @return the size of the intersection between this and that
     */
    public int sizeOfIntersectionWith(Interval1D that) {
        int globalMin = max(this.includedFrom_, that.includedFrom_);
        int globalMax = min(this.includedTo_, that.includedTo_);

        if (globalMax < globalMin) {
            return 0;
        } else {
            return globalMax - globalMin + 1;
        }
    }

    /**
     * Gives the bounding union of two intervals (a unique interval that cover
     * both intervals)
     * 
     * @param that
     *            another Interval1D
     * @return the bounding union of this and that
     */
    public Interval1D boundingUnion(Interval1D that) {
        return new Interval1D(min(this.includedFrom_, that.includedFrom_),
                max(this.includedTo_, that.includedTo_));
    }

    /**
     * Tests if two intervals are "unionable" (the union of the two intervals
     * form a unique interval)
     * 
     * @param that
     *            another Interval1D
     * @return true when the bounding union of this and that is equal to the
     *         real union and false otherwise
     */
    public boolean isUnionableWith(Interval1D that) {
        return (this.size() + that.size()
                - this.sizeOfIntersectionWith(that) == this.boundingUnion(that)
                        .size());
    }

    /**
     * Gives the union between two intervals
     * 
     * @param that
     *            another Interval1D
     * @return the union (a unique interval) between this and that
     * @throws IllegalArgumentException
     *             if this and that are not unionable
     */
    public Interval1D union(Interval1D that) {
        checkArgument(isUnionableWith(that),
                "The two intervals are not unionable");
        return boundingUnion(that);
    }

    @Override
    public boolean equals(Object thatO) {
        if (thatO == null) {
            return false;
        } else {
            if (thatO.getClass() != getClass()) {
                return false;
            } else {
                Interval1D i = (Interval1D) thatO;
                return ((includedFrom_ == i.includedFrom_)
                        && (includedTo_ == i.includedTo_));
            }
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(includedFrom(), includedTo());
    }

    @Override
    public String toString() {
        Locale l = null;
        String s = String.format(l, "[%d..%d]", includedFrom_, includedTo_);
        return s;
    }

}
