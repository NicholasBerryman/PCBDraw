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
    private final GUIGrid grid;
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

    @Override
    public void moveApparent(Coordinate dist) {
        Coordinate newStart = new Coordinate(line.getStartX(), line.getStartY()).add(dist);
        Coordinate newEnd = new Coordinate(line.getEndX(), line.getEndY()).add(dist);
        line.setStartX(newStart.x);
        line.setStartY(newStart.y);
        line.setEndX(newEnd.x);
        line.setEndY(newEnd.y);
    }

    @Override
    public void commitMove() {
        Coordinate traceStart = this.trace.getStartPoint();
        Coordinate dist = traceStart.subtract(grid.GUIToMM(new Coordinate(line.getStartX(), line.getStartY())));
        Coordinate newStart = grid.GUIToMM(new Coordinate(line.getStartX(), line.getStartY()));
        Coordinate newEnd = newStart.add(dist);
        
        this.trace.setStart(newStart);
        this.trace.setEnd(newEnd);
    }

    @Override
    public void cancelMove() {
        this.line.setStartX(grid.mmToGUI(this.trace.getStartPoint()).x);
        this.line.setStartY(grid.mmToGUI(this.trace.getStartPoint()).y);
        this.line.setEndX(grid.mmToGUI(this.trace.getEndPoint()).x);
        this.line.setEndY(grid.mmToGUI(this.trace.getEndPoint()).y);
    }

    @Override
    public Coordinate getBoundPos() {
        return new Coordinate(Math.min(this.line.getStartX(), this.line.getEndX()), Math.min(this.line.getStartY(), this.line.getEndY()));
    }
}
