/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pcbdraw.data;

import java.io.IOException;

/**
 *
 * @author Nick Berryman
 */
public class GCodeFile {
    private TextFile file;
    
    public GCodeFile(String fileName) throws IOException {
        file = new TextFile(fileName);
    }
    
    public void save(String gcode) throws IOException{
        file.save(gcode);
    }
   
}
