package action_factory;

import frames.MainGUINetworked;
import game_logic.ServerGameData;
import messages.Message;
import messages.QuestionAnsweredMessage;
import other_gui.QuestionGUIElementNetworked;
import other_gui.TeamGUIComponents;
import server.Client;

public class TimeOutChooseQuesAction extends Action{
	
	private ServerGameData gameData;
	private MainGUINetworked mainGUI;
	private QuestionGUIElementNetworked currentQuestion;
	private TeamGUIComponents currentTeam;
	private String answer;
	private Client client;
	
	@Override
	public void executeAction(MainGUINetworked mainGUI, ServerGameData gameData, Message message, Client client) {
		this.gameData = gameData;
		this.client = client;
		this.mainGUI = mainGUI;
		currentQuestion = client.getCurrentQuestion();
		currentTeam = currentQuestion.getCurrentTeam();
		
		mainGUI.disableAllClocks();
		questionDone();
		
	}

	private void questionDone(){
		
		currentQuestion.getCurrentTeam().deductPoints(currentQuestion.getPointValue());
		mainGUI.addUpdate(currentTeam.getTeamName()+" did not answer within 20s. $"+currentQuestion.getPointValue()+" will be deducted from their total.");
		//check the current question to see if all teams have had a chance, and if so
		if (currentQuestion.questionDone()){
			//set the next turn to be the team in clockwise order from original team, add an update, and see whether it is time for FJ
			gameData.setNextTurn(gameData.nextTurn(currentQuestion.getOriginalTeam()));
			mainGUI.addUpdate("No one answered the question correctly. The correct answer was: "+currentQuestion.getAnswer());
			checkReadyForFJ();
		}
		//if question has not been attempted by everyone, go to a buzz in period
		else{
			currentQuestion.resetNumSec();
			currentQuestion.setBuzzInPeriod();
			mainGUI.addUpdate("Buzz in to answer!");
		}
	}

	private void checkReadyForFJ(){
		//check with the game data if we are ready for FJ, if so have the mainGUI set it up
		if (gameData.readyForFinalJeoaprdy()){
			mainGUI.startFinalJeopardy();
		}
		//if not ready, move on to another question
		else{
			//add update to Game Progress, determine whether the game board buttons should be enabled or disabled, and change the main panel
			mainGUI.addUpdate("It is "+gameData.getCurrentTeam().getTeamName()+"'s turn to choose a question.");
			
			if (gameData.getCurrentTeam().getTeamIndex() != client.getTeamIndex()) mainGUI.disableAllButtons();
			mainGUI.restartClock();
			mainGUI.showMainPanel();
		}
	}
	
}
	
