/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pcbdraw.data;

import java.io.IOException;
import java.util.ArrayList;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

/**
 *
 * @author Nick Berryman
 */
public class GCBFile {
    private TextFile file;
    private int boardWidth;
    private int boardHeight;
    private boolean forCarvey;

    public GCBFile(String fileName) throws IOException {
        file = new TextFile(fileName);
    }
    
    public void save(ArrayList<Line> paths, ArrayList<Circle> holes, int boardWidth, int boardHeight, int unitMult, boolean forCarvey) throws IOException{
        StringBuilder str = new StringBuilder();
        str.append(boardWidth).append('\n');
        str.append(boardHeight).append('\n');
        str.append(forCarvey).append('\n');
        str.append("Lines:").append('\n');
        for (Line l : paths){
            str.append(l.getStartX()/unitMult);
            str.append(",");
            str.append(l.getStartY()/unitMult);
            str.append(",");
            str.append(l.getEndX()/unitMult);
            str.append(",");
            str.append(l.getEndY()/unitMult).append('\n');
        }
        str.append("Circles:").append('\n');
        for (Circle c : holes){
            str.append(c.getCenterX()/unitMult);
            str.append(",");
            str.append(c.getCenterY()/unitMult).append('\n');
        }
        file.save(str.toString());
    }
    
    public void read(ArrayList<Line> paths, ArrayList<Circle> holes, int unitMult) throws IOException{
        file.openToRead();
        boolean doingHoles = false;
        String line;
        boardWidth = Integer.parseInt(file.read());
        boardHeight = Integer.parseInt(file.read());
        forCarvey = Boolean.parseBoolean(file.read());
        file.read();
        while ((line = file.read()) != null){
            line = line.trim();
            if (line.equals("Circles:")) doingHoles = true;
            else{
                if (doingHoles){
                    Circle circ = new Circle();
                        circ.setCenterX(Double.parseDouble(line.split(",")[0])*unitMult);
                        circ.setCenterY(Double.parseDouble(line.split(",")[1])*unitMult);
                        circ.setStroke(Color.RED);
                        circ.setStrokeWidth(2);
                        circ.setFill(Color.WHITE);
                        holes.add(circ);
                }
                else{
                    Line currentLine = new Line();
                    currentLine.setStrokeWidth(2);
                    currentLine.setStroke(Color.RED);
                    currentLine.setStartX(Double.parseDouble(line.split(",")[0])*unitMult);
                    currentLine.setStartY(Double.parseDouble(line.split(",")[1])*unitMult);
                    currentLine.setEndX(Double.parseDouble(line.split(",")[2])*unitMult);
                    currentLine.setEndY(Double.parseDouble(line.split(",")[3])*unitMult);
                    paths.add(currentLine);
                }
            }
        }
        file.closeToRead();
    }

    public void readProperties() throws IOException{
        file.openToRead();
        boardWidth = Integer.parseInt(file.read());
        boardHeight = Integer.parseInt(file.read());
        forCarvey = Boolean.parseBoolean(file.read());
        file.closeToRead();
    }
    
    public int getBoardWidth() {
        return boardWidth;
    }

    public int getBoardHeight() {
        return boardHeight;
    }
    
    public boolean getForCarvey() {
        return forCarvey;
    }
}
