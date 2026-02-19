/**
 * The Cell class represents a single position on the game board.
 * Each cell stores:
 * - Its x and y coordinates
 * - Whether it is currently occupied by a player
 * This is the base class for all specific cell types
  (e.g.OctagonalCell and RhombicCell).
 */


public class Cell {

    protected int x;           // x-coordinate of the cell
    protected int y;           // y-coordinate of the cell
    protected boolean occupied; // indicates whether the cell is occupied

    /**
     * Constructs a new Cell at the given coordinates.
     * By default, a new cell is not occupied.
     */
    public Cell(int x, int y) {
        this.x = x;
        this.y = y;
        this.occupied = false;
    }


    // Returns whether the current cell is occupied
    public boolean isOccupied() {
        return occupied;
    }

    //Sets the occupied state of this cell.
    public void setOccupied(boolean occupied) {
        this.occupied = occupied;
    }
}