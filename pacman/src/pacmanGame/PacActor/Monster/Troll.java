package src.pacmanGame.PacActor.Monster;

import ch.aplu.jgamegrid.Location;
import src.pacmanGame.PacActor.Monster.Monster;
import src.pacmanGame.PacActor.Monster.MonsterType;
import src.pacmanGame.TorusVerseGame;

public class Troll extends Monster {
    private final TorusVerseGame torusVerseGame;
    public Troll(TorusVerseGame torusVerseGame) {
        super(torusVerseGame, MonsterType.Troll);
        this.torusVerseGame = torusVerseGame;
    }

    @Override
    protected void walkApproach() {
        double oldDirection = getDirection();

        // Walking approach:
        // Troll: Random walk.
        Location next = walkRandom(oldDirection);
        torusVerseGame.getGameCallback().monsterLocationChanged(this);
        addVisitedList(next);
    }
}