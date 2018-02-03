package frames;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import game_logic.Jeo_Timer;
import game_logic.ServerGameData;
import game_logic.User;
import listeners.NetworkedWindowListener;
import messages.PlayerLeftMessage;
import messages.RestartGameMessage;
import messages.TimeforOtherChooseQuesMessage;
import other_gui.AppearanceConstants;
import other_gui.AppearanceSettings;
import other_gui.FinalJeopardyGUINetworked;
import other_gui.QuestionGUIElement;
import other_gui.QuestionGUIElementNetworked;
import other_gui.TeamGUIComponents;
import other_gui.clock;
import server.Client;

//used only for a networked game and inherits from MainGUI
public class MainGUINetworked extends MainGUI {
	private Jeo_Timer jt;
	private Client client;
	//has a networked game data
	private ServerGameData serverGameData;
	//had a networked FJ panel that I need as a memeber variable
	private FinalJeopardyGUINetworked fjGUI;
	private int numSec;
	private JLabel currentTeamLabel;
	private clock ani1;
	private clock ani2;
	private clock ani3;
	private clock ani4;
	private JLabel time;
	
	public MainGUINetworked(ServerGameData gameData, Client client, User loggedInUser) {
		super(loggedInUser);
		this.serverGameData = gameData;
		this.client = client;
		//calls a method in MainGUI that basically acts as a constructor
		//since you can only call the super class's constructor as the first line of the child constructor,
		//but I need to have serverGameData initialized before I can cosntruct my GUI, this is the solution
		make(gameData);
	}
	
	public FinalJeopardyGUINetworked getFJGUI(){
		return fjGUI;
	}
	
	//disables all enabled buttons to have enabled icon
	public void disableAllButtons(){
		for (QuestionGUIElement question : gameData.getQuestions()){
			if (!question.isAsked()){
				question.getGameBoardButton().setDisabledIcon(QuestionGUIElement.getEnabledIcon());
				question.getGameBoardButton().setEnabled(false);
			}
		}
	}
	
