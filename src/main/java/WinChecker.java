public class WinChecker {

    private int size;

    public WinChecker(int size) {
        this.size = size;
    }

    public boolean checkWin(int[][] octagon, int[][] rhombic, int player) {

        boolean[][] visited = new boolean[size][size];

        if (player == 1) {
            for (int col = 0; col < size; col++) {
                if (octagon[0][col] == player) {
                    if (explorePath(octagon, rhombic, visited, 0, col, player)) {
                        return true;
                    }
                }
            }
        } else {
            for (int row = 0; row < size; row++) {
                if (octagon[row][0] == player) {
                    if (explorePath(octagon, rhombic, visited, row, 0, player)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public boolean checkWin(int[][] board, int player) {
        boolean[][] visited = new boolean[size][size];

        if (player == 1) {
            for (int col = 0; col < size; col++) {
                if (board[0][col] == player) {
                    if (explorePath(board, null, visited, 0, col, player)) {
                        return true;
                    }
                }
            }
        } else {
            for (int row = 0; row < size; row++) {
                if (board[row][0] == player) {
                    if (explorePath(board, null, visited, row, 0, player)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }


    private boolean explorePath(int[][] board, int[][] rhombic, boolean[][] visited, int row, int col, int player) {
        if (row < 0 || col < 0 || row >= size || col >= size) return false;
        if (visited[row][col] || board[row][col] != player) return false;

        visited[row][col] = true;

        // WIN CONDITIONS
        if (player == 1 && row == size - 1) return true; // BLACK reached bottom
        if (player == 2 && col == size - 1) return true; // WHITE reached right

        // normal directions
        if (explorePath(board, rhombic, visited, row+1, col, player) ||
                explorePath(board, rhombic, visited, row-1, col, player) ||
                explorePath(board, rhombic, visited, row, col+1, player) ||
                explorePath(board, rhombic, visited, row, col-1, player)) {
            return true;
        }

// 🔥 ADD THIS (diagonal via rhombics)
        if (rhombic != null) {

            // down-right
            if (row < size-1 && col < size-1 && rhombic[row][col] == player) {
                if (explorePath(board, rhombic, visited, row+1, col+1, player)) return true;
            }

            // down-left
            if (row < size-1 && col > 0 && rhombic[row][col-1] == player) {
                if (explorePath(board, rhombic, visited, row+1, col-1, player)) return true;
            }

            // up-right
            if (row > 0 && col < size-1 && rhombic[row-1][col] == player) {
                if (explorePath(board, rhombic, visited, row-1, col+1, player)) return true;
            }

            // up-left
            if (row > 0 && col > 0 && rhombic[row-1][col-1] == player) {
                if (explorePath(board, rhombic, visited, row-1, col-1, player)) return true;
            }
        }

        return false;
    }
}