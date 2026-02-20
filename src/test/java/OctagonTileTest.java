import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the OctagonTile class.
 *
 * This class verifies that:
 *  - The constructor correctly assigns row and column.
 *  - The class properly inherits from Tile.
 */
public class OctagonTileTest {

    /**
     * Unit Test:
     * Verifies that row and column values are stored correctly.
     */
    @Test
    void testConstructorInitialisesCoordinates() {
        OctagonTile tile = new OctagonTile(2, 5);

        assertEquals(2, tile.row,
                "Row should be correctly assigned in constructor");

        assertEquals(5, tile.col,
                "Column should be correctly assigned in constructor");
    }

    /**
     * Unit Test:
     * Verifies that OctagonTile is a subclass of Tile.
     */
    @Test
    void testInheritanceFromTile() {
        OctagonTile tile = new OctagonTile(0, 0);

        assertTrue(tile instanceof Tile,
                "OctagonTile should inherit from Tile");
    }

    /**
     * Unit Test:
     * Verifies multiple objects store independent state.
     */
    @Test
    void testMultipleInstancesHaveDifferentCoordinates() {
        OctagonTile tile1 = new OctagonTile(1, 1);
        OctagonTile tile2 = new OctagonTile(3, 4);

        assertNotEquals(tile1.row, tile2.row, "Each tile instance should store its own row value");

        assertNotEquals(tile1.col, tile2.col, "Each tile instance should store its own column value");
    }
}
