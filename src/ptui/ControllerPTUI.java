package ptui;

import model.LasersModel;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * This class represents the controller portion of the plain text UI.
 * It takes the model from the view (LasersPTUI) so that it can perform
 * the operations that are input in the run method.
 *
 * @author Sean Strout @ RIT CS
 * @author Daniel Jones
 * @author Michael Johansen
 */
public class ControllerPTUI  {
    /** The UI's connection to the model */
    private LasersModel model;

    /**
     * Construct the PTUI.  Create the model and initialize the view.
     * @param model The laser model
     */
    public ControllerPTUI(LasersModel model) {
        this.model = model;
    }

    /**
     * Run the main loop.  This is the entry point for the controller
     * @param inputFile The name of the input command file, if specified
     * @throws FileNotFoundException if no file is found
     */
    public void run(String inputFile) throws FileNotFoundException{
        //the scanner for the standard input
        Scanner standardInput = new Scanner(System.in);

        //Scanner variable for switching from file to standard input
        Scanner input;

        //There is only one file input for the safe. Purely run interactively
        if(inputFile == null){
            input = standardInput;
        }
        //If there is a commands file, sets the scanner to that file first
        else {
            input = new Scanner(new File(inputFile));
        }

        //print out the initial safe configuration
        System.out.print(this.model.to_string());
        //User input prompt
        LasersPTUI.displayPrompt();

        boolean run = true;
        while(run) {
            String userInput = input.nextLine();
            //Checks to make sure there was a user input in the line. Otherwise loops back.
            if (!userInput.isEmpty()) {
                //Checks first character only. Commands could be single characters or full words
                switch (userInput.charAt(0)) {
                    case 'a':
                        String[] commands = userInput.split(" ");
                        //Prints the command given if given by a file.
                        if(input!=standardInput){
                            System.out.println(userInput);
                        }
                        //too many or not enough coordinates provided
                        if (commands.length != 3) {
                            System.out.println("Incorrect coordinates");
                            LasersPTUI.displayPrompt();
                            break;
                        }
                        this.model.add(Integer.parseInt(commands[1]), Integer.parseInt(commands[2]));
                        //System.out.print(this.model.to_string());
                        break;
                    case 'd':
                        //Prints the command given if given by a file.
                        if(input!=standardInput){
                            System.out.println(userInput);
                        }
                        System.out.print(this.model.to_string());
                        LasersPTUI.displayPrompt();
                        break;
                    case 'h':
                        //Prints the command given if given by a file.
                        if(input!=standardInput){
                            System.out.println(userInput);
                        }
                        LasersPTUI.displayHelp();
                        LasersPTUI.displayPrompt();
                        break;
                    case 'q':
                        if(input!=standardInput){
                            System.out.println(userInput);
                        }
                        run = false;
                        break;
                    case 'r':
                        commands = userInput.split(" ");
                        //Prints the command given if given by a file.
                        if(input!=standardInput){
                            System.out.println(userInput);
                        }
                        //too many or not enough coordinates provided
                        if (commands.length != 3) {
                            System.out.println("Incorrect coordinates");
                            LasersPTUI.displayPrompt();
                            break;
                        }
                        this.model.remove(Integer.parseInt(commands[1]), Integer.parseInt(commands[2]));
                        break;
                    case 'v':
                        //Prints the command given if given by a file.
                        if(input!=standardInput){
                            System.out.println(userInput);
                        }
                        this.model.verify();
                        break;
                    default:
                        //Prints the command given if given by a file.
                        if(input!=standardInput){
                            System.out.println(userInput);
                        }
                        System.out.println("Unrecognized command: " + userInput);
                        LasersPTUI.displayPrompt();
                        break;
                }

            }

            //the current input is by file
            if (input != standardInput) {
                //is the end of the file and time to switch back to standard input
                if(!input.hasNextLine()){
                    input = standardInput;
                }
            }
        }
        input.close();
    }


}
