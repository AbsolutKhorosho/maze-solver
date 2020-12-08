package main;

import algorithms.*;

/**
 * Main class for the Maze
 * solving program.
 * Usage: java MazeSolver input_file output_file algorithm --output
 * java MazeSolver --help
 * to get list of algorithms
 * @author Matt Stetter
 */
public class MazeSolver {

    /**
     * List of algorithms.
     * Only Dijkstra is
     * implemented as of now.
     */
    private static final String[] algorithms = {
            "dijkstra"
    };

    /**
     * Called by main to
     * choose which algorithm
     * to use and whether to show
     * the GUI png output.
     * @param args
     */
    public static void parseArguments ( String[] args ) {

        // true if plot was chosen
        boolean showPlot = args[3].equals("--show");

        // times the algorithm and print in seconds
        double startTime = System.currentTimeMillis();

        if ( args[2].equals("dijkstra") ) {

            Dijkstra.solve( args[0], args[1], showPlot );

        }

        System.out.printf("Complete operation: Finished in %.5f seconds\n",
                (System.currentTimeMillis() - startTime) / 1000.0);

    }

    /**
     * The main method
     * shows help output
     * and prints usage
     * error if the arguments
     * are not supplied.
     * @param args
     */
    public static void main ( String[] args ) {

        // filters out help command and usage errors
        if ( args.length == 1
                && ( args[0].equals( "--help" ) ) || args[0].equals( "--h" ) ) {

            System.out.println("Usage: java MazeSolver input_file output_file algorithm --show");

            System.out.println("Usable algorithms: ");

            for ( String algorithm : algorithms ) {

                System.out.println( algorithm );

            }

        } else if (args.length != 4) {

            System.err.println("Usage: java MazeSolver input_file output_file algorithm --show");
            return;
        }

        // sends arguments to helper method parseArguments
        parseArguments( args );

    }

}
