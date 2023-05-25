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

                    // TODO: Level check Folder, if level check false, update map in editor

                    if(gameCheck(selectedFile)){
                        loadContext.load(selectedFile);
                    }




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

    // Game Checking
    private boolean gameCheck(File gameFolder) {
        boolean flag = true;
        File[] mapFiles = gameFolder.listFiles((dir, name) -> name.endsWith(".xml"));

        // 检查是否至少有一个正确命名的地图文件
        if (mapFiles == null || mapFiles.length == 0) {
            String message = String.format("[Game %s – no maps found]", gameFolder.getName());
            System.out.println(message);
            writeToLogFile(message);
            flag = false;
        }

        // 检查地图文件序列是否良好定义
        if (!checkMapFileSequence(mapFiles)){
            flag = false;
        }

        return flag;
    }

    private boolean checkMapFileSequence(File[] mapFiles) {
        boolean flag = true;
        Map<String, List<String>> mapLevelFiles = new HashMap<>();

        // 将地图文件按等级分组
        for (File file : mapFiles) {
            String fileName = file.getName();
            String level = getLevelFromFileName(fileName);

            List<String> levelFiles = mapLevelFiles.getOrDefault(level, new ArrayList<>());
            levelFiles.add(fileName);
            mapLevelFiles.put(level, levelFiles);
        }

        // 检查每个等级的地图文件数量
        for (Map.Entry<String, List<String>> entry : mapLevelFiles.entrySet()) {
            String level = entry.getKey();
            List<String> levelFiles = entry.getValue();

            if (levelFiles.size() > 1) {
                String message = String.format("[Game %s – multiple maps at same level: %s]",
                        currentFileName, String.join("; ", levelFiles));
                System.out.println(message);
                writeToLogFile(message);
                flag = false;
            }
        }
        return flag;
    }

    private String getLevelFromFileName(String fileName) {
        StringBuilder levelBuilder = new StringBuilder();

        for (int i = 0; i < fileName.length(); i++) {
            char ch = fileName.charAt(i);
            if (Character.isDigit(ch)) {
                levelBuilder.append(ch);
            } else {
                break;
            }
        }

        return levelBuilder.toString();
    }


    // Level Checking

    /**
     * check the validity of a level
     */
    private boolean levelCheck() {
        boolean isPacManStartValid = checkPacManStart();
        boolean isPortalCountValid = checkPortalCount();
        boolean isGoldAndPillCountValid = checkGoldAndPillCount();
        boolean isGoldAndPillAccessible = checkGoldAndPillAccessibility();

        return isPacManStartValid && isPortalCountValid && isGoldAndPillCountValid && isGoldAndPillAccessible;
    }


    private boolean checkPacManStart() {
        boolean flag = true;

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
            flag = false;
        }

        return flag;
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

        boolean validPortalCount = (portalWhiteCoordinates.size() == 0 || portalWhiteCoordinates.size() == 2)
                && (portalYellowCoordinates.size() == 0 || portalYellowCoordinates.size() == 2)
                && (portalDarkGoldCoordinates.size() == 0 || portalDarkGoldCoordinates.size() == 2)
                && (portalDarkGrayCoordinates.size() == 0 || portalDarkGrayCoordinates.size() == 2);


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
        boolean flag = true;

        int goldCount = 0;
        int pillCount = 0;
        for (int row = 0; row < model.getHeight(); row++) {
            for (int col = 0; col < model.getWidth(); col++) {
                char tileChar = model.getTile(col, row);
                if (tileChar == 'd') {
                    goldCount++;
                } else if (tileChar == 'c') {
                    pillCount++;
                }
            }
        }

        if (goldCount < 2 || pillCount < 2) {
            String message = String.format("[Level %s - Insufficient number of Gold or Pill: Gold = %d, Pill = %d]",
                    currentFileName, goldCount, pillCount);
            writeToLogFile(message);
            flag = false;
        }

        return flag;
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

        List<Integer> inaccessibleGoldPositions = new ArrayList<>();
        List<Integer> inaccessiblePillPositions = new ArrayList<>();

        // 检查金币的可访问性
        for (int position : goldPositions) {
            if (!isAccessible(position)) {
                inaccessibleGoldPositions.add(position);
            }
        }

        // 检查药丸的可访问性
        for (int position : pillPositions) {
            if (!isAccessible(position)) {
                inaccessiblePillPositions.add(position);
            }
        }

        // 如果存在不可访问的金币位置
        if(inaccessibleGoldPositions.isEmpty() && inaccessiblePillPositions.isEmpty()) {
            return true;
        }

        if (!inaccessibleGoldPositions.isEmpty()) {
            StringBuilder positionsString = new StringBuilder();
            for (int position : inaccessibleGoldPositions) {
                int row = position / model.getWidth();
                int col = position % model.getWidth();
                positionsString.append("(").append(col).append(", ").append(row).append("); ");
            }

            String message = String.format("[Level %s - Gold not accessible: %s]",
                    currentFileName, positionsString.toString());
            System.out.println(message);
            writeToLogFile(message);
        }

        if (!inaccessiblePillPositions.isEmpty()) {// 如果存在不可访问的药丸位置
            StringBuilder positionsString = new StringBuilder();
            for (int position : inaccessiblePillPositions) {
                int row = position / model.getWidth();
                int col = position % model.getWidth();
                positionsString.append("(").append(col).append(", ").append(row).append("); ");
            }

            String message = String.format("[Level %s - Pill not accessible: %s]",
                    currentFileName, positionsString.toString());
            System.out.println(message);
            writeToLogFile(message);
        }

        return false;
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
        int position = row * model.getWidth() + col;
        if (!visited[position]) {
            char tileChar = model.getTile(col, row);
            if (tileChar == 'a' || tileChar == 'c' || tileChar == 'd' || tileChar == 'e' || tileChar == 'f' || isPortal(tileChar)) {
                visited[position] = true;
                queue.offer(position);

                if (isPortal(tileChar)) {
                    // 获取对应传送门的位置
                    int pairPosition = getPortalPairPosition(tileChar);
                    if (pairPosition != -1 && !visited[pairPosition]) {
                        System.out.println(queue);
                        queue.offer(pairPosition);
                        System.out.println(queue);
                        //visited[pairPosition] = true;
                    }
                }

                if (tileChar == 'f') {
                    return true; // 找到可访问的位置
                }
            }
        }
        return false;
    }

    private boolean isPortal(char tileChar) {
        return tileChar == 'i' || tileChar == 'j' || tileChar == 'k' || tileChar == 'l';
    }

    private int getPortalPairPosition(char portalChar) {
        boolean firstPortalFound = false;
        for (int row = 0; row < model.getHeight(); row++) {
            for (int col = 0; col < model.getWidth(); col++) {
                char tileChar = model.getTile(col, row);
                if (tileChar == portalChar) {
                    if (firstPortalFound) {
                        return row * model.getWidth() + col;
                    }
                    firstPortalFound = true;
                }
            }
        }
        return -1; // 返回 -1 表示未找到对应的传送门
    }



    private void writeToLogFile(String message) {
        try {
            String logDirectoryName = "logDocument"; // 日志文件夹名称
            File logDirectory = new File(System.getProperty("user.dir"), logDirectoryName);
            if (!logDirectory.exists()) {
                logDirectory.mkdir(); // 创建日志文件夹
            }

            String logFileName = currentFileName + "_log.txt"; // 日志文件名为当前文件名加上后缀.log
            File logFile = new File(logDirectory, logFileName);

            FileWriter writer = new FileWriter(logFile, true);
            writer.write(message + "\n");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
