/**
 * Controller is located at the bottom of the game window.
 * It contains all the buttons and sliders that the user will
 * use to manipulate the game.
 *
 * @author Ginelle Ignacio
 * @author Rudi André Dahle
 * @author Olav Smevoll
 */

package sample;

import javafx.animation.Animation;
import javafx.animation.Animation.Status;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.event.EventHandler;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.NoSuchElementException;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.paint.Color;


public class Controller implements Initializable {

    //Data field related to FXML
    @FXML private Canvas canvas;
    @FXML private Canvas canvasGrid;
    @FXML private Canvas canvasBG;
    @FXML private ColorPicker colorPicker;
    @FXML private ColorPicker backgroundColor;
    @FXML private Slider zoomSlider;
    @FXML private Slider speedSlider;
    @FXML private Button playPause;
    @FXML private Label genCounter;
    @FXML private Label fpsCount;
    @FXML private Label zoomCount;
    @FXML private ToggleButton gridToggle;

    //Data field
    private GraphicsContext gc;
    private GraphicsContext gcGrid;
    private GraphicsContext gcBG;
    private Timeline timeline;
    private boolean running = false;
    private boolean showGrid = false;
    private double FPS; //frames per second
    private double xCoord;
    private double yCoord;


    //Objects
    Grid grid;
    Board gameBoard = new StatBoard();
    Graphics graphics;
    FileHandler reader;
    Stage helpWindow;
    Stage readWeb;
    Alerts error;


