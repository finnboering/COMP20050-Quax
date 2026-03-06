import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import javafx.scene.control.Button;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class QuaxApp extends Application {

    // board sizes
    private static final int BOARD_SIZE = 11;
    private static final int CELL_SIZE = 40;
    private static final int OFFSET = 50;

    // ui colors
    Color OCTAGON_COLOR = Color.DEEPSKYBLUE;
    Color OCTAGON_HIGHLIGHT_COLOR = Color.PALETURQUOISE;
    Color RHOMBIC_COLOR = Color.SKYBLUE;
    Color RHOMBIC_HIGHLIGHT_COLOR = Color.PALETURQUOISE;
    Color BORDER_COLOR = Color.ORANGE;
    Color OUTLINE_COLOR = Color.BLACK;

    // keeps track of whose turn it is
    private boolean isBlackTurn = true;
    private boolean firstMoveDone = false;

    // keeps track of player colour
    private boolean isBlack = true;
    private boolean player1 = isBlack;
    private boolean player2 = !isBlack;


    // ui text for the turn indicator
    private Text turnText;
    private Text statusText;
    private Text playerText;
    private Button pieRuleButton;


    // track occupancy of tile: 0 empty, 1 black, 2 white
    private int[][] octagonOwner = new int[BOARD_SIZE][BOARD_SIZE];
    private int[][] rhombicOwner = new int[BOARD_SIZE - 1][BOARD_SIZE - 1];

    @Override
    public void start(Stage stage) {
        Pane root = new Pane();

        // draw all the pieces of the board (in correct order)
        drawBackground(root);
        drawOctagons(root);
        drawRhombics(root);
        drawBorders(root);
        drawLabels(root);

        // draw the turn indicator and status
        drawTextBoxes(root);
        drawPieRuleButton(root);


        // calculate the base size of the board
        double baseSize = BOARD_SIZE * CELL_SIZE + (OFFSET * 2) - 17; // i  subtracted by 17 here so that there isnt extra white space

        // i wanted it to be rectangular so i removed 23 from the right hand side
        double windowWidth = baseSize - 23;
        double windowHeight = baseSize;

        Scene scene = new Scene(root, windowWidth, windowHeight);
        stage.setTitle("Quax Board");
        stage.setScene(scene);
        stage.show();
    }


    private void drawTextBoxes(Pane root) {
        turnText = new Text();
        turnText.setFont(Font.font(20));
        turnText.setFill(Color.BLACK);
        turnText.setX(150);
        turnText.setY(515);

        statusText = new Text("");
        statusText.setFont(Font.font(60));
        statusText.setFill(Color.RED);
        statusText.setX(75);
        statusText.setY(220);

        playerText = new Text("");
        playerText.setFont(Font.font(60));
        playerText.setFill(Color.RED);
        playerText.setX(75);
        playerText.setY(220);

        updateTurnText();
        root.getChildren().addAll(turnText, statusText, playerText);

    }

    private void drawBackground(Pane root) {
        // dark background blocks behind the board (for BLACK win side)
        double bgWidth = BOARD_SIZE * CELL_SIZE + 45;
        double bgHeight = 34;

        Rectangle topBg = new Rectangle(7.5, 7, bgWidth, bgHeight);
        topBg.setFill(Color.BLACK);

        Rectangle bottomBg = new Rectangle(7.5, BOARD_SIZE * CELL_SIZE + 19, bgWidth, bgHeight);
        bottomBg.setFill(Color.BLACK);

        root.getChildren().addAll(topBg, bottomBg);
    }

    // arrays to hold our shapes
    private Polygon[][] octagons = new Polygon[BOARD_SIZE][BOARD_SIZE];
    private Rectangle[][] rhombics = new Rectangle[BOARD_SIZE - 1][BOARD_SIZE - 1];

    private void drawOctagons(Pane root) {
        for (int r = 0; r < BOARD_SIZE; r++) {
            for (int c = 0; c < BOARD_SIZE; c++) {
                double x = c * CELL_SIZE + OFFSET, y = r * CELL_SIZE + OFFSET;

                Polygon octagon = createOctagon(x, y, CELL_SIZE);

                // add color and outline
                octagon.setFill(OCTAGON_COLOR);
                octagon.setStroke(OUTLINE_COLOR);

                // variables need to be final for the click event to work
                final int finalR = r;
                final int finalC = c;

                // makes the octagons clickable
                octagon.setOnMouseClicked(event -> attemptPlaceOnOctagon(finalR, finalC));

                // highlight valid tiles when hovering over them
                octagon.setOnMouseEntered(e -> {

                    // only highlight if empty
                    if (octagonOwner[finalR][finalC] == 0) {
                        //octagon.setOpacity(0.5);
                        octagon.setFill(OCTAGON_HIGHLIGHT_COLOR);
                    }
                });

                octagon.setOnMouseExited(e -> {

                    // restore original colour if still empty
                    if (octagonOwner[finalR][finalC] == 0) {
                        octagon.setFill(OCTAGON_COLOR);
                    }
                });

                // save it in our array
                octagons[r][c] = octagon;

                // put it on the screen
                root.getChildren().add(octagon);
            }
        }
    }

    private void drawRhombics(Pane root) {
        // the rhombic shapes go in the spaces between octagons
        for (int r = 0; r < BOARD_SIZE - 1; r++) {
            for (int c = 0; c < BOARD_SIZE - 1; c++) {

                // find the exact middle point
                double x = c * CELL_SIZE + OFFSET + CELL_SIZE / 2.0;
                double y = r * CELL_SIZE + OFFSET + CELL_SIZE / 2.0;

                Rectangle rhombic = createRhombic(x, y, CELL_SIZE / 2.5); // I divide by 2.5 so the rhombics fit better

                // add color and outline
                rhombic.setFill(RHOMBIC_COLOR);
                rhombic.setStroke(OUTLINE_COLOR);

                // save it in our array
                rhombics[r][c] = rhombic;

                // variables need to be final for the click event to work
                final int finalR = r;
                final int finalC = c;

                rhombic.setOnMouseClicked(e -> attemptPlaceOnRhombic(finalR, finalC));

                rhombic.setOnMouseEntered(e -> {

                    // only highlight if empty
                    if (rhombicOwner[finalR][finalC] == 0) {
                        rhombic.setFill(RHOMBIC_HIGHLIGHT_COLOR);
                    }
                });

                rhombic.setOnMouseExited(e -> {

                    // restore original colour if still empty
                    if (rhombicOwner[finalR][finalC] == 0) {
                        rhombic.setFill(RHOMBIC_COLOR);
                    }
                });

                // put it on the screen
                root.getChildren().add(rhombic);
            }
        }
    }

    private void drawBorders(Pane root) {
        double totalSize = BOARD_SIZE * CELL_SIZE;
        double barThickness = 20;

        // left and right bars
        Rectangle leftBar = new Rectangle(OFFSET - 42, OFFSET - CELL_SIZE / 2.0, barThickness, totalSize);
        Rectangle rightBar = new Rectangle(totalSize + OFFSET - 18, OFFSET - CELL_SIZE / 2.0, barThickness, totalSize);

        // top and bottom bars
        Rectangle topBar = new Rectangle(OFFSET - CELL_SIZE / 2.0, OFFSET - 42, totalSize, barThickness);
        Rectangle bottomBar = new Rectangle(OFFSET - CELL_SIZE / 2.0, totalSize + OFFSET - 18, totalSize, barThickness);

        // color all the bars
        Rectangle[] bars = {leftBar, rightBar, topBar, bottomBar};
        for (Rectangle bar : bars) {
            bar.setFill(BORDER_COLOR);
            bar.setStroke(OUTLINE_COLOR);
        }

        root.getChildren().addAll(bars);
    }

    private void drawLabels(Pane root) {
        Font labelFont = Font.font(14); // keeps font size the same

        for (int i = 0; i < BOARD_SIZE; i++) {
            double pos = i * CELL_SIZE + OFFSET;

            // letters across the top and bottom
            char letter = (char) ('A' + i);
            Text topText = new Text(String.valueOf(letter));
            Text bottomText = new Text(String.valueOf(letter));

            topText.setFont(labelFont);
            bottomText.setFont(labelFont);

            // center the text
            topText.setX(pos - topText.getLayoutBounds().getWidth() / 2);
            topText.setY(OFFSET - 27);

            bottomText.setX(pos - bottomText.getLayoutBounds().getWidth() / 2);
            bottomText.setY(BOARD_SIZE * CELL_SIZE + OFFSET - 3);


            // numbers down the sides
            String number = String.valueOf(i + 1);
            Text leftText = new Text(number);
            Text rightText = new Text(number);

            leftText.setFont(labelFont);
            rightText.setFont(labelFont);

            leftText.setX(OFFSET - 38);
            leftText.setY(pos + leftText.getLayoutBounds().getHeight() / 4);

            rightText.setX(BOARD_SIZE * CELL_SIZE + OFFSET - 14);
            rightText.setY(pos + rightText.getLayoutBounds().getHeight() / 4);

            root.getChildren().addAll(topText, bottomText, leftText, rightText);
        }
    }

    private void drawPieRuleButton(Pane root) {
        // Create pie rule button (hidden at start)
        pieRuleButton = new Button("Pie Rule");

        pieRuleButton.setLayoutX(10);
        pieRuleButton.setLayoutY(495);

        pieRuleButton.setVisible(false); // only appears after first move

        pieRuleButton.setOnAction(e -> {
            doPieRule();
            pieRuleButton.setVisible(false);  // disappears after pressed
        });

        // add it once (hidden) so we can just show/hide it later
        root.getChildren().add(pieRuleButton);
    }


    // helper : draws the shapes
    private Polygon createOctagon(double x, double y, double size) {
        double half = size / 2.0;
        double cut = size / 4.0; // how much to cut off the corners

        return new Polygon(
                x - half + cut, y - half,       // top left
                x + half - cut, y - half,       // top right
                x + half, y - half + cut,       // right top
                x + half, y + half - cut,       // right bottom
                x + half - cut, y + half,       // bottom right
                x - half + cut, y + half,       // bottom left
                x - half, y + half - cut,       // left bottom
                x - half, y - half + cut        // left top
        );
    }

    private Rectangle createRhombic(double x, double y, double size) {
        Rectangle square = new Rectangle(size, size);
        square.setX(x - size / 2.0);
        square.setY(y - size / 2.0);

        // rotate the square to make it a rhombic
        square.setRotate(45);

        return square;
    }

    private void updateTurnText() {
        turnText.setText("Current Turn: " + getCurrentPlayerTurn());
    }

    // helper : turn functions
    public void switchTurns() {
        // flips the turn back and forth
        isBlackTurn = !isBlackTurn;
    }

    // swap stone colors on the board and sets the turn to player who used rule to BLACK
    public void doPieRule() {

        statusText.setText("pie rule used"); // notify when the pie rule is used

        // swap octagon owner
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {

                if (octagonOwner[row][col] != 0) {
                    if (octagonOwner[row][col] == 1) {
                        octagonOwner[row][col] = 2;
                        octagons[row][col].setFill(Color.WHITE);
                    } else {
                        octagonOwner[row][col] = 1;
                        octagons[row][col].setFill(Color.BLACK);
                    }
                }
            }
        }

        // swap rhombic owner
        for (int row = 0; row < BOARD_SIZE - 1; row++) {
            for (int col = 0; col < BOARD_SIZE - 1; col++) {

                if (rhombicOwner[row][col] != 0) {
                    if (rhombicOwner[row][col] == 1) {
                        rhombicOwner[row][col] = 2;
                        rhombics[row][col].setFill(Color.WHITE);
                    } else {
                        rhombicOwner[row][col] = 1;
                        rhombics[row][col].setFill(Color.BLACK);
                    }
                }

            }
        }

        // after pie rule the player who pressed it becomes BLACK
        isBlackTurn = true;
        updateTurnText();
    }

    public String getCurrentPlayerTurn() {
        // returns whose turn it is as text
        if (isBlackTurn) {
            return "BLACK";
        } else {
            return "WHITE";
        }
    }

    // SR2.1 & SR2.2 : basic tile placement for octagons
    private void attemptPlaceOnOctagon(int row, int col) {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        if (!firstMoveDone) {
            firstMoveDone = true;
            pieRuleButton.setVisible(true);
        } else if (pieRuleButton.isVisible()) {
            pieRuleButton.setVisible(false);
        }

        // no full legality: only prevent overwriting an occupied cell
        if (octagonOwner[row][col] != 0) {
            statusText.setText("Cell occupied!");

            scheduler.schedule(() -> {
                statusText.setText(""); // Task to be executed after the delay
            }, 1500, TimeUnit.MILLISECONDS);
            scheduler.shutdown();
            return;
        }

        // place stone
        octagonOwner[row][col] = isBlackTurn ? 1 : 2;
        octagons[row][col].setFill(isBlackTurn ? Color.BLACK : Color.WHITE);


        // clear message + switch turn
        statusText.setText("");
        switchTurns();
        updateTurnText();
    }

    private void attemptPlaceOnRhombic(int row, int col) {
        //used to add the 2 second delay
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        if (!firstMoveDone) {
            firstMoveDone = true;
            pieRuleButton.setVisible(true);
        } else if (pieRuleButton.isVisible()) {
            pieRuleButton.setVisible(false);
        }

        if (rhombicOwner[row][col] != 0) {
            statusText.setText("Cell occupied!");

            scheduler.schedule(() -> {
                statusText.setText(""); // Task to be executed after the delay
            }, 2, TimeUnit.SECONDS);
            scheduler.shutdown();
            return;
        }

        rhombicOwner[row][col] = isBlackTurn ? 1 : 2;
        rhombics[row][col].setFill(isBlackTurn ? Color.BLACK : Color.WHITE);

        statusText.setText("");
        switchTurns();
        updateTurnText();
    }
}