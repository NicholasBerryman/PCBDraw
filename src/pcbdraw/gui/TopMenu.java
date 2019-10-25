/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pcbdraw.gui;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import pcbdraw.data.GCBFile;
import pcbdraw.gui.workspace.guigrid.GUIGrid;

/**
 *
 * @author Nick Berryman
 */
public class TopMenu extends MenuBar{
    private final MainPane mainPane;
    private final MenuItem save;
    private final MenuItem open;
    private final MenuItem newFile;
    
    private final MenuItem undo;
    private final MenuItem redo;
    
    public TopMenu(MainPane p){
        this.mainPane = p;
        Menu fileMenu = new Menu("File");
        this.getMenus().add(fileMenu);
        
        newFile = new MenuItem("New ");
        fileMenu.getItems().add(newFile);
        newFile.setOnAction((e) -> {newFile();});
        save = new MenuItem("Save");
        fileMenu.getItems().add(save);
        save.setOnAction((e) -> {save();});
        open = new MenuItem("Open");
        fileMenu.getItems().add(open);
        open.setOnAction((e) -> {open();});
        
        
        Menu editMenu = new Menu("Edit");
        this.getMenus().add(editMenu);
        
        undo = new MenuItem("Undo");
        editMenu.getItems().add(undo);
        undo.setOnAction((e) -> {undo();});
        redo = new MenuItem("Redo");
        editMenu.getItems().add(redo);
        redo.setOnAction((e) -> {redo();});
        
        new Shortcuts().initialise();
    }
    
    public void save(){
        try {
            GCBFile saving = GCBFile.askUserToSaveAs();
            if (saving != null) saving.save(mainPane.getWorkPane().getWorkspaceGrid());
        } catch (IOException ex) {
            Logger.getLogger(TopMenu.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public void open(){
        try {
            GCBFile loading = GCBFile.askUserToOpen();
            if (loading != null)mainPane.setWorkspace(loading.read());
        } catch (IOException ex) {
            Logger.getLogger(TopMenu.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public void newFile(){mainPane.setWorkspace(new GUIGrid());}
    
    public void undo(){mainPane.getWorkPane().undo();}
    public void redo(){mainPane.getWorkPane().redo();}
    
    private class Shortcuts {
        public void initialise(){
            KeyCombination undoKey = new KeyCodeCombination(KeyCode.Z, KeyCombination.CONTROL_DOWN);
            undo.acceleratorProperty().set(undoKey);

            KeyCombination redoKey = new KeyCodeCombination(KeyCode.Y, KeyCombination.CONTROL_DOWN);
            redo.acceleratorProperty().set(redoKey);

            KeyCombination saveKey = new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN);
            save.acceleratorProperty().set(saveKey);
            
            KeyCombination openKey = new KeyCodeCombination(KeyCode.O, KeyCombination.CONTROL_DOWN);
            open.acceleratorProperty().set(openKey);
            
            KeyCombination newFileKey = new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN);
            newFile.acceleratorProperty().set(newFileKey);
        }
    }
}
