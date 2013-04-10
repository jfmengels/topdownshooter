package game.agent;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class Equipe {

	private final List<Agent> agents;
	private final TagEquipe tag;
	private final BlackBoard blackBoard;
	private final Color couleur;

	public Equipe(TagEquipe tag) {
		this.agents = new ArrayList<Agent>();
		this.tag = tag;
		this.blackBoard = new BlackBoard();
		if (tag == TagEquipe.EST) {
			this.couleur = new Color(100, 100, 250);
		} else {
			this.couleur = new Color(250, 100, 100);
		}
	}

	public void writeToBlackboard(String message) {
		this.blackBoard.write(message);
	}

	public TagEquipe getTag() {
		return tag;
	}

	public Color getCouleur() {
		return couleur;
	}
}
