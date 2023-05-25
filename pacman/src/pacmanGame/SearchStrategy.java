package src.pacmanGame;

import ch.aplu.jgamegrid.Location;

public interface SearchStrategy {
    public Location[] search(TorusVerseGame torusVerseGame, PacMan actor);
}
