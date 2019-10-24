/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pcbdraw.circuit;

/**
 *
 * @author Nick Berryman
 */
public class PathTrace extends CircuitTrace{
    private final Line pathLine;

    public PathTrace(Coordinate startPoint, Coordinate endPoint) {
        this.pathLine = new Line(startPoint, endPoint);
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
        return pathLine.getBottomLeftBound();
    }

    @Override
    public boolean withinRange(Coordinate toCheck, double pathWidthMM) {
        return pathLine.distanceFrom(toCheck) <= pathWidthMM;
    }
    
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
