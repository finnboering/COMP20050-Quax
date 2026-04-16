package bot;

import game.Tile;
import game.Board;


public interface Strategy {
    Tile getMove(Board b);
}