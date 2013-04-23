package game.agent;

import game.Environnement;
import game.agent.etat.EtatOrganisation;

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class Equipe {

	private final List<Agent> agents;
	private final TagEquipe tag;
	private final BlackBoard blackBoard;
	private final Color couleur;
	private final Point posCible;
	private final List<Point> posDefense;
	private final EtatOrganisation organisation;

	public Equipe(TagEquipe tag, Point cible) {
		this.posCible = cible;
		this.agents = new ArrayList<Agent>();
		this.tag = tag;
		this.blackBoard = new BlackBoard();
		this.posDefense = new ArrayList<Point>();
		this.organisation = new EtatOrganisation();

		if (tag == TagEquipe.EST) {
			this.couleur = new Color(100, 100, 250);
		} else {
			this.couleur = new Color(250, 100, 100);
		}
	}

	public void ecrireTableau(Agent source, String message) {
		this.blackBoard.ecrire(source, message);
	}

	public void addAgent(Agent agent) {
		this.agents.add(agent);
		this.blackBoard.addAgent(agent);
	}

	public TagEquipe getTag() {
		return tag;
	}

	public Color getCouleur() {
		return couleur;
	}

	public Point getPosCible() {
		return posCible;
	}

	public List<Agent> getAgents() {
		return agents;
	}

	public List<Point> getPosDefense() {
		return posDefense;
	}

	public void addPosDefense(Point pos) {
		posDefense.add(pos);
	}

	public EtatOrganisation getOrganisation() {
		return organisation;
	}

	public String getNom() {
		if (this.tag == TagEquipe.EST) {
			return "Est";
		} else {
			return "Ouest";
		}
	}

	public int getNbAgentsVivants() {
		int count = 0;
		for (Agent agent : this.agents) {
			if (agent.estEnVie()) {
				count++;
			}
		}
		return count;
	}

	/**
	 * Notifie de la fin du jeu.
	 * @param env Environnement de la simulation.
	 */
	public void end(Environnement env) {
		env.end(this);
	}
}
