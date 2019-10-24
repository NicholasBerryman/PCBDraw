/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pcbdraw;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import pcbdraw.data.GCBFile;
import pcbdraw.data.GCodeFile;
import pcbdraw.gui.context.BoardTab;
import pcbdraw.gui.workspace.WorkPane;
import pcbdraw.gui.context.ExportTab;
import pcbdraw.gui.context.ToolsTab;
import pcbdraw.gui.TopMenu;
import pcbdraw.gui.LoadScreen;
import pcbdraw.CNC.GCodeGenerator;
import pcbdraw.CNC.GCodeGenerator.ProgressListener;
import pcbdraw.circuit.Coordinate;

/**
 *
 * @author Nick Berryman
 */
public class PCBDraw extends Application {
    Stage primaryStage;
    
    @Override
    public void start(Stage primaryStage) {
        
        this.primaryStage = primaryStage;
        initialise();
    }
    
    public void initialise(){
        SplitPane root = new SplitPane();
        Scene scene = new Scene(root, 640, 480);
        root.setOrientation(Orientation.VERTICAL);
        
        WorkPane workspace = new WorkPane(new Coordinate(100,100),5,4,true);
        root.getItems().add(workspace);
        workspace.update();
        
        primaryStage.getIcons().add(new Image(PCBDraw.class.getResourceAsStream("/icon_free.png")));
        primaryStage.setTitle("PCBDraw");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}
