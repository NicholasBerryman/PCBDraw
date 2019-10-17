/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pcbdraw.gui;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.Tab;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 *
 * @author Nick Berryman
 */
public class ExportPane extends Tab{
    private final Spinner<Double> zDownSpin = new Spinner<>(-10, 1, -0.3, 0.1);
    private final Spinner<Double> drillDownSpin = new Spinner<>(-10, 1, -1, 0.1);
    private final Spinner<Integer> zUpSpin = new Spinner<>(1, 100, 10);
    private final Spinner<Integer> feedSpin = new Spinner<>(1, 100000, 800);
    private final Spinner<Double> pWidthSpin = new Spinner<>(0.3, 25, 2, 0.1);
    private Button export;
    
    public ExportPane(){
        this.setClosable(false);
        this.setText("Export");
        VBox root = new VBox();
        this.setContent(root);
        
        HBox zDown = new HBox();
        root.getChildren().add(zDown);
        Label zDownLab = new Label("Z-Down (mm)");
        zDown.getChildren().add(zDownLab);
        zDownSpin.setPrefWidth(80);
        zDownSpin.setEditable(true);
        zDown.getChildren().add(zDownSpin);
        
        HBox drillDown = new HBox();
        root.getChildren().add(drillDown);
        Label drillDownLab = new Label("Drill Down (mm)");
        drillDown.getChildren().add(drillDownLab);
        drillDownSpin.setPrefWidth(80);
        drillDownSpin.setEditable(true);
        drillDown.getChildren().add(drillDownSpin);
        
        HBox zUp = new HBox();
        root.getChildren().add(zUp);
        Label zUpLab = new Label("Z-Up (mm)");
        zUp.getChildren().add(zUpLab);
        zUpSpin.setPrefWidth(60);
        zUpSpin.setEditable(true);
        zUp.getChildren().add(zUpSpin);
        
        HBox feed = new HBox();
        root.getChildren().add(feed);
        Label feedLab = new Label("Feedrate (mm/min)");
        feed.getChildren().add(feedLab);
        feedSpin.setPrefWidth(80);
        feedSpin.setEditable(true);
        feed.getChildren().add(feedSpin);
        
        HBox pWidth = new HBox();
        root.getChildren().add(pWidth);
        Label pWidthLab = new Label("Path Width (mm)");
        pWidth.getChildren().add(pWidthLab);
        pWidthSpin.setPrefWidth(60);
        pWidthSpin.setEditable(true);
        pWidth.getChildren().add(pWidthSpin);
        
        export = new Button("Export");
        root.getChildren().add(export);
    }
    
    public double getZDown(){
        return zDownSpin.getValue();
    }
    public double getZUp(){
        return zUpSpin.getValue();
    }
    public double getFeed(){
        return feedSpin.getValue();
    }
    public double getPathWidth(){
        return pWidthSpin.getValue();
    }
    
    public double getDrillDown(){
        return drillDownSpin.getValue();
    }
    
    public void addExportHandler(EventHandler<ActionEvent> handle){
        this.export.setOnAction(handle);
    }
}
