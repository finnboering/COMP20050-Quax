import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test for Main class.
 * ensures that the Main class exists and can be instantiated without errors.
 */
public class MainTest {

    @Test
    void testMainExists() {
        Main main = new Main();
        assertNotNull(main, "Main class should be instantiated");
    }
}
