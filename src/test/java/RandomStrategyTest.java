import bot.RandomStrategy;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/*
 * RandomStrategyTest
 *
 * Tests the behaviour of the random bot strategy.
 * This class verifies that:
 * -The correct strategy name is returned
 * -Moves are always placed on empty tiles
 * -Both octagon and rhombic placements can be selected
 * -The strategy works with partially filled boards
 * -The strategy returns null when no moves are available
 */

public class RandomStrategyTest {

    private static final int SIZE = 11;

    @Test
    void testGetNameReturnsRandom() {
        RandomStrategy strategy = new RandomStrategy();
        assertEquals("Random", strategy.getName());
    }

    @Test
    void testChooseMoveReturnsLegalEmptyTile() {
        RandomStrategy strategy = new RandomStrategy();

        int[][] octagons = new int[SIZE][SIZE];
        int[][] rhombics = new int[SIZE - 1][SIZE - 1];

        octagons[0][0] = 1;
        rhombics[0][0] = 2;

        int[] move = strategy.chooseMove(octagons, rhombics, 1);

        assertNotNull(move);
        assertEquals(3, move.length);

        int r = move[0];
        int c = move[1];
        int tileType = move[2];

        if (tileType == 0) {
            assertEquals(0, octagons[r][c]);
        } else {
            assertEquals(0, rhombics[r][c]);
        }
    }

    @Test
    void testChooseMoveReturnsNullWhenBoardIsFull() {
        RandomStrategy strategy = new RandomStrategy();

        int[][] octagons = new int[SIZE][SIZE];
        int[][] rhombics = new int[SIZE - 1][SIZE - 1];

        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                octagons[r][c] = 1;
            }
        }

        for (int r = 0; r < SIZE - 1; r++) {
            for (int c = 0; c < SIZE - 1; c++) {
                rhombics[r][c] = 2;
            }
        }

        assertNull(strategy.chooseMove(octagons, rhombics, 1));
    }

    @Test
    void testChooseMoveCanReturnRhombicWhenOnlyRhombicsRemain() {
        RandomStrategy strategy = new RandomStrategy();

        int[][] octagons = new int[SIZE][SIZE];
        int[][] rhombics = new int[SIZE - 1][SIZE - 1];

        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                octagons[r][c] = 1;
            }
        }

        rhombics[4][4] = 0;

        int[] move = strategy.chooseMove(octagons, rhombics, 1);

        assertNotNull(move);
        assertEquals(1, move[2]);
        assertEquals(0, rhombics[move[0]][move[1]]);
    }
}