import javafx.application.Platform;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.*;

/*
 * QuaxAppTest

 * Integration test for JavaFX app startup.
 *
 * This verifies that:
 -JavaFX runtime initializes correctly
 -Game scene is created
 -App launches without crashing
 */

public class QuaxAppTest {

    @BeforeAll
    static void initJFX() throws Exception {
        CountDownLatch latch = new CountDownLatch(1); //wait until JavaFX thread starts

        Platform.startup(latch::countDown); //start JavaFX runtime once before tests run

        latch.await(); //wait until JavaFX is ready
    }

    @Test
    void testApplicationStarts() throws Exception {

        CountDownLatch latch = new CountDownLatch(1);

        Platform.runLater(() -> { //UI code runs on JavaFX app thread
            try {

                QuaxApp app = new QuaxApp(); // Create application instance
                Stage stage = new Stage(); // Create new window stage for testing

                app.start(stage); //Launch app UI

                assertNotNull(stage.getScene()); //Verify that scene was successfully created

                stage.close();
            } finally {
                latch.countDown();
            }
        });

        latch.await(); //wait for UI thread test execution to finish
    }
}