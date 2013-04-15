package game.agent;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class Mouvement {

	private final Agent agent;
	private final List<Point> destination;
	private Point posDepart;
	private long tempsDepart;
	private long tempsEstime;

	public Mouvement(Agent agent) {
		this.agent = agent;
		this.destination = new ArrayList<Point>();
	}

	public void setDestinations(List<Point> destinations) {
		destination.clear();
		destination.addAll(destinations);
		tempsDepart = System.currentTimeMillis();
		posDepart = agent.getPosition();
		estimeTemps();
		// TODO Changer orientation
	}

	public void setDestination(Point dest) {
		destination.clear();
		destination.add(dest);
		tempsDepart = System.currentTimeMillis();
		posDepart = agent.getPosition();
		estimeTemps();
		// TODO Changer orientation
	}

	/**
	 * Bouge l'agent.
	 */
	public void bouger() {
		long tempsMis;
		boolean changeDirection = false;
		if (destination.size() > 0) {
			tempsMis = System.currentTimeMillis() - tempsDepart;
			while (tempsMis >= tempsEstime) {
				tempsMis -= tempsEstime;
				posDepart = destination.get(0);
				destination.remove(0);
				if (destination.size() > 0) {
					estimeTemps();
					changeDirection = true;
				} else {
					agent.setPosition(posDepart);
					break;
				}
			}
			if (destination.size() > 0) {
				Point dest = destination.get(0);
				int dx = dest.x - posDepart.x;
				int dy = dest.y - posDepart.y;
				double ratio = tempsMis / (double) tempsEstime;
				int x = posDepart.x + (int) (dx * ratio);
				int y = posDepart.y + (int) (dy * ratio);
				agent.setPosition(x, y);
			}
		}
		if (changeDirection) {
			// TODO Changer orientation
		}
	}

	public void estimeTemps() {
		tempsDepart = System.currentTimeMillis();
		Point dest = destination.get(0);
		int dx = dest.x - posDepart.x;
		int dy = dest.y - posDepart.y;
		double distance = Math.sqrt(dx * dx + dy * dy);
		tempsEstime = (long) (distance / agent.getVitesse() * 1000);
	}
}
