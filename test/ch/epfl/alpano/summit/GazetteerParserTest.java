package ch.epfl.alpano.summit;

/**
 *
 * @author Ghali Chraibi (262251)
 * @author Niels Poulsen (270494)
 */

import java.io.File;
import java.io.IOException;
import java.util.List;


public class GazetteerParserTest {

    public static void main(String[] args) {
        try {
            File file = new File("alps.txt");
            List<Summit> summits = GazetteerParser.readSummitsFrom(file);
            for (Summit summit : summits) {
                System.out.println(summit);
            }
        } 
        catch(IOException io) {
            System.out.println("Aïe");
        } catch (NumberFormatException nfe) {
            System.out.println("Ouille");
        } catch (IndexOutOfBoundsException oobe) {
            System.out.println("Prout");
        }
    }

}
