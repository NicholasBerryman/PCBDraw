/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pcbdraw.circuit.traces;

import pcbdraw.circuit.Coordinate;

/**
 *
 * @author Nick Berryman
 */
public class PathTrace extends CircuitTrace{
    private final Line pathLine;

    public PathTrace(Coordinate startPoint, Coordinate endPoint) {
        this.pathLine = new Line(new Coordinate(startPoint.x, startPoint.y), new Coordinate(endPoint.x, endPoint.y));
    }
    
    public void setStart(Coordinate start){
        this.pathLine.startPoint = start;
    }
    
    public void setEnd(Coordinate end){
        this.pathLine.endPoint = end;
    }
    
    public double getGradient(){
        return this.pathLine.getGradient();
    }
    
    public Coordinate getStartPoint(){
        return this.pathLine.startPoint;
    }
    
    public Coordinate getEndPoint(){
        return this.pathLine.endPoint;
    }
    
    public Coordinate getBottomLeftBound(){
        return this.pathLine.getBottomLeftBound();
    }
    
    public Coordinate getTopRightBound(){
        return this.pathLine.getTopRightBound(); 
    }
    
    @Override
    public Coordinate getMajorCoord() {
        return new Coordinate(pathLine.getBottomLeftBound().x, pathLine.getTopRightBound().y);
    }

    @Override
    public boolean withinRange(Coordinate toCheck, double pathWidthMM) {
        return pathLine.distanceFrom(toCheck) <= pathWidthMM/2.0;
    }
    
    public boolean inRangeOfEnd(Coordinate toCheck, double pathWidthMM){
        Coordinate startDist = pathLine.startPoint.subtract(toCheck);
        Coordinate endDist   = pathLine.endPoint.subtract(toCheck);
        return 
                Math.sqrt(startDist.x*startDist.x+startDist.y*startDist.y) < pathWidthMM/2.0
                || Math.sqrt(endDist.x*endDist.x+endDist.y*endDist.y) < pathWidthMM/2.0;
    }
    
    //Note that it is an asymmetrical equals
    @Override
    public boolean equals(CircuitTrace c) {
        if (c instanceof PathTrace)
        {
            PathTrace p = (PathTrace) c;
            return (this.getGradient() == p.getGradient()
                    && this.pathLine.distanceFrom(p.pathLine.startPoint) == 0
                    && this.pathLine.distanceFrom(p.pathLine.endPoint) == 0);
        }
        return false;
    }

    @Override
    public void simplifyUsing(CircuitTrace c) {
        if (c instanceof PathTrace)
        {
            PathTrace p = (PathTrace) c;
            boolean parallel = (this.getGradient() == ((PathTrace) c).getGradient());
            boolean startOn = p.pathLine.distanceFrom(this.pathLine.startPoint) == 0;
            boolean endOn = p.pathLine.distanceFrom(this.pathLine.startPoint) == 0;
            
            if (parallel && startOn && !endOn){
                this.pathLine.startPoint = p.pathLine.endPoint;
            }
            else if (parallel && endOn && !startOn){
                this.pathLine.endPoint = p.pathLine.startPoint;
            }
        }
    }

    @Override
    public double distanceTo(Coordinate c) {
        return this.pathLine.distanceFrom(c);
    }

    @Override
    public void moveTo(Coordinate c) {
        Coordinate distStartTopLeft = this.getStartPoint().subtract(this.getMajorCoord());
        Coordinate distStartEnd = this.getEndPoint().subtract(this.getStartPoint());
        this.pathLine.startPoint = c.add(distStartTopLeft);
        this.pathLine.endPoint = this.pathLine.startPoint.add(distStartEnd);
    }

    @Override
    public CircuitTrace copy() {
        return new PathTrace(this.getStartPoint(), this.getEndPoint());
    }

    @Override
    public boolean inArea(Coordinate botLeft, Coordinate topRight) {
        return 
            (this.getStartPoint().x >= botLeft.x && this.getStartPoint().x <= topRight.x &&
             this.getStartPoint().y > botLeft.y  && this.getStartPoint().y <= topRight.y)||
            (this.getEndPoint().x >= botLeft.x   && this.getEndPoint().x   <= topRight.x &&
             this.getEndPoint().y > botLeft.y    && this.getEndPoint().y    <= topRight.y);
    }
    
    public boolean fullyInArea(Coordinate botLeft, Coordinate topRight){
        return 
        (this.getStartPoint().x >= botLeft.x && this.getStartPoint().x <= topRight.x &&
         this.getStartPoint().y > botLeft.y  && this.getStartPoint().y <= topRight.y)&&
        (this.getEndPoint().x >= botLeft.x   && this.getEndPoint().x   <= topRight.x &&
         this.getEndPoint().y > botLeft.y    && this.getEndPoint().y    <= topRight.y);
    }
    
    private class Line{
        private Coordinate startPoint;
        private Coordinate endPoint;

        public Line(Coordinate startPoint, Coordinate endPoint){
            if (startPoint.x <= endPoint.x){
                this.startPoint = startPoint;
                this.endPoint = endPoint;
            }
            else if (startPoint.x > endPoint.x){
                this.startPoint = endPoint;
                this.endPoint = startPoint;
            }
            //TODO maybe throw error on line is a point
        }
        
        public double getGradient(){
            double rise = this.endPoint.y - this.startPoint.y;
            double run = this.endPoint.x  - this.startPoint.x;
            
            if (run != 0) return rise/run;
            return Double.POSITIVE_INFINITY;
        }
        
        //See https://stackoverflow.com/questions/849211/shortest-distance-between-a-point-and-a-line-segment
        public double distanceFrom(Coordinate toCheck){
            Coordinate pCoord = new Coordinate(this.endPoint.x-this.startPoint.x, this.endPoint.y-this.startPoint.y);
            double norm = pCoord.x*pCoord.x + pCoord.y*pCoord.y;
            double u = ((toCheck.x-this.startPoint.x) * pCoord.x + (toCheck.y-this.startPoint.y) * pCoord.y)/norm;
            if (u > 1) u = 1;
            else if (u < 0) u = 0;
            Coordinate distCoord = new Coordinate(
                    this.startPoint.x+u*pCoord.x - toCheck.x,
                    this.startPoint.y+u*pCoord.y - toCheck.y
            );
            return Math.sqrt(distCoord.x*distCoord.x + distCoord.y*distCoord.y);
        }
        
        public Coordinate getBottomLeftBound(){
            return new Coordinate(Math.min(this.startPoint.x, this.endPoint.x), Math.min(this.startPoint.y, this.endPoint.y));
        }
        public Coordinate getTopRightBound(){
            return new Coordinate(Math.max(this.startPoint.x, this.endPoint.x), Math.max(this.startPoint.y, this.endPoint.y));
        }
    }
}
