package game.agent.etat;

import game.Environnement;
import game.agent.Agent;

import java.awt.Point;

/**
 * Etat où l'agent vise à atteindre la cible adverse.
 * @author Jeroen Engels et Florent Claisse
 */
public class EtatAttaque implements Etat {

	@Override
	public void entre(Agent agent, Environnement env) {
		System.out.println(agent.getId() + "\tAttaque");
		agent.getMouvement().arrete();
	}

	@Override
	public void action(Agent agent, Environnement env) {
		if (agent.getMouvement().estArrete()) {
			Point dest = env.autreEquipe(agent.getEquipe()).getPosCible();
			agent.allerVers(dest);
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
		if (message.startsWith("voit")) {
			// Si on nous indique la position d'un agent ennemi.
			String str[] = message.split(" ");
			int id = Integer.parseInt(str[1]);
			int x = Integer.parseInt(str[2]);
			int y = Integer.parseInt(str[3]);
			Point p = new Point(x, y);

			if (env.distanceVers(agent.getPosition(), p) < agent.getPortee() * 2) {
				// Et que celui-ci est relativement proche, on va se diriger
				// vers lui et le chasser.
				EtatChasseur nvEtat = new EtatChasseur(new EtatAttaque(), id, p);
				agent.setEtat(nvEtat);
			}
		}
	}

	@Override
	public String getComportement() {
		return compAttaque;
	}
}
