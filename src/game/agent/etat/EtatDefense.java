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
	 * @var compDemandeId: Identifiant de la demande de comportement.
	 * @var resultatDemande: Compteur de non-attaquants du résultat de la
	 *      demande de comportement.
	 * @var defenseDemandeId: Identifiant de la demande de défense.
	 */
	private long tempsProchainMouvement;
	private Point dest;
	private final Random rand;
	private int compDemandeId;
	private int resultatDemande;
	private int defenseDemandeId;

	public EtatDefense() {
		rand = new Random();
		tempsProchainMouvement = 0;
		compDemandeId = 0;
		resultatDemande = Integer.MIN_VALUE;
		defenseDemandeId = 0;
	}

	@Override
	public void entre(Agent agent, Environnement env) {
		// Ne rien faire.
		System.out.println(agent.getId() + "\tDefense");
		agent.getMouvement().arrete();
		tempsProchainMouvement = 0;
		nvPositionDefense(agent);
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
				// Il faut définir une nouvelle position de défense.
				nvPositionDefense(agent);
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
						agent.allerVers(dest);
					}
				}
			}
		}
	}

	/**
	 * Définit une nouvelle position de défense, et demande aux autres agents si
	 * la position est déjà couverte.
	 * @param agent Agent dont on représente l'état.
	 */
	private void nvPositionDefense(Agent agent) {
		// On choisit une des positions prédéterminées au hasard
		List<Point> posPossibles = agent.getEquipe().getPosDefense();
		this.dest = posPossibles.get(rand.nextInt(posPossibles.size()));
		// Et on dit à l'agent d'y aller.
		agent.allerVers(dest);
		// On va ensuite demander à tous les agents en défense si la position
		// est déjà couverte, ou si elle va bientôt l'être.
		String message;
		synchronized (this) {
			message = "defenseDemande " + agent.getId() + " "
					+ (++defenseDemandeId) + " " + dest.x + " " + dest.y;
		}
		agent.getEquipe().ecrireTableau(agent, message);
	}

	@Override
	public void recoitMessage(Agent agent, Environnement env, String message) {
		if (message.startsWith("voit")) {
			reagitVoit(agent, env, message);
		} else if (message.startsWith("mort")) {
			reagitMort(agent, message);
		} else if (message.startsWith("comp")) {
			reagitComp(agent, message);
		} else if (message.startsWith("defenseDemande")) {
			reagitDefenseDemande(agent, message);
		} else if (message.startsWith("defenseRep")) {
			reagitDefenseRep(agent, message);
		} else {
			commun.recoitMessage(agent, env, message);
		}
	}

	/**
	 * Réagit à une annonce de décès d'un agent ami.
	 * @param agent Agent dont on représente l'état.
	 * @param env Environnement du jeu.
	 * @param message Message à lire.
	 */
	private void reagitVoit(Agent agent, Environnement env, String message) {
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

	/**
	 * Réagit à une annonce de décès d'un agent ami.
	 * @param agent Agent dont on représente l'état.
	 * @param message Message à lire.
	 */
	private void reagitMort(Agent agent, String message) {
		// Si on nous indique la mort d'un agent ami
		String comportement = message.split(" ")[2];
		if (comportement.equals(compAttaque)) {
			// Si c'était un agent en attaque, il faut vérifier s'il ne faut pas
			// basculer en attaque.
			if (agent.getEquipe().getNbAgentsVivants() == 1) {
				// Si on est le seul agent vivant, alors on passe en attaque.
				agent.setEtat(new EtatAttaque());
			} else {
				// Sinon, on va demander s'il reste des agents en attaque
				// (demande de comportement). Ceci a pour but de faire
				// attaquer l'agent si jamais il ne reste plus aucun attaquant
				// en vue.
				String nvMessage;
				synchronized (this) {
					nvMessage = "demandeComp " + agent.getId() + " "
							+ (++compDemandeId);
					resultatDemande = 0;
				}
				agent.getEquipe().ecrireTableau(agent, nvMessage);
			}
		}
	}

	/**
	 * Réagit à une réponse de demande de comportement.
	 * @param agent Agent dont on représente l'état.
	 * @param message Message à lire.
	 */
	private void reagitComp(Agent agent, String message) {
		// On reçoit un message de réponse à la demande de comportement.
		// On va regarder s'il nous est destiné, et s'il correspond à notre
		// dernière demande.
		String str[] = message.split(" ");
		int targetId = Integer.parseInt(str[1]);
		int compId = Integer.parseInt(str[2]);
		if (targetId == agent.getId() && compId == compDemandeId) {
			String comp = str[3];
			synchronized (this) {
				if (compAttaque.equals(comp)) {
					// Si on reçoit un message d'un attaquant, on n'a pas à
					// basculer en attaque, et on peut ignorer les futures
					// réponses. On incrémente simplement l'identifiant de la
					// dernière demande, pour faire comme si les messages futurs
					// étaient pour répondre à une demande périmée.
					compDemandeId++;
					resultatDemande = Integer.MIN_VALUE;
				} else {
					// Un non-attaquant a répondu.
					resultatDemande++;
					if (resultatDemande == agent.getEquipe()
							.getNbAgentsVivants() - 1) {
						// Si tous les agents ont répondu, alors il n'y a pas
						// d'attaquant, et on bascule en attaque.
						agent.setEtat(new EtatAttaque());
					}
				}
			}
		}
	}

	/**
	 * Réagit à une réponde, suite à une demande de duplication de position de
	 * défense.
	 * @param agent Agent dont on représente l'état.
	 * @param message Message à lire.
	 */
	private void reagitDefenseRep(Agent agent, String message) {
		String str[] = message.split(" ");
		int id = Integer.parseInt(str[1]);
		int idDemande = Integer.parseInt(str[2]);
		boolean conflit = Boolean.parseBoolean(str[3]);
		if (id == agent.getId() && idDemande == defenseDemandeId && conflit) {
			nvPositionDefense(agent);
		}
	}

	/**
	 * Réagit à une demande de duplication de position de défense.
	 * @param agent Agent dont on représente l'état.
	 * @param message Message à lire.
	 */
	private void reagitDefenseDemande(Agent agent, String message) {
		String str[] = message.split(" ");
		int id = Integer.parseInt(str[1]);
		int idDemande = Integer.parseInt(str[2]);
		int x = Integer.parseInt(str[3]);
		int y = Integer.parseInt(str[4]);
		boolean aMemeDest = dest.x == x && dest.y == y;
		String nvMessage = "defenseRep " + id + " " + idDemande + " "
				+ aMemeDest;
		agent.getEquipe().ecrireTableau(agent, nvMessage);
	}

	@Override
	public String getComportement() {
		return compDefense;
	}
}
