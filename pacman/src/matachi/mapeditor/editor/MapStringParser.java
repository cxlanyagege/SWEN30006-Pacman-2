/*
 *  Team Name: monday-16-15-team-04
 *  Team Member:
 *               Xinyi Yuan
 *               He Shen
 *               Yuchen Dong
 */

package src.matachi.mapeditor.editor;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import java.io.File;
import java.util.List;

public class MapStringParser {
    public static String parse(File currentFile) {

        SAXBuilder builder = new SAXBuilder();
        try{
            Document document = builder.build(currentFile);
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

                    Controller.getInstance().getModel().setTile(x, y, tileNr);
                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }

        return Controller.getInstance().getModel().getMapAsString();
    }
}

