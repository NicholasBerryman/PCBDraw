/**
 * Name: Nick Berryman
 * Date: 23/5/19
 * FileName: TreeReader.java
 * Purpose: Family Tree File Reader class
 */
package pcbdraw.data;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * Family Tree File Reader class
 * @author Nick Berryman
 */
public class TextReader {
    private final String fileName;
    private BufferedReader in;

    /**
     * Creates a Family Tree file-reader
     * @param fileName 
     */
    public TextReader(String fileName) {
        this.fileName = fileName;
    }
    
    /**
     * Open a new Family Tree file reader
     * @throws FileNotFoundException When the file is not found
     * @throws IOException When there is an error opening the file
     */
    public void open() throws FileNotFoundException, IOException{
        in = new BufferedReader(new FileReader(fileName));
    }
    
    /**
     * Closes the file reader
     * @throws IOException When there is an error closing the file
     */
    public void close() throws IOException{
        in.close();
    }
    
    /**
     * Reads a family tree from the file
     * @return The family tree encoded within the file
     * @throws IOException When there is an error reading the file
     */
    public String read() throws IOException{
        String str = in.readLine();
        
        return str;
    }
}
