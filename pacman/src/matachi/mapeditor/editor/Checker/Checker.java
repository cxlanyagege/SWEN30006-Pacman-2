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
    public File[] gameCheck(File gameFolder) {
        File[] mapFiles = gameFolder.listFiles((dir, name) -> name.endsWith(".xml"));
        File[] validMapFiles = null;

        // 检查是否有XML文件
        if (mapFiles == null || mapFiles.length == 0) {
            String message = String.format("[Game %s – no maps found]", gameFolder.getName());
            System.out.println(message);
            writeToLogFile(message, currentFileName);
        } else {
            List<File> validMapFilesList = new ArrayList<>();

            // 检查地图文件名是否以数字开头
            for (File file : mapFiles) {
                String fileName = file.getName();
                if (Character.isDigit(fileName.charAt(0))) {
                    validMapFilesList.add(file);
                }
            }

            // no map pass game check
            if(validMapFiles == null){
                String message = String.format("[Game %s – no maps found]", gameFolder.getName());
                //System.out.println(message);
                writeToLogFile(message, currentFileName);

            }

            // 检查地图文件序列是否良好定义
            if (validMapFilesList.size() > 0) {
                validMapFiles = validMapFilesList.toArray(new File[0]);
                if (!checkMapFileSequence(validMapFiles)) {
                    validMapFiles = null;
                }
            }


        }
        
        System.out.println("num of validMaps: " + validMapFiles);
        return validMapFiles;
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
