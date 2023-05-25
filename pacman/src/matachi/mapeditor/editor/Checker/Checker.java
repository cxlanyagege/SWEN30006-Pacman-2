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


    /**
     * check the validity of a level
     */
    public boolean levelCheck() {
        boolean isPacManStartValid = checkPacManStart();
        boolean isPortalCountValid = checkPortalCount();
        boolean isGoldAndPillCountValid = checkGoldAndPillCount();
        boolean isGoldAndPillAccessible = checkGoldAndPillAccessibility();

        return isPacManStartValid && isPortalCountValid && isGoldAndPillCountValid && isGoldAndPillAccessible;
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


    // level check helper methods

    public   boolean checkPacManStart() {
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

    private boolean checkPortalCount() {
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

    private boolean checkGoldAndPillCount() {
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

        if (goldCount < 2 || pillCount < 2) {
            String message = String.format("[Level %s - Insufficient number of Gold or Pill: Gold = %d, Pill = %d]",
                    currentFileName, goldCount, pillCount);
            writeToLogFile(message,currentFileName);
            flag = false;
        }

        return flag;
    }
    private boolean checkGoldAndPillAccessibility() {
        // 获取金币和药丸的位置
        Set<Integer> goldPositions = new HashSet<>();
        Set<Integer> pillPositions = new HashSet<>();

        for (int row = 0; row < model.getHeight(); row++) {
            for (int col = 0; col < model.getWidth(); col++) {
                char tileChar = model.getTile(col, row);
                if (tileChar == 'd') { // GoldTile
                    int position = row * model.getWidth() + col;
                    goldPositions.add(position);
                } else if (tileChar == 'c') { // PillTile
                    int position = row * model.getWidth() + col;
                    pillPositions.add(position);
                }
            }
        }

        List<Integer> inaccessibleGoldPositions = new ArrayList<>();
        List<Integer> inaccessiblePillPositions = new ArrayList<>();

        // 检查金币的可访问性
        for (int position : goldPositions) {
            if (!isAccessible(position)) {
                inaccessibleGoldPositions.add(position);
            }
        }

        // 检查药丸的可访问性
        for (int position : pillPositions) {
            if (!isAccessible(position)) {
                inaccessiblePillPositions.add(position);
            }
        }

        // 如果存在不可访问的金币位置
        if(inaccessibleGoldPositions.isEmpty() && inaccessiblePillPositions.isEmpty()) {
            return true;
        }

        // 如果存在不可访问的金币位置
        if (!inaccessibleGoldPositions.isEmpty()) {
            List<String> goldPositionsList = new ArrayList<>();
            for (int position : inaccessibleGoldPositions) {
                int row = position / model.getWidth();
                int col = position % model.getWidth();
                String positionString = "(" + col + ", " + row + ")";
                goldPositionsList.add(positionString);
            }

            String goldPositionsString = String.join("; ", goldPositionsList);
            String message = String.format("[Level %s - Gold not accessible: %s]", currentFileName, goldPositionsString);
            System.out.println(message);
            writeToLogFile(message,currentFileName);
        }

        // 如果存在不可访问的药丸位置
        if (!inaccessiblePillPositions.isEmpty()) {
            List<String> pillPositionsList = new ArrayList<>();
            for (int position : inaccessiblePillPositions) {
                int row = position / model.getWidth();
                int col = position % model.getWidth();
                String positionString = "(" + col + ", " + row + ")";
                pillPositionsList.add(positionString);
            }

            String pillPositionsString = String.join("; ", pillPositionsList);
            String message = String.format("[Level %s - Pill not accessible: %s]", currentFileName, pillPositionsString);
            System.out.println(message);
            writeToLogFile(message,currentFileName);
        }
        return false;
    }

    private boolean isAccessible(int startPosition) {
        boolean[] visited = new boolean[model.getWidth() * model.getHeight()];

        // 使用广度优先搜索算法检查可访问性
        Queue<Integer> queue = new ArrayDeque<>();
        queue.offer(startPosition);
        visited[startPosition] = true;

        while (!queue.isEmpty()) {
            int position = queue.poll();
            int row = position / model.getWidth();
            int col = position % model.getWidth();

            // 检查上方方向
            if (row > 0 && isAccessibleTile(col, row - 1, visited, queue)) {
                return true;
            }

            // 检查下方方向
            if (row < model.getHeight() - 1 && isAccessibleTile(col, row + 1, visited, queue)) {
                return true;
            }

            // 检查左方方向
            if (col > 0 && isAccessibleTile(col - 1, row, visited, queue)) {
                return true;
            }

            // 检查右方方向
            if (col < model.getWidth() - 1 && isAccessibleTile(col + 1, row, visited, queue)) {
                return true;
            }
        }

        return false;
    }

    private boolean isAccessibleTile(int col, int row, boolean[] visited, Queue<Integer> queue) {
        int position = row * model.getWidth() + col;
        if (!visited[position]) {
            char tileChar = model.getTile(col, row);
            if (tileChar == 'a' || tileChar == 'c' || tileChar == 'd' || tileChar == 'e' || tileChar == 'f' || isPortal(tileChar)) {
                visited[position] = true;
                queue.offer(position);

                if (isPortal(tileChar)) {
                    // 获取对应传送门的位置
                    int pairPosition = getPortalPairPosition(tileChar);
                    if (pairPosition != -1 && !visited[pairPosition]) {
                        System.out.println(queue);
                        queue.offer(pairPosition);
                        System.out.println(queue);
                        //visited[pairPosition] = true;
                    }
                }

                if (tileChar == 'f') {
                    return true; // 找到可访问的位置
                }
            }
        }
        return false;
    }

    private boolean isPortal(char tileChar) {
        return tileChar == 'i' || tileChar == 'j' || tileChar == 'k' || tileChar == 'l';
    }

    private int getPortalPairPosition(char portalChar) {
        boolean firstPortalFound = false;
        for (int row = 0; row < model.getHeight(); row++) {
            for (int col = 0; col < model.getWidth(); col++) {
                char tileChar = model.getTile(col, row);
                if (tileChar == portalChar) {
                    if (firstPortalFound) {
                        return row * model.getWidth() + col;
                    }
                    firstPortalFound = true;
                }
            }
        }
        return -1; // 返回 -1 表示未找到对应的传送门
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
