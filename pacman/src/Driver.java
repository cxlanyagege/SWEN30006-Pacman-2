package src;

import src.utility.GameCallback;
import src.utility.PropertiesLoader;

import java.util.Properties;

public class Driver {
    public static final String DEFAULT_PROPERTIES_PATH = "properties/test2.properties";

    private static String mapString = "aaaaaaaaaabbccccccca\n" +
            "aaaaaaabbbcccaaaaaca\n" +
            "aaaaaaabccccaaaaaacc\n" +
            "aaaaaabbcaaaaaaiaaac\n" +
            "aaabbbbcaaabbbbbbbbc\n" +
            "aabccacaajabcccccccc\n" +
            "aaabacaaaaabbbcbbbbb\n" +
            "aabbcaaajaaabahaaaaa\n" +
            "aabcalaaaaaabaaaaaga\n" +
            "aabaaaaaakaabkaafaaa\n" +
            "aaabaaaalaaabaaaaaai\n";

    /**
     * Starting point
     * @param args the command line arguments
     */

    public static void main(String args[]) {
        String propertiesPath = DEFAULT_PROPERTIES_PATH;
        if (args.length > 0) {
            propertiesPath = args[0];
        }
        final Properties properties = PropertiesLoader.loadPropertiesFile(propertiesPath);
        GameCallback gameCallback = new GameCallback();
        //new Game(gameCallback, properties);
        new Game(gameCallback, properties, mapString);

    }
}
