package model;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.*;
import java.util.ArrayList;


/**
 * Class represents an input maze.
 * It has an internal class Node that
 * is used to construct a Node map of the
 * maze.
 * @author Matt Stetter
 */
public class Maze {

    /**
     * Class represents a Node or
     * a crucial point in the maze
     * that is stored in a map.
     */
    public static class Node {

        /** holds the x an y position of the Node */
        private int xPos, yPos;

        /** holds the neighbor Nodes that connect to the Node */
        public ArrayList<Node> neighbors = new ArrayList<>(4);

        /** holds the distance to the most recent Node (for Dijkstra) */
        private int distance = Integer.MAX_VALUE;

        /** holds the node its tentatively connected to */
        private Node possible;

        /**
         * Constructor for the Node
         * that holds the x and y
         * position of the Node and
         * allocates an ArrayList of
         * neighbors with null.
         * @param xPos x position
         * @param yPos y position
         */
        public Node ( int xPos, int yPos ) {

            // x and y position
            this.xPos = xPos;
            this.yPos = yPos;

            // allocating the neighbors as null
            for (int i = 0; i < 4; i++) {
                this.neighbors.add( null );
            }

        }

        /**
         * Returns the x position
         * @return x position
         */
        public int getXPos () {
            return this.xPos;
        }

        /**
         * Returns the y position
         * @return y position
         */
        public int getYPos () {
            return this.yPos;
        }

        /**
         * Returns the neighbor at
         * a given cardinal direction
         * (index)
         * @param index the direction,
         *              0 : north
         *              1 : east
         *              2 : south
         *              3 : west
         * @return neighbor node at
         *          the specified direction
         */
        public Node getNeighbor ( int index ) {
            return this.neighbors.get(index);
        }

        /**
         * Sets the neighbor of a Node
         * in the given cardinal direction
         * @param index direction
         * @param neighbor neighbor Node
         */
        public void setNeighbor ( int index, Node neighbor ) {
            this.neighbors.set( index, neighbor );
        }

        /**
         * Returns the distance
         * from the recent Node
         * (for Dijkstra)
         * @return distance
         */
        public int getDistance () {
            return this.distance;
        }

        /**
         * Sets the distance
         * from the recent Node
         * @param distance distance
         */
        public void setDistance ( int distance ) {
            this.distance = distance;
        }

        /**
         * Gets the tentative distance
         * value for the Node to the
         * smallest distance Node
         * @return distance
         */
        public Node getPossible () {
            return this.possible;
        }

        /**
         * Sets the tentative distance
         * value for the Node to the
         * smallest distance Node
         * @param possible distance
         */
        public void setPossible ( Node possible ) {
            this.possible = possible;
        }

        /**
         * toString override, returns
         * the x and y position of the
         * node.
         * @return x and y pos
         */
        @Override
        public String toString() {
            return "Node at " + this.xPos + " " + this.yPos;
        }

        /**
         * hashCode override, returns
         * a number representing the
         * x and y position of
         * a Node (ex. Node at
         *          1 3 returns 13)
         * @return hash code
         */
        @Override
        public int hashCode() {
            return Integer.parseInt(this.xPos + "" + this.yPos);
        }

        /**
         * equals override, returns
         * true if the hashCodes are
         * the same for the two objects
         * @param e other object
         * @return boolean
         */
        @Override
        public boolean equals ( Object e ) {
            return this.getXPos() == ((Node) e).getXPos()
                    && this.getYPos() == ((Node) e).getYPos();
        }
    }

    /** holds a 2d array of the image pixels (0 for black, 1 for white) */
    private int[][] imagePixels;

    /** holds the height and width of the maze */
    private int height, width;

    /** holds the start and finish node of the maze */
    private Node start, finish;

    /** holds the counter of the number of nodes in the image */
    private int nodeCount;

    /**
     * Constructor for the Maze.
     * Loads the image file and
     * creates a Node map of the
     * Maze.
     * @param imageName the name of the file
     */
    public Maze ( String imageName, String outImageName ) {

        // attempts to load BufferedImage from file name
        BufferedImage image = null;
        try {
            image = ImageIO.read( new File ( imageName ) );
        } catch ( IOException e ) {
            System.err.println("Error: File: " + imageName + " could not be found.");
            return;
        }

        // saves the height and width of the image
        this.height = image.getHeight();
        this.width = image.getWidth();

        // attempts to create Raster of the image data
        Raster imageData = null;
        try {
            imageData = image.getData();
        } catch ( NullPointerException e ) {
            System.err.println("Error: File: " + imageName + " is blank.");
            return;
        }

        // gets an array of the image data
        int[] pixelArray = imageData.getPixels( 0, 0, image.getWidth(), image.getHeight(), new int[ image.getWidth() * image.getHeight() ] );

        // loads the array into a 2d array of image data
        this.imagePixels = new int[image.getHeight()][image.getWidth()];
        int count = 0;
        for (int y = 0; y < imageData.getHeight(); y++) {
            for (int x = 0; x < imageData.getWidth(); x++) {

                this.imagePixels[y][x] = pixelArray[count];
                ++count;

            }
        }

        /**
         * Uses InputStream and
         * OutputStream to load the contents
         * of the initial file to the
         * output file.
         */
        InputStream is = null;
        OutputStream os = null;
        try {
            is = new FileInputStream( new File ( imageName ) );
            os = new FileOutputStream( new File ( outImageName ) );
            byte[] buffer = new byte[1024];
            int length;
            try {
                while ((length = is.read(buffer)) > 0) {
                    os.write( buffer, 0, length );
                }
            } catch ( IOException e ) {
                System.err.println("Failure to copy file.");
                return;
            }
        } catch ( FileNotFoundException e ) {
            System.err.println("Failure to find file.");
            return;
        }

        // calls to make Node map
        this.getNodeMap();
    }

