import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Tile hierarchy
 * These tests verify:
 *  - Tiles correctly store row and column values
 *  - Subclasses OctagonalTile and RhombicTile properly inherit from Tile
 */
public class TileTest {

    /**
     * Unit Test:
     * OctagonTile stores correct row and column
     */
    @Test
    void testOctagonTileCoordinates() {
        OctagonTile tile = new OctagonTile(3, 7);

        assertEquals(3, tile.row, "Row value should be stored correctly");
        assertEquals(7, tile.col, "Column value should be stored correctly");
    }

    /**
     * Unit Test:
     * RhombicTile stores correct row and column.
     */
    @Test
    void testRhombicTileCoordinates() {
        RhombicTile tile = new RhombicTile(4, 2);

        assertEquals(4, tile.row, "Row value should be stored correctly");
        assertEquals(2, tile.col, "Column value should be stored correctly");
    }

    /**
     * Unit Test:
     * both subclasses extend Tile.
     */
    @Test
    void testInheritance() {
        OctagonTile octagon = new OctagonTile(0,0);
        RhombicTile rhombic = new RhombicTile(0,0);

        assertTrue(octagon instanceof Tile, "OctagonTile should inherit from Tile");

        assertTrue(rhombic instanceof Tile, "RhombicTile should inherit from Tile");
    }
}
