package action_factory;

import frames.MainGUINetworked;
import game_logic.ServerGameData;
import messages.Message;
import server.Client;

public class NoBuzzInAction extends Action{

	@Override
	public void executeAction(MainGUINetworked mainGUI, ServerGameData gameData, Message message, Client client) {
		client.getCurrentQuestion().stopTimer();
		//check with the game data if we are ready for FJ, if so have the mainGUI set it up
		if (gameData.readyForFinalJeoaprdy()){
			mainGUI.startFinalJeopardy();
		}
		//if not ready, move on to another question
		else{
			//add update to Game Progress, determine whether the game board buttons should be enabled or disabled, and change the main panel
			mainGUI.addUpdate("No one buzzed in in time. It is "+gameData.getCurrentTeam().getTeamName()+"'s turn to choose a question.");
			mainGUI.addUpdate("the correct answer is "+ client.getCurrentQuestion().getAnswer());
			if (gameData.getCurrentTeam().getTeamIndex() != client.getTeamIndex()) mainGUI.disableAllButtons();
			mainGUI.restartClock();
			mainGUI.showMainPanel();
		}
		
	}

}
