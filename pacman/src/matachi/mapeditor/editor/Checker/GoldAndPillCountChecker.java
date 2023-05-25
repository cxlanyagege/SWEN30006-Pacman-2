package src.matachi.mapeditor.editor.Checker;

import src.matachi.mapeditor.grid.Grid;

import static src.matachi.mapeditor.editor.Checker.LogUtil.writeToLogFile;

public class GoldAndPillCountChecker implements LevelChecker {
    private Grid model;
    private String currentFileName;

    public GoldAndPillCountChecker(Grid model, String currentFileName) {
        this.model = model;
        this.currentFileName = currentFileName;
    }

    @Override
    public boolean check() {
        boolean flag = true;

        int goldCount = 0;
        int pillCount = 0;
        for (int row = 0; row < model.getHeight(); row++) {
            for (int col = 0; col < model.getWidth(); col++) {
                char tileChar = model.getTile(col, row);
                if (tileChar == 'd') {
                    goldCount++;
                } else if (tileChar == 'c') {
                    pillCount++;
                }
            }
        }

        if ((goldCount + pillCount) < 2) {
            String message = String.format("[Level %s - Insufficient number of Gold or Pill: Gold = %d, Pill = %d]",
                    currentFileName, goldCount, pillCount);
            writeToLogFile(message,currentFileName);
            flag = false;
        }

        return flag;
    }

}
