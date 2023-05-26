package src.pacmanGame;

import ch.aplu.jgamegrid.*;

public class NewGameGrid extends GameGrid {

    private final char[][] mazeArray;

    public NewGameGrid(String mapString) {
        super();

        int nbHorzCells = 20;
        int nbVertCells = 11;
        mazeArray = new char[nbVertCells][nbHorzCells];


        String[] mazeRows = mapString.split("\n");

        for (int i = 0; i < nbVertCells; i++) {

            mazeArray[i] = mazeRows[i].toCharArray();
        }
    }


    public char getCellChar(Location location) {
        return mazeArray[location.y][location.x];
    }


}
