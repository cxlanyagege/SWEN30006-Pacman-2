// PacMan.java
// Simple PacMan implementation
package src.pacmanGame;

import ch.aplu.jgamegrid.*;

import src.utility.GameCallback;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class Game extends GameGrid
{
  private final static int nbHorzCells = 20;
  private final static int nbVertCells = 11;
  protected PacManGameGrid grid = new PacManGameGrid(nbHorzCells, nbVertCells);

  protected PacActor pacActor = new PacActor(this);

  //TODO: some level may have mulitple same monsters
  private Monster troll = new Monster(this, MonsterType.Troll);
  private Monster tx5 = new Monster(this, MonsterType.TX5);


  private ArrayList<Location> pillAndItemLocations = new ArrayList<Location>();
  private ArrayList<Actor> iceCubes = new ArrayList<Actor>();
  private Map<Color, ArrayList<Portal>> portals = new HashMap<Color, ArrayList<Portal>>();
  private Map<Actor, Portal> actorToLastPortalMap = new HashMap<Actor, Portal>();

  private ArrayList<Actor> goldPieces = new ArrayList<Actor>();
  private GameCallback gameCallback;
  private Properties properties;
  private int seed = 30006;
  private ArrayList<Location> propertyPillLocations = new ArrayList<>();
  private ArrayList<Location> propertyGoldLocations = new ArrayList<>();


  protected NewGameGrid newGrid;

  public Game(GameCallback gameCallback, Properties properties,String mapString) {

    //Setup game
    super(nbHorzCells, nbVertCells, 20, false);
    this.gameCallback = gameCallback;
    this.properties = properties;

    newGrid = new NewGameGrid(mapString);

    setSimulationPeriod(100);
    setTitle("[PacMan in the Multiverse]");

    //Setup for auto test
    pacActor.setPropertyMoves(properties.getProperty("PacMan.move"));
    pacActor.setAuto(Boolean.parseBoolean(properties.getProperty("PacMan.isAuto")));

    //loadMap();
    GGBackground bg = getBg();
    drawGridFromMap(bg);

    // pacman keyPad listener
    addKeyRepeatListener(pacActor);
    setKeyRepeatPeriod(150);

    //Setup Random seeds
    seed = Integer.parseInt(properties.getProperty("seed"));
    pacActor.setSeed(seed);
    troll.setSeed(seed);
    tx5.setSeed(seed);


    // set slow down
    troll.setSlowDown(3);
    tx5.setSlowDown(3);
    pacActor.setSlowDown(3);
    tx5.stopMoving(5);




    //Run the game
    doRun();
    show();

    // Loop to look for collision in the application thread
    // This makes it improbable that we miss a hit
    boolean hasPacmanBeenHit;
    boolean hasPacmanEatAllPills;
    setupPillAndItemsLocations();
    int maxPillsAndItems = countPillsAndItems();

    do {
      hasPacmanBeenHit = troll.getLocation().equals(pacActor.getLocation()) ||
              tx5.getLocation().equals(pacActor.getLocation());
      hasPacmanEatAllPills = pacActor.getNbPills() >= maxPillsAndItems;

      try {
        checkAndHandlePortalCollision(pacActor);
        checkAndHandlePortalCollision(troll);
        checkAndHandlePortalCollision(tx5);
        Thread.sleep(100);
      } catch (InterruptedException e) {

        e.printStackTrace();
      }


      delay(10);
    } while(!hasPacmanBeenHit && !hasPacmanEatAllPills);
    delay(120);



    Location loc = pacActor.getLocation();
    troll.setStopMoving(true);
    tx5.setStopMoving(true);
    pacActor.removeSelf();

    String title = "";
    if (hasPacmanBeenHit) {
      bg.setPaintColor(Color.red);
      title = "GAME OVER";
      addActor(new Actor("sprites/explosion3.gif"), loc);
    } else if (hasPacmanEatAllPills) {
      bg.setPaintColor(Color.yellow);
      title = "YOU WIN";
    }
    setTitle(title);
    gameCallback.endOfGame(title);

    doPause();

  }



  public Game(GameCallback gameCallback, Properties properties)
  {
    //Setup game
    super(nbHorzCells, nbVertCells, 20, false);
    this.gameCallback = gameCallback;
    this.properties = properties;
    setSimulationPeriod(100);
    setTitle("[PacMan in the Multiverse]");

    //Setup for auto test
    pacActor.setPropertyMoves(properties.getProperty("PacMan.move"));
    pacActor.setAuto(Boolean.parseBoolean(properties.getProperty("PacMan.isAuto")));

    loadPillAndItemsLocations();
    GGBackground bg = getBg();
    drawGrid(bg);



    //Setup Random seeds
    seed = Integer.parseInt(properties.getProperty("seed"));
    pacActor.setSeed(seed);
    troll.setSeed(seed);
    tx5.setSeed(seed);
    addKeyRepeatListener(pacActor);
    setKeyRepeatPeriod(150);
    troll.setSlowDown(3);
    tx5.setSlowDown(3);
    pacActor.setSlowDown(3);
    tx5.stopMoving(5);
    setupActorLocations();



    //Run the game
    doRun();
    show();
    // Loop to look for collision in the application thread
    // This makes it improbable that we miss a hit
    boolean hasPacmanBeenHit;
    boolean hasPacmanEatAllPills;
    setupPillAndItemsLocations();
    int maxPillsAndItems = countPillsAndItems();
    
    do {
      hasPacmanBeenHit = troll.getLocation().equals(pacActor.getLocation()) ||
              tx5.getLocation().equals(pacActor.getLocation());
      hasPacmanEatAllPills = pacActor.getNbPills() >= maxPillsAndItems;
      delay(10);
    } while(!hasPacmanBeenHit && !hasPacmanEatAllPills);
    delay(120);

    Location loc = pacActor.getLocation();
    troll.setStopMoving(true);
    tx5.setStopMoving(true);
    pacActor.removeSelf();

    String title = "";
    if (hasPacmanBeenHit) {
      bg.setPaintColor(Color.red);
      title = "GAME OVER";
      addActor(new Actor("sprites/explosion3.gif"), loc);
    } else if (hasPacmanEatAllPills) {
      bg.setPaintColor(Color.yellow);
      title = "YOU WIN";
    }
    setTitle(title);
    gameCallback.endOfGame(title);

    doPause();
  }

  public GameCallback getGameCallback() {
    return gameCallback;
  }

  private void setupActorLocations() {
    String[] trollLocations = this.properties.getProperty("Troll.location").split(",");
    String[] tx5Locations = this.properties.getProperty("TX5.location").split(",");
    String[] pacManLocations = this.properties.getProperty("PacMan.location").split(",");
    int trollX = Integer.parseInt(trollLocations[0]);
    int trollY = Integer.parseInt(trollLocations[1]);

    int tx5X = Integer.parseInt(tx5Locations[0]);
    int tx5Y = Integer.parseInt(tx5Locations[1]);

    int pacManX = Integer.parseInt(pacManLocations[0]);
    int pacManY = Integer.parseInt(pacManLocations[1]);

    addActor(troll, new Location(trollX, trollY), Location.NORTH);
    addActor(pacActor, new Location(pacManX, pacManY));
    addActor(tx5, new Location(tx5X, tx5Y), Location.NORTH);
  }




  private int countPillsAndItems() {
    int pillsAndItemsCount = 0;
    for (int y = 0; y < nbVertCells; y++)
    {
      for (int x = 0; x < nbHorzCells; x++)
      {
        Location location = new Location(x, y);
        int a = grid.getCell(location);
        if (a == 1 && propertyPillLocations.size() == 0) { // Pill
          pillsAndItemsCount++;
        } else if (a == 3 && propertyGoldLocations.size() == 0) { // Gold
          pillsAndItemsCount++;
        }
      }
    }
    if (propertyPillLocations.size() != 0) {
      pillsAndItemsCount += propertyPillLocations.size();
    }

    if (propertyGoldLocations.size() != 0) {
      pillsAndItemsCount += propertyGoldLocations.size();
    }

    return pillsAndItemsCount;
  }

  public ArrayList<Location> getPillAndItemLocations() {
    return pillAndItemLocations;
  }


  private void loadPillAndItemsLocations() {
    String pillsLocationString = properties.getProperty("Pills.location");
    if (pillsLocationString != null) {
      String[] singlePillLocationStrings = pillsLocationString.split(";");
      for (String singlePillLocationString: singlePillLocationStrings) {
        String[] locationStrings = singlePillLocationString.split(",");
        propertyPillLocations.add(new Location(Integer.parseInt(locationStrings[0]), Integer.parseInt(locationStrings[1])));
      }
    }

    String goldLocationString = properties.getProperty("Gold.location");
    if (goldLocationString != null) {
      String[] singleGoldLocationStrings = goldLocationString.split(";");
      for (String singleGoldLocationString: singleGoldLocationStrings) {
        String[] locationStrings = singleGoldLocationString.split(",");
        propertyGoldLocations.add(new Location(Integer.parseInt(locationStrings[0]), Integer.parseInt(locationStrings[1])));
      }
    }
  }
  private void setupPillAndItemsLocations() {
    for (int y = 0; y < nbVertCells; y++)
    {
      for (int x = 0; x < nbHorzCells; x++)
      {
        Location location = new Location(x, y);
        int a = grid.getCell(location);
        if (a == 1 && propertyPillLocations.size() == 0) {
          pillAndItemLocations.add(location);
        }
        if (a == 3 &&  propertyGoldLocations.size() == 0) {
          pillAndItemLocations.add(location);
        }
        if (a == 4) {
          pillAndItemLocations.add(location);
        }
      }
    }


    if (propertyPillLocations.size() > 0) {
      for (Location location : propertyPillLocations) {
        pillAndItemLocations.add(location);
      }
    }
    if (propertyGoldLocations.size() > 0) {
      for (Location location : propertyGoldLocations) {
        pillAndItemLocations.add(location);
      }
    }
  }

  private void drawGrid(GGBackground bg)
  {
    bg.clear(Color.gray);
    bg.setPaintColor(Color.white);
    for (int y = 0; y < nbVertCells; y++)
    {
      for (int x = 0; x < nbHorzCells; x++)
      {
        bg.setPaintColor(Color.white);
        Location location = new Location(x, y);
        int a = grid.getCell(location);
        if (a > 0)
          bg.fillCell(location, Color.lightGray);
        if (a == 1 && propertyPillLocations.size() == 0) { // Pill
          putPill(bg, location);
        } else if (a == 3 && propertyGoldLocations.size() == 0) { // Gold
          putGold(bg, location);
        } else if (a == 4) {
          putIce(bg, location);
        }
      }
    }

    for (Location location : propertyPillLocations) {
      putPill(bg, location);
    }

    for (Location location : propertyGoldLocations) {
      putGold(bg, location);
    }
  }

  protected void putPill(GGBackground bg, Location location){
    bg.fillCircle(toPoint(location), 5);
  }

  protected void putGold(GGBackground bg, Location location){
    bg.setPaintColor(Color.yellow);
    bg.fillCircle(toPoint(location), 5);
    Actor gold = new Actor("sprites/gold.png");
    this.goldPieces.add(gold);
    addActor(gold, location);
  }

  protected void putIce(GGBackground bg, Location location){
    bg.setPaintColor(Color.blue);
    bg.fillCircle(toPoint(location), 5);
    Actor ice = new Actor("sprites/ice.png");
    this.iceCubes.add(ice);
    addActor(ice, location);
  }

  protected void putPortal(GGBackground bg, Location location,Color color){
    bg.setPaintColor(Color.red);
    bg.fillCircle(toPoint(location), 5);


    if(color.equals(Color.white)){
      Portal portal = new Portal(color,location, "data/i_portalWhiteTile.png");
      addPortal(portal);
      addActor(portal, location);
    } else if (color.equals(Color.yellow)) {
      Portal portal = new Portal(color,location, "data/j_portalYellowTile.png");
      addPortal(portal);
      addActor(portal, location);
    } else if (color.equals(Color.orange)) {
      Portal portal = new Portal(color,location, "data/k_portalDarkGoldTile.png");
      addPortal(portal);
      addActor(portal, location);
    } else if (color.equals(Color.darkGray)) {
      Portal portal = new Portal(color,location, "data/l_portalDarkGrayTile.png");
      addPortal(portal);
      addActor(portal, location);
    }
  }




  public void addPortal(Portal portal) {
    Color color = portal.getColor();

    // If the list of portals for this color already exists, add the portal to the list
    // Otherwise, create a new list and add it to the HashMap
    ArrayList<Portal> portalList = portals.get(color);
    if (portalList == null) {
      portalList = new ArrayList<>();
      portals.put(color, portalList);
    }

    portalList.add(portal);
  }





  public void checkAndHandlePortalCollision(Actor actor) {
    // Iterate through the list of portals for each color
    for (ArrayList<Portal> portalPair : portals.values()) {
      // Check if the length of the list is 2
      if (portalPair.size() != 2) {
        continue;
      } else {
        // TODO: add error log
      }

      Portal portal1 = portalPair.get(0);
      Portal portal2 = portalPair.get(1);

      // If the actor is at one portal and is not currently inside any portal, move them to the other portal
      if (actor.getLocation().equals(portal1.getLocation()) &&
              actorToLastPortalMap.get(actor) == null) {
        actor.setLocation(portal2.getLocation());
        actorToLastPortalMap.put(actor, portal1); // actor is now inside portal1
      } else if (actor.getLocation().equals(portal2.getLocation()) &&
              actorToLastPortalMap.get(actor) == null) {
        actor.setLocation(portal1.getLocation());
        actorToLastPortalMap.put(actor, portal2); // actor is now inside portal2
      } else if (actorToLastPortalMap.get(actor) != null &&
              !actor.getLocation().equals(portal1.getLocation()) &&
              !actor.getLocation().equals(portal2.getLocation()) &&
              !actor.getBackground().getColor(actor.getLocation()).equals(Color.red)
              ) {
        // actor is not at any portal but lastPortalMap says they are inside a portal,
        // this means they have moved away from the portal, so we can reset the portal map entry
        actorToLastPortalMap.put(actor, null);
      }
    }
  }









  public void removeItem(String type,Location location){
    if(type.equals("gold")){
      for (Actor item : this.goldPieces){
        if (location.getX() == item.getLocation().getX() && location.getY() == item.getLocation().getY()) {
          item.hide();
        }
      }
    }else if(type.equals("ice")){
      for (Actor item : this.iceCubes){
        if (location.getX() == item.getLocation().getX() && location.getY() == item.getLocation().getY()) {
          item.hide();
        }
      }
    }
  }


  public void drawGridFromMap(GGBackground bg){


    bg.setPaintColor(Color.white);

    for (int y = 0; y < nbVertCells; y++)
    {
      for (int x = 0; x < nbHorzCells; x++)
      {
        bg.setPaintColor(Color.white);
        Location location = new Location(x, y);
        char a = newGrid.getCellChar(location);


        if (a == 'b'){
          bg.fillCell(location, Color.gray);
        }else{
          bg.fillCell(location, Color.lightGray);
        }




        if (a == 'a' ) { // pathTile


        } else if (a == 'c' ) { // pill
          putPill(bg, location);


        } else if (a == 'd') {//gold

          putGold(bg, location);


        }
        else if (a == 'e') {//ice
          putPill(bg, location);


        }
        else if (a == 'f') {//pacman
          addActor(pacActor, location);

        }
        else if (a == 'g') {//troll

          addActor(troll, location);

        }
        else if (a == 'h') {//tx5

          addActor(tx5, location);

        }
        else if (a == 'i') {//portal white
          putPortal(bg, location,Color.white);

        }
        else if (a == 'j') {//portal yellow
          putPortal(bg, location,Color.yellow);

        }
        else if (a == 'k') {//portal dark gold
          putPortal(bg, location,Color.orange);
        }
        else if (a == 'l') {//portal dark gray
          putPortal(bg, location,Color.darkGray);


        }

      }
    }
  }


  public int getNumHorzCells(){
    return this.nbHorzCells;
  }
  public int getNumVertCells(){
    return this.nbVertCells;
  }
}
