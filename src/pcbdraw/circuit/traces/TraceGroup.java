/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pcbdraw.circuit.traces;

import java.util.ArrayList;
import pcbdraw.circuit.Coordinate;
import pcbdraw.circuit.PCB;

/**
 *
 * @author Nick Berryman
 */
public class TraceGroup {
    private ArrayList<CircuitTrace> traces;
    private PCB pcb;
    
    public TraceGroup(ArrayList<CircuitTrace> traces, PCB pcb){
        this.traces = traces;
        this.pcb = pcb;
    }
    
    //Given in mm
    public void moveTo(Coordinate to){
        Coordinate topLeft = this.getAnchor();
        for (CircuitTrace t : traces){
            Coordinate dist = topLeft.subtract(t.getMajorCoord());
            t.moveTo(to.subtract(dist));
        }
    }
    
    public Coordinate getAnchor(){
        Coordinate topLeft = new Coordinate(Double.MAX_VALUE, Double.MIN_VALUE);
        for (CircuitTrace t : traces){
            if (t.getMajorCoord().x < topLeft.x) topLeft = new Coordinate(t.getMajorCoord().x, topLeft.y);
            if (t.getMajorCoord().y > topLeft.y) topLeft = new Coordinate(topLeft.x, t.getMajorCoord().y);
        }
        return topLeft;
    }
    
    public void verify(){
        ArrayList<CircuitTrace> deads = new ArrayList<>();
        for (CircuitTrace t : traces){
            if (!pcb.getTraces().contains(t)) deads.add(t);
        }
        this.traces.removeAll(deads);
    }
    
    public boolean contains(CircuitTrace toCheck){
        return this.traces.contains(toCheck);
    }
    
    public ArrayList<CircuitTrace> getTraces(){
        return new ArrayList<>(traces);
    }
    
    public TraceGroup copy(){
        return new TraceGroup(traces, pcb);
    }
    
    public TraceGroup duplicate(){
        ArrayList<CircuitTrace> copyList = new ArrayList<>();
        for (CircuitTrace t : traces){
            copyList.add(t.copy());
        }
        return new TraceGroup(copyList, this.pcb);
    }
}
