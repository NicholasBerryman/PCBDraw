/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pcbdraw.CNC.representations;

import java.io.IOException;
import pcbdraw.circuit.Coordinate;
import pcbdraw.data.GCodeFile;

/**
 *
 * @author Nick Berryman
 */
public abstract class CNCRepr {
    private final GCodeFile gcodeFile;
    private StringBuilder gcode = new StringBuilder();
    public CNCRepr(GCodeFile gcodeFile){
        this.gcodeFile = gcodeFile;
    }
    
    public abstract void initMachine();
    public abstract void move(Coordinate c);
    public abstract void startCut();
    public abstract void endCut();
    public abstract void cutHole(Coordinate c);
    public abstract void finish();

    public StringBuilder getGcode() {
        return gcode;
    }
    
    public void save() throws IOException{
        this.gcodeFile.save(gcode.toString());
    }
    
    public void reset(){
        gcode = new StringBuilder();
    }
}
