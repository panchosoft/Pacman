package org.panchosoft.util.pathfinding.heuristicas;

import org.panchosoft.pacman.util.pathfinding.AStarHeuristic;
import org.panchosoft.pacman.util.pathfinding.Mover;
import org.panchosoft.pacman.util.pathfinding.TileBasedMap;


public class ManhattanHeuristic implements AStarHeuristic {

	private int minimumCost;
	
	public ManhattanHeuristic(int minimumCost) {
		this.minimumCost = minimumCost;
	}
	

	public float getCost(TileBasedMap map, Mover mover, int x, int y, int tx,
			int ty) {
		return minimumCost * (Math.abs(x-tx) + Math.abs(y-ty));
	}

}
