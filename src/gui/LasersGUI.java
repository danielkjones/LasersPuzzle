package gui;

import backtracking.Backtracker;
import backtracking.Configuration;
import backtracking.SafeConfig;
import backtracking.SafeSolver;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.FileNotFoundException;
import java.util.*;

import model.Block;
import model.LasersModel;

/**
 * The main class that implements the JavaFX UI.   This class represents
 * the view/controller portion of the UI.  It is connected to the model
 * and receives updates from it.
 *
 * @author Sean Strout @ RIT CS
 * @author Daniel Jones
 * @author Michael Johansen
 */
public class LasersGUI extends Application implements Observer {
    /** The UI's connection to the model */
    public LasersModel model;

    /** The gridpane that stores all of the models information */
    public BorderPane background;

    /** The labelt that tell the user information about their interactions with the view */
    public Label info;

    /** The name of the file the user inputs */
    public String filename;

    /** The configuration of the models current safe */
    public Configuration safeConfig;

    /** The primary stage of the view */
    public Stage pStage;

    @Override
    public void init() throws Exception {
        // the init method is run before start.  the file name is extracted
        // here and then the model is created.
        try {
            Parameters params = getParameters();
            filename = params.getRaw().get(0);
            this.model = new LasersModel(filename);
        } catch (FileNotFoundException fnfe) {
            System.out.println(fnfe.getMessage());
            System.exit(-1);
        }
        this.model.addObserver(this);
    }

    /**
     * A private utility function for setting the background of a button to
     * an image in the resources subdirectory.
     *
     * @param button the button control
     * @param bgImgName the name of the image file
     */
    public void setButtonBackground(Button button, String bgImgName) {
        BackgroundImage backgroundImage = new BackgroundImage(
                new Image( getClass().getResource("resources/" + bgImgName).toExternalForm()),
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                BackgroundSize.DEFAULT);
        Background background = new Background(backgroundImage);
        button.setBackground(background);
    }

