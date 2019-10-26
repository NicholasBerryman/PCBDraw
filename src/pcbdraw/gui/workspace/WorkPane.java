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
import pcbdraw.circuit.MilliGrid;
import pcbdraw.gui.workspace.eventhandlers.WorkspaceEventHandler;

/**
 *
 * @author Nick Berryman
 */
public class WorkPane extends ScrollPane{
    private final Pane pane = new Pane();
    private GUIGrid workspace;
    private WorkspaceEventHandler workspaceHandler;

    public WorkPane() {
        this.workspace = new GUIGrid(new MilliGrid());
        workspaceHandler = new WorkspaceEventHandler(workspace, pane);
        
        this.setContent(pane);
        pane.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
    
        this.pane.addEventHandler(MouseEvent.MOUSE_CLICKED, workspaceHandler.clickHandler);
        this.pane.addEventHandler(MouseEvent.MOUSE_MOVED, workspaceHandler.moveHandler);
        this.workspace.draw(pane);
    }
    
    public enum WorkspaceAction{
        DrawLine,
        DrawHole,
        Select,
        Move,
        None;
        public final static WorkspaceAction Default = DrawLine;
    }

    public void setAction(WorkspaceAction action){
        this.workspaceHandler.setAction(action);
    }
    
    public void setWorkspace(GUIGrid workspace){
        this.pane.removeEventHandler(MouseEvent.MOUSE_CLICKED, workspaceHandler.clickHandler);
        this.pane.removeEventHandler(MouseEvent.MOUSE_MOVED, workspaceHandler.moveHandler);
        this.pane.getChildren().clear();
        this.workspace = workspace;
        this.workspaceHandler = new WorkspaceEventHandler(workspace, pane);
        this.workspace.draw(pane);
        this.pane.addEventHandler(MouseEvent.MOUSE_CLICKED, workspaceHandler.clickHandler);
        this.pane.addEventHandler(MouseEvent.MOUSE_MOVED, workspaceHandler.moveHandler);
    }
    
    public void update(){
        workspace.draw(pane);
    }
    
    public Pane getWorkPane(){
        return pane;
    }
    
    public GUIGrid getWorkspaceGrid(){
        return workspace;
    }
    
    public WorkspaceEventHandler getWorkspaceHandler(){
        return workspaceHandler;
    }
    
    public void undo(){
        this.workspaceHandler.undo();
        this.workspaceHandler.show(pane);
    }
    
    public void redo(){
        this.workspaceHandler.redo();
        this.workspaceHandler.show(pane);
    }
    
    public void cut(){
        this.workspaceHandler.cut();
        this.workspaceHandler.show(pane);
    }
    
    public void copy(){
        this.workspaceHandler.copy();
        this.workspaceHandler.show(pane);
    }
    
    public void paste(){
        this.workspaceHandler.paste();
        this.workspaceHandler.show(pane);
    }
}
