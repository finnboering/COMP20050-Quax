package game;

import javafx.application.Application;
import javafx.application.Platform;
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

import javafx.scene.control.ComboBox;

public class QuaxApp extends Application {

    // board sizes
    private static final int BOARD_SIZE = 11;
    private static final int CELL_SIZE = 40;
    private static final int OFFSET = 50;

    //ui colors
    Color OCTAGON_COLOR = Color.DEEPSKYBLUE;
    Color OCTAGON_HIGHLIGHT_COLOR = Color.PALETURQUOISE;
    Color RHOMBIC_COLOR = Color.SKYBLUE;
    Color RHOMBIC_HIGHLIGHT_COLOR = Color.PALETURQUOISE;
    Color BORDER_COLOR = Color.ORANGE;
    Color OUTLINE_COLOR = Color.BLACK;

    //keeps track of whose turn it is
    private boolean isBlackTurn = true;
    private boolean firstMoveDone = false;
    //prevents the pie rule from being triggered more than once per game
    private boolean pieRuleResolved = false;

    //keeps track of player colour
    private boolean isBlack = true;
    private boolean player1 = isBlack;
    private boolean player2 = !isBlack;

    private boolean gameOver = false;
    private boolean devMode = false;
    private int[] pendingBotMove = null;
    private ComboBox<String> strategyPicker;

    //bot
    private bot.Strategy botStrategy = new bot.SmartStrategy();
    //tracks whether the human is currently playing as black, can change after pie rule
    private boolean humanIsBlack;
    private int humanColour; //1 = black, 2 = white

    //ui text for the turn indicator
    private Text turnText;
    private Text statusText;
    //permanent label showing current colour assignments for human and bot
    private Text playerColourLabel;
    private Button pieRuleButton;

    private Button devModeButton;
    private Button confirmBotMoveButton;
    private Text devStrategyText;

    //track occupation of tile: 0 empty, 1 black, 2 white
    private int[][] octagonOwner = new int[BOARD_SIZE][BOARD_SIZE];
    private int[][] rhombicOwner = new int[BOARD_SIZE - 1][BOARD_SIZE - 1];


    //constructor to accept colour choice from the start screen
    public QuaxApp(boolean humanIsBlack) {
        this.humanIsBlack = humanIsBlack;
        this.humanColour = humanIsBlack ? 1 : 2;
    }

    //default no-arg constructor required
    public QuaxApp() {
        this.humanIsBlack = true; //default to human as black
        this.humanColour = 1;
    }

    @Override
    public void start(Stage stage) {
        Pane root = new Pane();

        //draw all the pieces of the board (in correct order)
        drawBackground(root);
        drawOctagons(root);
        drawRhombics(root);
        drawBorders(root);
        drawLabels(root);

        //draw the turn indicator and status
        drawTextBoxes(root);
        drawPlayerColourLabel(root);
        drawPieRuleButton(root);

        drawDevModeControls(root);

        //calculate the base size of the board
        double baseSize = BOARD_SIZE * CELL_SIZE + (OFFSET * 2) - 17; // i  subtracted by 17 here so that there isnt extra white space

        //i wanted it to be rectangular so i removed 23 from the right hand side
        double windowWidth = baseSize - 23;
        double windowHeight = baseSize + 80;

        Scene scene = new Scene(root, windowWidth, windowHeight);
        stage.setTitle("Quax game.Board");
        stage.setScene(scene);
        stage.show();


        //if human chose white, bot goes first as black
        if (!humanIsBlack) {
            triggerBotMove();
        }
    }


    private void drawTextBoxes(Pane root) {
        turnText = new Text();
        turnText.setFont(Font.font(20));
        turnText.setFill(Color.BLACK);
        turnText.setX(13);
        turnText.setY(513);

        statusText = new Text("");
        statusText.setFont(Font.font(30));
        statusText.setFill(Color.RED);
        statusText.setX(300);
        statusText.setY(517);

        updateTurnText();
        root.getChildren().addAll(turnText, statusText);

    }

