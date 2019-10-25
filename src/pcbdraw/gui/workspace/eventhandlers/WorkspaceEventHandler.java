/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pcbdraw.gui.workspace.eventhandlers;

import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import pcbdraw.circuit.Coordinate;
import pcbdraw.gui.workspace.WorkPane.WorkspaceAction;
import pcbdraw.gui.workspace.guigrid.GUIGrid;

/**
 *
 * @author Nick Berryman
 */
public class WorkspaceEventHandler{
    public final ClickHandler clickHandler;
    public final MoveHandler moveHandler;
    private final GUIGrid workspace;
    private WorkspaceAction action = WorkspaceAction.Default;
    private DrawingState drawingState = DrawingState.NotDrawing;
    
    private enum DrawingState{
        NotDrawing,
        DrawingPath,
        DrawingSelect
    }

    public WorkspaceEventHandler(GUIGrid workspace, Pane pane) {
        this.workspace = workspace;
        this.clickHandler = new ClickHandler(pane);
        this.moveHandler = new MoveHandler(pane);
    }
    
    public void setAction(WorkspaceAction newAction){
        cancelAction();
        this.action = newAction;
    }
    
    public void show(Pane pane){
        this.workspace.redraw(pane);
    }
    
    public void deselect(){
        workspace.deselect();
    }
    
    public void cancelAction(){
        workspace.getDrawingLine().reset();
        workspace.getSelectingRect().reset();
        this.drawingState = DrawingState.NotDrawing;
    }
    
    public void performDelete(Pane pane){
        workspace.deleteSelected();
        workspace.redraw(pane);
    }
    
    private class ClickHandler implements EventHandler<MouseEvent>{
        private final Pane pane;
        public ClickHandler(Pane p){
            this.pane = p;
        }
        
        @Override
        public void handle(MouseEvent event) {
            Coordinate mouseCoord = new Coordinate(event.getX(), event.getY());
            switch (action){
                case DrawLine:
                    if (drawingState != DrawingState.DrawingPath && drawingState != DrawingState.NotDrawing) cancelAction();
                    if (drawingState == DrawingState.DrawingPath){
                        workspace.getDrawingLine().updateDrawing(mouseCoord);
                        workspace.getDrawingLine().finishDrawing();
                        drawingState = DrawingState.NotDrawing;
                        workspace.redraw(pane);
                    }
                    else{
                        workspace.getDrawingLine().startDrawing(mouseCoord);
                        drawingState = DrawingState.DrawingPath;
                        workspace.redraw(pane);
                    }
                    break;
                case DrawHole:
                    cancelAction();
                    workspace.createHole(mouseCoord);
                    workspace.redraw(pane);
                    break;
                case Select:
                    if (drawingState != DrawingState.DrawingSelect && drawingState != DrawingState.NotDrawing) cancelAction();
                    if (drawingState == DrawingState.DrawingSelect){
                        workspace.getSelectingRect().updateDrawing(mouseCoord);
                        workspace.getSelectingRect().finishDrawing();
                        drawingState = DrawingState.NotDrawing;
                        workspace.redraw(pane);
                    }
                    else{
                        workspace.getSelectingRect().startDrawing(mouseCoord);
                        drawingState = DrawingState.DrawingSelect;
                        workspace.redraw(pane);
                    }
                    break;
                case Move:
                    workspace.commitSelected();
                    workspace.deselect();
                    action = WorkspaceAction.None;
                    break;
            }
        }
    }
    
    private class MoveHandler implements EventHandler<MouseEvent>{
        private final Pane pane;
        public MoveHandler(Pane p){
            this.pane = p;
        }
        
        @Override
        public void handle(MouseEvent event) {
            Coordinate mouseCoord = new Coordinate(event.getX(), event.getY());
            switch (action){
                case DrawLine:
                    if (drawingState == DrawingState.DrawingPath){
                        workspace.getDrawingLine().updateDrawing(mouseCoord);
                        workspace.redraw(pane);
                    }
                    break;
                case DrawHole:
                    break;
                case Select:
                    if (drawingState == DrawingState.DrawingSelect){
                        workspace.getSelectingRect().updateDrawing(mouseCoord);
                        workspace.redraw(pane);
                    }
                    break;
                case Move:
                    workspace.moveSelected(mouseCoord);
                    workspace.redraw(pane);
                    break;
            }
        }
    }
}
