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

    // board sizes
    private static final int BOARD_SIZE = 11;
    private static final int CELL_SIZE = 40;
    private static final int OFFSET = 50;

    // ui colors
    private static final Color OCTAGON_COLOR = Color.DEEPSKYBLUE;
    private static final Color OCTAGON_HIGHLIGHT_COLOR = Color.PALETURQUOISE;
    private static final Color RHOMBIC_COLOR = Color.SKYBLUE;
    private static final Color RHOMBIC_HIGHLIGHT_COLOR = Color.PALETURQUOISE;
    private static final Color BORDER_COLOR = Color.ORANGE;
    private static final Color OUTLINE_COLOR = Color.BLACK;
    private static final Color BLACK_STONE = Color.BLACK;
    private static final Color WHITE_STONE = Color.WHITE;

    // keeps track of whose turn it is
    private boolean isBlackTurn = true;

    // arrays to hold our shapes
    private Polygon[][] octagons = new Polygon[BOARD_SIZE][BOARD_SIZE];
    private Rectangle[][] rhombics = new Rectangle[BOARD_SIZE -1][BOARD_SIZE -1];

    // turn indicator text
    private Text turnText;
    private Text statusText;

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
        initHUD(root);

        // how big the popup window needs to be
        double windowSize = BOARD_SIZE * CELL_SIZE + (OFFSET * 2) - 22;

        Scene scene = new Scene(root, windowSize, windowSize);
        stage.setTitle("Quax Board");
        stage.setScene(scene);
        stage.show();
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

    private void drawOctagons(Pane root) {
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                double x = col * CELL_SIZE + OFFSET;
                double y = row * CELL_SIZE + OFFSET;

                Polygon octagon = createOctagon(x, y, CELL_SIZE);

                // add color and outline
                octagon.setFill(OCTAGON_COLOR);
                octagon.setStroke(OUTLINE_COLOR);

                // save it in our array
                octagons[row][col] = octagon;

                // capture loop variables
                final int r = row;
                final int c = col;

                octagon.setOnMouseClicked(e -> attemptPlaceOnOctagon(r, c));

                octagon.setOnMouseEntered(e -> {
                
                    // only highlight if empty
                    if (octagonOwner[r][c] == 0) {
                    //octagon.setOpacity(0.5);
                        octagon.setFill(OCTAGON_HIGHLIGHT_COLOR);
                    }
                });

                octagon.setOnMouseExited(e -> {
                
                    // restore original colour if still empty
                    if (octagonOwner[r][c] == 0) {
                        octagon.setFill(OCTAGON_COLOR);
                    }
                });

                root.getChildren().add(octagon);
            }
        }
    }

    private void drawRhombics(Pane root) {
        // the rhombic shapes go in the spaces between octagons
        for (int row = 0; row < BOARD_SIZE -1; row++) {
            for (int col = 0; col < BOARD_SIZE -1; col++) {

                // find the exact middle point
                double x = col * CELL_SIZE + OFFSET + CELL_SIZE / 2.0;
                double y = row * CELL_SIZE + OFFSET + CELL_SIZE / 2.0;

                Rectangle rhombic = createRhombic(x, y, CELL_SIZE / 2.5);

                rhombic.setFill(RHOMBIC_COLOR);
                rhombic.setStroke(OUTLINE_COLOR);

                // save it in the array
                rhombics[row][col] = rhombic;

                final int r = row;
                final int c = col;
                rhombic.setOnMouseClicked(e -> attemptPlaceOnRhombic(r, c));

                rhombic.setOnMouseEntered(e -> {
                
                    // only highlight if empty
                    if (rhombicOwner[r][c] == 0) {
                        rhombic.setFill(RHOMBIC_HIGHLIGHT_COLOR);
                    }
                });
                
                rhombic.setOnMouseExited(e -> {
                
                    // restore original colour if still empty
                    if (rhombicOwner[r][c] == 0) {
                        rhombic.setFill(RHOMBIC_COLOR);
                    }
                });

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

        // color all the bars in a loop
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
            topText.setY(OFFSET -27);

            bottomText.setX(pos - bottomText.getLayoutBounds().getWidth() / 2);
            bottomText.setY(BOARD_SIZE * CELL_SIZE + OFFSET -3);

            // numbers down the sides
            String number = String.valueOf(i + 1);
            Text leftText = new Text(number);
            Text rightText = new Text(number);

            leftText.setFont(labelFont);
            rightText.setFont(labelFont);

            leftText.setX(OFFSET -38);
            leftText.setY(pos + leftText.getLayoutBounds().getHeight() / 4);

            rightText.setX(BOARD_SIZE * CELL_SIZE + OFFSET -14);
            rightText.setY(pos + rightText.getLayoutBounds().getHeight() / 4);

            root.getChildren().addAll(topText, bottomText, leftText, rightText);
        }
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

    private void initHUD(Pane root) {
        turnText = new Text();
        turnText.setFont(Font.font(18));
        turnText.setFill(Color.BLACK);
        turnText.setX(20);
        turnText.setY(BOARD_SIZE * CELL_SIZE + (OFFSET * 2) -30);

        statusText = new Text("");
        statusText.setFont(Font.font(14));
        statusText.setFill(Color.BLACK);
        statusText.setX(150);
        statusText.setY(BOARD_SIZE * CELL_SIZE + (OFFSET * 2) -30);

        updateTurnText();
        root.getChildren().addAll(turnText, statusText);
    }       

    private void updateTurnText() {
        turnText.setText("Turn: " + getCurrentPlayerTurn());
    }

    // helper : turn functions

    public void switchTurns() {
        // flips the turn back and forth
        isBlackTurn = !isBlackTurn;
    }

    public String getCurrentPlayerTurn() {
        // returns whose turn it is as text
        if (isBlackTurn) {
            return "BLACK";
        } else {
            return "WHITE";
        }
    }

        private void attemptPlaceOnOctagon(int row, int col) {
        // no full legality: only prevent overwriting an occupied cell
        if (octagonOwner[row][col] != 0) {
            statusText.setText("That cell is already occupied.");
            return;
        }
        // place stone
        octagonOwner[row][col] = isBlackTurn ? 1 : 2;
        octagons[row][col].setFill(isBlackTurn ? BLACK_STONE : WHITE_STONE);;
        

        // clear message + switch turn
        statusText.setText("");
        switchTurns();
        updateTurnText();
    }

    private void attemptPlaceOnRhombic(int row, int col) {
        if (rhombicOwner[row][col] != 0) {
            statusText.setText("That cell is already occupied.");
            return;
        }
        
        rhombicOwner[row][col] = isBlackTurn ? 1 : 2;
        rhombics[row][col].setFill(isBlackTurn ? BLACK_STONE : WHITE_STONE);

        statusText.setText("");
        switchTurns();
        updateTurnText();
    }

}