    private void drawPlayerColourLabel(Pane root) {
        playerColourLabel = new Text();
        playerColourLabel.setFont(Font.font(14));
        playerColourLabel.setFill(Color.BLACK);
        playerColourLabel.setX(13);
        playerColourLabel.setY(530);
        updatePlayerColourLabel();
        root.getChildren().add(playerColourLabel);
    }

    private void updatePlayerColourLabel() {
        //does the label text based on current humanColour value
        //called after the pie rule to show the new colour assignment
        String humanColourText = humanColour == 1 ? "BLACK" : "WHITE";
        String botColourText = humanColour == 1 ? "WHITE" : "BLACK";
        playerColourLabel.setText("You: " + humanColourText + "  |  Bot: " + botColourText);
    }


    private void drawBackground(Pane root) {
        //dark background blocks behind the board for BLACK win side
        double bgWidth = BOARD_SIZE * CELL_SIZE + 45;
        double bgHeight = 34;

        Rectangle topBg = new Rectangle(7.5, 7, bgWidth, bgHeight);
        topBg.setFill(Color.BLACK);

        Rectangle bottomBg = new Rectangle(7.5, BOARD_SIZE * CELL_SIZE + 19, bgWidth, bgHeight);
        bottomBg.setFill(Color.BLACK);

        root.getChildren().addAll(topBg, bottomBg);
    }

    //rrays to hold our shapes
    private Polygon[][] octagons = new Polygon[BOARD_SIZE][BOARD_SIZE];
    private Rectangle[][] rhombics = new Rectangle[BOARD_SIZE - 1][BOARD_SIZE - 1];

    private void drawOctagons(Pane root) {
        for (int r = 0; r < BOARD_SIZE; r++) {
            for (int c = 0; c < BOARD_SIZE; c++) {
                double x = c * CELL_SIZE + OFFSET, y = r * CELL_SIZE + OFFSET;

                Polygon octagon = createOctagon(x, y, CELL_SIZE);

                //add color and outline
                octagon.setFill(OCTAGON_COLOR);
                octagon.setStroke(OUTLINE_COLOR);

                //variables need to be final for the click event to work
                final int finalR = r;
                final int finalC = c;

                //makes the octagons clickable
                octagon.setOnMouseClicked(event -> attemptPlaceOnOctagon(finalR, finalC));

                //highlight valid tiles when hovering over them
                octagon.setOnMouseEntered(e -> {
                    if (octagonOwner[finalR][finalC] == 0 && !gameOver) {
                        octagon.setFill(OCTAGON_HIGHLIGHT_COLOR);
                    }
                });

                octagon.setOnMouseExited(e -> {
                    if (octagonOwner[finalR][finalC] == 0 && !gameOver) {
                        octagon.setFill(OCTAGON_COLOR);
                    }
                });

                //save it in our array
                octagons[r][c] = octagon;

                // put it on the screen
                root.getChildren().add(octagon);
            }
        }
    }

