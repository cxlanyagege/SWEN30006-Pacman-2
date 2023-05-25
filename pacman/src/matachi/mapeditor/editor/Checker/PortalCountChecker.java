package src.matachi.mapeditor.editor.Checker;


import src.matachi.mapeditor.grid.Grid;

import java.util.ArrayList;
import java.util.List;

import static src.matachi.mapeditor.editor.Checker.LogUtil.writeToLogFile;

public class PortalCountChecker implements LevelChecker {
    private Grid model;
    private String currentFileName;

    public PortalCountChecker(Grid model, String currentFileName) {
        this.model = model;
        this.currentFileName = currentFileName;
    }

    @Override
    public boolean check() {
        System.out.println("PortalCountCheker");
        List<String> portalWhiteCoordinates = new ArrayList<>();
        List<String> portalYellowCoordinates = new ArrayList<>();
        List<String> portalDarkGoldCoordinates = new ArrayList<>();
        List<String> portalDarkGrayCoordinates = new ArrayList<>();

        for (int row = 0; row < model.getHeight(); row++) {
            for (int col = 0; col < model.getWidth(); col++) {
                char tileChar = model.getTile(col, row);
                if (tileChar == 'i') { // Portal White
                    portalWhiteCoordinates.add("(" + col + ", " + row + ")");
                } else if (tileChar == 'j') { // Portal Yellow
                    portalYellowCoordinates.add("(" + col + ", " + row + ")");
                } else if (tileChar == 'k') { // Portal Dark Gold
                    portalDarkGoldCoordinates.add("(" + col + ", " + row + ")");
                } else if (tileChar == 'l') { // Portal Dark Gray
                    portalDarkGrayCoordinates.add("(" + col + ", " + row + ")");
                }
            }
        }

        boolean validPortalCount = (portalWhiteCoordinates.size() == 0 || portalWhiteCoordinates.size() == 2)
                && (portalYellowCoordinates.size() == 0 || portalYellowCoordinates.size() == 2)
                && (portalDarkGoldCoordinates.size() == 0 || portalDarkGoldCoordinates.size() == 2)
                && (portalDarkGrayCoordinates.size() == 0 || portalDarkGrayCoordinates.size() == 2);


        if (!validPortalCount) {
            String message = String.format("[Level %s - portal count is not as expected: Portal White = %d %s, Portal Yellow = %d %s, Portal Dark Gold = %d %s, Portal Dark Gray = %d %s]",
                    currentFileName, portalWhiteCoordinates.size(), portalWhiteCoordinates,
                    portalYellowCoordinates.size(), portalYellowCoordinates,
                    portalDarkGoldCoordinates.size(), portalDarkGoldCoordinates,
                    portalDarkGrayCoordinates.size(), portalDarkGrayCoordinates);
            System.out.println(message);
            writeToLogFile(message,currentFileName);
        }

        return validPortalCount;
    }

}
