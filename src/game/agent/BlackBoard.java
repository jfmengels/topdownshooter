package game.agent;

import java.util.List;

/**
 * Intermédiaire dans la communication entre agents, permettant de réaliser des
 * broadcasts.
 * @author Jeroen Engels et Florent Claisse
 */
public class BlackBoard {

	/**
	 * Ecrit un message sur le tableau, qui sera envoyé à tous les autres
	 * agents.
	 * @param agent Agent qui écrit le message.
	 * @param message Message à écrire.
	 */
	public void ecrire(Agent agent, String message) {
		List<Agent> agents = agent.getEquipe().getAgents();
		System.out.println(agent.getId() + " envoie\t" + message);

		if (message.startsWith("kill")) {
			for (Agent a : agents) {
				a.recoitMessage(message);
			}
		} else {
			for (Agent a : agents) {
				if (a != agent && a.estEnVie()) {
					a.recoitMessage(message);
				}
			}
		}
	}
}
