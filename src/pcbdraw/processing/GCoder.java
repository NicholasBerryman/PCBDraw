/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pcbdraw.processing;

import java.util.ArrayList;
import java.util.Objects;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

/**
 *
 * @author Nick Berryman
 */
public class GCoder {
    private  StringBuilder gcode = new StringBuilder();
    private double zDown;
    private double drillDown;
    private double zUp;
    private double feedRate;
    private double pathWidth;
    
    private final double inverseResolution = 10;
    private int gcodeSmoothFactor; //The number of coordinates per gcode instruction
    //TODO add smoothing as option rather than forced
    private int maxProgress = 1;
    private int currentProgress = 0;
    private ProgressListener progListen;
    
    public GCoder(double zDown, double drillDown, double zUp, double feedRate, double pathWidth) {
        this.zDown = zDown;
        this.drillDown = drillDown;
        this.zUp = zUp;
        this.feedRate = feedRate;
        this.pathWidth = pathWidth;
        
        gcodeSmoothFactor = (int)(pathWidth*2);//This seems ok?
        if (gcodeSmoothFactor < 1) gcodeSmoothFactor = 1;
    }
    
    public String compile(ArrayList<Line> path, ArrayList<Circle> hole, double unitMult, double boardWidthMM, double boardHeightMM, boolean forCarvey) throws Exception{
        currentProgress = 0;
        maxProgress = path.size() + hole.size();
        gcode = new StringBuilder();
        ArrayList<Line> paths = new ArrayList<>();
        ArrayList<Circle> holes = new ArrayList<>();
        for (Line l : path){
            currentProgress++;
            updateProgress(currentProgress);
            Line l2 = new Line();
            if (l.getStartX() > l.getEndX() && l.getStartY() < l.getEndY()){
                l2.setStartX(l.getEndX());
                l2.setEndX(l.getStartX());
                l2.setStartY(l.getEndY());
                l2.setEndY(l.getStartY());
                l.setStartX(l2.getStartX());
                l.setEndX(l2.getEndX());
                l.setStartY(l2.getStartY());
                l.setEndY(l2.getEndY());
            }
            if (l.getStartX() < l.getEndX() || (!(l.getStartX() < l.getEndX()) && l.getStartY() < l.getEndY())){
                l2.setStartX(l.getStartX()/unitMult);
                l2.setEndX(l.getEndX()/unitMult);
                l2.setStartY(l.getStartY()/unitMult);
                l2.setEndY(l.getEndY()/unitMult);
                paths.add(l2);
            }
            else if (l.getStartX() > l.getEndX() || !(l.getStartX() > l.getEndX()) && l.getStartY() > l.getEndY()){
                l2.setEndX(l.getStartX()/unitMult);
                l2.setStartX(l.getEndX()/unitMult);
                l2.setEndY(l.getStartY()/unitMult);
                l2.setStartY(l.getEndY()/unitMult);
                paths.add(l2);
            }
        }
        for (Circle c : hole){
            currentProgress++;
            updateProgress(currentProgress);
            Circle c2 = new Circle();
            c2.setCenterX(c.getCenterX()/unitMult);
            c2.setCenterY(c.getCenterY()/unitMult);
            holes.add(c2);
        }
        
        gcode.append("G90G94\n");
        gcode.append("G17\n");
        gcode.append("G21\n");
        gcode.append("M9\n");
        
        Double[][] pathMask = createPathMask(paths, holes, boardWidthMM, boardHeightMM);
        if (forCarvey){
            if (!validateCarveyble(pathMask)) throw new Exception("Something is too close to the Carvey's SmartClamp!");
        }
        //printPathMask(pathMask);
        int[][] intMask = edgeFilter(pathMask);
        //printIntMask(intMask);
        contourPathGCode(intMask, pathMask, gcode);
        //printIntMask(intMask);
        holeGCode(gcode, holes, boardHeightMM);
        
        gcode.append("G0Z15\n");
        return gcode.toString();
    }
    
    private boolean validateCarveyble(Double[][] pathMask){
        //vertical part
        for (int x = 0; x < 0.75*25.4*inverseResolution; x++){
            for (int y = 1; y < 3.25*25.4*inverseResolution; y++){
                if (pathMask[x][pathMask[0].length-y] != null)
                    return false;
            }
        }
        //horizontal part
        for (int x = 0; x < 3.25*25.4*inverseResolution; x++){
            for (int y = 1; y < 0.75*25.4*inverseResolution; y++){
                if (pathMask[x][pathMask[0].length-y] != null){
                    return false;
                }
            }
        }
        return true;
    }
    
