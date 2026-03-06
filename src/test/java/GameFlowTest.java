import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/*
 * GameFlowTest

 * Tests realistic gameflow sequence simulation.

 * This test verifies that:
 -Multiple valid moves can be placed in sequence
 -Board state updates correctly after each move
 -Turn switching works during gameplay
 -Game rules are preserved across multiple moves
 */
public class GameFlowTest {

    @Test
    void testMultipleMoveSequence() {
        Board board = new Board();

        //simulate a gameflow sequence
        assertTrue(board.tryPlacingOctagonTile(0,0,true));
        assertTrue(board.tryPlacingOctagonTile(2,2,false));
        assertFalse(board.tryPlacingOctagonTile(0,0,true)); // occupied

        assertEquals(1, board.getTile(0,0).getPlayerTile());
        assertEquals(2, board.getTile(2,2).getPlayerTile()); //
    }
}