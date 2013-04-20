package game.agent.etat;

import game.Environnement;
import game.agent.Agent;

public class EtatDefense implements Etat {

	@Override
	public void entre(Agent agent, Environnement env) {
		// Ne rien faire.
		System.out.println(agent.getId() + "\tDefense");
	}

	@Override
	public void action(Agent agent, Environnement env) {
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void recoitMessage(Agent agent, Environnement env, String message) {
		// TODO Auto-generated method stub

	}

}
