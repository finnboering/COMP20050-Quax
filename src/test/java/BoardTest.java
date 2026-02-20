import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class BoardTest {

    @Test
    void testBoardInitialization() {
        Board board = new Board();  //make the grid is correctly created

        assertNotNull(board.getCell(0, 0));  //ensure edge cells exist
        assertNotNull(board.getCell(10, 10));
    }

    @Test
    void testAlternatingCellTypes() {
        Board board = new Board();    //make sure octagonal and rhombic cells are properly created the

        assertTrue(board.getCell(0,0) instanceof OctagonalCell);
        assertTrue(board.getCell(0,1) instanceof RhombicCell);
        assertTrue(board.getCell(1,0) instanceof RhombicCell);
        assertTrue(board.getCell(1,1) instanceof OctagonalCell);
    }


    //integration test for interaction between the board and cells
    @Test
    void testPlacingPieceUpdatesBoard() {
        Board board = new Board();

        Cell cell = board.getCell(2, 3);
        assertFalse(cell.isOccupied());

        cell.setOccupied(true);

        assertTrue(board.getCell(2, 3).isOccupied());
    }

}