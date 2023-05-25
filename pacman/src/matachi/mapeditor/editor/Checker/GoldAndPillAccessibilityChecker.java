package src.matachi.mapeditor.editor.Checker;


import src.matachi.mapeditor.grid.Grid;

import java.util.*;

import static src.matachi.mapeditor.editor.Checker.LogUtil.writeToLogFile;

public class GoldAndPillAccessibilityChecker implements LevelChecker {
    private Grid model;
    private String currentFileName;

    public GoldAndPillAccessibilityChecker(Grid model, String currentFileName) {
        this.model = model;
        this.currentFileName = currentFileName;
    }

    @Override
    public boolean check() {
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


}
