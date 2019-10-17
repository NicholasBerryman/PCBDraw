/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pcbdraw.gui;

import java.util.ArrayList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;

/**
 *
 * @author Nick Berryman
 */
public class CanvasPane extends ScrollPane{
    private Pane p;
    private ArrayList<Line> paths = new ArrayList<>();
    private ArrayList<Circle> holes = new ArrayList<>();
    
    private ArrayList<Line> selectedPaths = new ArrayList<>();
    private ArrayList<Circle> selectedHoles = new ArrayList<>();
    
    private CanvasAction action = CanvasAction.DrawLine;
    private Line currentLine = null;
    private Rectangle currentRect = null;
    private boolean isDrawing = false;
    
    private double gridSize = 1;
    
    private int generalHeightMM;
    private int generalWidthMM;
    private int generalSquareSizeMM;
    private int generalZoom;
    private boolean generallyForCarvey;
    
    public CanvasPane(){
        p = new Pane();
        this.setContent(p);
        p.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
        
        EventHandler<MouseEvent> clickHandler = new EventHandler<MouseEvent>() { 
            @Override 
            public void handle(MouseEvent e) {
                int gridX = (int)(Math.round(e.getX()/gridSize) * gridSize);
                int gridY = (int)(Math.round(e.getY()/gridSize) * gridSize);
                switch (action) {
                    case DrawLine:
                        if (currentLine == null){
                            if (generallyForCarvey && inCarveyRange(gridX/generalZoom, generalHeightMM - gridY/generalZoom)) new Alert(Alert.AlertType.INFORMATION, "Can't place paths under Carvey's SmartClamp!").showAndWait();
                            else{
                                currentLine = new Line();
                                currentLine.setStrokeWidth(2);
                                currentLine.setStroke(Color.RED);
                                currentLine.setStartX(gridX);
                                currentLine.setStartY(gridY);
                                currentLine.setEndX(gridX);
                                currentLine.setEndY(gridY);
                                p.getChildren().add(currentLine);
                                isDrawing = true;
                            }
                        }
                        else{
                            currentLine.setEndX(gridX);
                            currentLine.setEndY(gridY);
                            if (inCarveyRange(gridX/generalZoom, generalHeightMM - gridY/generalZoom)) new Alert(Alert.AlertType.INFORMATION, "Can't place paths under Carvey's SmartClamp!").showAndWait();
                            else paths.add(currentLine);
                            currentLine = null;
                            isDrawing = false;
                            drawGrid(generalWidthMM, generalHeightMM, generalSquareSizeMM, generalZoom, generallyForCarvey);
                            for (Line l : paths){
                                p.getChildren().add(l);
                            }
                            for (Circle c : holes){
                                p.getChildren().add(c);
                            }
                        }   
                        break;
                    case DrawHole:
                        if (generallyForCarvey && inCarveyRange(gridX/generalZoom, generalHeightMM - gridY/generalZoom)) new Alert(Alert.AlertType.INFORMATION, "Can't place holes under Carvey's SmartClamp!").showAndWait();
                        else{
                            Circle circ = new Circle();
                            circ.setCenterX(gridX);
                            circ.setCenterY(gridY);
                            circ.setStroke(Color.RED);
                            circ.setStrokeWidth(2);
                            circ.setFill(Color.WHITE);
                            holes.add(circ);
                            p.getChildren().add(circ);
                            drawGrid(generalWidthMM, generalHeightMM, generalSquareSizeMM, generalZoom, generallyForCarvey);
                            for (Line l : paths){
                                p.getChildren().add(l);
                            }   for (Circle c : holes){
                                p.getChildren().add(c);
                            }  
                        }
                        break;
                    case Select:
                        if (currentRect == null){
                            currentRect = new Rectangle();
                            currentRect.setX(gridX);
                            currentRect.setY(gridY);
                            currentRect.setWidth(0);
                            currentRect.setHeight(0);
                            currentRect.setStroke(Color.LIGHTGREEN);
                            currentRect.setFill(Color.TRANSPARENT);
                            currentRect.setStrokeWidth(5);
                            p.getChildren().add(currentRect);
                            isDrawing = true;
                        }
                        else{
                            deselect();
                            for (Line l : paths){
                                if (checkLineSelection(currentRect, l)){
                                    l.setStroke(Color.BLUE);
                                    selectedPaths.add(l);
                                }
                            }
                            for (Circle c : holes){
                                if (checkCircleSelection(currentRect, c)){
                                    c.setStroke(Color.BLUE);
                                    selectedHoles.add(c);
                                }
                            }
                            p.getChildren().remove(currentRect);
                            currentRect = null;
                            isDrawing = false;
                        }
                        break;
                    case Move:
                        double distX = gridX-selectedTopLeftX();
                        double distY = gridY-selectedTopLeftY();
                        for (Line l : selectedPaths){
                            l.setStartX(distX+l.getStartX());
                            l.setStartY(distY+l.getStartY());
                            l.setEndX(distX+l.getEndX());
                            l.setEndY(distY+l.getEndY());
                        }
                        for (Circle c : selectedHoles){
                            c.setCenterX(distX+c.getCenterX());
                            c.setCenterY(distY+c.getCenterY());
                        }
                        if (!clampHappy()){
                            new Alert(Alert.AlertType.INFORMATION, "Can't move circuit under Carvey's SmartClamp!").showAndWait();
                            for (Line l : selectedPaths){
                                l.setStartX(-distX+l.getStartX());
                                l.setStartY(-distY+l.getStartY());
                                l.setEndX(-distX+l.getEndX());
                                l.setEndY(-distY+l.getEndY());
                            }
                            for (Circle c : selectedHoles){
                                c.setCenterX(-distX+c.getCenterX());
                                c.setCenterY(-distY+c.getCenterY());
                        }
                        }
                        break;
                    default:
                        break;
                }
            }
        };
        
        EventHandler<MouseEvent> moveHandler = new EventHandler<MouseEvent>() { 
            @Override 
            public void handle(MouseEvent e) { 
                if (action == CanvasAction.DrawLine){
                    if (isDrawing){
                        int gridX = (int)(Math.round(e.getX()/gridSize) * gridSize);
                        int gridY = (int)(Math.round(e.getY()/gridSize) * gridSize);
                        currentLine.setEndX(gridX);
                        currentLine.setEndY(gridY);
                    }
                }
                if (action == CanvasAction.Select){
                    if (isDrawing){
                        int gridX = (int)(Math.round(e.getX()/gridSize) * gridSize);
                        int gridY = (int)(Math.round(e.getY()/gridSize) * gridSize);
                        double width = gridX-currentRect.getX();
                        double height = gridY-currentRect.getY();
                        if (width > 0)
                            currentRect.setWidth(width);
                        else{
                            currentRect.setTranslateX(width);
                            currentRect.setWidth(-width);
                        }
                        
                        if (height > 0)
                            currentRect.setHeight(height);
                        else{
                            currentRect.setHeight(-height);
                            currentRect.setTranslateY(height);
                        }
                    }
                }
            } 
        };
        
        p.addEventHandler(MouseEvent.MOUSE_CLICKED, clickHandler);
        p.addEventHandler(MouseEvent.MOUSE_MOVED, moveHandler);
    }
    
