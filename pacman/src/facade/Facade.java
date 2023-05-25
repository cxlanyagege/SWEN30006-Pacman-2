package src.facade;

import src.Driver;
import src.matachi.mapeditor.editor.Controller;
import src.pacmanGame.Game;
import src.pacmanGame.GameConfig;

import java.util.List;


public class Facade {

    private static Facade instance;
    private List<String> mapStrings;

    private Controller controller;
    private Game game;

    private boolean mapLoaded = false;


    private final GameConfig gameConfig = Driver.gameConfig;
    //Properties properties = PropertiesLoader.loadPropertiesFile(propertiesPath);


    private Facade() {

    }

    public static synchronized Facade getInstance() {
        if (instance == null) {
            instance = new Facade();
        }
        return instance;
    }


    public void startEditor() {
        controller = Controller.getInstance();
    }


    public synchronized void startGame() {

        if (mapLoaded) {

            this.game = new Game(gameConfig.getGameCallback(), gameConfig.getProperties(), mapStrings);
            //System.out.println(mapStrings.get(0));
        } else {
            System.out.println("Map not loaded");
        }

    }

    public synchronized void passMapString(List<String> mapStrings) {
        this.mapStrings = mapStrings;
    }

    public synchronized void mapLoaded() {
        this.mapLoaded = true;
    }


}
