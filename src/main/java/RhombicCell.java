/**
 * Represents a rhombic shaped cell on the board
 * This class extends Cell and does not add additional
 * behaviour, but allows the board to distinguish between
 * different cell types
 */

public class RhombicCell extends Cell {


    // Constructs a RhombicCell at the specified coordinates
    public RhombicCell(int x, int y) {
        super(x, y); // Calls the Cell constructor
    }
}