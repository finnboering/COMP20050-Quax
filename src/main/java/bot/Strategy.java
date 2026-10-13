package bot;

public interface Strategy {
    int[] chooseMove(int[][] octagonOwner, int[][] rhombicOwner, int botColour);
    String getName();
}
