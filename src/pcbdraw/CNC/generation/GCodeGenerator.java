/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pcbdraw.CNC.generation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import pcbdraw.CNC.representations.CNCRepr;
import pcbdraw.circuit.Coordinate;
import pcbdraw.circuit.traces.HoleTrace;
import pcbdraw.circuit.PCB;
import pcbdraw.circuit.traces.PathTrace;
import pcbdraw.gui.progress.Progressible;

/**
 *
 * @author Nick Berryman
 */
public class GCodeGenerator extends Progressible{
    private final CNCRepr cnc;
    private final PCB pcb;
    private final double pathWidthMM;
    private final int GCodeSmoothFactor = 1;
    private final double holeRatio = 1.25;
    private final double inverseResolution = 15;
    
    public GCodeGenerator(CNCRepr cnc, PCB pcb, double pathWidthMM){
        this.cnc = cnc;
        this.pcb = pcb;
        this.pathWidthMM = pathWidthMM;
    }
    
    public void compileAndSave() throws IOException, UncarveybleException{
        cnc.reset();
        Double[][] pathMask = this.createPathMaskMinimal();
        if (this.pcb.isCarvey()) if(!checkCarveyble(pathMask)) throw new UncarveybleException();
        this.separateHoleOverlap(pathMask);
            //this.printPathMask(pathMask);
        int[][] edgeMask = this.edgeFilter(pathMask);
            //this.printIntMask(edgeMask);
        this.contourPathGCode(edgeMask, pathMask);
        this.holeGCode();
        cnc.finish();
        cnc.save();
    }
    
    private Double[][] createPathMaskMinimal(){
        this.setMaxProgress(100);
        this.setProgress(0);
        Double[][] pathMask = new Double[(int)(pcb.getSize().x*inverseResolution)][(int)(pcb.getSize().y*inverseResolution)];
        this.setProgress(0);
        this.setMaxProgress(pathMask.length);
        
        Coordinate bottomLeft = new Coordinate(Double.MAX_VALUE, Double.MAX_VALUE);
        Coordinate topRight   = new Coordinate(Double.MIN_VALUE, Double.MIN_VALUE);
        
        for (PathTrace p : pcb.getPathTraces()){
            if (p.getBottomLeftBound().x < bottomLeft.x) bottomLeft = new Coordinate(p.getBottomLeftBound().x, bottomLeft.y);
            if (p.getBottomLeftBound().y < bottomLeft.y) bottomLeft = new Coordinate(bottomLeft.x, p.getBottomLeftBound().y);
            if (p.getTopRightBound().x >  topRight.x) topRight = new Coordinate(p.getTopRightBound().x, topRight.y);
            if (p.getTopRightBound().y >  topRight.y) topRight = new Coordinate(topRight.x, p.getBottomLeftBound().y);
        }
        for (HoleTrace h : pcb.getHoleTraces()){
            if (h.getMajorCoord().x < bottomLeft.x) bottomLeft = new Coordinate(h.getMajorCoord().x, bottomLeft.y);
            if (h.getMajorCoord().y < bottomLeft.y) bottomLeft = new Coordinate(bottomLeft.x, h.getMajorCoord().y);
            if (h.getMajorCoord().x >  topRight.x) topRight = new Coordinate(h.getMajorCoord().x, topRight.y);
            if (h.getMajorCoord().y >  topRight.y) topRight = new Coordinate(topRight.x, h.getMajorCoord().y);
        }
        
        int startX = (int) (inverseResolution*(bottomLeft.x-(pathWidthMM*holeRatio*1.1)));
        int startY = (int) (inverseResolution*(bottomLeft.y-(pathWidthMM*holeRatio*1.1)));
        int endX   = (int) (inverseResolution*(topRight.x+(pathWidthMM*holeRatio*1.1)));
        int endY   = (int) (inverseResolution*(topRight.y+(pathWidthMM*holeRatio*1.1)));
        
        if (startX < 0) startX = 0;
        if (startY < 0) startY = 0;
        if (endX > pathMask.length) endX = pathMask.length;
        if (endY > pathMask[0].length) endY = pathMask[0].length;
        for (int x = startX; x <= endX; x++){
            for (int y = startY; y <= endY; y++){
                Coordinate point = new Coordinate(x/inverseResolution,y/inverseResolution);
                boolean hasPath = false;
                double slope = 0;
                
                for (PathTrace p : pcb.getPathTraces()){
                    boolean onLine = p.withinRange(point, pathWidthMM);
                    boolean atEnd  = p.inRangeOfEnd(point, pathWidthMM);
                    hasPath |= onLine;
                    if (onLine && !atEnd)     slope = p.getGradient();
                    else if (onLine && atEnd) slope = Double.MIN_VALUE;
                    if (hasPath) break;
                }
                for (HoleTrace h : pcb.getHoleTraces()){
                    boolean inCircle = h.withinRange(point, pathWidthMM*holeRatio);
                    hasPath |= inCircle;
                    if (inCircle) slope = Double.MIN_VALUE;
                    if (hasPath) break;
                }
                if (hasPath) pathMask[x][y] = slope;
                else pathMask[x][y] = null;
            }
            this.incrementProgress();
        }
        return pathMask;
    }
    
