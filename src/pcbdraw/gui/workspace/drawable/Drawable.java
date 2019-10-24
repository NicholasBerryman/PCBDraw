/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pcbdraw.gui.workspace.drawable;

import javafx.scene.layout.Pane;
import pcbdraw.circuit.Coordinate;
import pcbdraw.gui.workspace.guigrid.GUIGrid;

/**
 *
 * @author Nick Berryman
 */
public abstract class Drawable {
    private final Runnable finishTask;
    private final GUIGrid grid;
    
    public Drawable(Runnable onFinish, GUIGrid grid){
        this.finishTask = onFinish;
        this.grid = grid;
    }
    
    public GUIGrid getGrid(){
        return grid;
    }
    
    public abstract void startDrawing(Coordinate start);
    public abstract void updateDrawing(Coordinate update);
    public abstract void reset();
    
    public void finishDrawing(){
        if (this.finishTask != null) this.finishTask.run();
        this.reset();
    }
}
