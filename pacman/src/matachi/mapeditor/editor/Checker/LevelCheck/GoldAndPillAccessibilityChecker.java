/*
 *  Team Name: monday-16-15-team-04
 *  Team Member:
 *               Xinyi Yuan
 *               He Shen
 *               Yuchen Dong
 */

package src.matachi.mapeditor.editor.Checker.LevelCheck;

import src.matachi.mapeditor.grid.Grid;

import java.util.*;

import static src.utility.LogUtil.writeToLogFile;

public class GoldAndPillAccessibilityChecker implements LevelChecker {
    private Grid model;
    private String currentFileName;

    public GoldAndPillAccessibilityChecker(Grid model, String currentFileName) {
        this.model = model;
        this.currentFileName = currentFileName;
    }

    @Override
    public boolean check() {
        // get the positions of gold & pill
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

        // check the accessibility of gold
        for (int position : goldPositions) {
            if (!isAccessible(position)) {
                inaccessibleGoldPositions.add(position);
            }
        }

        // check the accessibility of pill
        for (int position : pillPositions) {
            if (!isAccessible(position)) {
                inaccessiblePillPositions.add(position);
            }
        }

        // if no inaccessible gold & pill
        if(inaccessibleGoldPositions.isEmpty() && inaccessiblePillPositions.isEmpty()) {
            return true;
        }

        // if exist inaccessible gold, log out
        if (!inaccessibleGoldPositions.isEmpty()) {
            List<String> goldPositionsList = new ArrayList<>();
            for (int position : inaccessibleGoldPositions) {
                int row = position / model.getWidth();
                int col = position % model.getWidth();
                String positionString = "(" + (col + 1) + ", " + (row + 1) + ")";
                goldPositionsList.add(positionString);
            }

            String goldPositionsString = String.join("; ", goldPositionsList);
            String message = String.format("[Level %s - Gold not accessible: %s]", currentFileName, goldPositionsString);
            writeToLogFile(message,currentFileName);
        }

        // if exist inaccessible pill, log out
        if (!inaccessiblePillPositions.isEmpty()) {
            List<String> pillPositionsList = new ArrayList<>();
            for (int position : inaccessiblePillPositions) {
                int row = position / model.getWidth();
                int col = position % model.getWidth();
                String positionString = "(" + (col + 1) + ", " + (row + 1) + ")";
                pillPositionsList.add(positionString);
            }

            String pillPositionsString = String.join("; ", pillPositionsList);
            String message = String.format("[Level %s - Pill not accessible: %s]", currentFileName, pillPositionsString);
            writeToLogFile(message,currentFileName);
        }
        return false;
    }

    private boolean isAccessible(int startPosition) {
        boolean[] visited = new boolean[model.getWidth() * model.getHeight()];

        Queue<Integer> queue = new ArrayDeque<>();
        queue.offer(startPosition);
        visited[startPosition] = true;

        while (!queue.isEmpty()) {
            int position = queue.poll();
            int row = position / model.getWidth();
            int col = position % model.getWidth();

            // check 4 direction
            if (row > 0) {
                isAccessibleTile(col, row - 1, visited, queue);
            }
            if (row < model.getHeight() - 1) {
                isAccessibleTile(col, row + 1, visited, queue);
            }
            if (col > 0) {
                isAccessibleTile(col - 1, row, visited, queue);
            }
            if (col < model.getWidth() - 1) {
                isAccessibleTile(col + 1, row, visited, queue);
            }
        }
        for (int i = 0; i < model.getWidth(); i++) {
            for (int j = 0; j < model.getHeight(); j++) {
                char tileChar = model.getTile(i, j);
                if (tileChar == 'f' && visited[j * model.getWidth() + i]) {
                    return true;
                }
            }
        }

        return false;
    }

    private void isAccessibleTile(int col, int row, boolean[] visited, Queue<Integer> queue) {
        int position = row * model.getWidth() + col;
        if (!visited[position]) {
            char tileChar = model.getTile(col, row);
            if (tileChar == 'a' || tileChar == 'c' || tileChar == 'd' || tileChar == 'e' || tileChar == 'f') {
                visited[position] = true;
                queue.offer(position);
            } else if (isPortal(tileChar)) {
                // Find the pair of Portal
                int pairPosition = getPortalPairPosition(tileChar, position);
                if (pairPosition != -1) {
                    visited[position] = true;
                    queue.offer(position);
                    if (!visited[pairPosition]) {
                        queue.offer(pairPosition);
                        visited[pairPosition] = true;
                    }
                }
            }
        }
    }

    private boolean isPortal(char tileChar) {
        return tileChar == 'i' || tileChar == 'j' || tileChar == 'k' || tileChar == 'l';
    }

    private int getPortalPairPosition(char portalChar, int currentPos) {
        List<Integer> portalPositions = new ArrayList<>();
        for (int row = 0; row < model.getHeight(); row++) {
            for (int col = 0; col < model.getWidth(); col++) {
                char tileChar = model.getTile(col, row);
                if (tileChar == portalChar) {
                    portalPositions.add(row * model.getWidth() + col);
                }
            }
        }
        // if no pair of portal exist
        if (portalPositions.size() != 2) {
            return -1;
        }
        // return the pair of portal
        return portalPositions.get(0) == currentPos ? portalPositions.get(1) : portalPositions.get(0);
    }

}
