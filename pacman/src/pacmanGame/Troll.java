package src.pacmanGame;

import ch.aplu.jgamegrid.Location;

public class Troll extends Monster {
    private final Game game;
    public Troll(Game game) {
        super(game, MonsterType.Troll);
        this.game = game;
    }

    @Override
    protected void walkApproach() {
        double oldDirection = getDirection();

        // Walking approach:
        // Troll: Random walk.
        Location next = walkRandom(oldDirection);
        game.getGameCallback().monsterLocationChanged(this);
        addVisitedList(next);
    }
}