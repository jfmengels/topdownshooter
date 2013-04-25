package game.agent.etat;

import game.Environnement;
import game.agent.Agent;

/**
 * Représente et applique un comportement à l'agent.
 * @author Jeroen Engels et Florent Claisse
 */
public interface Etat {

	public static final String compAttaque = "attaque";
	public static final String compDefense = "defense";
	public static final String compAutre = "autre";
	public static final EtatCommun commun = new EtatCommun();

	/**
	 * Rentre dans l'état et initialise des données si nécessaire.
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
	 * Lit un message envoyé.
	 * @param agent Agent dont on représente l'état.
	 * @param env Environnement du jeu.
	 * @param message Message à lire.
	 */
	public void recoitMessage(Agent agent, Environnement env, String message);

	/**
	 * Retourne une description du comportement général de l'état.
	 * @return String
	 */
	public String getComportement();
}
