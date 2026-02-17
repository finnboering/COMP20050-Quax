public class Board {

    private static final int SIZE = 11;
    private Cell[][] grid;

    public Board() {
        grid = new Cell[SIZE][SIZE];
        initialiseBoard();
    }

    private void initialiseBoard() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {

                // For now, just alternate types
                if ((i + j) % 2 == 0) {
                    grid[i][j] = new OctagonalCell(i, j);
                } else {
                    grid[i][j] = new RhombicCell(i, j);
                }
            }
        }
    }

    public Cell getCell(int row, int col) {
        return grid[row][col];
    }

}

