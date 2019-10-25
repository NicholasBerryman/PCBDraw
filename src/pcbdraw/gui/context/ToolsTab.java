/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pcbdraw.gui.context;

import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Tab;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import pcbdraw.gui.workspace.WorkPane;
import pcbdraw.gui.workspace.eventhandlers.WorkspaceEventHandler;

/**
 *
 * @author Nick Berryman
 */
public class ToolsTab extends Tab{
    private final RadioButton path;
    private final RadioButton hole;
    private final RadioButton select;
    private final ToggleGroup tools;
    private final Button delete;
    private final Button move;
    private final WorkspaceEventHandler workspaceActioner;
    private final Pane workpane;
    
    public ToolsTab(WorkspaceEventHandler workspaceActioner, Pane workpane){
        super();
        this.workspaceActioner = workspaceActioner;
        this.workpane = workpane;
        
        this.setClosable(false);
        this.setText("Tools");
        
        VBox root = new VBox();
        this.setContent(root);
        
        tools = new ToggleGroup();
        
        VBox toggle = new VBox();
        root.getChildren().add(toggle);
        
        path = new RadioButton("Path");
        toggle.getChildren().add(path);
        path.setToggleGroup(tools);
        path.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue){
                workspaceActioner.deselect();
                workspaceActioner.cancelAction();
                workspaceActioner.setAction(WorkPane.WorkspaceAction.DrawLine);
            }
            workspaceActioner.show(workpane);
        });
        path.setSelected(true);
        
        hole = new RadioButton("Hole");
        toggle.getChildren().add(hole);
        hole.setToggleGroup(tools);
        hole.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue){
                workspaceActioner.deselect();
                workspaceActioner.cancelAction();
                workspaceActioner.setAction(WorkPane.WorkspaceAction.DrawHole);
            }
            workspaceActioner.show(workpane);
        });
        
        select = new RadioButton("Select");
        toggle.getChildren().add(select);
        select.setToggleGroup(tools);
        select.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue){
                workspaceActioner.deselect();
                workspaceActioner.cancelAction();
                workspaceActioner.setAction(WorkPane.WorkspaceAction.Select);
            }
            workspaceActioner.show(workpane);
        });
        
        delete = new Button("Delete");
        delete.pressedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue){
                workspaceActioner.cancelAction();
                workspaceActioner.performDelete(workpane);
            }
            workspaceActioner.show(workpane);
        });
        root.getChildren().add(delete);
        
        move = new Button("Move");
        move.pressedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue){
                deselect();
                workspaceActioner.cancelAction();
                workspaceActioner.setAction(WorkPane.WorkspaceAction.Move);
            }
            workspaceActioner.show(workpane);
        });
        
        root.getChildren().add(move);
    }
    public void deselect(){
        path.setSelected(false);
        hole.setSelected(false);
        select.setSelected(false);
    }
}
