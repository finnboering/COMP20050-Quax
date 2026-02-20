/**
 * The Board class represents the game board
 * It creates and stores an 11x11 grid of Cell objects
 * The board alternates between OctagonalCell and
 * RhombicCell based on position
 */

public class Board {

    // size of the board 11x11
    private static final int SIZE = 11;
    private Cell[][] grid;

    // Constructs the board and initializes all cells.
    public Board() {
        grid = new Cell[SIZE][SIZE];
        initialiseBoard();
    }


     //Initializes the board grid
     //Alternates cell types based on coordinate parity


    private void initialiseBoard() {
        // loops through every row and column to place the tiles
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {

                // alternating between octagon and rhombic
                if ((i + j) % 2 == 0) {
                    grid[i][j] = new OctagonalCell(i, j);
                } else {
                    grid[i][j] = new RhombicCell(i, j);
                }
            }
        }
    }

    //Returns the cell at the specified coordinates

    public Cell getCell(int row, int col) {
        return grid[row][col];
    }

}