    private Double[][] createPathMask(){
        this.setMaxProgress(100);
        this.setProgress(0);
        Double[][] pathMask = new Double[(int)(pcb.getSize().x*inverseResolution)][(int)(pcb.getSize().y*inverseResolution)];
        this.setProgress(0);
        this.setMaxProgress(pathMask.length);
        
        for (int x = 0; x < pathMask.length; x++){
            for (int y = 0; y < pathMask[0].length; y++){
                Coordinate point = new Coordinate(x/inverseResolution,y/inverseResolution);
                boolean hasPath = false;
                double slope = 0;
                
                for (PathTrace p : pcb.getPathTraces()){
                    boolean onLine = p.withinRange(point, pathWidthMM);
                    boolean atEnd  = p.inRangeOfEnd(point, pathWidthMM);
                    hasPath |= onLine;
                    if (onLine && !atEnd)     slope = p.getGradient();
                    else if (onLine && atEnd) slope = Double.MIN_VALUE;
                    if (hasPath) break;
                }
                for (HoleTrace h : pcb.getHoleTraces()){
                    boolean inCircle = h.withinRange(point, pathWidthMM*holeRatio);
                    hasPath |= inCircle;
                    if (inCircle) slope = Double.MIN_VALUE;
                    if (hasPath) break;
                }
                if (hasPath) pathMask[x][y] = slope;
                else pathMask[x][y] = null;
            }
            this.incrementProgress();
        }
        return pathMask;
    }
    
    private boolean checkCarveyble(Double[][] pathMask){
        //vertical part
        for (int x = 0; x < 0.75*25.4*inverseResolution; x++){
            for (int y = 0; y < 3.25*25.4*inverseResolution; y++){
                if (pathMask[x][y] != null)
                    return false;
            }
        }
        //horizontal part
        for (int x = 0; x < 3.25*25.4*inverseResolution; x++){
            for (int y = 0; y < 0.75*25.4*inverseResolution; y++){
                if (pathMask[x][y] != null){
                    return false;
                }
            }
        }
        return true;
    }
    
    private void separateHoleOverlap(Double[][] pathMask){
        for (int i = 0; i < pcb.getHoleTraces().size(); i++){
            ArrayList<HoleTrace> overlaps = new ArrayList<>();
            HoleTrace h = pcb.getHoleTraces().get(i);
            for (int j = i+1; j < pcb.getHoleTraces().size(); j++){
                HoleTrace h2 = pcb.getHoleTraces().get(j);
                double dist = h.distanceTo(h2.getMajorCoord());
                if (dist <= 2*holeRatio*pathWidthMM) overlaps.add(h2);
            }
            
            for (HoleTrace h2 : overlaps){
                if (h.getMajorCoord().x != h2.getMajorCoord().x && h.getMajorCoord().y != h2.getMajorCoord().y)
                    continue;
                for (int x = (int)((h.getMajorCoord().x-holeRatio*pathWidthMM)*inverseResolution); x <= (int)((h.getMajorCoord().x+holeRatio*pathWidthMM)*inverseResolution); x++){
                    for (int y = (int)((h.getMajorCoord().y-holeRatio*pathWidthMM)*inverseResolution); y <= (int)((h.getMajorCoord().y+holeRatio*pathWidthMM)*inverseResolution); y++){
                        Coordinate currPoint = new Coordinate(x/inverseResolution,y/inverseResolution);
                        double pointDist1 = currPoint.distanceTo(h.getMajorCoord());
                        double pointDist2 = currPoint.distanceTo(h2.getMajorCoord());
                        if (Math.abs(pointDist1-pointDist2) < 1/inverseResolution) pathMask[x][y] = null;
                    }
                }
            }
        }
    }
    
