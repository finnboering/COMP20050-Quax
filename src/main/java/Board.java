public class Board {

    // size of the board
    int BOARD_SIZE = 11;

    public Board() {
        // initalize the empty grids
        octagons = new Tile[BOARD_SIZE][BOARD_SIZE];
        rhombics = new Tile[BOARD_SIZE - 1][BOARD_SIZE - 1];
        initialiseBoard();
    }

    // separate arrays to hold octagons and rhombics
    Tile[][] octagons;
    Tile[][] rhombics;

    private void initialiseBoard() {
        // fills the 11x11 array with octagons
        for (int r = 0; r < BOARD_SIZE; r++) {
            for (int c = 0; c < BOARD_SIZE; c++) {
                octagons[r][c] = new OctagonTile(r, c);
            }
        }


        // fills the 10x10 array with rhombics
        for (int r = 0; r < BOARD_SIZE - 1; r++) {
            for (int c = 0; c < BOARD_SIZE - 1; c++) {
                rhombics[r][c] = new RhombicTile(r, c);
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

    // TODO : figure out rhombic tile placement later
}
