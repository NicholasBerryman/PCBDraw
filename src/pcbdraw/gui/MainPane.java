/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pcbdraw.gui;

import java.io.IOException;
import java.util.Optional;
import javafx.geometry.Orientation;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.SplitPane;
import javafx.stage.Stage;
import pcbdraw.data.GCBFile;
import pcbdraw.gui.context.ContextPane;
import pcbdraw.gui.workspace.WorkPane;
import pcbdraw.gui.workspace.guigrid.GUIGrid;

/**
 *
 * @author Nick Berryman
 */
public class MainPane extends SplitPane{
    private WorkPane workspace;
    private ContextPane context;
    private SplitPane mainPart;
    private TopMenu menu;
    private GCBFile gcb;
    private Stage window;
    
    public void initialise(Stage window){
        this.window = window;
        menu = new TopMenu(this);
        this.initExitMessage(window);
        
        mainPart = new SplitPane();
        mainPart.setDividerPosition(0, 0);
        workspace = new WorkPane();
        context = new ContextPane(workspace.getWorkspaceGrid(),workspace.getWorkspaceHandler(),workspace.getWorkPane());
        context.setMinWidth(190);
        context.setMaxWidth(250);
        
        this.getItems().add(menu);
        this.getItems().add(mainPart);
        this.setOrientation(Orientation.VERTICAL);
        mainPart.getItems().add(context);
        mainPart.getItems().add(workspace);
        workspace.update();
    }
    
    private void initExitMessage(Stage window){
        window.setOnCloseRequest((e) -> {
            Alert saveConfirm = new Alert(AlertType.CONFIRMATION);
            saveConfirm.setTitle("Save PCB?");
            saveConfirm.setHeaderText("Would you like to Save your PCB?");
            
            ButtonType save = new ButtonType("Save");
            ButtonType no = new ButtonType("No");
            ButtonType cancel = new ButtonType("Cancel");
            saveConfirm.getButtonTypes().setAll(save, no, cancel);
            
            
            try {
                if (gcb == null){
                    Optional<ButtonType> result = saveConfirm.showAndWait();
                    if (result.get() == save) if (menu.save()) System.exit(0);
                    if (result.get() == no) System.exit(0);
                    e.consume();
                    return;
                }
                else if (!gcb.predictText(workspace.getWorkspaceGrid().getWorkspace()).equals(gcb.getText())){
                    Optional<ButtonType> result = saveConfirm.showAndWait();
                    if (result.get() == save) if (menu.save()) System.exit(0);
                    if (result.get() == no) System.exit(0);
                    e.consume();
                    return;
                }
                System.exit(0);
            } catch (IOException ex) {
                System.exit(0);
            }
        });
    }
    
    public WorkPane getWorkPane(){
        return this.workspace;
    }
    
    public void setWorkspace(GUIGrid workspace){
        mainPart.getItems().remove(context);
        mainPart.getItems().remove(this.workspace);
        
        this.workspace.setWorkspace(workspace);
        this.context = new ContextPane(this.workspace.getWorkspaceGrid(),this.workspace.getWorkspaceHandler(),this.workspace.getWorkPane());
        this.context.setMinWidth(190);
        this.context.setMaxWidth(250);
        mainPart.getItems().add(context);
        mainPart.getItems().add(this.workspace);
        mainPart.setDividerPosition(0,0);
    }
    
    public void setGCBFile(GCBFile gcb){
        this.gcb = gcb;
        if (this.gcb != null)
            window.setTitle("PCBDraw - "+gcb.getName());
        else
            window.setTitle("PCBDraw");
    }
    
    public GCBFile getGCBFile(){
        return this.gcb;
    }
}