    private int[][] edgeFilter(Double[][] pathMask){
        int[][] edgeMask = new int[pathMask.length][pathMask[0].length];
        this.setProgress(0);
        this.setMaxProgress(pathMask.length);
        int replaceProg = 0;
        for (int x = 0; x < pathMask.length; x++){
            for (int y = 0; y < pathMask[0].length; y++){
                boolean isEdge;
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
                    if (isEdge) {
                        edgeMask[x][y] = 1;
                        this.incrementProgress();
                        replaceProg++;
                    }
                    else edgeMask[x][y] = 0;
                }
            }
            this.incrementProgress();
        }
        this.setMaxProgress(replaceProg/500);
        return edgeMask;
    }
    
    private void contourPathGCode(int[][] edgeMask, Double[][] pathMask){
        Coordinate start;
        Double lastSlope = null;
        int contProgress = 0;
        this.setProgress(0);
        this.cnc.initMachine();
        while (true){
            cnc.endCut();
            start = null;
            for (int x = 0; x < edgeMask.length && start == null; x++){
                for (int y = 0; y < edgeMask[0].length && start == null; y++){
                    if (edgeMask[x][y] == 1){
                        if (start == null){
                            start = new Coordinate(x, y);
                            break;
                        }
                    }
                }
            }

            if (start == null) break;
            cnc.move(new Coordinate(start.x/inverseResolution, start.y/inverseResolution));
            cnc.startCut();
            Coordinate current = new Coordinate(start.x, start.y);
            Coordinate lastCirc = null;
            int pathCount = 2;
            int smootherCount = 0;
            while (true){
                edgeMask[(int)current.x][(int)current.y] = pathCount;
                pathCount++;
                if (pathCount % 500 == 0){
                    contProgress++;
                    this.incrementProgress();
                }
                
                if (new Double(Double.MIN_VALUE).equals(pathMask[(int)current.x][(int)current.y])){
                    if (smootherCount % GCodeSmoothFactor == 0) cnc.move(new Coordinate(current.x/inverseResolution, current.y/inverseResolution));
                    lastCirc = new Coordinate(current.x, current.y);
                    lastSlope = Double.MIN_VALUE;
                    smootherCount++;
                }
                else if (lastSlope == null || !Objects.equals(lastSlope, pathMask[(int)current.x][(int)current.y])){
                    if (lastSlope != null)
                        if (lastSlope == Double.MIN_VALUE && lastCirc != null) cnc.move(new Coordinate(lastCirc.x/inverseResolution, lastCirc.y/inverseResolution));
                    cnc.move(new Coordinate(current.x/inverseResolution, current.y/inverseResolution));
                    lastSlope = pathMask[(int)current.x][(int)current.y];
                    smootherCount = 0;
                }
                else smootherCount = 0;

                if (current.y > 0)                 if (edgeMask[(int)current.x][(int)current.y-1] == 1){
                    current = current.subtract(new Coordinate(0,1));
                    continue;
                }
                if (current.x < edgeMask.length)   if (edgeMask[(int)current.x+1][(int)current.y] == 1){
                    current = current.add(new Coordinate(1,0));
                    continue;
                }
                if (current.y < edgeMask[0].length)if (edgeMask[(int)current.x][(int)current.y+1] == 1){
                    current = current.add(new Coordinate(0,1));
                    continue;
                }
                if (current.x > 0)                 if (edgeMask[(int)current.x-1][(int)current.y] == 1){
                    current = current.subtract(new Coordinate(1,0));
                    continue;
                }
                if (current.y > 0 && current.x > 0)                                if (edgeMask[(int)current.x-1][(int)current.y-1] == 1){
                    current = current.subtract(new Coordinate(1,1));
                    continue;
                }
                if (current.y > 0 && current.x < edgeMask.length)                  if (edgeMask[(int)current.x+1][(int)current.y-1] == 1){
                    current = current.add(new Coordinate(1,-1));
                    continue;
                }
                if (current.y < edgeMask[0].length && current.x < edgeMask.length) if (edgeMask[(int)current.x+1][(int)current.y+1] == 1){
                    current = current.add(new Coordinate(1,1));
                    continue;
                }
                if (current.y < edgeMask[0].length && current.x > 0)               if (edgeMask[(int)current.x-1][(int)current.y+1] == 1){
                    current = current.add(new Coordinate(-1,1));
                    continue;
                }
                break;
            }
            cnc.move(new Coordinate(start.x/inverseResolution, start.y/inverseResolution));
        }
        this.setMaxProgress(contProgress);
    }
    
    private void holeGCode(){
        this.pcb.getHoleTraces().sort((HoleTrace t1, HoleTrace t2) ->{
            if (t1.getMajorCoord().x < t2.getMajorCoord().x) return -1;
            if (t1.getMajorCoord() == t2.getMajorCoord())
                if (t1.getMajorCoord().y < t2.getMajorCoord().y) return -1;
            return 1;
        });
        for (HoleTrace h : this.pcb.getHoleTraces()) cnc.cutHole(h.getMajorCoord());
    }
    
    
    private void printPathMask(Double[][] pathMask){
        for (int y = pathMask[0].length-1; y>=0;y--){
            for (int x = 0; x<pathMask.length; x++){
                if (pathMask[x][y] != null)System.out.print("1");
                else System.out.print(" ");
            }
            System.out.println();
        }
    }
    
    private void printIntMask(int[][] pathMask){
        for (int y = pathMask[0].length-1; y>=0; y--){
            for (int x = 0; x<pathMask.length;x++){
                String intVal = Integer.toString(pathMask[x][y]);
                char intEnd = intVal.charAt(intVal.length()-1);
                if (pathMask[x][y] > 0)System.out.print(intEnd);
                else System.out.print(" ");
            }
            System.out.println();
        }
    }
    
    public class UncarveybleException extends RuntimeException{}
}
