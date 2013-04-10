/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package game;

import game.agent.Agent;
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

	public Environnement() {
		this.setBackground(Color.white);
		this.agents = new ArrayList<Agent>();
		this.decors = new ArrayList<Decor>();
		this.threadAgent = new ArrayList<Thread>();
		decors.add(new Cible(TagEquipe.EST, new Point(100, 100)));

		decors.add(new Cible(TagEquipe.OUEST, new Point(200, 200)));
		decors.add(new Mur(new Point(450, 120), new Dimension(230, 120),
				Color.darkGray));
		agents.add(new Agent());
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
}
