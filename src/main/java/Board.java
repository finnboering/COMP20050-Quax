public class Board {

    // size of the board
    int BOARD_SIZE = 11;
    //single grid for all tiles
    private Tile[][] grid;

    // separate arrays to hold octagons and rhombics
    private Tile[][] octagons;
    private Tile[][] rhombics;

    public Board() {
        // initalize the empty grids
        grid = new Tile[BOARD_SIZE][BOARD_SIZE];
        octagons = new Tile[BOARD_SIZE][BOARD_SIZE];
        rhombics = new Tile[BOARD_SIZE - 1][BOARD_SIZE - 1];
        initialiseBoard();
    }



    private void initialiseBoard() {
        // fills the 11x11 array with octagons and updates grid
        for (int r = 0; r < BOARD_SIZE; r++) {
            for (int c = 0; c < BOARD_SIZE; c++) {
                if ((r + c) % 2 == 0) {
                    OctagonTile oct = new OctagonTile(r, c);
                    octagons[r][c] = oct;
                    grid[r][c] = oct;
                } else {
                    RhombicTile rhom = new RhombicTile(r, c);
                    if (r < BOARD_SIZE - 1 && c < BOARD_SIZE - 1) {
                        rhombics[r][c] = rhom;
                    }
                    grid[r][c] = rhom;
                }
            }
        }
    }

    // SR2.1 & SR2.2 : basic tile placement for octagons
    public boolean tryPlacingOctagonTile(int r, int c, boolean isBlackTurn) {

        Tile clickedTile = octagons[r][c];

        // only allow placement if it has no tile yet
        if (clickedTile.hasNoTile()) {

            // assign the tile to the current player
            if (isBlackTurn) {
                clickedTile.setPlayerTile(1); // black
            } else {
                clickedTile.setPlayerTile(2); // white
            }

            return true; // tile placed successfully
        }

        return false; // placement failed
    }

    public Tile getTile(int row, int col) {
        return grid[row][col];
    }


    public int getOctagonOwner(int row, int col) {
        if (octagons[row][col] != null) {
            return octagons[row][col].getPlayerTile();
        }
        return 0;
    }

    public int getRhombicOwner(int row, int col) {
        if (rhombics[row][col] != null) {
            return rhombics[row][col].getPlayerTile();
        }
        return 0;
    }
}
