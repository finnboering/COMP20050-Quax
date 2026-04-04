import javafx.application.Platform;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.*;

/*
 * QuaxAppTest
 * Integration test for the QuaxApp UI start up
 * This verifies that:
  -JavaFX runtime initializes correctly
  -Game scene is created when human plays as black
  -Game scene is created when human plays as white
  -App launches without crashing for both constructors
 */

public class QuaxAppTest {

    @BeforeAll
    static void initJFX() throws Exception {
        CountDownLatch latch = new CountDownLatch(1); // wait until JavaFX thread starts
        Platform.startup(latch::countDown); // start JavaFX runtime once before tests run
        latch.await(); // wait until JavaFX is ready
    }

    @Test
    void testApplicationStartsHumanBlack() throws Exception {

        CountDownLatch latch = new CountDownLatch(1);

        Platform.runLater(() -> { // UI code runs on JavaFX app thread
            try {
                QuaxApp app = new QuaxApp(true); // human plays as black
                Stage stage = new Stage();

                app.start(stage);

                assertNotNull(stage.getScene()); // verify scene was created

                stage.close();
            } catch (Exception ex) {
                fail("App threw an exception: " + ex.getMessage());
            } finally {
                latch.countDown();
            }
        });

        latch.await();
    }

    @Test
    void testApplicationStartsHumanWhite() throws Exception {

        CountDownLatch latch = new CountDownLatch(1);

        Platform.runLater(() -> {
            try {
                QuaxApp app = new QuaxApp(false); // human plays as white
                Stage stage = new Stage();

                app.start(stage);

                assertNotNull(stage.getScene());

                stage.close();
            } catch (Exception ex) {
                fail("App threw an exception: " + ex.getMessage());
            } finally {
                latch.countDown();
            }
        });

        latch.await();
    }

    @Test
    void testApplicationStartsDefaultConstructor() throws Exception {

        CountDownLatch latch = new CountDownLatch(1);

        Platform.runLater(() -> {
            try {
                QuaxApp app = new QuaxApp(); // default no-arg constructor
                Stage stage = new Stage();

                app.start(stage);

                assertNotNull(stage.getScene());

                stage.close();
            } catch (Exception ex) {
                fail("App threw an exception: " + ex.getMessage());
            } finally {
                latch.countDown();
            }
        });

        latch.await();
    }
}
