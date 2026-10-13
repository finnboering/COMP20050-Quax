package bot;

import game.WinChecker;
import java.util.*;

public class SmartStrategy implements Strategy {

    private static final int BOARD_SIZE = 11;

    @Override
    public String getName() {
        return "Smart";
    }

    @Override
    public int[] chooseMove(int[][] octagonOwner, int[][] rhombicOwner, int botColour) {

        int opponentColour;
        if (botColour == 1)
            opponentColour = 2;
        else
            opponentColour = 1;

        // try win immediately
        int[] winningMove = findWinningMove(octagonOwner, rhombicOwner, botColour);
        if (winningMove != null)
            return winningMove;

        // block opponent from winning
        int[] blockingMove = findWinningMove(octagonOwner, rhombicOwner, opponentColour);
        if (blockingMove != null)
            return blockingMove;

        // pick the most contested cell using Dijkstra on both players
        return findBestContestedMove(octagonOwner, rhombicOwner, botColour, opponentColour);
    }

    // try every empty tile and return one that wins immediately
    private int[] findWinningMove(int[][] octagonOwner, int[][] rhombicOwner, int colour) {
        WinChecker winChecker = new WinChecker(BOARD_SIZE);

        for (int r = 0; r < BOARD_SIZE; r++) {
            for (int c = 0; c < BOARD_SIZE; c++) {
                if (octagonOwner[r][c] == 0) {
                    octagonOwner[r][c] = colour;
                    boolean wins = winChecker.checkWin(octagonOwner, rhombicOwner, colour);
                    octagonOwner[r][c] = 0;
                    if (wins)
                        return new int[]{r, c, 0};
                }
            }
        }

        for (int r = 0; r < BOARD_SIZE - 1; r++) {
            for (int c = 0; c < BOARD_SIZE - 1; c++) {
                if (rhombicOwner[r][c] == 0) {
                    rhombicOwner[r][c] = colour;
                    boolean wins = winChecker.checkWin(octagonOwner, rhombicOwner, colour);
                    rhombicOwner[r][c] = 0;
                    if (wins)
                        return new int[]{r, c, 1};
                }
            }
        }

        return null;
    }

    // runs Dijkstra from one edge and returns a distance array for every cell
    // owned cells cost 0 , empty cost 1, opponent cells are skipped
    // colour 1 starts from row 0, colour 2 starts from column 0
    private int[] runDijkstra(int[][] octagonOwner, int[][] rhombicOwner, int colour, boolean startFromTopRow) {

        int opponentColour;
        if (colour == 1)
            opponentColour = 2;
        else
            opponentColour = 1;

        int[] dist = new int[BOARD_SIZE * BOARD_SIZE];
        Arrays.fill(dist, Integer.MAX_VALUE);

        PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(a -> a[0]));

        // load up the starting edge
        for (int i = 0; i < BOARD_SIZE; i++) {
            int r;
            int c;
            if (startFromTopRow) {
                r = 0;
                c = i;
            } else {
                r = i;
                c = 0;
            }

            if (octagonOwner[r][c] != opponentColour) {
                int cost;
                if (octagonOwner[r][c] == colour)
                    cost = 0;
                else
                    cost = 1;
                int pos = r * BOARD_SIZE + c;
                dist[pos] = cost;
                pq.offer(new int[]{cost, pos});
            }
        }

        while (!pq.isEmpty()) {
            int[] current = pq.poll();
            int currentDist = current[0];
            int pos = current[1];

            if (currentDist > dist[pos])
                continue;

            int r = pos / BOARD_SIZE;
            int c = pos % BOARD_SIZE;

            // check up down left right neighbours
            for (int[] neighbour : getOctagonNeighbours(r, c)) {
                int nr = neighbour[0];
                int nc = neighbour[1];

                if (octagonOwner[nr][nc] == opponentColour)
                    continue;

                int nPos = nr * BOARD_SIZE + nc;
                int newCost;
                if (octagonOwner[nr][nc] == colour)
                    newCost = currentDist;
                else
                    newCost = currentDist + 1;

                if (newCost < dist[nPos]) {
                    dist[nPos] = newCost;
                    pq.offer(new int[]{newCost, nPos});
                }
            }

            // check diagonal neighbours reachable through a rhombic tile
            for (int[] diagonal : getDiagonalViaRhombic(r, c, rhombicOwner, colour)) {
                int nr = diagonal[0]; // neighbour row
                int nc = diagonal[1]; // neighbour col
                int rr = diagonal[2]; // rhombic row
                int rc = diagonal[3]; // rhombic col

                if (octagonOwner[nr][nc] == opponentColour)
                    continue;

                int nPos = nr * BOARD_SIZE + nc;

                int rhombicCost;
                if (rhombicOwner[rr][rc] == colour)
                    rhombicCost = 0;
                else
                    rhombicCost = 1;

                int newCost;
                if (octagonOwner[nr][nc] == colour)
                    newCost = currentDist + rhombicCost;
                else
                    newCost = currentDist + 1 + rhombicCost;

                if (newCost < dist[nPos]) {
                    dist[nPos] = newCost;
                    pq.offer(new int[]{newCost, nPos});
                }
            }

        }

