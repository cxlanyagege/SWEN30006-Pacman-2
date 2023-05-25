package src.pacmanGame;

public class MonsterFactory {
    public static Monster createMonster(TorusVerseGame torusVerseGame, MonsterType type) {
        return switch (type) {
            case TX5 -> new Tx5(torusVerseGame);
            case Troll -> new Troll(torusVerseGame);
        };
    }
}
