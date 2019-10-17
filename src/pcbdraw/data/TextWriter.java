/**
 * Name: Nick Berryman
 * Date: 23/5/19
 * FileName: TreeWrite.java
 * Purpose: Family Tree file-writer class
 */
package pcbdraw.data;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Family Tree file-writer class
 * @author Nick Berryman
 */
public class TextWriter {
    private final String fileName;
    private PrintWriter out;

    /**
     * Create a new Family Tree file-writer
     * @param fileName 
     */
    public TextWriter(String fileName) {
        this.fileName = fileName;
    }
    
    /**
     * Open the File writer
     * @throws FileNotFoundException When the file isn't found
     * @throws IOException When there is an error opening the file
     */
    private void open() throws FileNotFoundException, IOException{
        out = new PrintWriter(new FileWriter(fileName));
    }
    
    /**
     * Close the file writer
     * @throws IOException When there is an error closing the file
     */
    private void close() throws IOException{
        out.close();
    }
    
    /**
     * Create a new family tree file
     * @throws IOException When there is an error creating the file
     */
    public void create() throws IOException{
        if (!new File(fileName).exists()) {
            this.open();
            this.close();
        }
    }
    
    /**
     * Save the specified Family Tree to the file
     * @param toWrite Family Tree to save
     * @throws IOException When there is an error writing to file
     */
    public void save(String toWrite) throws IOException{
        this.open();
        out.print(toWrite);
        this.close();
    }
}
