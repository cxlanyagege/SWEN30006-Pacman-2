/*
 *  Team Name: monday-16-15-team-04
 *  Team Member:
 *               Xinyi Yuan
 *               He Shen
 *               Yuchen Dong
 */

// PacMan.java
// Simple PacMan implementation
package src.pacmanGame;

import ch.aplu.jgamegrid.*;

import src.pacmanGame.Item.Portal;
import src.pacmanGame.PacActor.Monster.Monster;
import src.pacmanGame.PacActor.Monster.MonsterFactory;
import src.pacmanGame.PacActor.Monster.MonsterType;
import src.pacmanGame.PacActor.PacMan.PacMan;
import src.utility.GameCallback;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.List;


public class TorusVerseGame extends GameGrid {
    private final static int nbHorzCells = 20;
    private final static int nbVertCells = 11;

    public PacMan pacMan;

    private final ArrayList<Monster> trolls = new ArrayList<>();
    private final  ArrayList<Monster> tx5s = new ArrayList<>();
    private final ArrayList<Location> pillAndItemLocations = new ArrayList<>();
    private final  ArrayList<Actor> iceCubes = new ArrayList<>();
    private final Map<Color, ArrayList<Portal>> portals = new HashMap<>();
    private final Map<Actor, Portal> actorToLastPortalMap = new HashMap<>();

    private final ArrayList<Actor> goldPieces = new ArrayList<>();
    private final GameCallback gameCallback;

    protected List<NewGameGrid> grids = new ArrayList<>();
    protected NewGameGrid currentGrid;
    private boolean pacManAdded = false;
    private final MonsterFactory monsterFactory = new MonsterFactory();


    public TorusVerseGame(GameCallback gameCallback, Properties properties, List<String> mapStrings) {
        // Setup game
        super(nbHorzCells, nbVertCells, 20, false);
        this.gameCallback = gameCallback;
        pacMan = PacMan.getPacManInstance(this);

        int currentMapIndex = 0;

        // Create NewGameGrid objects for each mapString
        for (String mapString : mapStrings) {
            currentGrid = new NewGameGrid(mapString);
            grids.add(currentGrid);
        }

        currentGrid = grids.get(currentMapIndex);

        setSimulationPeriod(100);
        setTitle("[PacMan in the TorusVerse]");

        // Setup for auto test
        pacMan.setAuto(Boolean.parseBoolean(properties.getProperty("PacMan.isAuto")));

        //load map from mapStrings
        GGBackground bg = getBg();
        drawGridFromMap(bg);

        // pacman keyPad listener
        addKeyRepeatListener(pacMan);
        setKeyRepeatPeriod(150);

        // setup Random seeds
        int seed = Integer.parseInt(properties.getProperty("seed"));

        // slow down
        pacMan.setSlowDown(3);
        for (Monster troll : trolls) {
            troll.setSeed(seed);
            troll.setSlowDown(3);
        }
        for (Monster tx5 : tx5s) {
            tx5.setSeed(seed);
            tx5.setSlowDown(3);
            tx5.stopMoving(5);
        }

        // Run the game
        doRun();
        show();

        // Loop to look for collision in the application thread
        // This makes it improbable that we miss a hit
        boolean hasPacmanBeenHit = false;
        boolean hasPacmanEatAllPills;
        setupItemsLocationsFromMap();
        int maxPillsAndItems = countItemsFromMap();

        boolean hasCompletedAllMaps = false;

        do {

            hasPacmanEatAllPills = pacMan.getNbPills() >= maxPillsAndItems;

            // If completed one map begin the next map

            if (hasPacmanEatAllPills) {



                trolls.clear();
                tx5s.clear();
                portals.clear();
                goldPieces.clear();
                iceCubes.clear();
                pillAndItemLocations.clear();



                if (currentMapIndex < mapStrings.size() - 1) {
                    bg.clear();
                    for (Actor actor : getActors()) {
                        if (actor != pacMan) {
                            actor.removeSelf();
                        }
                    }
                    refresh();

                    currentMapIndex++;
                    currentGrid = grids.get(currentMapIndex);


                    drawGridFromMap(bg);

                    //set Actor properties for new map
                    pacMan.setSlowDown(3);
                    for (Monster troll : trolls) {
                        troll.setSeed(seed);
                        troll.setSlowDown(3);
                    }
                    for (Monster tx5 : tx5s) {
                        tx5.setSeed(seed);
                        tx5.setSlowDown(3);
                        tx5.stopMoving(5);
                    }


                    pacMan.setNbPills(0);
                    setupItemsLocationsFromMap();
                    maxPillsAndItems = countItemsFromMap();


                } else {
                    hasCompletedAllMaps = true;

                }
            }

            for (Monster troll : trolls) {
                if (troll.getLocation().equals(pacMan.getLocation())) {
                    hasPacmanBeenHit = true;
                    break;
                }
            }

            if (!hasPacmanBeenHit) {
                for (Monster tx5 : tx5s) {
                    if (tx5.getLocation().equals(pacMan.getLocation())) {
                        hasPacmanBeenHit = true;
                        break;
                    }
                }
            }

            if (portals.size() != 0) {
                try {
                    checkAndHandlePortalCollision(pacMan);
                    for (Monster troll : trolls) {
                        checkAndHandlePortalCollision(troll);
                    }
                    for (Monster tx5 : tx5s) {
                        checkAndHandlePortalCollision(tx5);
                    }
                    Thread.sleep(100);
                } catch (InterruptedException e) {

                    e.printStackTrace();
                }

                delay(10);

            }


        } while (!hasPacmanBeenHit && !hasCompletedAllMaps);
        delay(120);


        Location loc = pacMan.getLocation();
        for (Monster troll : trolls) {
            troll.setStopMoving(true);
        }
        for (Monster tx5 : tx5s) {
            tx5.setStopMoving(true);
        }
        pacMan.removeSelf();

        String title = "";
        if (hasPacmanBeenHit) {
            getBg().setPaintColor(Color.red);
            title = "GAME OVER";
            addActor(new Actor("sprites/explosion3.gif"), loc);
        } else if (hasCompletedAllMaps) {
            getBg().setPaintColor(Color.yellow);
            title = "YOU WIN";
        }
        setTitle(title);
        gameCallback.endOfGame(title);

        doPause();


    }

