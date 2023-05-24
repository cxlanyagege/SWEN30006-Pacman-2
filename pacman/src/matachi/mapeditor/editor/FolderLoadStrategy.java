package src.matachi.mapeditor.editor;

import org.jdom.Document;
import org.jdom.input.SAXBuilder;
import src.facade.Facade;


import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FolderLoadStrategy implements LoadStrategy {
    private final Facade facade = Facade.getInstance();


    @Override
    public void load(File selectedFile) {
        SAXBuilder builder = new SAXBuilder();

        try {

            File[] files = selectedFile.listFiles();
            List<Document> documents = new ArrayList<>();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile() && file.getName().endsWith(".xml")) {
                        Document document = builder.build(file);
                        if (document != null) {
                            documents.add(document);
                        }
                    }
                }
            }
            List<String> mapStrings = new ArrayList<>();

            for (Document document : documents) {
                String mapString = MapStringParser.parse(document);
                mapStrings.add(mapString);
            }
            facade.passMapString(mapStrings);
            facade.mapLoaded();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

