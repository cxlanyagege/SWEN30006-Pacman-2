package src.matachi.mapeditor.editor;

import org.jdom.Document;
import org.jdom.input.SAXBuilder;
import src.facade.Facade;


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
            facade.passMapString(mapStrings);
            facade.mapLoaded();
            Controller.getInstance().grid.redrawGrid();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

