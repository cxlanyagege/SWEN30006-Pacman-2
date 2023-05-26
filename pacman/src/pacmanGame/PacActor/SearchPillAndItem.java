package src.pacmanGame.PacActor;

import ch.aplu.jgamegrid.Location;
import src.pacmanGame.Item.Portal;
import src.pacmanGame.PacActor.PacMan.PacMan;
import src.pacmanGame.TorusVerseGame;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public class SearchPillAndItem implements SearchStrategy {
    @Override
    public synchronized Location[] search(TorusVerseGame torusVerseGame, PacMan actor) {
        int[][] dirs = {{0, -1}, {-1, 0}, {0, 1}, {1, 0}}; // WEST, NORTH, EAST, SOUTH
        boolean[][] visited = new boolean[torusVerseGame.getNumHorzCells()][torusVerseGame.getNumVertCells()];
        Queue<Location> queue = new LinkedList<>();
        Map<Location, Location> pathTo = new HashMap<>(); // store the path to each reachable location
        Location start = actor.getLocation();
        queue.offer(start);
        visited[start.getX()][start.getY()] = true;
        pathTo.put(start, null);

        while (!queue.isEmpty()) {
            Location current = queue.poll();
            if (actor.isPillLocation(current)) {
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

                // Check if there is portal at current position
                Portal portal = torusVerseGame.getPortalAt(current);
                if (portal != null) {
                    // Find the other side
                    Location portalOtherEnd = torusVerseGame.getOtherPortalEnd(portal).getLocation();
                    if (!visited[portalOtherEnd.getX()][portalOtherEnd.getY()]) {
                        queue.offer(portalOtherEnd);
                        visited[portalOtherEnd.getX()][portalOtherEnd.getY()] = true;
                        pathTo.put(portalOtherEnd, current);
                    }
                }

                if (x >= 0 && x < torusVerseGame.getNumHorzCells() && y >= 0 && y < torusVerseGame.getNumVertCells()
                        && actor.canMove(next) && !visited[x][y]) {
                    queue.offer(next);
                    visited[x][y] = true;
                    pathTo.put(next, current);
                }
            }
        }
        return null;
    }
}