    public void drawGrid(int widthMM, int heightMM, int squareSizeMM, int MULT, boolean forCarvey){
        this.p.getChildren().clear();
        
        generalWidthMM = widthMM;
        generalHeightMM = heightMM;
        generalSquareSizeMM = squareSizeMM;
        generalZoom = MULT;
        generallyForCarvey = forCarvey;
        
        int squareSizeU = squareSizeMM*MULT;
        int totalWidthU = widthMM*MULT;
        int totalHeightU = heightMM*MULT;
        
        this.gridSize = squareSizeU;
        
        for (int x = 0; x < totalWidthU; x+=squareSizeU){
            Line l = new Line(x, 0, x, totalHeightU);
            l.setStrokeWidth(0.5);
            p.getChildren().add(l);
        }
        for (int y = 0; y < totalHeightU; y+=squareSizeU){
            Line l = new Line(0, y, totalWidthU, y);
            l.setStrokeWidth(0.5);
            p.getChildren().add(l);
        }
        
        if (forCarvey){
            double height = Math.min(3.25*25.4*MULT, heightMM*MULT);
            double width  = Math.min(0.75*25.4*MULT, widthMM*MULT);
            Rectangle clampV = new Rectangle(0, heightMM*MULT-height, width, height);
            p.getChildren().add(clampV);
            height = Math.min(0.75*25.4*MULT, heightMM*MULT);
            width  = Math.min(3.25*25.4*MULT, widthMM*MULT);
            Rectangle clampH = new Rectangle(0, heightMM*MULT-height, width, height);
            p.getChildren().add(clampH);
        }
        
        for (Circle c : holes){
            c.setRadius(gridSize/4.0);
        }
    }
    
