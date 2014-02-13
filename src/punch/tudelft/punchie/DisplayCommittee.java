package punch.tudelft.punchie;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.app.Activity;

public class DisplayCommittee extends Activity {
	
	private ContentManager cm;
	private PunchCommittee committee;
	private String reference;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_display_committee);
		cm = ((PunchApplication)getApplication()).getContentManager();
		reference = this.getIntent().getExtras().getString("reference");
        committee = (PunchCommittee) cm.getItem(reference);
        displayInfo(committee);
	}
	
	private TextView getTextView(String text) {
    	TextView result = new TextView(this);
    	result.setTextAppearance(getApplicationContext(), R.style.itemStyle);
    	result.setText(text);
    	return result;
    }
    
    public void displayInfo(PunchCommittee committee) {
    	this.setTitle(committee.getName());
    	
    	TextView committeeTitle = (TextView) findViewById(R.id.committeeTitle);
    	committeeTitle.setText(committee.getName());
    	
    	LinearLayout result = (LinearLayout) findViewById(R.id.result);
    	result.removeAllViews();
		
		for (PunchMember member : committee.getMembers()) {
			if (member.getName() != null) {
				result.addView(getTextView(member.getName()));
			}
		}
    }

}
