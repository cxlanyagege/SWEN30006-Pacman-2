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



     private final GameConfig gameConfig= Driver.gameConfig;
     //Properties properties = PropertiesLoader.loadPropertiesFile(propertiesPath);



    private Facade(){

    }

    public static synchronized Facade getInstance() {
        if (instance == null) {
            instance = new Facade();
        }
        return instance;
    }



    public void startEditor(){
        controller = Controller.getInstance();
    }



    public synchronized void startGame(){

        //game = new Game(gameConfig.getGameCallback(),gameConfig.getProperties(),mapString);

        if(!mapLoaded){

            game = new Game(gameConfig.getGameCallback(),gameConfig.getProperties());
        }else if(mapStrings.size() == 1){
            game = new Game(gameConfig.getGameCallback(),gameConfig.getProperties(),mapStrings.get(0));
            System.out.println(mapStrings.get(0));

        }else{
            //TODO: implement multiple maps game
            System.out.println(mapStrings);
        }

    }

    public synchronized void passMapString(List<String> mapStrings){
        this.mapStrings = mapStrings;
    }

    public synchronized void mapLoaded(){
        mapLoaded = true;
    }








}
