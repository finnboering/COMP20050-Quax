import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the RhombicTile class.
 *
 * This class verifies that:
 *  - The constructor correctly assigns row and column.
 *  - The class properly inherits from Tile.
 */
public class RhombicTileTest {

    /**
     * Unit Test:
     * Verifies correct coordinate storage.
     */
    @Test
    void testConstructorInitialisesCoordinates() {
        RhombicTile tile = new RhombicTile(6, 8);

        assertEquals(6, tile.row, "Row should be correctly assigned in constructor");

        assertEquals(8, tile.col, "Column should be correctly assigned in constructor");
    }

    /**
     * Unit Test:
     * Verifies that RhombicTile inherits from Tile.
     */
    @Test
    void testInheritanceFromTile() {
        RhombicTile tile = new RhombicTile(0, 0);

        assertTrue(tile instanceof Tile, "RhombicTile should inherit from Tile");
    }

    /**
     * Unit Test:
     * Verifies separate instances maintain separate state.
     */
    @Test
    void testDifferentInstances() {
        RhombicTile tile1 = new RhombicTile(2, 2);
        RhombicTile tile2 = new RhombicTile(4, 5);

        assertNotEquals(tile1.row, tile2.row, "Each tile should store its own row");

        assertNotEquals(tile1.col, tile2.col, "Each tile should store its own column");
    }
}
