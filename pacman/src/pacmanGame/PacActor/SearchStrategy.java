/*
 *  Team Name: monday-16-15-team-04
 *  Team Member:
 *               Xinyi Yuan
 *               He Shen
 *               Yuchen Dong
 */

package src.pacmanGame.PacActor;

import ch.aplu.jgamegrid.Location;
import src.pacmanGame.PacActor.PacMan.PacMan;
import src.pacmanGame.TorusVerseGame;

public interface SearchStrategy {
    public Location[] search(TorusVerseGame torusVerseGame, PacMan actor);
}
