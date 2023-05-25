package src.matachi.mapeditor.editor;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;


import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import src.Driver;
import src.facade.Facade;
import src.matachi.mapeditor.grid.Camera;
import src.matachi.mapeditor.grid.Grid;
import src.matachi.mapeditor.grid.GridCamera;
import src.matachi.mapeditor.grid.GridModel;
import src.matachi.mapeditor.grid.GridView;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

/**
 * Controller of the application.
 *
 * @author Daniel "MaTachi" Jonsson
 * @version 1
 * @since v0.0.5
 */
public class Controller implements ActionListener, GUIInformation {

    /**
     * The model of the map editor.
     */
    private Grid model;

    private Tile selectedTile;
    public Camera camera;

    public List<Tile> tiles;

    public GridView grid;
    public View view;

    private int gridWith = Constants.MAP_WIDTH;
    private int gridHeight = Constants.MAP_HEIGHT;
    private String currentFileName;
    private String fileDirectory;

    private final Facade facade = Facade.getInstance();
    private LoadContext loadContext;
    private static Controller instance;

    /**
     * Construct the controller.
     */
    private Controller() {
        init(Constants.MAP_WIDTH, Constants.MAP_HEIGHT);

    }

    public String getCurrentFileName() {
        return currentFileName;
    }

    public Grid getModel() {
        return model;
    }

    public static synchronized Controller getInstance() {
        if (instance == null) {
            instance = new Controller();
        }
        return instance;
    }

    public void init(int width, int height) {
        this.tiles = TileManager.getTilesFromFolder("data/");
        this.model = new GridModel(width, height, tiles.get(0).getCharacter());
        this.camera = new GridCamera(model, Constants.GRID_WIDTH,
                Constants.GRID_HEIGHT);

        grid = new GridView(this, camera, tiles); // Every tile is
        // 30x30 pixels

        this.view = new View(this, camera, grid, tiles);
    }

    /**
     * Different commands that comes from the view.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        for (Tile t : tiles) {
            if (e.getActionCommand().equals(
                    Character.toString(t.getCharacter()))) {
                selectedTile = t;
                break;
            }
        }
        if (e.getActionCommand().equals("flipGrid")) {
            // view.flipGrid();
        } else if (e.getActionCommand().equals("save")) {
            saveFile();
        } else if (e.getActionCommand().equals("load")) {
            loadFile();
        } else if (e.getActionCommand().equals("update")) {
            updateGrid(gridWith, gridHeight);
        } else if (e.getActionCommand().equals("start_game")) {
            new Thread(() -> {
                facade.startGame();
            }).start();
        }
    }

    public void updateGrid(int width, int height) {
        view.close();
        init(width, height);
        view.setSize(width, height);
    }

    DocumentListener updateSizeFields = new DocumentListener() {

        public void changedUpdate(DocumentEvent e) {
            gridWith = view.getWidth();
            gridHeight = view.getHeight();
        }

        public void removeUpdate(DocumentEvent e) {
            gridWith = view.getWidth();
            gridHeight = view.getHeight();
        }

        public void insertUpdate(DocumentEvent e) {
            gridWith = view.getWidth();
            gridHeight = view.getHeight();
        }
    };

    private void saveFile() {

        JFileChooser chooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "xml files", "xml");
        chooser.setFileFilter(filter);
        File workingDirectory = new File(System.getProperty("user.dir"));
        chooser.setCurrentDirectory(workingDirectory);

        int returnVal = chooser.showSaveDialog(null);
        try {
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File selectedFile = chooser.getSelectedFile();
                currentFileName = selectedFile.getName();
                fileDirectory = selectedFile.getParent();
                //levelCheck();
                Checker checker = new Checker();
                checker.levelCheck();

                Element level = new Element("level");
                Document doc = new Document(level);
                doc.setRootElement(level);

                Element size = new Element("size");
                int height = model.getHeight();
                int width = model.getWidth();
                size.addContent(new Element("width").setText(width + ""));
                size.addContent(new Element("height").setText(height + ""));
                doc.getRootElement().addContent(size);

                for (int y = 0; y < height; y++) {
                    Element row = new Element("row");
                    for (int x = 0; x < width; x++) {
                        char tileChar = model.getTile(x, y);

                        String type = switch (tileChar) {
                            case 'b' -> "WallTile";
                            case 'c' -> "PillTile";
                            case 'd' -> "GoldTile";
                            case 'e' -> "IceTile";
                            case 'f' -> "PacTile";
                            case 'g' -> "TrollTile";
                            case 'h' -> "TX5Tile";
                            case 'i' -> "PortalWhiteTile";
                            case 'j' -> "PortalYellowTile";
                            case 'k' -> "PortalDarkGoldTile";
                            case 'l' -> "PortalDarkGrayTile";
                            default -> "PathTile";
                        };


                        Element e = new Element("cell");
                        row.addContent(e.setText(type));
                    }
                    doc.getRootElement().addContent(row);
                }
                XMLOutputter xmlOutput = new XMLOutputter();
                xmlOutput.setFormat(Format.getPrettyFormat());
                xmlOutput
                        .output(doc, new FileWriter(selectedFile));
            }
        } catch (FileNotFoundException e1) {
            JOptionPane.showMessageDialog(null, "Invalid file!", "error",
                    JOptionPane.ERROR_MESSAGE);
        } catch (IOException e) {
        }
    }

    public void loadFile() {
        SAXBuilder builder = new SAXBuilder();
        try {
            JFileChooser chooser = new JFileChooser();
            // enable chooser select both files and directories
            chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

            File selectedFile;
            BufferedReader in;
            FileReader reader = null;
            File workingDirectory = new File(System.getProperty("user.dir"));
            chooser.setCurrentDirectory(workingDirectory);

            int returnVal = chooser.showOpenDialog(null);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                selectedFile = chooser.getSelectedFile();
                if (selectedFile.exists() && selectedFile.isDirectory()) {
                    currentFileName = selectedFile.getName();
                    fileDirectory = selectedFile.getParent();
                    //Code to load folder
                    this.loadContext = new LoadContext();
                    LoadStrategy folderLoadStrategy = new FolderLoadStrategy();
                    this.loadContext.setStrategy(folderLoadStrategy);
                    loadContext.load(selectedFile);

                } else if (selectedFile.canRead() && selectedFile.exists()) {
                    currentFileName = selectedFile.getName();
                    fileDirectory = selectedFile.getParent();
                    //Code to load file
                    this.loadContext = new LoadContext();
                    LoadStrategy fileLoadStrategy = new FileLoadStrategy();
                    this.loadContext.setStrategy(fileLoadStrategy);
                    loadContext.load(selectedFile);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Tile getSelectedTile() {
        return selectedTile;
    }
}
