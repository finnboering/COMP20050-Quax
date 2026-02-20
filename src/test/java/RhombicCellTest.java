import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


public class RhombicCellTest {


    @Test
    void testRhombicCellInheritance() {
        RhombicCell cell = new RhombicCell(2, 2);

        assertTrue(cell instanceof Cell);//confirm inheritance from cell
        assertEquals(2, cell.x);//make sure the constructor correctly passes the valu
        assertEquals(2, cell.y);
    }
}
