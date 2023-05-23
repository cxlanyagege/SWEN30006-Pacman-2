package src.matachi.mapeditor.editor;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;
import java.util.ArrayList;
import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;


import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import src.Driver;
import src.matachi.mapeditor.grid.Camera;
import src.matachi.mapeditor.grid.Grid;
import src.matachi.mapeditor.grid.GridCamera;
import src.matachi.mapeditor.grid.GridModel;
import src.matachi.mapeditor.grid.GridView;

import org.jdom.Attribute;
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
 * 
 */
public class Controller implements ActionListener, GUIInformation {

	/**
	 * The model of the map editor.
	 */
	private Grid model;

	private Tile selectedTile;
	private Camera camera;

	private List<Tile> tiles;

	private GridView grid;
	private View view;

	private int gridWith = Constants.MAP_WIDTH;
	private int gridHeight = Constants.MAP_HEIGHT;
	private String currentFileName;
	private String fileDirectory;

	/**
	 * Construct the controller.
	 */
	public Controller() {
		init(Constants.MAP_WIDTH, Constants.MAP_HEIGHT);

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
			// TODO: Code to switch to pacman game
			new Thread(() -> {
				String args[] = new String[0];
				Driver.main(args);
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
						char tileChar = model.getTile(x,y);
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
			File selectedFile;
			BufferedReader in;
			FileReader reader = null;
			File workingDirectory = new File(System.getProperty("user.dir"));
			chooser.setCurrentDirectory(workingDirectory);

			int returnVal = chooser.showOpenDialog(null);
			Document document;
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				selectedFile = chooser.getSelectedFile();
				if (selectedFile.canRead() && selectedFile.exists()) {
					currentFileName = selectedFile.getName();
					fileDirectory = selectedFile.getParent();
					document = (Document) builder.build(selectedFile);

					Element rootNode = document.getRootElement();

					List sizeList = rootNode.getChildren("size");
					Element sizeElem = (Element) sizeList.get(0);
					int height = Integer.parseInt(sizeElem
							.getChildText("height"));
					int width = Integer
							.parseInt(sizeElem.getChildText("width"));
					updateGrid(width, height);

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

					String mapString = model.getMapAsString();
					System.out.println(mapString);
					grid.redrawGrid();

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
