package bot;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RandomStrategy implements Strategy {

    private final Random random = new Random();

    @Override
    public int[] chooseMove(int[][] octagonOwner, int[][] rhombicOwner, int botColour) {
        List<int[]> moves = new ArrayList<>();

        for (int r = 0; r < octagonOwner.length; r++)
            for (int c = 0; c < octagonOwner[r].length; c++)
                if (octagonOwner[r][c] == 0) moves.add(new int[]{r, c, 0});

        for (int r = 0; r < rhombicOwner.length; r++)
            for (int c = 0; c < rhombicOwner[r].length; c++)
                if (rhombicOwner[r][c] == 0) moves.add(new int[]{r, c, 1});

        if (moves.isEmpty()) return null;
        return moves.get(random.nextInt(moves.size()));
    }

    @Override
    public String getName() { return "Random"; }
}
