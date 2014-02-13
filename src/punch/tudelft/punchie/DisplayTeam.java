package punch.tudelft.punchie;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.app.Activity;

public class DisplayTeam extends Activity {
	
	private ContentManager cm;
	private PunchTeam team;
	private String reference;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_display_team);
		cm = ((PunchApplication)getApplication()).getContentManager();
		reference = this.getIntent().getExtras().getString("reference");
        team = (PunchTeam) cm.getItem(reference);
        displayInfo(team);
	}
	
	private TextView getTextView(String text) {
    	TextView result = new TextView(this);
    	result.setTextAppearance(getApplicationContext(), R.style.itemStyle);
    	result.setText(text);
    	return result;
    }
    
    public void displayInfo(PunchTeam team) {
    	this.setTitle(team.getName());
    	
    	TextView teamTitle = (TextView) findViewById(R.id.teamTitle);
    	teamTitle.setText(team.getName());
    	
    	LinearLayout result = (LinearLayout) findViewById(R.id.result);
    	result.removeAllViews();
    	
		if(team.getTheme() != null) {
			result.addView(getTextView("Theme: " + team.getTheme()));
		}		
		if(team.getYell() != null) {
	    	result.addView(getTextView("Yell: " + team.getYell()));
		}	
		
		LinearLayout playerView = (LinearLayout) findViewById(R.id.players);
		for (PunchMember member : team.getPlayers()) {
			if (member.getName() != null) {
				playerView.addView(getTextView(member.getName()));
			}
		}
		
		LinearLayout trainersView = (LinearLayout) findViewById(R.id.trainers);
		for (PunchMember member : team.getTrainers()) {
			if (member.getName() != null) {
				trainersView.addView(getTextView(member.getName()));
			}
		}
		
		LinearLayout coachesView = (LinearLayout) findViewById(R.id.coaches);
		for (PunchMember member : team.getCoaches()) {
			if (member.getName() != null) {
				coachesView.addView(getTextView(member.getName()));
			}
		}
    }

}
