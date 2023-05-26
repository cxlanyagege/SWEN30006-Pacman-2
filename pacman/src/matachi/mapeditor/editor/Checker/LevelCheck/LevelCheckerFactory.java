package src.matachi.mapeditor.editor.Checker.LevelCheck;


import src.matachi.mapeditor.grid.Grid;

public class LevelCheckerFactory {
    public static LevelChecker createPacManStartChecker(Grid model, String currentFileName) {
        return new PacManStartChecker(model,currentFileName);
    }

    public static LevelChecker createPortalCountChecker(Grid model, String currentFileName)  {
        return new PortalCountChecker(model,currentFileName);
    }

    public static LevelChecker createGoldAndPillCountChecker(Grid model, String currentFileName)  {
        return new GoldAndPillCountChecker(model,currentFileName);
    }

    public static LevelChecker createGoldAndPillAccessibilityChecker(Grid model, String currentFileName) {
        return new GoldAndPillAccessibilityChecker(model,currentFileName);
    }
}

