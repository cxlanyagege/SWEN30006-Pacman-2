package src.pacmanGame;

import ch.aplu.jgamegrid.*;

public class NewGameGrid extends PacManGameGrid{

    private final int nbHorzCells = 20;
    private final int nbVertCells = 11;
    private char[][] mazeArray;

    private String mapString;

    public NewGameGrid(String mapString){
        super();

        this.mapString = mapString;
        mazeArray = new char[nbVertCells][nbHorzCells];


        String[] mazeRows = mapString.split("\n");

        for (int i = 0; i < nbVertCells; i++) {

            mazeArray[i] = mazeRows[i].toCharArray();
        }
    }



    public char getCellChar(Location location)
    {
        return mazeArray[location.y][location.x];
    }



}
