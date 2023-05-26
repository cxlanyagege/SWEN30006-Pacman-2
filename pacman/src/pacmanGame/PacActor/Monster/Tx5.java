/*
 *  Team Name: monday-16-15-team-04
 *  Team Member:
 *               Xinyi Yuan
 *               He Shen
 *               Yuchen Dong
 */

package src.pacmanGame.PacActor.Monster;

import ch.aplu.jgamegrid.Location;
import src.pacmanGame.TorusVerseGame;

public class Tx5 extends Monster {
    private final TorusVerseGame torusVerseGame;
    public Tx5(TorusVerseGame torusVerseGame) {
        super(torusVerseGame, MonsterType.TX5);
        this.torusVerseGame = torusVerseGame;
    }

    @Override
    protected void walkApproach() {
        Location pacLocation = torusVerseGame.pacMan.getLocation();
        double oldDirection = getDirection();

        // Walking approach:
        // TX5: Determine direction to pacActor and try to move in that direction. Otherwise, random walk.
        Location.CompassDirection compassDir =
                getLocation().get4CompassDirectionTo(pacLocation);
        Location next = getLocation().getNeighbourLocation(compassDir);
        setDirection(compassDir);
        if (!isVisited(next) && canMove(next))
        {
            setLocation(next);
        }
        else
        {
            next = walkRandom(oldDirection);
        }
        torusVerseGame.getGameCallback().monsterLocationChanged(this);
        addVisitedList(next);
    }
}