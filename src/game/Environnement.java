package game;

import game.agent.Agent;
import game.agent.Equipe;
import game.agent.TagEquipe;
import game.elements.Cible;
import game.elements.Decor;
import game.elements.Mur;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JPanel;

/**
 * Environnement dans lequel évolue des agents.
 * @author Jeroen Engels et Florent Claisse
 */
public class Environnement extends JPanel {

	private static final long serialVersionUID = 1L;

	/**
	 * Données de l'environnement.
	 * @var agents: Les agents présents dans l'environnement.
	 * @var decors: Les décors (murs, cibles...) de l'environnement.
	 * @var equipes: Les différentes équipes présentes.
	 * @var vainqueur: L'équipe gagnante si le jeu est fini, null sinon.
	 */
	private final List<Agent> agents;
	private final List<Decor> decors;
	private final List<Equipe> equipes;
	private Equipe vainqueur;

	/**
	 * Créé un nouvel environnement.
	 */
	public Environnement() {
		this.setBackground(Color.white);
		this.agents = new ArrayList<Agent>();
		this.decors = new ArrayList<Decor>();
		this.equipes = new ArrayList<Equipe>();
		this.vainqueur = null;

		Equipe est = new Equipe(TagEquipe.EST, new Point(100, 100));
		Equipe ouest = new Equipe(TagEquipe.OUEST, new Point(675, 450));

		est.addPosDefense(new Point(70, 190));
		est.addPosDefense(new Point(140, 190));
		est.addPosDefense(new Point(190, 140));
		est.addPosDefense(new Point(190, 70));

		ouest.addPosDefense(new Point(600, 420));
		ouest.addPosDefense(new Point(600, 490));
		ouest.addPosDefense(new Point(630, 410));
		ouest.addPosDefense(new Point(700, 410));

		this.equipes.add(est);
		this.equipes.add(ouest);

		for (Equipe equipe : equipes) {
			decors.add(new Cible(equipe.getTag(), equipe.getPosCible()));
		}
		createWalls();

		// Ajout d'agents dans l'équipe est
		agents.add(new Agent(est, new Point(150, 240), this));
		agents.add(new Agent(est, new Point(300, 100), this));
		agents.add(new Agent(est, new Point(300, 150), this));

		// Ajout d'agents dans l'équipe ouest.
		agents.add(new Agent(ouest, new Point(460, 460), this));
		agents.add(new Agent(ouest, new Point(410, 510), this));
		agents.add(new Agent(ouest, new Point(510, 410), this));
	}

	/**
	 * Créé des décors.
	 */
	private void createWalls() {
		// delimitation walls
		decors.add(new Mur(new Point(0, 0), new Dimension(800, 10), Color.black));
		decors.add(new Mur(new Point(0, 0), new Dimension(10, 600), Color.black));
		decors.add(new Mur(new Point(0, 552), new Dimension(800, 10),
				Color.black));
		decors.add(new Mur(new Point(774, 0), new Dimension(10, 600),
				Color.black));

		// Horizontal
		for (int i = 1; i < 3; i++) {
			for (int j = 0; j < 11; j++) {
				decors.add(new Mur(new Point(10 + j * 70, 10 + 190 * i),
						new Dimension(50, 10), Color.darkGray));
			}
		}
		// Vertical
		for (int i = 1; i < 4; i++) {
			for (int j = 0; j < 20; j++) {
				decors.add(new Mur(new Point(10 + 190 * i, 10 + j * 70),
						new Dimension(10, 50), Color.darkGray));
			}
		}

	}

	/**
	 * Commence l'exécution de l'environnement.
	 */
	public void start() {
		// On initialise les agents pour les informations qui ne peuvent pas
		// être données lors de leur création et qui doivent être prêtes lors de
		// l'exécution du thread.
		for (Agent agent : this.agents) {
			agent.init();
		}

		// On commence les threads.
		for (Agent agent : this.agents) {
			new Thread(agent).start();
		}
	}

	/**
	 * Arrête proprement un thread attribué à agent.
	 * @param agent Agent à arrêter.
	 */
	public void arreteThread(Agent agent) {
		agent.terminate();
	}

	/**
	 * Retourne les agents de l'environnement.
	 * @return Liste d'Agent
	 */
	public List<Agent> getAgents() {
		return agents;
	}

