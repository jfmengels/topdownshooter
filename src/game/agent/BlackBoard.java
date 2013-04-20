package game.agent;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BlackBoard {

	private final List<Agent> agents;
	private final Map<Agent, Point> enemiPos;

	public BlackBoard() {
		this.agents = new ArrayList<Agent>();
		this.enemiPos = new HashMap<Agent, Point>();
	}

	public void notifierPos(Collection<Agent> enemis) {
		for (Agent enemi : enemis) {
			// Notifier les positions de chaque enemi.
			enemiPos.put(enemi, enemi.getPosition());
		}
	}

	public void ecrire(Agent agent, String message) {
		System.out.println(agent.getId() + " envoie\t" + message);
		for (Agent a : this.agents) {
			if (a != agent) {
				a.recoitMessage(message);
			}
		}
	}

	public void addAgent(Agent agent) {
		this.agents.add(agent);
	}

}
