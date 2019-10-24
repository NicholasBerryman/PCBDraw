/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pcbdraw.gui.workspace.guigrid;

import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import pcbdraw.circuit.Coordinate;
import pcbdraw.circuit.PathTrace;

/**
 *
 * @author Nick Berryman
 */
public class GUIPath extends GUITrace{
    private final PathTrace trace;
    private final Line line;
    private GUIGrid grid;
    public GUIPath(PathTrace trace, GUIGrid grid){
        this.trace = trace;
        Coordinate start = grid.mmToGUI(trace.getStartPoint());
        Coordinate end = grid.mmToGUI(trace.getEndPoint());
        line = new Line(start.x,start.y,end.x,end.y);
        line.setStroke(Color.RED);
        line.setStrokeWidth(2);
        this.grid = grid;
        
        super.init(line, trace);
    }
    
    public Coordinate getStart(){
        return grid.mmToGUI(trace.getStartPoint());
    }
    
    public Coordinate getEnd(){
        return grid.mmToGUI(trace.getEndPoint());
    }
}
