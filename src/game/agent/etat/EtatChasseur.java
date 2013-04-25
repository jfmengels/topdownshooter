package game.agent.etat;

import game.Environnement;
import game.agent.Agent;

import java.awt.Point;

public class EtatChasseur implements Etat {
	/**
	 * @var etatPrecedent: Etat dans lequel se trouvait précédement l'agent.
	 * @var idCible: Cible courante de l'agent.
	 * @var posSignale: Position à laquelle la cible a été signalée.
	 */
	private final Etat etatPrecedent;
	private int idCible;
	private Point posSignale;

	/**
	 * Créé un nouvel état chasseur.
	 * @param etat Etat dans lequel l'agent devra retourner après avoir fini la
	 *            chasse.
	 * @param idCible Identifiant de l'ennemi qu'il va chasser.
	 * @param posSignale Position à laquelle l'ennemi a été signalé, ou à
	 *            laquelle on l'a vu pour la dernière fois.
	 */
	public EtatChasseur(Etat etat, int idCible, Point posSignale) {
		this.etatPrecedent = etat;
		this.idCible = idCible;
		this.posSignale = posSignale;
	}

	@Override
	public void entre(Agent agent, Environnement env) {
		// On va se diriger jusqu'à la cible.
		System.out.println(agent.getId() + "\tChasseur: " + idCible);
		agent.allerVers(posSignale);
	}

	@Override
	public void action(Agent agent, Environnement env) {
		// On regarde si on a des ennemis en vue.
		boolean ennemi = agent.voitEnnemi(); // Si oui, il va lui tirer dessus.
		if (!ennemi) {
			// Sinon, on se déplace.
			agent.getMouvement().bouger();
		}
	}

	@Override
	public void recoitMessage(Agent agent, Environnement env, String message) {
		if (message.startsWith("kill")) {
			// Si on est notifié que notre cible est morte, on va revenir à
			// l'état précédent.
			String str[] = message.split(" ");
			int id = Integer.parseInt(str[1]);
			if (id == idCible) {
				agent.setEtat(etatPrecedent);
			}
		} else if (message.startsWith("voit")) {
			// Si on nous indique la position d'un ennemi
			String str[] = message.split(" ");
			int id = Integer.parseInt(str[1]);
			int x = Integer.parseInt(str[2]);
			int y = Integer.parseInt(str[3]);
			if (id == idCible) {
				// Si c'est la cible actuelle, on met à jour sa position
				posSignale.x = x;
				posSignale.y = y;
				agent.allerVers(posSignale);
			} else {
				// Sinon, on va regarder si elle est plus proche que pour le
				// signalement de la cible actuelle.
				Point p = new Point(x, y);

				double distAncienneCible = env.distanceVers(
						agent.getPosition(), posSignale);
				double distEnnemi = env.distanceVers(agent.getPosition(), p);
				if (distEnnemi < distAncienneCible) {
					// ELle est plus proche, on va changer notre cible vers
					// celui-ci.
					this.idCible = id;
					this.posSignale = p;
					agent.allerVers(posSignale);
				}
			}
		}
	}

	@Override
	public String getComportement() {
		return etatPrecedent.getComportement();
	}
}
