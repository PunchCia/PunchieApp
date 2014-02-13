package punch.tudelft.punchie;

import java.util.ArrayList;

public class PunchTeam extends PunchItem {
	
	private String theme;
	private String yell;
	private ArrayList<PunchMember> players;
	private ArrayList<PunchMember> trainers;
	private ArrayList<PunchMember> coaches;
	private PunchMember captain;
	private String gender;
	private int number;
	
	public PunchTeam(String ref, String nm) {
		super(ref,nm,"team");
		players = new ArrayList<PunchMember>();
		trainers = new ArrayList<PunchMember>();
		coaches = new ArrayList<PunchMember>();
		captain = null;
	}
	
	public void setGender(String gen) {
		gender = gen;
	}
	
	public void setNumber(int nr) {
		number = nr;
	}
	
	public void setTheme(String thm) {
		theme = thm;
	}
	
	public void setYell(String yl) {
		yell = yl;
	}
	
	public void addPlayer(PunchMember member) {
		players.add(member);
		if(member.isCaptain())
			captain = member;
	}
	
	public void setPlayers(ArrayList<PunchMember> list) {
		players = list;
	}
	
	public void addTrainer(PunchMember member) {
		trainers.add(member);
	}
	
	public void setTrainers(ArrayList<PunchMember> list) {
		trainers = list;
	}
	
	public void addCoach(PunchMember member) {
		coaches.add(member);
	}
	
	public void setCoaches(ArrayList<PunchMember> list) {
		coaches = list;
	}
	
	public void setCaptain(PunchMember member) {
		captain = member;
	}
	
	public String getGender() {
		return gender;
	}
	
	public int getNumber() {
		return number;
	}
	
	public String getTheme() {
		return theme;
	}
	
	public String getYell() {
		return yell;
	}
	
	public ArrayList<PunchMember> getPlayers() {
		return players;
	}
	
	public ArrayList<PunchMember> getTrainers() {
		return trainers;
	}
	
	public ArrayList<PunchMember> getCoaches() {
		return coaches;
	}
	
	public PunchMember getCaptain() {
		return captain;
	}
}
