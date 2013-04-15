/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package game;

import game.agent.Agent;
import game.agent.Equipe;
import game.agent.TagEquipe;
import game.elements.Cible;
import game.elements.Decor;
import game.elements.Mur;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

/**
 * @author Florent
 */
public class Environnement extends JPanel {

	private static final long serialVersionUID = 1L;
	private final List<Agent> agents;
	private final List<Decor> decors;
	private final List<Thread> threadAgent;
	private final List<Equipe> equipes;

	public Environnement() {
		this.setBackground(Color.white);
		this.agents = new ArrayList<Agent>();
		this.decors = new ArrayList<Decor>();
		this.threadAgent = new ArrayList<Thread>();
		this.equipes = new ArrayList<Equipe>();

		Equipe est = new Equipe(TagEquipe.EST, new Point(100, 100));
		Equipe ouest = new Equipe(TagEquipe.OUEST, new Point(200, 200));
		this.equipes.add(est);
		this.equipes.add(ouest);

		for (Equipe equipe : equipes) {
			decors.add(new Cible(equipe.getTag(), equipe.getPosCible()));
		}
		decors.add(new Mur(new Point(450, 120), new Dimension(230, 120),
				Color.darkGray));
		agents.add(new Agent(est, new Point(200, 500), this));
		agents.add(new Agent(ouest, new Point(300, 100), this));
	}

	public void start() {
		for (Agent agent : this.agents) {
			this.threadAgent.add(new Thread(agent));
		}
		for (Thread thread : this.threadAgent) {
			thread.start();
		}
	}

	public List<Agent> getAgents() {
		return agents;
	}

	public List<Decor> getDecors() {
		return decors;
	}

	public Equipe autreEquipe(TagEquipe equipe) {
		return equipe == TagEquipe.EST ? equipes.get(1) : equipes.get(0);
	}

	public Equipe autreEquipe(Equipe equipe) {
		return equipe == equipes.get(0) ? equipes.get(1) : equipes.get(0);
	}

	public boolean tirer(Agent source, Agent destination) {
		// TODO Implement
		// Besoin d'autre arguments ? Des positions plutôt que des agents, etc ?
		return false;
	}

	public List<Point> cheminVers(Point depart, Point destination) {
		// TODO Implement
		ArrayList<Point> list = new ArrayList<Point>();
		list.add(destination);
		return list;
	}

	public List<Agent> enemisEnVue(Agent agent) {
		// TODO Implement
		ArrayList<Agent> liste = new ArrayList<Agent>();
		return liste;
	}
}
