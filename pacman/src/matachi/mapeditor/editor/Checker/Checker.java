package src.matachi.mapeditor.editor.Checker;
import static src.matachi.mapeditor.editor.Checker.LogUtil.writeToLogFile;

import src.matachi.mapeditor.editor.Controller;
import src.matachi.mapeditor.grid.Grid;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Checker {

    private Grid model;
    private String currentFileName;


    public Checker() {

        this.model = Controller.getInstance().getModel();
        this.currentFileName = Controller.getInstance().getCurrentFileName();

    }

    public boolean levelCheck() {
        LevelChecker pacManStartChecker = LevelCheckerFactory.createPacManStartChecker(model, currentFileName);
        LevelChecker portalCountChecker = LevelCheckerFactory.createPortalCountChecker(model, currentFileName);
        LevelChecker goldAndPillCountChecker = LevelCheckerFactory.createGoldAndPillCountChecker(model, currentFileName);
        LevelChecker goldAndPillAccessibilityChecker = LevelCheckerFactory.createGoldAndPillAccessibilityChecker(model, currentFileName);

        return pacManStartChecker.check()
                && portalCountChecker.check()
                && goldAndPillCountChecker.check()
                && goldAndPillAccessibilityChecker.check();
    }


    // Game Checking
    public boolean gameCheck(File gameFolder) {
        boolean flag = true;
        File[] mapFiles = gameFolder.listFiles((dir, name) -> name.endsWith(".xml"));

        // 检查是否至少有一个正确命名的地图文件
        if (mapFiles == null || mapFiles.length == 0) {
            String message = String.format("[Game %s – no maps found]", gameFolder.getName());
            System.out.println(message);
            writeToLogFile(message,currentFileName);
            flag = false;
        }

        // 检查地图文件序列是否良好定义
        if (!checkMapFileSequence(mapFiles)){
            flag = false;
        }

        return flag;
    }

    // game checker helper methods
    private boolean checkMapFileSequence(File[] mapFiles) {
        boolean flag = true;
        Map<String, List<String>> mapLevelFiles = new HashMap<>();

        // 将地图文件按等级分组
        for (File file : mapFiles) {
            String fileName = file.getName();
            String level = getLevelFromFileName(fileName);

            List<String> levelFiles = mapLevelFiles.getOrDefault(level, new ArrayList<>());
            levelFiles.add(fileName);
            mapLevelFiles.put(level, levelFiles);
        }

        // 检查每个等级的地图文件数量
        for (Map.Entry<String, List<String>> entry : mapLevelFiles.entrySet()) {
            String level = entry.getKey();
            List<String> levelFiles = entry.getValue();

            if (levelFiles.size() > 1) {
                String message = String.format("[Game %s – multiple maps at same level: %s]",
                        currentFileName, String.join("; ", levelFiles));
                System.out.println(message);
                writeToLogFile(message,currentFileName);
                flag = false;
            }
        }
        return flag;
    }

    private String getLevelFromFileName(String fileName) {
        StringBuilder levelBuilder = new StringBuilder();

        for (int i = 0; i < fileName.length(); i++) {
            char ch = fileName.charAt(i);
            if (Character.isDigit(ch)) {
                levelBuilder.append(ch);
            } else {
                break;
            }
        }

        return levelBuilder.toString();
    }



}
