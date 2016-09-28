package gui;

import backtracking.Backtracker;
import backtracking.Configuration;
import backtracking.SafeConfig;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import model.LasersModel;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Optional;

/**
 * The
 * @author: Michael Johansen
 * @author: Daniel Jones
 */
public class ControllerGUI {
    //instance variable for the model
    private LasersModel model;

    //instance variable for the view
    private LasersGUI gui;

    public ControllerGUI(LasersModel model, LasersGUI gui){
        this.model = model;
        this.gui = gui;
    }

    /**
     * The check function for the check button.
     * Checks the validity of the model, and reflects on the view whether or not the model is a correct solution.
     * If it is not a correct solution, the view will show the first block that makes the solution incorrect.
     */
    public  void check(){
        //checks if the model is valid
        if(!gui.model.verify()){
            //if not look make the first block that causes it to not be invalid red.
            GridPane safeGrid = (GridPane) gui.background.getCenter();
            Button errorRec = (Button) getNode(gui.model.verifyRow,gui.model.verifyCol,safeGrid);
            if(model.getblock(gui.model.verifyRow,gui.model.verifyCol).getCharToDisplay()=='.'){
                Image img = new Image(getClass().getResourceAsStream("resources/red.png"));
                ImageView imgView = new ImageView(img);
                errorRec.setGraphic(imgView);
            }
            gui.setButtonBackground(errorRec,"red.png");
        }
        //inform the user of the result
        gui.info.setText(model.output);
    }

    /**
     * The solve function for the solve button.
     * Takes the current safe configuration and will attempt to solve that configuration.
     * If it is successful, it will make the view show the correct safe configuration.
     * If it is incorrect It will inform the user that there is no solution to the current configuration.
     * @param safeConfig The safe configuration to attempt to solve.
     */
    public void solve(SafeConfig safeConfig){
        Backtracker bt = new Backtracker(false);
        //attempt to solve the safe
        Optional<Configuration> sol = bt.solve(safeConfig);
        //if there is a solution, show it on the view
        if(sol.isPresent()){
            SafeConfig solConfig = (SafeConfig) sol.get();
            this.model = solConfig.getSafe();
            gui.model = this.model;
            gui.updateGUISafe();
        }
        //if not, inform the user.
        else {
            gui.info.setText(gui.filename.substring(gui.filename.lastIndexOf('\\')+1) + " has no solution.");
        }
    }

    /**
     * Load a new safe file.
     * @param safeConfig The current safe configuration to be replaced by the newly loaded one.
     */
    public void load(SafeConfig safeConfig){
        //open a file chooser to allow the user to select a file
        FileChooser fc = new FileChooser();
        fc.setTitle("Choose a safe file.");
        File selectedFile = fc.showOpenDialog(gui.pStage);
        gui.filename = selectedFile.toString();
        try {
            safeConfig = new SafeConfig(gui.filename);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            model = new LasersModel(gui.filename);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        //load in the new file and update the models information to the new safe
        gui.model = this.model;
        gui.safeConfig = safeConfig;
        gui.updateGUISafe();
        gui.info.setText(gui.filename.substring(gui.filename.lastIndexOf('\\')+1) + " loaded.");
    }

    /**
     * restart the safe without any input, and update the view to reflect that.
     */
    public void restart(){
        //remake the model
        try {
            model = new LasersModel(gui.filename);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        //update the view to the new model
        gui.model = this.model;
        gui.updateGUISafe();
        gui.info.setText(gui.filename.substring(gui.filename.lastIndexOf('\\')+1) + " loaded.");
    }

    /**
     * Sets the model to the next valid successor towards completion if it exists
     * @param safeConfig a safeConfiguration to find the successor for
     */
    public void hint(SafeConfig safeConfig){
        //A Backtracker to find the full path returns to the List of configurations
        Backtracker bt = new Backtracker(false);
        List<Configuration> path = bt.solveWithPath(safeConfig);
        //If the List is null that means no goal was reached and there wasn't a valid solution
        //moving forward from the current configuration
        if(path == null){
            gui.info.setText("Not a part of a valid solution");
            //gui.info.setText(model.output);
        }
        //This is the solution
        else if(path.size() == 0){
            gui.info.setText("Safe is fully verified!");
        }
        else {
            SafeConfig next;
            //Iterate through the path list to find the next addition of a laser
            int i;
            for (i = 0; i < path.size(); i++) {
                next = (SafeConfig) path.get(i);
                //Find the next configuration that is not identical to the starting laser.
                if (!sameConfig(safeConfig, next)) {
                    //Sets the model to the current safe from the next placed laser
                    this.model = next.getSafe();
                    gui.model = this.model;
                    gui.info.setText("Laser added at: (" + next.getCurrentRow() + ", " + next.getCurrentCol() + ")");
                    gui.updateGUISafe();
                    break;
                }
            }
        }
    }

    /**
     * Sees if two SafeConfigs' safes are identical. A product of the backtracking algorithm that will
     * leave tiles empty.
     * @param config1 the first SafeConfig to compare
     * @param config2 the second SafeConfig to compare
     * @return true if they are identical, false otherwise
     */
    private boolean sameConfig(SafeConfig config1, SafeConfig config2){
        //Get the safes from the two
        LasersModel safe1 = config1.getSafe();
        LasersModel safe2 = config2.getSafe();
        int totalRows = safe1.getRows();
        int totalCols = safe1.getCols();
        //Just to check, if the dimensions are different there is no way the two are identical
        if( totalRows != safe2.getRows() || totalCols != safe2.getCols() ){
            return false;
        }
        //Check every tile and see if the character on the corresponding tiles are the same
        for(int r = 0; r < totalRows; r++){
            for(int c = 0; c < totalCols; c++){
                if(safe1.getblock(r,c).getCharToDisplay() != safe2.getblock(r,c).getCharToDisplay()){
                    return false;
                }
            }
        }
        //If they are all the same then the two safes are identical
        return true;
    }

    /**
     * get a node from the button grid.
     * @param row row to access
     * @param column column to access
     * @param gridPane the button grid to access
     * @return the node found.
     */
    public Node getNode(final int row, final int column, GridPane gridPane) {
        List<Node> children = gridPane.getChildren();
        Node res = null;
        //look through all the children to find the target node
        for(Node node : children) {
            if(gridPane.getColumnIndex(node) == column && gridPane.getRowIndex(node) == row ) {
                res = node;
                break;
            }
        }
        return res;
    }

}