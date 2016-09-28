package model;

/**
 * An object used to represent each location in a safe grid.
 * @author Daniel Jones
 * @author Michael Johansen
 */
public class Block {
    //numOfBeams ensures that we don't have to check for additional lasers when removing/adding beams
    private int numOfBeams;
    private char charToDisplay;

    /**
     * Constructor of a block class
     * @param inputChar The character that the block displays
     */
    public Block(char inputChar){
        this.charToDisplay = inputChar;
        this.numOfBeams = 0;
    }

    /**
     * Add a beam counter to a block, if the block displays an empty character
     * it will cause the block to display a laser character
     */
    public void addBeam(){
        this.numOfBeams+=1;
        if(this.numOfBeams>0 && this.charToDisplay=='.'){
            this.charToDisplay = '*';
        }
    }

    /**
     * Remove a beam counter from a block, if the block displays a beam character
     * and the beam counter of that block is zero, it displays an empty block.
     */
    public void removeBeam(){
        this.numOfBeams-=1;
        if(this.numOfBeams<1&&this.getCharToDisplay()=='*'){
            this.charToDisplay = '.';
            this.numOfBeams = 0;
        }
    }

    /**
     * Removes a laser at coordinates (r,c) from a safe. Also removes any beams that are associated with that block
     * @param safe The safe to remove the laser from.
     * @param r the row in which the laser is to be removed.
     * @param c the column in which the laser is to be removed.
     */
    public void removeLaser(Block[][] safe,int r,int c){
        //If the place the laser was previously in has a laser pointed at it, it will now display
        //that lasers beam
        if(safe[r][c].numOfBeams>0){
            safe[r][c].setCharToDisplay('*');
        }
        //if not, it will now display an empty block
        else {
            safe[r][c].setCharToDisplay('.');
        }
        //remove beam counters to all blocks over, under, to the left, and to the right of the laser that was
        //removed, until either the edge or a pillar is hit
        for(int i =r+1;i<safe.length;i++){
            if (safe[i][c].getCharToDisplay() == '.' || safe[i][c].getCharToDisplay() == '*' || safe[i][c].getCharToDisplay() == 'L') {
                safe[i][c].removeBeam();
            }
            else break;
        }
        for(int i =r-1; i>=0;i--){
            if (safe[i][c].getCharToDisplay() == '.' || safe[i][c].getCharToDisplay() == '*' || safe[i][c].getCharToDisplay() == 'L') {
                safe[i][c].removeBeam();
            }
            else break;
        }
        for(int i =c+1;i<safe[r].length;i++){
            if (safe[r][i].getCharToDisplay() == '.' || safe[r][i].getCharToDisplay() == '*' || safe[r][i].getCharToDisplay() == 'L') {
                safe[r][i].removeBeam();
            }
            else break;
        }
        for(int i =c-1; i>=0;i--){
            if (safe[r][i].getCharToDisplay() == '.' || safe[r][i].getCharToDisplay() == '*' || safe[r][i].getCharToDisplay() == 'L') {
                safe[r][i].removeBeam();
            }
            else break;
        }
    }

    /**
     * Adds a laser to a safe at the coordinates(r,c), and adds the beams that that laser would produce.
     * @param safe The safe array to add the laser to.
     * @param r the row in which to add the laser.
     * @param c the column which to add the laser.
     */
    public void addLaser(Block[][] safe,int r,int c){
        //makes the block at (r,c) display a laser
        safe[r][c].setCharToDisplay('L');

        //adds a beam counter to each block above, below, to the left and to the right of the laser
        //until either the edge of the safe or a pillar is hit
        for(int i =r+1;i<safe.length;i++){
            if (safe[i][c].getCharToDisplay() == '.' || safe[i][c].getCharToDisplay() == '*' || safe[i][c].getCharToDisplay() == 'L') {
                safe[i][c].addBeam();
            }
            else {
                break;
            }
        }
        for(int i =r-1; i>=0;i--){
            if (safe[i][c].getCharToDisplay() == '.' || safe[i][c].getCharToDisplay() == '*'|| safe[i][c].getCharToDisplay() == 'L') {
                safe[i][c].addBeam();
            }
            else {
                break;
            }
        }
        for(int i =c+1;i<safe[r].length;i++){
            if (safe[r][i].getCharToDisplay() == '.' || safe[r][i].getCharToDisplay() == '*' || safe[r][i].getCharToDisplay() == 'L') {
                safe[r][i].addBeam();
            }
            else {
                break;
            }
        }
        for(int i =c-1; i>=0;i--){
            if (safe[r][i].getCharToDisplay() == '.' || safe[r][i].getCharToDisplay() == '*' || safe[r][i].getCharToDisplay() == 'L') {
                safe[r][i].addBeam();
            }
            else {
                break;
            }
        }
    }

    /**
     * An accessor function to find what type of block this is.
     * @return The character that this block will display
     */
    public char getCharToDisplay(){
        return this.charToDisplay;
    }

    /**
     * A setter method to set what kind of block this is.
     * @param setter The character to make this block display
     */
    public void setCharToDisplay(char setter){
        this.charToDisplay = setter;
    }

    @Override
    public String toString() {
        return Character.toString(this.charToDisplay);
    }
}
