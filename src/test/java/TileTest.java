import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/*
 * TileTest
 *
 * Tests base Tile class functionality.
 *
 * Verifies:
 -Tile ownership
 -Player tile assignment
 -Empty tile detection
 -Coordinate correctness
 */

public class TileTest {

    @Test
    void testTileStartsEmpty() {
        OctagonTile tile = new OctagonTile(2,3);

        assertEquals(0, tile.getPlayerTile());
        assertTrue(tile.hasNoTile());
    }

    @Test
    void testSetPlayerTileBlack() {
        OctagonTile tile = new OctagonTile(0,0);

        tile.setPlayerTile(1);

        assertEquals(1, tile.getPlayerTile());
        assertFalse(tile.hasNoTile());
    }

    @Test
    void testSetPlayerTileWhite() {
        RhombicTile tile = new RhombicTile(1,2);

        tile.setPlayerTile(2);

        assertEquals(2, tile.getPlayerTile());
        assertFalse(tile.hasNoTile());
    }

    @Test
    void testTileCoordinatesStoredCorrectly() {
        RhombicTile tile = new RhombicTile(5,7);

        assertEquals(5, tile.row);
        assertEquals(7, tile.col);
    }
}