        return dist;
    }

    // finds the empty cell with the lowest combined path cost for both players
    private int[] findBestContestedMove(int[][] octagonOwner, int[][] rhombicOwner, int botColour, int opponentColour) {

        // colour 1 goes top to bottom, colour 2 goes left to right
        boolean botStartsFromTop = (botColour == 1);

        int[] botDistances = runDijkstra(octagonOwner, rhombicOwner, botColour, botStartsFromTop);
        int[] oppDistances = runDijkstra(octagonOwner, rhombicOwner, opponentColour, !botStartsFromTop);

        int bestScore = Integer.MAX_VALUE;
        int[] bestMove = null;

        for (int r = 0; r < BOARD_SIZE; r++) {
            for (int c = 0; c < BOARD_SIZE; c++) {
                if (octagonOwner[r][c] != 0)
                    continue;

                int pos = r * BOARD_SIZE + c;

                if (botDistances[pos] == Integer.MAX_VALUE || oppDistances[pos] == Integer.MAX_VALUE)
                    continue;

                // low score means both players want this cell
                int score = botDistances[pos] + oppDistances[pos];

                if (score < bestScore) {
                    bestScore = score;
                    bestMove = new int[]{r, c, 0};
                }
            }
        }

        // place a rhombic if its worth it
        int[] rhombicMove = findBestRhombicMove(octagonOwner, rhombicOwner, botColour, opponentColour);
        if (rhombicMove != null)
            return rhombicMove;

        // fallback -> scan outward from center
        if (bestMove == null)
            return fallbackToCenter(octagonOwner);

        return bestMove;
    }

    // place a rhombic tile when it connects two of our diagonal stones
    // also blocks the opponent if they have a diagonal bridge ready
    private int[] findBestRhombicMove(int[][] octagonOwner, int[][] rhombicOwner, int botColour, int opponentColour) {
        int[] best = null;
        int bestGain = 0;

        for (int r = 0; r < BOARD_SIZE - 1; r++) {
            for (int c = 0; c < BOARD_SIZE - 1; c++) {
                if (rhombicOwner[r][c] != 0)
                    continue;

                boolean topLeft     = octagonOwner[r][c] == botColour;
                boolean topRight    = octagonOwner[r][c + 1] == botColour;
                boolean bottomLeft  = octagonOwner[r + 1][c] == botColour;
                boolean bottomRight = octagonOwner[r + 1][c + 1] == botColour;

                // only place if exactly two diagonally opposite corners are ours
                boolean ourDiagonal1 = topLeft && bottomRight && !topRight && !bottomLeft;
                boolean ourDiagonal2 = topRight && bottomLeft && !topLeft && !bottomRight;

                if (ourDiagonal1 || ourDiagonal2) {
                    boolean opponentOnCorner =
                            octagonOwner[r][c] == opponentColour ||
                                    octagonOwner[r][c + 1] == opponentColour ||
                                    octagonOwner[r + 1][c] == opponentColour ||
                                    octagonOwner[r + 1][c + 1] == opponentColour;

                    if (!opponentOnCorner && bestGain < 1) {
                        bestGain = 1;
                        best = new int[]{r, c, 1};
                    }
                }

                // block opponent bridge if we havent found our own
                boolean oppDiagonal1 = octagonOwner[r][c] == opponentColour && octagonOwner[r + 1][c + 1] == opponentColour;
                boolean oppDiagonal2 = octagonOwner[r][c + 1] == opponentColour && octagonOwner[r + 1][c] == opponentColour;

                if ((oppDiagonal1 || oppDiagonal2) && best == null)
                    best = new int[]{r, c, 1};
            }
        }

        return best;
    }

    // up down left right neighbours that are on the board
    private List<int[]> getOctagonNeighbours(int r, int c) {
        List<int[]> neighbours = new ArrayList<>();
        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

        for (int[] dir : directions) {
            int nr = r + dir[0]; // neighbour row
            int nc = c + dir[1]; // neighbour col

            if (nr >= 0 && nr < BOARD_SIZE && nc >= 0 && nc < BOARD_SIZE)
                neighbours.add(new int[]{nr, nc});
        }


        return neighbours;
    }

    // returns diagonal neighbours reachable using a rhombic tile that isnt owned by the opponent
    private List<int[]> getDiagonalViaRhombic(int r, int c, int[][] rhombicOwner, int colour) {
        List<int[]> result = new ArrayList<>();

        int opponentColour;
        if (colour == 1)
            opponentColour = 2;
        else
            opponentColour = 1;

        int[][] diagonals = {{-1, -1}, {-1, 1}, {1, -1}, {1, 1}};

        for (int[] d : diagonals) {
            int nr = r + d[0];   // neighbour row
            int nc = c + d[1];   // neighbour col

            if (nr < 0 || nc < 0 || nr >= BOARD_SIZE || nc >= BOARD_SIZE)
                continue;

            // rhombic sits at the top left of the 2x2 square
            int rr = Math.min(r, nr);   // rhombic row
            int rc = Math.min(c, nc);   // rhombic col

            if (rr >= BOARD_SIZE - 1 || rc >= BOARD_SIZE - 1)
                continue;

            if (rhombicOwner[rr][rc] != opponentColour)
                result.add(new int[]{nr, nc, rr, rc});
        }

        return result;
    }

    // searches outward from center for the first empty octagon tile
    private int[] fallbackToCenter(int[][] octagonOwner) {
        int mid = BOARD_SIZE / 2;

        for (int radius = 0; radius <= mid; radius++)
            for (int r = mid - radius; r <= mid + radius; r++)
                for (int c = mid - radius; c <= mid + radius; c++)
                    if (r >= 0 && c >= 0 && r < BOARD_SIZE && c < BOARD_SIZE && octagonOwner[r][c] == 0)
                        return new int[]{r, c, 0};

        return null;
    }
}