    public void redraw(){
        this.drawGrid(generalWidthMM, generalHeightMM, generalSquareSizeMM, generalZoom, generallyForCarvey);
        for (Line l : paths){
            p.getChildren().add(l);
        }
        for (Circle c : holes){
            p.getChildren().add(c);
        }
    }
    
    public void zoom(int MULT){
        for (Line l : paths){
            l.setStartX(l.getStartX()/generalZoom);
            l.setStartY(l.getStartY()/generalZoom);
            l.setEndX(l.getEndX()/generalZoom);
            l.setEndY(l.getEndY()/generalZoom);
        }
        for (Circle c : holes){
            c.setCenterX(c.getCenterX()/generalZoom);
            c.setCenterY(c.getCenterY()/generalZoom);
        }
        
        this.drawGrid(generalWidthMM, generalHeightMM, generalSquareSizeMM, MULT, generallyForCarvey);
        
        for (Line l : paths){
            l.setStartX(l.getStartX()*MULT);
            l.setStartY(l.getStartY()*MULT);
            l.setEndX(l.getEndX()*MULT);
            l.setEndY(l.getEndY()*MULT);
            p.getChildren().add(l);
        }
        for (Circle c : holes){
            c.setCenterX(c.getCenterX()*generalZoom);
            c.setCenterY(c.getCenterY()*generalZoom);
            c.setRadius(gridSize/4.0);
            p.getChildren().add(c);
        }
    }
    
    
    public void updateSquareSize(int squareSize){
        this.drawGrid(generalWidthMM, generalHeightMM, squareSize, generalZoom, generallyForCarvey);
        
        for (Line l : paths){
            p.getChildren().add(l);
        }
        for (Circle c : holes){
            p.getChildren().add(c);
        }
    }
    
    public void updateWidth(int width){
        ArrayList<Line> toRemoveL = new ArrayList<>();
        for (Line l : paths){
            if (l.getStartX()/generalZoom > width || l.getEndX()/generalZoom > width) toRemoveL.add(l);
        }
        paths.removeAll(toRemoveL);
        
        ArrayList<Circle> toRemoveC = new ArrayList<>();
        for (Circle c : holes){
            if (c.getBoundsInParent().getMaxX()/generalZoom > width) toRemoveC.add(c);
        }
        holes.removeAll(toRemoveC);
        clearCarveybles();
        this.drawGrid(width, generalHeightMM, generalSquareSizeMM, generalZoom, generallyForCarvey);
        this.redraw();
    }
    
    public void updateHeight(int height){
        ArrayList<Line> toRemoveL = new ArrayList<>();
        for (Line l : paths){
            if (l.getStartY()/generalZoom > height || l.getEndY()/generalZoom > height) toRemoveL.add(l);
        }
        paths.removeAll(toRemoveL);
        
        ArrayList<Circle> toRemoveC = new ArrayList<>();
        for (Circle c : holes){
            if (c.getBoundsInParent().getMaxY()/generalZoom > height) toRemoveC.add(c);
        }
        holes.removeAll(toRemoveC);
        clearCarveybles();
        this.drawGrid(generalWidthMM, height, generalSquareSizeMM, generalZoom, generallyForCarvey);
        this.redraw();
    }
    
    public void updateForCarvey(boolean forCarvey){
        generallyForCarvey = true;
        clearCarveybles();
        this.drawGrid(generalWidthMM, generalHeightMM, generalSquareSizeMM, generalZoom, forCarvey);
        this.redraw();
    }
    
    private void clearCarveybles(){
        ArrayList<Line> toRemoveL = new ArrayList<>();
        ArrayList<Circle> toRemoveC = new ArrayList<>();
        if (generallyForCarvey){
            for (Line l : paths){
                double adjustedX1 = l.getStartX()/generalZoom;
                double adjustedY1 = generalHeightMM - l.getStartY()/generalZoom;
                double adjustedX2 = l.getEndX()/generalZoom;
                double adjustedY2 = generalHeightMM - l.getEndY()/generalZoom;
                if (inCarveyRange(adjustedX1, adjustedY1) || inCarveyRange(adjustedX2, adjustedY2)) 
                    toRemoveL.add(l);
            }
            paths.removeAll(toRemoveL);
            for (Circle c : holes){
                double adjustedX = c.getCenterX()/generalZoom;
                double adjustedY = generalHeightMM - c.getCenterY()/generalZoom;
                if (inCarveyRange(adjustedX, adjustedY))
                    toRemoveC.add(c);
            }
            holes.removeAll(toRemoveC);
        }
    }
    
