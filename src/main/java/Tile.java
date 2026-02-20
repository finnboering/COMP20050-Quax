// base class for rhombic and octagon tiles & stores the grid position of a tiles
public abstract class Tile {

    int row; // row pos
    int col; // col pos

    // 0 is empty | 1 is black | 2 is white
    int playerTile;

    // sets up a new tile with its grid position and marks it as empty
    public Tile(int row, int col) {
        this.row = row;
        this.col = col;
        this.playerTile = 0;
    }

    // gets who owns the tile
    public int getPlayerTile() {
        return playerTile;
    }
    // updates the tile to belong to a player
    public void setPlayerTile(int playerID) {
        this.playerTile = playerID;
    }

    // checks if the tile is still empty so someone can play on it
    public boolean hasNoTile() {
        return playerTile == 0;
    }

    // public int getRow() { return row; }
    // public int getCol() { return col; }
}
