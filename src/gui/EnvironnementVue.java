package gui;

import game.Environnement;
import game.agent.Agent;
import game.elements.Decor;

import java.awt.Font;
import java.awt.Graphics;

import javax.swing.JPanel;

public class EnvironnementVue extends JPanel {

	private static final long serialVersionUID = 1L;
	private final Environnement env;
	public final long refreshRate = 40; // Rafraîchit toutes les refreshRate ms.

	/**
	 * Créé un affichage graphique.
	 * @param env Environnement à représenter.
	 */
	public EnvironnementVue(Environnement env) {
		this.env = env;
	}

	/**
	 * Met à jour l'affichage.
	 */
	public void update() {
		this.repaint();
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		// Affichage des décors (murs, cibles...).
		for (Decor decor : env.getDecors()) {
			decor.paint(g);
		}

		// Affichage des agents.
		for (Agent agent : env.getAgents()) {
			agent.paint(g);
		}

		// Affichage du vainqueur, si la partie est finie.
		if (env.getVainqueur() != null) {
			String messageVainqueur = env.getVainqueur().getNom().toUpperCase()
					+ " A GAGNE";
			g.setColor(env.getVainqueur().getCouleur());
			g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 70));
			g.drawString(messageVainqueur, 150, 280);
		}
	}

	/**
	 * Retourne la fréquence de rafraîchissement de l'affichage.
	 * @return temps en millisecondes entre deux rafraîchissements.
	 */
	public long getRefreshRate() {
		return refreshRate;
	}
}