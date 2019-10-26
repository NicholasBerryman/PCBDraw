/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pcbdraw.gui;

import javafx.geometry.Orientation;
import javafx.scene.control.SplitPane;
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
    
    public void initialise(){
        mainPart = new SplitPane();
        mainPart.setDividerPosition(0, 0);
        workspace = new WorkPane();
        context = new ContextPane(workspace.getWorkspaceGrid(),workspace.workspaceHandler(),workspace.getWorkPane());
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
        this.context = new ContextPane(this.workspace.getWorkspaceGrid(),this.workspace.workspaceHandler(),this.workspace.getWorkPane());
        this.context.setMinWidth(190);
        this.context.setMaxWidth(250);
        mainPart.getItems().add(context);
        mainPart.getItems().add(this.workspace);
    }
}
