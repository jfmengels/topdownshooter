package game.agent.etat;

import game.Environnement;
import game.agent.Agent;

import java.util.ArrayList;
import java.util.List;

public class EtatOrganisation implements Etat {

	private class Data {
		int id;
		int speed;

		public Data(int id, int speed) {
			this.id = id;
			this.speed = speed;
		}
	}

	private int nbAgents;
	private List<Data> dataList;
	private EtatAttribution attribution;

	public EtatOrganisation() {
		dataList = new ArrayList<EtatOrganisation.Data>();
		attribution = new EtatAttribution();
	}

	@Override
	public void entre(Agent agent, Environnement env) {
		agent.getEquipe().ecrireTableau(agent, "demandeData");
		nbAgents = agent.getEquipe().getAgents().size();

		String message = "dataAgent " + agent.getId() + " "
				+ ((int) agent.getVitesse());
		this.recoitMessage(agent, env, message);
	}

	@Override
	public void action(Agent agent, Environnement env) {
		// Ne rien faire
	}

	@Override
	public void recoitMessage(Agent agent, Environnement env, String message) {
		if (message.startsWith("dataAgent")) {
			String[] dataStr = message.split(" ");
			int id = Integer.parseInt(dataStr[1]);
			int speed = Integer.parseInt(dataStr[2]);
			synchronized (this) {
				insererDonnees(new Data(id, speed));
				if (dataList.size() == nbAgents) {
					attribuerEtats(agent, env);
				}
			}
		} else {
			System.out.println("Autre");
			attribution.recoitMessage(agent, env, message);
		}
	}

	private void insererDonnees(Data data) {
		int index;
		for (index = 0; index < dataList.size(); index++) {
			if (dataList.get(index).speed > data.speed) {
				break;
			}
		}
		dataList.add(index, data);
	}

	private void attribuerEtats(Agent agent, Environnement env) {
		// Le premier tiers le plus lent va être en défense.
		int index;
		for (index = 0; index < dataList.size() / 3; index++) {
			if (dataList.get(index).id == agent.getId()) {
				agent.setEtat(new EtatDefense());
			} else {
				String message = "setEtat " + dataList.get(index).id
						+ " defense";
				agent.getEquipe().ecrireTableau(agent, message);
			}
		}

		// Le reste va être en attaque
		for (; index < dataList.size(); index++) {
			if (dataList.get(index).id == agent.getId()) {
				agent.setEtat(new EtatAttaque());
			} else {
				String message = "setEtat " + dataList.get(index).id
						+ " attaque";
				agent.getEquipe().ecrireTableau(agent, message);
			}
		}

	}
}
