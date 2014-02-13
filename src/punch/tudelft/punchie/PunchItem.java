package punch.tudelft.punchie;

public class PunchItem {

	private String reference;
	private String name;
	private String type;
	
	public PunchItem(String ref, String nm, String tp) {
		reference = ref;
		name = nm;
		type = tp;
	}
	
	public String getReference() {
		return reference;
	}
	
	public String getName() {
		return name;
	}
	
	public String getType() {
		return type;
	}
	
}
