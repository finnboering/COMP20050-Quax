import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CellTest {

    @Test
    void testCellInitialState() {
        Cell cell = new Cell(2, 3); //creat a cell at pos (2,3) , x=2, y =3

        assertEquals(2, cell.x);
        assertEquals(3, cell.y);
        assertFalse(cell.isOccupied()); //make sure its empty
    }

    @Test
    void testSetOccupied() {
        Cell cell = new Cell(0, 0);

        cell.setOccupied(true);
        assertTrue(cell.isOccupied()); //making sure its occupied when set to be so

        cell.setOccupied(false);
        assertFalse(cell.isOccupied()); //making sure its empty when set to be so

    }
}