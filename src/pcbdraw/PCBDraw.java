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
import pcbdraw.gui.BoardPane;
import pcbdraw.gui.CanvasPane;
import pcbdraw.gui.ExportPane;
import pcbdraw.gui.ToolsPane;
import pcbdraw.gui.TopMenu;
import pcbdraw.gui.LoadScreen;
import pcbdraw.processing.GCoder;
import pcbdraw.processing.GCoder.ProgressListener;

/**
 *
 * @author Nick Berryman
 */
public class PCBDraw extends Application {
    private CanvasPane canvas;
    private ExportPane export;
    private BoardPane board;
    private ToolsPane tools;
    private SplitPane bottom;
    
    private final int defaultZoom = 5;
    private final int defaultSqSize = 4;
    private final int defaultWidth = 100;
    private final int defaultHeight = 100;
    private Stage primaryStage;
    
    
    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        initialise();
        setOpenedState(bottom, defaultZoom, defaultSqSize, defaultWidth, defaultHeight, true);
    }
    
    public void initialise(){
        TopMenu menu = new TopMenu();
        SplitPane root = new SplitPane(menu);
        root.setOrientation(Orientation.VERTICAL);
        
        bottom = new SplitPane();
        root.getItems().add(bottom);
        
        Scene scene = new Scene(root, 640, 480);
        
        FileChooser gcbChoose = new FileChooser();
        FileChooser.ExtensionFilter gcbFilter = new FileChooser.ExtensionFilter("PCB files (*.gcb)", "*.gcb");
        gcbChoose.getExtensionFilters().add(gcbFilter);
        
        menu.setSaveHandler(new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent event) {
                File toSave = gcbChoose.showSaveDialog(primaryStage);
                if (toSave != null){
                    try {
                        GCBFile gcb = new GCBFile(toSave.getPath());
                        gcb.save(canvas.getPaths(), canvas.getHoles(), canvas.getGeneralWidthMM(), canvas.getGeneralHeightMM(), canvas.getGeneralZoom() ,canvas.isGenerallyForCarvey());
                    } catch (IOException ex) {
                        Logger.getLogger(PCBDraw.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        });
        
        menu.setOpenHandler(new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent event) {
                File toOpen = gcbChoose.showOpenDialog(primaryStage);
                if (toOpen != null){
                    try {
                        GCBFile gcb = new GCBFile(toOpen.getPath());
                        gcb.readProperties();
                        setOpenedState(bottom, defaultZoom, defaultSqSize, gcb.getBoardWidth(),gcb.getBoardHeight(), gcb.getForCarvey());
                        canvas.getPaths().clear();
                        canvas.getHoles().clear();
                        gcb.read(canvas.getPaths(), canvas.getHoles(), canvas.getGeneralZoom());
                        canvas.drawGrid(gcb.getBoardWidth(),gcb.getBoardHeight(),canvas.getGeneralSquareSizeMM(),canvas.getGeneralZoom(),canvas.isGenerallyForCarvey());
                        canvas.redraw();
                    } catch (IOException ex) {
                        Logger.getLogger(PCBDraw.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }    
            }
        });
        
        menu.setNewFileHandler(new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent event) {
                setOpenedState(bottom, defaultZoom, defaultSqSize, defaultWidth, defaultHeight, true);    
            }
        });
        
        primaryStage.getIcons().add(new Image(PCBDraw.class.getResourceAsStream("/icon_free.png")));
        primaryStage.setTitle("PCBDraw");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void setOpenedState(SplitPane bottom, int zoom, int sqSize, int width, int height, boolean forCarvey){
        bottom.getItems().clear();
        
        TabPane tabs = new TabPane();
        board = new BoardPane();
        board.setZoom(zoom);
        board.setSqSize(sqSize);
        board.setWidth(width);
        board.setHeight(height);
        board.setForCarvey(forCarvey);
        tabs.getTabs().add(board);
        tools = new ToolsPane();
        tabs.getTabs().add(tools);
        export = new ExportPane();
        tabs.getTabs().add(export);
        bottom.getItems().add(tabs);
        tabs.setMinWidth(190);
        tabs.setMaxWidth(250);
        bottom.setDividerPosition(0, 0);
        
        canvas = new CanvasPane();
        bottom.getItems().add(canvas);
        canvas.drawGrid(width, height, defaultSqSize, defaultZoom, forCarvey);
        
        
        board.addZoomListener(new ChangeListener(){
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                canvas.zoom((Integer) newValue);
            }
        });
        board.addSqSizeListener(new ChangeListener(){
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                canvas.updateSquareSize((Integer) newValue);
            }
        });
        board.addWidthListener(new ChangeListener(){
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                canvas.updateWidth((Integer) newValue);
            }
        });
        board.addHeightListener(new ChangeListener(){
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                canvas.updateHeight((Integer) newValue);
            }
        });
        board.addCarveyListener(new ChangeListener(){
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                canvas.updateForCarvey((Boolean) newValue);
            }
        });
        
        tools.addPathHandler(new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent event) {
                canvas.deselect();
                canvas.setAction(CanvasPane.CanvasAction.DrawLine);
            }
        });
        tools.addHoleHandler(new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent event) {
                canvas.deselect();
                canvas.setAction(CanvasPane.CanvasAction.DrawHole);
            }
        });
        tools.addSelectHandler(new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent event) {
                canvas.deselect();
                canvas.setAction(CanvasPane.CanvasAction.Select);
            }
        });
        tools.addMoveHandler(new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent event) {
                canvas.setAction(CanvasPane.CanvasAction.Move);
                tools.deselect();
            }
        });
        tools.addDeleteHandler(new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent event) {
                canvas.deleteSelected();
            }
        });
        
        FileChooser ncChoose = new FileChooser();
        FileChooser.ExtensionFilter gcodeFilter = new FileChooser.ExtensionFilter("GCode files (*.gcode)", "*.gcode");
        FileChooser.ExtensionFilter ncFilter = new FileChooser.ExtensionFilter("GCode files (*.nc)", "*.nc");
        ncChoose.getExtensionFilters().add(gcodeFilter);
        ncChoose.getExtensionFilters().add(ncFilter);
        export.addExportHandler(new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent event) {
                //new Alert(Alert.AlertType.INFORMATION, "The program will now generate the gcode.\nThis may take some time and the program may stop responding for a bit - just wait it out").showAndWait();
                LoadScreen load = new LoadScreen();
                GCoder gcoder = new GCoder(export.getZDown(), export.getDrillDown(), export.getZUp(), export.getFeed(), export.getPathWidth());
                gcoder.setProgressListener(new ProgressListener(){
                    @Override
                    public void update(int newProgress) {
                        Platform.runLater(new Runnable(){
                            @Override
                            public void run() {
                                load.setProgress(newProgress/(double)gcoder.getMaxProgress());
                            }
                        });
                        /*System.out.print(newProgress);
                        System.out.print("|");
                        System.out.println(gcoder.getMaxProgress());*/
                    }
                });
                Thread t = new Thread(){
                    @Override
                    public void run(){
                        String gcode = "";
                        try {
                            Platform.runLater(load::show);
                            gcode = gcoder.compile(canvas.getPaths(), canvas.getHoles(), canvas.getGeneralZoom(), canvas.getGeneralWidthMM(), canvas.getGeneralHeightMM(), canvas.isGenerallyForCarvey());
                            Platform.runLater(load::enableExit);
                        }
                        catch (ArrayIndexOutOfBoundsException e){
                            Platform.runLater(()->{
                                    new Alert(Alert.AlertType.INFORMATION, "Cannot export!\nToo close to edge of board!").showAndWait();
                            });
                            
                            Platform.runLater(load::enableExit);
                            Platform.runLater(load::setErrorMessage);
                            return;
                        }
                        catch (Exception e){
                            Platform.runLater(()->{
                                    new Alert(Alert.AlertType.INFORMATION, "Cannot export!\n"+e.getMessage()).showAndWait();
                            });
                            
                            Platform.runLater(load::enableExit);
                            Platform.runLater(load::setErrorMessage);
                            return;
                        }
                        catch (Error e){
                            Platform.runLater(()->{
                                    new Alert(Alert.AlertType.INFORMATION, "Cannot export!\n"+e.getMessage()).showAndWait();
                                    System.exit(1);
                            });
                            
                            Platform.runLater(load::enableExit);
                            Platform.runLater(load::setErrorMessage);
                            return;
                        }
                        final String gcodeF = gcode;
                        Platform.runLater(new Runnable(){
                            @Override
                            public void run() {
                                File toSave = ncChoose.showSaveDialog(primaryStage);

                                if (toSave != null){
                                    try {
                                        GCodeFile exportFile = new GCodeFile(toSave.getPath());
                                        exportFile.save(gcodeF);
                                    } catch (IOException ex) {
                                        Logger.getLogger(PCBDraw.class.getName()).log(Level.SEVERE, null, ex);
                                    }
                                } 
                            }
                        });
                        
                    }
                };
                t.start();
            }
        });
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}
