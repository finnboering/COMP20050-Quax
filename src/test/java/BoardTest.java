import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/*
 * BoardTest
 *
 * Tests the board initialization,tile pattern generation,and basic move placement logic.
 * This class verifies that:
 -The board is created successfully
 -All tiles are initialized
 -The board follows the alternating octagon/rhombic pattern
 -Tile placement works correctly
 -Illegal moves like overwriting tiles are prevented
 */

public class BoardTest {

    @Test
    void testBoardCreation() {
        Board board = new Board();

        assertNotNull(board);
    }

    @Test
    void testAllTilesInitialised() {
        Board board = new Board();

        for(int r = 0; r < 11; r++) {
            for(int c = 0; c < 11; c++) {
                assertNotNull(board.getTile(r,c));
            }
        }
    }

    @Test
    void testAlternatingTilePattern() {
        Board board = new Board();

        Tile t1 = board.getTile(0,0);
        Tile t2 = board.getTile(0,1);
        Tile t3 = board.getTile(1,0);

        assertTrue(t1 instanceof OctagonTile);
        assertTrue(t2 instanceof RhombicTile);
        assertTrue(t3 instanceof RhombicTile);
    }

    @Test
    void testTryPlacingOctagonTileBlack() {
        Board board = new Board();

        boolean result = board.tryPlacingOctagonTile(0,0,true);

        assertTrue(result);

        Tile tile = board.getTile(0,0);
        assertEquals(1, tile.getPlayerTile());
    }

    @Test
    void testTryPlacingOctagonTileWhite() {
        Board board = new Board();

        boolean result = board.tryPlacingOctagonTile(0,0,false);

        assertTrue(result);

        Tile tile = board.getTile(0,0);
        assertEquals(2, tile.getPlayerTile());
    }

    @Test
    void testCannotOverwriteTile() {
        Board board = new Board();

        board.tryPlacingOctagonTile(0,0,true);

        boolean result = board.tryPlacingOctagonTile(0,0,false);

        assertFalse(result);
    }





}
