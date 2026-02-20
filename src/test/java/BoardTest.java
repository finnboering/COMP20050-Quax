import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit and integration tests for the Board class
 * These tests verify:
 *  -The board is correctly initialised to 11x11
 *  -Tiles are created properly
 *  -Alternating tile pattern is correct
 *  -getTile() correctly returns tiles from the grid
 */
public class BoardTest {

    @Test
    void testBoardInitialization() {
        Board board = new Board();  //verifies that a Board object is correctly created

        assertNotNull(board, "Board should be successfully created");
        assertNotNull(board.getTile(0, 0));  //ensure edge cells exist
        assertNotNull(board.getTile(10, 10));
    }

    /**
     * Unit Test:
     * the board contains 11 rows and 11 columns
     */
    @Test
    void testBoardSize() {
        Board board = new Board();

        for (int row = 0; row < 11; row++) {
            for (int col = 0; col < 11; col++) {
                assertNotNull(board.getTile(row, col), "Each tile in the grid should be initialised");
            }
        }
    }

    /**
     * Unit Test:
     * that tiles alternate correctly between OctagonTile and RhombicTile
     */
    @Test
    void testAlternatingTilePattern() {
        Board board = new Board();

        Tile tile00 = board.getTile(0, 0);
        Tile tile01 = board.getTile(0, 1);

        assertTrue(tile00 instanceof OctagonTile, "(0,0) should be an OctagonTile");

        assertTrue(tile01 instanceof RhombicTile, "(0,1) should be a RhombicTile");
    }

    /**
     * Integration Test:
     * getTile() returns the correct tile object and that the tile type is consistent across the board
     */
    @Test
    void testGetTileIntegration() {
        Board board = new Board();

        Tile tile = board.getTile(5, 5);

        assertNotNull(tile, "getTile() should return a valid Tile object");

        // Since (5 + 5) % 2 == 0, it should be an OctagonTile
        assertTrue(tile instanceof OctagonTile, "Tile at (5,5) should be an OctagonTile");
    }
}

