/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pcbdraw.gui.workspace;

import javafx.geometry.Insets;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import pcbdraw.circuit.Coordinate;
import pcbdraw.circuit.MilliGrid;

/**
 *
 * @author Nick Berryman
 */
public class WorkPane extends ScrollPane{
    private final Pane pane = new Pane();
    private final GUIGrid workspace;
    
    private CanvasAction action;

    public WorkPane(Coordinate sizeMM, double zoom, double squareSizeMM, boolean carvey) {
        this.action = CanvasAction.Default; //Maybe redo to use unified default?
        this.workspace = new GUIGrid(zoom, squareSizeMM, carvey, new MilliGrid(sizeMM));
        
        this.setContent(pane);
        pane.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
    }
    
    public enum CanvasAction{
        DrawLine,
        DrawHole,
        Select,
        Move;
        public final static CanvasAction Default = DrawLine;
    }

    public void setAction(CanvasAction action){
        this.action = action;
    }
    
    public void update(){
        this.pane.getChildren().clear();
        workspace.drawGrid(this.pane);
        workspace.drawTraces(this.pane);
    }
    
}

//TODO: Split into GUIPath/GUIHole/GUIGrid, ToolActionHandlingClass[TODO:RENAME], this class interfaces them
 