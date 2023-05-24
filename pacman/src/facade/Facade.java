package src.facade;

import src.Driver;
import src.matachi.mapeditor.editor.Controller;
import src.pacmanGame.Game;
import src.pacmanGame.GameConfig;




public class Facade {

    private static Facade instance;
    private String mapString;

     private Controller controller;
     private Game game;

     private boolean mapLoaded = false;



     private GameConfig gameConfig= Driver.gameConfig;
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

        if(mapLoaded == false){

            game = new Game(gameConfig.getGameCallback(),gameConfig.getProperties());
        }else{
            game = new Game(gameConfig.getGameCallback(),gameConfig.getProperties(),mapString);

        }

    }

    public synchronized void passMapString(String mapString){
        this.mapString = mapString;
    }

    public synchronized void mapLoaded(){
        mapLoaded = true;
    }








}
