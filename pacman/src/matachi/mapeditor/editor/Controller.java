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
    public Grid model;

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
                levelCheck();

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
                    System.out.println("Directory selected");
                    currentFileName = selectedFile.getName();
                    fileDirectory = selectedFile.getParent();
                    //Code to load file
                    this.loadContext = new LoadContext();
                    LoadStrategy folderLoadStrategy = new FolderLoadStrategy();
                    this.loadContext.setStrategy(folderLoadStrategy);

                    loadContext.load(selectedFile);
                    //TODO: Game Check

                } else if (selectedFile.canRead() && selectedFile.exists()) {
                    currentFileName = selectedFile.getName();
                    fileDirectory = selectedFile.getParent();
                    //Code to load file
                    this.loadContext = new LoadContext();
                    LoadStrategy fileLoadStrategy = new FileLoadStrategy();
                    this.loadContext.setStrategy(fileLoadStrategy);

                    loadContext.load(selectedFile);
                    levelCheck();
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

    // Level Checking

    /**
     * check the validity of a level
     */
    private void levelCheck() {
        checkPacManStart();
        checkPortalCount();
        checkGoldAndPillCount();
        checkGoldAndPillAccessibility();
    }

    private void checkPacManStart() {
        int pacManStartCount = 0;
        List<String> pacManStartCoordinates = new ArrayList<>();

        for (int row = 0; row < model.getHeight(); row++) {
            for (int col = 0; col < model.getWidth(); col++) {
                char tileChar = model.getTile(col, row);
                if (tileChar == 'f') {
                    pacManStartCount++;
                    pacManStartCoordinates.add("(" + col + ", " + row + ")");
                }
            }
        }

        if (pacManStartCount != 1) {
            String message = String.format("[Level %s – no start or more than one start for PacMan: %s]", currentFileName, String.join("; ", pacManStartCoordinates));
            writeToLogFile(message);
        }
    }

    private boolean checkPortalCount() {
        List<String> portalWhiteCoordinates = new ArrayList<>();
        List<String> portalYellowCoordinates = new ArrayList<>();
        List<String> portalDarkGoldCoordinates = new ArrayList<>();
        List<String> portalDarkGrayCoordinates = new ArrayList<>();

        for (int row = 0; row < model.getHeight(); row++) {
            for (int col = 0; col < model.getWidth(); col++) {
                char tileChar = model.getTile(col, row);
                if (tileChar == 'i') { // Portal White
                    portalWhiteCoordinates.add("(" + col + ", " + row + ")");
                } else if (tileChar == 'j') { // Portal Yellow
                    portalYellowCoordinates.add("(" + col + ", " + row + ")");
                } else if (tileChar == 'k') { // Portal Dark Gold
                    portalDarkGoldCoordinates.add("(" + col + ", " + row + ")");
                } else if (tileChar == 'l') { // Portal Dark Gray
                    portalDarkGrayCoordinates.add("(" + col + ", " + row + ")");
                }
            }
        }

        boolean validPortalCount = portalWhiteCoordinates.size() == 2 && portalYellowCoordinates.size() == 2 &&
                portalDarkGoldCoordinates.size() == 2 && portalDarkGrayCoordinates.size() == 2;

        if (!validPortalCount) {
            String message = String.format("[Level %s - portal count is not as expected: Portal White = %d %s, Portal Yellow = %d %s, Portal Dark Gold = %d %s, Portal Dark Gray = %d %s]",
                    currentFileName, portalWhiteCoordinates.size(), portalWhiteCoordinates,
                    portalYellowCoordinates.size(), portalYellowCoordinates,
                    portalDarkGoldCoordinates.size(), portalDarkGoldCoordinates,
                    portalDarkGrayCoordinates.size(), portalDarkGrayCoordinates);
            System.out.println(message);
            writeToLogFile(message);
        }

        return validPortalCount;
    }


    private boolean checkGoldAndPillCount() {

        int goldCount = 0;
        int pillCount = 0;
        for (int row = 0; row < model.getHeight(); row++) {
            for (int col = 0; col < model.getWidth(); col++) {
                char tileChar = model.getTile(col, row);
                if (tileChar == 'd') { // GoldTile
                    goldCount++;
                } else if (tileChar == 'c') { // PillTile
                    pillCount++;
                }
            }
        }

        boolean validCount = goldCount >= 2 && pillCount >= 2;

        if (!validCount) {
            String message = String.format("[Level %s – less than 2 Gold and Pill: Gold = %d, Pill = %d]",
                    currentFileName, goldCount, pillCount);
            System.out.println(message);
            writeToLogFile(message);
        }

        return validCount;
    }


    private boolean checkGoldAndPillAccessibility() {
        // 获取金币和药丸的位置
        Set<Integer> goldPositions = new HashSet<>();
        Set<Integer> pillPositions = new HashSet<>();

        for (int row = 0; row < model.getHeight(); row++) {
            for (int col = 0; col < model.getWidth(); col++) {
                char tileChar = model.getTile(col, row);
                if (tileChar == 'd') { // GoldTile
                    int position = row * model.getWidth() + col;
                    goldPositions.add(position);
                } else if (tileChar == 'c') { // PillTile
                    int position = row * model.getWidth() + col;
                    pillPositions.add(position);
                }
            }
        }

        // 检查金币的可访问性
        for (int position : goldPositions) {
            if (!isAccessible(position)) {
                String message = String.format("[Level %s - Gold at position (%d, %d) is not accessible]",
                        currentFileName, position % model.getWidth(), position / model.getWidth());
                System.out.println(message);
                writeToLogFile(message);
                return false;
            }
        }

        // 检查药丸的可访问性
        for (int position : pillPositions) {
            if (!isAccessible(position)) {
                String message = String.format("[Level %s - Pill at position (%d, %d) is not accessible]",
                        currentFileName, position % model.getWidth(), position / model.getWidth());
                System.out.println(message);
                writeToLogFile(message);
                return false;
            }
        }

        return true;
    }

    private boolean isAccessible(int startPosition) {
        boolean[] visited = new boolean[model.getWidth() * model.getHeight()];

        // 使用广度优先搜索算法检查可访问性
        Queue<Integer> queue = new ArrayDeque<>();
        queue.offer(startPosition);
        visited[startPosition] = true;

        while (!queue.isEmpty()) {
            int position = queue.poll();
            int row = position / model.getWidth();
            int col = position % model.getWidth();

            // 检查上方方向
            if (row > 0 && isAccessibleTile(col, row - 1, visited, queue)) {
                return true;
            }

            // 检查下方方向
            if (row < model.getHeight() - 1 && isAccessibleTile(col, row + 1, visited, queue)) {
                return true;
            }

            // 检查左方方向
            if (col > 0 && isAccessibleTile(col - 1, row, visited, queue)) {
                return true;
            }

            // 检查右方方向
            if (col < model.getWidth() - 1 && isAccessibleTile(col + 1, row, visited, queue)) {
                return true;
            }
        }

        return false;
    }

    private boolean isAccessibleTile(int col, int row, boolean[] visited, Queue<Integer> queue) {
        if (!visited[row * model.getWidth() + col]) {
            char tileChar = model.getTile(col, row);
            if (tileChar == 'a' || tileChar == 'c' || tileChar == 'd' || tileChar == 'e' || tileChar == 'f') {
                visited[row * model.getWidth() + col] = true;
                queue.offer(row * model.getWidth() + col);
            }
            if (tileChar == 'f') {
                return true; // 找到可访问的位置
            }
        }
        return false;
    }


    private void writeToLogFile(String message) {
        try {
            String logFileName = currentFileName + "_log.txt"; // log文件名为当前文件名加上后缀.log
            FileWriter writer = new FileWriter(new File(fileDirectory, logFileName), true);
            writer.write(message + "\n");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