	/**
	 * Retourne la liste des décors de l'environnement (murs, cibles...)
	 * @return List de Decor.
	 */
	public List<Decor> getDecors() {
		return decors;
	}

	/**
	 * Retourne l'équipe opposée.
	 * @param equipe Tag de l'équipe dont on veut connaître l'adversaire.
	 * @return Equipe
	 */
	public Equipe autreEquipe(TagEquipe equipe) {
		return equipe == TagEquipe.EST ? equipes.get(1) : equipes.get(0);
	}

	/**
	 * Retourne l'équipe opposée.
	 * @param equipe Équipe dont on veut connaître l'adversaire.
	 * @return Equipe
	 */
	public Equipe autreEquipe(Equipe equipe) {
		return equipe == equipes.get(0) ? equipes.get(1) : equipes.get(0);
	}

	/**
	 * Tire sur un agent.
	 * @param source Agent tireur.
	 * @param cible Agent ciblé.
	 * @return true si la cible est tuée, false sinon.
	 */
	public boolean tirer(Agent source, Agent cible) {
		// On lui inflige des dégats.
		boolean vivant = cible.toucher(source.getDegats());
		// Si l'agent est mort, on arrête l'exécution de son thread.
		if (!vivant) {
			arreteThread(cible);
			// Si tous les agents de l'équipe sont morts, on arrête la partie.
			if (cible.getEquipe().getNbAgentsVivants() == 0) {
				this.end(source.getEquipe());
			}
		}
		return !vivant;
	}

	/**
	 * Retourne le chemin que doit prendre un agent entre deux points.
	 * @param depart Point de départ.
	 * @param destination Point de destination.
	 * @return List de points intermédiaires.
	 */
	public List<Point> cheminVers(Point depart, Point destination) {
		boolean finished = false;
		Set<Point> aTraiter = new HashSet<>();
		aTraiter.add(destination);
		int[][] cout = new int[800][600];

		// initialisation tableau
		for (int i = 0; i < 800; i++) {
			for (int j = 0; j < 600; j++) {
				cout[i][j] = 1000000; // correspond au maximum de la distance
										// dans l'ecran
			}
		}
		cout[destination.x][destination.y] = 0;
		while (!aTraiter.isEmpty()) {
			// selection du point a traiter le plus proche de la cible
			Point minimum = null;
			for (Point point : aTraiter) {
				if (distance(point, depart) < distance(minimum, depart)) {
					minimum = point;
				}
			}
			aTraiter.remove(minimum);

			// on met a jour la distance minimum des voisin
			for (int i = -1; i <= 1; i++) {
				for (int j = -1; j <= 1; j++) {
					if (!isWalkable(minimum.x + i, minimum.y + j))
						continue;
					if ((minimum.x + i < 0) || (minimum.x + i >= 800))
						continue;
					if ((minimum.y + j < 0) || (minimum.y + j >= 600))
						continue;

					if ((cout[minimum.x][minimum.y] + 1) < cout[minimum.x + i][minimum.y
							+ j]) {
						cout[minimum.x + i][minimum.y + j] = cout[minimum.x][minimum.y] + 1;
						aTraiter.add(new Point(minimum.x + i, minimum.y + j));
					}
				}
			}

			// si on est arrivé, alors on arrete
			if (minimum.equals(depart)) {
				finished = true;
				break;
			}
		}

		if (!finished)
			return null;

		// recupération d'un chemin
		ArrayList<Point> chemin = new ArrayList<>();
		Point currentPoint = depart;
		while (!currentPoint.equals(destination)) {
			chemin.add(currentPoint);
			int x = currentPoint.x, y = currentPoint.y;
			int pixelCost = 1000000;
			for (int i = -1; i <= 1; i++) {
				for (int j = -1; j <= 1; j++) {
					if ((i == 0) && (j == 0))
						continue;
					if (cout[currentPoint.x + i][currentPoint.y + j] < pixelCost) {
						pixelCost = cout[currentPoint.x + i][currentPoint.y + j];
						x = currentPoint.x + i;
						y = currentPoint.y + j;
					}
				}
			}
			currentPoint = new Point(x, y);
		}
		chemin.add(destination);
		// optimisation
		ArrayList<Point> cheminOpti = new ArrayList<>();
		Point src = null, via = null, target;
		for (Point point : chemin) {
			if (src == null) {
				src = point;
				continue;
			}
			if (via == null) {
				via = point;
				continue;
			}
			target = point;
			if (isVisible(src, target)) {
				via = target;
			} else {
				cheminOpti.add(src);
				src = via;
				via = target;
			}
		}
		if (src != null) {
			cheminOpti.add(src);
		}
		cheminOpti.add(destination);
		return cheminOpti;
	}

