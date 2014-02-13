package punch.tudelft.punchie;

import java.util.ArrayList;

public class PunchCommittee extends PunchItem 
{
	
	private ArrayList<PunchMember> members;

	public PunchCommittee(String ref, String nm) {
		super(ref, nm, "committee");
		members = new ArrayList<PunchMember>();	
	}
	
	public ArrayList<PunchMember> getMembers() {
		return members;
	}
	
	public void setMembers(ArrayList<PunchMember> list) {
		members = list;
	}
	
	public void addMember(PunchMember member) {
		members.add(member);
	}

}
