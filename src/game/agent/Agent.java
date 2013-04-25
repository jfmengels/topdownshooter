package game.agent;

import game.Environnement;
import game.agent.etat.Etat;
import game.agent.etat.EtatAttribution;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.util.List;
import java.util.Random;

import display.IDessinable;

/**
 * Représentation d'un agent.
 * @author Jeroen Engels et Florent Claisse
 */
public class Agent implements Runnable, IDessinable {

	/**
	 * Informations sur l'agent et son environnement.
	 * @var idCount: Permet d'attribuer un identifiant différent à chaque agent.
	 * @var id: Identifiant de l'agent.
	 * @var equipe: Equipe à laquelle l'agent appartient.
	 * @var environnement: Environnement dans lequel l'agent évolue.
	 * @var etat: Etat dans lequel l'agent se trouve, et qui va dicter son
	 *      comportement.
	 */
	private static int idCount;
	private final int id;
	private final Equipe equipe;
	private final Environnement environnement;
	private Etat etat;

	/**
	 * Attributs / capacités de l'agent
	 * @var vieMax: Points de vie que possède l'agent s'il a tous ses points de
	 *      vie.
	 * @var vieActuelle: Points de vie qu'il a en ce moment.
	 * @var vitesse: Vitesse de déplacement, en pixels par seconde.
	 * @var portee: Distance à laquelle il voit, en pixels.
	 * @var degats: Nombre de points de vie qu'il enlève lorsqu'il touche une
	 *      cible.
	 */
	private final int vieMax;
	private int vieActuelle;
	private final double vitesse;
	private final int portee;
	private final int degats;

	/**
	 * Informations sur la position et l'orientation de l'agent.
	 * @var mouvement: Va gérer le déplacement (et le réorientation automatique)
	 *      de l'agent.
	 * @var position: Position actuelle de l'agent, de la forme (x,y), en
	 *      pixels.
	 * @var orientation: Où est-ce que l'agent regarde, en radians.
	 */
	private final Mouvement mouvement;
	private Point position;
	private double orientation;

	/**
	 * Données liés à l'exécution du thread.
	 * @var threadRunning: true si le thread tourne, false s'il doit s'arrêter.
	 */
	private boolean threadRunning;

	/**
	 * Constantes pour le paramétrage de l'agent.
	 * @var VIEMAX VIEMIN: Bornes pour le nombre de points de vie.
	 * @var VITESSEMAX VITESSEMIN: Bornes pour la vitesse.
	 * @var PORTEEMAX PORTEEMIN: Bornes pour la portée.
	 * @var DEGATSMAX DEGATSMIN: Bornes pour le nombre de dégâts.
	 * @var ANGLE: Taille de l'angle de vue, en radians.
	 */
	public static final int VIEMAX = 30;
	public static final int VIEMIN = 29;
	public static final int VITESSEMAX = 40;
	public static final int VITESSEMIN = 30;
	public static final int PORTEEMAX = 150;
	public static final int PORTEEMIN = 100;
	public static final int DEGATSMAX = 31;
	public static final int DEGATSMIN = 30;
	public static final double ANGLE = Math.PI / 4;

	/**
	 * Créé un nouvel agent.
	 * @param equipe Equipe auquel l'agent appartient.
	 * @param position Position de départ de l'agent.
	 * @param env Environnement dans lequel l'agent va évoluer.
	 */
	public Agent(Equipe equipe, Point position, Environnement env) {
		synchronized (Agent.class) {
			// Attribution d'un identifiant.
			this.id = idCount++;
		}
		this.equipe = equipe;
		this.position = position;
		this.environnement = env;

		equipe.addAgent(this);

		this.mouvement = new Mouvement(this);

		// L"agent commence dans l'état attribution, qui a pour but de
		// déterminer son futur rôle.
		this.etat = new EtatAttribution();

		// Détermination aléatoire des valeurs des attributs.
		Random rand = new Random();
		this.vieMax = rand.nextInt(VIEMAX - VIEMIN) + VIEMIN;
		this.vieActuelle = this.vieMax;
		this.vitesse = rand.nextInt(VITESSEMAX - VITESSEMIN) + VITESSEMIN;
		this.portee = rand.nextInt(PORTEEMAX - PORTEEMIN) + PORTEEMIN;
		this.degats = rand.nextInt(DEGATSMAX - DEGATSMIN) + DEGATSMIN;
		this.orientation = 0;
	}

	@Override
	public void run() {
		threadRunning = true;
		while (threadRunning) { // Si on peut toujours
			this.etat.action(this, environnement);
		}
	}

