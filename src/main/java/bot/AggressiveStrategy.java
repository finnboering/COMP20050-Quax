package bot;

import game.WinChecker;
import java.util.*;

public class AggressiveStrategy implements Strategy {

    private static final int BOARD_SIZE = 11;

    @Override
    public String getName() {
        return "Aggressive";
    }

    @Override
    public int[] chooseMove(int[][] octagonOwner, int[][] rhombicOwner, int botColour) {

        int opponentColour;
        if (botColour == 1)
            opponentColour = 2;
        else
            opponentColour = 1;

        // play center on first move if possible
        int mid = BOARD_SIZE / 2;
        if (octagonOwner[mid][mid] == 0 && countOurStones(octagonOwner, botColour) == 0)
            return new int[]{mid, mid, 0};

        // try win immediately
        int[] winningMove = findWinningMove(octagonOwner, rhombicOwner, botColour);
        if (winningMove != null)
            return winningMove;

        // block opponent from winning
        int[] blockingMove = findWinningMove(octagonOwner, rhombicOwner, opponentColour);
        if (blockingMove != null)
            return blockingMove;

        // place our bridge immediately without checking if there is a better move
        int[] ourBridge = findOurBridgeMove(octagonOwner, rhombicOwner, botColour, opponentColour);
        if (ourBridge != null)
            return ourBridge;

        // stop the opponent connecting their diagonal stones using a rhombic tile
        int[] theirBridge = findOpponentBridgeMove(octagonOwner, rhombicOwner, opponentColour);
        if (theirBridge != null)
            return theirBridge;

        // pick a move that helps with our win and gets in the opponents path
        int[] confrontationalMove = findConfrontationalMove(octagonOwner, botColour, opponentColour);
        if (confrontationalMove != null)
            return confrontationalMove;

        // fallback -> scan outward from center
        return fallbackToCenter(octagonOwner);
    }

