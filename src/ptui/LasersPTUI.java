package ptui;

import java.io.FileNotFoundException;
import java.util.Observable;
import java.util.Observer;

import model.LasersModel;

/**
 * This class represents the view portion of the plain text UI.  It
 * is initialized first, followed by the controller (ControllerPTUI).
 * You should create the model here, and then implement the update method.
 *
 * @author Sean Strout @ RIT CS
 * @author Daniel Jones
 * @author Michael Johansen
 */
public class LasersPTUI implements Observer {
    /** The UI's connection to the model */
    private LasersModel model;

    /**
     * Construct the PTUI.  Create the model and initialize the view.
     * @param filename the safe file name
     * @throws FileNotFoundException if file not found
     */
    public LasersPTUI(String filename) throws FileNotFoundException {
        try {
            this.model = new LasersModel(filename);
        } catch (FileNotFoundException fnfe) {
            System.out.println(fnfe.getMessage());
            System.exit(-1);
        }
        this.model.addObserver(this);
    }

    public LasersModel getModel() { return this.model; }

    /**
     * Prints out a manual for the commands of the safe
     */
    public static void displayHelp(){
        System.out.println("a|add r c: Add laser to (r,c)\n" +
                "d|display: Display safe\n" +
                "h|help: Print this help message\n" +
                "q|quit: Exit program\n" +
                "r|remove r c: Remove laser from (r,c)\n" +
                "v|verify: Verify safe correctness");

    }

    /**
     * Prints out the safe
     */
    public void displaySafe(){
        System.out.print(this.model.to_string());
    }

    /**
     * Prints out the prompt for user input
     */
    public static void displayPrompt(){
        System.out.print("> ");
    }

    /**
     * Display the message from user interactions
     */
    public void displayMessage(){
        System.out.println(this.model.output);
    }

    @Override
    public void update(Observable o, Object arg) {
        displaySafe();
        displayMessage();
        displayPrompt();
    }
}
