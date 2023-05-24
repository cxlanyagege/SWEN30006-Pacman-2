package src.matachi.mapeditor.editor;

import java.io.*;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import src.matachi.mapeditor.grid.Grid;


public class FileComponent {
    private String filePath;

    public FileComponent(String filePath) {
        this.filePath = filePath;
    }

    public void save(Grid model) {
        Element level = new Element("level");
        Document doc = new Document(level);
        doc.setRootElement(level);

        Element size = new Element("size");
        int height = model.getHeight();
        int width = model.getWidth();
        size.addContent(new Element("width").setText(String.valueOf(width)));
        size.addContent(new Element("height").setText(String.valueOf(height)));
        doc.getRootElement().addContent(size);

        for (int y = 0; y < height; y++) {
            Element row = new Element("row");
            for (int x = 0; x < width; x++) {
                char tileChar = model.getTile(x, y);
                String type = "PathTile";

                if (tileChar == 'b')
                    type = "WallTile";
                else if (tileChar == 'c')
                    type = "PillTile";
                else if (tileChar == 'd')
                    type = "GoldTile";
                else if (tileChar == 'e')
                    type = "IceTile";
                else if (tileChar == 'f')
                    type = "PacTile";
                else if (tileChar == 'g')
                    type = "TrollTile";
                else if (tileChar == 'h')
                    type = "TX5Tile";
                else if (tileChar == 'i')
                    type = "PortalWhiteTile";
                else if (tileChar == 'j')
                    type = "PortalYellowTile";
                else if (tileChar == 'k')
                    type = "PortalDarkGoldTile";
                else if (tileChar == 'l')
                    type = "PortalDarkGrayTile";

                Element e = new Element("cell");
                row.addContent(e.setText(type));
            }
            doc.getRootElement().addContent(row);
        }

        XMLOutputter xmlOutput = new XMLOutputter();
        xmlOutput.setFormat(Format.getPrettyFormat());
        try {
            xmlOutput.output(doc, new FileWriter(filePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void load(File selectedFile, Grid model) {
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

                    model.setTile(x, y, tileNr);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
