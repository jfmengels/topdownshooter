/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package game.elements;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;

/**
 * @author Florent
 */
public class Mur extends Decor {

	private final Dimension taille;
	private final Color couleur;

	public Mur(Point position, Dimension taille, Color couleur) {
		super(position);
		this.taille = taille;
		this.couleur = couleur;
	}

	@Override
	public void paint(Graphics g) {
		g.setColor(couleur);
		g.fillRect(position.x, position.y, taille.width, taille.height);
	}

	public Dimension getTaille() {
		return taille;
	}
}
