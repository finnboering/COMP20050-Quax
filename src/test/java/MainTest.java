import game.Main;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/*
 * MainTest

*This makes sure that the program entry point can be instantiated
 */

public class MainTest {

    @Test
    void testMainInstantiation() {
        Main main = new Main();

        assertNotNull(main);
    }

}