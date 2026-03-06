import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/*
 * RhombicTileTest
 *
 *Tests rhombic tile functionality:
 -Coordinate storage
 -Tile inheritance
 -Initial empty tile state
 */

public class RhombicTileTest {

    @Test
    void testConstructorCoordinates() {
        RhombicTile tile = new RhombicTile(6,8);

        assertEquals(6, tile.row);
        assertEquals(8, tile.col);
    }

    @Test
    void testInheritance() {
        RhombicTile tile = new RhombicTile(0,0);

        assertTrue(tile instanceof Tile);
    }

    @Test
    void testTileInitiallyEmpty() {
        RhombicTile tile = new RhombicTile(2,3);

        assertTrue(tile.hasNoTile());
    }
}