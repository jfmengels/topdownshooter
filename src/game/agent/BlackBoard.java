package game.agent;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BlackBoard {

	private final List<Agent> agents;
	private final Map<Agent, Point> amiPos;
	private final Map<Agent, Point> enemiPos;

	public BlackBoard() {
		this.agents = new ArrayList<Agent>();
		this.amiPos = new HashMap<Agent, Point>();
		this.enemiPos = new HashMap<Agent, Point>();
	}

	public void write(String message) {
		// TODO Implement blackboard.
	}

}
