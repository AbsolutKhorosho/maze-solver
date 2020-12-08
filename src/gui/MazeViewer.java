package gui;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.io.File;

/**
 * GUI Viewer for a
 * PNG image
 */
public class MazeViewer extends Application {

    /** holds the loaded image */
    private ImageView loadImage;

    /** holds the output traced maze */
    private static String fileName;

    /**
     * Loads the output maze
     * image to be drawn on
     * the stage.
     */
    public void init () {

        Image im = new Image ( new File ( fileName ).toURI().toString() );

        loadImage = new ImageView ( im );

    }

    /**
     * Overrides the abstract start
     * method to show the loaded png
     * on the stage.
     * @param stage window
     */
    @Override
    public void start ( Stage stage ) {

        // creates parent to show in scene
        StackPane parent = new StackPane();

        // adds image to parent
        parent.getChildren().add( loadImage );

        // sets scene and shows window
        stage.setScene( new Scene ( parent ) );
        stage.setResizable( false );
        stage.show();
    }

    /**
     * Used by the MazeSolver
     * algorithm to show the
     * output maze file
     * @param file file name
     */
    public static void showTrace ( String file ) {

        fileName = file;

        Application.launch();

    }

    /**
     * The main method
     * @param args file name
     */
    public static void main ( String[] args ) {

        showTrace( args[0] );

    }
}
