/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pcbdraw.gui.workspace;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;
import pcbdraw.circuit.CircuitTrace;

/**
 *
 * @author Nick Berryman
 */
public abstract class GUITrace {
    private boolean isSelected = false;
    private Shape traceShape;
    private CircuitTrace trace;
    
    public void init(Shape traceShape, CircuitTrace trace){
        this.traceShape = traceShape;
        this.trace = trace;
    }
    
    public void select(){
        this.isSelected = true;
        this.traceShape.setStroke(Color.BLUE);
    }
    
    public void deselect(){
        this.isSelected = false;
        this.traceShape.setStroke(Color.RED);
    }
    
    public boolean isSelected(){
        return this.isSelected;
    }
    
    public void draw(Pane p){
        if (!p.getChildren().contains(traceShape))p.getChildren().add(traceShape);
    }
    
    public CircuitTrace getTrace(){
        return this.trace;
    }
}
