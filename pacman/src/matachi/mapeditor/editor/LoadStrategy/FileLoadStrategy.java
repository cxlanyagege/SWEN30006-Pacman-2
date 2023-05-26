package src.matachi.mapeditor.editor.LoadStrategy;

import src.facade.Facade;
import src.matachi.mapeditor.editor.Checker.Checker;
import src.matachi.mapeditor.editor.Controller;
import src.matachi.mapeditor.editor.MapStringParser;


import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileLoadStrategy implements LoadStrategy {
    private final Facade facade = Facade.getInstance();


    @Override
    public void load(File selectedFile) {

        try {

            String mapString = MapStringParser.parse(selectedFile);
            List<String> mapStrings = new ArrayList<>();
            mapStrings.add(mapString);

            Checker checker = new Checker();
            boolean checkResult =  checker.levelCheck();
            Controller.getInstance().grid.redrawGrid();

            if(checkResult){
                facade.passMapString(mapStrings);
                facade.mapLoaded();

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

