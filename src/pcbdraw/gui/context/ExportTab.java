/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pcbdraw.gui.context;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.Tab;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import pcbdraw.CNC.generation.GCodeGenerator;
import pcbdraw.CNC.generation.GCodeGenerator.UncarveybleException;
import pcbdraw.CNC.representations.CarveyRepr;
import pcbdraw.circuit.PCB;
import pcbdraw.data.GCodeFile;
import pcbdraw.gui.progress.LoadScreen;

/**
 *
 * @author Nick Berryman
 */
public class ExportTab extends Tab{
    private final Spinner<Double> zDownSpin = new Spinner<>(-10, 1, -0.2, 0.1);
    private final Spinner<Double> drillDownSpin = new Spinner<>(-10, 1, -0.8, 0.1);
    private final Spinner<Integer> zUpSpin = new Spinner<>(1, 100, 10);
    private final Spinner<Integer> feedSpin = new Spinner<>(1, 100000, 800);
    private final Spinner<Double> pWidthSpin = new Spinner<>(0.1, 25, 2, 0.1);
    private final Button export;
    private final PCB pcb;
    
    //TODO connect to gcoder
    public ExportTab(PCB pcb){
        super();
        this.pcb = pcb;
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
        zDownSpin.focusedProperty().addListener((observable, oldValue, newValue) -> { zDownSpin.increment(0);});
        zDown.getChildren().add(zDownSpin);
        
        HBox drillDown = new HBox();
        root.getChildren().add(drillDown);
        Label drillDownLab = new Label("Drill Down (mm)");
        drillDown.getChildren().add(drillDownLab);
        drillDownSpin.setPrefWidth(80);
        drillDownSpin.setEditable(true);
        drillDownSpin.focusedProperty().addListener((observable, oldValue, newValue) -> { drillDownSpin.increment(0);});
        drillDown.getChildren().add(drillDownSpin);
        
        HBox zUp = new HBox();
        root.getChildren().add(zUp);
        Label zUpLab = new Label("Z-Up (mm)");
        zUp.getChildren().add(zUpLab);
        zUpSpin.setPrefWidth(60);
        zUpSpin.setEditable(true);
        zUpSpin.focusedProperty().addListener((observable, oldValue, newValue) -> { zUpSpin.increment(0);});
        zUp.getChildren().add(zUpSpin);
        
        HBox feed = new HBox();
        //root.getChildren().add(feed);
        Label feedLab = new Label("Feedrate (mm/min)");
        feed.getChildren().add(feedLab);
        feedSpin.setPrefWidth(80);
        feedSpin.setEditable(true);
        feedSpin.focusedProperty().addListener((observable, oldValue, newValue) -> { feedSpin.increment(0);});
        feed.getChildren().add(feedSpin);
        
        HBox pWidth = new HBox();
        root.getChildren().add(pWidth);
        Label pWidthLab = new Label("Path Width (mm)");
        pWidth.getChildren().add(pWidthLab);
        pWidthSpin.setPrefWidth(60);
        pWidthSpin.setEditable(true);
        pWidthSpin.focusedProperty().addListener((observable, oldValue, newValue) -> { pWidthSpin.increment(0);});
        pWidth.getChildren().add(pWidthSpin);
        
        export = new Button("Export");
        root.getChildren().add(export);
        
        export.setOnAction((e) -> {
            try {
                zDownSpin.increment(0);
                drillDownSpin.increment(0);
                zUpSpin.increment(0);
                feedSpin.increment(0);
                pWidthSpin.increment(0);
                
                GCodeFile gcodeFile = GCodeFile.askUserToSaveAs();
                if (gcodeFile != null)
                {
                    LoadScreen exportProgressWindow = new LoadScreen();
                    CarveyRepr carvey = new CarveyRepr(gcodeFile, zDownSpin.getValue(), drillDownSpin.getValue(), zUpSpin.getValue());
                    GCodeGenerator gcoder = new GCodeGenerator(carvey, pcb, pWidthSpin.getValue());
                    gcoder.addProgressListener((progress) -> {
                        Platform.runLater(() -> {
                            exportProgressWindow.setProgress(progress);
                        });
                    });
                    exportProgressWindow.show();
                    new Thread(){
                        @Override
                        public void run(){
                            try {
                                gcoder.compileAndSave();
                            } catch (IOException ex) {
                                Platform.runLater(() -> {
                                    new Alert(Alert.AlertType.INFORMATION, "Error accessing this file!").showAndWait();
                                    exportProgressWindow.setErrorMessage();
                                });
                                try {
                                    gcodeFile.delete();
                                } catch (IOException ex1) {
                                    Logger.getLogger(ExportTab.class.getName()).log(Level.SEVERE, null, ex1);
                                }
                            } catch (IndexOutOfBoundsException e2){
                                Platform.runLater(() -> {
                                    new Alert(Alert.AlertType.INFORMATION, "Error exporting!\nSomething is too close to the edge of the board!").showAndWait();
                                    exportProgressWindow.setErrorMessage();
                                });
                                try {
                                    gcodeFile.delete();
                                } catch (IOException ex1) {
                                    Logger.getLogger(ExportTab.class.getName()).log(Level.SEVERE, null, ex1);
                                }
                            } catch (UncarveybleException e3){
                                Platform.runLater(() -> {
                                    new Alert(Alert.AlertType.INFORMATION, "Error exporting!\nSomething is too close to Carvey's Smartclamp!").showAndWait();
                                    exportProgressWindow.setErrorMessage();
                                });
                                try {
                                    gcodeFile.delete();
                                } catch (IOException ex1) {
                                    Logger.getLogger(ExportTab.class.getName()).log(Level.SEVERE, null, ex1);
                                }
                            } catch (OutOfMemoryError e4){
                                Platform.runLater(() -> {
                                    new Alert(Alert.AlertType.INFORMATION, "Error exporting!\nYour board is too big for your computer to handle!\nPlease either make you board smaller or try again on a beefier computer!").showAndWait();
                                    exportProgressWindow.setErrorMessage();
                                });
                                try {
                                    gcodeFile.delete();
                                } catch (IOException ex1) {
                                    Logger.getLogger(ExportTab.class.getName()).log(Level.SEVERE, null, ex1);
                                }
                            }
                            
                            Platform.runLater(() -> {exportProgressWindow.enableExit();});
                        }
                    }.start();
                }
            } catch (IOException ex) {
                Logger.getLogger(ExportTab.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }
}
