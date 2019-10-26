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
public abstract class CircuitTrace {
    public abstract Coordinate getMajorCoord();
    public abstract boolean withinRange(Coordinate toCheck, double pathWidthMM);
    public abstract boolean equals(CircuitTrace c);
    public abstract void simplifyUsing(CircuitTrace c);
    public abstract double distanceTo(Coordinate c);
}
