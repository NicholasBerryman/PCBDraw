/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pcbdraw.gui;

import javafx.geometry.Orientation;
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
    private GCBFile gcb;
    private Stage window;
    
    public void initialise(Stage window){
        this.window = window;
        mainPart = new SplitPane();
        mainPart.setDividerPosition(0, 0);
        workspace = new WorkPane();
        context = new ContextPane(workspace.getWorkspaceGrid(),workspace.getWorkspaceHandler(),workspace.getWorkPane());
        context.setMinWidth(190);
        context.setMaxWidth(250);
        
        this.getItems().add(new TopMenu(this));
        this.getItems().add(mainPart);
        this.setOrientation(Orientation.VERTICAL);
        mainPart.getItems().add(context);
        mainPart.getItems().add(workspace);
        workspace.update();
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
