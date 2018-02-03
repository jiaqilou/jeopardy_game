package messages;

public class TimeOutChooseQuesMessage implements Message{
	private int team;
	
	public TimeOutChooseQuesMessage(int team){
		this.team = team;
	}
	
	public int getBuzzInTeam(){
		return team;
	}
}
