package game.agent.etat;

import game.Environnement;
import game.agent.Agent;

import java.awt.Point;
import java.util.List;

public class EtatAttaque implements Etat {

	@Override
	public void entre(Agent agent, Environnement env) {
		System.out.println(agent.getId() + "\tAttaque");

	}

	@Override
	public void action(Agent agent, Environnement env) {
		if (agent.getMouvement().estArrete()) {
			Point cible = env.autreEquipe(agent.getEquipe()).getPosCible();
			List<Point> chemin = env.cheminVers(agent.getPosition(), cible);
			agent.getMouvement().setDestinations(chemin);
		}
		// On regarde si on a des ennemis en vue.
		boolean ennemi = agent.voitEnnemi(); // Si oui, il va lui tirer dessus.
		if (!ennemi) {
			// Sinon, on le déplace normalement.
			agent.getMouvement().bouger();

			// On regarde s'il a atteint la cible.
			Point cible = env.autreEquipe(agent.getEquipe()).getPosCible();

			if (agent.getPosition().equals(cible)) {
				agent.getEquipe().end(env);
			}
		}
	}

	@Override
	public void recoitMessage(Agent agent, Environnement env, String message) {

	}
}
