package src.pacmanGame;

import ch.aplu.jgamegrid.*;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Random;

public abstract class PacActor extends Actor {

    protected Game game;
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
        return !c.equals(Color.gray) && location.getX() < game.getNumHorzCells()
                && location.getX() >= 0 && location.getY() < game.getNumVertCells() && location.getY() >= 0;
    }
}

