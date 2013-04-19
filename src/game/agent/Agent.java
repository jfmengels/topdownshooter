/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package game.agent;

import game.Environnement;
import game.agent.etat.Etat;
import game.agent.etat.EtatAttaque;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

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

	private final Environnement environnement;
	private boolean threadRunning;

	// TODO Ajouter orientation. Point, angle ?

	public Agent(Equipe equipe, Point position, Environnement env) {
		this.equipe = equipe;
		equipe.addAgent(this);
		this.environnement = env;

		this.mouvement = new Mouvement(this);
		this.etat = new EtatAttaque();

		this.vieMax = 100;
		this.vieActuelle = 90;
		this.position = position;
		this.vitesse = 60;
	}

	@Override
	public void run() {
		threadRunning = true;
		etat.entre(this, environnement);
		while (threadRunning) {
			etat.action(this, environnement);
			mouvement.bouger();
		}
	}

	/**
	 * Arrete le thread courant.
	 */
	public void terminate() {
		threadRunning = false;
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

	/**
	 * Touche l'agent lorsqu'on lui tire dessus.
	 * @param pv Points de vie à retirer.
	 * @return True si l'agent est encore en vie ensuite, false sinon.
	 */
	public boolean toucher(int pv) {
		vieActuelle -= pv;
		if (vieActuelle < 0) {
			vieActuelle = 0;
		}
		return estEnVie();
	}

	public boolean estEnVie() {
		return vieActuelle > 0;
	}

	public double getVitesse() {
		return vitesse;
	}

	public Mouvement getMouvement() {
		return mouvement;
	}

	public Equipe getEquipe() {
		return equipe;
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

	public Etat getEtat() {
		return etat;
	}

	public void setEtat(Etat etat) {
		this.etat = etat;
	}
}
