/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pcbdraw.gui.workspace.guigrid;

import java.util.ArrayList;
import pcbdraw.circuit.Coordinate;

/**
 *
 * @author Nick Berryman
 */
public class GUITraceCollection {
    private final ArrayList<GUITrace> traces = new ArrayList<>();
    
    public ArrayList<GUITrace> getTraces(){
        return traces;
    }
    
    //Take in as GUI Units
    public void selectAllInRegion(Coordinate start, Coordinate size){
        Coordinate end = start.add(size);
        for (GUITrace t : traces){
            if (t instanceof GUIPath){
                GUIPath p = (GUIPath)t;
                if (  (p.getStart().x >= start.x && p.getStart().x <= end.x
                    && p.getStart().y >= start.y && p.getStart().y <= end.y)
                    ||(p.getEnd().x   >= start.x && p.getEnd().x   <= end.x
                    && p.getEnd().y   >= start.y && p.getEnd().y   <= end.y))
                {
                    t.select();
                }
            }
            else if (t instanceof GUIHole){
                GUIHole h = (GUIHole)t;
                if ( h.getCentre().x >= start.x && h.getCentre().x <= end.x
                     && h.getCentre().y >= start.y && h.getCentre().y <= end.y)
                {
                    t.select();
                }
            }
        }
        
        //TODO update for better selection of lines, maybe
    }
    
    public ArrayList<GUITrace> deleteSelected(){
        ArrayList<GUITrace> newTraces = new ArrayList<>();
        for (GUITrace t : traces) if (t.isSelected()) newTraces.add(t);
        traces.removeAll(newTraces);
        return newTraces;
    }
    
    public void deselectAll(){
        for (GUITrace t : traces)
            t.deselect();
    }
    
    public ArrayList<GUITrace> getSelected(){
        ArrayList<GUITrace> temp = new ArrayList<>();
        for (GUITrace t : traces){
            if (t.isSelected()) temp.add(t);
        }
        return temp;
    }
    
    public Coordinate moveSelected(Coordinate newPos){
        Coordinate currTopLeft = new Coordinate(Double.MAX_VALUE, Double.MAX_VALUE);
        for (GUITrace t : traces){
            if (t.getBoundPos().x < currTopLeft.x) 
                currTopLeft = new Coordinate(t.getBoundPos().x, currTopLeft.y);
            if (t.getBoundPos().y < currTopLeft.y) 
                currTopLeft = new Coordinate(currTopLeft.x, t.getBoundPos().y);
        }
        Coordinate dist = newPos.subtract(currTopLeft);
        for (GUITrace t : traces){
            t.moveApparent(dist);
        }
        return currTopLeft;
    }
    
    public Coordinate commitSelected(){
        Coordinate currTopLeft = new Coordinate(Double.MAX_VALUE, Double.MAX_VALUE);
        for (GUITrace t : traces){
            if (t.getBoundPos().x < currTopLeft.x) 
                currTopLeft = new Coordinate(t.getBoundPos().x, currTopLeft.y);
            if (t.getBoundPos().y < currTopLeft.y) 
                currTopLeft = new Coordinate(currTopLeft.x, t.getBoundPos().y);
        }
        for (GUITrace t : this.getSelected()){
            t.commitMove();
        }
        return currTopLeft;
    }
    
    public void cancelMove(){
        for (GUITrace t : traces){
            t.cancelMove();
        }
    }
}