    public Double[][] createPathMask(ArrayList<Line> pathsAdjusted, ArrayList<Circle> holesAdjusted, double boardWidthMM, double boardHeightMM){
        Double[][] pathMask = new Double[(int)(boardWidthMM*inverseResolution)][(int)(boardHeightMM*inverseResolution)];
        currentProgress = 0;
        maxProgress = pathMask.length * pathMask[0].length;
        
        for (int x = 0; x < boardWidthMM*inverseResolution; x++){
            for (int y = 0; y < boardHeightMM*inverseResolution; y++){
                boolean hasPath = false;
                double slope = 0;
                for (Line l : pathsAdjusted){
                    hasPath |= pointOnLine(x/inverseResolution,y/inverseResolution,l) > 0;
                    if (pointOnLine(x/inverseResolution,y/inverseResolution,l) == 1){
                        if (l.getEndX() != l.getStartX())
                            slope = (l.getEndY()-l.getStartY())/(l.getEndX()-l.getStartX());
                        else slope = Double.POSITIVE_INFINITY;
                    }
                    else if (pointOnLine(x/inverseResolution,y/inverseResolution,l) == 2)slope = Double.MIN_VALUE;
                }
                for (Circle c : holesAdjusted){
                    hasPath |= pointInCircle(x/inverseResolution,y/inverseResolution,c);
                    if (pointInCircle(x/inverseResolution,y/inverseResolution,c))
                        slope = Double.MIN_VALUE;
                }
                if (hasPath) pathMask[x][y] = slope;
                else pathMask[x][y] = null;
                currentProgress++;
            }
            updateProgress(currentProgress);
        }
        return pathMask;
    }
    
    private int pointOnLine(double xp, double yp, Line l){
        boolean intersectsSegment = false;
        double distToIntersect = 0;
        double intersectX = 0;
        double intersectY = 0;
        double distToPoint;
        if (l.getEndX() != l.getStartX() && l.getEndY() == l.getStartY()){
            intersectY = l.getStartY();
            intersectX = xp;
            intersectsSegment = intersectX<=l.getEndX() && intersectX>=l.getStartX();
            distToIntersect = Math.abs(intersectY-yp);
        }
        else if (l.getEndX() == l.getStartX() && l.getEndY() != l.getStartY()){
            intersectX = l.getStartX();
            intersectY = yp;
            intersectsSegment = intersectY<=l.getEndY() && intersectY>=l.getStartY();
            distToIntersect = Math.abs(intersectX-xp);
        }
        else if (l.getEndX() != l.getStartX() && l.getEndY() != l.getStartY()){
            double slope = (l.getEndY()-l.getStartY())/(l.getEndX()-l.getStartX());
            double offset = l.getStartY()-slope*l.getStartX(); //I'm pretty sure this is right

            double perpSlope = -1.0/slope;
            double perpOffset = yp-perpSlope*xp;

            //y = perpSlope*x+perpOffset & y = slope*x+offset
            //slope*x+offset = perpSlope*x+perpOffset
            //(slope-perpSlope)*x+offset = perpOffset
            //x=(perpOffset-offset)/(slope-perpSlope)
            //y = slope*x+offset
            intersectX = (perpOffset-offset)/(slope-perpSlope);
            intersectY = slope*intersectX+offset;
            intersectsSegment = intersectX<=l.getEndX() && intersectX>=l.getStartX();
            distToIntersect = Math.sqrt(Math.pow(intersectX-xp,2)+Math.pow(intersectY-yp,2));
        }
        distToPoint = Math.min(Math.sqrt(Math.pow(l.getStartX()-xp,2)+Math.pow(l.getStartY()-yp,2)),Math.sqrt(Math.pow(l.getEndX()-xp,2)+Math.pow(l.getEndY()-yp,2)));
        
        if ((distToPoint < pathWidth/2.0))
            return 2;
        if (intersectsSegment && distToIntersect <= pathWidth/2.0)
            return 1;
        return 0;
    }
    
    private boolean pointInCircle(double xp, double yp, Circle c){
        double distToCircle = Math.sqrt(Math.pow(c.getCenterX()-xp,2)+Math.pow(c.getCenterY()-yp,2));
        return distToCircle <= 1.3*pathWidth; 
    }
    
    private void printPathMask(Double[][] pathMask){
        for (int y = 0; y<pathMask[0].length; y++){
            for (int x = 0; x<pathMask.length;x++){
                if (pathMask[x][y] != null)System.out.print("1");
                else System.out.print(" ");
            }
            System.out.println();
        }
    }
    private void printIntMask(int[][] pathMask){
        for (int y = 0; y<pathMask[0].length; y++){
            for (int x = 0; x<pathMask.length;x++){
                String intVal = Integer.toString(pathMask[x][y]);
                char intEnd = intVal.charAt(intVal.length()-1);
                if (pathMask[x][y] > 0)System.out.print(intEnd);
                else System.out.print(" ");
            }
            System.out.println();
        }
    }
    
