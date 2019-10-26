/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pcbdraw.circuit;

import java.util.ArrayList;
import pcbdraw.circuit.traces.CircuitTrace;
import pcbdraw.circuit.traces.HoleTrace;
import pcbdraw.circuit.traces.PathTrace;
import pcbdraw.circuit.traces.TraceGroup;

/**
 *
 * @author Nick Berryman
 */
public class MilliGrid {
    private double zoom;
    private double squareSizeMM;
    private PCB pcb;
    private TraceGroup selected = null;

    public MilliGrid(double zoom, double squareSizeMM, PCB pcb) {
        this.zoom = zoom;
        this.squareSizeMM = squareSizeMM;
        this.pcb = pcb;
    }
    
    public MilliGrid(){
        this(5,4,new PCB(new Coordinate(100,100), true));
    }

    public double getZoom() {
        return zoom;
    }

    public void setZoom(double zoom) {
        this.zoom = zoom;
    }

    public double getSquareSizeMM() {
        return squareSizeMM;
    }

    public void setSquareSizeMM(double squareSizeMM) {
        this.squareSizeMM = squareSizeMM;
    }

    public PCB getPCB() {
        return pcb;
    }

    public void setPCB(PCB pcb) {
        this.pcb = pcb;
    }
    
    public void selectAllInRange(Coordinate start, Coordinate end){
        ArrayList<CircuitTrace> toSelect = new ArrayList<>();
        for (CircuitTrace t : pcb.getTraces()){
            if (t instanceof PathTrace){
                PathTrace p = (PathTrace)t;
                if (  (p.getStartPoint().x >= start.x && p.getStartPoint().x <= end.x
                    && p.getStartPoint().y <= start.y && p.getStartPoint().y >= end.y)
                    ||(p.getEndPoint().x   >= start.x && p.getEndPoint().x   <= end.x
                    && p.getEndPoint().y   <= start.y && p.getEndPoint().y   >= end.y))
                {
                    toSelect.add(t);
                }
            }
            else if (t instanceof HoleTrace){
                HoleTrace h = (HoleTrace)t;
                if ( h.getMajorCoord().x >= start.x && h.getMajorCoord().x <= end.x
                     && h.getMajorCoord().y <= start.y && h.getMajorCoord().y >= end.y)
                {
                    toSelect.add(t);
                }
            }
        }
        if (toSelect.size() > 0) this.selected = new TraceGroup(toSelect, this.pcb);
        else this.selected = null;
    }
    
    public void select(TraceGroup toSelect){
        this.selected = toSelect;
        this.verifySelected();
    }
    
    public void deselectAll(){
        this.selected = null;
    }
    
    public void verifySelected(){
        this.selected.verify();
        if (this.selected.getTraces().isEmpty()) this.selected = null;
    }
    
    public TraceGroup getSelected(){
        return this.selected;
    }
    
    public boolean checkSelected(CircuitTrace toCheck){
        if (this.selected == null) return false;
        return this.selected.contains(toCheck);
    }
    
    public double mmToGUI(double mmValue){return zoom*mmValue;}
    public double GUIToMM(double GUIValue){return GUIValue/zoom;}
    public Coordinate mmToGUI(Coordinate coord){return new Coordinate(mmToGUI(coord.x), mmToGUI(this.pcb.getSize().y) - mmToGUI(coord.y));}
    public Coordinate GUIToMM(Coordinate coord){return new Coordinate(GUIToMM(coord.x), this.pcb.getSize().y - GUIToMM(coord.y));}
    public Coordinate mmRoundGridSquare(Coordinate coord){return new Coordinate(Math.round(coord.x/squareSizeMM)*squareSizeMM, Math.round(coord.y/squareSizeMM)*squareSizeMM);}
    public Coordinate GUIRoundGridSquare(Coordinate coord){return mmToGUI(mmRoundGridSquare(GUIToMM(coord)));}
}
