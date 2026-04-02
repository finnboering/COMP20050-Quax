/*
using DFS to search through connected tiles.
 it checks if there is a continuous path from one side of the board to the other.
 DFS works well here since we only care if a path exists.
*/

public class WinChecker {

    private int size;

    public WinChecker(int size) {
        this.size = size;
    }

    // main win check (uses DFS + rhombics)
    public boolean checkWin(int[][] octagons, int[][] rhombics, int player) {

        //track visited tiles so we dont loop
        boolean[][] visited = new boolean[size][size];

        // black goes top -> bottom
        if (player == 1) {
            for (int col = 0; col < size; col++) {
                if (octagons[0][col] == player) {
                    if (findPath(octagons, rhombics, visited, 0, col, player)) {
                        return true;
                    }
                }
            }
            // white goes left -> right
        } else {
            for (int row = 0; row < size; row++) {
                if (octagons[row][0] == player) {
                    if (findPath(octagons, rhombics, visited, row, 0, player)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    // same thing but no rhombics
    public boolean checkWin(int[][] board, int player) {
        boolean[][] visited = new boolean[size][size];

        if (player == 1) {
            for (int col = 0; col < size; col++) {
                if (board[0][col] == player) {
                    if (findPath(board, null, visited, 0, col, player)) {
                        return true;
                    }
                }
            }
        } else {
            for (int row = 0; row < size; row++) {
                if (board[row][0] == player) {
                    if (findPath(board, null, visited, row, 0, player)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }


    // DFS to try find a path
    private boolean findPath(int[][] octagons, int[][] rhombics, boolean[][] visited, int row, int col, int player) {
        // stop if invalid or already seen
        if (row < 0 || col < 0 || row >= size || col >= size) return false;
        if (visited[row][col] || octagons[row][col] != player) return false;

        // mark as visited
        visited[row][col] = true;

        // reached the other side = win
        if (player == 1 && row == size - 1) return true; // BLACK reached bottom
        if (player == 2 && col == size - 1) return true; // WHITE reached right

        // normal moves ( down / up / right / left )
        if (findPath(octagons, rhombics, visited, row+1, col, player) || findPath(octagons, rhombics, visited, row-1, col, player) || findPath(octagons, rhombics, visited, row, col+1, player) || findPath(octagons, rhombics, visited, row, col-1, player)) {
            return true;
        }

        // diagonal moves using rhombics
        if (rhombics != null) {
            // down -> right
            if (row < size-1 && col < size-1 && rhombics[row][col] == player && octagons[row+1][col+1] == player) {
                if (findPath(octagons, rhombics, visited, row+1, col+1, player))
                    return true;
            }

            // down -> left
            if (row < size-1 && col > 0 && rhombics[row][col-1] == player && octagons[row+1][col-1] == player) {
                if (findPath(octagons, rhombics, visited, row+1, col-1, player))
                    return true;
            }

            // up -> right
            if (row > 0 && col < size-1 && rhombics[row-1][col] == player && octagons[row-1][col+1] == player) {
                if (findPath(octagons, rhombics, visited, row-1, col+1, player))
                    return true;
            }

            // up -> left
            if (row > 0 && col > 0 && rhombics[row-1][col-1] == player && octagons[row-1][col-1] == player) {
                if (findPath(octagons, rhombics, visited, row-1, col-1, player))
                    return true;
            }
        }
        return false;
    }
}