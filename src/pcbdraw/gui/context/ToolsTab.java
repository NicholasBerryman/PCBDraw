/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pcbdraw.gui.context;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Tab;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;

/**
 *
 * @author Nick Berryman
 */
public class ToolsTab extends Tab{
    RadioButton path;
    RadioButton hole;
    RadioButton select;
    ToggleGroup tools;
    Button delete;
    Button move;
    
    public ToolsTab(){
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
        path.setSelected(true);
        
        hole = new RadioButton("Hole");
        toggle.getChildren().add(hole);
        hole.setToggleGroup(tools);
        
        select = new RadioButton("Select");
        toggle.getChildren().add(select);
        select.setToggleGroup(tools);
        
        delete = new Button("Delete");
        root.getChildren().add(delete);
        
        move = new Button("Move");
        root.getChildren().add(move);
    }
    
    public void addPathHandler(EventHandler<ActionEvent> handle){
        this.path.setOnAction(handle);
    }
    
    public void addHoleHandler(EventHandler<ActionEvent> handle){
        this.hole.setOnAction(handle);
    }
    
    public void addSelectHandler(EventHandler<ActionEvent> handle){
        this.select.setOnAction(handle);
    }
    
    public void addDeleteHandler(EventHandler<ActionEvent> handle){
        this.delete.setOnAction(handle);
    }
    
    public void addMoveHandler(EventHandler<ActionEvent> handle){
        this.move.setOnAction(handle);
    }
    
    public void deselect(){
        path.setSelected(false);
        hole.setSelected(false);
        select.setSelected(false);
    }
}
