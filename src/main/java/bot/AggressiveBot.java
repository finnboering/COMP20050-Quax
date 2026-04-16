package bot;

import game.Board;
import game.Tile;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AggressiveBot implements Strategy {

    public Tile getMove(Board b) {

        List<Tile> emptyTiles = new ArrayList<>();

        for (Tile t : b.getTiles()) {
            if (t.hasNoTile()) {
                emptyTiles.add(t);
            }
        }

        // 50% chance: play aggressively (next to opponent)
        if (Math.random() < 0.5) {
            List<Tile> aggressiveMoves = new ArrayList<>();

            for (Tile t : emptyTiles) {
                if (isNearOpponent(b, t)) {
                    aggressiveMoves.add(t);
                }
            }

            if (!aggressiveMoves.isEmpty()) {
                return aggressiveMoves.get(new Random().nextInt(aggressiveMoves.size()));
            }
        }

        // Otherwise: play center-ish
        return getCenterMove(emptyTiles);
    }

    private boolean isNearOpponent(Board b, Tile t) {

        for (Tile other : b.getTiles()) {

            // skip empty tiles
            if (other.hasNoTile()) continue;

            int rowDiff = Math.abs(t.getRow() - other.getRow());
            int colDiff = Math.abs(t.getCol() - other.getCol());

            // if it's adjacent (including diagonals)
            if (rowDiff <= 1 && colDiff <= 1) {
                return true;
            }
        }

        return false;
    }

    private Tile getCenterMove(List<Tile> tiles) {
        Tile best = null;
        int bestDist = Integer.MAX_VALUE;

        int centerRow = 5; // adjust if board size different
        int centerCol = 5;

        for (Tile t : tiles) {
            int dist = Math.abs(t.getRow() - centerRow) + Math.abs(t.getCol() - centerCol);

            if (dist < bestDist) {
                bestDist = dist;
                best = t;
            }
        }

        return best;
    }
}