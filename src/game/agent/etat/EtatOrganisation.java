package game.agent.etat;

import game.Environnement;
import game.agent.Agent;

import java.util.ArrayList;
import java.util.List;

public class EtatOrganisation implements Etat {

	private class Data {
		int id;
		int vitesse;
		int portee;
		int degats;
		int vieMax;
		double valeurAttaque;
		double valeurDefense;

		public Data(int id, int vitesse, int portee, int degats, int vieMax) {
			this.id = id;
			this.vitesse = vitesse;
			this.portee = portee;
			this.degats = degats;
			this.vieMax = vieMax;
			valeurAttaque = heuristiqueAttaque(this);
			valeurDefense = heuristiqueDefense(this);
		}
	}

	private int nbAgents;
	private final List<Data> dataAttaque;
	private final List<Data> dataDefense;
	private final EtatAttribution attribution;
	private boolean demandeFaite;

	public EtatOrganisation() {
		dataAttaque = new ArrayList<EtatOrganisation.Data>();
		dataDefense = new ArrayList<EtatOrganisation.Data>();
		attribution = new EtatAttribution();
		demandeFaite = false;
	}

	@Override
	public void entre(Agent agent, Environnement env) {
		// Ne rien faire
	}

	@Override
	public void action(Agent agent, Environnement env) {
		if (!demandeFaite) {
			demandeFaite = true;
			agent.getEquipe().ecrireTableau(agent, "demandeData");
			nbAgents = agent.getEquipe().getAgents().size();

			int vitesse = (int) agent.getVitesse();
			int portee = agent.getPortee();
			int degats = agent.getDegats();
			int vieMax = agent.getVieMax();
			String str = "dataAgent " + agent.getId() + " " + vitesse + " "
					+ portee + " " + degats + " " + vieMax;
			this.recoitMessage(agent, env, str);
		}
	}

	@Override
	public void recoitMessage(Agent agent, Environnement env, String message) {
		if (message.startsWith("dataAgent")) {
			String[] dataStr = message.split(" ");
			int id = Integer.parseInt(dataStr[1]);
			int speed = Integer.parseInt(dataStr[2]);
			int portee = Integer.parseInt(dataStr[3]);
			int degats = Integer.parseInt(dataStr[4]);
			int vieMax = Integer.parseInt(dataStr[5]);

			synchronized (this) {
				insererDonnees(new Data(id, speed, portee, degats, vieMax));
				if (dataAttaque.size() == nbAgents) {
					attribuerEtats(agent, env);
				}
			}
		} else {
			attribution.recoitMessage(agent, env, message);
		}
	}

	private void insererDonnees(Data data) {
		// On classe l'agent en fonction de son aptitude en attaque.
		int index;
		for (index = 0; index < dataAttaque.size(); index++) {
			if (dataAttaque.get(index).valeurAttaque > data.valeurAttaque) {
				break;
			}
		}
		dataAttaque.add(index, data);

		for (index = 0; index < dataDefense.size(); index++) {
			if (dataDefense.get(index).valeurDefense > data.valeurDefense) {
				break;
			}
		}
		dataDefense.add(index, data);
	}

	/**
	 * Heuristique pour regarder l'aptitude d'un agent pour l'offensive.
	 * @param data Données utilisés pour le calcul de l'aptitude.
	 * @return Une valeur d'aptitude. Une grande valeur démontrera une forte
	 *         aptitude pour le rôle.
	 */
	private double heuristiqueAttaque(Data data) {
		// On va seulement regarder la vitesse, qui sert à chasser des ennemis
		// ainsi qu'à atteindre rapidement l'objectif.
		return data.vitesse;
	}

	/**
	 * Heuristique pour regarder l'aptitude d'un agent pour la défense.
	 * @param data Données utilisés pour le calcul de l'aptitude.
	 * @return Une valeur d'aptitude. Une grande valeur démontrera une forte
	 *         aptitude pour le rôle.
	 */
	private double heuristiqueDefense(Data data) {
		// On va regarder la portée (pour pouvoir tirer le plus tôt possible sur
		// les ennemis), les dégâts (pour pouvoir achever rapidement
		// l'adversaire) ainsi que le nombre maximal de points de vie (pour
		// pouvoir rester le plus longtemps possible en défense).

		// Pondérations de chaque attribut
		int pondPortee = 3;
		int pondDegats = 2;
		int pondPV = 1;

		// On va ajouter la valeur de chaque attribut, proportionnelle aux
		// bornes aléatoires, valeur qui sera ensuite pondérée.
		double valeur = (data.portee - Agent.PORTEEMIN)
				/ (Agent.PORTEEMAX - Agent.PORTEEMIN) * pondPortee;
		valeur += (data.degats - Agent.DEGATSMIN)
				/ (Agent.DEGATSMAX - Agent.DEGATSMIN) * pondDegats;
		valeur += (data.vieMax - Agent.VIEMIN) / (Agent.VIEMAX - Agent.VIEMIN)
				* pondPV;
		return valeur;
	}

	private void attribuerRole(Agent agent, Data data, String etat) {
		String message = "setEtat " + data.id + " " + etat;
		if (data.id == agent.getId()) {
			agent.recoitMessage(message);
		} else {
			agent.getEquipe().ecrireTableau(agent, message);
		}
	}

	private void attribuerEtats(Agent agent, Environnement env) {
		// On va attribuer un rôle successivement au meilleur agent en attaque,
		// puis le meilleur en défense, et ainsi de suite jusqu'à avoir attribué
		// un rôle à chacun.

		while (!dataAttaque.isEmpty() && !dataDefense.isEmpty()) {
			if (!dataAttaque.isEmpty()) {
				attribuerRole(agent, dataAttaque.get(0), "attaque");
				dataDefense.remove(dataAttaque.get(0));
				dataAttaque.remove(0);
			}
			if (!dataDefense.isEmpty()) {
				attribuerRole(agent, dataDefense.get(0), "defense");
				dataAttaque.remove(dataDefense.get(0));
				dataDefense.remove(0);
			}
		}
/*
		int index;
		for (index = 0; index < dataAttaque.size() / 3; index++) {
			if (dataAttaque.get(index).id == agent.getId()) {
				agent.setEtat(new EtatDefense());
			} else {
				String message = "setEtat " + dataAttaque.get(index).id
						+ " defense";
				agent.getEquipe().ecrireTableau(agent, message);
			}
		}

		// Le reste va être en attaque
		for (; index < dataAttaque.size(); index++) {
			if (dataAttaque.get(index).id == agent.getId()) {
				agent.setEtat(new EtatAttaque());
			} else {
				String message = "setEtat " + dataAttaque.get(index).id
						+ " attaque";
				agent.getEquipe().ecrireTableau(agent, message);
			}
		}
*/
	}
}
