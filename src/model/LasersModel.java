package model;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Observable;
import java.util.Scanner;

/**
 * @author Daniel K Jones
 * @author Michael Johansen
 */

public class LasersModel extends Observable {

    /** variables for safe pieces, isDigit will also have to be used */
    public final static char EMPTY = '.';
    public final static char BEAM = '*';
    public final static char PILLAR = 'X';
    public final static char LASER = 'L';
    private int rows;
    private int cols;

    //for use with the gui
    public int verifyRow,verifyCol; //If verify fails, these will be the coordinates it fails at
    public String output;

    /** the 2 dimensional array representation of the safe */
    private Block[][] safe;

    public LasersModel(String filename) throws FileNotFoundException {
        File inputFile = new File(filename);
        Scanner in = new Scanner(inputFile);
        int counter = 0;
        while(in.hasNextLine()){
            String line = in.nextLine();
            char[] rawLine = line.toCharArray();
            String[] rawLineString = line.split(" ");

            if(counter==0){
                this.rows = Integer.parseInt(rawLineString[0]);
                this.cols = Integer.parseInt(rawLineString[1]);
                safe = new Block[rows][cols];
            }

            //This else if statement makes sure that we only pay attention to the useful information in the file.
            else if(counter<=rows){
                int lineIndex = 0;
                //The raw line includes spaces, the arrays should be without space characters
                for(int chr = 0; chr < rawLine.length; chr++){
                    if(rawLine[chr] != ' '){
                        //lineInfo[lineIndex++] = rawLine[chr];
                        this.safe[counter-1][lineIndex++] = new Block(rawLine[chr]);
                    }
                }
            }
            counter+=1;
        }
        in.close();
    }

    /**
     * A constructor to make a LasersModel from another LasersModel. A deep copy constructor.
     */
    public LasersModel(LasersModel other){
        this.rows = other.rows;
        this.cols = other.cols;
        this.safe = new Block[this.rows][this.cols];
        //Go through once and place the pillars of the safe and the empty spots of the safe. Not lasers.
        for(int r = 0; r < this.rows; r++){
            for(int c = 0; c < this.cols; c++){
                //The character in this block
                char blockChar = other.safe[r][c].getCharToDisplay();
                if(Character.isDigit(blockChar) || blockChar == PILLAR || blockChar == EMPTY){
                    this.safe[r][c] = new Block((blockChar));
                }
                //Put empty placeholders in the spots for the lasers
                else{
                    this.safe[r][c] = new Block(EMPTY);
                }

            }
        }
        //Go through once more and add the lasers. This in turn will add the beams without creating
        //issues
        for(int r = 0; r < this.rows; r++){
            for(int c = 0; c < this.cols; c++) {
                char blockChar = other.safe[r][c].getCharToDisplay();
                if (blockChar == LASER) {
                    //add a laser to this position of the safe
                    this.add(r, c);
                }
            }
        }

        this.output = other.output;
    }


    /**
     * Adds a new laser to the Safe in the location determined by row 'r' and column 'c'
     * @param r the row to place a laser
     * @param c the column to place a laser
     * @return true if a laser was added, false if a laser was not added
     */
    public boolean add(int r,int c){
        //Checks to made sure coordinates given are within range
        if( r >= this.rows || r < 0 || c >= this.cols || c < 0) {
            output = "Error adding laser at: (" + r + ", " + c + ")";
            announceChange();
            return false;
        }
        //checks to make sure the spot isn't occupied by a pillar or laser already
        //will only add to empty spots or spots with a beam (although invalid)
        else if( this.safe[r][c].getCharToDisplay() != EMPTY && this.safe[r][c].getCharToDisplay() != BEAM){
            output = "Error adding laser at: (" + r + ", " + c + ")";
            announceChange();
            return false;
        }
        //Errors have been handled. Laser is valid to be added
        else {
            this.safe[r][c].addLaser(this.safe,r,c);
            output = "Laser added at: (" + r + ", " + c + ")";
            announceChange();
            return true;
        }

    }