    // count how many stones of the given colour are on the board
    private int countOurStones(int[][] octagonOwner, int colour) {
        int count = 0;
        for (int r = 0; r < BOARD_SIZE; r++)
            for (int c = 0; c < BOARD_SIZE; c++)
                if (octagonOwner[r][c] == colour)
                    count++;
        return count;
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

    // place the first rhombic tile it finds that connects two of our diagonal stones
    private int[] findOurBridgeMove(int[][] octagonOwner, int[][] rhombicOwner, int botColour, int opponentColour) {
        for (int r = 0; r < BOARD_SIZE - 1; r++) {
            for (int c = 0; c < BOARD_SIZE - 1; c++) {
                if (rhombicOwner[r][c] != 0)
                    continue;

                boolean topLeft = octagonOwner[r][c] == botColour;
                boolean topRight = octagonOwner[r][c + 1] == botColour;
                boolean bottomLeft = octagonOwner[r + 1][c] == botColour;
                boolean bottomRight = octagonOwner[r + 1][c + 1] == botColour;

                boolean ourDiagonal1 = topLeft && bottomRight && !topRight && !bottomLeft;
                boolean ourDiagonal2 = topRight && bottomLeft && !topLeft && !bottomRight;

                if (ourDiagonal1 || ourDiagonal2)
                    return new int[]{r, c, 1};
            }
        }
        return null;
    }

    // stops the opponent from connecting their diagonal stones using a rhombic tile
    // triggered when two of their diagonally opposite corners are already placed
    private int[] findOpponentBridgeMove(int[][] octagonOwner, int[][] rhombicOwner, int opponentColour) {
        for (int r = 0; r < BOARD_SIZE - 1; r++) {
            for (int c = 0; c < BOARD_SIZE - 1; c++) {
                if (rhombicOwner[r][c] != 0)
                    continue;

                boolean oppDiagonal1 = octagonOwner[r][c] == opponentColour && octagonOwner[r + 1][c + 1] == opponentColour;
                boolean oppDiagonal2 = octagonOwner[r][c + 1] == opponentColour && octagonOwner[r + 1][c] == opponentColour;

                if (oppDiagonal1 || oppDiagonal2)
                    return new int[]{r, c, 1};
            }
        }
        return null;
    }

    // finds a tile on our shortest path that is also useful to the opponent
    // the bot moves toward its goal while also getting in the opponents way
    private int[] findConfrontationalMove(int[][] octagonOwner, int botColour, int opponentColour) {

        int[] ourDist = getDistanceArray(octagonOwner, botColour, opponentColour);
        int[] oppDist = getDistanceArray(octagonOwner, opponentColour, botColour);

        // find best distance to the goal edge
        int ourBestGoal = Integer.MAX_VALUE;

        if (botColour == 1) {
            for (int c = 0; c < BOARD_SIZE; c++) {
                int idx = (BOARD_SIZE - 1) * BOARD_SIZE + c;
                if (ourDist[idx] < ourBestGoal)
                    ourBestGoal = ourDist[idx];
            }
        } else {
            for (int r = 0; r < BOARD_SIZE; r++) {
                int idx = r * BOARD_SIZE + (BOARD_SIZE - 1);
                if (ourDist[idx] < ourBestGoal)
                    ourBestGoal = ourDist[idx];
            }
        }

        int bestScore = Integer.MAX_VALUE;
        int[] bestMove = null;

        for (int r = 0; r < BOARD_SIZE; r++) {
            for (int c = 0; c < BOARD_SIZE; c++) {
                if (octagonOwner[r][c] != 0)
                    continue;

                int idx = r * BOARD_SIZE + c;
                if (ourDist[idx] == Integer.MAX_VALUE || oppDist[idx] == Integer.MAX_VALUE)
                    continue;

                // only use cells that sit on our shortest path
                if (ourDist[idx] >= ourBestGoal)
                    continue;

                // use cells that are also most important to the opponent
                int distFromCenter = Math.abs(r - BOARD_SIZE / 2) + Math.abs(c - BOARD_SIZE / 2);
                int score = oppDist[idx] + distFromCenter / 2;

                if (score < bestScore) {
                    bestScore = score;
                    bestMove = new int[]{r, c, 0};
                }
            }
        }

        return bestMove;
    }

    // runs Dijkstra from one edge and returns a distance array for every cell
    private int[] getDistanceArray(int[][] octagonOwner, int colour, int blockedColour) {

        int[] dist = new int[BOARD_SIZE * BOARD_SIZE];
        Arrays.fill(dist, Integer.MAX_VALUE);

        PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(a -> a[0]));

        if (colour == 1) {
            for (int c = 0; c < BOARD_SIZE; c++) {
                if (octagonOwner[0][c] != blockedColour) {
                    int cost;
                    if (octagonOwner[0][c] == colour)
                        cost = 0;
                    else
                        cost = 1;
                    dist[c] = cost;
                    pq.offer(new int[]{cost, c});
                }
            }
        } else {
            for (int r = 0; r < BOARD_SIZE; r++) {
                int idx = r * BOARD_SIZE;
                if (octagonOwner[r][0] != blockedColour) {
                    int cost;
                    if (octagonOwner[r][0] == colour)
                        cost = 0;
                    else
                        cost = 1;
                    dist[idx] = cost;
                    pq.offer(new int[]{cost, idx});
                }
            }
        }

        int[][] directions = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};

        while (!pq.isEmpty()) {
            int[] current = pq.poll();
            int d = current[0];
            int idx = current[1];

            if (d > dist[idx])
                continue;

            int r = idx / BOARD_SIZE;
            int c = idx % BOARD_SIZE;

            for (int[] dir : directions) {
                int nr = r + dir[0];
                int nc = c + dir[1];

                if (nr < 0 || nc < 0 || nr >= BOARD_SIZE || nc >= BOARD_SIZE)
                    continue;
                if (octagonOwner[nr][nc] == blockedColour)
                    continue;

                int nIdx = nr * BOARD_SIZE + nc;
                int newCost;
                if (octagonOwner[nr][nc] == colour)
                    newCost = d;
                else
                    newCost = d + 1;

                if (newCost < dist[nIdx]) {
                    dist[nIdx] = newCost;
                    pq.offer(new int[]{newCost, nIdx});
                }
            }
        }

        return dist;
    }

    // searches outward from center for the first empty octagon
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
