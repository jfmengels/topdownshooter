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
	 * Arrete le thread courant.
	 */
	public void terminate() {
		threadRunning = false;
	}

	public Point getPosition() {
		return position;
	}

	public void setPosition(Point position) {
		this.position = position;
	}

	public void setPosition(int x, int y) {
		this.position = new Point(x, y);
	}

	public int getVieActuelle() {
		return vieActuelle;
	}

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
			vieActuelle = 0;
		}
		return estEnVie();
	}

	public boolean estEnVie() {
		return vieActuelle > 0;
	}

	public double getVitesse() {
		return vitesse;
	}

	public Mouvement getMouvement() {
		return mouvement;
	}

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

	public boolean memeEquipe(Agent agent) {
		return this.equipe.getTag() == agent.equipe.getTag();
	}

	public Etat getEtat() {
		return etat;
	}

	public void setEtat(Etat etat) {
		this.etat = etat;
		this.etat.entre(this, environnement);
	}

	public int getId() {
		return id;
	}

	public int getDegats() {
		return degats;
	}

	public void recoitMessage(String message) {
		this.etat.recoitMessage(this, environnement, message);
	}

	public int getPortee() {
		return (int) portee;
	}

	public void init() {
		this.etat.entre(this, environnement);
	}

	public double getOrientation() {
		return orientation;
	}

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
				angle *= -1;
			}
			this.orientation = angle;
		}
	}

	public boolean voitEnnemi() {
		List<Agent> ennemisEnVue = environnement.ennemisEnVue(this);
		boolean ennemi = !ennemisEnVue.isEmpty();
		if (ennemi) {
			Agent cible = ennemisEnVue.get(0);
			this.tirer(cible);
		}
		return ennemi;
	}

	public void tirer(Agent cible) {
		// Si un ennemi est en vue, on va le viser et lui tirer dessus.
		// On attend un moment pour viser et tirer.
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		// Est-ce que l'ennemi est toujours en vue ?
		if (environnement.ennemisEnVue(this).contains(cible)) {
			boolean mort = environnement.tirer(this, cible);
			if (mort) {
				// Si on a tué l'ennemi, on notifie les alliés.
				String message = "kill " + cible.getId();
				this.getEquipe().ecrireTableau(this, message);
			}
		}
	}
}