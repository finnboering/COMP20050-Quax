import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


/*
 * TurnTest
 *
 * Tests turn switching in the game
 *
 * Ensures that:
 -Game starts with BLACK turn
 -Turn switching toggles
 */


public class TurnTest {

    @Test
    void testTurnSwitching() {
        QuaxApp app = new QuaxApp();

        // default should start as BLACK
        assertEquals("BLACK", app.getCurrentPlayerTurn());

        app.switchTurns();
        assertEquals("WHITE", app.getCurrentPlayerTurn());

        app.switchTurns();
        assertEquals("BLACK", app.getCurrentPlayerTurn());
    }
}