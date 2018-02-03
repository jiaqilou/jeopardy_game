package other_gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.swing.JLabel;
import javax.swing.JPanel;

import frames.MainGUI;
import game_logic.GameData;
import game_logic.Jeo_Timer;
import messages.BuzzInMessage;
import messages.NoBuzzInMessage;
import messages.QuestionAnsweredMessage;
import messages.QuestionClickedMessage;
import messages.TimeOutChooseQuesMessage;
import messages.TimeforOtherChooseQuesMessage;
import server.Client;

//inherits from QuestionGUIElement
public class QuestionGUIElementNetworked extends QuestionGUIElement {
	private Jeo_Timer jt;
	private int numSec;
	private Client client;
	
	private JPanel aniPanel;
	//very similar variables as in the AnsweringLogic class
	public Boolean hadSecondChance;
	private TeamGUIComponents currentTeam;
	private TeamGUIComponents originalTeam;
	private JLabel timeLabel;
	private JPanel timePanel;
	private clock ani;
	int teamIndex;
	int numTeams;
	
	private boolean InbuzzInPeriod;
	//stores team index as the key to a Boolean indicating whether they have gotten a chance to answer the question
	private HashMap<Integer, Boolean> teamHasAnswered;
	
	public QuestionGUIElementNetworked(String question, String answer, String category, int pointValue, int indexX, int indexY) {
		super(question, answer, category, pointValue, indexX, indexY);
		InbuzzInPeriod = false;
	}
	