    /**
     * The
     * @param stage the stage to add UI components into
     */
    private void init(Stage stage) {

        //Initialize the stage and the fields
        pStage = stage;
        Parameters params = getParameters();
        filename = params.getRaw().get(0);
        safeConfig = null;
        try {
            safeConfig = new SafeConfig(filename);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        model = null;
        try {
            model = new LasersModel(filename);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        background = new BorderPane();
        updateGUISafe();
        info = new Label(filename + " loaded.");
        background.setTop(info);
        ControllerGUI controller = new ControllerGUI(model,this);
        //make the button box
        HBox buttons = new HBox();

        //The check button and its function.
        Button Check = new Button("Check");
        Check.setOnAction(event -> {
            controller.check();
        });

        //The hint button and its function
        Button Hint = new Button("Hint");
        Hint.setOnAction(event -> {
            //Gets the hint from the current model, but switches over to a SafeConfig
            controller.hint(new SafeConfig(model));
        });

        //The solve button and its function
        Button Solve = new Button("Solve");
        Solve.setOnAction(event -> {
            controller.solve((SafeConfig) safeConfig);
        });

        //the restart button and its functions
        Button Restart = new Button("Restart");
        Restart.setOnAction(event -> {
            controller.restart();
        });

        //the load button and it's function
        Button Load = new Button("Load");
        Load.setOnAction(event -> {
            controller.load((SafeConfig) safeConfig);
        });

        //Add the buttons to the button node
        buttons.getChildren().addAll(Check,Hint,Solve,Restart,Load);
        background.setBottom(buttons);

        Scene scene = new Scene(background);
        stage.setScene(scene);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        init(primaryStage);  // do all your UI initialization here

        primaryStage.setTitle("Lasers");
        primaryStage.show();
    }

    @Override
    public void update(Observable o, Object arg) {
    }

    /**
     * The update method for the GUI.
     */
    public void updateGUISafe(){

        //update the button grid so that it reflects the model
        background.getChildren().removeAll();
        GridPane safeGrid = new GridPane();
        for(int r=0;r<model.getSafe().length;r++){
            safeGrid.addRow(r);
            for (int c=0;c<model.getSafe()[0].length;c++){
                safeGrid.addColumn(c);
                Block currBlock = model.getblock(r,c);
                safeGrid.add(makeButton(currBlock.getCharToDisplay(),c,r),c,r);
            }
        }
        //show the view
        safeGrid.setPadding(new Insets(10,10,10,10));
        background.setCenter(safeGrid);
        pStage.sizeToScene();
    }

    /**
     * Makes a button in the safe grid to reflect the type of block at location r,c in the safe oof the model
     * @param displayVal The character at r,c in the safe of the model
     * @param r the row to access in the safe of the model
     * @param c the column to access in the safe of the model
     * @return A new button to reflect the type of block at location r,c in the safe oof the model
     */
    private Button makeButton(char displayVal, int r, int c){
        Button button = new Button();
        Image Img = null;
        //make the button have a different image depending on the block type at r,c in the safe of the model
        switch (displayVal){
            case '.':
                Img = new Image(getClass().getResourceAsStream("resources/white.png"));
                button.setOnMouseClicked(event -> {
                    Image ImgTest = new Image(getClass().getResourceAsStream("resources/laser.png"));
                    model.add(c,r);
                    updateGUISafe();
                    ImageView test = new ImageView(ImgTest);
                    button.setGraphic(test);
                    info.setText(model.output);
                });
                break;
            case '*':
                Img = new Image(getClass().getResourceAsStream("resources/beam.png"));
                button.setOnMouseClicked(event -> {
                    Image ImgTest = new Image(getClass().getResourceAsStream("resources/laser.png"));
                    model.add(c,r);
                    updateGUISafe();
                    ImageView test = new ImageView(ImgTest);
                    button.setGraphic(test);
                    info.setText(model.output);
                });
                break;
            case 'X':
                Img = new Image(getClass().getResourceAsStream("resources/pillarX.png"));
                button.setOnMouseClicked(event -> {
                    model.add(c,r);
                    info.setText(model.output);
                });
                break;
            case 'L':
                Img = new Image(getClass().getResourceAsStream("resources/laser.png"));
                button.setOnMouseClicked(event -> {
                    Image ImgTest = new Image(getClass().getResourceAsStream("resources/white.png"));
                    model.remove(c,r);
                    updateGUISafe();
                    ImageView test = new ImageView(ImgTest);
                    button.setGraphic(test);
                    info.setText(model.output);
                });
                break;
            case '0':
                Img = new Image(getClass().getResourceAsStream("resources/pillar0.png"));
                button.setOnMouseClicked(event -> {
                    model.add(c,r);
                    info.setText(model.output);
                });
                break;
            case '1':
                Img = new Image(getClass().getResourceAsStream("resources/pillar1.png"));
                button.setOnMouseClicked(event -> {
                    model.add(c,r);
                    info.setText(model.output);
                });
                break;
            case '2':
                Img = new Image(getClass().getResourceAsStream("resources/pillar2.png"));
                button.setOnMouseClicked(event -> {
                    model.add(c,r);
                    info.setText(model.output);
                });
                break;
            case '3':
                Img = new Image(getClass().getResourceAsStream("resources/pillar3.png"));
                button.setOnMouseClicked(event -> {
                    model.add(c,r);
                    info.setText(model.output);
                });
                break;
            case '4':
                Img = new Image(getClass().getResourceAsStream("resources/pillar4.png"));
                button.setOnMouseClicked(event -> {
                    model.add(c,r);
                    info.setText(model.output);
                });
                break;
        }
        //make the image of the button shown
        ImageView Icon = new ImageView(Img);
        button.setGraphic(Icon);
        setButtonBackground(button,"white.png");
        return button;
    }

}
