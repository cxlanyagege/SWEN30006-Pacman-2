package src.matachi.mapeditor.editor;

import org.jdom.Document;
import org.jdom.input.SAXBuilder;
import src.facade.Facade;
import src.matachi.mapeditor.editor.Checker.Checker;


import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileLoadStrategy implements LoadStrategy {
    private final Facade facade = Facade.getInstance();


    @Override
    public void load(File selectedFile) {
        SAXBuilder builder = new SAXBuilder();

        try {
            Document document = builder.build(selectedFile);
            String mapString = MapStringParser.parse(document);
            List<String> mapStrings = new ArrayList<>();
            mapStrings.add(mapString);

            Checker checker = new Checker();
            boolean checkResult =  checker.levelCheck();
            System.out.println("Check result: " + checkResult);
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

