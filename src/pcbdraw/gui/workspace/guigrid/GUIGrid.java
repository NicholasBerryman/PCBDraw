/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pcbdraw.gui.workspace.guigrid;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import pcbdraw.circuit.traces.CircuitTrace;
import pcbdraw.circuit.Coordinate;
import pcbdraw.circuit.traces.HoleTrace;
import pcbdraw.circuit.MilliGrid;
import pcbdraw.circuit.traces.PathTrace;
import pcbdraw.gui.workspace.drawable.DrawableLine;
import pcbdraw.gui.workspace.drawable.DrawableMover;
import pcbdraw.gui.workspace.drawable.DrawableRect;

/**
 *
 * @author Nick Berryman
 */
public class GUIGrid {
    private final MilliGrid workspace;
    private final DrawableLine drawingLine;
    private final DrawableRect selectingRect;
    private final DrawableMover mover;
    
    public GUIGrid(MilliGrid workspace){
        this.workspace = workspace;
        drawingLine = new DrawableLine(this.workspace);
        selectingRect = new DrawableRect(this.workspace);
        mover = new DrawableMover(this.workspace);
    }
    
    public MilliGrid getWorkspace(){return this.workspace;}
    public DrawableLine getDrawingLine(){return this.drawingLine;}
    public DrawableRect getSelectingRect(){return this.selectingRect;}
    public DrawableMover getMover(){return this.mover;}
   
    public CircuitTrace addPath(Coordinate start, Coordinate end){
        return this.workspace.getPCB().addTrace(new PathTrace(
                this.workspace.GUIToMM(start),
                this.workspace.GUIToMM(end)
        ));
    }
    
    public CircuitTrace addHole(Coordinate centre){
        return this.workspace.getPCB().addTrace(new HoleTrace(
                this.workspace.GUIToMM(centre)
        ));
    }
    
    public void draw(Pane p){
        p.getChildren().clear();
        this.drawGrid(p);
        this.drawPaths(p);
        this.drawHoles(p);
    }
    
    private void drawGrid(Pane p){
        double stroke = 0.5;
        for (double x = 0; x < workspace.mmToGUI(workspace.getPCB().getSize().x); x+=workspace.mmToGUI(workspace.getSquareSizeMM())){
            Line l = new Line(x, 0, x, workspace.mmToGUI(workspace.getPCB().getSize().y));
            l.setStrokeWidth(stroke);
            p.getChildren().add(l);
        }
        for (double y = 0; y < workspace.mmToGUI(workspace.getPCB().getSize().y); y+=workspace.mmToGUI(workspace.getSquareSizeMM())){
            Line l = new Line(0, y, workspace.mmToGUI(workspace.getPCB().getSize().x), y);
            l.setStrokeWidth(stroke);
            p.getChildren().add(l);
        }
        if (this.getWorkspace().getPCB().isCarvey()) drawCarvey(p);
        drawingLine.draw(p);
        selectingRect.draw(p);
    }
    
    private void drawCarvey(Pane p){
        double height = Math.min(workspace.mmToGUI(3.25*25.4), workspace.mmToGUI(workspace.getPCB().getSize().y));
        double width  = Math.min(workspace.mmToGUI(0.75*25.4), workspace.mmToGUI(workspace.getPCB().getSize().x));
        Rectangle clampV = new Rectangle(0, workspace.mmToGUI(workspace.getPCB().getSize().y)-height, width, height);
        p.getChildren().add(clampV);
        height = Math.min(workspace.mmToGUI(0.75*25.4), workspace.mmToGUI(workspace.getPCB().getSize().y));
        width  = Math.min(workspace.mmToGUI(3.25*25.4), workspace.mmToGUI(workspace.getPCB().getSize().x));
        Rectangle clampH = new Rectangle(0, workspace.mmToGUI(workspace.getPCB().getSize().y)-height, width, height);
        p.getChildren().add(clampH);
    }
    
    private void drawPaths(Pane p){
        for (PathTrace t : workspace.getPCB().getPathTraces()){
            Coordinate start = workspace.mmToGUI(t.getStartPoint());
            Coordinate end = workspace.mmToGUI(t.getEndPoint());
            if (workspace.checkSelected(t)){
                if (mover.isMoving()) {
                    start = start.add(mover.getOffset());
                    end = end.add(mover.getOffset());
                }
            }
            Line l = new Line(
                    start.x,
                    start.y,
                    end.x,
                    end.y
            );
            l.setStroke(Color.RED);
            if (workspace.checkSelected(t)) l.setStroke(Color.BLUE);
            l.setStrokeWidth(2);
            p.getChildren().add(l);
        }
    }
    
    private void drawHoles(Pane p){
        for (HoleTrace h : workspace.getPCB().getHoleTraces()){
            Circle c = new Circle();
            Coordinate centre = workspace.mmToGUI(h.getMajorCoord());
            c.setStroke(Color.RED);
            if (workspace.checkSelected(h)){
                c.setStroke(Color.BLUE);
                if (mover.isMoving()) centre = centre.add(mover.getOffset());
            }
            c.setCenterX(centre.x);
            c.setCenterY(centre.y);
            c.setFill(Color.WHITE);
            c.setStrokeWidth(2);
            c.setRadius(workspace.mmToGUI(workspace.getSquareSizeMM())/4.0);
            p.getChildren().add(c);
        }
    }
}