	/**
	 * Retourne la liste des ennemis que peut voir un agent.
	 * @param agent Agent observateur.
	 * @return List des ennemis visibles.
	 */
	public List<Agent> ennemisEnVue(Agent agentSource) {
		ArrayList<Agent> liste = new ArrayList<>();
		if (agentSource == null) {
			return liste;
		}
		int distance;
		int x1, x2, y1, y2;
		x1 = agentSource.getPosition().x;
		y1 = agentSource.getPosition().y;
		for (Agent agent : agents) {
			if (agentSource.memeEquipe(agent) || !agent.estEnVie()) {
				continue;
			}
			x2 = agent.getPosition().x;
			y2 = agent.getPosition().y;
			distance = (x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2);
			if (distance <= agentSource.getPortee() * agentSource.getPortee()) {
				// gestion cone de vue
				double angle = ((x2 - x1)
						* Math.cos(agentSource.getOrientation()) + (y2 - y1)
						* Math.sin(-agentSource.getOrientation()))
						/ Math.sqrt(distance);
				if (angle < Math.cos(Agent.ANGLE)) {
					continue;
				}
				// gestions murs
				if (isVisible(agentSource.getPosition(), agent.getPosition())) {
					liste.add(agent);
				}
			}
		}

		return liste;
	}

	private boolean isVisible(Point p, Point q) {
		double x, dx = q.x - p.x;
		double y, dy = q.y - p.y;
		int pas = 200;
		for (int i = 0; i < pas; i++) {
			x = p.x + i * dx / pas;
			y = p.y + i * dy / pas;
			if (!isWalkable((int) x, (int) y)) {
				return false;
			}
		}

		return true;
	}

	private boolean isWalkable(int x, int y) {
		int xmin, xmax, ymin, ymax;
		int tolerance = 3;
		Mur mur;
		for (Decor decor : decors) {
			if (decor.getClass() == Mur.class) {
				mur = (Mur) decor;
				xmin = mur.getPosition().x - tolerance;
				ymin = mur.getPosition().y - tolerance;
				xmax = mur.getTaille().width + xmin + 2 * tolerance;
				ymax = mur.getTaille().height + ymin + 2 * tolerance;
				if ((x >= xmin) && (x <= xmax)) {
					if ((y >= ymin) && (y <= ymax)) {
						return false;
					}
				}
			}
		}
		return true;
	}

	/**
	 * Indique la distance entre deux points.
	 * @param p Premier point.
	 * @param q Deuxième point.
	 * @return Le carré de la distance entre p et q.
	 */
	private int distance(Point p, Point q) {
		if (p == null || q == null) {
			return 1000000; // valeur improbable
		}
		// faibles calculs, resultats imprecis
		// return Math.abs(p.x - q.x) + Math.abs(p.y - q.y);

		// calculs lourds, resultats precis
		return (p.x - q.x) * (p.x - q.x) + (p.y - q.y) * (p.y - q.y);
	}

	/**
	 * Retourne l'agent correspondant à l'identifiant..
	 * @param id Identifiant de l'agent recherché.
	 * @return L'agent avec l'id correspondant, null si aucun n'est trouvé.
	 */
	public Agent getAgent(int id) {
		for (Agent agent : this.agents) {
			if (agent.getId() == id) {
				return agent;
			}
		}
		return null;
	}

	/**
	 * Déclare un vainqueur et arrête la simulation.
	 * @param equipe Equipe gagnante.
	 */
	public void end(Equipe equipe) {
		// On déclare le vainqueur
		vainqueur = equipe;
		// On arrêter tous les threads.
		for (Agent agent : this.agents) {
			agent.terminate();
		}
	}

	/**
	 * Indique l'équipe gagnante.
	 * @return Le vainqueur si la partie est finie, null sinon.
	 */
	public Equipe getVainqueur() {
		return vainqueur;
	}
}