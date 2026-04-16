import game.QuaxApp;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


/*
 * TurnTest
 *
 * Tests turn switching in the game.
 *
 * Ensures that:
 -Game starts with BLACK turn when human plays as black
 -Game starts with BLACK turn when human plays as white (internal state starts as black)
 -Turn switching toggles correctly between BLACK and WHITE
 -Multiple consecutive switches work correctly
 */


public class TurnTest {

    @Test
    void testTurnSwitchingHumanBlack() {
        QuaxApp app = new QuaxApp(true); // human plays as black

        // should start as BLACK
        assertEquals("BLACK", app.getCurrentPlayerTurn());

        app.switchTurns();
        assertEquals("WHITE", app.getCurrentPlayerTurn());

        app.switchTurns();
        assertEquals("BLACK", app.getCurrentPlayerTurn());
    }

    @Test
    void testTurnSwitchingHumanWhite() {
        QuaxApp app = new QuaxApp(false); // human plays as white

        // internal state still starts as BLACK (bot goes first)
        assertEquals("BLACK", app.getCurrentPlayerTurn());

        app.switchTurns();
        assertEquals("WHITE", app.getCurrentPlayerTurn());
    }

    @Test
    void testDefaultConstructorStartsAsBlack() {
        QuaxApp app = new QuaxApp();

        assertEquals("BLACK", app.getCurrentPlayerTurn());
    }

    @Test
    void testMultipleTurnSwitches() {
        QuaxApp app = new QuaxApp(true);

        // 5 flips should land on WHITE
        for (int i = 0; i < 5; i++) {
            app.switchTurns();
        }

        assertEquals("WHITE", app.getCurrentPlayerTurn());
    }
}