	/**
	 * Arrête le thread courant.
	 */
	public void terminate() {
		threadRunning = false;
	}

	/**
	 * Retourne la position de l'agent.
	 * @return Point
	 */
	public Point getPosition() {
		return position;
	}

	/**
	 * Modifie la position de l'agent.
	 * @param position Nouvelle position
	 */
	public void setPosition(Point position) {
		this.position = position;
	}

	/**
	 * Modifie la position de l'agent.
	 * @param x Position sur l'axe des abscisses.
	 * @param y Position sur l'axe des ordonnées.
	 */
	public void setPosition(int x, int y) {
		this.position = new Point(x, y);
	}

	/**
	 * Retourne le nombre de points de vie actuel de l'agent.
	 * @return int
	 */
	public int getVieActuelle() {
		return vieActuelle;
	}

	/**
	 * Retourne le nombre de points de vie maximum de l'agent.
	 * @return int
	 */
	public int getVieMax() {
		return vieMax;
	}

	/**
	 * Touche l'agent lorsqu'on lui tire dessus.
	 * @param pv Points de vie à retirer.
	 * @return True si l'agent est encore en vie ensuite, false sinon.
	 */
	public boolean toucher(int pv) {
		vieActuelle -= pv;
		if (vieActuelle < 0) {
			// Si l'agent meurt, il va prévenir les autres de son équipe.
			vieActuelle = 0;
			String message = "mort " + getId() + " " + etat.getComportement();
			equipe.ecrireTableau(this, message);
		}
		return estEnVie();
	}

	/**
	 * Indique si l'agent est en vie.
	 * @return true si l'agent est en vie, false sinon.
	 */
	public boolean estEnVie() {
		return vieActuelle > 0;
	}

	/**
	 * Retourne la vitesse de l'agent.
	 * @return vitesse
	 */
	public double getVitesse() {
		return vitesse;
	}

	/**
	 * Retourne le gestionnaire de mouvement de l'agent.
	 * @return {@link Mouvement}
	 */
	public Mouvement getMouvement() {
		return mouvement;
	}

	/**
	 * Retourne l'équipe à laquelle appartient l'agent.
	 * @return {@link Equipe}
	 */
	public Equipe getEquipe() {
		return equipe;
	}

	@Override
	public void paint(Graphics g) {
		if (!estEnVie()) {
			g.setColor(equipe.getCouleur());
			g.drawLine(position.x - 6, position.y - 6, position.x + 6,
					position.y + 6);
			g.drawLine(position.x + 6, position.y - 6, position.x - 6,
					position.y + 6);
		} else {
			final int tailleBarre = 31;
			double angle1, angle2;
			g.setColor(equipe.getCouleur());
			g.fillOval(position.x - 2, position.y - 2, 5, 5);
			angle1 = orientation - ANGLE;
			g.drawLine(position.x, position.y, (int) (position.x + portee
					* Math.cos(angle1)),
					(int) (position.y + portee * Math.sin(-angle1)));
			angle2 = orientation + ANGLE;
			g.drawLine(position.x, position.y, (int) (position.x + portee
					* Math.cos(angle2)),
					(int) (position.y + portee * Math.sin(-angle2)));
			g.drawArc(position.x - portee, position.y - portee, portee * 2,
					portee * 2, (int) (angle1 * 180 / Math.PI), 90);
			g.setColor(Color.red);
			g.fillRect(position.x - 15, position.y - 10, tailleBarre, 2);
			g.setColor(Color.green);
			double vie = (double) vieActuelle / (double) vieMax;
			g.fillRect(position.x - 15, position.y - 10,
					(int) (tailleBarre * vie), 2);
		}
		g.setFont(new Font(Font.SANS_SERIF, Font.ITALIC, 10));
		g.setColor(Color.black);
		g.drawString(getId() + "", position.x + 3, position.y + 3);
	}

	/**
	 * Indique si l'agent est de la même équipe qu'un autre agent.
	 * @param agent Agent dont on veut comparer l'équipe.
	 * @return true si les deux agents sont de la même équipe, false sinon.
	 */
	public boolean memeEquipe(Agent agent) {
		return this.equipe.getTag().equals(agent.equipe.getTag());
	}

	/**
	 * Retourne l'état courant de l'agent.
	 * @return {@link Etat}
	 */
	public Etat getEtat() {
		return etat;
	}

	/**
	 * Modifie l'état de l'agent.
	 * @param etat Nouvel état.
	 */
	public void setEtat(Etat etat) {
		this.etat = etat;
		this.etat.entre(this, environnement);
	}

