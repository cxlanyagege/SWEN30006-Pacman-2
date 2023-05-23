// PacActor.java
// Used for PacMan
package src;

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
  private Queue<Location> pathToClosestPill = new LinkedList<>();
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

  // Use A* search to get to pills
  private Queue<Location> aStarSearch(Location start, Location goal) {
    List<Node> openList = new ArrayList<>();
    List<Location> closedList = new ArrayList<>();
    openList.add(new Node(start, null, 0, start.getDistanceTo(goal)));

    while (!openList.isEmpty()) {
      // Get node in open list with smallest fCost
      Node current = openList.stream()
              .min(Comparator.comparingDouble(Node::fCost))
              .orElseThrow(RuntimeException::new);

      // Check goal and reconstruct the path
      if (current.location.equals(goal)) {
        Queue<Location> path = new LinkedList<>();
        while (current.parent != null) {
          path.add(current.location);
          current = current.parent;
        }
        return path;
      }

      // Add location to closed list
      openList.remove(current);
      closedList.add(current.location);

      // Generate child nodes
      for (int[] direction : new int[][]{{0, 1}, {1, 0}, {0, -1}, {-1, 0}}) {
        Location childLocation = new Location(
                current.location.getX() + direction[0],
                current.location.getY() + direction[1]);
        if (!canMove(childLocation) || closedList.contains(childLocation)) {
          continue;
        }
        double gCost = current.gCost + 1; // Assuming cost for moving to each cell is 1
        double hCost = childLocation.getDistanceTo(goal);
        Node childNode = new Node(childLocation, current, gCost, hCost);
        // If child node is in open list and has larger fCost, then skip this child
        if (openList.stream().anyMatch(node -> node.location.equals(childLocation) && childNode.fCost() >= node.fCost())) {
          continue;
        }
        openList.add(childNode);
      }

      for (Node node : openList) {
        System.out.println(node.location);
      }

    }

    // Cannot fina a path
    return new LinkedList<>();
  }

  public void act()
  {
    show(idSprite);
    idSprite++;
    if (idSprite == nbSprites)
      idSprite = 0;

    if (isAuto) {
      //moveInAutoLegacy();
      //moveInAutoAStar();
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
        if (x >= 0 && x < game.getNumHorzCells() && y >= 0 && y < game.getNumVertCells()
                && canMove(next) && !visited[x][y]) {
          queue.offer(next);
          visited[x][y] = true;
          pathTo.put(next, current);
        }
      }
    }
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

  private void followPropertyMoves() {
    String currentMove = propertyMoves.get(propertyMoveIndex);
    switch(currentMove) {
      case "R":
        turn(90);
        break;
      case "L":
        turn(-90);
        break;
      case "M":
        Location next = getNextMoveLocation();
        if (canMove(next)) {
          setLocation(next);
          eatPill(next);
        }
        break;
    }
    propertyMoveIndex++;
  }

//  private void moveInAutoLegacy() {
//
//    // Use walking sequence in property file
//    if (propertyMoves.size() > propertyMoveIndex) {
//      followPropertyMoves();
//      return;
//    }
//
//    // Get closest pill location
//    Location closestPill = closestPillLocation();
//    double oldDirection = getDirection();
//
//    // Set direction to the closest pill
//    Location.CompassDirection compassDir =
//            getLocation().get4CompassDirectionTo(closestPill);
//    Location next = getLocation().getNeighbourLocation(compassDir);
//    setDirection(compassDir);
//
//    // Move to the closest pill
//    if (!isVisited(next) && canMove(next)) {
//      setLocation(next);
//    } else {
//      // normal movement
//      int sign = randomiser.nextDouble() < 0.5 ? 1 : -1;
//      setDirection(oldDirection);
//      turn(sign * 90);  // Try to turn left/right
//      next = getNextMoveLocation();
//      if (canMove(next)) {
//        setLocation(next);
//      } else {
//        setDirection(oldDirection);
//        next = getNextMoveLocation();
//        if (canMove(next)) // Try to move forward
//        {
//          setLocation(next);
//        } else {
//          setDirection(oldDirection);
//          turn(-sign * 90);  // Try to turn right/left
//          next = getNextMoveLocation();
//          if (canMove(next)) {
//            setLocation(next);
//          } else {
//            setDirection(oldDirection);
//            turn(180);  // Turn backward
//            next = getNextMoveLocation();
//            setLocation(next);
//          }
//        }
//      }
//    }
//    eatPill(next);
//    addVisitedList(next);
//  }

//  private void moveInAutoAStar() {
//    // get the next move from pathToClosestPill
//    Location next = pathToClosestPill.poll();
//
//    // if next is null, it means the path has been exhausted or not yet calculated,
//    // so we calculate a new path to the closest pill/gold
//    if (next == null) {
//      Location closestPill = closestPillLocation();
//      pathToClosestPill = aStarSearch(getLocation(), closestPill);
//      // after calculation, get the next move again
//      next = pathToClosestPill.poll();
//    }
//
//    // now we got the next move, move to it if it's valid
//    if (next != null && canMove(next)) {
//      setLocation(next);
//      eatPill(next);
//    }
//  }

  // Auto move main handle
  private void moveInAutoMode() {
    if (propertyMoves.size() > propertyMoveIndex) {
      followPropertyMoves();
      return;
    }

    Location[] closestPillAndFirstStep = closestPillLocation();
    if (closestPillAndFirstStep != null) {
      Location firstStep = closestPillAndFirstStep[1];
      setDirection(getLocation().getCompassDirectionTo(firstStep));
      setLocation(firstStep);
      eatPill(firstStep);
    }
  }

  private void addVisitedList(Location location)
  {
    visitedList.add(location);
    if (visitedList.size() == listLength)
      visitedList.remove(0);
  }

  private boolean isVisited(Location location)
  {
    for (Location loc : visitedList)
      if (loc.equals(location))
        return true;
    return false;
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
    return nbPills;
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
      pillAndItemLocations.remove(location);
    } else if (c.equals(Color.yellow)) {
      nbPills++;
      score+= 5;
      getBackground().fillCell(location, Color.lightGray);
      game.getGameCallback().pacManEatPillsAndItems(location, "gold");
      game.removeItem("gold",location);
      pillAndItemLocations.remove(location);
    } else if (c.equals(Color.blue)) {
      getBackground().fillCell(location, Color.lightGray);
      game.getGameCallback().pacManEatPillsAndItems(location, "ice");
      game.removeItem("ice",location);
      pillAndItemLocations.remove(location);
    }
    String title = "[PacMan in the Multiverse] Current score: " + score;
    gameGrid.setTitle(title);
  }
}
