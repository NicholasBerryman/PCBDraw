/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pcbdraw.gui;

import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import pcbdraw.gui.workspace.WorkPane;

/**
 *
 * @author Nick Berryman
 */
public class TopMenu extends MenuBar{
    private final WorkPane workspace;
    private final MenuItem save;
    private final MenuItem open;
    private final MenuItem newFile;
    
    private final MenuItem undo;
    private final MenuItem redo;
    
    public TopMenu(Scene s, WorkPane p){
        this.workspace = p;
        Menu fileMenu = new Menu("File");
        this.getMenus().add(fileMenu);
        
        newFile = new MenuItem("New");
        fileMenu.getItems().add(newFile);
        save = new MenuItem("Save  (Ctrl+S)");
        fileMenu.getItems().add(save);
        open = new MenuItem("Open  (Ctrl+O)");
        fileMenu.getItems().add(open);
        
        
        Menu editMenu = new Menu("Edit");
        this.getMenus().add(editMenu);
        
        undo = new MenuItem("Undo  (Ctrl+Z)");
        editMenu.getItems().add(undo);
        undo.setOnAction((e) -> {undo();});
        redo = new MenuItem("Redo  (Ctrl+Y)");
        editMenu.getItems().add(redo);
        redo.setOnAction((e) -> {redo();});
        
        new Shortcuts().initialise(s);
    }
    
    public void save(){}
    public void open(){}
    public void newFile(){}
    
    public void undo(){workspace.undo();}
    public void redo(){workspace.redo();}
    
    private class Shortcuts {
        public void initialise(Scene s){
            KeyCombination undo = new KeyCodeCombination(KeyCode.Z, KeyCombination.CONTROL_DOWN);
            s.getAccelerators().put(undo, (Runnable) () -> {
                undo();
            });

            KeyCombination redo = new KeyCodeCombination(KeyCode.Y, KeyCombination.CONTROL_DOWN);
            s.getAccelerators().put(redo, (Runnable) () -> {
                redo();
            });

            KeyCombination save = new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN);
            s.getAccelerators().put(save, (Runnable) () -> {
                save();
            });
            
            KeyCombination open = new KeyCodeCombination(KeyCode.O, KeyCombination.CONTROL_DOWN);
            s.getAccelerators().put(open, (Runnable) () -> {
                open();
            });
        }
    }
}
