package src.matachi.mapeditor.editor;

import org.jdom.Document;
import org.jdom.Element;
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
            Element rootNode = document.getRootElement();

            List sizeList = rootNode.getChildren("size");
            Element sizeElem = (Element) sizeList.get(0);

            List rows = rootNode.getChildren("row");
            for (int y = 0; y < rows.size(); y++) {
                Element cellsElem = (Element) rows.get(y);
                List cells = cellsElem.getChildren("cell");

                for (int x = 0; x < cells.size(); x++) {
                    Element cell = (Element) cells.get(x);
                    String cellValue = cell.getText();

                    char tileNr = switch (cellValue) {
                        case "PathTile" -> 'a';
                        case "WallTile" -> 'b';
                        case "PillTile" -> 'c';
                        case "GoldTile" -> 'd';
                        case "IceTile" -> 'e';
                        case "PacTile" -> 'f';
                        case "TrollTile" -> 'g';
                        case "TX5Tile" -> 'h';
                        case "PortalWhiteTile" -> 'i';
                        case "PortalYellowTile" -> 'j';
                        case "PortalDarkGoldTile" -> 'k';
                        case "PortalDarkGrayTile" -> 'l';
                        default -> '0';
                    };

                    Controller.getInstance().model.setTile(x, y, tileNr);
                }
            }
            String mapString = Controller.getInstance().model.getMapAsString();
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