    /**
     * Removes a laser from the safe at the coordinate given by 'r' row and 'c' column.
     * If there is no laser at that coordinate point given an error message is printed.
     * @param r row coordinate of laser to be removed
     * @param c column coordinate of laser to be removed
     * @return true if a laser was removed, false if a laser was not removed
     */
    public boolean remove(int r,int c){
        //Checks to made sure coordinates given are within range
        if( r >= this.rows || r < 0 || c >= this.cols || c < 0) {
            output = "Error removing laser at: (" + r + ", " + c + ")";
            announceChange();
            return false;
        }
        //Checks if there is actually a laser to be removed at the coordinates given
        else if(this.safe[r][c].getCharToDisplay() != LASER){
            output = "Error removing laser at: (" + r + ", " + c + ")";
            announceChange();
            return false;
        }
        //There is in fact a laser to be removed
        else {
            this.safe[r][c].removeLaser(this.safe,r,c);
            output = "Laser removed at: (" + r + ", " + c + ")";
            announceChange();
            return true;
        }


    }

    /**
     * Verifies that the safe is valid and follows all of the restraints:
     * 1. Each file that is not a pillar must have either a laser or a beam over it
     * 2. Each pillar that requires a certain number of lasers must add up exactly
     * 3. Two or more lasers are not in the same row or column. (in sight of each other).
     * Prints either a message if the safe is valid or invalid
     * @return True if the safe is valid, False if it is invalid
     */
    public boolean verify(){
        for(int r=0;r<safe.length;r++){
            for(int c=0;c<safe[r].length;c++){
                if(safe[r][c].getCharToDisplay()==LASER){
                    if(!checkLaserValidity(r,c)){
                        verifyRow = r;
                        verifyCol = c;
                        output = "Error verifying at: (" + r + ", " + c + ")";
                        //System.out.println(output);
                        announceChange();
                        return false;
                    }
                }
                else if(safe[r][c].getCharToDisplay()==EMPTY){
                    verifyRow = r;
                    verifyCol = c;
                    output = "Error verifying at: (" + r + ", " + c + ")";
                    //System.out.println(output);
                    announceChange();
                    return false;
                }
                else if(Character.isDigit(safe[r][c].getCharToDisplay())){
                    int laserCounter =0;
                    if(r+1<safe.length){
                        if(safe[r+1][c].getCharToDisplay()=='L'){
                            laserCounter+=1;
                        }
                    }
                    if(r-1>=0){
                        if(safe[r-1][c].getCharToDisplay()=='L'){
                            laserCounter+=1;
                        }
                    }
                    if(c+1<safe[r].length){
                        if(safe[r][c+1].getCharToDisplay()=='L'){
                            laserCounter+=1;
                        }
                    }
                    if(c-1>=0){
                        if(safe[r][c-1].getCharToDisplay()=='L'){
                            laserCounter+=1;
                        }
                    }
                    if(laserCounter!=Character.getNumericValue(safe[r][c].getCharToDisplay())){
                        verifyRow = r;
                        verifyCol = c;
                        output = "Error verifying at: (" + r + ", " + c + ")";
                        //System.out.println(output);
                        announceChange();
                        return false;
                    }
                }
            }
        }
        output = "Safe is fully verified!";
        //System.out.println(output);
        announceChange();
        return true;
    }

