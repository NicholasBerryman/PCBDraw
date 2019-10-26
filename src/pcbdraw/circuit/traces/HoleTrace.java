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
public class HoleTrace extends CircuitTrace{
    private Coordinate centrePoint;
    
    public HoleTrace(Coordinate centrePoint){
        this.centrePoint = new Coordinate(centrePoint.x, centrePoint.y);
    }
    
    public void setCentrePoint(Coordinate centrePoint) {
        this.centrePoint = centrePoint;
    }
    
    @Override
    public Coordinate getMajorCoord() {
        return this.centrePoint;
    }

    @Override
    public boolean withinRange(Coordinate toCheck, double radiusMM) {
        double distToCircle = Math.sqrt(Math.pow(centrePoint.x-toCheck.x,2)+Math.pow(centrePoint.y-toCheck.y,2));
        return distToCircle <= radiusMM; 
    }

    @Override
    public boolean equals(CircuitTrace c) {
        if (c instanceof HoleTrace)
            return this.centrePoint.equals(((HoleTrace) c).centrePoint);
        return false;
    }

    @Override
    public void simplifyUsing(CircuitTrace c) {}

    @Override
    public double distanceTo(Coordinate c) {
        double distToCircle = Math.sqrt(Math.pow(centrePoint.x-c.x,2)+Math.pow(centrePoint.y-c.y,2));
        return distToCircle;
    }

    @Override
    public void moveTo(Coordinate c) {
        this.centrePoint = c;
    }

    @Override
    public CircuitTrace copy() {
        return new HoleTrace(this.centrePoint);
    }

    @Override
    public boolean inArea(Coordinate botLeft, Coordinate topRight) {
        return 
            this.getMajorCoord().x >= botLeft.x && this.getMajorCoord().x <= topRight.x &&
            this.getMajorCoord().y >= botLeft.y  && this.getMajorCoord().y <= topRight.y;
    }
}
