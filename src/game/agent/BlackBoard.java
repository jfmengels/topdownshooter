package game.agent;

import java.util.ArrayList;
import java.util.List;

public class BlackBoard {

	private final List<Agent> agents;

	public BlackBoard() {
		this.agents = new ArrayList<Agent>();
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
