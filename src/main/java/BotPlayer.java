import java.util.*;

public class BotPlayer {

    private Random random = new Random();
    private int BOARD_SIZE = 11;

    // tracks which strategy was used for the last move — used by dev mode
    private String lastStrategy = "Random";

    public String getLastStrategy() {
        return lastStrategy;
    }

    // randomly decide whether to use the pie rule (50/50)
    public boolean decidePieRule() {
        return random.nextBoolean();
    }

    // main entry point for choosing a move
    // tries strategies in priority order: win -> block -> best move -> random
    public int[] chooseMove(int[][] octagonOwner, int[][] rhombicOwner, int botColour) {
        int humanColour = botColour == 1 ? 2 : 1;

        // 1. take the win if its there
        int[] winMove = findWinningMove(octagonOwner, rhombicOwner, botColour);
        if (winMove != null) {
            lastStrategy = "Win";
            return winMove;
        }

        // 2. block the human if they are about to win
        int[] blockMove = findBlockingMove(octagonOwner, rhombicOwner, humanColour, botColour);
        if (blockMove != null) {
            lastStrategy = "Block";
            return blockMove;
        }

        // 3. use threat-aware heuristic to find the best strategic tile
        int[] bestMove = findBestMove(octagonOwner, rhombicOwner, botColour, humanColour);
        if (bestMove != null) {
            lastStrategy = "Best Move";
            return bestMove;
        }

        // fallback: random
        lastStrategy = "Random";
        return chooseRandomMove(octagonOwner, rhombicOwner);
    }

    // kept for backwards compatibility with old random bot calls
    public int[] chooseMove(int[][] octagonOwner, int[][] rhombicOwner) {
        return chooseRandomMove(octagonOwner, rhombicOwner);
    }

    // --- STRATEGY 1: WIN DETECTION ---
    // tries placing on every empty tile and checks if it wins the game
    private int[] findWinningMove(int[][] octagonOwner, int[][] rhombicOwner, int botColour) {
        WinChecker winChecker = new WinChecker(BOARD_SIZE);

        // check octagons
        for (int r = 0; r < BOARD_SIZE; r++) {
            for (int c = 0; c < BOARD_SIZE; c++) {
                if (octagonOwner[r][c] == 0) {
                    octagonOwner[r][c] = botColour;
                    boolean wins = winChecker.checkWin(octagonOwner, rhombicOwner, botColour);
                    octagonOwner[r][c] = 0;
                    if (wins) return new int[]{r, c, 0};
                }
            }
        }

        // check rhombics
        for (int r = 0; r < BOARD_SIZE - 1; r++) {
            for (int c = 0; c < BOARD_SIZE - 1; c++) {
                if (rhombicOwner[r][c] == 0) {
                    rhombicOwner[r][c] = botColour;
                    boolean wins = winChecker.checkWin(octagonOwner, rhombicOwner, botColour);
                    rhombicOwner[r][c] = 0;
                    if (wins) return new int[]{r, c, 1};
                }
            }
        }

        return null;
    }

    // --- STRATEGY 2: BLOCK DETECTION ---
    // tries placing on every empty tile and checks if the human would win there
    // if yes, the bot blocks that tile
    private int[] findBlockingMove(int[][] octagonOwner, int[][] rhombicOwner, int humanColour, int botColour) {
        WinChecker winChecker = new WinChecker(BOARD_SIZE);

        // check octagons
        for (int r = 0; r < BOARD_SIZE; r++) {
            for (int c = 0; c < BOARD_SIZE; c++) {
                if (octagonOwner[r][c] == 0) {
                    octagonOwner[r][c] = humanColour;
                    boolean humanWins = winChecker.checkWin(octagonOwner, rhombicOwner, humanColour);
                    octagonOwner[r][c] = 0;
                    if (humanWins) return new int[]{r, c, 0};
                }
            }
        }

        // check rhombics
        for (int r = 0; r < BOARD_SIZE - 1; r++) {
            for (int c = 0; c < BOARD_SIZE - 1; c++) {
                if (rhombicOwner[r][c] == 0) {
                    rhombicOwner[r][c] = humanColour;
                    boolean humanWins = winChecker.checkWin(octagonOwner, rhombicOwner, humanColour);
                    rhombicOwner[r][c] = 0;
                    if (humanWins) return new int[]{r, c, 1};
                }
            }
        }

        return null;
    }

