package algorithms;

import model.Maze;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.Color;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.util.HashMap;
import java.io.File;

public class PathTrace {

    private static final int TRACE_COLOR = Color.RED.getRGB();

    private static HashMap<Maze.Node, Maze.Node> nodeMap;

    private static String outFileName;

    private static Maze nodeMaze;

    public static void trace ( HashMap<Maze.Node, Maze.Node> finalized, String outputFileName, Maze imageMaze ) {

        nodeMap = finalized;

        outFileName = outputFileName;

        nodeMaze = imageMaze;

        _trace();

    }

    private static void _trace (  ) {

        BufferedImage oldOutputSolution;

        try {
            oldOutputSolution = ImageIO.read( new File ( outFileName ) );
        } catch ( IOException e ) {
            System.out.println("Could not find output file.");
            return;
        }

        BufferedImage outputSolution = new BufferedImage( oldOutputSolution.getWidth(), oldOutputSolution.getHeight(), BufferedImage.TYPE_INT_ARGB );
        Graphics2D g = outputSolution.createGraphics();
        g.drawImage( oldOutputSolution, 0, 0, null );
        g.dispose();

        outputSolution.setRGB( nodeMaze.getStart().getXPos(), nodeMaze.getStart().getYPos(), TRACE_COLOR );

        Maze.Node cur = nodeMaze.getFinish();

        while ( !cur.equals( nodeMaze.getStart() ) ) {

            Maze.Node pathNode = nodeMap.get( new Maze.Node ( cur.getXPos(), cur.getYPos() ) );

            if ( cur.getXPos() == pathNode.getXPos() ) {

                if ( cur.getYPos() > pathNode.getYPos() ) {

                    for ( int i = cur.getYPos(); i > pathNode.getYPos(); i-- ) {


                        outputSolution.setRGB( cur.getXPos(), i, TRACE_COLOR );

                    }

                } else {

                    for ( int i = cur.getYPos(); i < pathNode.getYPos(); i++ ) {

                        outputSolution.setRGB( cur.getXPos(), i, TRACE_COLOR );

                    }

                }

            } else {

                if ( cur.getXPos() > pathNode.getXPos() ) {

                    for ( int i = cur.getXPos(); i > pathNode.getXPos(); i-- ) {

                        outputSolution.setRGB( i, cur.getYPos(), TRACE_COLOR );

                    }

                } else {

                    for ( int i = cur.getXPos(); i < pathNode.getXPos(); i++ ) {

                        outputSolution.setRGB( i, cur.getYPos(), TRACE_COLOR );

                    }

                }

            }

            cur = pathNode;

        }

        try {
            ImageIO.write( outputSolution, "png",  new File ( outFileName ) );
        } catch ( IOException e ) {
            System.err.println("Could not write to solution");
            return;
        }

    }

}
