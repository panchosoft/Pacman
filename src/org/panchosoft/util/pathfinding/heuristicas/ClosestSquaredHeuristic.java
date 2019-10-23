package org.panchosoft.util.pathfinding.heuristicas;

import org.panchosoft.pacman.util.pathfinding.AStarHeuristic;
import org.panchosoft.pacman.util.pathfinding.Mover;
import org.panchosoft.pacman.util.pathfinding.TileBasedMap;

public class ClosestSquaredHeuristic implements AStarHeuristic {

	public float getCost(TileBasedMap map, Mover mover, int x, int y, int tx, int ty) {		
		float dx = tx - x;
		float dy = ty - y;
		
		return ((dx*dx)+(dy*dy));
	}

}
