/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pcbdraw.CNC;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import pcbdraw.CNC.representations.CNCRepr;
import pcbdraw.circuit.traces.CircuitTrace;
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
    private final double inverseResolution = 10;
    
    public GCodeGenerator(CNCRepr cnc, PCB pcb, double pathWidthMM){
        this.cnc = cnc;
        this.pcb = pcb;
        this.pathWidthMM = pathWidthMM;
    }
    
    public void compileAndSave() throws IOException, UncarveybleException{
        cnc.reset();
        Double[][] pathMask = this.createPathMask();
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
    
    private Double[][] createPathMask(){
        Double[][] pathMask = new Double[(int)(pcb.getSize().x*inverseResolution)][(int)(pcb.getSize().y*inverseResolution)];
        this.setProgress(0);
        this.setMaxProgress(pathMask.length);
        
        for (int x = 0; x < pathMask.length; x++){
            for (int y = 0; y < pathMask[0].length; y++){
                Coordinate point = new Coordinate(x/10.0,y/10.0);
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
                        double pointDist1 = Math.sqrt(Math.pow(h.getMajorCoord().x*inverseResolution-x,2)+Math.pow(h.getMajorCoord().y*inverseResolution-y,2));
                        double pointDist2 = Math.sqrt(Math.pow(x-h2.getMajorCoord().x*inverseResolution,2)+Math.pow(y-h2.getMajorCoord().y*inverseResolution,2));

                        if ((int)pointDist1 == (int)pointDist2) pathMask[x][y] = null;
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
            this.setMaxProgress(replaceProg);
        }
        return edgeMask;
    }
    
    private void contourPathGCode(int[][] edgeMask, Double[][] pathMask){
        Coordinate start;
        Double lastSlope = null;
        
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
                this.incrementProgress();
                
                if (pathMask[(int)current.x][(int)current.y] == null) System.out.println("a");
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
    }
    
    private void holeGCode(){
        this.pcb.getTraces().sort((CircuitTrace t1, CircuitTrace t2) ->{
            if (t1 instanceof HoleTrace && t2 instanceof HoleTrace){
                if (t1.getMajorCoord().x < t2.getMajorCoord().x) return -1;
                if (t1.getMajorCoord() == t2.getMajorCoord())
                    if (t1.getMajorCoord().y < t2.getMajorCoord().y) return -1;
                return 1;
            }
            else if (t1 instanceof HoleTrace && ! (t2 instanceof HoleTrace))
                return -1;
            return 0;
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
