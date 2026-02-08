public class Board {

    private static final int SIZE = 11;
    private char[][] grid;

    public Board() {
        grid = new char[SIZE][SIZE];

        // initialise board with empty cells
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                grid[row][col] = '.';
            }
        }
    }

    public void printBoard() {
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                System.out.print(grid[row][col] + " ");
            }
            System.out.println();
        }
    }
}
