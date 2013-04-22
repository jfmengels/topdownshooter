package game.agent.etat;

import game.Environnement;
import game.agent.Agent;
import game.agent.Mouvement;

import java.awt.Point;
import java.util.List;
import java.util.Random;

public class EtatDefense implements Etat {

	private long tempsProchainMouvement;
	private final Random rand;

	public EtatDefense() {
		rand = new Random();
	}

	@Override
	public void entre(Agent agent, Environnement env) {
		// Ne rien faire.
		System.out.println(agent.getId() + "\tDefense");
		tempsProchainMouvement = 0;
	}

	@Override
	public void action(Agent agent, Environnement env) {
		// TODO Regarder si personne en vue.
		Mouvement mouv = agent.getMouvement();
		if (mouv.estArrete()
				&& System.currentTimeMillis() >= tempsProchainMouvement) {
			// S'il faut définir une nouvelle position de campement

			// TODO Print
			System.out.println("EtatDefense action - Deplacement");

			List<Point> posPossibles = agent.getEquipe().getPosDefense();
			Point cible = posPossibles.get(rand.nextInt(posPossibles.size()));
			List<Point> chemin = env.cheminVers(agent.getPosition(), cible);
			mouv.setDestinations(chemin);
		} else if (!mouv.estArrete()) {
			// Continuer le mouvement

			mouv.bouger();
			if (mouv.estArrete()) {
				tempsProchainMouvement = System.currentTimeMillis()
						+ (rand.nextInt(4) + 1) * 500;
			}
		}
	}

	@Override
	public void recoitMessage(Agent agent, Environnement env, String message) {
		// TODO Auto-generated method stub

	}

}
