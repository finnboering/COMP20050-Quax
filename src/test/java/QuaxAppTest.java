import javafx.application.Platform;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import java.util.concurrent.CountDownLatch;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration test for the QuaxApp UI start up
 * This test verifies that:
 *  -JavaFX runtime starts correctly
 *  -application can create a Scene
 *  -the Stage is properly initialised and the scene is attached
 */
public class QuaxAppTest {

    @BeforeAll //ensures that the JavaFX runtime is started once before any test runs
    static void initJFX() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1); //used to pause execution until the JavaFX thread can be fully initialised
        Platform.startup(latch::countDown);//start JavaFX runtime
        latch.await(); //wait until JavaFX startup completes
    }

    /**
     * This test verifies that:
     *  -a QuaxApp instance can be created
     *  -the start (Stage) method executes
     *  -a Scene is attached to the Stage
     **/


    @Test
    void testApplicationStarts() throws InterruptedException {

        CountDownLatch latch = new CountDownLatch(1);

        Platform.runLater(() -> { //run on the JavaFX thread
            try {
                QuaxApp app = new QuaxApp(); //create an instance of the App
                Stage stage = new Stage(); //create an instance of the stage
                app.start(stage); //call the start

                assertNotNull(stage.getScene(), "Scene should be created and attached to Stage");

                stage.close();
            } finally {
                latch.countDown();
            }
        });

        latch.await();
    }
}


//tests make sure that the app launches without crashing and that a scene was created