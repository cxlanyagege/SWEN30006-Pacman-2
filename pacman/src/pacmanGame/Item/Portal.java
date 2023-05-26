package src.pacmanGame.Item;

import java.awt.Color;
import ch.aplu.jgamegrid.Actor;
import ch.aplu.jgamegrid.Location;

public class Portal extends Actor {
    private Color color;
    private Location location;

    public Portal(Color color, Location location, String spriteImagePath) {
        super(spriteImagePath);
        this.color = color;
        this.location = location;
    }

    // getters and setters...

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}

