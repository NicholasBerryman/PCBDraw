/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pcbdraw.gui;

import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import pcbdraw.circuit.Coordinate;
import pcbdraw.gui.context.ContextPane;
import pcbdraw.gui.workspace.WorkPane;

/**
 *
 * @author Nick Berryman
 */
public class MainPane extends SplitPane{
    private WorkPane workspace;
    private ContextPane context;
    private Scene scene;
    
    public void initialise(Scene scene){
        SplitPane mainPart = new SplitPane();
        workspace = new WorkPane(new Coordinate(100,100),5,4,true);
        context = new ContextPane(workspace.getWorkspaceGrid(),workspace.workspaceHandler(),workspace.getWorkPane());
        
        this.getItems().add(new TopMenu(scene, workspace));
        this.getItems().add(mainPart);
        this.setOrientation(Orientation.VERTICAL);
        mainPart.getItems().add(context);
        mainPart.getItems().add(workspace);
        workspace.update();
    }
    
    private void undo(){workspace.undo();}
    private void redo(){workspace.redo();}
}