	/**
	 * Retourne l'identifiant unique de l'agent.
	 * @return int
	 */
	public int getId() {
		return id;
	}

	/**
	 * Retourne le nombre de dégâts qu'inflige l'agent.
	 * @return int
	 */
	public int getDegats() {
		return degats;
	}

	/**
	 * Reçoit et intérprète un message.
	 * @param message Message reçu.
	 */
	public void recoitMessage(String message) {
		this.etat.recoitMessage(this, environnement, message);
	}

	/**
	 * Retourne la portée dont dispose l'agent.
	 * @return int
	 */
	public int getPortee() {
		return (int) portee;
	}

	/**
	 * Initialise l'agent, notamment son état.
	 */
	public void init() {
		this.etat.entre(this, environnement);
	}

	/**
	 * Retourne l'orientation de l'agent.
	 * @return Orientation, en radians.
	 */
	public double getOrientation() {
		return orientation;
	}

	/**
	 * Change l'orientation de l'agent.
	 * @param destination Point vers lequel l'agent doit se tourner.
	 */
	public void setOrientation(Point destination) {
		double hypo;
		double adja;

		hypo = Math.sqrt((position.x - destination.x)
				* (position.x - destination.x) + (position.y - destination.y)
				* (position.y - destination.y));
		adja = destination.x - position.x;
		if (hypo != 0) {
			double cos = adja / hypo;
			double angle = Math.acos(cos);
			if (destination.y > position.y) {
				angle = -angle;
			}
			this.orientation = angle;
		}
	}

	/**
	 * Regarde s'il y a des ennemis en vue, et si oui, réagit à leur présence.
	 * @return true si il y a au moins un ennemi en vue, false sinon.
	 */
	public boolean voitEnnemi() {
		List<Agent> ennemisEnVue = environnement.ennemisEnVue(this);
		boolean ennemiPresent = !ennemisEnVue.isEmpty();
		if (ennemiPresent) {
			// S'il y a des ennemis en vue, on notifie les alliés de la position
			// de chacun.
			for (Agent ennemi : ennemisEnVue) {
				Point pos = ennemi.getPosition();
				String message = "voit " + ennemi.getId() + " " + pos.x + " "
						+ pos.y;
				equipe.ecrireTableau(this, message);
			}
			Agent cible = ennemisEnVue.get(0);
			this.tirer(cible);
		}
		return ennemiPresent;
	}

	/**
	 * Commence la procédure de tir sur un ennemi.
	 * @param cible Agent sur lequel tirer.
	 */
	public void tirer(Agent cible) {
		// Si un ennemi est en vue, on va le viser et lui tirer dessus.
		// On attend un moment pour tirer.
		this.setOrientation(cible.getPosition());
		this.mouvement.pause();
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// Si l'agent est encore en vie
		if (estEnVie()) {
			// On va de nouveau regarder qui est en vue.
			List<Agent> ennemis = environnement.ennemisEnVue(this);
			// S'il y a des ennemis en vue, on notifie les alliés de la position
			// de chacun.
			for (Agent ennemi : ennemis) {
				Point pos = ennemi.getPosition();
				String message = "voit " + ennemi.getId() + " " + pos.x + " "
						+ pos.y;
				equipe.ecrireTableau(this, message);
			}

			// Est-ce que l'ennemi est toujours en vue ?
			if (ennemis.contains(cible)) {
				boolean mort = environnement.tirer(this, cible);
				if (mort) {
					// Si on a tué l'ennemi, on notifie les alliés.
					String message = "kill " + cible.getId();
					this.equipe.ecrireTableau(this, message);
					if (ennemis.size() == 1) {
						// S'il n'y avait que cet ennemi en vue, on se reoriente
						// comme avant de l'avoir vu.
						this.mouvement.rectifierOrientation();
					}
				}
			} else if (ennemis.isEmpty()) {
				// Si on ne voit plus personne, on se reoriente comme avant
				// d'avoir visé.
				this.mouvement.rectifierOrientation();
			}
		}
	}

	/**
	 * Planifie le déplacement vers un point.
	 * @param dest Point de destination.
	 */
	public void allerVers(Point dest) {
		List<Point> chemin = environnement.cheminVers(getPosition(), dest);
		this.getMouvement().setDestinations(chemin);
	}

	/**
	 * Retourne une représentation textuelle des attributs de l'agent, à but
	 * d'être communiqué entre agents.
	 * @return String
	 */
	public String getAttributs() {
		return "dataAgent " + id + " " + (int) vitesse + " " + portee + " "
				+ degats + " " + vieMax;
	}
}