/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pcbdraw.gui;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory.IntegerSpinnerValueFactory;
import javafx.scene.control.Tab;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 *
 * @author Nick Berryman
 */
public class BoardPane extends Tab{
    private Spinner<Integer> widthSpin = new Spinner<>(10, 1000, 100);
    private Spinner<Integer> heightSpin = new Spinner<>(10, 1000, 100);
    private Spinner<Integer> zoomSpin = new Spinner<>(1, 100, 10);
    private Spinner<Integer> sqSizeSpin = new Spinner<>(1, 100, 5);
    private CheckBox forCarvey = new CheckBox();
    
    public BoardPane(){
        this.setClosable(false);
        this.setText("Board");
        VBox root = new VBox();
        this.setContent(root);
        
        HBox width = new HBox();
        root.getChildren().add(width);
        width.getChildren().add(new Label("Board Width (mm): "));
        widthSpin.setPrefWidth(60);
        widthSpin.setEditable(true);
        widthSpin.focusedProperty().addListener((observable, oldValue, newValue) -> { widthSpin.increment(0);});
        width.getChildren().add(widthSpin);
        
        HBox height = new HBox();
        root.getChildren().add(height);
        height.getChildren().add(new Label("Board Height (mm): "));
        heightSpin.setPrefWidth(60);
        heightSpin.setEditable(true);
        heightSpin.focusedProperty().addListener((observable, oldValue, newValue) -> { heightSpin.increment(0);});
        height.getChildren().add(heightSpin);
        
        
        HBox zoom = new HBox();
        root.getChildren().add(zoom);
        zoom.getChildren().add(new Label("Zoom: "));
        zoomSpin.setPrefWidth(60);
        zoomSpin.setEditable(true);
        zoomSpin.focusedProperty().addListener((observable, oldValue, newValue) -> { zoomSpin.increment(0);});
        zoom.getChildren().add(zoomSpin);
        
        HBox squareSize = new HBox();
        root.getChildren().add(squareSize);
        squareSize.getChildren().add(new Label("Square Size (mm): "));
        sqSizeSpin.setPrefWidth(60);
        sqSizeSpin.setEditable(true);
        sqSizeSpin.focusedProperty().addListener((observable, oldValue, newValue) -> { sqSizeSpin.increment(0);});
        squareSize.getChildren().add(sqSizeSpin);
        
        HBox carvey = new HBox();
        root.getChildren().add(carvey);
        carvey.getChildren().add(new Label("Using Carvey: "));
        carvey.getChildren().add(forCarvey);
        
        forCarvey.selectedProperty().addListener(new ChangeListener(){
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                updateMaxSizes((Boolean) newValue);
            }
        });
    }
    
    public void addZoomListener(ChangeListener listen){
      zoomSpin.valueProperty().addListener(listen);
    }
    
    public void addSqSizeListener(ChangeListener listen){
      sqSizeSpin.valueProperty().addListener(listen);
    }
    
    public void setZoom(int zoom){
        zoomSpin.getValueFactory().setValue(zoom);
    }
    
    public void setSqSize(int sqSize){
        sqSizeSpin.getValueFactory().setValue(sqSize);
    }
    
    public void setWidth(int width){
        widthSpin.getValueFactory().setValue(width);
    }
    
    public void setHeight(int height){
        heightSpin.getValueFactory().setValue(height);
    }
    
    public void setForCarvey(boolean forCarvey){
        this.forCarvey.setSelected(forCarvey);
        updateMaxSizes(forCarvey);
    }
    
    private void updateMaxSizes(boolean forCarvey){
        if (forCarvey){
            ((IntegerSpinnerValueFactory)widthSpin.getValueFactory()).setMax(300);
            ((IntegerSpinnerValueFactory)heightSpin.getValueFactory()).setMax(200);
        }
        else{
            ((IntegerSpinnerValueFactory)widthSpin.getValueFactory()).setMax(1000);
            ((IntegerSpinnerValueFactory)heightSpin.getValueFactory()).setMax(1000);
        }
    }
    
    public void addWidthListener(ChangeListener listen){
        widthSpin.valueProperty().addListener(listen);
    }
    
    public void addHeightListener(ChangeListener listen){
        heightSpin.valueProperty().addListener(listen);
    }
    
    public void addCarveyListener(ChangeListener listen){
        forCarvey.selectedProperty().addListener(listen);
    }
}
