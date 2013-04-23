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
		List<Agent> enemisEnVue = env.enemisEnVue(agent);
		if (!enemisEnVue.isEmpty()) {
			Agent cible = enemisEnVue.get(0);
			agent.tirer(cible);
		} else {
			agent.getMouvement().bouger();
		}
	}

	@Override
	public void recoitMessage(Agent agent, Environnement env, String message) {
		// TODO Auto-generated method stub

	}

}
