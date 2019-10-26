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
        for (CircuitTrace t : this.getHoleTraces())
            if (trace.equals(t)) return null;
        for (CircuitTrace t : this.getHoleTraces())
            trace.simplifyUsing(t);
        this.traces.add(trace);
        return trace;
    }

    public boolean isCarvey() {
        return carvey;
    }

    public void setCarvey(boolean carvey) {
        this.carvey = carvey;
    }
}
