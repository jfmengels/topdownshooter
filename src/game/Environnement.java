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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JPanel;

/**
 * @author Florent
 */
public class Environnement extends JPanel {

	private static final long serialVersionUID = 1L;
	private final List<Agent> agents;
	private final List<Decor> decors;
	private final List<Equipe> equipes;

	public Environnement() {
		this.setBackground(Color.white);
		this.agents = new ArrayList<Agent>();
		this.decors = new ArrayList<Decor>();
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
		decors.add(new Mur(new Point(50, 250), new Dimension(300, 100),
				Color.darkGray));
		decors.add(new Mur(new Point(50, 350), new Dimension(50, 100),
				Color.darkGray));
		decors.add(new Mur(new Point(300, 350), new Dimension(50, 100),
				Color.darkGray));
		// agents.add(new Agent(est, new Point(200, 500), this));
		agents.add(new Agent(est, new Point(200, 240), this));
		// agents.add(new Agent(ouest, new Point(300, 100), this));
		agents.add(new Agent(est, new Point(300, 100), this));
		agents.add(new Agent(est, new Point(100, 150), this));
	}

	public void start() {
		for (Agent agent : this.agents) {
			agent.init();
		}
		for (Agent agent : this.agents) {
			Thread thread = new Thread(agent);
			thread.start();
		}
	}

	/**
	 * Arrete proprement un thread attribué à agent.
	 * @param agent Agent à arreter.
	 */
	public void arreteThread(Agent agent) {
		agent.terminate();
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

	public boolean tirer(Agent source, Agent cible) {
		// TODO Mettre un nombre de pv en variable.
		boolean vivant = cible.toucher(100);
		if (!vivant) {
			arreteThread(cible);
		}
		return !vivant;
	}

	public List<Point> cheminVers(Point depart, Point destination) {
		boolean finished = false;
		Set<Point> aTraiter = new HashSet<>();
		aTraiter.add(destination);
		int[][] cout = new int[800][600];

		// initialisation tableau
		for (int i = 0; i < 800; i++) {
			for (int j = 0; j < 600; j++) {
				cout[i][j] = 1000000; // correspond au maximum de la distance
										// dans l'ecran
			}
		}
		cout[destination.x][destination.y] = 0;
		while (!aTraiter.isEmpty()) {
			// selection du point a traiter le plus proche de la cible
			Point minimum = null;
			for (Point point : aTraiter) {
				if (distance(point, depart) < distance(minimum, depart)) {
					minimum = point;
				}
			}
			aTraiter.remove(minimum);

			// on met a jour la distance minimum des voisin
			for (int i = -1; i <= 1; i++) {
				for (int j = -1; j <= 1; j++) {
					if (!isWalkable(minimum.x + i, minimum.y + j))
						continue;
					if ((minimum.x + i < 0) || (minimum.x + i >= 800))
						continue;
					if ((minimum.y + j < 0) || (minimum.y + j >= 600))
						continue;

					if ((cout[minimum.x][minimum.y] + 1) < cout[minimum.x + i][minimum.y
							+ j]) {
						cout[minimum.x + i][minimum.y + j] = cout[minimum.x][minimum.y] + 1;
						aTraiter.add(new Point(minimum.x + i, minimum.y + j));
					}
				}
			}

			// si on est arrivé, alors on arrete
			if (minimum.equals(depart)) {
				finished = true;
				break;
			}
		}

		if (!finished)
			return null;

		// recupération d'un chemin
		ArrayList<Point> chemin = new ArrayList<>();
		Point currentPoint = depart;
		while (!currentPoint.equals(destination)) {
			chemin.add(currentPoint);
			int x = currentPoint.x, y = currentPoint.y;
			int pixelCost = 1000000;
			for (int i = -1; i <= 1; i++) {
				for (int j = -1; j <= 1; j++) {
					if ((i == 0) && (j == 0))
						continue;
					if (cout[currentPoint.x + i][currentPoint.y + j] < pixelCost) {
						pixelCost = cout[currentPoint.x + i][currentPoint.y + j];
						x = currentPoint.x + i;
						y = currentPoint.y + j;
					}
				}
			}
			currentPoint = new Point(x, y);
		}
		return chemin;
	}

	/**
	 * Retourne la liste des enemis que peut voir un agent.
	 * @param agent Agent observateur.
	 * @return List des enemis visibles.
	 */
	public List<Agent> enemisEnVue(Agent agentSource) {
		ArrayList<Agent> liste = new ArrayList<>();
		if (agentSource == null) {
			return liste;
		}
		int distance;
		int x1, x2, y1, y2;
		x1 = agentSource.getPosition().x;
		y1 = agentSource.getPosition().y;
		for (Agent agent : liste) {
			if (agentSource.memeEquipe(agent)) {
				continue;
			}
			x2 = agent.getPosition().x;
			y2 = agent.getPosition().y;
			distance = (x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2);
			if (distance <= agentSource.getPortee() * agentSource.getPortee()) {
				// gestion cone de vue
				// gestions murs
				if (isVisible(agentSource.getPosition(), agent.getPosition())) {
					liste.add(agent);
				}
			}
		}

		return liste;
	}

	private boolean isVisible(Point p, Point q) {
		if (p.x > q.x) {
			Point temp = p;
			p = q;
			q = temp;
		}

		double x = p.x;
		double y = p.y;
		double pasX, pasY;
		if (Math.abs(q.x - p.x) > Math.abs(q.y - p.y)) {
			pasX = 1; // puisque les points sont réordonnés selon leur position
						// sur l'axe des abcisses
			pasY = (q.y - p.y) / Math.abs(q.x - p.x);
		} else {
			pasX = (q.x - p.x) / Math.abs(q.y - p.y);
			pasY = (q.y - p.y) / Math.abs(q.y - p.y);
		}

		while (x <= q.x) {
			if (!isWalkable((int) x, (int) y)) {
				return false;
			}
			x += pasX;
			y += pasY;
		}

		return true;
	}

	private boolean isWalkable(int x, int y) {
		int xmin, xmax, ymin, ymax;
		Mur mur;
		for (Decor decor : decors) {
			if (decor.getClass() == Mur.class) {
				mur = (Mur) decor;
				xmin = mur.getPosition().x - 3;
				ymin = mur.getPosition().y - 3;
				xmax = mur.getTaille().width + xmin + 3;
				ymax = mur.getTaille().height + ymin + 3;
				if ((x >= xmin) && (x <= xmax)) {
					if ((y >= ymin) && (y <= ymax)) {
						return false;
					}
				}
			}
		}
		return true;
	}

	private int distance(Point p, Point q) {
		if ((p == null) || (q == null)) {
			return 1000000; // valeur improbable
		}
		// return Math.abs(p.x - q.x) + Math.abs(p.y - q.y); //faibles calculs,
		// resultats imprecis
		return (p.x - q.x) * (p.x - q.x) + (p.y - q.y) * (p.y - q.y); // calculs
																		// lourds,
																		// resultats
																		// precis
	}

	public List<Point> chemin(List<Point> cameFrom, Point destination) {
		return null;
	}

	public Agent getAgent(int id) {
		for (Agent agent : this.agents) {
			if (agent.getId() == id) {
				return agent;
			}
		}
		return null;
	}
}