import game.Board;
import game.OctagonTile;
import game.Tile;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/*
 * OctagonTileTest
 *
 Tests octagon tile functionality:
 -Coordinate storage
 -Inheritance from game.Tile class
 -Initial empty tile
 */

public class OctagonTileTest {

    @Test
    void testConstructorSetsCoordinates() {
        OctagonTile tile = new OctagonTile(3,4);

        assertEquals(3, tile.getRow());
        assertEquals(4, tile.getCol());
    }

    @Test
    void testInheritanceFromTile() {
        OctagonTile tile = new OctagonTile(0,0);

        assertTrue(tile instanceof Tile);
    }

    @Test
    void testNewTileIsEmpty() {
        OctagonTile tile = new OctagonTile(1,1);

        assertEquals(0, tile.getPlayerTile());
    }


    //Integration tests:octagon placement
    @Test
    void testPlaceOctagonTileBlack() {
        Board board = new Board();

        boolean result = board.tryPlacingOctagonTile(2, 2, true);

        assertTrue(result);
        assertEquals(1, board.getTile(2, 2).getPlayerTile());
    }

    @Test
    void testPlaceOctagonTileWhite() {
        Board board = new Board();

        boolean result = board.tryPlacingOctagonTile(4, 4, false);

        assertTrue(result);
        assertEquals(2, board.getTile(4, 4).getPlayerTile());
    }
}
