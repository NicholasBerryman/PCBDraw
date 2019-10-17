/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pcbdraw.gui;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;

/**
 *
 * @author Nick Berryman
 */
public class TopMenu extends MenuBar{
    MenuItem save;
    MenuItem open;
    MenuItem newFile;
    
    public TopMenu(){
        Menu fileMenu = new Menu("File");
        this.getMenus().add(fileMenu);
        
        newFile = new MenuItem("New");
        fileMenu.getItems().add(newFile);
        save = new MenuItem("Save");
        fileMenu.getItems().add(save);
        open = new MenuItem("Open");
        fileMenu.getItems().add(open);
    }
    
    public void setSaveHandler(EventHandler<ActionEvent> handler){
        save.setOnAction(handler);
    }
    public void setOpenHandler(EventHandler<ActionEvent> handler){
        open.setOnAction(handler);
    }
    public void setNewFileHandler(EventHandler<ActionEvent> handler){
        newFile.setOnAction(handler);
    }
}
