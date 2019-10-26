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
public class DrawableMover extends Drawable{
    private Coordinate start = new Coordinate(0,0);
    private Coordinate current = new Coordinate(0,0);
    private boolean moving = false;

    public DrawableMover(MilliGrid grid) {
        super(grid);
    }
    
    @Override
    public void startDrawing(Coordinate start) {
        this.moving = true;
        start = this.getGrid().GUIRoundGridSquare(start);
        this.start = start;
    }

    @Override
    public void updateDrawing(Coordinate update) {
        update = this.getGrid().GUIRoundGridSquare(update);
        this.current = update;
    }

    @Override
    public void reset() {
        this.start = new Coordinate(0,0);
        this.current = new Coordinate(0,0);
        this.moving = false;
    }

    @Override
    public Coordinate[] finishDrawing() {
        Coordinate c1 = new Coordinate(start.x, start.y);
        Coordinate c2 = new Coordinate(current.x, current.y);
        this.reset();
        return new Coordinate[]{c1, c2};
    }
    
    public Coordinate getOffset(){
        return current.subtract(start);
    }
    
    public boolean isMoving(){
        return this.moving;
    }
}