    public GameCallback getGameCallback() {
        return gameCallback;
    }


    public ArrayList<Location> getPillAndItemLocations() {
        return pillAndItemLocations;
    }


    protected void putPill(GGBackground bg, Location location) {
        bg.fillCircle(toPoint(location), 5);
    }

    protected void putGold(GGBackground bg, Location location) {
        bg.setPaintColor(Color.yellow);
        bg.fillCircle(toPoint(location), 5);
        Actor gold = new Actor("sprites/gold.png");
        this.goldPieces.add(gold);
        addActor(gold, location);
    }

    protected void putIce(GGBackground bg, Location location) {
        bg.setPaintColor(Color.blue);
        bg.fillCircle(toPoint(location), 5);
        Actor ice = new Actor("sprites/ice.png");
        this.iceCubes.add(ice);
        addActor(ice, location);
    }

    protected void putPortal(GGBackground bg, Location location, Color color) {
        bg.setPaintColor(Color.red);
        bg.fillCircle(toPoint(location), 5);


        if (color.equals(Color.white)) {
            Portal portal = new Portal(color, location, "sprites/portal_white.png");
            addPortal(portal);
            addActor(portal, location);
        } else if (color.equals(Color.yellow)) {
            Portal portal = new Portal(color, location, "sprites/portal_yellow.png");
            addPortal(portal);
            addActor(portal, location);
        } else if (color.equals(Color.orange)) {
            Portal portal = new Portal(color, location, "sprites/portal_dark_gold.png");
            addPortal(portal);
            addActor(portal, location);
        } else if (color.equals(Color.darkGray)) {
            Portal portal = new Portal(color, location, "sprites/portal_dark_gray.png");
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

    // Get portal from specific location
    public Portal getPortalAt(Location location) {
        for (ArrayList<Portal> portalPair : portals.values()) {
            for (Portal portal : portalPair) {
                if (portal.getLocation().equals(location)) {
                    return portal;
                }
            }
        }
        return null;
    }

    // Get the other portal from one portal
    public Portal getOtherPortalEnd(Portal portal) {
        ArrayList<Portal> portalPair = portals.get(portal.getColor());
        if (portalPair.get(0).equals(portal)) {
            return portalPair.get(1);
        } else {
            return portalPair.get(0);
        }
    }

    public synchronized void removeItem(String type, Location location) {
        if (type.equals("gold")) {
            for (Actor item : this.goldPieces) {
                if (location.getX() == item.getLocation().getX() && location.getY() == item.getLocation().getY()) {
                    item.hide();
                }
            }
        } else if (type.equals("ice")) {
            for (Actor item : this.iceCubes) {
                if (location.getX() == item.getLocation().getX() && location.getY() == item.getLocation().getY()) {
                    item.hide();
                }
            }
        }
    }


    public void drawGridFromMap(GGBackground bg) {


        //bg.setPaintColor(Color.white);

        for (int y = 0; y < nbVertCells; y++) {
            for (int x = 0; x < nbHorzCells; x++) {
                bg.setPaintColor(Color.white);
                Location location = new Location(x, y);
                char a = currentGrid.getCellChar(location);

                if (a == 'b') {
                    bg.fillCell(location, Color.gray);
                } else {
                    bg.fillCell(location, Color.lightGray);
                }

                if (a == 'a') { // pathTile
                    bg.fillCell(location, Color.lightGray);

                } else if (a == 'c') { // pill
                    putPill(bg, location);
                } else if (a == 'd') {//gold
                    putGold(bg, location);
                } else if (a == 'e') {//ice
                    putIce(bg, location);
                } else if (a == 'f') {//pacman
                    if (!pacManAdded) {
                        addActor(pacMan, location);
                        pacManAdded = true;
                    } else {
                        pacMan.setLocation(location);
                    }

                } else if (a == 'g') {//troll
                    Monster troll =
                            monsterFactory.createMonster(this,
                                    MonsterType.Troll);
                    addActor(troll, location);
                    trolls.add(troll);
                } else if (a == 'h') {//tx5
                    Monster tx5 =
                            monsterFactory.createMonster(this,
                                    MonsterType.TX5);
                    addActor(tx5, location);
                    tx5s.add(tx5);
                } else if (a == 'i') {//portal white
                    putPortal(bg, location, Color.white);
                } else if (a == 'j') {//portal yellow
                    putPortal(bg, location, Color.yellow);
                } else if (a == 'k') {//portal dark gold
                    putPortal(bg, location, Color.orange);
                } else if (a == 'l') {//portal dark gray
                    putPortal(bg, location, Color.darkGray);
                }
            }
        }
    }


    private void setupItemsLocationsFromMap() {
        for (int y = 0; y < nbVertCells; y++) {
            for (int x = 0; x < nbHorzCells; x++) {
                Location location = new Location(x, y);
                char a = currentGrid.getCellChar(location);

                if (a == 'c') { // pill
                    pillAndItemLocations.add(location);
                } else if (a == 'd') {//gold
                    pillAndItemLocations.add(location);
                } else if (a == 'e') {//ice
                    pillAndItemLocations.add(location);
                }
            }
        }
    }

    private int countItemsFromMap() {
        int pillsAndItemsCount = 0;
        for (int y = 0; y < nbVertCells; y++) {
            for (int x = 0; x < nbHorzCells; x++) {
                Location location = new Location(x, y);
                char a = currentGrid.getCellChar(location);

                if (a == 'c') { // pill
                    pillsAndItemsCount++;
                } else if (a == 'd') {//gold
                    pillsAndItemsCount++;
                }
            }
        }
        return pillsAndItemsCount;
    }

    public int getNumHorzCells() {
        return nbHorzCells;
    }

    public int getNumVertCells() {
        return nbVertCells;
    }
}
