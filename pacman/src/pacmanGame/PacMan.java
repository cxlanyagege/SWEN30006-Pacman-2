// PacActor.java
// Used for PacMan
package src.pacmanGame;

import ch.aplu.jgamegrid.*;
import java.awt.event.KeyEvent;
import java.awt.Color;
import java.util.*;

public class PacMan extends PacActor implements GGKeyRepeatListener
{
  private static final int nbSprites = 4;
  private int idSprite = 0;
  private int nbPills = 0;
  private int score = 0;
  //rivate Game game;
  private List<Location> pillAndItemLocations;
  private SearchStrategy searchPillAndItem = new SearchPillAndItem();
  public PacMan(TorusVerseGame torusVerseGame)
  {
    super(true, "sprites/pacpix.gif", nbSprites);  // Rotatable
    this.torusVerseGame = torusVerseGame;
  }
  private boolean isAuto = false;

  public void setAuto(boolean auto) {
    isAuto = auto;
    pillAndItemLocations = torusVerseGame.getPillAndItemLocations();
  }

  public void setNbPills(int nbPills) {
    this.nbPills = nbPills;
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
    this.torusVerseGame.getGameCallback().pacManLocationChanged(getLocation(), score, nbPills);
  }

  // Check if pill is still valid in that position
  boolean isPillLocation(Location location) {
    List<Location> pillLocations = torusVerseGame.getPillAndItemLocations();
    for (Location pillLocation : pillLocations) {
      if (pillLocation.equals(location)) {
        return true;
      }
    }
    return false;
  }

  // Auto move main handle
  private void moveInAutoMode() {
    Location[] closestPillAndFirstStep = searchPillAndItem.search(torusVerseGame, this);
    if (closestPillAndFirstStep != null) {
      Location firstStep = closestPillAndFirstStep[1];
      setDirection(getLocation().getCompassDirectionTo(firstStep));
      setLocation(firstStep);
      eatPill(firstStep);
    }
  }

  public int getNbPills() {
    System.out.println("Current eat pill: " + nbPills);
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
      torusVerseGame.getGameCallback().pacManEatPillsAndItems(location, "pills");
      if (isAuto) pillAndItemLocations.remove(location);
    } else if (c.equals(Color.yellow)) {
      nbPills++;
      score+= 5;
      getBackground().fillCell(location, Color.lightGray);
      torusVerseGame.getGameCallback().pacManEatPillsAndItems(location, "gold");
      torusVerseGame.removeItem("gold",location);
      if (isAuto) pillAndItemLocations.remove(location);
    } else if (c.equals(Color.blue)) {
      getBackground().fillCell(location, Color.lightGray);
      torusVerseGame.getGameCallback().pacManEatPillsAndItems(location, "ice");
      torusVerseGame.removeItem("ice",location);
      if (isAuto) pillAndItemLocations.remove(location);
    }
    String title = "[PacMan in the Multiverse] Current score: " + score;
    gameGrid.setTitle(title);
  }
}
