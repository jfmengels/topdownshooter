/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import game.Environnement;

import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JFrame;

/**
 * @author Florent
 */
public class GameWindow extends JFrame {
	private static final long serialVersionUID = 1L;
	private final Environnement env;
	private final EnvironnementVue vue;

	public GameWindow() {
		super("Simulation FPS");
		this.env = new Environnement();
		this.vue = new EnvironnementVue(env);

		this.setSize(800, 600);
		this.setContentPane(vue);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);

		this.start();
	}

	private void start() {
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				vue.update();
			}
		};
		new Timer().schedule(task, vue.getRefreshRate(), vue.getRefreshRate());
		this.env.start();
	}

}