    private int[][] edgeFilter(Double[][] pathMask){
        int[][] edgeMask = new int[pathMask.length][pathMask[0].length];
        currentProgress = 0;
        maxProgress = pathMask.length * pathMask[0].length;
        int replaceProg = 0;
        for (int x = 0; x < pathMask.length; x++){
            for (int y = 0; y < pathMask[0].length; y++){
                boolean isEdge = true;
                if (pathMask[x][y] != null){
                    int neighbourCount = 0;
                    for (int xi = -1; xi <= 1; xi++){
                        for (int yi = -1; yi <= 1; yi++){
                            if (Math.abs(xi) == Math.abs(yi)) continue;
                            if (x+xi >= 0 && y+yi >= 0){
                                if (pathMask[x+xi][y+yi] != null)
                                    neighbourCount++;
                            }
                        }
                    }
                    isEdge = (neighbourCount < 4);
                    if (isEdge){
                        edgeMask[x][y] = 1;
                        currentProgress++;
                        updateProgress(currentProgress);
                        replaceProg++;
                    }
                    else edgeMask[x][y] = 0;
                }
            }
        }
        currentProgress = 0;
        maxProgress = replaceProg;
        return edgeMask;
    }
    
    private void contourPathFilter(int[][] edgeMask){
        Integer startX;
        Integer startY;
        while (true){
            startX = null;
            startY = null;
            for (int x = 0; x < edgeMask.length && startX == null; x++){
                for (int y = 0; y < edgeMask[0].length && startX == null; y++){
                    if (edgeMask[x][y] == 1){
                        if (startX == null){
                            startX = x;
                            startY = y;
                            break;
                        }
                    }
                }
            }
            
            if (startX == null) break;
            
            int currX = startX;
            int currY = startY;
            int pathCount = 2;
            /*for (int xi = -1; xi <= 1; xi++){
                for (int yi = -1; yi <= 1; yi++){
                    if (currX+xi >= 0 && currY+yi >= 0 && (xi!=0 || yi!=0)){
                        if (edgeMask[currX+xi][currY+yi] > 0){
                            if (edgeMask[currX+xi][currY+yi] == 1){
                                edgeMask[currX+xi][currY+yi] = pathCount;
                                pathCount++;
                                currX += xi;
                                currY += yi;
                                xi = -1;
                                yi = -2;
                            }
                        }
                    }
                }
            }*/

            while (true){
                edgeMask[currX][currY] = pathCount;
                pathCount++;

                if (currY > 0)                 if (edgeMask[currX][currY-1] == 1){
                    currY -= 1;
                    continue;
                }
                if (currX < edgeMask.length)   if (edgeMask[currX+1][currY] == 1){
                    currX += 1;
                    continue;
                }
                if (currY < edgeMask[0].length)if (edgeMask[currX][currY+1] == 1){
                    currY += 1;
                    continue;
                }
                if (currX > 0)                 if (edgeMask[currX-1][currY] == 1){
                    currX -= 1;
                    continue;
                }
                if (currY > 0 && currX > 0)                                if (edgeMask[currX-1][currY-1] == 1){
                    currX -= 1;
                    currY -= 1;
                    continue;
                }
                if (currY > 0 && currX < edgeMask.length)                  if (edgeMask[currX+1][currY-1] == 1){
                    currX += 1;
                    currY -= 1;
                    continue;
                }
                if (currY < edgeMask[0].length && currX < edgeMask.length) if (edgeMask[currX+1][currY+1] == 1){
                    currX += 1;
                    currY += 1;
                    continue;
                }
                if (currY < edgeMask[0].length && currX > 0)               if (edgeMask[currX-1][currY+1] == 1){
                    currX -= 1;
                    currY += 1;
                    continue;
                }
                break;
            }
        }
    }
    
    public void setProgressListener(ProgressListener newListen){
        this.progListen = newListen;
    }
    
    private void updateProgress(int newProgress){
        progListen.update(newProgress);
    }
    
    public int getMaxProgress(){
        return maxProgress;
    }
    