	//set the client and also set the map with the booleans to all have false
	public void setClient(Client client, int numTeams){
		this.client = client;
		this.numTeams = numTeams;
		teamIndex = client.getTeamIndex();
		teamHasAnswered = new HashMap<>();
		for (int i = 0; i< numTeams; i++) teamHasAnswered.put(i, false);
		
		ActionListener al = new ActionListener () {
			public void actionPerformed(ActionEvent e) {
				numSec -- ;
				timeLabel.setText(": "+ numSec);
					
				if (numSec <=0) {
					numSec = 20;
					if (ani == null) {
						ani = new clock();
						System.out.println("setclient");
						ani.setBackColor(new Color(0,0,139));
						timePanel.add(ani);	
					}
					if (!ani.isVisible()) {
						ani.setVisible(true);
					}
					try {
						Thread.sleep(200);
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
					if (InbuzzInPeriod) {
						if (client.isHost()) {
							client.sendMessage(new NoBuzzInMessage());
						}		
					}
					else if (client.isHost()) {
						System.out.println("Aaaa");
						//System.out.println(currentTeam.getTeamName());
						//if (client.isHost()) {
							client.sendMessage(new TimeOutChooseQuesMessage(currentTeam.getTeamIndex()));
						//}
					}
				}
			}
				
		};
		jt = new Jeo_Timer (20000, al);
		jt.stopTimer();
	}
	
	public void resetNumSec() {
		numSec = 20;
	}
	
	public void stopTimer () {
		jt.stopTimer();
	}
	
	public void restartTimer () {
		jt.restartTimer();
	}
	
	//returns whether every team has had a chance at answering this question
	public Boolean questionDone(){
		Boolean questionDone = true;
		for (Boolean currentTeam : teamHasAnswered.values()){
			questionDone = questionDone && currentTeam;
		}
		return questionDone;
	}
	
	
	
	//overrides the addActionListeners method in super class
	@Override
	public void addActionListeners(MainGUI mainGUI, GameData gameData){
		
		passButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				//send a buzz in message
				//ani.setVisible(false);
				System.out.println("buzz in");
				wa.setVisible(false);
				System.out.println("buzz in 1");
				client.sendMessage(new BuzzInMessage(teamIndex));
				System.out.println("buzz in 2");
			}
			
		});
		
		gameBoardButton.addActionListener(new ActionListener(){
			
			@Override
			public void actionPerformed(ActionEvent e) {
				//send a question clicked message
				client.sendMessage(new QuestionClickedMessage(getX(), getY()));
			}
		});
		
		submitAnswerButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				//send question answered message
				client.sendMessage(new QuestionAnsweredMessage(answerField.getText()));
			}
			
		});
	}
	
	//override the resetQuestion method
	@Override
	public void resetQuestion(){
		super.resetQuestion();
		hadSecondChance = false;
		currentTeam = null;
		originalTeam = null;
		teamHasAnswered.clear();
		//reset teamHasAnswered map so all teams get a chance to anaswer again
		for (int i = 0; i< numTeams; i++) teamHasAnswered.put(i, false);
	}
	
	@Override
	public void populate(){
		super.populate();
		//ani = new clock();
		//ani.setBackColor(new Color(0,0,139));
		//timePanel.add(ani);	
		passButton.setText("Buzz In!");
	}
	
	@Override
	public void setInfoPanel() {
		numSec = 20;
		infoPanel = new JPanel(new GridLayout(1,4));
		AppearanceSettings.setBackground(AppearanceConstants.darkBlue,infoPanel);
		infoPanel.setPreferredSize(new Dimension(900, 80));
		timePanel = new JPanel();
		timePanel.setLayout(new GridLayout(1,2));
		timeLabel = new JLabel (":20" , JLabel.CENTER);
		AppearanceSettings.setBackground(AppearanceConstants.darkBlue, timeLabel,timePanel);
		AppearanceSettings.setForeground(Color.lightGray, teamLabel, timeLabel);
		AppearanceSettings.setFont(AppearanceConstants.fontLarge,timeLabel);
		
		timePanel.add(timeLabel);
		infoPanel.add(timePanel);
		
		//ani = new clock ();
		//infoPanel.add(ani);
		//ani.setVisible(false);
		
	}
	
	public int getOriginalTeam(){
		return originalTeam.getTeamIndex();
	}

	public void updateAnnouncements(String announcement){
		announcementsLabel.setText(announcement);
	}
	
	public void setOriginalTeam(int team, GameData gameData){
		originalTeam = gameData.getTeamDataList().get(team);
		updateTeam(team, gameData);
	}
	
	//update the current team of this question
	public void updateTeam(int team, GameData gameData){
		if(ani != null ){
			ani.setVisible(false);
		}
		InbuzzInPeriod = false;
		resetNumSec();
		currentTeam = gameData.getTeamDataList().get(team);
		passButton.setVisible(false);
		answerField.setText("");
		//if the current team is this client
		if (team == teamIndex){
			AppearanceSettings.setEnabled(true, submitAnswerButton, answerField);
			announcementsLabel.setText("answer within 20 seconds!");
		}
		//if the current team is an opponent
		else{
			AppearanceSettings.setEnabled(false, submitAnswerButton, answerField);
			announcementsLabel.setText("It's "+currentTeam.getTeamName()+"'s turn to try to answer the question.");
			wa.setVisible(true);
		}
		//mark down that this team has had a chance to answer
		teamHasAnswered.replace(team, true);
		hadSecondChance = false;
		teamLabel.setText(currentTeam.getTeamName());
	}
	
	//called from QuestionAnswerAction when there is an illformated answer
	public void illFormattedAnswer(){
		
		if (currentTeam.getTeamIndex() == teamIndex){
			announcementsLabel.setText("You had an illformatted answer. Please try again");
		}
		else{
			announcementsLabel.setText(currentTeam.getTeamName()+" had an illformatted answer. They get to answer again.");
		}
	}
	
	//set the gui to be a buzz in period, also called from QuestionAnswerAction
	public void setBuzzInPeriod(){
		if (ani == null) {
			ani = new clock();
			System.out.println("setbuzzperiod");
			ani.setBackColor(new Color(0,0,139));
			timePanel.add(ani);
		}
		ani.setVisible(true);
		wa.setVisible(false);
		passButton.setVisible(true);
		teamLabel.setText("");
		InbuzzInPeriod = true;
		if (teamHasAnswered.get(teamIndex)){
			AppearanceSettings.setEnabled(false, submitAnswerButton, answerField, passButton);
			announcementsLabel.setText("It's time to buzz in! But you've already had your chance..");
		}
		else{
			announcementsLabel.setText("Buzz in to answer the question!");
			passButton.setEnabled(true);
			AppearanceSettings.setEnabled(false, submitAnswerButton, answerField);
		}
	}
	
	
	public TeamGUIComponents getCurrentTeam(){
		return currentTeam;
	}
}
