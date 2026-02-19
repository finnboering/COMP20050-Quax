import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class OctagonalCellTest {

    @Test
    void testOctagonalCellInheritance() {
        OctagonalCell cell = new OctagonalCell(1, 1);

        assertTrue(cell instanceof Cell); //confirm inheritance from cell
        assertEquals(1, cell.x); //make sure the constructor correctly passes the value to teh superclass
        assertEquals(1, cell.y);
    }
}
