/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pcbdraw.circuit;

import java.util.ArrayList;

/**
 *
 * @author Nick Berryman
 */
public class MilliGrid {
    private Coordinate size;
    private final ArrayList<CircuitTrace> traces = new ArrayList<>();
    
    public MilliGrid(Coordinate size){
        this.size = size;
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
    
    public void addTrace(CircuitTrace trace){
        for (CircuitTrace t : this.getHoleTraces())
            if (trace.equals(t)) return;
        for (CircuitTrace t : this.getHoleTraces())
            trace.simplifyUsing(t);
        this.traces.add(trace);
    }
}
