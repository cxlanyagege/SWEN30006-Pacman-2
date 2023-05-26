package src.pacmanGame.PacActor;

import ch.aplu.jgamegrid.Location;
import src.pacmanGame.PacActor.PacMan.PacMan;
import src.pacmanGame.TorusVerseGame;

public interface SearchStrategy {
    public Location[] search(TorusVerseGame torusVerseGame, PacMan actor);
}
