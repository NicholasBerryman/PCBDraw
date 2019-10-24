/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pcbdraw.gui;

import javafx.geometry.Orientation;
import javafx.scene.control.SplitPane;
import pcbdraw.circuit.Coordinate;
import pcbdraw.gui.workspace.WorkPane;

/**
 *
 * @author Nick Berryman
 */
public class MainPane extends SplitPane{
    public void initialise(){
        this.setOrientation(Orientation.VERTICAL);
        
        WorkPane workspace = new WorkPane(new Coordinate(100,100),5,4,false);
        this.getItems().add(workspace);
        workspace.update();
    }
}
