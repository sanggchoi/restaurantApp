package RestaurantBackend;

import java.io.*;

/**
 * Interface for creating files
 */
public interface FileCreator {

    /**
     * Create a new file in the working directory
     * @param fileName: the name of the file to be created
     */
    default void createNewFile(String fileName){
        try {
            File f = new File(fileName);
            f.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
