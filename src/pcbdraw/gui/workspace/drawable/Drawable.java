/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pcbdraw.gui.workspace.drawable;

import javafx.scene.layout.Pane;
import pcbdraw.circuit.Coordinate;

/**
 *
 * @author Nick Berryman
 */
public abstract class Drawable {
    private Runnable finishTask;
    public Drawable(Runnable onFinish){
        this.finishTask = onFinish;
    }
    
    public abstract void startDrawing(Coordinate start);
    public abstract void updateDrawing(Coordinate update);
    public abstract void reset();
    
    public void finishDrawing(){
        if (this.finishTask != null) this.finishTask.run();
        this.reset();
    }
}