    /**
     * Controller class has a default constructor that
     * receives no argument.
     */
    public Controller(){

    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {

        //Objects
        gc = canvas.getGraphicsContext2D();
        gcGrid = canvasGrid.getGraphicsContext2D();
        gcBG = canvasBG.getGraphicsContext2D();
        graphics = new Graphics(gc);
        grid = new Grid(gcGrid);
        reader = new FileHandler();
        helpWindow = new Stage();
        readWeb = new Stage();
        error = new Alerts();


        //Grid properties
        graphics.setCellHeight(gameBoard.getBoardHeight());
        graphics.setCellWidth(gameBoard.getBoardWidth());
        graphics.setXCell(xCoord);
        graphics.setYCell(yCoord);
        grid.setCanvasHeight(gcGrid.getCanvas().heightProperty().intValue());
        grid.setCanvasWidth(gcGrid.getCanvas().widthProperty().intValue());
        grid.setCellWidth(graphics.getCellWidth());
        grid.setCellHeight(graphics.getCellHeight());


        //Initial properties in the GUI
        genCounter.setText(gameBoard.getGenCounter());  //DUPLIKAT??
        graphics.gc.setFill(Color.rgb(26,0,104));
        colorPicker.setValue(Color.rgb(26,0,104));
        backgroundColor.setValue(Color.rgb(220,220,220));
        speedSlider.setValue(50.0);
        speedSlider.setShowTickMarks(true);
//        zoomSlider.setShowTickMarks(true);
        FPS = speedSlider.getValue();
        fpsCount.setText(Integer.toString((int)FPS));
        //zoomCount.setText(Integer.toString((int)zoomSlider.getValue));
        //gridToggle.setSelected(true);

        //Time properties responsible for the animation
        Duration duration = Duration.millis(1000/FPS);
        KeyFrame keyframe = new KeyFrame(duration, (ActionEvent e) -> {
            graphics.draw(gameBoard.getGameBoard());
            gameBoard.nextGeneration();
            genCounter.setText(gameBoard.getGenCounter());  //DUPLIKAT??
        });
        timeline = new Timeline();
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.getKeyFrames().add(keyframe);
    }


    /**
     * Method called when user selects a single cell
     * to input into the canvas area
     *
     * @author Rudi André Dahle
     * @param event Represents a mouse event used when
     *              the user interacts with the GUI.
     */
    public void selectCell(MouseEvent event){
        xCoord = event.getX();
        yCoord = event.getY();
        graphics.setYCell(yCoord);
        graphics.setXCell(xCoord);
        graphics.drawCell(gameBoard.getGameBoard());
    }


    /**
     * Method called when user drags cells to input into
     * the canvas area
     *
     * @author Rudi André Dahle
     * @param event Represents a mouse event used when
     *              the user interacts with the GUI.
     */
    public void dragCell(MouseEvent event){
        xCoord = event.getX();
        yCoord = event.getY();
        graphics.setYCell(yCoord);
        graphics.setXCell(xCoord);
        graphics.drawCell(gameBoard.getGameBoard());
    }


    /**
     * Method called when user plays or pauses the button
     * for the animation
     *
     * @author Rudi André Dahle
     * @param actionEvent represents an Action Event used to
     *                    when a button has been fired.
     */
    @FXML
    public void OnStartClick(ActionEvent actionEvent) {
        if(timeline.getStatus() == Status.RUNNING) {
            timeline.pause();
            running = false;
        } else {
            timeline.play();
            running = true;
        }
    }



    /**
     * Method called when user press play/pause button
     * to change the button text.
     *
     * @author Ginelle Ignacio
     */
    public void playPauseEvent(){
        if(running){
            playPause.setText("Pause");
        } else {
            playPause.setText("Play");
        }
    }


    /**
     * Clear button to clear the cells in the canvas area
     *
     * @author Ginelle Ignacio
     * @param actionEvent represents an Action Event used to
     *                    when a button has been fired.
     */
    public void clearEvent(ActionEvent actionEvent){
        timeline.stop();
        playPause.setText("Play");
        gameBoard.resetGenCount();
        gameBoard.clearBoard();
        graphics.draw(gameBoard.getGameBoard());
    }


    /**
     * Speed slider manipulates the speed of animation
     *
     * @author Ginelle Ignacio
     * @param event Represents a mouse event used when
     *              the user interacts with the slider.
     */
    public void speedChanged(MouseEvent event) {
        timeline.stop();

        this.FPS = speedSlider.getValue();
        fpsCount.setText(Integer.toString((int)FPS));

        if (running) {
            if (FPS != 0) {
                Duration duration = Duration.millis(1000 / FPS);
                KeyFrame keyframe = new KeyFrame(duration, (ActionEvent e) -> {
                    graphics.draw(gameBoard.getGameBoard());
                    gameBoard.nextGeneration();
                    genCounter.setText(gameBoard.getGenCounter());
                });
                timeline = new Timeline();
                timeline.setCycleCount(Animation.INDEFINITE);
                timeline.getKeyFrames().add(keyframe);
                timeline.play();
            }
        }
    }


    /**
     * Method called when the user drags the zoom slider to
     * zoom in or zoom out
     *
     * @author Ginelle Ignacio
     * @param event Represents a mouse event used when
     *              the user interacts with the slider
     */
    public void zoomChanged(MouseEvent event) {
        int zoom = (int)zoomSlider.getValue();



        /*graphics.setCellHeight(zoom);
        graphics.setCellWidth(zoom/2);
        grid.setCanvasHeight(gcGrid.getCanvas().heightProperty().intValue());
        grid.setCanvasWidth(gcGrid.getCanvas().widthProperty().intValue());
        grid.setCellHeight(graphics.getCellHeight());
        grid.setCellWidth(graphics.getCellWidth()/2);*/

    }

    /**
     * Color picker changes the colors of the cells
     *
     * @author Ginelle Ignacio
     * @param actionEvent represents an Action Event used to
     *                    when a button has been fired.
     */
    public void colorChanged(ActionEvent actionEvent){
        graphics.gc.setFill(colorPicker.getValue());
    }


    /**
     * Change background color of the game.
     * @author Rudi André Dahle
     * @param actionEvent represents an Action Event used to
     *                    when a button has been fired.
     */
    public void backgroundChanged(ActionEvent actionEvent){
        double canvasWidth = canvas.getWidth();
        double canvasHeight = canvas.getHeight();

        gcBG.setFill(backgroundColor.getValue());
        gcBG.fillRect(0, 0, canvasWidth, canvasHeight);

    }


    /**
     * Grid toggle to make the grid visible or invisible
     *
     * @author Rudi André Dahle
     * @param actionEvent Represents an Action Event used
     *                    when a button has been fired.
     */
    public void gridEvent(ActionEvent actionEvent) {
        if (!showGrid) {
            showGrid = true;
            gridToggle.setSelected(true);
            grid.draw();

        }else {
            showGrid = false;
            grid.clearGrid();
            gridToggle.setSelected(false);
        }
    }

    /**
     * Method called when user select on "Guidelines" on Menu.
     * Method contains information about the rules of the game,
     * and details about the controllers.
     *
     * @author Ginelle Ignacio
     * @param ae represents an Action Event used
     *           when a menu item has been clicked
     * @throws Exception if an error occurs while opening help window
     */
    public void helpEvent (ActionEvent ae) throws Exception {
        try{
            Parent helpRoot = FXMLLoader.load(getClass().getClassLoader().getResource("Rules/Guide.fxml"));
            helpWindow.setTitle("Guidelines");
            helpWindow.setScene(new Scene(helpRoot));
            helpWindow.show();
        } catch (IOException io){
            error.notLoading();
        }
    }

    /**
     * "Open File..." menu item set to open FileChooser window
     * Method also includes exceptions.
     *
     * @author Rudi Andre Dahle
     * @author Ginelle Ignacio
     * @param ae represents an Action Event used
     *           when a menu item has been clicked
     * @throws PatternFormatExceptions if an error occurs while
     *         opening file chooser window
     */
    public void openFiles(ActionEvent ae)throws PatternFormatExceptions {
        try {
            reader.chooseFile();
        } catch (FileNotFoundException fe){
            error.fileNotFound();
            throw new PatternFormatExceptions("File not found");
        } catch (IOException e) {
            error.errorOpeningfile();
            throw new PatternFormatExceptions("Error opening file");
        } catch (NoSuchElementException ne){
            error.incorrectMatch();
            throw new PatternFormatExceptions("Incorrect file format");
        } catch (IllegalStateException ie) {
            error.errorReading();
            throw new PatternFormatExceptions("Error reading from file");
        }
    }


    /**
     * Method called when the user selects "Read Web File.."
     * on the menu list under "File". This method will read
     * a web address and convert it into a pattern.
     *
     * @author Ginelle Ignacio
     * @param ae represents an Action Event used to
     *           when a menu item has been clicked
     * @throws Exception if an error occurs while opening URL window
     */

    public void webFile(ActionEvent ae) throws Exception {
        try {
            Parent webRoot = FXMLLoader.load(getClass().getClassLoader().getResource("WebFile/Webfile.fxml"));
            readWeb.setTitle("Read web file");
            readWeb.setScene(new Scene(webRoot));
            readWeb.show();
        } catch (IOException io){
            error.notLoading();
        }
    }

    /**
     * Method called when the user clicks on the "Close"
     * item located in Menu under File to close whole window
     *
     * @author Ginelle Ignacio
     * @param ae represents an Action Event used to
     *           when a menu item has been clicked
     */
    public void closeWindow (ActionEvent ae){
        System.exit(0);
    }
}