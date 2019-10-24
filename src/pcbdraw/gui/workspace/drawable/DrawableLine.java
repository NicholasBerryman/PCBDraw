/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pcbdraw.gui.workspace.drawable;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import pcbdraw.circuit.Coordinate;

/**
 *
 * @author Nick Berryman
 */
public class DrawableLine extends Drawable{
    private Line line = null;

    public DrawableLine(Runnable onFinish) {
        super(onFinish);
    }
    
    @Override
    public void startDrawing(Coordinate start) {
        line = new Line(start.x, start.y, start.x, start.y);
        line.setStroke(Color.RED);
        line.setStrokeWidth(2);
    }

    @Override
    public void updateDrawing(Coordinate update) {
        line.setEndX(update.x);
        line.setEndY(update.y);
    }

    @Override
    public void reset() {
        line = null;
    }
    
    public void draw(Pane p) {
        if (line != null)
            if (!p.getChildren().contains(line))
                p.getChildren().add(line);
    }
    
    public Line getLine(){
        return line;
    }

}
