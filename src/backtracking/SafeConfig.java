package backtracking;

import model.LasersModel;
import model.Block;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;

/**
 * The class represents a single configuration of a safe.  It is
 * used by the backtracker to generate successors, check for
 * validity, and eventually find the goal.
 *
 * This class is given to you here, but it will undoubtedly need to
 * communicate with the model.  You are free to move it into the model
 * package and/or incorporate it into another class.
 *
 * @author Sean Strout @ RIT CS
 * @author Daniel Jones
 * @author Michael Johansen
 */
public class SafeConfig implements Configuration {

    private LasersModel safe;
    //Keeps track of the coordinates of the tile to be edited
    private int currentRow;
    private int currentCol;

    /**
     * A constructor that makes a SafeConfig from a safe file
     * @param filename file for safe information
     * @throws FileNotFoundException if file is not found
     */
    public SafeConfig(String filename) throws FileNotFoundException {
        try {
            this.safe = new LasersModel(filename);
        } catch (FileNotFoundException fnfe) {
            System.out.println(fnfe.getMessage());
            System.exit(-1);
        }
        this.currentRow = 0;
        this.currentCol = -1;
    }

    /**
     * A constructor that makes a SafeConfig from another SafeConfig
     * by making a deep copy.
     * @param other Another SafeFig
     */
    public SafeConfig(SafeConfig other){
        this.safe = new LasersModel(other.safe);
        this.currentRow = other.currentRow;
        this.currentCol = other.currentCol;
    }

    /**
     * A constructor that takes in another SafeConfig as well as different LaserModel.
     * A sort of combination of the two constructors above
     * @param model A LaserModel to make as a safe
     */
    public SafeConfig(LasersModel model){
        this.safe = new LasersModel(model);
        this.currentRow = 0;
        this.currentCol = -1;
    }

    /**
     * Gets an ArrayList of the next configurations. A configuration where a laser is
     * added to a tile, and a configuration where the tile is left empty. If the
     * end of the configuration is reached it may only return a single configuration.
     * @return An ArrayList of child configurations
     */
    @Override
    public Collection<Configuration> getSuccessors() {
        ArrayList<Configuration> successors = new ArrayList<>();
        //Make two children, deep copies of current configuration
        SafeConfig child1 = new SafeConfig(this);
        SafeConfig child2 = new SafeConfig(this);
        //Increments the tile location to be edited for the next generation
        child1.currentCol += 1;
        //The next spot is out of range in the columns
        if(child1.currentCol >= child1.safe.getCols()) {
            //Circulates to the next row
            child1.currentCol = child1.currentCol % child1.safe.getCols();
            child1.currentRow += 1;
        }
        //Attempts to add a laser to the current working spot
        boolean added = child1.safe.add(child1.currentRow, child1.currentCol);
        //If a laser could not be added there is a pillar or another laser in that spot.
        //Moves along to the next valid spot to place a laser
        while(!added){
            child1.currentCol += 1;
            //The next spot is out of range in the columns
            if(child1.currentCol >= child1.safe.getCols()){
                //Circulates to the next row
                child1.currentCol = child1.currentCol % child1.safe.getCols();
                child1.currentRow += 1;
            }

            //If the rows are out of range, then that means the last spots for the
            //safe are not valid spots to add a laser and this configuration can be
            //tested for validity
            if( child1.currentRow >= child1.safe.getRows() ){
                successors.add(child1);
                return successors;
            }
            //Try to add in the next spot
            added = child1.safe.add(child1.currentRow, child1.currentCol);
        }
        //The laser has been added, in the nearest valid spot. Match the second child's current
        //row and column values to the first child's
        child2.currentRow = child1.currentRow;
        child2.currentCol = child1.currentCol;

        //MJ - don't know if these if statements are necessary, I think it might help
        if(child1.isSafeValid()
                && child1.safe.checkLaserValidity(child1.currentRow,child1.currentCol)
                && child1.isValid()) {
            successors.add(child1);
        }
        if( child2.isSafeValid()
                && child2.isValid()) {
            successors.add(child2);
        }

        return successors;
    }

    /**
     * Depending on the point in the safe that the Safe has been edited up to,
     * either checks if the whole safe is valid if everything has been checked,
     * or just checks if a placed laser is in a valid position
     * @return true if the safe up to that point in editing is valid, false otherwise
     */
    @Override
    public boolean isValid() {
        //If the currentRow counter is equal to or exceeding the number of rows
        //in the safe, then the end of the safe has been reached
        if(this.currentRow >= this.safe.getRows()){
            return this.safe.verify();
        }
        //Makes a copy of the tile that's in question
        Block tileCopy = this.safe.getblock(this.currentRow, this.currentCol);
        //If the tile that is in question is a laser then it checks that the laser is valid
        if(tileCopy.getCharToDisplay() == LasersModel.LASER){
            return this.safe.checkLaserValidity(this.currentRow, this.currentCol);
        }
        return true;
    }


    public boolean isSafeValid(){
        for(int r=0;r<safe.getRows();r++){
            for(int c=0;c<safe.getCols();c++){
                if(safe.getblock(r,c).getCharToDisplay()==LasersModel.LASER){
                    if(!this.safe.checkLaserValidity(r,c)){
                        return false;
                    }
                }
                else if(Character.isDigit(safe.getblock(r,c).getCharToDisplay())){
                    if(!this.safe.checkPillarValidity(r,c,Character.getNumericValue(safe.getblock(r,c).getCharToDisplay()))){
                        return  false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * Checks to see if the safe is fully verified, and therefore the goal
     * @return true if fully verified, false if no
     */
    @Override
    public boolean isGoal() {
        //When the currentRow counter is greater than or equal to the number of rows in the safe
        //the end of the safe has been reached, and is goal if the entire safe is valid.
        if(this.safe.verify()) {
            return true;
        }
        return false;
    }

    /**
     * Getter function for getting the LaserModel representation of the safe
     * @return the LaserModel
     */
    public LasersModel getSafe(){
        return this.safe;
    }

    /**
     * Getter function for the current row location
     * @return the current row location
     */
    public int getCurrentRow(){ return this.currentRow; }

    /**
     * Getter function for the current col location
     * @return the current col location
     */
    public int getCurrentCol(){ return this.currentCol; }
}
