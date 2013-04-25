package game.agent;

import java.util.AbstractQueue;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Intermédiaire dans la communication entre agents, permettant de réaliser des
 * broadcasts.
 * @author Jeroen Engels et Florent Claisse
 */
public class BlackBoard implements Runnable {
	// TODO Document

	private class Message {
		Agent emmetteur;
		String contenu;

		public Message(Agent emmetteur, String message) {
			this.emmetteur = emmetteur;
			this.contenu = message;
		}
	}

	private final AbstractQueue<Message> messages;
	private final Equipe equipe;
	private boolean threadRunning;

	public BlackBoard(Equipe equipe) {
		this.messages = new ConcurrentLinkedQueue<Message>();
		this.threadRunning = true;
		this.equipe = equipe;
	}

	/**
	 * Ecrit un message sur le tableau, qui sera envoyé à tous les autres
	 * agents.
	 * @param agent Agent qui écrit le message.
	 * @param message Message à écrire.
	 */
	public void ecrire(Agent agent, String message) {

		System.out.println(agent.getId() + " envoie\t" + message);

		if (message.startsWith("kill")) {
			messages.offer(new Message(null, message));
		} else {
			messages.offer(new Message(agent, message));
		}
	}

	@Override
	public void run() {
		Message message;
		List<Agent> agents = equipe.getAgents();
		while (threadRunning) {
			message = messages.poll();
			if (message != null) {
				for (Agent agent : agents) {
					if (!agent.equals(message.emmetteur) && agent.estEnVie()) {
						agent.recoitMessage(message.contenu);
					}
				}
			}
		}
	}
}
