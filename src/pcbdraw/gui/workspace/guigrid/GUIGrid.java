/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pcbdraw.gui.workspace.guigrid;

import java.util.ArrayList;
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
import pcbdraw.gui.workspace.eventhandlers.ReversibleAction;
import pcbdraw.gui.workspace.eventhandlers.UndoController;

/**
 *
 * @author Nick Berryman
 */
public class GUIGrid {
    private double zoom;
    private double squareSizeMM;
    private boolean carvey;
    private MilliGrid workspace;
    private final DrawableLine drawingLine;
    private final DrawableRect selectingRect;
    private final UndoController undoController = new UndoController();
    
    private final GUITraceCollection traces = new GUITraceCollection();
    
    public GUIGrid(double zoom, double squareSizeMM, boolean carvey, MilliGrid workspace){
        this.zoom = zoom;
        this.squareSizeMM = squareSizeMM;
        this.carvey = carvey;
        this.workspace = workspace;
        
        drawingLine = new DrawableLine(this::finishDrawingLine, this);
        selectingRect = new DrawableRect(this::finishSelecting, this);
    }
    
    public GUIGrid(){
        this(5,4,true,new MilliGrid(new Coordinate(100,100)));
    }
    
    public void deselect(){this.traces.deselectAll();}
    public void clearUndo(){this.undoController.clear();}
    
    public void setSize(Coordinate size) {this.workspace.resize(size);}; //In mm
    public void setSquareSizeMM(double squareSizeMM){this.squareSizeMM = squareSizeMM;}
    public void setCarvey(boolean carvey){this.carvey = carvey;}
    public void setZoom(double zoom){
        this.zoom = zoom;
        for (GUITrace t : traces.getTraces()) t.cancelMove();
    }
    
    public double getZoom(){return this.zoom;}
    public double getSquareSizeMM(){return this.squareSizeMM;}
    public boolean getCarvey(){return this.carvey;}
    public MilliGrid getWorkspace(){return this.workspace;}
    public DrawableLine getDrawingLine(){return this.drawingLine;}
    public DrawableRect getSelectingRect(){return this.selectingRect;}
    
    public double mmToGUI(double mmValue){return zoom*mmValue;}
    public double GUIToMM(double GUIValue){return GUIValue/zoom;}
    public Coordinate mmToGUI(Coordinate coord){return new Coordinate(mmToGUI(coord.x), mmToGUI(this.workspace.getSize().y) - mmToGUI(coord.y));}
    public Coordinate GUIToMM(Coordinate coord){return new Coordinate(GUIToMM(coord.x), this.workspace.getSize().y - GUIToMM(coord.y));}
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
    
    private void drawCarvey(Pane pane){
        //TODO fix 
        double height = Math.min(mmToGUI(3.25*25.4), mmToGUI(workspace.getSize().y));
        double width  = Math.min(mmToGUI(0.75*25.4), mmToGUI(workspace.getSize().x));
        Rectangle clampV = new Rectangle(0, mmToGUI(workspace.getSize().y)-height, width, height);
        pane.getChildren().add(clampV);
        height = Math.min(mmToGUI(0.75*25.4), mmToGUI(workspace.getSize().y));
        width  = Math.min(mmToGUI(3.25*25.4), mmToGUI(workspace.getSize().x));
        Rectangle clampH = new Rectangle(0, mmToGUI(workspace.getSize().y)-height, width, height);
        pane.getChildren().add(clampH);
    }
    
    //Provide in GUI Units
    public void createHole(Coordinate centre){
        centre = GUIToMM(centre);
        this.addHole(centre, true);
    }
    
    public void deleteSelected(){
        ArrayList<GUITrace> deleted = this.traces.deleteSelected();
        undoController.add(new ReversibleAction(){
            public void redo(){traces.getTraces().removeAll(deleted);}
            public void undo(){traces.getTraces().addAll(deleted);}
        });
    }
    
    private Coordinate oldPos = null;
    public void moveSelected(Coordinate newCoord){
        if (oldPos == null) oldPos = this.traces.moveSelected(this.GUIRoundGridSquare(newCoord));
        else this.traces.moveSelected(this.GUIRoundGridSquare(newCoord));
    }
    
    public void commitSelected(){
        ArrayList<GUITrace> moved = this.traces.getSelected();
        Coordinate newCoord = this.traces.commitSelected();
        Coordinate oldCoord = this.oldPos;
        undoController.add(new ReversibleAction(){
            public void redo(){traces.deselectAll();for (GUITrace t : moved)t.select();traces.moveSelected(newCoord);traces.deselectAll();}
            public void undo(){traces.deselectAll();for (GUITrace t : moved)t.select();traces.moveSelected(oldCoord);traces.deselectAll();}
        });
        this.oldPos = null;
    }
    
    //Provide in mm
    public void addHole(Coordinate centre, boolean roundToGrid){
        if (roundToGrid) centre = mmRoundGridSquare(centre);
        HoleTrace hole = new HoleTrace(centre);
        if (workspace.addTrace(hole) != null){
            GUITrace h = new GUIHole(hole, this);
            this.traces.getTraces().add(h);
            undoController.add(new ReversibleAction(){
                public void redo(){workspace.addTrace(hole); traces.getTraces().add(h);}
                public void undo(){workspace.getTraces().remove(hole); traces.getTraces().remove(h);}
            });
        }
        //TODO Check range/carvey
    }
    
    //Provide in mm
    public void addPath(Coordinate start, Coordinate end, boolean roundToGrid){
        if (roundToGrid)start = mmRoundGridSquare(start);
        if (roundToGrid)end = mmRoundGridSquare(end);
        PathTrace path = new PathTrace(start, end);
        if (workspace.addTrace(path) != null){
            GUIPath p = new GUIPath(path, this);
            this.traces.getTraces().add(p);
            undoController.add(new ReversibleAction(){
                public void redo(){workspace.addTrace(path); traces.getTraces().add(p);}
                public void undo(){workspace.getTraces().remove(path); traces.getTraces().remove(p);}
            });
        }
        //TODO Check range/carvey
    }
    
    public void undo(){this.undoController.undo();}
    public void redo(){this.undoController.redo();}
    
    private void finishDrawingLine(){
        Coordinate start = new Coordinate(this.drawingLine.getLine().getStartX(), this.drawingLine.getLine().getStartY());
        start = GUIToMM(start);
        Coordinate end = new Coordinate(this.drawingLine.getLine().getEndX(), this.drawingLine.getLine().getEndY());
        end = GUIToMM(end);
        this.addPath(start, end, true);
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
