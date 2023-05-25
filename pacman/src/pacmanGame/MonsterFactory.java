package src.pacmanGame;

public class MonsterFactory {
    public static Monster createMonster(Game game, MonsterType type) {
        return switch (type) {
            case TX5 -> new Tx5(game);
            case Troll -> new Troll(game);
        };
    }
}
