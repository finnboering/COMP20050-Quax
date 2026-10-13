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

        // black goes top -> bottom
        // white goes left -> right
        // seed DFS from every owned tile on the starting edge
        if (player == 1) {
            for (int col = 0; col < size; col++) {
                if (octagons[0][col] == player) {
                    boolean[][] visited = new boolean[size][size];
                    if (findPath(octagons, rhombics, visited, 0, col, player)) {
                        return true;
                    }
                }
            }
        } else {
            for (int row = 0; row < size; row++) {
                if (octagons[row][0] == player) {
                    boolean[][] visited = new boolean[size][size];
                    if (findPath(octagons, rhombics, visited, row, 0, player)) {
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

        // normal moves (down / up / right / left)
        if (findPath(octagons, rhombics, visited, row+1, col, player) ||
                findPath(octagons, rhombics, visited, row-1, col, player) ||
                findPath(octagons, rhombics, visited, row, col+1, player) ||
                findPath(octagons, rhombics, visited, row, col-1, player)) {
            return true;
        }

        // diagonal moves using rhombics
        // rhombics array is [size-1][size-1] so max valid index is size-2
        // a rhombic at [r][c] bridges the four corner octagons:
        // [r][c], [r][c+1], [r+1][c], [r+1][c+1]
        if (rhombics != null) {
            // down -> right: rhombic[row][col] bridges current to [row+1][col+1]
            if (row < size-1 && col < size-1 &&
                    rhombics[row][col] == player &&
                    octagons[row+1][col+1] == player) {
                if (findPath(octagons, rhombics, visited, row+1, col+1, player))
                    return true;
            }

            // down -> left: rhombic[row][col-1] bridges current to [row+1][col-1]
            if (row < size-1 && col > 0 &&
                    rhombics[row][col-1] == player &&
                    octagons[row+1][col-1] == player) {
                if (findPath(octagons, rhombics, visited, row+1, col-1, player))
                    return true;
            }

            // up -> right: rhombic[row-1][col] bridges current to [row-1][col+1]
            if (row > 0 && col < size-1 &&
                    rhombics[row-1][col] == player &&
                    octagons[row-1][col+1] == player) {
                if (findPath(octagons, rhombics, visited, row-1, col+1, player))
                    return true;
            }

            // up -> left: rhombic[row-1][col-1] bridges current to [row-1][col-1]
            // fixed: added bounds check to ensure row-1 and col-1 are within rhombics array
            if (row > 0 && col > 0 &&
                    row-1 < size-1 && col-1 < size-1 &&
                    rhombics[row-1][col-1] == player &&
                    octagons[row-1][col-1] == player) {
                if (findPath(octagons, rhombics, visited, row-1, col-1, player))
                    return true;
            }
        }
        return false;
    }
}