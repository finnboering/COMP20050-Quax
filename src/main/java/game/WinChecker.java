package game;

import java.util.*;

public class WinChecker {

    private int size;

    public WinChecker(int size) {
        this.size = size;
    }

    public boolean checkWin(int[][] oct, int[][] rhom, int player) {

        // keep track of visited tiles so we dont loop
        boolean[][] visitedOct = new boolean[size][size];
        boolean[][] visitedRhom = new boolean[size - 1][size - 1];

        // player 1 : top - > bottom
        if (player == 1) {
            for (int c = 0; c < size; c++) {
                if (oct[0][c] == player && visitedOct[0][c] == false) {
                    if (dfsOct(oct, rhom, visitedOct, visitedRhom, 0, c, player)) {
                        return true;
                    }
                }
            }
        }
        // player 2 : left - > right
        else {
            for (int r = 0; r < size; r++) {
                if (oct[r][0] == player && visitedOct[r][0] == false) {
                    if (dfsOct(oct, rhom, visitedOct, visitedRhom, r, 0, player)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    // DFS from an octagon
    private boolean dfsOct(int[][] oct, int[][] rhom,
                           boolean[][] visitedOct, boolean[][] visitedRhom,
                           int r, int c, int player) {

        // out of bounds
        if (r < 0 || c < 0 || r >= size || c >= size) {
            return false;
        }

        // already visited or not ours
        if (visitedOct[r][c] == true || oct[r][c] != player) {
            return false;
        }

        visitedOct[r][c] = true;

        // win condition
        if (player == 1 && r == size - 1) {
            return true;
        }
        if (player == 2 && c == size - 1) {
            return true;
        }

        // move up / down / left / right
        if (dfsOct(oct, rhom, visitedOct, visitedRhom, r + 1, c, player)) return true;
        if (dfsOct(oct, rhom, visitedOct, visitedRhom, r - 1, c, player)) return true;
        if (dfsOct(oct, rhom, visitedOct, visitedRhom, r, c + 1, player)) return true;
        if (dfsOct(oct, rhom, visitedOct, visitedRhom, r, c - 1, player)) return true;

        // diagonal using rhombics

        // down - right
        if (r < size - 1 && c < size - 1) {
            if (rhom[r][c] == player) {
                if (dfsOct(oct, rhom, visitedOct, visitedRhom, r + 1, c + 1, player)) return true;
            }
        }

        // down - left
        if (r < size - 1 && c > 0) {
            if (rhom[r][c - 1] == player) {
                if (dfsOct(oct, rhom, visitedOct, visitedRhom, r + 1, c - 1, player)) return true;
            }
        }

        // up - right
        if (r > 0 && c < size - 1) {
            if (rhom[r - 1][c] == player) {
                if (dfsOct(oct, rhom, visitedOct, visitedRhom, r - 1, c + 1, player)) return true;
            }
        }

        // up - left
        if (r > 0 && c > 0) {
            if (rhom[r - 1][c - 1] == player) {
                if (dfsOct(oct, rhom, visitedOct, visitedRhom, r - 1, c - 1, player)) return true;
            }
        }

        // move into rhombics as part of path
        if (r < size - 1 && c < size - 1) {
            if (dfsRhom(oct, rhom, visitedOct, visitedRhom, r, c, player)) return true;
        }
        if (r < size - 1 && c > 0) {
            if (dfsRhom(oct, rhom, visitedOct, visitedRhom, r, c - 1, player)) return true;
        }
        if (r > 0 && c < size - 1) {
            if (dfsRhom(oct, rhom, visitedOct, visitedRhom, r - 1, c, player)) return true;
        }
        if (r > 0 && c > 0) {
            if (dfsRhom(oct, rhom, visitedOct, visitedRhom, r - 1, c - 1, player)) return true;
        }

        return false;
    }

    // DFS from a rhombic
    private boolean dfsRhom(int[][] oct, int[][] rhom,
                            boolean[][] visitedOct, boolean[][] visitedRhom,
                            int r, int c, int player) {

        // out of bounds
        if (r < 0 || c < 0 || r >= size - 1 || c >= size - 1) {
            return false;
        }

        // already visited or not ours
        if (visitedRhom[r][c] == true || rhom[r][c] != player) {
            return false;
        }

        visitedRhom[r][c] = true;

        // rhombic connects 4 octagons
        if (dfsOct(oct, rhom, visitedOct, visitedRhom, r, c, player)) return true;
        if (dfsOct(oct, rhom, visitedOct, visitedRhom, r + 1, c, player)) return true;
        if (dfsOct(oct, rhom, visitedOct, visitedRhom, r, c + 1, player)) return true;
        if (dfsOct(oct, rhom, visitedOct, visitedRhom, r + 1, c + 1, player)) return true;

        return false;
    }

    // without rhombics
    public boolean checkWin(int[][] board, int player) {

        boolean[][] visited = new boolean[size][size];

        if (player == 1) {
            for (int c = 0; c < size; c++) {
                if (board[0][c] == player) {
                    if (dfsOct(board, new int[size - 1][size - 1], visited,
                            new boolean[size - 1][size - 1], 0, c, player)) {
                        return true;
                    }
                }
            }
        } else {
            for (int r = 0; r < size; r++) {
                if (board[r][0] == player) {
                    if (dfsOct(board, new int[size - 1][size - 1], visited,
                            new boolean[size - 1][size - 1], r, 0, player)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }
}