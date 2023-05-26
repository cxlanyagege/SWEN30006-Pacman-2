/*
 *  Team Name: monday-16-15-team-04
 *  Team Member:
 *               Xinyi Yuan
 *               He Shen
 *               Yuchen Dong
 */

package src.pacmanGame.PacActor.Monster;

import src.pacmanGame.TorusVerseGame;

public class MonsterFactory {
    public MonsterFactory() {

    }
    public Monster createMonster(TorusVerseGame torusVerseGame, MonsterType type) {
        return switch (type) {
            case TX5 -> new Tx5(torusVerseGame);
            case Troll -> new Troll(torusVerseGame);
        };
    }
}
