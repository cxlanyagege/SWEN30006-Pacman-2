package src;

import src.facade.Facade;
import src.pacmanGame.GameConfig;
import src.utility.GameCallback;
import src.utility.PropertiesLoader;

import java.util.Properties;

public class Driver {
    public static final String DEFAULT_PROPERTIES_PATH = "properties/test.properties";
    public volatile static Properties properties;
    public volatile static GameConfig gameConfig;

    public static GameCallback gameCallback = new GameCallback();



    /**
     * Starting point
     * @param args the command line arguments
     */

    public static void main(String[] args) {
        String propertiesPath = DEFAULT_PROPERTIES_PATH;


        if (args.length > 0) {
            propertiesPath = args[0];
        }
        properties = PropertiesLoader.loadPropertiesFile(propertiesPath);

        gameConfig = GameConfig.getInstance();
        gameConfig.setGameCallBack(gameCallback);
        gameConfig.setProperties(properties);

        Facade facade = Facade.getInstance();
        facade.startEditor();






    }
}
