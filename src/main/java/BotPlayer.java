import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BotPlayer {

    private Random random = new Random();

    //randomly decide whether to use the pie rule
    public boolean decidePieRule() {
        return random.nextBoolean();
    }

    //collect all empty tiles and return a random one
    //returns int[] {row, col, tileType} where tileType 0 = octagon, 1 = rhombic
    public int[] chooseMove(int[][] octagonOwner, int[][] rhombicOwner) {
        List<int[]> availableMoves = new ArrayList<>();

        //ollect empty octagons
        for (int r = 0; r < octagonOwner.length; r++) {
            for (int c = 0; c < octagonOwner[r].length; c++) {
                if (octagonOwner[r][c] == 0) {
                    availableMoves.add(new int[]{r, c, 0}); //0 = octagon
                }
            }
        }

        //collect empty rhombics
        for (int r = 0; r < rhombicOwner.length; r++) {
            for (int c = 0; c < rhombicOwner[r].length; c++) {
                if (rhombicOwner[r][c] == 0) {
                    availableMoves.add(new int[]{r, c, 1}); //1 = rhombic
                }
            }
        }

        //no moves available
        if (availableMoves.isEmpty()) return null;

        //pick a random move
        return availableMoves.get(random.nextInt(availableMoves.size()));
    }
}