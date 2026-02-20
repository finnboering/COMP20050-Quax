// base class for rhombic and octagon tiles & stores the grid position of a tiles
public abstract class Tile {

    protected int row; // row position

    protected int col; // column position

    // this creates the tile at a given position
    public Tile(int row, int col) {
        this.row = row;
        this.col = col;
    }

}

