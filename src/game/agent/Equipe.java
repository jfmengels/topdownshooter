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

	public Equipe(TagEquipe tag, Point cible) {
		this.posCible = cible;
		this.agents = new ArrayList<Agent>();
		this.tag = tag;
		this.blackBoard = new BlackBoard();
		if (tag == TagEquipe.EST) {
			this.couleur = new Color(100, 100, 250);
		} else {
			this.couleur = new Color(250, 100, 100);
		}
	}

	public void ecrireTableau(String message) {
		this.blackBoard.ecrire(message);
	}

	public void addAgent(Agent agent) {
		this.agents.add(agent);
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
}