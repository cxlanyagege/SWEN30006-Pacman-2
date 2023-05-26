package src.pacmanGame.PacActor;

import ch.aplu.jgamegrid.*;
import src.pacmanGame.TorusVerseGame;

import java.awt.Color;

public abstract class PacActor extends Actor {

    protected TorusVerseGame torusVerseGame;

    public PacActor(boolean isRotatable, String spriteName, int nbSprites) {
        super(isRotatable, spriteName, nbSprites);
    }

    public PacActor(String spriteName) {
        super(spriteName);
    }

    public boolean canMove(Location location) {
        Color c = getBackground().getColor(location);
        return !c.equals(Color.gray) && location.getX() < torusVerseGame.getNumHorzCells()
                && location.getX() >= 0 && location.getY() < torusVerseGame.getNumVertCells() && location.getY() >= 0;
    }
}

