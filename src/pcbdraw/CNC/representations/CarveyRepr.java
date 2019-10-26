/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pcbdraw.CNC.representations;

import pcbdraw.circuit.Coordinate;
import pcbdraw.data.GCodeFile;

/**
 *
 * @author Nick Berryman
 */
public class CarveyRepr extends CNCRepr{
    private final double zDown;
    private final double drillDown;
    private final double zUp;
    
    public CarveyRepr(GCodeFile gcodeFile, double zDown, double drillDown, double zUp){
        super(gcodeFile);
        this.zDown = zDown;
        this.zUp = zUp;
        this.drillDown = drillDown;
    }
    
    @Override
    public void initMachine() {
        this.getGcode().append("G90G94\n");//Absolute, units/minute mode
        this.getGcode().append("G17\n");   //XY Plane
        this.getGcode().append("G21\n");   //mm
        this.getGcode().append("M9\n");    //Coolant off
    }

    @Override
    public void move(Coordinate c) {
        this.getGcode()
                .append("X").append(c.x)   //X Coordinate
                .append("Y").append(c.y)   //Y Coordinate
                .append('\n');
    }

    @Override
    public void startCut() {
        this.moveDown(zDown);
    }

    @Override
    public void endCut() {
        this.getGcode().append("G0")        //Rapid move
                .append("Z").append(zUp)    //Moving height
                .append('\n');
    }    

    @Override
    public void cutHole(Coordinate c) {
        this.endCut();
        this.move(c);
        this.moveDown(drillDown);
        
    }

    @Override
    public void finish() {
        this.getGcode().append("G0")         //Rapid move
                .append("Z").append(zUp*2)   //Finishing height
                .append('\n');
    }

    
    private void moveDown(double depth){
        this.getGcode().append("G1")        //Linear move
                .append("Z").append(depth)  //Cutting depth
                .append("F").append(800)    //Feedrate 800mm/min
                .append("S").append(10000) //Spindle speed 100000 rpm
                .append('\n');
    }
}
