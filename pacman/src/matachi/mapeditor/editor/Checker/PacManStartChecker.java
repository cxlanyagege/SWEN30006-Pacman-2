package src.matachi.mapeditor.editor.Checker;

import src.matachi.mapeditor.grid.Grid;
import java.util.ArrayList;
import java.util.List;

import static src.matachi.mapeditor.editor.Checker.LogUtil.writeToLogFile;


public class PacManStartChecker implements LevelChecker{
    private Grid model;
    private String currentFileName;

    public PacManStartChecker(Grid model, String currentFileName) {
        this.model = model;
        this.currentFileName = currentFileName;
    }
    public boolean check(){
        boolean flag = true;

        int pacManStartCount = 0;
        List<String> pacManStartCoordinates = new ArrayList<>();

        for (int row = 0; row < model.getHeight(); row++) {
            for (int col = 0; col < model.getWidth(); col++) {
                char tileChar = model.getTile(col, row);
                if (tileChar == 'f') {
                    pacManStartCount++;
                    pacManStartCoordinates.add("(" + col + ", " + row + ")");
                }
            }
        }

        if (pacManStartCount != 1) {
            String message = String.format("[Level %s – no start or more than one start for PacMan: %s]", currentFileName, String.join("; ", pacManStartCoordinates));
            writeToLogFile(message,currentFileName);
            flag = false;
        }

        return flag;
    }

}
