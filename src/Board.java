public class Board {

    // size of the board 11x11
    private static final int SIZE = 11;

    // the 2d array that holds all the tiles behind the scenes
    private Tile[][] grid;

    public Board() {
        // sets up the empty grid and then fills it
        grid = new Tile[SIZE][SIZE];
        initialiseBoard();
    }

    private void initialiseBoard() {
        // loops through every row and column to place the tiles
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {

                // alternating between octagon and rhombic
                if ((i + j) % 2 == 0) {
                    grid[i][j] = new OctagonTile(i, j);
                } else {
                    grid[i][j] = new RhombicTile(i, j);
                }
            }
        }
    }

    public Tile getTile(int row, int col) {
        // helper : grabs a specific tile from the board
        return grid[row][col];
    }

}