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
public class Coordinate {
    public final double x;
    public final double y;
    
    public Coordinate(double x, double y) {
        this.x = x;
        this.y = y;
    }
    
    public Coordinate translate(double x, double y){
        return new Coordinate(this.x+x, this.y+y);
    }
    
    public double distanceTo(Coordinate c){
        return Math.sqrt(Math.pow(x-c.x,2) + Math.pow(y-c.y, 2));
    }
    
    public boolean equals(Coordinate c){
        return this.x == c.x && this.y == c.y;
    }
    
    public Coordinate add(Coordinate c){
        return new Coordinate(this.x+c.x, this.y+c.y);
    }
    public Coordinate subtract(Coordinate c){
        return new Coordinate(this.x-c.x, this.y-c.y);
    }
}