    /**
     * Creates the node map of
     * the given image data.
     * Saves the start and finish
     * node which can be used via their
     * neighbors to construct a node
     * graph of the maze.
     */
    public void getNodeMap () {

        /**
         * Allocates an ArrayList the size
         * of the width of the image with
         * null. This list will contain
         * the top layer nodes that have
         * blank spaces below them such that
         * lower nodes can connect to them.
         */
        ArrayList<Node> topNodes = new ArrayList<>(this.width);
        for ( int i = 0; i < this.width; i++ ) {
            topNodes.add( null );
        }

        // finds the start Node and saves it
        // (assumes start node at top)
        for ( int x = 1; x < this.width; x++ ) {

            if (imagePixels[0][x] > 0) {

                Node n = new Node( x, 0 );

                this.start = n;

                topNodes.set( x, n );

                this.nodeCount += 1;

                break;

            }
        }

        /**
         * Iterates through image data
         * and decides at each pixel if a
         * Node should be placed there.
         * Afterwards, if a Node was placed,
         * it decides if the Node should link
         * to Nodes above it, or get saved as
         * a top Node for Nodes below it
         * to connect.
         */
        for ( int y = 1; y < this.height - 1; y++ ) {

            // buffered previous, current, and next pixel value to optimize
            boolean prv = false;
            boolean cur = false;
            boolean nxt = this.imagePixels[y][1] > 0;

            // represents the node to the left of the current x position
            Node leftNode = null;

            for ( int x = 1; x < this.width - 1; x++ ) {

                // increments the previous, current, and next values
                prv = cur;
                cur = nxt;
                nxt = imagePixels[y][x + 1] > 0;

                // initializes the current Node
                Node n = null;

                // if the current pixel is a wall, move on
                if ( !cur ) {
                    continue;
                }

                if ( prv ) {

                    // OPEN, OPEN, OPEN
                    if ( nxt ) {

                        // only place a node if the top or bottom is not a wall
                        if (imagePixels[y-1][x] > 0
                            || imagePixels[y+1][x] > 0) {

                            n = new Node ( x, y );

                            // linking to the leftNode
                            if ( leftNode != null ) {

                                n.setNeighbor(3, leftNode);

                                leftNode.setNeighbor(1, n);


                                leftNode = n;

                            }

                        }

                    // OPEN, OPEN, WALL. place a Node because the right is a wall
                    } else {

                        n = new Node ( x, y );

                        // linking to the leftNode
                        if ( leftNode != null ) {

                            n.setNeighbor( 3, leftNode );

                            leftNode.setNeighbor( 1, n );

                        }

                    }

                } else {

                    // WALL, OPEN, OPEN. place a Node because the left is a wall
                    if ( nxt ) {

                        n = new Node ( x, y );

                        leftNode = n;

                    // WALL, OPEN, WALL. place a Node if the top or bottom is a wall (dead end)
                    } else {

                        if (imagePixels[y + 1][x] == 0
                            || imagePixels[y - 1][x] == 0) {

                            n = new Node ( x, y );

                        }

                    }

                }

                // if a Node has been created
                if ( n != null ) {

                    // if the top is empty, try to link to the topNode
                    if ( imagePixels[y - 1][x] > 0 ) {

                        Node top = topNodes.get( x );

                        top.setNeighbor( 2, n );

                        n.setNeighbor(  0, top );

                    }

                    // if the bottom is empty, set Node as topNode
                    if ( imagePixels[y + 1][x] > 0 ) {

                        topNodes.set( x, n );

                    // otherwise, delete the current topNode at the position
                    } else {

                        topNodes.set( x, null );

                    }

                    this.nodeCount += 1;

                }

            }

        }

        // finds the finish node, saves it, and connects it to the topNode
        for (int x = 1; x < this.width - 1; x++) {

            if ( imagePixels[this.height - 1][x] > 0) {

                this.finish = new Node( x, this.height - 1 );

                Node top = topNodes.get( x );

                top.setNeighbor(  2,  this.finish );

                this.finish.setNeighbor( 0, top );

                this.nodeCount += 1;

                break;

            }

        }
    }

    /**
     * Returns the starting
     * Node of the Maze
     * @return Start Node
     */
    public Node getStart () {
        return this.start;
    }

    /**
     * Returns the finishing
     * Node of the Maze
     * @return Finish Node
     */
    public Node getFinish () {
        return this.finish;
    }

    /**
     * Returns the total of
     * the Nodes in the Maze
     * @return Node total
     */
    public int getNodeCount () {
        return this.nodeCount;
    }
}
