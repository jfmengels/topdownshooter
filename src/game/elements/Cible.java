/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package game.elements;

import game.agent.TagEquipe;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

/**
 * @author Florent
 */
public class Cible extends Decor {

	private final TagEquipe equipe;

	public Cible(TagEquipe equipe, Point position) {
		super(position);
		this.equipe = equipe;
	}

	@Override
	public void paint(Graphics g) {
		if (equipe == TagEquipe.EST) {
			g.setColor(new Color(200, 200, 250));
		} else {
			g.setColor(new Color(250, 200, 200));
		}
		g.fillOval(position.x - 10, position.y - 10, 21, 21);
		if (equipe == TagEquipe.EST) {
			g.setColor(new Color(100, 100, 250));
		} else {
			g.setColor(new Color(250, 100, 100));
		}
		g.fillOval(position.x - 2, position.y - 2, 5, 5);
	}
}
