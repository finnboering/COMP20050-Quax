import javafx.application.Platform;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class QuaxAppTest {

    @Test
    void testApplicationStarts() throws Exception {

        Platform.startup(() -> {});  //start the javafx runtime

        Platform.runLater(() -> {
            try {
                QuaxApp app = new QuaxApp();
                Stage stage = new Stage();
                app.start(stage);             //simulates launching the app

                assertNotNull(stage.getScene()); //make sure a scene was created and attached to the stage
                stage.close();
            } catch (Exception e) {
                fail("Application failed to start: " + e.getMessage());
            }
        });
    }
}


//tests make sure that the app launches without crashing and that a scene was created