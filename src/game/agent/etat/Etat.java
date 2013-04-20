package game.agent.etat;

import game.Environnement;
import game.agent.Agent;

public interface Etat {

	/**
	 * Rentre dans l'état.
	 * @param agent Agent dont on représente l'état.
	 * @param env Environnement du jeu.
	 */
	public void entre(Agent agent, Environnement env);

	/**
	 * Réalise l'action en fonction de l'état courant.
	 * @param agent Agent dont on représente l'état.
	 * @param env Environnement du jeu.
	 */
	public void action(Agent agent, Environnement env);

	/**
	 * Lis un message envoyé.
	 * @param message Message à lire.
	 */
	public void recoitMessage(Agent agent, Environnement env, String message);
}
