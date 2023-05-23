package src.facade;

import src.Driver;
import src.matachi.mapeditor.editor.Controller;
import src.pacmanGame.Game;
import src.pacmanGame.GameConfig;




public class Facade {


    private String mapString;

     private Controller controller;
     private Game game;

     private boolean mapLoaded = false;



     private GameConfig gameConfig= Driver.gameConfig;
     //Properties properties = PropertiesLoader.loadPropertiesFile(propertiesPath);



    public Facade(){

    }



    public void startEditor(){
        controller = new Controller();
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
