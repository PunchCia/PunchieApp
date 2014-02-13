package punch.tudelft.punchie;

public class PunchMember extends PunchItem {
	
	private PunchTeam team;
	private String nevoboNummer;
	private Boolean objection;
	private String address;
	private String email;
	private String mobileNr;
	private Boolean captain;
	private String position;
	
	public PunchMember(String ref, String nm) {
		super(ref,nm,"member");
		captain = false;
		objection = true;
	}
	
	public void setTeam(PunchTeam tm) {
		team = tm;
	}
	
	public void setNevNr(String nevNr) {
		nevoboNummer = nevNr;
	}
	
	public void setObjection(Boolean obj) {
		objection = obj;
	}
	
	public void setAddress(String adr) {
		address = adr;
	}
	
	public void setEmail(String e) {
		email = e;
	}
	
	public void setMobileNr(String mob) {
		mobileNr = mob;
	}
	
	public void setCaptain(Boolean cp) {
		captain = cp;
	}
	
	public void setPosition(String pos) {
		position = pos;
	}
	
	public PunchTeam getTeam() {
		return team;
	}
	
	public String getNevNr() {
		return nevoboNummer;
	}
	
	public Boolean getObjection() {
		return objection;
	}
	
	public String getAddress() {
		return address;
	}
	
	public String getEmail() {
		return email;
	}
	
	public String getMobileNr() {
		return mobileNr;
	}
	
	public Boolean isCaptain() {
		return captain;
	}
	
	public String getPosition() {
		return position;
	}
}
