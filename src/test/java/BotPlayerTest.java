import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/*
 * BotPlayerTest
 *
 * Tests the BotPlayer AI logic.
 *
 * This verifies that:
 -BotPlayer can be instantiated
 -chooseMove returns a valid move on an empty board
 -chooseMove returns null when the board is full
 -chooseMove returns a move with a valid tile type (0 = octagon, 1 = rhombic)
 -chooseMove only picks from empty tiles (owner == 0)
 -decidePieRule returns a boolean
 */

public class BotPlayerTest {

    private static final int BOARD_SIZE = 11;

    @Test
    void testBotPlayerCreation() {
        BotPlayer bot = new BotPlayer();

        assertNotNull(bot);
    }

    @Test
    void testChooseMoveOnEmptyBoard() {
        BotPlayer bot = new BotPlayer();

        int[][] octagonOwner = new int[BOARD_SIZE][BOARD_SIZE];
        int[][] rhombicOwner = new int[BOARD_SIZE - 1][BOARD_SIZE - 1];

        int[] move = bot.chooseMove(octagonOwner, rhombicOwner);

        assertNotNull(move);
    }

    @Test
    void testChooseMoveReturnsTileType() {
        BotPlayer bot = new BotPlayer();

        int[][] octagonOwner = new int[BOARD_SIZE][BOARD_SIZE];
        int[][] rhombicOwner = new int[BOARD_SIZE - 1][BOARD_SIZE - 1];

        int[] move = bot.chooseMove(octagonOwner, rhombicOwner);

        // tile type must be 0 (octagon) or 1 (rhombic)
        assertTrue(move[2] == 0 || move[2] == 1);
    }

    @Test
    void testChooseMoveReturnsNullOnFullBoard() {
        BotPlayer bot = new BotPlayer();

        // fill every octagon and rhombic slot
        int[][] octagonOwner = new int[BOARD_SIZE][BOARD_SIZE];
        int[][] rhombicOwner = new int[BOARD_SIZE - 1][BOARD_SIZE - 1];

        for (int r = 0; r < BOARD_SIZE; r++) {
            for (int c = 0; c < BOARD_SIZE; c++) {
                octagonOwner[r][c] = 1;
            }
        }
        for (int r = 0; r < BOARD_SIZE - 1; r++) {
            for (int c = 0; c < BOARD_SIZE - 1; c++) {
                rhombicOwner[r][c] = 1;
            }
        }

        int[] move = bot.chooseMove(octagonOwner, rhombicOwner);

        assertNull(move);
    }

    @Test
    void testChooseMoveOnlyPicksEmptyTile() {
        BotPlayer bot = new BotPlayer();

        // fill all octagons except one
        int[][] octagonOwner = new int[BOARD_SIZE][BOARD_SIZE];
        int[][] rhombicOwner = new int[BOARD_SIZE - 1][BOARD_SIZE - 1];

        for (int r = 0; r < BOARD_SIZE; r++) {
            for (int c = 0; c < BOARD_SIZE; c++) {
                octagonOwner[r][c] = 1;
            }
        }
        // fill all rhombics too
        for (int r = 0; r < BOARD_SIZE - 1; r++) {
            for (int c = 0; c < BOARD_SIZE - 1; c++) {
                rhombicOwner[r][c] = 1;
            }
        }

        // leave only one octagon empty
        octagonOwner[5][5] = 0;

        int[] move = bot.chooseMove(octagonOwner, rhombicOwner);

        assertNotNull(move);
        assertEquals(5, move[0]);
        assertEquals(5, move[1]);
        assertEquals(0, move[2]); // octagon
    }

    @Test
    void testChooseMoveReturnsValidRowAndCol() {
        BotPlayer bot = new BotPlayer();

        int[][] octagonOwner = new int[BOARD_SIZE][BOARD_SIZE];
        int[][] rhombicOwner = new int[BOARD_SIZE - 1][BOARD_SIZE - 1];

        int[] move = bot.chooseMove(octagonOwner, rhombicOwner);

        assertNotNull(move);
        assertTrue(move[0] >= 0);
        assertTrue(move[1] >= 0);
    }

    @Test
    void testDecidePieRuleReturnsBoolean() {
        BotPlayer bot = new BotPlayer();

        // decidePieRule always returns a boolean - we just verify it runs without error
        boolean decision = bot.decidePieRule();
        assertTrue(decision == true || decision == false);
    }
}
