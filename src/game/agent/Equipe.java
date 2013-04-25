package game.agent;

import game.Environnement;
import game.agent.etat.EtatOrganisation;

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Equipe d'agents.
 * @author Jeroen Engels et Florent Claisse
 */
public class Equipe {

	/**
	 * @var agents: Agents présents dans l'équipe.
	 * @var tag: Tag de l'équipe.
	 * @var blackboard: Intermédiaire de communication pour l'équipe.
	 * @var couleur: Couleur de l'équipe, pour l'affichage.
	 * @var posCible: Position de la cible de l'équipe. Celle que les agents de
	 *      l'équipe doivent protéger, pas attaquer.
	 * @var posDefense: Positions stratégiques de défense pour protéger la
	 *      cible.
	 * @var posDefenseOrientation:
	 * @var organisation: Etat organisation dans lequel un état passe pour
	 *      attribuer les rôles aux agents.
	 */
	private final List<Agent> agents;
	private final TagEquipe tag;
	private final BlackBoard blackBoard;
	private final Color couleur;
	private final Point posCible;
	private final List<Point> posDefense;
	private final Map<Point, Point> posDefenseOrientation;
	private final EtatOrganisation organisation;

	/**
	 * Créé une nouvelle équipe.
	 * @param tag tag de l'équipe.
	 * @param cible Position de la cible à protéger.
	 */
	public Equipe(TagEquipe tag, Point cible) {
		this.posCible = cible;
		this.agents = new ArrayList<Agent>();
		this.tag = tag;
		this.blackBoard = new BlackBoard();
		this.posDefense = new ArrayList<Point>();
		this.posDefenseOrientation = new HashMap<Point, Point>();
		this.organisation = new EtatOrganisation();

		// On attribue une couleur en fonction de l'équipe.
		if (tag == TagEquipe.EST) {
			this.couleur = new Color(100, 100, 250);
		} else {
			this.couleur = new Color(250, 100, 100);
		}
	}

	/**
	 * Ecrit un message au reste de l'équipe.
	 * @param source {@link Agent} qui écrit le message.
	 * @param message Message à écrire.
	 */
	public void ecrireTableau(Agent source, String message) {
		this.blackBoard.ecrire(source, message);
	}

	/**
	 * Ajoute un agent à l'équipe.
	 * @param agent {@link Agent} à ajouter.
	 */
	public void addAgent(Agent agent) {
		this.agents.add(agent);
	}

	/**
	 * Retourne le tag de l'équipe.
	 * @return {@link TagEquipe}
	 */
	public TagEquipe getTag() {
		return tag;
	}

	/**
	 * Retourne la couleur représentant l'équipe.
	 * @return {@link Color}
	 */
	public Color getCouleur() {
		return couleur;
	}

	/**
	 * Retourne la position de la cible à protéger.
	 * @return {@link Point}
	 */
	public Point getPosCible() {
		return posCible;
	}

	/**
	 * Retourne les agents de l'équipe.
	 * @return {@link List} d'{@link Agent}
	 */
	public List<Agent> getAgents() {
		return agents;
	}

	/**
	 * Ajoute une position stratégique de défense.
	 * @param pos {@link Point}
	 * @param orientation Point vers lequel regarder au point.
	 */
	public void addPosDefense(Point pos, Point orientation) {
		posDefense.add(pos);
		posDefenseOrientation.put(pos, orientation);
	}

	/**
	 * Retourne la liste des positions stratégiques de défense.
	 * @return {@link List} de {@link Point}
	 */
	public List<Point> getPosDefense() {
		return posDefense;
	}

	/**
	 * Retourne l'orientation
	 * @param pos Position stratégique de défense.
	 * @return Point vers lequel regarder.
	 */
	public Point getOrientationDefense(Point pos) {
		return posDefenseOrientation.get(pos);
	}

	/**
	 * Retourne l'état d'organisation, utilisé pour attribuer les rôles de
	 * l'équipe.
	 * @return {@link EtatOrganisation}
	 */
	public EtatOrganisation getOrganisation() {
		return organisation;
	}

	/**
	 * Retourne le nom de l'équipe.
	 * @return String
	 */
	public String getNom() {
		if (this.tag == TagEquipe.EST) {
			return "Est";
		} else {
			return "Ouest";
		}
	}

	/**
	 * Retourne le nombre d'agents encore vivants de l'équipe.
	 * @return Un nombre dans [0, nombre d'agents].
	 */
	public int getNbAgentsVivants() {
		int count = 0;
		for (Agent agent : this.agents) {
			if (agent.estEnVie()) {
				count++;
			}
		}
		return count;
	}

	/**
	 * Notifie de la fin du jeu.
	 * @param env Environnement de la simulation.
	 */
	public void end(Environnement env) {
		env.end(this);
	}
}
