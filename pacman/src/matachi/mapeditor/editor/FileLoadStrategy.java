package src.matachi.mapeditor.editor;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import src.matachi.mapeditor.grid.Grid;
import javax.swing.JFileChooser;


import java.io.File;
import java.util.List;

public class FileLoadStrategy implements LoadStrategy {


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

                    char tileNr = 'a';
                    if (cellValue.equals("PathTile"))
                        tileNr = 'a';
                    else if (cellValue.equals("WallTile"))
                        tileNr = 'b';
                    else if (cellValue.equals("PillTile"))
                        tileNr = 'c';
                    else if (cellValue.equals("GoldTile"))
                        tileNr = 'd';
                    else if (cellValue.equals("IceTile"))
                        tileNr = 'e';
                    else if (cellValue.equals("PacTile"))
                        tileNr = 'f';
                    else if (cellValue.equals("TrollTile"))
                        tileNr = 'g';
                    else if (cellValue.equals("TX5Tile"))
                        tileNr = 'h';
                    else if (cellValue.equals("PortalWhiteTile"))
                        tileNr = 'i';
                    else if (cellValue.equals("PortalYellowTile"))
                        tileNr = 'j';
                    else if (cellValue.equals("PortalDarkGoldTile"))
                        tileNr = 'k';
                    else if (cellValue.equals("PortalDarkGrayTile"))
                        tileNr = 'l';
                    else
                        tileNr = '0';

                    Controller.getInstance().model.setTile(x, y, tileNr);
                }
            }



        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

