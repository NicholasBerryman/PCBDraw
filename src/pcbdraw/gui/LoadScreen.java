/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pcbdraw.gui;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 *
 * @author Nick Berryman
 */
public class LoadScreen {
    ProgressBar prog;
    Button ok;
    Label loadLabel;
    Stage stage;
    public LoadScreen(){
        VBox root = new VBox();
        root.setAlignment(Pos.CENTER);
        Scene scene = new Scene(root, 250, 100);
        stage = new Stage();
        stage.setTitle("Exporting....");
        
        loadLabel = new Label("Exporting gcode file...");
        root.getChildren().add(loadLabel);
        prog = new ProgressBar();
        root.getChildren().add(prog);
        ok = new Button("OK");
        root.getChildren().add(ok);
        ok.setDisable(true);
        
        ok.setOnAction(new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent event) {
                stage.hide();
            }
        });
        
        
        stage.setScene(scene);
    }
    
    public void show(){
        stage.setOnCloseRequest(new EventHandler<WindowEvent>(){
            @Override
            public void handle(WindowEvent event) {
                event.consume();
            }
        });
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.show();
    }
    
    public void enableExit(){
        loadLabel.setText("Export done!");
        ok.setDisable(false);
    }
    
    public void setProgress(double value){
        prog.setProgress(value);
    }
    
    public void setErrorMessage(){
        loadLabel.setText("Export error!");
    }
}
