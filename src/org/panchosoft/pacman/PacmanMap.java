/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.panchosoft.pacman;

import org.panchosoft.pacman.util.pathfinding.Mover;
import org.panchosoft.pacman.util.pathfinding.TileBasedMap;

/**
 *
 * @author Pancho
 */
public class PacmanMap implements TileBasedMap {
/** The map width in tiles */
	public static final int WIDTH = 15;
	/** The map height in tiles */
	public static final int HEIGHT = 15;

	/** Indicate grass terrain at a given location */
	public static final int GRASS = 0;
	/** Indicate water terrain at a given location */
	public static final int WATER = 1;
	/** Indicate trees terrain at a given location */
	public static final int TREES = 2;
	/** Indicate a plane is at a given location */
	public static final int PLANE = 3;
	/** Indicate a boat is at a given location */
	public static final int BOAT = 4;
	/** Indicate a tank is at a given location */
	public static final int TANK = 5;

	/** The terrain settings for each tile in the map */
	private int[][] terrain = new int[WIDTH][HEIGHT];
	/** The unit in each tile of the map */
	private int[][] units = new int[WIDTH][HEIGHT];
	/** Indicator if a given tile has been visited during the search */
	private boolean[][] visited = new boolean[WIDTH][HEIGHT];

	/**
	 * Create a new test map with some default configuration
	 */
	public PacmanMap() {
        fillArea(0,0,14,14,GRASS);
        fillArea(4,0,2,1,TREES);
		fillArea(9,0,2,1,TREES);
        fillArea(1,1,2,1,TREES);
        fillArea(7,1,1,1,TREES);
        fillArea(12,1,2,1,TREES);
        fillArea(4,2,2,1,TREES);
        fillArea(9,2,2,1,TREES);
        fillArea(1,3,2,2,TREES);
        fillArea(3,4,1,1,TREES);
        fillArea(5,3,1,2,TREES);
        fillArea(7,3,1,4,TREES);
        fillArea(9,3,1,2,TREES);
         fillArea(12,3,2,2,TREES);
         fillArea(11,4,1,1,TREES);
         fillArea(0,6,1,3,TREES);
         fillArea(2,6,1,3,TREES);
         fillArea(4,6,1,3,TREES);
         fillArea(6,6,3,3,TREES);
         fillArea(10,6,1,3,TREES);
         fillArea(12,6,1,3,TREES);
         fillArea(14,6,1,3,TREES);
        fillArea(1,10,4,2,TREES);
        fillArea(4,12,1,1,TREES);
        fillArea(1,13,2,1,TREES);
        fillArea(4,14,2,1,TREES);
         fillArea(6,12,3,1,TREES);
         fillArea(6,10,1,1,TREES);
         fillArea(8,10,1,1,TREES);
         fillArea(10,10,4,2,TREES);
         fillArea(10,12,1,1,TREES);
         fillArea(9,14,2,1,TREES);
         fillArea(9,14,2,1,TREES);
         fillArea(12,13,2,1,TREES);
           fillArea(7,13,1,1,TREES);
        units[7][7] = TANK;
//
//		units[15][15] = TANK;
//		units[2][7] = BOAT;
//		units[20][25] = PLANE;
	}

	/**
	 * Fill an area with a given terrain type
	 *
	 * @param x The x coordinate to start filling at
	 * @param y The y coordinate to start filling at
	 * @param width The width of the area to fill
	 * @param height The height of the area to fill
	 * @param type The terrain type to fill with
	 */
	private void fillArea(int x, int y, int width, int height, int type) {
		for (int xp=x;xp<x+width;xp++) {
			for (int yp=y;yp<y+height;yp++) {
				terrain[xp][yp] = type;
			}
		}
	}

	/**
	 * Clear the array marking which tiles have been visted by the path
	 * finder.
	 */
	public void clearVisited() {
		for (int x=0;x<getWidthInTiles();x++) {
			for (int y=0;y<getHeightInTiles();y++) {
				visited[x][y] = false;
			}
		}
	}

	/**
	 * @see TileBasedMap#visited(int, int)
	 */
	public boolean visited(int x, int y) {
		return visited[x][y];
	}

	/**
	 * Get the terrain at a given location
	 *
	 * @param x The x coordinate of the terrain tile to retrieve
	 * @param y The y coordinate of the terrain tile to retrieve
	 * @return The terrain tile at the given location
	 */
	public int getTerrain(int x, int y) {
		return terrain[x][y];
	}

	/**
	 * Get the unit at a given location
	 *
	 * @param x The x coordinate of the tile to check for a unit
	 * @param y The y coordinate of the tile to check for a unit
	 * @return The ID of the unit at the given location or 0 if there is no unit
	 */
	public int getUnit(int x, int y) {
		return units[x][y];
	}

	/**
	 * Set the unit at the given location
	 *
	 * @param x The x coordinate of the location where the unit should be set
	 * @param y The y coordinate of the location where the unit should be set
	 * @param unit The ID of the unit to be placed on the map, or 0 to clear the unit at the
	 * given location
	 */
	public void setUnit(int x, int y, int unit) {
		units[x][y] = unit;
	}

	/**
	 * @see TileBasedMap#blocked(Mover, int, int)
	 */
	public boolean blocked(Mover mover, int x, int y) {
		// if theres a unit at the location, then it's blocked

		int unit = ((PacMover) mover).getType();
    
		// planes can move anywhere
		if (unit == PLANE) {
			return false;
		}
		// tanks can only move across grass
		if (unit == TANK) {
			return terrain[x][y] != GRASS;
		}
		// boats can only move across water
		if (unit == BOAT) {
			return terrain[x][y] != WATER;
		}

		// unknown unit so everything blocks
		return true;
	}

	/**
	 * @see TileBasedMap#getCost(Mover, int, int, int, int)
	 */
	public float getCost(Mover mover, int sx, int sy, int tx, int ty) {
		return 1;
	}

	/**
	 * @see TileBasedMap#getHeightInTiles()
	 */
	public int getHeightInTiles() {
		return WIDTH;
	}

	/**
	 * @see TileBasedMap#getWidthInTiles()
	 */
	public int getWidthInTiles() {
		return HEIGHT;
	}

	/**
	 * @see TileBasedMap#pathFinderVisited(int, int)
	 */
	public void pathFinderVisited(int x, int y) {
		visited[x][y] = true;
	}
}
