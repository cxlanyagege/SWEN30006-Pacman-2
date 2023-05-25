package src.pacmanGame;

import ch.aplu.jgamegrid.Location;

public class Tx5 extends Monster {
    private final Game game;
    public Tx5(Game game) {
        super(game, MonsterType.TX5);
        this.game = game;
    }

    @Override
    protected void walkApproach() {
        Location pacLocation = game.pacMan.getLocation();
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
        game.getGameCallback().monsterLocationChanged(this);
        addVisitedList(next);
    }
}