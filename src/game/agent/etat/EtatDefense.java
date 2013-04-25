package game.agent.etat;

import game.Environnement;
import game.agent.Agent;
import game.agent.Mouvement;

import java.awt.Point;
import java.util.List;
import java.util.Random;

/**
 * Etat où l'agent vise à défendre la cible.
 * @author Jeroen Engels et Florent Claisse
 */
public class EtatDefense implements Etat {

	/**
	 * @var tempsProchainMouvement: Moment où on va se redéplacer vers une
	 *      nouvelle position stratégique.
	 * @var dest: Point stratégique que veut couvrir l'agent.
	 * @var rand: Générateur de nombres aléatoires.
	 */
	private long tempsProchainMouvement;
	private Point dest;
	private final Random rand;

	public EtatDefense() {
		rand = new Random();
		tempsProchainMouvement = 0;
	}

	@Override
	public void entre(Agent agent, Environnement env) {
		// Ne rien faire.
		System.out.println(agent.getId() + "\tDefense");
		agent.getMouvement().arrete();
		tempsProchainMouvement = 0;
	}

	@Override
	public void action(Agent agent, Environnement env) {
		// On regarde si on a des ennemis en vue.
		boolean ennemi = agent.voitEnnemi(); // Si oui, il va lui tirer dessus.
		if (!ennemi) {
			// Sinon, on le déplace normalement.
			Mouvement mouv = agent.getMouvement();
			if (mouv.estArrete()
					&& System.currentTimeMillis() >= tempsProchainMouvement) {
				// S'il faut définir une nouvelle position de campement, on
				// choisit une des positions prédéterminées au hasard
				List<Point> posPossibles = agent.getEquipe().getPosDefense();
				this.dest = posPossibles.get(rand.nextInt(posPossibles.size()));
				// Et on dit à l'agent d'y aller.
				agent.allerVers(dest);
			} else if (!mouv.estArrete()) {
				// Continuer le mouvement
				mouv.bouger();
				if (mouv.estArrete()) {
					if (agent.getPosition().equals(dest)) {
						// Si on finit le mouvement, on définit combien de temps
						// on reste à cette position.
						tempsProchainMouvement = System.currentTimeMillis()
								+ (rand.nextInt(4) + 1) * 500;

						// On va regarder vers le point associé à cette position
						// stratégique.
						Point pos = agent.getPosition();
						Point orientation = agent.getEquipe()
								.getOrientationDefense(pos);
						agent.setOrientation(orientation);
					} else {
						if (dest == null) {
							// TODO Print
							System.out.println("dest null " + agent.getId());
						}
						agent.allerVers(dest);
					}
				}
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

			if (env.distanceVers(agent.getPosition(), p) < agent.getPortee() * 1.5) {
				// Et que celui-ci est relativement proche, on va se diriger
				// vers lui et le chasser.
				EtatChasseur nvEtat = new EtatChasseur(new EtatDefense(), id, p);
				agent.setEtat(nvEtat);
			}
		}
	}

	@Override
	public String getComportement() {
		return compDefense;
	}
}
