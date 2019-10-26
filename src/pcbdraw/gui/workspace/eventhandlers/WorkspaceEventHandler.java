/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pcbdraw.gui.workspace.eventhandlers;

import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import pcbdraw.circuit.traces.CircuitTrace;
import pcbdraw.circuit.Coordinate;
import pcbdraw.circuit.traces.TraceGroup;
import pcbdraw.gui.workspace.WorkPane.WorkspaceAction;
import pcbdraw.gui.workspace.guigrid.GUIGrid;

/**
 *
 * @author Nick Berryman
 */
public class WorkspaceEventHandler{
    public final ClickHandler clickHandler;
    public final MoveHandler moveHandler;
    
    private final UndoController undoController;
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
        this.undoController = new UndoController();
    }
    
    public void setAction(WorkspaceAction newAction){
        cancelAction();
        this.action = newAction;
        if (this.action == WorkspaceAction.Move && this.workspace.getWorkspace().getSelected() != null)
            this.workspace.getMover().startDrawing(this.workspace.getWorkspace().mmToGUI(this.workspace.getWorkspace().getSelected().getAnchor()));
    }
    
    public void show(Pane pane){
        this.workspace.draw(pane);
    }
    
    public void cancelAction(){
        workspace.getDrawingLine().reset();
        workspace.getSelectingRect().reset();
        workspace.getMover().reset();
        this.drawingState = DrawingState.NotDrawing;
    }
    
    public void undo(){
        if (this.drawingState != DrawingState.NotDrawing)
            this.cancelAction();
        else
            this.undoController.undo();
    }
    
    public void redo(){
        this.undoController.redo();
    }
    
    public void deselect(){
        this.workspace.getWorkspace().deselectAll();
    }
    
    public void delete(){
        TraceGroup selected = this.workspace.getWorkspace().getSelected().copy();
        if (selected != null){
            for (CircuitTrace t : selected.getTraces()){
                this.workspace.getWorkspace().getPCB().getTraces().remove(t);
            }
        }
        this.workspace.getWorkspace().deselectAll();
        
        undoController.add(new ReversibleAction(){
            public void redo(){for (CircuitTrace t : selected.getTraces()) workspace.getWorkspace().getPCB().getTraces().remove(t);}
            public void undo(){for (CircuitTrace t : selected.getTraces()) workspace.getWorkspace().getPCB().getTraces().add(t);}
        });
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
                        Coordinate[] pathPos = workspace.getDrawingLine().finishDrawing();
                        CircuitTrace t = workspace.addTrace(pathPos[0], pathPos[1]);
                        undoController.add(new ReversibleAction(){
                            public void redo(){workspace.getWorkspace().getPCB().getTraces().add(t);}
                            public void undo(){workspace.getWorkspace().getPCB().getTraces().remove(t);}
                        });
                        drawingState = DrawingState.NotDrawing;
                        workspace.draw(pane);
                    }
                    else{
                        workspace.getDrawingLine().startDrawing(mouseCoord);
                        drawingState = DrawingState.DrawingPath;
                        workspace.draw(pane);
                    }
                    break;
                case DrawHole:
                    cancelAction();
                    CircuitTrace t = workspace.addHole(workspace.getWorkspace().GUIRoundGridSquare(mouseCoord));
                    undoController.add(new ReversibleAction(){
                        public void redo(){workspace.getWorkspace().getPCB().getTraces().add(t);}
                        public void undo(){workspace.getWorkspace().getPCB().getTraces().remove(t);}
                    });
                    workspace.draw(pane);
                    break;
                case Select:
                    if (drawingState != DrawingState.DrawingSelect && drawingState != DrawingState.NotDrawing) cancelAction();
                    if (drawingState == DrawingState.DrawingSelect){
                        workspace.getSelectingRect().updateDrawing(mouseCoord);
                        Coordinate[] selectPos = workspace.getSelectingRect().finishDrawing();
                        workspace.getWorkspace().selectAllInRange(workspace.getWorkspace().GUIToMM(selectPos[0]), workspace.getWorkspace().GUIToMM(selectPos[1]));
                        drawingState = DrawingState.NotDrawing;
                        workspace.draw(pane);
                    }
                    else{
                        workspace.getSelectingRect().startDrawing(mouseCoord);
                        drawingState = DrawingState.DrawingSelect;
                        workspace.draw(pane);
                    }
                    break;
                case Move:
                    TraceGroup selected = workspace.getWorkspace().getSelected().copy();
                    Coordinate[] movePos = workspace.getMover().finishDrawing();
                    selected.moveTo(workspace.getWorkspace().GUIToMM(movePos[1]));
                    undoController.add(new ReversibleAction(){
                        public void redo(){selected.moveTo(workspace.getWorkspace().GUIToMM(movePos[1]));}
                        public void undo(){selected.moveTo(workspace.getWorkspace().GUIToMM(movePos[0]));}
                    });
                    workspace.getWorkspace().deselectAll();
                    action = WorkspaceAction.None;
                    workspace.draw(pane);
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
                        workspace.draw(pane);
                    }
                    break;
                case DrawHole:
                    break;
                case Select:
                    if (drawingState == DrawingState.DrawingSelect){
                        workspace.getSelectingRect().updateDrawing(mouseCoord);
                        workspace.draw(pane);
                    }
                    break;
                case Move:
                    if (workspace.getMover().isMoving()){
                        workspace.getMover().updateDrawing(mouseCoord);
                        workspace.draw(pane);
                    }
                    break;
            }
        }
    }
}