    // --- STRATEGY 3: THREAT-AWARE HEURISTIC ---
    // scores every empty tile based on how much it helps the bot AND hurts the human
    // picks the tile with the best combined score
    private int[] findBestMove(int[][] octagonOwner, int[][] rhombicOwner, int botColour, int humanColour) {
        int[] bestMove = null;
        double bestScore = Double.MAX_VALUE;

        // score each empty octagon
        for (int r = 0; r < BOARD_SIZE; r++) {
            for (int c = 0; c < BOARD_SIZE; c++) {
                if (octagonOwner[r][c] == 0) {
                    double score = scoreTile(octagonOwner, rhombicOwner, r, c, botColour, humanColour);
                    if (score < bestScore) {
                        bestScore = score;
                        bestMove = new int[]{r, c, 0};
                    }
                }
            }
        }

        // score each empty rhombic
        for (int r = 0; r < BOARD_SIZE - 1; r++) {
            for (int c = 0; c < BOARD_SIZE - 1; c++) {
                if (rhombicOwner[r][c] == 0) {
                    double score = scoreTile(octagonOwner, rhombicOwner, r, c, botColour, humanColour);
                    if (score < bestScore) {
                        bestScore = score;
                        bestMove = new int[]{r, c, 1};
                    }
                }
            }
        }

        return bestMove;
    }

    //scores a tile — lower is better


    private double scoreTile(int[][] octagonOwner, int[][] rhombicOwner, int r, int c, int botColour, int humanColour) {

        //base score: distance to goal (lower = closer to winning)
        double botDist = distanceToGoal(r, c, botColour);

        // if this tile is adjacent to an existing bot tile, it extends the chain so we reduce the score
        double adjacencyBonus = 0;
        int[][] neighbours = {{r-1,c},{r+1,c},{r,c-1},{r,c+1}};
        for (int[] n : neighbours) {
            int nr = n[0], nc = n[1];
            if (nr >= 0 && nc >= 0 && nr < BOARD_SIZE && nc < BOARD_SIZE) {
                if (octagonOwner[nr][nc] == botColour) {
                    adjacencyBonus -= 3; //extending existing chain
                }
            }
        }

        //human threat - tiles in the middle of the human's path
        double humanThreat = distanceToGoal(r, c, humanColour) + distanceToStart(r, c, humanColour);

        //combine: goal distance + adjacency bonus - human threat disrupt
        return (botDist + adjacencyBonus) - (0.3 * humanThreat);
    }

    //distance from tile to the colour's goal edge
    private double distanceToGoal(int r, int c, int colour) {
        if (colour == 1) {
            return BOARD_SIZE - 1 - r; //black - distance to bottom row
        } else {
            return BOARD_SIZE - 1 - c; //white - distance to right col
        }
    }

    //distance from tile to the colour's start edge
    private double distanceToStart(int r, int c, int colour) {
        if (colour == 1) {
            return r; //black- distance to top row
        } else {
            return c; //white -distance to left col
        }
    }




    //DFS helper for finding connected clusters of tiles
    private void dfsCluster(int[][] octagonOwner, boolean[][] visited, int r, int c, int colour, List<int[]> cluster) {
        if (r < 0 || c < 0 || r >= BOARD_SIZE || c >= BOARD_SIZE) return;
        if (visited[r][c] || octagonOwner[r][c] != colour) return;

        visited[r][c] = true;
        cluster.add(new int[]{r, c});

        dfsCluster(octagonOwner, visited, r-1, c, colour, cluster);
        dfsCluster(octagonOwner, visited, r+1, c, colour, cluster);
        dfsCluster(octagonOwner, visited, r, c-1, colour, cluster);
        dfsCluster(octagonOwner, visited, r, c+1, colour, cluster);
    }


    //picks a completely random empty tile
    private int[] chooseRandomMove(int[][] octagonOwner, int[][] rhombicOwner) {
        List<int[]> availableMoves = new ArrayList<>();

        for (int r = 0; r < octagonOwner.length; r++) {
            for (int c = 0; c < octagonOwner[r].length; c++) {
                if (octagonOwner[r][c] == 0) {
                    availableMoves.add(new int[]{r, c, 0});
                }
            }
        }

        for (int r = 0; r < rhombicOwner.length; r++) {
            for (int c = 0; c < rhombicOwner[r].length; c++) {
                if (rhombicOwner[r][c] == 0) {
                    availableMoves.add(new int[]{r, c, 1});
                }
            }
        }

        if (availableMoves.isEmpty()) return null;
        return availableMoves.get(random.nextInt(availableMoves.size()));
    }
}