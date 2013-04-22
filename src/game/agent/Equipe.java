package game.agent;

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

	public Equipe(TagEquipe tag, Point cible) {
		this.posCible = cible;
		this.agents = new ArrayList<Agent>();
		this.tag = tag;
		this.blackBoard = new BlackBoard();
		this.posDefense = new ArrayList<Point>();
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
}
