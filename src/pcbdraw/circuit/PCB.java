/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pcbdraw.circuit;

import pcbdraw.circuit.traces.CircuitTrace;
import pcbdraw.circuit.traces.HoleTrace;
import pcbdraw.circuit.traces.PathTrace;
import java.util.ArrayList;

/**
 *
 * @author Nick Berryman
 */
public class PCB {
    private Coordinate size;
    private final ArrayList<CircuitTrace> traces = new ArrayList<>();
    private boolean carvey;
    
    public PCB(Coordinate size, boolean carvey){
        this.size = size;
        this.carvey = carvey;
    }
    
    public void resize(Coordinate newSize){
        this.size = newSize;
    }
    
    public Coordinate getSize(){
        return this.size;
    }
    
    public ArrayList<CircuitTrace> getTraces(){
        return this.traces;
    }
    
    public ArrayList<PathTrace> getPathTraces(){
        ArrayList<PathTrace> toReturn = new ArrayList<>();
        for (CircuitTrace t : traces){
            if (t instanceof PathTrace) toReturn.add((PathTrace)t);
        }
        return toReturn;
    }
    
    public ArrayList<HoleTrace> getHoleTraces(){
        ArrayList<HoleTrace> toReturn = new ArrayList<>();
        for (CircuitTrace t : traces){
            if (t instanceof HoleTrace) toReturn.add((HoleTrace)t);
        }
        return toReturn;
    }
    
    public CircuitTrace addTrace(CircuitTrace trace){
        this.traces.add(trace);
        return trace;
    }
    
    public boolean isCarvey() {
        return carvey;
    }

    public void setCarvey(boolean carvey) {
        this.carvey = carvey;
    }
    
    public boolean verifyCarveyble(){
        for (CircuitTrace t : this.getTraces()){
            if (!verifyCarveyble(t)) return false;
        }
        return true;
    }
    
    public boolean verifyLocations(){
        for (CircuitTrace t : this.getTraces()){
            if (!verifyLocation(t)) return false;
        }
        return true;
    }
    
    private boolean verifyCarveyble(CircuitTrace t){
        if (!carvey) return true;
        boolean notOk = t.inArea(new Coordinate(0,0), new Coordinate(0.75*25.4, 3.25*25.4));
        notOk        |= t.inArea(new Coordinate(0,0), new Coordinate(3.25*25.4, 0.75*25.4));
        return !notOk;
    }
    
    private boolean verifyLocation(CircuitTrace t){
        if (t instanceof PathTrace) return ((PathTrace) t).fullyInArea(new Coordinate(0,0), this.size);
        return t.inArea(new Coordinate(0,0), this.size);
    }
}
