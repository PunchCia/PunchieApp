package punch.tudelft.punchie;

import android.os.Bundle;
import android.app.Activity;
import android.text.util.Linkify;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class DisplayMember extends Activity {
	
	private ContentManager cm;
	private Contacts contactManager;
	private PunchMember member;
	private PhoneContact contact;
	private String reference;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_member);
        cm = ((PunchApplication)getApplication()).getContentManager();
        contactManager = ((PunchApplication)getApplication()).getContactManager();
        reference = this.getIntent().getExtras().getString("reference");
        member = (PunchMember) cm.getItem(reference);
        displayInfo(member);
        contact = contactManager.getContact(member);
        displayContactInfo(contact);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_display_member, menu);
        return true;
    }
    
    private TextView getTextView(String text) {
    	TextView result = new TextView(this);
    	result.setTextAppearance(getApplicationContext(), R.style.itemStyle);
    	result.setText(text);
    	return result;
    }
    
    public void displayInfo(PunchMember member) {
    	this.setTitle(member.getName());
    	
    	TextView memberTitle = (TextView) findViewById(R.id.memberTitle);
    	memberTitle.setText(member.getName());
    	
    	LinearLayout result = (LinearLayout) findViewById(R.id.result);
    	result.removeAllViews();
    	
		if(member.getNevNr() != null) {
			result.addView(getTextView("Nevobo nummer: " + member.getNevNr()));
		}		
		if(member.getTeam() != null) {
	    	result.addView(getTextView("Team: " + member.getTeam().getName()));
		}	
		if(!member.getObjection()) {
			
			if(member.getMobileNr() != null) {
				TextView phone = getTextView("Mobiel nummer: " + member.getMobileNr());
				Linkify.addLinks(phone, Linkify.PHONE_NUMBERS);
				result.addView(phone);
			}
			if(member.getEmail() != null) {
				TextView email = getTextView("Email: " + member.getEmail());
				Linkify.addLinks(email, Linkify.EMAIL_ADDRESSES);
				result.addView(email);
			}
			if(member.getAddress() != null) {
				TextView adres = getTextView("Adres: " + member.getAddress());
				Linkify.addLinks(adres, Linkify.MAP_ADDRESSES);
				result.addView(adres);
			}
		} else {
			result.addView(getTextView("Deze persoon heeft aangegeven bezwaar te hebben tegen publicatie van zijn of haar contactgegevens."));
		}
    }
    
    public void displayContactInfo(PhoneContact contact) {
    	LinearLayout resultContact = (LinearLayout) findViewById(R.id.resultContact);
    	resultContact.removeAllViews();
    	if(contact != null) {
	    	if(contact.getPhoneNumbers().size() > 0) {
	    		TextView phoneTxt = getTextView("Mobiel nummer: " + contact.getPhoneNumbers().get(0));
	    		Linkify.addLinks(phoneTxt, Linkify.PHONE_NUMBERS);
	    		resultContact.addView(phoneTxt);
	    	}
	    	if(contact.getEmails().size() > 0) {
	    		TextView emailTxt = getTextView("Email: " + contact.getEmails().get(0));
	    		Linkify.addLinks(emailTxt, Linkify.EMAIL_ADDRESSES);
	    		resultContact.addView(emailTxt);
	    	}
    	}
    	if(contact == null || (contact.getPhoneNumbers().size() < 1 && contact.getEmails().size() < 1)) {
    		TextView errorTxt = getTextView("Er is geen info over " + member.getName() + " beschikbaar in je contacten.");
    		resultContact.addView(errorTxt);
    	}
    	if(!member.getObjection()) {
    		if(member.getMobileNr() != null || member.getEmail() != null) {
				Button addContact = new Button(this);
				addContact.setText("Voeg missende gegevens van " + member.getName() + " toe aan contacten");
				addContact.setOnClickListener(addContactListener);
				resultContact.addView(addContact);
			}
    	}
    }
    
    View.OnClickListener addContactListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			if(contact != null) {
				contactManager.updateContact(member, contact, true, true);
			} else {
				contactManager.addContact(member);
			}
		}
	};
}
