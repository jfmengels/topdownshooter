package game.agent.etat;

import game.Environnement;
import game.agent.Agent;

/**
 * Etat pour effectuer certains comportements communs à tous les états.
 * @author Jeroen Engels et Florent Claisse
 */
public class EtatCommun {

	/**
	 * Lit un message envoyé.
	 * @param agent Agent dont on représente l'état.
	 * @param env Environnement du jeu.
	 * @param message Message à lire.
	 */
	public void recoitMessage(Agent agent, Environnement env, String message) {
		if (message.startsWith("demandeComp")) {
			reagitDemandeComp(agent, env, message);
		} else if (message.startsWith("demandeData")) {
			envoieDonnees(agent);
		}
	}

	/**
	 * Réagit à une demande de comportement d'un autre agent.
	 * @param agent Agent dont on représente l'état.
	 * @param env Environnement du jeu.
	 * @param message Message à lire.
	 */
	private void reagitDemandeComp(Agent agent, Environnement env,
			String message) {
		// Si un autre agent a demandé à savoir le comportement des autres
		// agents, on lui répond.
		String str[] = message.split(" ");
		int idAgent = Integer.parseInt(str[1]);
		int idDemande = Integer.parseInt(str[2]);
		String nvMessage = "comp " + idAgent + " " + idDemande + " "
				+ agent.getEtat().getComportement();
		agent.getEquipe().ecrireTableau(agent, nvMessage);
	}

	/**
	 * Envoie des données qui concernent l'agent, à destination de l'agent
	 * organisateur.
	 * @param agent Agent dont on représente l'état.
	 */
	private void envoieDonnees(Agent agent) {
		String attributs = agent.getAttributs();
		agent.getEquipe().ecrireTableau(agent, attributs);
	}
}
