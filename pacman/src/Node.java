package src;

import ch.aplu.jgamegrid.Location;

public class Node {

    public Location location;
    public Node parent;
    public double gCost;
    public double hCost;

    public Node(Location location, Node parent, double gCost, double hCost) {
        this.location = location;
        this.parent = parent;
        this.gCost = gCost;
        this.hCost = hCost;
    }

    public double fCost() {
        return gCost + hCost;
    }
}
