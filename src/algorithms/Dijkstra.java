package algorithms;

import model.Maze;
import gui.MazeViewer;

import java.util.HashMap;
import java.util.ArrayList;

/**
 * Class holds the Dijkstra
 * Shortest Path algorithm
 * and calls the PathTrace
 * when the algorithm has
 * completed.
 * @author Matt Stetter
 */
public class Dijkstra {

    /** holds the queue of considered Nodes */
    private static ArrayList<Maze.Node> queue = new ArrayList<>();

    /** holds the considered Nodes for efficient .contains() */
    private static HashMap<Maze.Node, Integer> hashQueue = new HashMap<>();

    /** holds the Nodes that are finalized */
    private static HashMap<Maze.Node, Maze.Node> finalized = new HashMap<>();

    /**  */
    private static Maze.Node startNode, finalNode;

    private static Maze maze;

    public static void solve ( String inputFileName, String outputFileName, boolean showPlot ) {

        double startMazeTime = System.currentTimeMillis();

        maze = new Maze( inputFileName, outputFileName );

        System.out.printf("Created node map: Finished in %.5f seconds.\n", (System.currentTimeMillis() - startMazeTime) / 1000.0);

        startNode = maze.getStart();
        finalNode = maze.getFinish();

        startNode.setPossible( startNode );
        startNode.setDistance(0);

        double startPathTime = System.currentTimeMillis();

        _solve( );

        System.out.printf("Finalized nodes: Finished in %.5f seconds.\n", (System.currentTimeMillis() - startPathTime) / 1000.0);

        double startTraceTime = System.currentTimeMillis();

        PathTrace.trace( finalized, outputFileName, maze );

        System.out.printf("Traced node path: Finished in %.5f seconds.\n", (System.currentTimeMillis() - startTraceTime) / 1000.0);

        if ( showPlot ) {

            MazeViewer.showTrace( outputFileName );

        }
    }

    private static void _solve () {

        Maze.Node cur = maze.getStart();

        while ( finalized.size() < maze.getNodeCount() && !finalized.containsKey( finalNode ) ) {

            if ( hashQueue.containsKey( cur ) ) {

                queue.remove( cur );

                hashQueue.remove( cur );

            }

//            System.out.println( finalized.size() + " " + maze.getNodeCount() );

            ArrayList<Maze.Node> considered = new ArrayList<>();

            for (int i = 0; i < 4; i++) {
                if (cur.getNeighbor(i) != null) {
                    considered.add(cur.getNeighbor(i));
                }
            }

            // weird possible edge case with completely disconnected Node
            if (considered.isEmpty()) {
                finalized.put(cur, null);
                continue;
            }

            ArrayList<Maze.Node> nonFinalizedNodes = new ArrayList<>();

            for (Maze.Node n : considered) {

                if (!finalized.containsKey(n)) {

                    nonFinalizedNodes.add(n);

                }

            }

            if ( nonFinalizedNodes.size() < 1 ) {

                finalized.put( cur, cur.getPossible() );

//                System.out.println( cur + " committed because dead end");

                if ( queue.size() > 0 ) {

//                    System.out.println( "Removing " + queue.get( queue.size() - 1 ) );

                    int smallestDistance = Integer.MAX_VALUE;

                    int smallestIndex = -1;

                    for ( int i = 0; i < queue.size(); i++ ) {

                        if ( queue.get(i).getDistance() < smallestDistance ) {

                            smallestIndex = i;

                            smallestDistance = queue.get(i).getDistance();

                        }

                    }

                    cur = queue.remove( smallestIndex );

                    hashQueue.remove( cur );

                }

                continue;

            }

            for (Maze.Node n : nonFinalizedNodes) {

                int distance;

                if (n.getXPos() != cur.getXPos()) {

                    distance = Math.abs(n.getXPos() - cur.getXPos()) + cur.getDistance();

                } else {

                    distance = Math.abs(n.getYPos() - cur.getYPos()) + cur.getDistance();

                }

                if (distance < n.getDistance()) {

                    n.setDistance(distance);

                    n.setPossible(cur);

                }
            }

//            System.out.println( cur + " committed because its neighbors are touched.");

            finalized.put(cur, cur.getPossible());

            cur = nonFinalizedNodes.get(0);

            if ( nonFinalizedNodes.size() > 1 ) {

                for ( int i = 1; i < nonFinalizedNodes.size(); i++ ) {

//                    System.out.println("Adding " + nonFinalizedNodes.get(i));

                    queue.add( nonFinalizedNodes.get( i ) );

                    hashQueue.put( nonFinalizedNodes.get( i ), queue.size() - 1 );

                }
            }
        }
    }
}
