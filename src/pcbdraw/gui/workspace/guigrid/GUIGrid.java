/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pcbdraw.gui.workspace.guigrid;

import java.util.Collections;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import pcbdraw.circuit.Coordinate;
import pcbdraw.circuit.HoleTrace;
import pcbdraw.circuit.MilliGrid;
import pcbdraw.circuit.PathTrace;
import pcbdraw.gui.workspace.drawable.DrawableLine;
import pcbdraw.gui.workspace.drawable.DrawableRect;

/**
 *
 * @author Nick Berryman
 */
public class GUIGrid {
    public double zoom;
    private double squareSizeMM;
    private boolean carvey;
    private final MilliGrid workspace;
    private final DrawableLine drawingLine;
    private final DrawableRect selectingRect;
    
    private final GUITraceCollection traces = new GUITraceCollection();
    
    public GUIGrid(double zoom, double squareSizeMM, boolean carvey, MilliGrid workspace){
        this.zoom = zoom;
        this.squareSizeMM = squareSizeMM;
        this.workspace = workspace;
        this.carvey = carvey;
        
        drawingLine = new DrawableLine(this::finishDrawingLine, this);
        selectingRect = new DrawableRect(this::finishSelecting, this);
    }
    
    public void setZoom(double zoom){this.zoom = zoom;}
    public void setSquareSizeMM(double squareSizeMM){this.squareSizeMM = squareSizeMM;}
    public void setCarvey(boolean carvey){this.carvey = carvey;}
    
    public DrawableLine getDrawingLine(){return this.drawingLine;}
    public DrawableRect getSelectingRect(){return this.selectingRect;}
    
    public double mmToGUI(double mmValue){return zoom*mmValue;}
    public double GUIToMM(double GUIValue){return GUIValue/zoom;}
    public Coordinate mmToGUI(Coordinate coord){
        return new Coordinate(mmToGUI(coord.x), mmToGUI(this.workspace.getSize().y) - mmToGUI(coord.y));
    }
    public Coordinate GUIToMM(Coordinate coord){return new Coordinate(GUIToMM(coord.x), this.workspace.getSize().y - GUIToMM(coord.y));}
    //public Coordinate GUIToMM(Coordinate coord){return new Coordinate(GUIToMM(coord.x), GUIToMM(coord.y));}
    public Coordinate mmRoundGridSquare(Coordinate coord){return new Coordinate(Math.round(coord.x/squareSizeMM)*squareSizeMM, Math.round(coord.y/squareSizeMM)*squareSizeMM);}
    public Coordinate GUIRoundGridSquare(Coordinate coord){return mmToGUI(mmRoundGridSquare(GUIToMM(coord)));}
    
    public void redraw(Pane pane){
        pane.getChildren().clear();
        this.drawGrid(pane);
        this.drawTraces(pane);
    }
    
    public void drawGrid(Pane pane){
        double stroke = 0.5;
        for (double x = 0; x < mmToGUI(workspace.getSize().x); x+=mmToGUI(squareSizeMM)){
            Line l = new Line(x, 0, x, mmToGUI(workspace.getSize().y));
            l.setStrokeWidth(stroke);
            pane.getChildren().add(l);
        }
        for (double y = 0; y < mmToGUI(workspace.getSize().y); y+=mmToGUI(squareSizeMM)){
            Line l = new Line(0, y, mmToGUI(workspace.getSize().x), y);
            l.setStrokeWidth(stroke);
            pane.getChildren().add(l);
        }
        if (this.carvey) drawCarvey(pane);
        drawingLine.draw(pane);
        selectingRect.draw(pane);
    }
    
    public void drawTraces(Pane pane){
        Collections.sort(traces.getTraces(), (GUITrace o1, GUITrace o2) -> {
            if (o1 instanceof GUIPath && o2 instanceof GUIHole) return -1;
            if (o2 instanceof GUIPath && o1 instanceof GUIHole) return 1;
            return 0;
        }); //Sets holes to end of list (render at top)
        
        for (GUITrace trace : traces.getTraces())
            trace.draw(pane);
    }
    
    //Provide in mm
    private void addPath(Coordinate start, Coordinate end){
        start = mmRoundGridSquare(start);
        end = mmRoundGridSquare(end);
        PathTrace path = new PathTrace(start, end);
        workspace.addTrace(path);
        this.traces.getTraces().add(new GUIPath(path, this));
        //TODO Check range/carvey
    }
    
    //Provide in GUI Units
    public void createHole(Coordinate centre){
        centre = GUIToMM(centre);
        this.addHole(centre);
    }
    
    
    
    
    private void drawCarvey(Pane pane){
        double height = Math.min(mmToGUI(3.25*25.4), mmToGUI(workspace.getSize().y));
        double width  = Math.min(mmToGUI(0.75*25.4), mmToGUI(workspace.getSize().x));
        Rectangle clampV = new Rectangle(0, mmToGUI(workspace.getSize().y)-height, width, height);
        pane.getChildren().add(clampV);
        Rectangle clampH = new Rectangle(0, mmToGUI(workspace.getSize().y)-width, height, width);
        pane.getChildren().add(clampH);
    }
    
    //Provide in mm
    private void addHole(Coordinate centre){
        centre = mmRoundGridSquare(centre);
        HoleTrace hole = new HoleTrace(centre);
        workspace.addTrace(hole);
        this.traces.getTraces().add(new GUIHole(hole, mmToGUI(squareSizeMM)/4.0, this));
        //TODO Check range/carvey
    }
    
    private void finishDrawingLine(){
        Coordinate start = new Coordinate(this.drawingLine.getLine().getStartX(), this.drawingLine.getLine().getStartY());
        start = GUIToMM(start);
        Coordinate end = new Coordinate(this.drawingLine.getLine().getEndX(), this.drawingLine.getLine().getEndY());
        end = GUIToMM(end);
        this.addPath(start, end);
        this.drawingLine.reset();
    }
    
    private void finishSelecting(){
        Coordinate start = new Coordinate(selectingRect.getRectangle().getX(), selectingRect.getRectangle().getY());
        Coordinate size = new Coordinate(selectingRect.getRectangle().getWidth(), selectingRect.getRectangle().getHeight());
        
        traces.deselectAll();
        traces.selectAllInRegion(start, size);
        this.selectingRect.reset();
    }
}
