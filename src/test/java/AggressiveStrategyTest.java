import bot.AggressiveStrategy;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/*
 * AggressiveStrategyTest
 *
 * Tests the behaviour of the aggressive bot strategy.
 * This class verifies that:
 * -The correct strategy name is returned
 * -The bot takes a winning move when available
 * -The bot blocks an opponent’s winning move
 * -Rhombic tiles are used to create diagonal connections
 * -Rhombic tiles are used to block opponent connections
 * -Returned moves are valid and follow expected priority rules
 */

public class AggressiveStrategyTest {

    private static final int SIZE = 11;

    @Test
    void testGetNameReturnsAggressive() {
        AggressiveStrategy strategy = new AggressiveStrategy();
        assertEquals("Aggressive", strategy.getName());
    }

    @Test
    void testChooseMoveTakesWinningOctagonMove() {
        AggressiveStrategy strategy = new AggressiveStrategy();

        int[][] octagons = new int[SIZE][SIZE];
        int[][] rhombics = new int[SIZE - 1][SIZE - 1];

        //Black is one move away from a top-to-bottom win in column 0
        for (int r = 0; r < SIZE - 1; r++) {
            octagons[r][0] = 1;
        }

        int[] move = strategy.chooseMove(octagons, rhombics, 1);

        assertNotNull(move);
        assertArrayEquals(new int[]{SIZE - 1, 0, 0}, move);
    }

    @Test
    void testChooseMoveBlocksOpponentWinningOctagonMove() {
        AggressiveStrategy strategy = new AggressiveStrategy();

        int[][] octagons = new int[SIZE][SIZE];
        int[][] rhombics = new int[SIZE - 1][SIZE - 1];

        // Black is one move away from winning, bot is White
        for (int r = 0; r < SIZE - 1; r++) {
            octagons[r][0] = 1;
        }

        int[] move = strategy.chooseMove(octagons, rhombics, 2);

        assertNotNull(move);
        assertArrayEquals(new int[]{SIZE - 1, 0, 0}, move);
    }

    @Test
    void testChooseMovePlacesRhombicToCreateOwnDiagonalConnection() {
        AggressiveStrategy strategy = new AggressiveStrategy();

        int[][] octagons = new int[SIZE][SIZE];
        int[][] rhombics = new int[SIZE - 1][SIZE - 1];

        // Black owns a diagonal pair with an empty rhombic between them
        octagons[0][0] = 1;
        octagons[1][1] = 1;

        int[] move = strategy.chooseMove(octagons, rhombics, 1);

        assertNotNull(move);
        assertArrayEquals(new int[]{0, 0, 1}, move);
    }

    @Test
    void testChooseMovePlacesRhombicToBlockOpponentDiagonalConnection() {
        AggressiveStrategy strategy = new AggressiveStrategy();

        int[][] octagons = new int[SIZE][SIZE];
        int[][] rhombics = new int[SIZE - 1][SIZE - 1];

        // White has a diagonal pair; bot is Black and should block with rhombic
        octagons[0][0] = 2;
        octagons[1][1] = 2;

        int[] move = strategy.chooseMove(octagons, rhombics, 1);

        assertNotNull(move);
        assertArrayEquals(new int[]{0, 0, 1}, move);
    }
}