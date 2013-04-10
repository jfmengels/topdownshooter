/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package game.agent;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;

import display.IDessinable;

/**
 * @author Florent
 */
public class Agent implements Runnable, IDessinable {

	private final Equipe equipe;
	private Etat etat;

	private final int vieMax;
	private int vieActuelle;

	private Point position;
	private final Mouvement mouvement;
	private double vitesse;

	public Agent() {
		this.equipe = new Equipe(TagEquipe.EST);
		this.vieMax = 100;
		this.vieActuelle = 90;
		this.position = new Point(200, 500);
		this.mouvement = new Mouvement(this);
		this.vitesse = 60;
		ArrayList<Point> array = new ArrayList<Point>();
		array.add(new Point(100, 100));
		array.add(new Point(200, 300));
		array.add(new Point(100, 400));
		mouvement.setDestinations(array);
	}

	@Override
	public void run() {
		while (true) {
			mouvement.bouger();
		}
	}

	public Point getPosition() {
		return position;
	}

	public void setPosition(Point position) {
		this.position = position;
	}

	public void setPosition(int x, int y) {
		this.position = new Point(x, y);
	}

	public int getVieActuelle() {
		return vieActuelle;
	}

	public int getVieMax() {
		return vieMax;
	}

	public double getVitesse() {
		return vitesse;
	}

	@Override
	public void paint(Graphics g) {
		g.setColor(equipe.getCouleur());
		g.fillOval(position.x - 2, position.y - 2, 5, 5);
		g.setColor(Color.red);
		g.fillRect(position.x - 15, position.y - 10, 31, 2);
		g.setColor(Color.green);
		double vie = (double) vieActuelle / (double) vieMax;
		g.fillRect(position.x - 15, position.y - 10, (int) (31 * vie), 2);
	}

	public boolean memeEquipe(Agent agent) {
		return this.equipe.getTag() == agent.equipe.getTag();
	}
}
