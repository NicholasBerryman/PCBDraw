/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pcbdraw.gui.workspace.drawable;

import pcbdraw.circuit.Coordinate;
import pcbdraw.circuit.MilliGrid;

/**
 *
 * @author Nick Berryman
 */
public abstract class Drawable {
    private final MilliGrid grid;
    
    public Drawable(MilliGrid grid){
        this.grid = grid;
    }
    
    public MilliGrid getGrid(){
        return grid;
    }
    
    public abstract void startDrawing(Coordinate start);
    public abstract void updateDrawing(Coordinate update);
    public abstract void reset();
    
    public abstract Coordinate[] finishDrawing();
}
