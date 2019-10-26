/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pcbdraw.data;

import java.io.File;
import java.io.IOException;
import javafx.stage.FileChooser;
import pcbdraw.circuit.Coordinate;
import pcbdraw.circuit.traces.HoleTrace;
import pcbdraw.circuit.MilliGrid;
import pcbdraw.circuit.PCB;
import pcbdraw.circuit.traces.PathTrace;

/**
 *
 * @author Nick Berryman
 */
public class GCBFile {
    private TextFile file;
    private String fileName;

    public static GCBFile askUserToOpen() throws IOException{
        FileChooser gcbChoose = new FileChooser();
        FileChooser.ExtensionFilter gcbFilter = new FileChooser.ExtensionFilter("PCB files (*.gcb)", "*.gcb");
        gcbChoose.getExtensionFilters().add(gcbFilter);
        
        File toRead = gcbChoose.showOpenDialog(null);
        if (toRead == null) return null;
        return new GCBFile(toRead.getPath());
    }
    
    public static GCBFile askUserToSaveAs() throws IOException{
        FileChooser gcbChoose = new FileChooser();
        FileChooser.ExtensionFilter gcbFilter = new FileChooser.ExtensionFilter("PCB files (*.gcb)", "*.gcb");
        gcbChoose.getExtensionFilters().add(gcbFilter);
        
        File toSave = gcbChoose.showSaveDialog(null);
        if (toSave == null) return null;
        return new GCBFile(toSave.getPath());
    }
    
    public GCBFile(String fileName) throws IOException {
        this.fileName = fileName;
        file = new TextFile(fileName);
    }
    
    public String getName(){
        return new File(fileName).getName();
    }
    
    public void save(MilliGrid pcb) throws IOException{
        StringBuilder str = new StringBuilder();
        str.append(pcb.getPCB().getSize().x).append('\n');
        str.append(pcb.getPCB().getSize().y).append('\n');
        str.append(pcb.getZoom()).append('\n');
        str.append(pcb.getSquareSizeMM()).append('\n');
        str.append(pcb.getPCB().isCarvey()).append('\n');
        str.append("Paths:").append('\n');
        for (PathTrace p : pcb.getPCB().getPathTraces()){
            System.out.println(p.getStartPoint().x);
            str.append(p.getStartPoint().x);
            str.append(",");
            str.append(p.getStartPoint().y);
            str.append(",");
            str.append(p.getEndPoint().x);
            str.append(",");
            str.append(p.getEndPoint().y).append('\n');
        }
        str.append("Holes:").append('\n');
        for (HoleTrace h : pcb.getPCB().getHoleTraces()){
            str.append(h.getMajorCoord().x);
            str.append(",");
            str.append(h.getMajorCoord().y).append('\n');
        }
        file.save(str.toString());
    }
    
    public MilliGrid read() throws IOException{
        file.openToRead();
        boolean doingHoles = false;
        Coordinate size = new Coordinate(Double.parseDouble(file.read()), Double.parseDouble(file.read()));
        double zoom = Double.parseDouble(file.read());
        double sqSizeMM = Double.parseDouble(file.read());
        boolean carvey = Boolean.parseBoolean(file.read());
        PCB pcb = new PCB(size, carvey);
        MilliGrid gcb = new MilliGrid(zoom, sqSizeMM, pcb);
        file.read();
        
        String line;
        while ((line = file.read()) != null){
            line = line.trim();
            if (line.equals("Holes:")) doingHoles = true;
            else{
                if (doingHoles){
                    pcb.addTrace(new HoleTrace(new Coordinate(Double.parseDouble(line.split(",")[0]), Double.parseDouble(line.split(",")[1]))));
                }
                else{
                    pcb.addTrace(new PathTrace(
                        new Coordinate(Double.parseDouble(line.split(",")[0]), Double.parseDouble(line.split(",")[1])), 
                        new Coordinate(Double.parseDouble(line.split(",")[2]), Double.parseDouble(line.split(",")[3]))
                    ));
                }
            }
        }
        file.closeToRead();
        return gcb;
    }
}
