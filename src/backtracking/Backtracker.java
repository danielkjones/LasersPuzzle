package backtracking;

import java.awt.image.AreaAveragingScaleFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

/**
 * This class represents the classic recursive backtracking algorithm.
 * It has a solver that can take a valid configuration and return a
 * solution, if one exists.
 *
 * This file comes from the backtracking lab. It should be useful
 * in this project. A second method has been added that you should
 * implement.
 *
 * @author Sean Strout @ RIT CS
 * @author James Heliotis @ RIT CS
 * @author Daniel Jones
 * @author Michael Johansen
 */
public class Backtracker {

    private boolean debug;

    /**
     * An ArrayList to hold the path of configurations to reach a verified solution
     */
    private List<Configuration> path = null;

    /**
     * Initialize a new backtracker.
     *
     * @param debug Is debugging output enabled?
     */
    public Backtracker(boolean debug) {
        this.debug = debug;
        if (this.debug) {
            System.out.println("Backtracker debugging enabled...");
        }
    }

    /**
     * A utility routine for printing out various debug messages.
     *
     * @param msg    The type of config being looked at (current, goal,
     *               successor, e.g.)
     * @param config The config to display
     */
    private void debugPrint(String msg, Configuration config) {
        if (this.debug) {
            System.out.println(msg + ":\n" + config);
        }
    }

    /**
     * Try find a solution, if one exists, for a given configuration.
     *
     * @param config A valid configuration
     * @return A solution config, or null if no solution
     */
    public Optional<Configuration> solve(Configuration config) {
        debugPrint("Current config", config);
        if (config.isGoal()) {
            debugPrint("\tGoal config", config);
            return Optional.of(config);
        } else {
            for (Configuration child : config.getSuccessors()) {
                if (child.isValid()) {
                    debugPrint("\tValid successor", child);
                    Optional<Configuration> sol = solve(child);
                    if (sol.isPresent()) {
                        return sol;
                    }
                } else {
                    debugPrint("\tInvalid successor", child);
                }
            }
            // implicit backtracking happens here
        }
        return Optional.empty();
    }

    /**
     * Find a goal configuration if it exists, and how to get there.
     *
     * @param config the starting configuration
     * @return a list of configurations to get to a goal configuration.
     * If there are none, return null.
     */
    public List<Configuration> solveWithPath(Configuration config) {

        debugPrint("Current config", config);
        if (config.isGoal()) {
            debugPrint("\tGoal config", config);
            //If there is a solution make an ArrayList and start filling it with configurations
            this.path = new ArrayList<>();
            //this.path.add(0,config);
            return path;
        } else {
            for (Configuration child : config.getSuccessors()) {
                if (child.isValid()) {
                    debugPrint("\tValid successor", child);
                    solveWithPath(child);
                    //If the path ArrayList exits a solution was found
                    if(this.path != null) {
                        this.path.add(0, child);
                        return this.path;
                    }
                } else {
                    debugPrint("\tInvalid successor", child);
                }
            }
            // implicit backtracking happens here
        }
        return null;
    }
}
