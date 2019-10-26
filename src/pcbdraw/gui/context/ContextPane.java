/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pcbdraw.gui.context;

import javafx.scene.control.TabPane;
import javafx.scene.layout.Pane;
import pcbdraw.gui.workspace.eventhandlers.WorkspaceEventHandler;
import pcbdraw.gui.workspace.guigrid.GUIGrid;

/**
 *
 * @author Nick Berryman
 */
public class ContextPane extends TabPane{
    //TODO add gcoder
    public ContextPane(GUIGrid workspace, WorkspaceEventHandler workspaceActioner, Pane workPane){
        super();
        this.getTabs().add(new BoardTab(workspace, workPane));
        this.getTabs().add(new ToolsTab(workspaceActioner, workPane));
        this.getTabs().add(new ExportTab(workspace.getWorkspace()));
    }
}