    private boolean clampHappy(){
        if (!generallyForCarvey) return true;
        for (Line l : paths){
                double adjustedX1 = l.getStartX()/generalZoom;
                double adjustedY1 = generalHeightMM - l.getStartY()/generalZoom;
                double adjustedX2 = l.getEndX()/generalZoom;
                double adjustedY2 = generalHeightMM - l.getEndY()/generalZoom;
                if (inCarveyRange(adjustedX1, adjustedY1) || inCarveyRange(adjustedX2, adjustedY2)) 
                    return false;
            }
            
            for (Circle c : holes){
                double adjustedX = c.getCenterX()/generalZoom;
                double adjustedY = generalHeightMM - c.getCenterY()/generalZoom;
                if (inCarveyRange(adjustedX, adjustedY))
                    return false;
            }
            return true;
    }
    
    private boolean inCarveyRange(double adjustedX, double adjustedY){
        boolean inRange = false;
        if (adjustedX < 0.75*25.4 && adjustedY < 3.25*25.4) inRange = true;
        if (adjustedX < 3.25*25.4 && adjustedY < 0.75*25.4) inRange = true;
        
        return inRange;
    }
    
    public void setAction(CanvasAction action){
        p.getChildren().remove(currentLine);
        currentLine = null;
        isDrawing = false;
        this.action = action;
    }
    
    private boolean checkLineSelection(Rectangle rect, Line l){
        boolean toSelect = false;
        if ((l.getStartX() <= rect.getX()+rect.getWidth()+rect.getTranslateX() && l.getStartX() >= rect.getX()+rect.getTranslateX()) && (l.getStartY() <= rect.getY()+rect.getHeight()+rect.getTranslateY() && l.getStartY() >= rect.getY()+rect.getTranslateY()))
            toSelect = true;
        if ((l.getEndX() <= rect.getX()+rect.getWidth()+rect.getTranslateX() && l.getEndX() >= rect.getX()+rect.getTranslateX()) && (l.getEndY() <= rect.getY()+rect.getHeight()+rect.getTranslateY() && l.getEndY() >= rect.getY()+rect.getTranslateY()))
            toSelect = true;
        
        
        return toSelect;
    }
    
    private boolean checkCircleSelection(Rectangle rect, Circle c){
        return 
            (c.getCenterX()<= rect.getX()+rect.getWidth()+rect.getTranslateX() && c.getCenterX() >= rect.getX()+rect.getTranslateX()) && 
            (c.getCenterY() <= rect.getY()+rect.getHeight()+rect.getTranslateY() && c.getCenterY()>= rect.getY()+rect.getTranslateY());
    }
    
    private double selectedTopLeftX(){
        double x = Integer.MAX_VALUE;
        for (Line l : selectedPaths){
            x = Math.min(x, l.getStartX());
            x = Math.min(x, l.getEndX());
        }
        for (Circle c : selectedHoles){
            x = Math.min(x, c.getCenterX());
        }
        return x;
    }
    private double selectedTopLeftY(){
        double y = Integer.MAX_VALUE;
        for (Line l : selectedPaths){
            y = Math.min(y, l.getStartY());
            y = Math.min(y, l.getEndY());
        }
        for (Circle c : selectedHoles){
            y = Math.min(y, c.getCenterY());
        }
        return y;
    }
    
    public void deselect(){
        for (Line l : selectedPaths){
            l.setStroke(Color.RED);
        }
        for (Circle c : selectedHoles){
            c.setStroke(Color.RED);
        }
        selectedPaths.clear();
        selectedHoles.clear();
    }
    
    public void deleteSelected(){
        paths.removeAll(selectedPaths);
        holes.removeAll(selectedHoles);
        deselect();
        this.drawGrid(generalWidthMM, generalHeightMM, generalSquareSizeMM, generalZoom, generallyForCarvey);
        for (Line l : paths){
            p.getChildren().add(l);
        }
        for (Circle c : holes){
            p.getChildren().add(c);
        }
    }
    

    public ArrayList<Line> getPaths() {
        return paths;
    }

    public ArrayList<Circle> getHoles() {
        return holes;
    }

    public int getGeneralHeightMM() {
        return generalHeightMM;
    }

    public int getGeneralWidthMM() {
        return generalWidthMM;
    }

    public int getGeneralSquareSizeMM() {
        return generalSquareSizeMM;
    }

    public int getGeneralZoom() {
        return generalZoom;
    }

    public boolean isGenerallyForCarvey() {
        return generallyForCarvey;
    }
    
    public enum CanvasAction{
        DrawLine,
        DrawHole,
        Select,
        Move
    }
}
