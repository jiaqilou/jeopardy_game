package action_factory;

import frames.MainGUINetworked;
import game_logic.ServerGameData;
import messages.Message;
import server.Client;

public class NextTeamChooseAction extends Action{

	@Override
	public void executeAction(MainGUINetworked mainGUI, ServerGameData gameData, Message message, Client client) {
		mainGUI.ChangeToNextChoose();
		
	}

}
