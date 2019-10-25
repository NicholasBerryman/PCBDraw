/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pcbdraw.gui.context;

import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory.DoubleSpinnerValueFactory;
import javafx.scene.control.Tab;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import pcbdraw.circuit.Coordinate;
import pcbdraw.gui.workspace.guigrid.GUIGrid;

/**
 *
 * @author Nick Berryman
 */
public class BoardTab extends Tab{
    private final Spinner<Double> widthSpin = new Spinner<>(10.0, 1000.0, 100.0);
    private final Spinner<Double> heightSpin = new Spinner<>(10.0, 1000.0, 100.0);
    private final Spinner<Double> zoomSpin = new Spinner<>(1.0, 100.0, 10.0, 0.5);
    private final Spinner<Double> sqSizeSpin = new Spinner<>(1.0, 100.0, 5.0, 0.5);
    private final CheckBox forCarvey = new CheckBox();
    
    public BoardTab(GUIGrid workspace, Pane pane){
        super();
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
        widthSpin.getEditor().textProperty().addListener((obs, oldvalue, newValue) -> {
            if (!newValue.equals(""))
                workspace.setSize(new Coordinate(Double.parseDouble(newValue), workspace.getWorkspace().getSize().y));
            workspace.redraw(pane);
        });
        widthSpin.getValueFactory().setValue(workspace.getWorkspace().getSize().x);
        width.getChildren().add(widthSpin);
        
        HBox height = new HBox();
        root.getChildren().add(height);
        height.getChildren().add(new Label("Board Height (mm): "));
        heightSpin.setPrefWidth(60);
        heightSpin.setEditable(true);
        heightSpin.focusedProperty().addListener((observable, oldValue, newValue) -> { heightSpin.increment(0);});
        heightSpin.getEditor().textProperty().addListener((obs, oldvalue, newValue) -> {
            if (!newValue.equals(""))
                workspace.setSize(new Coordinate(workspace.getWorkspace().getSize().x, Double.parseDouble(newValue)));
            workspace.redraw(pane);
        });
        heightSpin.getValueFactory().setValue(workspace.getWorkspace().getSize().y);
        height.getChildren().add(heightSpin);
        
        
        HBox zoom = new HBox();
        root.getChildren().add(zoom);
        zoom.getChildren().add(new Label("Zoom: "));
        zoomSpin.setPrefWidth(60);
        zoomSpin.setEditable(true);
        zoomSpin.focusedProperty().addListener((observable, oldValue, newValue) -> { zoomSpin.increment(0);});
        zoomSpin.getEditor().textProperty().addListener((obs, oldvalue, newValue) -> {
            if (!newValue.equals(""))
                workspace.setZoom(Double.parseDouble(newValue));
            workspace.redraw(pane);
        });
        zoomSpin.getValueFactory().setValue(workspace.getZoom());
        zoom.getChildren().add(zoomSpin);
        
        HBox squareSize = new HBox();
        root.getChildren().add(squareSize);
        squareSize.getChildren().add(new Label("Square Size (mm): "));
        sqSizeSpin.setPrefWidth(60);
        sqSizeSpin.setEditable(true);
        sqSizeSpin.focusedProperty().addListener((observable, oldValue, newValue) -> { sqSizeSpin.increment(0);});
        sqSizeSpin.getEditor().textProperty().addListener((obs, oldvalue, newValue) -> {
            if (!newValue.equals(""))
                workspace.setSquareSizeMM(Double.parseDouble(newValue));
            workspace.redraw(pane);
        });
        sqSizeSpin.getValueFactory().setValue(workspace.getSquareSizeMM());
        squareSize.getChildren().add(sqSizeSpin);
        
        HBox carvey = new HBox();
        root.getChildren().add(carvey);
        carvey.getChildren().add(new Label("Using Carvey: "));
        carvey.getChildren().add(forCarvey);
        forCarvey.selectedProperty().addListener((observable, oldValue, newValue) -> {
            workspace.setCarvey(newValue);
            updateMaxSizes(newValue);
            workspace.redraw(pane);
        });
        forCarvey.setSelected(workspace.getCarvey());
    }
    
    private void updateMaxSizes(boolean forCarvey){
        if (forCarvey){
            ((DoubleSpinnerValueFactory)widthSpin.getValueFactory()).setMax(300);
            ((DoubleSpinnerValueFactory)heightSpin.getValueFactory()).setMax(200);
        }
        else{
            ((DoubleSpinnerValueFactory)widthSpin.getValueFactory()).setMax(1000);
            ((DoubleSpinnerValueFactory)heightSpin.getValueFactory()).setMax(1000);
        }
    }
}
