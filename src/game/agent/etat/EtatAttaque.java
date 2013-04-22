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
			tirer(agent, env, cible);
		} else {
			agent.getMouvement().bouger();
		}
	}

	private void tirer(Agent source, Environnement env, Agent cible) {
		// Si un enemi est en vue, on va le viser et lui tirer dessus.
		// On attend un moment pour viser et tirer.
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		// Est-ce que l'enemi est toujours en vue ?
		if (env.enemisEnVue(source).contains(cible)) {
			boolean mort = env.tirer(source, cible);
			if (mort) {
				// Si on a tué l'enemi, on notifie les alliés.
				// TODO Notifier blackboard d'un kill.
			}
		}
	}

	@Override
	public void recoitMessage(Agent agent, Environnement env, String message) {
		// TODO Auto-generated method stub

	}

}
