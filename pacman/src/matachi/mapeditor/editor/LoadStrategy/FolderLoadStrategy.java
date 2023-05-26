/*
 *  Team Name: monday-16-15-team-04
 *  Team Member:
 *               Xinyi Yuan
 *               He Shen
 *               Yuchen Dong
 */

package src.matachi.mapeditor.editor.LoadStrategy;

import src.facade.Facade;
import src.matachi.mapeditor.editor.Checker.Checker;
import src.matachi.mapeditor.editor.Controller;
import src.matachi.mapeditor.editor.MapStringParser;


import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FolderLoadStrategy implements LoadStrategy {
    private final Facade facade = Facade.getInstance();

    @Override
    public void load(File selectedFile) {

        try {

            Checker checker = new Checker();
            File[] validMapFiles = checker.gameCheck(selectedFile);

            List<String> mapStrings = new ArrayList<>();

            if(validMapFiles != null){
                int numOffFiles = validMapFiles.length;
                int numPassCheck = 0;

                for (File file : validMapFiles) {
                    if (file.isFile() && file.getName().endsWith(".xml")) {
                        // parse
                        String mapString = MapStringParser.parse(file);
                        Controller.getInstance().setCurrentFileName(file.getName());
                        checker = new Checker();
                        if (checker.levelCheck()) {
                            mapStrings.add(mapString);
                            numPassCheck++;
                        } else {
                            Controller.getInstance().grid.redrawGrid();
                            return;
                        }
                    }
                }

                if (numOffFiles != 0 && numOffFiles == numPassCheck) {

                    if (mapStrings.size()>0) {
                        facade.passMapString(mapStrings);
                        facade.mapLoaded();
                        new Thread(facade::startGame).start();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
