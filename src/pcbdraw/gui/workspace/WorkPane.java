/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pcbdraw.gui.workspace;

import pcbdraw.gui.workspace.guigrid.GUIGrid;
import javafx.geometry.Insets;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import pcbdraw.circuit.Coordinate;
import pcbdraw.circuit.MilliGrid;
import pcbdraw.gui.workspace.eventhandlers.WorkspaceEventHandler;

/**
 *
 * @author Nick Berryman
 */
public class WorkPane extends ScrollPane{
    private final Pane pane = new Pane();
    private final GUIGrid workspace;
    private WorkspaceEventHandler workspaceHandler;

    public WorkPane(Coordinate sizeMM, double zoom, double squareSizeMM, boolean carvey) {
        this.workspace = new GUIGrid(zoom, squareSizeMM, carvey, new MilliGrid(sizeMM));
        workspaceHandler = new WorkspaceEventHandler(workspace, pane);
        
        this.setContent(pane);
        pane.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
    
        this.pane.addEventHandler(MouseEvent.MOUSE_CLICKED, workspaceHandler.clickHandler);
        this.pane.addEventHandler(MouseEvent.MOUSE_MOVED, workspaceHandler.moveHandler);
    }
    
    public enum WorkspaceAction{
        DrawLine,
        DrawHole,
        Select,
        Move;
        public final static WorkspaceAction Default = DrawLine;
    }

    public void setAction(WorkspaceAction action){
        this.workspaceHandler.setAction(action);
    }
    
    public void update(){
        workspace.redraw(pane);
    }
    
}
