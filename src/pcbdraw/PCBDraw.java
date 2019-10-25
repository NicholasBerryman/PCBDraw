/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pcbdraw;

import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import pcbdraw.gui.MainPane;

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
        MainPane root = new MainPane();
        Scene scene = new Scene(root, 640, 480);
        root.initialise();
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
