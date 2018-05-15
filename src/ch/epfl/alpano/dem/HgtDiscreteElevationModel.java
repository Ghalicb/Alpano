package ch.epfl.alpano.dem;

/**
 * Represents the altitude of a certain area coming from a hgt file
 *
 * @author Ghali Chraibi (262251)
 * @author Niels Poulsen (270494)
 */

import static ch.epfl.alpano.Preconditions.checkArgument;
import static java.lang.Integer.parseInt;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ShortBuffer;
import java.nio.channels.FileChannel.MapMode;

import ch.epfl.alpano.Interval1D;
import ch.epfl.alpano.Interval2D;

public final class HgtDiscreteElevationModel implements DiscreteElevationModel {

    /**
     * The length of a hgt file
     */
    private final static int HGT_LENGTH = SAMPLES_PER_DEGREE + 1;
    
    /**
     * The size of a hgt file (the number of sample it stocks * 2, because
     * each sample is stocked in two bytes)
     */
    private final static long HGT_SIZE = 2 * (HGT_LENGTH)*(HGT_LENGTH);
    /**
     * A shortbuffer where all the (elevation) samples of the hgt are stocked
     */
    private ShortBuffer hgt;

    /** The minimum longitude covered by this Hgt */
    private final int hgtLongMin;
    /** The minimum latitude covered by this Hgt */
    private final int hgtLatMin;
    
    private final Interval2D samples_;

    /**
     * Constructs a HgtDiscreteElevationModel based on his associated file
     * 
     * @param file
     *            the hgt file which contains all the information about the dem
     * @throws IllegalArgumentException
     *             if the file does not respect the format of a hgt file
     */
    public HgtDiscreteElevationModel(File file) {
        checkArgument(checkFileNameValidity(file),
                "The file does not respect the hgt format defined");
        
        if (file.getName().charAt(0) == 'N') {
            hgtLatMin = parseInt(file.getName().substring(1, 3));
        } else {
            hgtLatMin = (-1) * parseInt(file.getName().substring(1, 3));
        } 
        
        if (file.getName().charAt(3) == 'E') {
            hgtLongMin = parseInt(file.getName().substring(4, 7));
        } else {
            hgtLongMin = (-1) * parseInt(file.getName().substring(4, 7));
        }

        try (FileInputStream hgtFIS = new FileInputStream(file)) {
            hgt = hgtFIS.getChannel().map(MapMode.READ_ONLY, 0, HGT_SIZE)
                    .asShortBuffer();
        } catch (IOException iof) {
            throw new IllegalArgumentException();
        }
        
        Interval1D longitude = new Interval1D(SAMPLES_PER_DEGREE * hgtLongMin,
                SAMPLES_PER_DEGREE * (hgtLongMin + 1));
        Interval1D latitude = new Interval1D(SAMPLES_PER_DEGREE * hgtLatMin,
                SAMPLES_PER_DEGREE * (hgtLatMin + 1));
        samples_ = new Interval2D(longitude, latitude);
    }

    /**
     * Checks if a file is in the hgt format
     * 
     * @param file
     *            the hgt file of whom we need to check the format
     * @return true when the file has the correct format for a hgt file, false
     *         otherwise
     */
    private static boolean checkFileNameValidity(File file) {
        String fileName = file.getName();

        if (fileName.length() != 11) {
            return false;
        }

        boolean condition1 = fileName.charAt(0) == 'N'
                || fileName.charAt(0) == 'S';
        boolean condition2 = parseInt(fileName.substring(1, 3)) < 90;
        boolean condition3 = fileName.charAt(3) == 'E'
                || fileName.charAt(3) == 'W';
        boolean condition4 = parseInt(fileName.substring(4, 7)) < 180;
        boolean condition5 = fileName.substring(7, 11).equals(".hgt");
        boolean condition6 = file.length() == HGT_SIZE;

        return (condition1 && condition2 && condition3 && condition4
                && condition5 && condition6);
    }

    @Override
    public void close() throws Exception {
        hgt = null;
    }

    @Override
    public Interval2D extent() {
        return samples_;
    }

    @Override
    public double elevationSample(int x, int y) {
        checkArgument(extent().contains(x, y));
        int indexLongLocal = x - hgtLongMin * SAMPLES_PER_DEGREE;
        int indexLatLocal = SAMPLES_PER_DEGREE - (y - hgtLatMin * SAMPLES_PER_DEGREE);

        int indexInHgt = (HGT_LENGTH) * indexLatLocal + indexLongLocal;

        return hgt.get(indexInHgt);
    }

}
