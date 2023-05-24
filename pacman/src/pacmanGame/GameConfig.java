package src.pacmanGame;

import src.utility.GameCallback;

import java.util.Properties;

public class GameConfig {
    private GameCallback gameCallback;
    private Properties properties;
    private String mapString;

    private static GameConfig instance;


    public static GameConfig getInstance() {
        if (instance == null) {
            instance = new GameConfig();
        }
        return instance;
    }

    public GameConfig() {
    }


    public GameConfig(GameCallback gameCallback, Properties properties) {
        this.gameCallback = gameCallback;
        this.properties = properties;

    }

    public GameConfig(GameCallback gameCallback, Properties properties, String mapString) {
        this.gameCallback = gameCallback;
        this.properties = properties;
        this.mapString = mapString;
    }

    public GameCallback getGameCallback() {
        return gameCallback;
    }

    public void setGameCallback(GameCallback gameCallback) {
        this.gameCallback = gameCallback;
        System.out.println("setGameCallback");
    }

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
        //System.out.println("setProperties");
    }

    public void setGameCallBack(GameCallback gameCallback) {
        this.gameCallback = gameCallback;
    }

    public String getMapString() {
        return mapString;
    }

    public void setMapString(String mapString) {
        this.mapString = mapString;
    }
}
