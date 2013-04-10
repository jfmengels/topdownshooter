/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package game.elements;

import java.awt.Point;

import display.IDessinable;

/**
 * @author Florent
 */
public abstract class Decor implements IDessinable {

	protected Point position;

	public Decor(Point position) {
		this.position = position;
	}
	
	public Point getPosition() {
		return position;
	}

}
