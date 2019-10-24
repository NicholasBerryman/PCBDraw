/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pcbdraw.gui.workspace.drawable;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import pcbdraw.circuit.Coordinate;

/**
 *
 * @author Nick Berryman
 */
public class DrawableRect extends Drawable{
    Rectangle rect;
    Coordinate anchor;
    
    public DrawableRect(Runnable onFinish) {
        super(onFinish);
    }
    
    @Override
    public void startDrawing(Coordinate start) {
        anchor = start;
        rect = new Rectangle(anchor.x, anchor.y, anchor.x, anchor.y);
        rect.setStroke(Color.LIME);
    }

    @Override
    public void updateDrawing(Coordinate update) {
        Coordinate size = anchor.subtract(update);
        Coordinate topLeft = new Coordinate(anchor.x, anchor.y);
        
        if (size.x < 0){
            topLeft = anchor.add(new Coordinate(size.x, 0));
            size = new Coordinate(-size.x, size.y);
        }
        if (size.y < 0){
            topLeft = anchor.add(new Coordinate(0, size.y));
            size = new Coordinate(size.x, -size.y);
        }
        
        rect.setX(topLeft.x);
        rect.setY(topLeft.y);
        rect.setWidth(size.x);
        rect.setHeight(size.y);
    }

    @Override
    public void reset() {
        rect = null;
    }

    public void draw(Pane p) {
        if (rect != null)
            if (!p.getChildren().contains(rect))
                p.getChildren().add(rect);
    }
    
    public Rectangle getRectangle(){
        return rect;
    }
}
