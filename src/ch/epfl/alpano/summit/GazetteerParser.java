package ch.epfl.alpano.summit;

/**
 * Reads a specific kind of file to extract informations about summits 
 *
 * @author Ghali Chraibi (262251)
 * @author Niels Poulsen (270494)
 */
import static java.nio.charset.StandardCharsets.US_ASCII;
import static java.util.Collections.unmodifiableList;
import static java.lang.Math.abs;
import static java.lang.Math.toRadians;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import ch.epfl.alpano.GeoPoint;

public final class GazetteerParser {

    private static final int FIRST_CHAR_FOR_LONGITUDE = 0;
    private static final int LAST_CHAR_FOR_LONGITUDE = 8;
    private static final int FIRST_CHAR_FOR_LATITUDE = 9;
    private static final int LAST_CHAR_FOR_LATITUDE = 17;
    private static final int FIRST_CHAR_FOR_ALTITUDE = 19;
    private static final int LAST_CHAR_FOR_ALTITUDE = 23;
    private static final int FIRST_CHAR_FOR_NAME = 36;

    private static final int MINUTES_PER_DEGREE = 60;
    private static final int SECONDS_PER_DEGREE = 3600;

    private GazetteerParser() {
    }

    /**
     * Gives a list of summits corresponding to their descriptions found in a
     * specifically formatted file
     * 
     * @param file
     *            the file containing all the information about summits
     * @return a list of summits
     * @throws IOException
     *             if there is a I/O problem while reading the file or if the
     *             file does not respect the right format
     */
    public static List<Summit> readSummitsFrom(File file) throws IOException {

        try (BufferedReader summitBR = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), US_ASCII))) {
            List<Summit> summits = new ArrayList<Summit>();
            String s;
            while ((s = summitBR.readLine()) != null) {
                summits.add(readSummitInformation(s));
            }
            summits = unmodifiableList(summits);
            return summits;
        } catch (NumberFormatException nfe) {
            throw new IOException(nfe);
        } catch (IndexOutOfBoundsException oobe) {
            throw new IOException(oobe);
        }
    }

    /**
     * Gives the summit corresponding to a line of the file of summits
     * 
     * @param summitInfo
     *            a line containing all the information about a summit
     * @return the summit described by summitInfo
     */
    private static Summit readSummitInformation(String summitInfo) {
        double longitude = dmsToRadian(summitInfo.substring(
                FIRST_CHAR_FOR_LONGITUDE, LAST_CHAR_FOR_LONGITUDE + 1)
                .trim());
        double latitude = dmsToRadian(summitInfo
                .substring(FIRST_CHAR_FOR_LATITUDE, LAST_CHAR_FOR_LATITUDE + 1)
                .trim());
        GeoPoint position = new GeoPoint(longitude, latitude);

        int altitude = Integer.parseInt(summitInfo
                .substring(FIRST_CHAR_FOR_ALTITUDE, LAST_CHAR_FOR_ALTITUDE + 1)
                .trim());

        String name = summitInfo.substring(FIRST_CHAR_FOR_NAME);

        return new Summit(name, position, altitude);
    }

    /**
     * Convert an angle from d:m:s to radians
     * 
     * @param dms
     *            an angle in the format degrees:minutes:secondes
     * @return the angle in radians
     */
    private static double dmsToRadian(String dms) {
        String[] dmsSplitted = dms.split(":");
        double absoluteRadian = abs(Double.parseDouble(dmsSplitted[0]))
                + Double.parseDouble(dmsSplitted[1]) / MINUTES_PER_DEGREE
                + Double.parseDouble(dmsSplitted[2]) / SECONDS_PER_DEGREE;
        if (Double.parseDouble(dmsSplitted[0]) >= 0) {
            return toRadians(absoluteRadian);
        } else {
            return toRadians((-1) * absoluteRadian);
        }
    }

}
