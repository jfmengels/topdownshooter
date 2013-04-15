package gui;

import game.Environnement;
import game.agent.Agent;
import game.elements.Decor;

import java.awt.Graphics;

import javax.swing.JPanel;

public class EnvironnementVue extends JPanel {

	private static final long serialVersionUID = 1L;
	private final Environnement env;
	public final long refreshRate = 40; // Rafraîchit toutes les refreshRate ms.

	public EnvironnementVue(Environnement env) {
		this.env = env;
	}

	public void update() {
		this.repaint();
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		for (Decor decor : env.getDecors()) {
			decor.paint(g);
		}
		for (Agent agent : env.getAgents()) {
			agent.paint(g);
		}
	}

	public long getRefreshRate() {
		return refreshRate;
	}
}
