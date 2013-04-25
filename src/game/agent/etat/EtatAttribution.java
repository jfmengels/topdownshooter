package game.agent.etat;

import game.Environnement;
import game.agent.Agent;

import java.util.Random;

/**
 * Etat où l'agent va élire un organisateur, puis attendre que celui-ci lui
 * attribue un rôle.
 * @author Jeroen Engels et Florent Claisse
 */
public class EtatAttribution implements Etat {

	private int randomToken;
	private boolean estOrga;
	private int compteurMessages;
	private int nbAgents;
	private boolean tokenEnvoye;

	/**
	 * Créé un nouvel état d'attribution.
	 */
	public EtatAttribution() {
		randomToken = new Random().nextInt();
		estOrga = true;
		compteurMessages = 0;
		tokenEnvoye = false;
	}

	@Override
	public void action(Agent agent, Environnement env) {
		if (!tokenEnvoye) {
			tokenEnvoye = true;
			agent.getEquipe().ecrireTableau(agent, "token " + randomToken);
		}
	}

	@Override
	public void entre(Agent agent, Environnement env) {
		nbAgents = agent.getEquipe().getAgents().size() - 1;
	};

	@Override
	public void recoitMessage(Agent agent, Environnement env, String message) {
		if (message.startsWith("token")) {
			interpreteToken(agent, message);
		} else if (message.startsWith("setEtat")) {
			interpreteEtat(agent, message);
		} else if (message.startsWith("demandeData")) {
			envoieDonnees(agent);
		}
	}

	@Override
	public String getComportement() {
		return compAutre;
	}

	/**
	 * Lis tous les messages de type "token X", pour déterminer si oui ou non
	 * l'agent courant sera l'agent organisateur.
	 */
	private void interpreteToken(Agent agent, String message) {
		String tokenStr = message.split(" ")[1];
		int token = Integer.parseInt(tokenStr);
		if (token < randomToken) {
			estOrga = false;
		}
		synchronized (this) {
			compteurMessages++;
			if (estOrga && compteurMessages >= nbAgents) {
				agent.setEtat(agent.getEquipe().getOrganisation());
			}
		}
	}

	/**
	 * Lis un message pour changer l'état de l'agent.
	 * @param agent Agent dont il faut changer l'état.
	 * @param message Message décrivant le nouvel état.
	 */
	private void interpreteEtat(Agent agent, String message) {
		// Lis le message de la forme :
		// setEtat idAgent nouvelEtat
		// Si idAgent correspond à l'id de l'agent, alors il change son état
		// vers nouvelEtat.
		String idStr = message.split(" ")[1]; // id de l'agent
		int id = Integer.parseInt(idStr);
		if (id == agent.getId()) {
			String etat = message.split(" ")[2]; // nouvel etat
			if (etat.equals("attaque")) {
				agent.setEtat(new EtatAttaque());
			} else if (etat.equals("defense")) {
				agent.setEtat(new EtatDefense());
			}
		}
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