	@Override
	protected JPanel createProgressPanel() {
		// create panels
		JPanel pointsPanel = new JPanel(new GridLayout(gameData.getNumberOfTeams(), 3));
		JPanel southEastPanel = new JPanel(new BorderLayout());
		JPanel eastPanel = new JPanel();
		// other local variables
		JLabel updatesLabel = new JLabel("Game Progress");
		JScrollPane updatesScrollPane = new JScrollPane(updatesTextArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		// setting appearances
		AppearanceSettings.setBackground(AppearanceConstants.lightBlue, southEastPanel, updatesLabel, updatesScrollPane,
				updatesTextArea);
		AppearanceSettings.setSize(400, 400, pointsPanel, updatesScrollPane);
		AppearanceSettings.setTextComponents(updatesTextArea);

		updatesLabel.setFont(AppearanceConstants.fontLarge);
		pointsPanel.setBackground(Color.darkGray);
		updatesLabel.setBorder(BorderFactory.createLineBorder(Color.darkGray));
		updatesScrollPane.setBorder(null);

		updatesTextArea.setText("Welcome to Jeopardy!");
		updatesTextArea.setFont(AppearanceConstants.fontSmall);
		updatesTextArea.append("The team to go first will be " + gameData.getCurrentTeam().getTeamName());

		eastPanel.setLayout(new BoxLayout(eastPanel, BoxLayout.PAGE_AXIS));
		// adding components/containers
		JPanel propanel = new JPanel ();
		propanel.setLayout(new BoxLayout(propanel,BoxLayout.X_AXIS));
		updatesLabel.setBorder(null);
		propanel.add(updatesLabel);
		
		currentTeamLabel = new JLabel (client.getUser().getUsername(),JLabel.CENTER);
		currentTeamLabel.setFont(AppearanceConstants.fontMedium);
		propanel.add(currentTeamLabel);
		southEastPanel.add(propanel, BorderLayout.NORTH);
		southEastPanel.add(updatesScrollPane, BorderLayout.CENTER);
		AppearanceSettings.setBackground(AppearanceConstants.lightBlue,propanel,currentTeamLabel,updatesLabel);
		// adding team labels, which are stored in the TeamGUIComponents class,
		// to the appropriate panel
		for (int i = 0; i < gameData.getNumberOfTeams(); i++) {
			TeamGUIComponents team = gameData.getTeamDataList().get(i);
			pointsPanel.add(team.getMainTeamNameLabel());
			if ( i == 0){
				ani1 = new clock ();
				pointsPanel.add(ani1);
			}
			else if (i == 1) {
				ani2 = new clock ();
				pointsPanel.add(ani2);
			}
			else if (i == 2) {
				ani3 = new clock ();
				pointsPanel.add(ani3);
			}
			else if (i == 3) {
				ani4 = new clock ();
				pointsPanel.add(ani4);
			}
			
			pointsPanel.add(team.getTotalPointsLabel());
		}
		setClock ();
		eastPanel.add(pointsPanel);
		eastPanel.add(southEastPanel);

		return eastPanel;
	}
	
	public void updatecurrentTeamLabel(String name) {
		currentTeamLabel.setText(name);
	}
	
	public void stopClock() {
		jt.stopTimer();
	}
	
	public void restartClock () {
		numSec= 15 ;
		time.setText(": "+ numSec);
		jt.restartTimer();
		setClock ();
	}
	
	public void disableAllClocks() {
		if (ani1 != null) {
			ani1.setVisible(false);
		}
		if (ani2 != null) {
			ani2.setVisible(false);
		}
		if (ani3 != null) {
			ani3.setVisible(false);
		}
		if (ani4 != null) {
			ani4.setVisible(false);
		}
	}
	
	public void setClock () {
		if (ani1 != null) {
			ani1.setVisible(false);
		}
		if (ani2 != null) {
			ani2.setVisible(false);
		}
		if (ani3 != null) {
			ani3.setVisible(false);
		}
		if (ani4 != null) {
			ani4.setVisible(false);
		}
		for (int i = 0; i < gameData.getNumberOfTeams(); i++) {
			TeamGUIComponents team = gameData.getTeamDataList().get(i);
			if (team.getTeamName().equals(serverGameData.getCurrentTeam().getTeamName())) {
				if ( i == 0){
					ani1.setVisible(true);
				}
				else if (i == 1) {
					ani2.setVisible(true);
				}
				else if (i == 2) {
					ani3.setVisible(true);
				}
				else if (i == 3) {
					ani4.setVisible(true);
				}
			}
		}
	}
	
	public void setClockwithTeamIndex (int index) {
		if (ani1 != null) {
			ani1.setVisible(false);
		}
		if (ani2 != null) {
			ani2.setVisible(false);
		}
		if (ani3 != null) {
			ani3.setVisible(false);
		}
		if (ani4 != null) {
			ani4.setVisible(false);
		}
		for (int i = 0; i < gameData.getNumberOfTeams(); i++) {
			if (i == index) {
				if ( i == 0){
					ani1.setVisible(true);
				}
				else if (i == 1) {
					ani2.setVisible(true);
				}
				else if (i == 2) {
					ani3.setVisible(true);
				}
				else if (i == 3) {
					ani4.setVisible(true);
				}
			}
		}
	}
	
	public void updateCurrentTeamLabel() {
		currentTeamLabel.setText(serverGameData.getCurrentTeam().getTeamName());
	}
	
	//enables all questions that have not been chosen
	public void enableAllButtons(){
		for (QuestionGUIElement question : gameData.getQuestions()){
			if (!question.isAsked()){
				question.getGameBoardButton().setIcon(QuestionGUIElement.getEnabledIcon());
				question.getGameBoardButton().setEnabled(true);
			}
		}
	}
	
	//depending on whether the current team is same at the client's team index, we enable or disable all buttons
	//override showMainPanel from super class in order to always check if we should enable.disbale buttons
	@Override
	public void showMainPanel() {
		if (gameData.getCurrentTeam().getTeamIndex() != client.getTeamIndex()) disableAllButtons();
		
		else enableAllButtons();
		
		super.showMainPanel();
	}
	
	//override from super class; only add the restart option if the client is the host
	@Override
	protected void createMenu() {

		if (client.isHost()){
			menu.add(restartThisGameButton);
		}
	
		menu.add(chooseNewGameFileButton);
		menu.add(logoutButton);
		menu.add(exitButton);
		menuBar.add(menu);
		setJMenuBar(menuBar);
	}
	
	@Override
	protected JPanel createJeopardyPanel() {
		jeopardyPanel = new JPanel();
		JLabel jeopardyLabel = new JLabel("Jeopardy");
		AppearanceSettings.setBackground(AppearanceConstants.lightBlue, jeopardyPanel, jeopardyLabel);
		jeopardyLabel.setFont(AppearanceConstants.fontLarge);
		jeopardyPanel.add(jeopardyLabel);
		
		numSec = 15;
		time = new JLabel (": 15");
		jeopardyPanel.add(time);
		ActionListener al = new ActionListener () {

			@Override
			public void actionPerformed(ActionEvent e) {
				numSec -- ;
				time.setText(": "+ numSec);
				
				if (numSec <=0) {
					numSec = 15;
					//serverGameData.nextTurn();
					if (client.isHost()) {
						client.sendMessage(new TimeforOtherChooseQuesMessage());
					}
				}
			}
			
		};
		jt = new Jeo_Timer (15000, al);
		return jeopardyPanel;
	}
	
	public void ChangeToNextChoose() {
		serverGameData.nextTurn();
		addUpdate("time out for choosing question. It is "+serverGameData.getCurrentTeam().getTeamName()+"'s turn to choose a question.");
		updateCurrentTeamLabel();
		setClock() ;
		if (serverGameData.getCurrentTeam().getTeamIndex() != client.getTeamIndex()) {
			disableAllButtons();
		}
		else {
			enableAllButtons();
		}
	}
	
	//in the non networked game, this logic happens in the AnsweringLogic class in the QuestionGUIElement
	//but we need to be able to call this from QuestionAnsweredAction class
	public void startFinalJeopardy(){
		gameData.disableRemainingButtons();
		addUpdate("It's time for Final Jeopardy!");
		gameData.determineFinalists();
		//if no one made it show the main panel and show the rating window
		if (gameData.getFinalistsAndEliminatedTeams().getFinalists().size() == 0){
			showMainPanel();
			new WinnersAndRatingGUINetworked(serverGameData, this, client, true).setVisible(true);
		}
		else{
			//if this client did not make it to FJ, show the rating window
			if (gameData.getFinalistsAndEliminatedTeams().getElimindatedTeamsIndices().contains(client.getTeamIndex())){
				showMainPanel();
				client.setElimindated(true);
				new WinnersAndRatingGUINetworked(serverGameData, this, client, false).setVisible(true);
			}
			// create and store a networked fjpanel and switch to it
			else{
				fjGUI = new FinalJeopardyGUINetworked(serverGameData, this, client);
				changePanel(fjGUI);
			}
		}

	}
	
	//sets the bet for the provided team with the provided bet amount, called from SetBetAction class
	public void setBet(int team, int bet){
		TeamGUIComponents teamData = serverGameData.getTeam(team);
		teamData.setBet(bet);
		fjGUI.updateTeamBet(teamData);
	}
	
	//since we serialize over the gameData with all GUI objects transient, we need to repopulate them on the client side
	//we override this from the super class in order to add different action listeners to the question object
	//and so we can iterate over the networked questions instead
	@Override
	protected void populateQuestionButtons(){
		for (int x = 0; x<QUESTIONS_LENGTH_AND_WIDTH; x++){
			for (int y = 0; y<QUESTIONS_LENGTH_AND_WIDTH; y++){
				QuestionGUIElementNetworked question = serverGameData.getNetworkedQuestions()[x][y];
				question.setClient(client, gameData.getNumberOfTeams());
				question.addActionListeners(this, serverGameData);
				questionButtons[question.getX()][question.getY()] = question.getGameBoardButton();
			}
		}

	}
	// adding event listeners, override from MainGUI
	@Override
	protected void addListeners() {

		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		//add window listener
		addWindowListener(new NetworkedWindowListener(client, MainGUINetworked.this));
		//add action listeners
		exitButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				sendPlayerLeftMessage();
				jt.stopTimer();
				System.exit(0);
			}
		});

		restartThisGameButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				//choose a different team to start the game
				gameData.chooseFirstTeam();
				client.sendMessage(new RestartGameMessage(gameData.getCurrentTeam().getTeamIndex()));
			}
		});

		chooseNewGameFileButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				sendPlayerLeftMessage();
				jt.stopTimer();
				new StartWindowGUI(loggedInUser).setVisible(true);
			}
		});
		
		logoutButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				sendPlayerLeftMessage();
				jt.stopTimer();
				new LoginGUI();
			}
		});
	}
	
	private void sendPlayerLeftMessage(){
		client.sendMessage(new PlayerLeftMessage(client.getTeamIndex()));
		client.close();
		jt.stopTimer();
		dispose();
	}

}