    /**
     * G0 - Fast X?Y?Z?
     * G1 - Slow X?Y?Z?F?
     * 
    **/
    private void contourPathGCode(int[][] edgeMask, Double[][] pathMask, StringBuilder gcode){
        Integer startX;
        Integer startY;
        int reversedY;
        currentProgress = 0;
        Double lastSlope = null;
        //Double lastUsedSlope = null;
        
        while (true){
            gcode.append("G0Z").append(Double.toString(zUp)).append("\n");
            startX = null;
            startY = null;
            for (int x = 0; x < edgeMask.length && startX == null; x++){
                for (int y = 0; y < edgeMask[0].length && startX == null; y++){
                    if (edgeMask[x][y] == 1){
                        if (startX == null){
                            startX = x;
                            startY = y;
                            break;
                        }
                    }
                }
            }

            if (startX == null) break;
            reversedY = edgeMask[0].length - startY;
            gcode.append("X").append(Double.toString(startX/inverseResolution)).append("Y").append(Double.toString(reversedY/inverseResolution)).append("\n");
            gcode.append("G1").append("X").append(Double.toString(startX/inverseResolution)).append("Y").append(Double.toString(reversedY/inverseResolution)).append("Z").append(Double.toString(zDown)).append("F").append(feedRate).append("S").append("10000").append("\n");
            int currX = startX;
            int currY = startY;
            int pathCount = 2;
            int smootherCount = 0;
            int lastCircX = 0;
            int lastCircY = 0;
            
            while (true){
                edgeMask[currX][currY] = pathCount;
                reversedY = edgeMask[0].length - currY;
                pathCount++;
                currentProgress++;
                updateProgress(currentProgress);
                
                if (pathMask[currX][currY] == Double.MIN_VALUE){
                    if (smootherCount % gcodeSmoothFactor == 0)
                        gcode.append("X").append(Double.toString(currX/inverseResolution)).append("Y").append(Double.toString(reversedY/inverseResolution)).append("\n");
                    lastCircX = currX;
                    lastCircY = reversedY;
                    lastSlope = Double.MIN_VALUE;
                    smootherCount++;
                }
                else if (lastSlope == null || !Objects.equals(lastSlope, pathMask[currX][currY])){
                    if (lastSlope != null)
                        if (lastSlope == Double.MIN_VALUE)
                            gcode.append("X").append(Double.toString(lastCircX/inverseResolution)).append("Y").append(Double.toString(lastCircY/inverseResolution)).append("\n");
                    gcode.append("X").append(Double.toString(currX/inverseResolution)).append("Y").append(Double.toString(reversedY/inverseResolution)).append("\n");
                    lastSlope = pathMask[currX][currY];
                    smootherCount = 0;
                }
                else smootherCount = 0;

                if (currY > 0)                 if (edgeMask[currX][currY-1] == 1){
                    currY -= 1;
                    continue;
                }
                if (currX < edgeMask.length)   if (edgeMask[currX+1][currY] == 1){
                    currX += 1;
                    continue;
                }
                if (currY < edgeMask[0].length)if (edgeMask[currX][currY+1] == 1){
                    currY += 1;
                    continue;
                }
                if (currX > 0)                 if (edgeMask[currX-1][currY] == 1){
                    currX -= 1;
                    continue;
                }
                if (currY > 0 && currX > 0)                                if (edgeMask[currX-1][currY-1] == 1){
                    currX -= 1;
                    currY -= 1;
                    continue;
                }
                if (currY > 0 && currX < edgeMask.length)                  if (edgeMask[currX+1][currY-1] == 1){
                    currX += 1;
                    currY -= 1;
                    continue;
                }
                if (currY < edgeMask[0].length && currX < edgeMask.length) if (edgeMask[currX+1][currY+1] == 1){
                    currX += 1;
                    currY += 1;
                    continue;
                }
                if (currY < edgeMask[0].length && currX > 0)               if (edgeMask[currX-1][currY+1] == 1){
                    currX -= 1;
                    currY += 1;
                    continue;
                }
                break;
            }
            reversedY = edgeMask[0].length - startY;
            gcode.append("X").append(Double.toString(startX/inverseResolution)).append("Y").append(Double.toString(reversedY/inverseResolution)).append("\n");
        }
    }
    
    private void holeGCode(StringBuilder gcode, ArrayList<Circle> holes, double boardHeightMM){
        for (Circle c : holes){
            gcode.append("G0Z").append(Double.toString(zUp)).append("\n");
            gcode.append("X").append(Double.toString(c.getCenterX())).append("Y").append(Double.toString(boardHeightMM - c.getCenterY())).append("\n");
            gcode.append("G1").append("X").append(Double.toString(c.getCenterX())).append("Y").append(Double.toString(boardHeightMM - c.getCenterY())).append("Z").append(Double.toString(drillDown)).append("F").append(feedRate).append("S").append("10000").append("\n");
        }
    }
    
    public interface ProgressListener{
        public void update(int newProgress);
    }
}
