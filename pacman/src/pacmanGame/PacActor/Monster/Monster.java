/*
 *  Team Name: monday-16-15-team-04
 *  Team Member:
 *               Xinyi Yuan
 *               He Shen
 *               Yuchen Dong
 */

// Monster.java
// Used for PacMan
package src.pacmanGame.PacActor.Monster;

import ch.aplu.jgamegrid.*;
import src.pacmanGame.PacActor.PacActor;
import src.pacmanGame.TorusVerseGame;

import java.util.*;

public abstract class Monster extends PacActor
{
  private MonsterType type;
  private ArrayList<Location> visitedList = new ArrayList<Location>();
  private final int listLength = 10;
  private boolean stopMoving = false;
  private int seed = 0;
  private Random randomiser = new Random(0);

  public Monster(TorusVerseGame torusVerseGame, MonsterType type)
  {
    super("sprites/" + type.getImageName());
    this.torusVerseGame = torusVerseGame;
    this.type = type;
  }

  public void stopMoving(int seconds) {
    this.stopMoving = true;
    Timer timer = new Timer(); // Instantiate Timer Object
    int SECOND_TO_MILLISECONDS = 1000;
    final Monster monster = this;
    timer.schedule(new TimerTask() {
      @Override
      public void run() {
        monster.stopMoving = false;
      }
    }, seconds * SECOND_TO_MILLISECONDS);
  }

  public void setSeed(int seed) {
    this.seed = seed;
    randomiser.setSeed(seed);
  }

  public void setStopMoving(boolean stopMoving) {
    this.stopMoving = stopMoving;
  }

  public void act()
  {
    if (stopMoving) {
      return;
    }
    walkApproach();
    if (getDirection() > 150 && getDirection() < 210)
      setHorzMirror(false);
    else
      setHorzMirror(true);
  }

  protected abstract void walkApproach();

  protected Location walkRandom(double oldDirection)
  {
    // Random walk
    int sign = randomiser.nextDouble() < 0.5 ? 1 : -1;
    setDirection(oldDirection);
    turn(sign * 90);  // Try to turn left/right
    Location next = getNextMoveLocation();
    if (canMove(next))
    {
      setLocation(next);
    }
    else {
      setDirection(oldDirection);
      next = getNextMoveLocation();
      if (canMove(next)) {// Try to move forward{
        setLocation(next);
      } else {
        setDirection(oldDirection);
        turn(-sign * 90);  // Try to turn right/left
        next = getNextMoveLocation();
        if (canMove(next)) {
          setLocation(next);
        } else {
          setDirection(oldDirection);
          turn(180);  // Turn backward
          next = getNextMoveLocation();
          setLocation(next);
        }
      }
    }
    return next;
  }

  public MonsterType getType() {
    return type;
  }

  public void addVisitedList(Location location)
  {
    visitedList.add(location);
    if (visitedList.size() == listLength)
      visitedList.remove(0);
  }

  public boolean isVisited(Location location)
  {
    for (Location loc : visitedList)
      if (loc.equals(location))
        return true;
    return false;
  }
}