    private void drawRhombics(Pane root) {
        for (int r = 0; r < BOARD_SIZE - 1; r++) {
            for (int c = 0; c < BOARD_SIZE - 1; c++) {

                double x = c * CELL_SIZE + OFFSET + CELL_SIZE / 2.0;
                double y = r * CELL_SIZE + OFFSET + CELL_SIZE / 2.0;

                Rectangle rhombic = createRhombic(x, y, CELL_SIZE / 2.5);

                rhombic.setFill(RHOMBIC_COLOR);
                rhombic.setStroke(OUTLINE_COLOR);

                rhombics[r][c] = rhombic;

                final int finalR = r;
                final int finalC = c;

                rhombic.setOnMouseClicked(e -> attemptPlaceOnRhombic(finalR, finalC));

                rhombic.setOnMouseEntered(e -> {
                    if (rhombicOwner[finalR][finalC] == 0 && !gameOver) {
                        rhombic.setFill(RHOMBIC_HIGHLIGHT_COLOR);
                    }
                });

                rhombic.setOnMouseExited(e -> {
                    if (rhombicOwner[finalR][finalC] == 0 && !gameOver) {
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

        // color all the bars
        Rectangle[] bars = {leftBar, rightBar, topBar, bottomBar};
        for (Rectangle bar : bars) {
            bar.setFill(BORDER_COLOR);
            bar.setStroke(OUTLINE_COLOR);
        }

        root.getChildren().addAll(bars);
    }

    private void drawLabels(Pane root) {
        Font labelFont = Font.font(14); //keeps font size the same

        for (int i = 0; i < BOARD_SIZE; i++) {
            double pos = i * CELL_SIZE + OFFSET;

            //letters across the top and bottom
            char letter = (char) ('A' + i);
            Text topText = new Text(String.valueOf(letter));
            Text bottomText = new Text(String.valueOf(letter));

            topText.setFont(labelFont);
            bottomText.setFont(labelFont);

            //center the text
            topText.setX(pos - topText.getLayoutBounds().getWidth() / 2);
            topText.setY(OFFSET - 27);

            bottomText.setX(pos - bottomText.getLayoutBounds().getWidth() / 2);
            bottomText.setY(BOARD_SIZE * CELL_SIZE + OFFSET - 3);


            //numbers down the sides
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
        pieRuleButton = new Button("Pie Rule");
        pieRuleButton.setLayoutX(220);
        pieRuleButton.setLayoutY(495);
        pieRuleButton.setVisible(false);

        pieRuleButton.setOnAction(e -> {
            //don't swap tile colours just show message and swap player identities
            statusText.setText("Pie rule used");

            //human becomes Black, bot becomes White, bot goes again
            humanIsBlack = true;
            humanColour = 1;
            isBlackTurn = false; //bot's turn (White) goes again

            pieRuleButton.setVisible(false);
            pieRuleButton.setDisable(false);
            updateTurnText();
            updatePlayerColourLabel();

            //clear message after 1.5s then trigger bot move
            ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
            scheduler.schedule(() -> Platform.runLater(() -> statusText.setText("")), 1500, TimeUnit.MILLISECONDS);
            scheduler.shutdown();

            triggerBotMove();
        });

        root.getChildren().add(pieRuleButton);
    }


    //helper : draws the shapes
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

    //helper : turn functions
    public void switchTurns() {
        // flips the turn back and forth
        isBlackTurn = !isBlackTurn;
    }

    // swap stone colors on the board and sets the turn to player who used rule to BLACK
    public void doPieRule() {
        statusText.setText("Pie rule used");

        // swap octagon owners
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

        // swap rhombic owners
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

        // NOTE: turn setting is now handled by the caller
    }

    public String getCurrentPlayerTurn() {
        //returns whose turn it is as text
        if (isBlackTurn) {
            return "BLACK";
        } else {
            return "WHITE";
        }
    }

    // SR2.1 & SR2.2 : basic tile placement for octagons
    private void attemptPlaceOnOctagon(int row, int col) {
        if (gameOver) return;

        //block clicks when it's the bot's turn
        boolean isHumanTurn = (isBlackTurn && humanColour == 1) || (!isBlackTurn && humanColour == 2);
        if (!isHumanTurn) return;

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        if (!firstMoveDone) {
            firstMoveDone = true;
            if (humanIsBlack) {
                //human went first so show pie rule button but disable it
                pieRuleButton.setVisible(true);
                pieRuleButton.setDisable(true);
            }
        } else if (pieRuleButton.isVisible()) {
            pieRuleButton.setVisible(false);
            pieRuleButton.setDisable(false);
        }

        //prevent overwriting an occupied cell
        if (octagonOwner[row][col] != 0) {
            statusText.setText("Cell occupied!");

            scheduler.schedule(() -> {
                statusText.setText(""); //executed after the delay
            }, 1500, TimeUnit.MILLISECONDS);
            scheduler.shutdown();
            return;
        }

        //place stone
        if (isBlackTurn) {
            octagonOwner[row][col] = 1;
        } else {
            octagonOwner[row][col] = 2;
        }
        if (isBlackTurn) {
            octagons[row][col].setFill(Color.BLACK);
        } else {
            octagons[row][col].setFill(Color.WHITE);
        }

        //check win after placing on octagon
        int currentPlayer;
        if (isBlackTurn) {
            currentPlayer = 1;
        } else {
            currentPlayer = 2;
        }

        WinChecker winChecker = new WinChecker(BOARD_SIZE);

        if (winChecker.checkWin(octagonOwner, rhombicOwner, currentPlayer)) {
            gameOver = true;
            if (currentPlayer == 1) {
                statusText.setText("BLACK WINS");
            } else {
                statusText.setText("WHITE WINS");
            }
            return;
        }


        //clear message and switch turn
        statusText.setText("");
        switchTurns();
        updateTurnText();


        //trigger bot after 0.5 s delay
        triggerBotMove();
    }

    private void attemptPlaceOnRhombic(int row, int col) {
        if (gameOver) return;

        //block clicks when it's the bot's turn
        boolean isHumanTurn = (isBlackTurn && humanColour == 1) || (!isBlackTurn && humanColour == 2);
        if (!isHumanTurn) return;


        //used to add the 2 second delay
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        if (!firstMoveDone) {
            firstMoveDone = true;
            if (humanIsBlack) {
                //human went first so show pie rule button but disable it
                pieRuleButton.setVisible(true);
                pieRuleButton.setDisable(true);
            }
        } else if (pieRuleButton.isVisible()) {
            pieRuleButton.setVisible(false);
            pieRuleButton.setDisable(false);
        }

        if (rhombicOwner[row][col] != 0) {
            statusText.setText("Cell occupied!");

            scheduler.schedule(() -> {
                statusText.setText(""); //executed after the delay
            }, 2, TimeUnit.SECONDS);
            scheduler.shutdown();
            return;
        }

        rhombicOwner[row][col] = isBlackTurn ? 1 : 2;
        rhombics[row][col].setFill(isBlackTurn ? Color.BLACK : Color.WHITE);

        int currentPlayer = isBlackTurn ? 1 : 2;
        WinChecker winChecker = new WinChecker(BOARD_SIZE);
        if (winChecker.checkWin(octagonOwner, rhombicOwner, currentPlayer)) {
            gameOver = true;
            statusText.setText(currentPlayer == 1 ? "BLACK WINS" : "WHITE WINS");
            return;
        }

        statusText.setText("");
        switchTurns();
        updateTurnText();


        //trigger bot after 0.5 s delay
        triggerBotMove();
    }

    //schedules the bot move with a 0.5 s delay
    private void triggerBotMove() {
        if (gameOver) return;
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.schedule(() -> Platform.runLater(() -> doBotMove()), 500, TimeUnit.MILLISECONDS);
        scheduler.shutdown();
    }

    private void doBotMove() {
        if (gameOver) return;

        // handle pie rule when bot goes first (human chose white)
        if (!humanIsBlack && firstMoveDone && !pieRuleResolved) {
            pieRuleResolved = true;
        }

        int botColour = humanIsBlack ? 2 : 1;
        int[] move = botStrategy.chooseMove(octagonOwner, rhombicOwner, botColour);
        if (move == null) return;

        if (devMode) {
            pendingBotMove = move;
            highlightBotMove(move);
            devStrategyText.setText("Strategy: " + botStrategy.getName()
                    + "  →  " + (move[2] == 0 ? "Octagon" : "Rhombic")
                    + " [" + move[0] + "][" + move[1] + "]");
            confirmBotMoveButton.setVisible(true);
        } else {
            executeBotMove(move);
        }
    }

    private void drawDevModeControls(Pane root) {
        devModeButton = new Button("Dev Mode: OFF");
        devModeButton.setLayoutX(13);
        devModeButton.setLayoutY(545);
        devModeButton.setOnAction(e -> {
            devMode = !devMode;
            devModeButton.setText(devMode ? "Dev Mode: ON" : "Dev Mode: OFF");
            updateDevControlsVisibility();
            if (!devMode) {
                confirmBotMoveButton.setVisible(false);
                devStrategyText.setText("");
                clearBotHighlight();
                pendingBotMove = null;
            }
        });

        strategyPicker = new ComboBox<>();
        strategyPicker.getItems().addAll("Smart", "Aggressive", "Random");
        strategyPicker.setValue("Smart");
        strategyPicker.setLayoutX(140);
        strategyPicker.setLayoutY(545);
        strategyPicker.setVisible(false);
        strategyPicker.setOnAction(e -> {
            String selected = strategyPicker.getValue();
            switch (selected) {
                case "Random"     -> botStrategy = new bot.RandomStrategy();
                case "Aggressive" -> botStrategy = new bot.AggressiveStrategy();
                default           -> botStrategy = new bot.SmartStrategy();
            }
        });

        confirmBotMoveButton = new Button("Confirm Bot Move");
        confirmBotMoveButton.setLayoutX(290);
        confirmBotMoveButton.setLayoutY(545);
        confirmBotMoveButton.setVisible(false);
        confirmBotMoveButton.setOnAction(e -> {
            if (pendingBotMove != null) {
                confirmBotMoveButton.setVisible(false);
                devStrategyText.setText("");
                clearBotHighlight();
                executeBotMove(pendingBotMove);
                pendingBotMove = null;
            }
        });

        devStrategyText = new Text("");
        devStrategyText.setFont(Font.font(14));
        devStrategyText.setFill(Color.DARKGREEN);
        devStrategyText.setX(13);
        devStrategyText.setY(585);

        root.getChildren().addAll(devModeButton, strategyPicker, confirmBotMoveButton, devStrategyText);
    }

    private void updateDevControlsVisibility() {
        strategyPicker.setVisible(devMode);
    }

    private void highlightBotMove(int[] move) {
        int r = move[0], c = move[1], tileType = move[2];
        if (tileType == 0) octagons[r][c].setFill(Color.ORANGE);
        else rhombics[r][c].setFill(Color.ORANGE);
    }

    private void clearBotHighlight() {
        if (pendingBotMove == null) return;
        int r = pendingBotMove[0], c = pendingBotMove[1], tileType = pendingBotMove[2];
        if (tileType == 0) octagons[r][c].setFill(OCTAGON_COLOR);
        else rhombics[r][c].setFill(RHOMBIC_COLOR);
    }

    private void executeBotMove(int[] move) {
        if (gameOver) return;
        int r = move[0], c = move[1], tileType = move[2];
        int botColour = humanIsBlack ? 2 : 1;

        if (tileType == 0) {
            octagonOwner[r][c] = botColour;
            octagons[r][c].setFill(botColour == 1 ? Color.BLACK : Color.WHITE);
        } else {
            rhombicOwner[r][c] = botColour;
            rhombics[r][c].setFill(botColour == 1 ? Color.BLACK : Color.WHITE);
        }

        WinChecker winChecker = new WinChecker(BOARD_SIZE);
        if (winChecker.checkWin(octagonOwner, rhombicOwner, botColour)) {
            gameOver = true;
            statusText.setText(botColour == 1 ? "BLACK WINS" : "WHITE WINS");
            return;
        }

        if (!humanIsBlack && !pieRuleResolved) {
            pieRuleResolved = true;
            firstMoveDone = true;
            pieRuleButton.setVisible(true);
            switchTurns();
            updateTurnText();
            return;
        }

        switchTurns();
        updateTurnText();
    }
}