    /**
     * Checks if a laser at a given coordinate does not conflict with any other lasers
     * in the safe. Checks in all four directions if there is a laser is in sight. If
     * there is a pillar in the way the laser is not in sight.
     * @param r the row coordinate for the laser
     * @param c the column coordinate for the laser
     * @return true if the laser is valid, false if it is not valid
     */
    public boolean checkLaserValidity(int r, int c){
        //Checks up the rows in the same column
        for(int i =r+1;i<this.safe.length;i++){
            if (this.safe[i][c].getCharToDisplay() == LASER) {
                return false;
            }
            else if(this.safe[i][c].getCharToDisplay() == PILLAR ||
                    Character.isDigit(this.safe[i][c].getCharToDisplay())){
                break;
            }
        }
        //Checks down the rows in the same column
        for(int i =r-1; i>=0;i--){
            if (this.safe[i][c].getCharToDisplay() == LASER) {
                return false;
            }
            else if(this.safe[i][c].getCharToDisplay() == PILLAR ||
                    Character.isDigit(this.safe[i][c].getCharToDisplay())){
                break;
            }
        }
        //Checks up the columns in the same row
        for(int i =c+1;i<this.safe[r].length;i++){
            if (this.safe[r][i].getCharToDisplay() == LASER) {
                return false;
            }
            else if(this.safe[r][i].getCharToDisplay() == PILLAR ||
                    Character.isDigit(this.safe[r][i].getCharToDisplay())){
                break;
            }
        }
        //Checks down the columns in the same row
        for(int i =c-1; i>=0;i--){
            if (this.safe[r][i].getCharToDisplay() == LASER) {
                return false;
            }
            else if(this.safe[r][i].getCharToDisplay() == PILLAR ||
                    Character.isDigit(this.safe[r][i].getCharToDisplay())){
                break;
            }
        }
        return true;
    }

    public boolean checkPillarValidity(int r, int c, int max){
        int counter =0;
        //Checks up the rows in the same column
        for(int i =r+1;(i<this.safe.length && i<r+2);i++){
            if (this.safe[i][c].getCharToDisplay() == LASER) {
                counter++;
                if(counter>max){
                    return false;
                }
            }
        }
        //Checks down the rows in the same column
        for(int i =r-1; (i>=0 && i>r-2);i--){
            if (this.safe[i][c].getCharToDisplay() == LASER) {
                counter++;
                if(counter>max){
                    return false;
                }
            }
        }
        //Checks up the columns in the same row
        for(int i =c+1;(i<this.safe[r].length && i<c+2);i++){
            if (this.safe[r][i].getCharToDisplay() == LASER) {
                counter++;
                if(counter>max){
                    return false;
                }
            }
        }
        //Checks down the columns in the same row
        for(int i =c-1; (i>=0 && i>c-2);i--){
            if (this.safe[r][i].getCharToDisplay() == LASER) {
                counter++;
                if(counter>max){
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Puts the safe into a plain text format for the user interface output
     * @return A string representation of the safe
     */
    public String to_string(){
        //New string to build off of for safe representation
        String display = " ";

        //The column coordinates
        for(int i = 0; i < this.cols; i++){
            //if the dimensions are double digits, reduce to the single digit spot
            display = display.concat(" " + String.valueOf(i % 10));
        }
        display = display.concat("\n  ");

        //The divider
        for(int i = 0; i < (this.cols * 2) - 1; i++){
            display = display.concat("-");
        }
        display = display.concat("\n");

        //The body of the output
        for(int row = 0; row < this.rows; row++){
            //The row coordinate and divider
            //if the dimensions are double digits, reduce to the single digit spot
            display = display.concat(String.valueOf(row % 10) + "|");
            //The actual safe representation accessed by row and column coordinates
            for(int col = 0; col < this.cols; col++){
                display = display.concat(String.valueOf(this.safe[row][col]));
                if(col != this.cols - 1){
                    display = display.concat(" ");
                }
            }

            display = display.concat("\n");

        }

        return display;
    }

    /**
     * getter function for the 2D array safe
     */
    public Block[][] getSafe(){return this.safe;}

    /**
     * Returns the block object for a particular tile in the safe
     * @param r the row
     * @param c the column
     * @return the block object at that coordinate in the safe
     */
    public Block getblock(int r,int c){return this.safe[r][c];}

    /**
     * getter function for the number of rows
     */
    public int getRows(){ return this.rows; }
    /**
     * getter function for the number of columns
     */
    public int getCols(){ return this.cols; }

    /**
     * A utility method that indicates the model has changed and
     * notifies observers
     */
    private void announceChange() {
        setChanged();
        notifyObservers();
    }
}
