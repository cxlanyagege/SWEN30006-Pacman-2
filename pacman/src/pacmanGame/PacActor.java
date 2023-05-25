package src.pacmanGame;

import ch.aplu.jgamegrid.*;

import java.awt.Color;

public abstract class PacActor extends Actor {

    protected TorusVerseGame torusVerseGame;
    //private ArrayList<Location> visitedList = new ArrayList<Location>();

    //private final int listLength = 15;


    public PacActor(boolean isRotatable, String spriteName, int nbSprites) {
        super(isRotatable, spriteName, nbSprites);
    }

    public PacActor(String spriteName) {
        super(spriteName);
    }

    protected boolean canMove(Location location) {
        Color c = getBackground().getColor(location);
        return !c.equals(Color.gray) && location.getX() < torusVerseGame.getNumHorzCells()
                && location.getX() >= 0 && location.getY() < torusVerseGame.getNumVertCells() && location.getY() >= 0;
    }
}

