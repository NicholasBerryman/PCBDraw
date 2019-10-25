/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pcbdraw.gui.workspace.guigrid;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import pcbdraw.circuit.Coordinate;
import pcbdraw.circuit.HoleTrace;

/**
 *
 * @author Nick Berryman
 */
public class GUIHole extends GUITrace{
    private final HoleTrace trace;
    private final Circle circle;
    private final GUIGrid grid;
    
    public GUIHole(HoleTrace trace, double radius, GUIGrid grid){
        this.trace = trace;
        Coordinate centre = grid.mmToGUI(trace.getMajorCoord());
        circle = new Circle();
        circle.setCenterX(centre.x);
        circle.setCenterY(centre.y);
        circle.setStroke(Color.RED);
        circle.setFill(Color.WHITE);
        circle.setStrokeWidth(2);
        circle.setRadius(radius);
        this.grid = grid;
        
        super.init(circle, trace);
    }
    
    public Coordinate getCentre(){
        return grid.mmToGUI(trace.getMajorCoord());
    }

    @Override
    public void moveApparent(Coordinate dist) {
        Coordinate newPos = new Coordinate(circle.getCenterX(), circle.getCenterY()).add(dist);
        circle.setCenterX(newPos.x);
        circle.setCenterY(newPos.y);
    }

    @Override
    public void commitMove() {
        Coordinate newPos = grid.GUIToMM(new Coordinate(circle.getCenterX(),circle.getCenterY()));
        this.trace.setCentrePoint(newPos);
    }

    @Override
    public void cancelMove() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
