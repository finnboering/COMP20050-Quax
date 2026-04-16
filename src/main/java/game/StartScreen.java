package game;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class StartScreen extends Application {

    @Override
    public void start(Stage stage) {
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #1a1a2e;");

        Label title = new Label("QUAX");
        title.setFont(Font.font(48));
        title.setTextFill(Color.SKYBLUE);

        Label subtitle = new Label("Choose your colour");
        subtitle.setFont(Font.font(18));
        subtitle.setTextFill(Color.WHITE);

        Button playBlackBtn = new Button("Play as Black");
        Button playWhiteBtn = new Button("Play as White");

        //the buttons
        String btnStyle = "-fx-font-size: 16px; -fx-min-width: 160px; -fx-min-height: 45px; " +
                "-fx-cursor: hand; -fx-background-radius: 8;";

        playBlackBtn.setStyle(btnStyle + "-fx-background-color: #111111; -fx-text-fill: white;");
        playWhiteBtn.setStyle(btnStyle + "-fx-background-color: #f0f0f0; -fx-text-fill: black;");

        //launch the game with the chosen colour
        playBlackBtn.setOnAction(e -> launchGame(stage, true));
        playWhiteBtn.setOnAction(e -> launchGame(stage, false));

        root.getChildren().addAll(title, subtitle, playBlackBtn, playWhiteBtn);

        Scene scene = new Scene(root, 360, 260);
        stage.setTitle("Quax");
        stage.setScene(scene);
        stage.show();
    }

    private void launchGame(Stage stage, boolean humanIsBlack) {
        QuaxApp game = new QuaxApp(humanIsBlack);
        try {
            game.start(stage);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}