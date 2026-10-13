import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class QuaxApp extends Application {

    // board sizes
    private static final int BOARD_SIZE = 11;
    private static final int CELL_SIZE = 55;
    private static final int OFFSET = 45;

    // dark theme colors
// light cream theme colors
    private static final Color OCTAGON_COLOR = Color.web("#c8b89a");
    private static final Color OCTAGON_HOVER_COLOR = Color.web("#b8a88a");
    private static final Color RHOMBIC_COLOR = Color.web("#b8a47a");
    private static final Color RHOMBIC_HOVER_COLOR = Color.web("#a8946a");
    private static final Color BLACK_BORDER_COLOR = Color.web("#2c2c2c");
    private static final Color WHITE_BORDER_COLOR = Color.web("#ffffff");
    private static final Color TILE_OUTLINE = Color.web("#a09070");
    private static final Color BLACK_STONE = Color.web("#1a1a1a");
    private static final Color WHITE_STONE = Color.web("#f5f5f5");
    private static final Color TEXT_COLOR = Color.web("#2c2c2c");
    private static final Color ACCENT_COLOR = Color.web("#8b4513");
    private static final Color PENDING_COLOR = Color.web("#53d8fb");

    // keeps track of whose turn it is
    private boolean isBlackTurn = true;
    private boolean firstMoveDone = false;
    // prevents the pie rule from being triggered more than once per game
    private boolean pieRuleResolved = false;
    private boolean gameOver = false;

    // keeps track of player colour
    private boolean isBlack = true;
    private boolean player1 = isBlack;
    private boolean player2 = !isBlack;

    // bot
    private BotPlayer bot = new BotPlayer();
    // tracks whether the human is currently playing as black, can change after pie rule
    private boolean humanIsBlack;
    private int humanColour; // 1 = black, 2 = white

    // ui elements
    private Label turnLabel;
    private Label statusLabel;
    private Label playerColourLabel;
    private Label devStrategyLabel;
    private Button pieRuleButton;
    private Button devModeButton;
    private Button confirmBotMoveButton;

    // track occupation of tile: 0 empty, 1 black, 2 white
    private int[][] octagonOwner = new int[BOARD_SIZE][BOARD_SIZE];
    private int[][] rhombicOwner = new int[BOARD_SIZE - 1][BOARD_SIZE - 1];

    // dev mode
    private boolean devMode = false;
    private int[] pendingBotMove = null;

    // arrays to hold our shapes
    private Polygon[][] octagons = new Polygon[BOARD_SIZE][BOARD_SIZE];
    private Rectangle[][] rhombics = new Rectangle[BOARD_SIZE - 1][BOARD_SIZE - 1];

    // constructor to accept colour choice from start screen
    public QuaxApp(boolean humanIsBlack) {
        this.humanIsBlack = humanIsBlack;
        this.humanColour = humanIsBlack ? 1 : 2;
    }

    // default no-arg constructor required by JavaFX
    public QuaxApp() {
        this.humanIsBlack = true;
        this.humanColour = 1;
    }

    @Override
    public void start(Stage stage) {
        HBox root = new HBox(30);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #f5f0e8;");
        root.setPadding(new Insets(20));

        VBox leftPanel = buildLeftPanel();
        Pane boardPane = buildBoardPane();
        VBox rightPanel = buildRightPanel();

        // title at the top, board row below
        VBox mainLayout = new VBox(20);
        mainLayout.setAlignment(Pos.TOP_CENTER);
        mainLayout.setStyle("-fx-background-color: #f5f0e8;");

        Label title = new Label("QUAX");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 42));
        title.setTextFill(ACCENT_COLOR);

        HBox boardRow = new HBox(30);
        boardRow.setAlignment(Pos.CENTER);

        // wrap panels in StackPane to centre them vertically beside the board
        StackPane leftWrapper = new StackPane(buildLeftPanel());
        leftWrapper.setAlignment(Pos.CENTER);

        StackPane rightWrapper = new StackPane(buildRightPanel());
        rightWrapper.setAlignment(Pos.CENTER);

        boardRow.getChildren().addAll(leftWrapper, boardPane, rightWrapper);

        mainLayout.getChildren().addAll(title, boardRow);
        root.getChildren().add(mainLayout);

        Scene scene = new Scene(root);
        stage.setTitle("Quax");
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();

        if (!humanIsBlack) {
            triggerBotMove();
        }
    }

    private Pane buildBoardPane() {
        Pane boardPane = new Pane();
        boardPane.setStyle("-fx-background-color: #f5f0e8;");

        double boardPixelSize = BOARD_SIZE * CELL_SIZE + OFFSET * 2;
        boardPane.setPrefSize(boardPixelSize, boardPixelSize);
        boardPane.setMinSize(boardPixelSize, boardPixelSize);
        boardPane.setMaxSize(boardPixelSize, boardPixelSize);

        drawBorders(boardPane);
        drawOctagons(boardPane);
        drawRhombics(boardPane);
        drawLabels(boardPane);

        return boardPane;
    }

    private VBox buildLeftPanel() {
        VBox panel = new VBox(20);
        panel.setAlignment(Pos.TOP_LEFT);
        panel.setPrefWidth(220);
        panel.setPrefHeight(220);
        panel.setMaxWidth(220);
        panel.setMaxHeight(220);
        panel.setPadding(new Insets(20));
        panel.setStyle("-fx-background-color: #ebe5d8; -fx-background-radius: 12;");

        // turn indicator
        turnLabel = new Label("Current Turn: BLACK");
        turnLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        turnLabel.setTextFill(Color.web("#2c2c2c"));
        turnLabel.setWrapText(true);

        // player colour label
        playerColourLabel = new Label();
        playerColourLabel.setFont(Font.font("Arial", 13));
        playerColourLabel.setTextFill(Color.web("#5c4a32"));
        playerColourLabel.setWrapText(true);
        updatePlayerColourLabel();

        // status label for messages
        statusLabel = new Label("");
        statusLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        statusLabel.setTextFill(Color.web("#8b4513"));
        statusLabel.setWrapText(true);

        Rectangle separator = new Rectangle(180, 2);
        separator.setFill(Color.web("#c8b89a"));

        // legend
        VBox legend = buildLegend();

        panel.getChildren().addAll(turnLabel, playerColourLabel, statusLabel, separator, legend);
        return panel;
    }

    private VBox buildRightPanel() {
        VBox panel = new VBox(20);
        panel.setAlignment(Pos.TOP_LEFT);
        panel.setPrefWidth(220);
        panel.setPrefHeight(220);
        panel.setMaxWidth(220);
        panel.setMaxHeight(220);
        panel.setPadding(new Insets(20));
        panel.setStyle("-fx-background-color:  #ebe5d8; -fx-background-radius: 12;");

        Label controlsTitle = new Label("Controls");
        controlsTitle.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        controlsTitle.setTextFill(Color.web("#2c2c2c"));

        Rectangle separator = new Rectangle(180, 2);
        separator.setFill(Color.web("#c8b89a"));

        // pie rule button
        pieRuleButton = createStyledButton("Pie Rule", "#e94560");
        pieRuleButton.setVisible(true);
        pieRuleButton.setDisable(true);
        pieRuleButton.setOnAction(e -> {
            statusLabel.setText("Colours switched!");
            humanIsBlack = true;
            humanColour = 1;
            isBlackTurn = false;
            pieRuleButton.setDisable(false);
            updateTurnLabel();
            updatePlayerColourLabel();

            ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
            scheduler.schedule(() -> Platform.runLater(() -> statusLabel.setText("")), 2000, TimeUnit.MILLISECONDS);
            scheduler.shutdown();

            triggerBotMove();
        });

        Rectangle separator2 = new Rectangle(180, 2);
        separator2.setFill(Color.web("#c8b89a"));

        // dev mode button
        devModeButton = createStyledButton("Dev Mode: OFF", "#0f3460");
        devModeButton.setOnAction(e -> {
            devMode = !devMode;
            devModeButton.setText(devMode ? "Dev Mode: ON" : "Dev Mode: OFF");
            if (!devMode) {
                confirmBotMoveButton.setVisible(false);
                devStrategyLabel.setText("");
                clearBotHighlight();
                // if there was a pending bot move, execute it now instead of dropping it
                if (pendingBotMove != null) {
                    int[] moveToExecute = pendingBotMove;
                    pendingBotMove = null;
                    executeBotMove(moveToExecute);
                }

            }
        });

        // confirm bot move button — only visible in dev mode
        confirmBotMoveButton = createStyledButton("Confirm Bot Move", "#0f3460");
        confirmBotMoveButton.setVisible(false);
        confirmBotMoveButton.setOnAction(e -> {
            if (pendingBotMove != null) {
                confirmBotMoveButton.setVisible(false);
                devStrategyLabel.setText("");
                clearBotHighlight();
                executeBotMove(pendingBotMove);
                pendingBotMove = null;
            }
        });

        // dev strategy label
        devStrategyLabel = new Label("");
        devStrategyLabel.setFont(Font.font("Arial", 12));
        devStrategyLabel.setTextFill(Color.web("#1a6b8a"));
        devStrategyLabel.setWrapText(true);

        panel.getChildren().addAll(
                controlsTitle,
                separator,
                pieRuleButton,
                separator2,
                devModeButton,
                confirmBotMoveButton,
                devStrategyLabel
        );
        return panel;
    }

    private VBox buildLegend() {
        VBox legend = new VBox(10);
        legend.setPadding(new Insets(20, 0, 0, 0));


        HBox blackRow = new HBox(10);
        blackRow.setAlignment(Pos.CENTER_LEFT);
        Circle blackCircle = new Circle(8, BLACK_STONE);
        Label blackLabel = new Label("Black — top to bottom");
        blackLabel.setFont(Font.font("Arial", 12));
        blackLabel.setTextFill(Color.web("#aaaaaa"));
        blackRow.getChildren().addAll(blackCircle, blackLabel);

        HBox whiteRow = new HBox(10);
        whiteRow.setAlignment(Pos.CENTER_LEFT);
        Circle whiteCircle = new Circle(8, WHITE_STONE);
        whiteCircle.setStroke(Color.web("#555555"));
        Label whiteLabel = new Label("White — left to right");
        whiteLabel.setFont(Font.font("Arial", 12));
        whiteLabel.setTextFill(Color.web("#aaaaaa"));
        whiteRow.getChildren().addAll(whiteCircle, whiteLabel);

        legend.getChildren().addAll(blackRow, whiteRow);
        return legend;
    }

    private Button createStyledButton(String text, String bgColor) {
        Button btn = new Button(text);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        btn.setStyle(
                "-fx-background-color: " + bgColor + ";" +
                        "-fx-text-fill: white;" +
                        "-fx-background-radius: 8;" +
                        "-fx-padding: 10 20 10 20;" +
                        "-fx-cursor: hand;"
        );
        return btn;
    }

    private void drawBorders(Pane root) {
        double totalSize = BOARD_SIZE * CELL_SIZE;
        double boardLeft = OFFSET - CELL_SIZE / 2.0;
        double boardTop = OFFSET - CELL_SIZE / 2.0;
        double barThickness = 12;

        // black wins top to bottom — red borders on top and bottom
        Rectangle topBar = new Rectangle(boardLeft, boardTop - barThickness - 2, totalSize, barThickness);
        Rectangle bottomBar = new Rectangle(boardLeft, boardTop + totalSize + 2, totalSize, barThickness);
        topBar.setFill(BLACK_BORDER_COLOR);
        bottomBar.setFill(BLACK_BORDER_COLOR);
        topBar.setArcWidth(6);
        topBar.setArcHeight(6);
        bottomBar.setArcWidth(6);
        bottomBar.setArcHeight(6);

        // white wins left to right — orange borders on left and right
        Rectangle leftBar = new Rectangle(boardLeft - barThickness - 2, boardTop, barThickness, totalSize);
        Rectangle rightBar = new Rectangle(boardLeft + totalSize + 2, boardTop, barThickness, totalSize);
        leftBar.setFill(WHITE_BORDER_COLOR);
        rightBar.setFill(WHITE_BORDER_COLOR);
        leftBar.setArcWidth(6);
        leftBar.setArcHeight(6);
        rightBar.setArcWidth(6);
        rightBar.setArcHeight(6);

        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.web("#000000", 0.5));
        shadow.setRadius(8);
        for (Rectangle bar : new Rectangle[]{topBar, bottomBar, leftBar, rightBar}) {
            bar.setEffect(shadow);
        }

        root.getChildren().addAll(topBar, bottomBar, leftBar, rightBar);


    }

    private void addBorderLabel(Pane root, String text, double x, double y) {
        Label label = new Label(text);
        label.setFont(Font.font("Arial", FontWeight.BOLD, 10));
        label.setTextFill(Color.web("#aaaaaa"));
        label.setLayoutX(x);
        label.setLayoutY(y);
        root.getChildren().add(label);
    }

    private void drawOctagons(Pane root) {
        for (int r = 0; r < BOARD_SIZE; r++) {
            for (int c = 0; c < BOARD_SIZE; c++) {
                double x = c * CELL_SIZE + OFFSET;
                double y = r * CELL_SIZE + OFFSET;

                Polygon octagon = createOctagon(x, y, CELL_SIZE);
                octagon.setFill(OCTAGON_COLOR);
                octagon.setStroke(TILE_OUTLINE);
                octagon.setStrokeWidth(1.5);

                DropShadow shadow = new DropShadow();
                shadow.setColor(Color.web("#000000", 0.4));
                shadow.setRadius(4);
                octagon.setEffect(shadow);

                final int finalR = r;
                final int finalC = c;

                octagon.setOnMouseClicked(event -> attemptPlaceOnOctagon(finalR, finalC));

                octagon.setOnMouseEntered(e -> {
                    if (octagonOwner[finalR][finalC] == 0 && !isPendingBotTile(finalR, finalC, 0)) {
                        octagon.setFill(OCTAGON_HOVER_COLOR);
                    }
                });

                octagon.setOnMouseExited(e -> {
                    if (octagonOwner[finalR][finalC] == 0 && !isPendingBotTile(finalR, finalC, 0)) {
                        octagon.setFill(OCTAGON_COLOR);
                    }
                });

                octagons[r][c] = octagon;
                root.getChildren().add(octagon);
            }
        }
    }

    private void drawRhombics(Pane root) {
        for (int r = 0; r < BOARD_SIZE - 1; r++) {
            for (int c = 0; c < BOARD_SIZE - 1; c++) {
                double x = c * CELL_SIZE + OFFSET + CELL_SIZE / 2.0;
                double y = r * CELL_SIZE + OFFSET + CELL_SIZE / 2.0;

                Rectangle rhombic = createRhombic(x, y, CELL_SIZE / 2.8);
                rhombic.setFill(RHOMBIC_COLOR);
                rhombic.setStroke(TILE_OUTLINE);
                rhombic.setStrokeWidth(1);

                final int finalR = r;
                final int finalC = c;

                rhombic.setOnMouseClicked(e -> attemptPlaceOnRhombic(finalR, finalC));

                rhombic.setOnMouseEntered(e -> {
                    if (rhombicOwner[finalR][finalC] == 0 && !isPendingBotTile(finalR, finalC, 1)) {
                        rhombic.setFill(RHOMBIC_HOVER_COLOR);
                    }
                });

                rhombic.setOnMouseExited(e -> {
                    if (rhombicOwner[finalR][finalC] == 0 && !isPendingBotTile(finalR, finalC, 1)) {
                        rhombic.setFill(RHOMBIC_COLOR);
                    }
                });

                rhombics[r][c] = rhombic;
                root.getChildren().add(rhombic);
            }
        }
    }

    private void drawLabels(Pane root) {
        Font labelFont = Font.font("Arial", FontWeight.BOLD, 11);
        double boardLeft = OFFSET - CELL_SIZE / 2.0;
        double boardTop = OFFSET - CELL_SIZE / 2.0;
        double totalSize = BOARD_SIZE * CELL_SIZE;
        double barThickness = 12;

        for (int i = 0; i < BOARD_SIZE; i++) {
            double pos = i * CELL_SIZE + OFFSET;

            char letter = (char) ('A' + i);

            Text topText = new Text(String.valueOf(letter));
            topText.setFont(labelFont);
            topText.setFill(Color.web("#aaaaaa"));
            topText.setX(pos - topText.getLayoutBounds().getWidth() / 2);
            topText.setY(boardTop - barThickness - 6);

            Text bottomText = new Text(String.valueOf(letter));
            bottomText.setFont(labelFont);
            bottomText.setFill(Color.web("#aaaaaa"));
            bottomText.setX(pos - bottomText.getLayoutBounds().getWidth() / 2);
            bottomText.setY(boardTop + totalSize + barThickness + 14);

            String number = String.valueOf(i + 1);

            Text leftText = new Text(number);
            leftText.setFont(labelFont);
            leftText.setFill(Color.web("#aaaaaa"));
            leftText.setX(boardLeft - barThickness - 18);
            leftText.setY(pos + 4);

            Text rightText = new Text(number);
            rightText.setFont(labelFont);
            rightText.setFill(Color.web("#aaaaaa"));
            rightText.setX(boardLeft + totalSize + barThickness + 6);
            rightText.setY(pos + 4);

            root.getChildren().addAll(topText, bottomText, leftText, rightText);
        }
    }

    private boolean isPendingBotTile(int r, int c, int tileType) {
        return devMode && pendingBotMove != null
                && pendingBotMove[0] == r
                && pendingBotMove[1] == c
                && pendingBotMove[2] == tileType;
    }

    private Polygon createOctagon(double x, double y, double size) {
        double half = size / 2.0;
        double cut = size / 4.0;

        return new Polygon(
                x - half + cut, y - half,
                x + half - cut, y - half,
                x + half, y - half + cut,
                x + half, y + half - cut,
                x + half - cut, y + half,
                x - half + cut, y + half,
                x - half, y + half - cut,
                x - half, y - half + cut
        );
    }

    private Rectangle createRhombic(double x, double y, double size) {
        Rectangle square = new Rectangle(size, size);
        square.setX(x - size / 2.0);
        square.setY(y - size / 2.0);
        square.setRotate(45);
        square.setArcWidth(3);
        square.setArcHeight(3);
        return square;
    }

    private void updateTurnLabel() {
        turnLabel.setText("Current Turn: " + (isBlackTurn ? "BLACK" : "WHITE"));
    }

    public void switchTurns() {
        isBlackTurn = !isBlackTurn;
    }

    private void updatePlayerColourLabel() {
        String humanColourText = humanColour == 1 ? "BLACK" : "WHITE";
        String botColourText = humanColour == 1 ? "WHITE" : "BLACK";
        playerColourLabel.setText("You: " + humanColourText + "  |  Bot: " + botColourText);
    }

    public String getCurrentPlayerTurn() {
        return isBlackTurn ? "BLACK" : "WHITE";
    }

    // SR2.1 & SR2.2 : basic tile placement for octagons
    private void attemptPlaceOnOctagon(int row, int col) {
        if (gameOver) return;
        if (isBlackTurn && humanColour != 1 || !isBlackTurn && humanColour != 2) return;

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        if (!firstMoveDone) {
            firstMoveDone = true;
            if (humanIsBlack) {
                pieRuleButton.setDisable(true);
            }
        } else if (!pieRuleButton.isDisabled()) {
            pieRuleButton.setDisable(true);
        }

        if (octagonOwner[row][col] != 0) {
            statusLabel.setText("Cell occupied!");
            scheduler.schedule(() ->
                            Platform.runLater(() -> statusLabel.setText("")),
                    1500, TimeUnit.MILLISECONDS);
            scheduler.shutdown();
            return;
        }

        octagonOwner[row][col] = isBlackTurn ? 1 : 2;
        octagons[row][col].setFill(isBlackTurn ? BLACK_STONE : WHITE_STONE);
        if (!isBlackTurn) octagons[row][col].setStroke(Color.web("#cccccc"));

        int currentPlayer = isBlackTurn ? 1 : 2;
        WinChecker winChecker = new WinChecker(BOARD_SIZE);
        if (winChecker.checkWin(octagonOwner, rhombicOwner, currentPlayer)) {
            gameOver = true;
            statusLabel.setText(currentPlayer == 1 ? "BLACK WINS!" : "WHITE WINS!");
            return;
        }

        statusLabel.setText("");
        switchTurns();
        updateTurnLabel();
        triggerBotMove();
    }

    private void attemptPlaceOnRhombic(int row, int col) {
        if (gameOver) return;
        if (isBlackTurn && humanColour != 1 || !isBlackTurn && humanColour != 2) return;

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        if (!firstMoveDone) {
            firstMoveDone = true;
            if (humanIsBlack) {
                pieRuleButton.setDisable(true);
            }
        } else if (!pieRuleButton.isDisabled()) {
            pieRuleButton.setDisable(true);
        }

        if (rhombicOwner[row][col] != 0) {
            statusLabel.setText("Cell occupied!");
            scheduler.schedule(() ->
                            Platform.runLater(() -> statusLabel.setText("")),
                    2, TimeUnit.SECONDS);
            scheduler.shutdown();
            return;
        }

        rhombicOwner[row][col] = isBlackTurn ? 1 : 2;
        rhombics[row][col].setFill(isBlackTurn ? BLACK_STONE : WHITE_STONE);

        int currentPlayer = isBlackTurn ? 1 : 2;
        WinChecker winChecker = new WinChecker(BOARD_SIZE);
        if (winChecker.checkWin(octagonOwner, rhombicOwner, currentPlayer)) {
            gameOver = true;
            statusLabel.setText(currentPlayer == 1 ? "BLACK WINS!" : "WHITE WINS!");
            return;
        }

        statusLabel.setText("");
        switchTurns();
        updateTurnLabel();
        triggerBotMove();
    }

    private void triggerBotMove() {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.schedule(() -> Platform.runLater(() -> doBotMove()), 500, TimeUnit.MILLISECONDS);
        scheduler.shutdown();
    }

    private void doBotMove() {
        if (gameOver) return;

        // handle pie rule
        if (humanIsBlack && firstMoveDone && pieRuleButton.isVisible() && !pieRuleResolved) {
            pieRuleResolved = true;
            pieRuleButton.setDisable(true);

            if (bot.decidePieRule()) {
                statusLabel.setText("Bot used Pie Rule!");
                humanIsBlack = false;
                humanColour = 2;
                isBlackTurn = false;
                updateTurnLabel();
                updatePlayerColourLabel();
                ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
                scheduler.schedule(() -> Platform.runLater(() -> statusLabel.setText("")), 1500, TimeUnit.MILLISECONDS);
                scheduler.shutdown();
                return;
            }
        }

        int botColour = humanIsBlack ? 2 : 1;
        int[] move = bot.chooseMove(octagonOwner, rhombicOwner, botColour);
        if (move == null) return;

        if (devMode) {
            pendingBotMove = move;
            highlightBotMove(move);
            devStrategyLabel.setText("Strategy: " + bot.getLastStrategy()
                    + "\n→ " + (move[2] == 0 ? "Octagon" : "Rhombic")
                    + " [" + move[0] + "][" + move[1] + "]");
            confirmBotMoveButton.setVisible(true);
        } else {
            executeBotMove(move);
        }
    }

    private void highlightBotMove(int[] move) {
        if (move[2] == 0) {
            octagons[move[0]][move[1]].setFill(PENDING_COLOR);
        } else {
            rhombics[move[0]][move[1]].setFill(PENDING_COLOR);
        }
    }

    private void clearBotHighlight() {
        if (pendingBotMove == null) return;
        if (pendingBotMove[2] == 0) {
            octagons[pendingBotMove[0]][pendingBotMove[1]].setFill(OCTAGON_COLOR);
        } else {
            rhombics[pendingBotMove[0]][pendingBotMove[1]].setFill(RHOMBIC_COLOR);
        }
    }

    private void executeBotMove(int[] move) {
        int r = move[0];
        int c = move[1];
        int tileType = move[2];
        int botColour = humanIsBlack ? 2 : 1;

        if (tileType == 0) {
            octagonOwner[r][c] = botColour;
            octagons[r][c].setFill(botColour == 1 ? BLACK_STONE : WHITE_STONE);
            if (botColour == 2) octagons[r][c].setStroke(Color.web("#cccccc"));
        } else {
            rhombicOwner[r][c] = botColour;
            rhombics[r][c].setFill(botColour == 1 ? BLACK_STONE : WHITE_STONE);
        }

        WinChecker winChecker = new WinChecker(BOARD_SIZE);
        if (winChecker.checkWin(octagonOwner, rhombicOwner, botColour)) {
            gameOver = true;
            statusLabel.setText(botColour == 1 ? "BLACK WINS!" : "WHITE WINS!");
            return;
        }

        if (!humanIsBlack && !pieRuleResolved) {
            pieRuleResolved = true;
            firstMoveDone = true;
            pieRuleButton.setDisable(false);
            switchTurns();
            updateTurnLabel();
            return;
        }

        switchTurns();
        updateTurnLabel();
    }

    // doPieRule kept for compatibility
    public void doPieRule() {
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                if (octagonOwner[row][col] != 0) {
                    if (octagonOwner[row][col] == 1) {
                        octagonOwner[row][col] = 2;
                        octagons[row][col].setFill(WHITE_STONE);
                    } else {
                        octagonOwner[row][col] = 1;
                        octagons[row][col].setFill(BLACK_STONE);
                    }
                }
            }
        }
        for (int row = 0; row < BOARD_SIZE - 1; row++) {
            for (int col = 0; col < BOARD_SIZE - 1; col++) {
                if (rhombicOwner[row][col] != 0) {
                    if (rhombicOwner[row][col] == 1) {
                        rhombicOwner[row][col] = 2;
                        rhombics[row][col].setFill(WHITE_STONE);
                    } else {
                        rhombicOwner[row][col] = 1;
                        rhombics[row][col].setFill(BLACK_STONE);
                    }
                }
            }
        }
    }
}