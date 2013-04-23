package game.agent;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

/**
 * Gère les mouvements d'un agent.
 * @author Jeroen Engels et Florent Claisse
 */
public class Mouvement {

	/**
	 * Informations liées à l'agent et son mouvement.
	 * @var agent: Agent à déplacer.
	 * @var destination: Liste de points vers lequel l'agent doit passer pour
	 *      atteindre sa destination courante finale.
	 * @var posDepart: Point d'où est parti l'agent. Est mis à jour à chaque
	 *      fois qu'il se dirige vers une nouvelle destination (intermédiaire ou
	 *      finale).
	 * @var tempsDepart: Moment auquel l'agent est parti, est lié à posDepart.
	 * @var tempsEstime: Temps que va mettre l'agent pour atteindre sa
	 *      destination (intermédiaire ou finale), est lié à tempsDepart.
	 */
	private final Agent agent;
	private final List<Point> destination;
	private Point posDepart;
	private long tempsDepart;
	private long tempsEstime;

	/**
	 * Créé un gestionaire de mouvement associé pour un agent.
	 * @param agent Agent pour lequel créer le gestionnaire.
	 */
	public Mouvement(Agent agent) {
		this.agent = agent;
		this.destination = new ArrayList<Point>();
	}

	/**
	 * Modifie la destination d'un agent.
	 * @param destinations Nouvelles destinations. Le dernier élément de la
	 *            liste correspondra à la destination finale.
	 */
	public void setDestinations(List<Point> destinations) {
		// On supprime les destinations courantes.
		destination.clear();
		destination.addAll(destinations);

		// L'agent va partir de sa position actuelle
		posDepart = agent.getPosition();
		// On va estimer le temps qu'il va mettre (mettra à jour tempsDepart,
		// tempsEstime), et on va le réorienter vers se première destination.
		estimeTemps();
		agent.setOrientation(destinations.get(0));
	}

	/**
	 * Regarde si l'agent a fini de se déplacer.
	 * @return true si l'agent est arrêté, false sinon.
	 */
	public boolean estArrete() {
		return this.destination.isEmpty();
	}

	/**
	 * Bouge l'agent.
	 */
	public void bouger() {
		long tempsMis;
		boolean changeDirection = false;
		if (!estArrete()) {
			// Si l'agent doit encore se déplacer.
			tempsMis = System.currentTimeMillis() - tempsDepart;

			// On regarde s'il a dépassé sa destination (en boucle).
			while (tempsMis >= tempsEstime) {
				// On met à jour sa position et ses destinations.
				posDepart = destination.get(0);
				destination.remove(0);
				if (!estArrete()) {
					// Si ce n'était qu'une destination intermédiaire, on passe
					// à la destination suivante.
					estimeTemps();
					changeDirection = true;
				} else {
					// Sinon, il a atteint sa destination.
					agent.setPosition(posDepart);
					break;
				}
				tempsMis = System.currentTimeMillis() - tempsDepart;
			}

			// S'il doit toujours se déplacer
			if (!estArrete()) {
				// On calcule la position où il devrait se trouver.
				Point dest = destination.get(0);
				int dx = dest.x - posDepart.x;
				int dy = dest.y - posDepart.y;
				double ratio = tempsMis / (double) tempsEstime;
				int x = posDepart.x + (int) (dx * ratio);
				int y = posDepart.y + (int) (dy * ratio);
				agent.setPosition(x, y);
			}
		}
		if (changeDirection && !estArrete()) {
			// Si on a changé le direction de l'agent, on le réoriente.
			agent.setOrientation(destination.get(0));
		}
	}

	/**
	 * Estime le temps que va mettre l'agent pour atteindre se destination, et
	 * modifie les attributs en conséquence.
	 */
	public void estimeTemps() {
		// On regarde la distance du point courant à la prochaine destination.
		Point dest = destination.get(0);
		int dx = dest.x - posDepart.x;
		int dy = dest.y - posDepart.y;
		double distance = Math.sqrt(dx * dx + dy * dy);
		// Et on calcule le temps que mettra l'agent pour y aller.
		tempsEstime = (long) (distance / agent.getVitesse() * 1000);
		tempsDepart = System.currentTimeMillis();
	}
}