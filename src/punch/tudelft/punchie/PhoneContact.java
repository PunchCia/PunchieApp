package punch.tudelft.punchie;

import java.util.ArrayList;

public class PhoneContact {

	private String contactId;
	private String displayName;
	private ArrayList<String> phoneNumbers;
	private ArrayList<String> emails;
	
	public PhoneContact(String id, String name, ArrayList<String> nrs, ArrayList<String> em) {
		contactId = id;
		displayName = name;
		phoneNumbers = nrs;
		emails = em;
	}
	
	public String getContactId() {
		return contactId;
	}
	
	public String getDisplayName() {
		return displayName;
	}
	
	public ArrayList<String> getPhoneNumbers() {
		return phoneNumbers;
	}
	
	public ArrayList<String> getEmails() {
		return emails;
	}
	
}
