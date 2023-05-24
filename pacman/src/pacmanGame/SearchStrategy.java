package src.pacmanGame;

import ch.aplu.jgamegrid.Location;

public interface SearchStrategy {
    public Location[] search(Game game, PacActor actor);
}
