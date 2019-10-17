/**
 * Name: Nick Berryman
 * Date: 23/5/19
 * FileName: TreeFile.java
 * Purpose: Read/Write Family Tree File class
 */
package pcbdraw.data;

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Read/Write Family Tree File class
 * @author Nick Berryman
 */
public class TextFile {
    private final TextReader in;
    private final TextWriter out;

    /**
     * Creates a new read/write family tree file
     * @param fileName Name of file
     * @throws IOException When there is an error opening to file
     */
    public TextFile(String fileName) throws IOException {
        this.in = new TextReader(fileName);
        this.out = new TextWriter(fileName);
        
        this.out.create();
    }
    
    public void openToRead() throws IOException, FileNotFoundException{
        in.open();
    }
    
    public void closeToRead() throws IOException{
        in.close();
    }
    
    /**
     * Read a family tree from the file
     * @return The family tree encoded within the file
     * @throws IOException When there is an error reading the file
     * @throws ClassNotFoundException When there is an error with the class
     */
    public String read() throws IOException{
        return in.read();
    }
    
    /**
     * Saves a family tree to the file, overwriting any others
     * @throws IOException When there is an error writing to the file
     * @throws ClassNotFoundException When there is an error with the class
     */
    public void save(String toWrite) throws IOException{
        out.save(toWrite);
    }    
}
