package game.agent.etat;

import game.Environnement;
import game.agent.Agent;

import java.awt.Point;
import java.util.List;

public class EtatAttaque implements Etat {

	@Override
	public void entre(Agent agent, Environnement env) {
		Point cible = env.autreEquipe(agent.getEquipe()).getPosCible();
		List<Point> chemin = env.cheminVers(agent.getPosition(), cible);
		agent.getMouvement().setDestinations(chemin);
	}

	@Override
	public void action(Agent agent, Environnement env) {
		List<Agent> enemisEnVue = env.enemisEnVue(agent);
		if (!enemisEnVue.isEmpty()) {
			// Si un enemi est en vue, on va le viser et lui tirer dessus.
			// TODO Trouver un moyen d'instaurer un délai.
			boolean mort = env.tirer(agent, enemisEnVue.get(0));
			if (mort) {
				// Si on a tué l'enemi, on notifie les alliés.
				// TODO Notifier blackboard d'un kill.
			}
		} else {
			agent.getMouvement().bouger();
		}
	}
}
