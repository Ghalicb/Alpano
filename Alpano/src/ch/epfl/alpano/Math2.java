package ch.epfl.alpano;

/**
 * Regroups a collection of mathematical methods
 *
 * @author Ghali Chraibi (262251)
 * @author Niels Poulsen (270494)
 */

import static java.lang.Math.PI;
import static java.lang.Math.abs;
import static java.lang.Math.floor;
import static java.lang.Math.sin;
import static ch.epfl.alpano.Preconditions.checkArgument;

import java.util.function.DoubleUnaryOperator;

public interface Math2 {

    /** Represents the value of 2*Pi */
    static double PI2 = 2 * PI;

    /**
     * Squares a number
     * 
     * @param x
     *            the number to be squared
     * @return the number squared
     */
    static double sq(double x) {
        return x * x;
    }

    /**
     * Calculates the remainder from the integer division of two numbers
     * 
     * @param x
     *            the dividend of the integer division
     * @param y
     *            the divisor of the integer division
     * @return the remainder from the integer division of x by y
     */
    static double floorMod(double x, double y) {
        return x - y * (floor(x / y));
    }

    /**
     * Calculates the haversin of an angle
     * 
     * @param x
     *            the angle
     * @return the haversin of x
     */
    static double haversin(double x) {
        return sq(sin(x / 2.0));
    }

    /**
     * Calculates the angular distance between two angles
     * 
     * @param a1
     *            the first angle
     * @param a2
     *            the second angle
     * @return the angular distance between a1 and a2
     */
    static double angularDistance(double a1, double a2) {
        return floorMod((a2 - a1 + PI), PI2) - PI;
    }

    /**
     * Calculates the value of f(x), through the linear interpolation of f(0)
     * and f(1)
     * 
     * @param y0
     *            represents f(0)
     * @param y1
     *            represents f(1)
     * @param x
     *            the x-axis value of which we want the y-axis value
     * @return f(x)
     */
    static double lerp(double y0, double y1, double x) {
        return (y1 - y0) * x + y0;
    }

    /**
     * Calculates f(x,y) through the bilinear interpolation of f(0,0), f(0,1),
     * f(1,0), f(1,1)
     * 
     * @param z00
     *            represents f(0,0)
     * @param z10
     *            represents f(1,0)
     * @param z01
     *            represents f(0,1)
     * @param z11
     *            represents f(1,1)
     * @param x
     *            the position on the x-axis of the point
     * @param y
     *            the position on the y-axis of the point
     * @return f(x,y)
     */
    static double bilerp(double z00, double z10, double z01, double z11,
            double x, double y) {
        double z1 = lerp(z00, z01, y);
        double z2 = lerp(z10, z11, y);
        return lerp(z1, z2, x);
    }

    /**
     * Searches for the first interval of size dX in between two values,
     * containing a root of a function, if such root exists
     * 
     * @param f
     *            the function whose root has to be found
     * @param minX
     *            the smallest value that can be in the interval
     * @param maxX
     *            the biggest value that can be in the interval
     * @param dX
     *            the size of the interval
     * @return the lower bound of the first interval containing a root of f, and
     *         Double.POSITIVE_INFINITY if no such interval exists
     * @throws IllegalArgumentException
     *             if the smallest possible value is bigger than the biggest
     *             possible value
     */
    static double firstIntervalContainingRoot(DoubleUnaryOperator f,
            double minX, double maxX, double dX) {
        checkArgument(minX < maxX, "Minimum bigger than maximum!");
        // x1: The value of the lower bound of the interval
        double x1 = minX - dX;
        // x2: The value of the upper bound of the interval
        double x2 = Math.min(minX, maxX - dX);
        // findZero: Used to check if a zero is in between x1 and x2
        double findZero;
        do {
            x1 += dX;
            x2 += dX;
            findZero = f.applyAsDouble(x1) * f.applyAsDouble(x2);
        } while ((findZero >= 0) && (x2 <= maxX - dX));
        if (findZero > 0) {
            return Double.POSITIVE_INFINITY;
        } else {
            return x1;
        }
    }

    /**
     * Uses the dichotomy method to find an interval comprised between two
     * bounds and smaller or equal to epsilon that contains a root of a function
     * 
     * @param f
     *            the function whose root has to be found
     * @param x1
     *            the first bound in which the interval has to be
     * @param x2
     *            the second bound in which the interval has to be
     * @param epsilon
     *            the maximum size of the interval
     * @return the smallest bound of the interval found
     * @throws IllegalArgumentException
     *             if no root is contained in between the two bounds
     */
    static double improveRoot(DoubleUnaryOperator f, double x1, double x2,
            double epsilon) {
        checkArgument(f.applyAsDouble(x1) * f.applyAsDouble(x2) <= 0,
                "This interval contains no root on the given function");
        // The intervals lower bound
        double small;
        // The intervals upper bound
        double big;
        // The point in the middle of the interval
        double mid;
        if (x1 < x2) {
            small = x1;
            big = x2;
        } else {
            small = x2;
            big = x1;
        }
        while (abs(big - small) > epsilon) {
            mid = 0.5 * (small + big);
            if (f.applyAsDouble(mid) * f.applyAsDouble(big) <= 0) {
                small = mid;
            } else {
                big = mid;
            }
        }
        return small;
    }

}
