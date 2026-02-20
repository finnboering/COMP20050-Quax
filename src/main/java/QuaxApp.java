/**
 * The mainJavaFX application class for the Quax game.
 * This class is responsible for:
 * - Launching the JavaFX application
 * - Creating the primary stage
 * - Setting up the scene and user interface
 */

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class QuaxApp extends Application {

    // SR2.0
    private boolean blackTurn =true;

    private static final int SIZE =11;
    private static final int CELL_SIZE = 40;
    private static final int OFFSET =50;

    private Polygon[][] octagons = new Polygon[SIZE][SIZE];
    private Rectangle[][] rhombic =new Rectangle[SIZE-1][SIZE-1];

    @Override
    public void start(Stage stage) {


         // Called automatically when the JavaFX application starts.
         // Sets up the primary stage and displays the board UI.


        // create a blank container to hold drawing
        Pane root = new Pane();

        // top black background
        Rectangle topBackground = new Rectangle(7.5, 7, SIZE *CELL_SIZE + 45, 34);
        topBackground.setFill(Color.BLACK);// sets the color

        // bottom black background
        Rectangle bottomBackground = new Rectangle(7.5, SIZE * CELL_SIZE +19, SIZE * CELL_SIZE + 45, 34);
        bottomBackground.setFill(Color.BLACK);

        // add BLACK side background to pane
        root.getChildren().addAll(topBackground, bottomBackground);

        // creates the 11x11 grid of octagonal cells
        for (int row=0;row <SIZE;row++) {
            for (int col=0;col <SIZE; col++) {

                // creates an octagon positioned by its row and column
                Polygon octagon = createOctagon(col *CELL_SIZE +OFFSET, row * CELL_SIZE + OFFSET, CELL_SIZE);

                // stores the octagon in a 2D array
                octagons[row][col] = octagon;

                octagon.setFill(Color.DEEPSKYBLUE);
                octagon.setStroke(Color.BLACK);

                root.getChildren().add(octagon); // add octagon to the container
            }
        }

        // creates the 10x10 grid of rhombic connector cells
        for (int row = 0;row < SIZE -1; row++) {
            for (int col = 0;col< SIZE -1;col++) {

                // positions the rhombic cell midway between diagonally adjacent octagons
                double x = col* CELL_SIZE + OFFSET+ CELL_SIZE / 2;
                double y = row* CELL_SIZE+ OFFSET + CELL_SIZE /2;

                Rectangle rhombicCell = createRhombic(x, y,CELL_SIZE /2.5);
                this.rhombic[row][col]= rhombicCell; // stores the rhombic connector placement by row and coloumns
                rhombicCell.setFill(Color.SKYBLUE);
                rhombicCell.setStroke(Color.BLACK);

                root.getChildren().add(rhombicCell);
            }
        }

        // draws the left WHITE overlay
        Rectangle leftBar = new Rectangle(OFFSET -42, OFFSET- CELL_SIZE /2, 20, SIZE *CELL_SIZE);
        leftBar.setFill(Color.ORANGE);
        leftBar.setStroke(Color.BLACK);

        // draws the right WHITE overlay
        Rectangle rightBar = new Rectangle(SIZE * CELL_SIZE + OFFSET - 18, OFFSET - CELL_SIZE / 2, 20, SIZE * CELL_SIZE);
        rightBar.setFill(Color.ORANGE);
        rightBar.setStroke(Color.BLACK);

        // draws the top BLACK overlay
        Rectangle topBar = new Rectangle(OFFSET - CELL_SIZE / 2, OFFSET- 42, SIZE * CELL_SIZE, 20);
        topBar.setFill(Color.ORANGE);
        topBar.setStroke(Color.BLACK);

        // draws the bottom BLACK overlay
        Rectangle bottomBar = new Rectangle(OFFSET - CELL_SIZE /2, SIZE* CELL_SIZE + OFFSET -18, SIZE * CELL_SIZE, 20);
        bottomBar.setFill(Color.ORANGE);
        bottomBar.setStroke(Color.BLACK);

        root.getChildren().addAll(leftBar, rightBar,topBar , bottomBar);

        // adds alphabetical column labels to the left and right of the board
        for (int col = 0; col < SIZE; col++) {

            // converts column index into corresponding upper case
            char letter = (char) ('A'+ col);

            // calculates horizontal center position
            double x = col * CELL_SIZE + OFFSET;

            // top label
            Text topLabel = new Text(String.valueOf(letter)); // creates the top label -> displays the column letter
            topLabel.setFont(Font.font(14)); // consistent font size -> allows for alignment on other devices
            double topWidth = topLabel.getLayoutBounds().getWidth(); // measures text width -> labels can be centered horizontally
            topLabel.setX(x - topWidth / 2); // horizontally centers the label above its column
            topLabel.setY(OFFSET - 27); // sets the label slightly above the top board

            // bottom label
            Text bottomLabel = new Text(String.valueOf(letter));
            bottomLabel.setFont(Font.font(14));
            double bottomWidth = bottomLabel.getLayoutBounds().getWidth();
            bottomLabel.setX(x - bottomWidth / 2);
            bottomLabel.setY(SIZE * CELL_SIZE + OFFSET - 3);

            root.getChildren().addAll(topLabel, bottomLabel);
        }

        // add numerical row labels 1-11 on the boards side
        for (int row = 0; row < SIZE; row++) {

            // converts row index into row number
            String number = String.valueOf(row + 1);

            // vertical center position of the row
            double y = row * CELL_SIZE + OFFSET;

            Text leftLabel = new Text(number); // creates label to display on LHS
            leftLabel.setFont(Font.font(14));
            double leftHeight = leftLabel.getLayoutBounds().getHeight();
            leftLabel.setX(OFFSET -38);
            leftLabel.setY(y + leftHeight / 4);

            Text rightLabel = new Text(number); // display on RHS
            rightLabel.setFont(Font.font(14));
            // measures text height so the label can be aligned (vertically) with row center
            double rightHeight = rightLabel.getLayoutBounds().getHeight();
            rightLabel.setX(SIZE * CELL_SIZE +OFFSET -14);
            rightLabel.setY(y + rightHeight / 4);
            root.getChildren().addAll(leftLabel, rightLabel);
        }

        // creates the fx scene sized to fit the board + padding on the sides
        Scene scene = new Scene(root, SIZE * CELL_SIZE + OFFSET *2 -38, SIZE * CELL_SIZE+ OFFSET *2 -38);
        stage.setTitle("Quax Board"); // window title
        stage.setScene(scene); // applys the game to the window
        stage.show(); // makes application window visible


    }

    // creates the octagonal cells at the given coordinates
    private Polygon createOctagon(double x, double y, double size) {

        double half = size / 2; // distance from the center to the edge of shape
        double cut = size / 4;  // amount to cut corners

        // the eight vertex coordinates of the octagon
        Polygon octagon = new Polygon(

                // 8 points are calculated relative from the center
                // moves clockwise from top left edge, Trims each square by "cut"
                x - half + cut, y - half,
                x + half - cut, y - half,

                x + half, y - half + cut,
                x + half, y + half - cut,

                x + half - cut, y + half,
                x - half + cut, y + half,

                x - half, y + half - cut,
                x - half, y - half + cut
        );

        return octagon; // return the constructed octagonal shape
    }

    // creates the rhombic tiles at given coordinates
    private Rectangle createRhombic(double x, double y, double size) {

        Rectangle square = new Rectangle(size, size);

        square.setX(x - size / 2);
        square.setY(y - size / 2);

        square.setRotate(45);
        return square;
    }

    // SR2.0 -> used to switch between players turns
    private void switchTurns() {
        // BLACK turn = true, WHITE turn = false
        blackTurn = !blackTurn;
    }

    // SR2.0 -> returns the current players turn
    private String getCurrentPlayerTurn() {
        if (blackTurn) {
            return "BLACK";
        } else {
            return "WHITE";
        }
    }
}
