import game.WinChecker;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/*
 * WinCheckerTest
 *
 * Tests the game.WinChecker DFS win detection logic.
 *
 * This verifies that:
 -No win is detected on an empty board
 -Black wins with a complete top-to-bottom path of octagons
 -White wins with a complete left-to-right path of octagons
 -No false win is triggered for incomplete paths
 -Rhombic diagonal connections contribute to winning paths
 -Single-tile paths do not count as wins (no edge-to-edge connection)
 */

public class WinCheckerTest {

    private static final int SIZE = 11;

    @Test
    void testNoWinOnEmptyBoard() {
        WinChecker checker = new WinChecker(SIZE);

        int[][] board = new int[SIZE][SIZE];
        int[][] rhombics = new int[SIZE - 1][SIZE - 1];

        assertFalse(checker.checkWin(board, rhombics, 1));
        assertFalse(checker.checkWin(board, rhombics, 2));
    }

    @Test
    void testBlackWinsTopToBottom() {
        WinChecker checker = new WinChecker(SIZE);

        int[][] octagons = new int[SIZE][SIZE];
        int[][] rhombics = new int[SIZE - 1][SIZE - 1];

        // fill column 0 with BLACK to create a top-to-bottom path
        for (int r = 0; r < SIZE; r++) {
            octagons[r][0] = 1;
        }

        assertTrue(checker.checkWin(octagons, rhombics, 1));
    }

    @Test
    void testWhiteWinsLeftToRight() {
        WinChecker checker = new WinChecker(SIZE);

        int[][] octagons = new int[SIZE][SIZE];
        int[][] rhombics = new int[SIZE - 1][SIZE - 1];

        // fill row 0 with WHITE to create a left-to-right path
        for (int c = 0; c < SIZE; c++) {
            octagons[0][c] = 2;
        }

        assertTrue(checker.checkWin(octagons, rhombics, 2));
    }

    @Test
    void testBlackDoesNotWinWithIncompleteColumn() {
        WinChecker checker = new WinChecker(SIZE);

        int[][] octagons = new int[SIZE][SIZE];
        int[][] rhombics = new int[SIZE - 1][SIZE - 1];

        // fill all but the last row in column 0
        for (int r = 0; r < SIZE - 1; r++) {
            octagons[r][0] = 1;
        }

        assertFalse(checker.checkWin(octagons, rhombics, 1));
    }

    @Test
    void testWhiteDoesNotWinWithIncompleteRow() {
        WinChecker checker = new WinChecker(SIZE);

        int[][] octagons = new int[SIZE][SIZE];
        int[][] rhombics = new int[SIZE - 1][SIZE - 1];

        // fill all but the last column in row 0
        for (int c = 0; c < SIZE - 1; c++) {
            octagons[0][c] = 2;
        }

        assertFalse(checker.checkWin(octagons, rhombics, 2));
    }

    @Test
    void testBlackWinWithSingleRowPath() {
        WinChecker checker = new WinChecker(SIZE);

        // test using the no-rhombics overload
        int[][] board = new int[SIZE][SIZE];

        for (int r = 0; r < SIZE; r++) {
            board[r][0] = 1;
        }

        assertTrue(checker.checkWin(board, 1));
    }

    @Test
    void testWhiteWinWithSingleRowPath() {
        WinChecker checker = new WinChecker(SIZE);

        int[][] board = new int[SIZE][SIZE];

        for (int c = 0; c < SIZE; c++) {
            board[0][c] = 2;
        }

        assertTrue(checker.checkWin(board, 2));
    }

    @Test
    void testBlackDoesNotWinOnSingleTile() {
        WinChecker checker = new WinChecker(SIZE);

        int[][] octagons = new int[SIZE][SIZE];
        int[][] rhombics = new int[SIZE - 1][SIZE - 1];

        // only one tile placed at the top row - no path to the bottom
        octagons[0][5] = 1;

        assertFalse(checker.checkWin(octagons, rhombics, 1));
    }

    @Test
    void testWhiteDoesNotWinOnSingleTile() {
        WinChecker checker = new WinChecker(SIZE);

        int[][] octagons = new int[SIZE][SIZE];
        int[][] rhombics = new int[SIZE - 1][SIZE - 1];

        // only one tile placed at the leftmost column - no path to the right
        octagons[5][0] = 2;

        assertFalse(checker.checkWin(octagons, rhombics, 2));
    }

    @Test
    void testRhombicDiagonalConnectsPath() {
        WinChecker checker = new WinChecker(SIZE);

        int[][] octagons = new int[SIZE][SIZE];
        int[][] rhombics = new int[SIZE - 1][SIZE - 1];

        // BLACK path: goes down col 0 until row 5, then diagonal via rhombic[5][0] to col 1,
        // then continues down col 1 to the bottom
        for (int r = 0; r <= 5; r++) {
            octagons[r][0] = 1; // top half in col 0
        }
        rhombics[5][0] = 1;         // diagonal bridge
        octagons[6][1] = 1;         // lands in col 1
        for (int r = 6; r < SIZE; r++) {
            octagons[r][1] = 1; // bottom half in col 1
        }
        // Also need the destination octagon to be connected on the right side
        octagons[5][1] = 1; // bridge landing needs continuity

        assertTrue(checker.checkWin(octagons, rhombics, 1));
    }

    @Test
    void testBlackWinsThroughDiagonalRhombicChain() {
        WinChecker checker = new WinChecker(SIZE);

        int[][] octagons = new int[SIZE][SIZE];
        int[][] rhombics = new int[SIZE - 1][SIZE - 1];

        // Create a diagonal chain of black octagons
        for (int i = 0; i < SIZE; i++) {
            octagons[i][i] = 1;
        }

        // Connect them with rhombics
        for (int i = 0; i < SIZE - 1; i++) {
            rhombics[i][i] = 1;
        }

        assertTrue(checker.checkWin(octagons, rhombics, 1));
    }

    @Test
    void testRhombicsAloneDoNotCountAsWin() {
        WinChecker checker = new WinChecker(SIZE);

        int[][] octagons = new int[SIZE][SIZE];
        int[][] rhombics = new int[SIZE - 1][SIZE - 1];

        // Only rhombics, no octagons
        for (int i = 0; i < SIZE - 1; i++) {
            rhombics[i][i] = 1;
        }

        assertFalse(checker.checkWin(octagons, rhombics, 1));
    }

    @Test
    void testDiagonalWithoutRhombicsDoesNotWin() {
        WinChecker checker = new WinChecker(SIZE);

        int[][] octagons = new int[SIZE][SIZE];
        int[][] rhombics = new int[SIZE - 1][SIZE - 1];

        // Diagonal octagons but no connecting rhombics
        for (int i = 0; i < SIZE; i++) {
            octagons[i][i] = 1;
        }

        assertFalse(checker.checkWin(octagons, rhombics, 1));
    }

    @Test
    void testBrokenRhombicChainDoesNotWin() {
        WinChecker checker = new WinChecker(SIZE);
    
        int[][] octagons = new int[SIZE][SIZE];
        int[][] rhombics = new int[SIZE - 1][SIZE - 1];
    
        // Full diagonal octagons
        for (int i = 0; i < SIZE; i++) {
            octagons[i][i] = 1;
        }
    
        // Missing one rhombic connection (break the chain)
        for (int i = 0; i < SIZE - 1; i++) {
            if (i != 5) {
                rhombics[i][i] = 1;
            }
        }
    
        assertFalse(checker.checkWin(octagons, rhombics, 1));
    }
}
