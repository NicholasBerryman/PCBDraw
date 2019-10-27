/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pcbdraw.gui;

import java.io.IOException;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import pcbdraw.circuit.MilliGrid;
import pcbdraw.data.GCBFile;
import pcbdraw.gui.workspace.guigrid.GUIGrid;

/**
 *
 * @author Nick Berryman
 */
public class TopMenu extends MenuBar{
    private final MainPane mainPane;
    private final MenuItem save;
    private final MenuItem saveAs;
    private final MenuItem open;
    private final MenuItem newFile;
    
    private final MenuItem undo;
    private final MenuItem redo;
    private final MenuItem cut;
    private final MenuItem copy;
    private final MenuItem paste;
    
    public TopMenu(MainPane p){
        this.mainPane = p;
        Menu fileMenu = new Menu("File");
        this.getMenus().add(fileMenu);
        
        newFile = new MenuItem("New ");
        fileMenu.getItems().add(newFile);
        newFile.setOnAction((e) -> {newFile();});
        saveAs = new MenuItem("Save As");
        save = new MenuItem("Save");
        fileMenu.getItems().add(save);
        save.setOnAction((e) -> {save();});
        fileMenu.getItems().add(saveAs);
        saveAs.setOnAction((e) -> {saveAs();});
        open = new MenuItem("Open");
        fileMenu.getItems().add(open);
        open.setOnAction((e) -> {open();});
        
        
        Menu editMenu = new Menu("Edit");
        this.getMenus().add(editMenu);
        
        undo = new MenuItem("Undo");
        editMenu.getItems().add(undo);
        undo.setOnAction((e) -> {p.getWorkPane().undo();});
        redo = new MenuItem("Redo");
        editMenu.getItems().add(redo);
        redo.setOnAction((e) -> {p.getWorkPane().redo();});
        cut = new MenuItem("Cut");
        editMenu.getItems().add(cut);
        cut.setOnAction((e) -> {p.getWorkPane().cut();});
        copy = new MenuItem("Copy");
        editMenu.getItems().add(copy);
        copy.setOnAction((e) -> {p.getWorkPane().copy();});
        paste = new MenuItem("Paste");
        editMenu.getItems().add(paste);
        paste.setOnAction((e) -> {p.getWorkPane().paste();});
        
        new Shortcuts().initialise();
    }
    
    public boolean saveAs(){
        try {
            GCBFile saving = GCBFile.askUserToSaveAs();
            if (saving != null) {
                saving.save(mainPane.getWorkPane().getWorkspaceGrid().getWorkspace());
                mainPane.setGCBFile(saving);
                return true;
            }
            return false;
        } catch (IOException ex) {
            Logger.getLogger(TopMenu.class.getName()).log(Level.SEVERE, null, ex);
        }
        return true;
    }
    
    public boolean save(){
        if (mainPane.getGCBFile() != null){
            try {
                mainPane.getGCBFile().save(mainPane.getWorkPane().getWorkspaceGrid().getWorkspace());
            } catch (IOException ex) {
                Logger.getLogger(TopMenu.class.getName()).log(Level.SEVERE, null, ex);
            }
            return true;
        }
        else return saveAs();
    }
    
    public void open(){
        try {
            GCBFile loading = GCBFile.askUserToOpen();
            if (loading != null){
                mainPane.setWorkspace(new GUIGrid(loading.read()));
                mainPane.setGCBFile(loading);
            }
        } catch (IOException ex) {
            Logger.getLogger(TopMenu.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public void newFile(){
        Alert saveConfirm = new Alert(Alert.AlertType.CONFIRMATION);
        saveConfirm.setTitle("Save PCB?");
        saveConfirm.setHeaderText("Would you like to Save your PCB?");

        ButtonType save = new ButtonType("Save");
        ButtonType no = new ButtonType("No");
        ButtonType cancel = new ButtonType("Cancel");
        saveConfirm.getButtonTypes().setAll(save, no, cancel);
        boolean cont = false;
        try {
            if (mainPane.getGCBFile() == null){
                Optional<ButtonType> result = saveConfirm.showAndWait();
                if (result.get() == save) if (save()) cont = true;
                if (result.get() == no) cont = true;
                if (!cont)return;
            }
            else if (!mainPane.getGCBFile().predictText(mainPane.getWorkPane().getWorkspaceGrid().getWorkspace()).equals(mainPane.getGCBFile().getText())){
                Optional<ButtonType> result = saveConfirm.showAndWait();
                if (result.get() == save) if (save()) cont = true;
                if (result.get() == no) cont = true;
                if (!cont)return;
            }
        } catch (IOException ex) {}
        mainPane.setWorkspace(new GUIGrid(new MilliGrid()));
        mainPane.setGCBFile(null);
    }
    
    private class Shortcuts {
        public void initialise(){
            KeyCombination undoKey = new KeyCodeCombination(KeyCode.Z, KeyCombination.CONTROL_DOWN);
            undo.acceleratorProperty().set(undoKey);

            KeyCombination redoKey = new KeyCodeCombination(KeyCode.Y, KeyCombination.CONTROL_DOWN);
            redo.acceleratorProperty().set(redoKey);
            
            KeyCombination saveKey = new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN);
            save.acceleratorProperty().set(saveKey);

            KeyCombination saveAsKey = new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN, KeyCombination.ALT_DOWN);
            saveAs.acceleratorProperty().set(saveAsKey);
            
            KeyCombination openKey = new KeyCodeCombination(KeyCode.O, KeyCombination.CONTROL_DOWN);
            open.acceleratorProperty().set(openKey);
            
            KeyCombination newFileKey = new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN);
            newFile.acceleratorProperty().set(newFileKey);
            
            KeyCombination cutKey = new KeyCodeCombination(KeyCode.X, KeyCombination.CONTROL_DOWN);
            cut.acceleratorProperty().set(cutKey);
            
            KeyCombination copyKey = new KeyCodeCombination(KeyCode.C, KeyCombination.CONTROL_DOWN);
            copy.acceleratorProperty().set(copyKey);
            
            KeyCombination pasteKey = new KeyCodeCombination(KeyCode.V, KeyCombination.CONTROL_DOWN);
            paste.acceleratorProperty().set(pasteKey);
        }
    }
}
