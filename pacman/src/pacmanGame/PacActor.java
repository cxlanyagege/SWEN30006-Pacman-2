// PacActor.java
// Used for PacMan
package src.pacmanGame;

import ch.aplu.jgamegrid.*;
import java.awt.event.KeyEvent;
import java.awt.Color;
import java.util.*;

public class PacActor extends Actor implements GGKeyRepeatListener
{
  private static final int nbSprites = 4;
  private int idSprite = 0;
  private int nbPills = 0;
  private int score = 0;
  private Game game;
  private ArrayList<Location> visitedList = new ArrayList<Location>();
  private List<String> propertyMoves = new ArrayList<>();
  private int propertyMoveIndex = 0;
  private final int listLength = 1000;
  private int seed;
  private Random randomiser = new Random();
  private List<Location> pillAndItemLocations;



  public PacActor(Game game)
  {
    super(true, "sprites/pacpix.gif", nbSprites);  // Rotatable
    this.game = game;
  }
  private boolean isAuto = false;




  public void setAuto(boolean auto) {
    isAuto = auto;
    pillAndItemLocations = game.getPillAndItemLocations();
  }


  public void setSeed(int seed) {
    this.seed = seed;
    randomiser.setSeed(seed);
  }

  public void setPropertyMoves(String propertyMoveString) {
    if (propertyMoveString != null) {
      this.propertyMoves = Arrays.asList(propertyMoveString.split(","));
    }
  }

  public void keyRepeated(int keyCode)
  {
    if (isAuto) {
      return;
    }
    if (isRemoved())  // Already removed
      return;
    Location next = null;
    switch (keyCode)
    {
      case KeyEvent.VK_LEFT:
        next = getLocation().getNeighbourLocation(Location.WEST);
        setDirection(Location.WEST);
        break;
      case KeyEvent.VK_UP:
        next = getLocation().getNeighbourLocation(Location.NORTH);
        setDirection(Location.NORTH);
        break;
      case KeyEvent.VK_RIGHT:
        next = getLocation().getNeighbourLocation(Location.EAST);
        setDirection(Location.EAST);
        break;
      case KeyEvent.VK_DOWN:
        next = getLocation().getNeighbourLocation(Location.SOUTH);
        setDirection(Location.SOUTH);
        break;
    }
    if (next != null && canMove(next))
    {
      setLocation(next);
      eatPill(next);
    }
  }

  public void act()
  {
    show(idSprite);
    idSprite++;
    if (idSprite == nbSprites)
      idSprite = 0;

    if (isAuto) {
      moveInAutoMode();
    }
    this.game.getGameCallback().pacManLocationChanged(getLocation(), score, nbPills);
  }

  // Use BFS to pick the closest pill location
  private Location[] closestPillLocation() {
    int[][] dirs = {{0, -1}, {-1, 0}, {0, 1}, {1, 0}}; // WEST, NORTH, EAST, SOUTH
    boolean[][] visited = new boolean[game.getNumHorzCells()][game.getNumVertCells()];
    Queue<Location> queue = new LinkedList<>();
    Map<Location, Location> pathTo = new HashMap<>(); // store the path to each reachable location
    Location start = getLocation();
    queue.offer(start);
    visited[start.getX()][start.getY()] = true;
    pathTo.put(start, null);

    while (!queue.isEmpty()) {
      Location current = queue.poll();
      if (isPillLocation(current)) {
        // Find the first step in the path to the closest pill
        Location firstStep = current;
        while (!pathTo.get(firstStep).equals(start)) {
          firstStep = pathTo.get(firstStep);
        }
        return new Location[]{current, firstStep};
      }

      for (int[] dir : dirs) {
        int x = current.getX() + dir[0];
        int y = current.getY() + dir[1];
        Location next = new Location(x, y);

        // Check if there is portal at current position
        Portal portal = game.getPortalAt(current);
        if (portal != null) {
          // Find the other side
          Location portalOtherEnd = game.getOtherPortalEnd(portal).getLocation();
          if (!visited[portalOtherEnd.getX()][portalOtherEnd.getY()]) {
            queue.offer(portalOtherEnd);
            visited[portalOtherEnd.getX()][portalOtherEnd.getY()] = true;
            pathTo.put(portalOtherEnd, current);
          }
        }

        if (x >= 0 && x < game.getNumHorzCells() && y >= 0 && y < game.getNumVertCells()
                && canMove(next) && !visited[x][y]) {
          queue.offer(next);
          visited[x][y] = true;
          pathTo.put(next, current);
        }
      }
    }

    //System.out.println("No more pills");
    return null;
  }

  // Check if pill is still valid in that position
  private boolean isPillLocation(Location location) {
    List<Location> pillLocations = game.getPillAndItemLocations();
    for (Location pillLocation : pillLocations) {
      if (pillLocation.equals(location)) {
        return true;
      }
    }
    return false;
  }

  // Auto move main handle
  private void moveInAutoMode() {
    Location[] closestPillAndFirstStep = closestPillLocation();
    if (closestPillAndFirstStep != null) {
      Location firstStep = closestPillAndFirstStep[1];
      setDirection(getLocation().getCompassDirectionTo(firstStep));
      setLocation(firstStep);
      eatPill(firstStep);
    }
  }

  boolean canMove(Location location)
  {
    Color c = getBackground().getColor(location);
    if ( c.equals(Color.gray) || location.getX() >= game.getNumHorzCells()
            || location.getX() < 0 || location.getY() >= game.getNumVertCells() || location.getY() < 0)
      return false;
    else
      return true;
  }

  public int getNbPills() {
    //System.out.println("Current eat pill: " + nbPills);
    return nbPills;
  }

  public void setNbPills(int nbPills) {
    this.nbPills = nbPills;
  }

  private void eatPill(Location location)
  {
    Color c = getBackground().getColor(location);
    if (c.equals(Color.white))
    {
      nbPills++;
      score++;
      getBackground().fillCell(location, Color.lightGray);
      game.getGameCallback().pacManEatPillsAndItems(location, "pills");
      if (isAuto) pillAndItemLocations.remove(location);
    } else if (c.equals(Color.yellow)) {
      nbPills++;
      score+= 5;
      getBackground().fillCell(location, Color.lightGray);
      game.getGameCallback().pacManEatPillsAndItems(location, "gold");
      game.removeItem("gold",location);
      if (isAuto) pillAndItemLocations.remove(location);
    } else if (c.equals(Color.blue)) {
      getBackground().fillCell(location, Color.lightGray);
      game.getGameCallback().pacManEatPillsAndItems(location, "ice");
      game.removeItem("ice",location);
      if (isAuto) pillAndItemLocations.remove(location);
    }
    String title = "[PacMan in the Multiverse] Current score: " + score;
    gameGrid.setTitle(title);
  }
}
