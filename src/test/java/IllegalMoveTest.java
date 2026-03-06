import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/*
 * IllegalMoveTest

 * Tests rule enforcement for invalid gameplay moves.
 *
 * This verifies that:
 -Players cannot overwrite occupied tiles
 -The first player's move remains unchanged after an illegal move attempt
 */


public class IllegalMoveTest {

    @Test
    void testCannotOverwriteTile() {
        Board board = new Board();

        boolean firstMove = board.tryPlacingOctagonTile(3,3,true);
        boolean secondMove = board.tryPlacingOctagonTile(3,3,false);

        assertTrue(firstMove);
        assertFalse(secondMove);

        // tile should still belong to first player
        assertEquals(1, board.getTile(3,3).getPlayerTile());
    }
}