
package org.panchosoft.pacman;

import org.panchosoft.pacman.util.pathfinding.Mover;

public class PacMover implements Mover {
	
	private int type;

	public PacMover(int type) {
		this.type = type;
	}

	public int getType() {
		return type;
	}
}
