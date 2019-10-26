/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pcbdraw.data;

import java.io.File;
import java.io.IOException;
import javafx.stage.FileChooser;

/**
 *
 * @author Nick Berryman
 */
public class GCodeFile {
    private TextFile file;
    public static GCodeFile askUserToSaveAs() throws IOException{
        FileChooser gcbChoose = new FileChooser();
        FileChooser.ExtensionFilter gcbFilter = new FileChooser.ExtensionFilter("GCode files (*.gcode)", "*.gcode");
        gcbChoose.getExtensionFilters().add(gcbFilter);
        
        File toSave = gcbChoose.showSaveDialog(null);
        if (toSave == null) return null;
        return new GCodeFile(toSave.getPath());
    }
    
    public GCodeFile(String fileName) throws IOException {
        file = new TextFile(fileName);
    }
    
    public void save(String gcode) throws IOException{
        file.save(gcode);
    }
}

