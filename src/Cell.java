public class Cell {

    protected int x;
    protected int y;
    protected boolean occupied;

    public Cell(int x, int y) {
        this.x = x;
        this.y = y;
        this.occupied = false;
    }

    public boolean isOccupied() {
        return occupied;
    }

    public void setOccupied(boolean occupied) {
        this.occupied = occupied;
    }
}