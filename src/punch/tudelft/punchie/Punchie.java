package punch.tudelft.punchie;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Punchie extends Activity {
	
	private ContentManager cm;
	private Button smoboButton;
	private String authenticationError = "Please supply valid authentication credentials at the settings page";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((PunchApplication) this.getApplication()).initialize(this);
        setContentView(R.layout.punchie);
        cm = ((PunchApplication) getApplication()).getContentManager();
        Boolean authenticated = cm.authenticationFilled();
        smoboButton = (Button) findViewById(R.id.smoboButton);
        smoboButton.setEnabled(authenticated);
    }
    
    @Override
    public void onResume() {
    	super.onResume();
    	findViewById(R.id.smoboButton).setEnabled(cm.authenticationFilled());
    	if (!cm.authenticationFilled()) {
    		smoboButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.buttondisabled));
    		((TextView) findViewById(R.id.systemMessage)).setText(authenticationError);
    	} else {
    		smoboButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.buttondefault));
    		((TextView) findViewById(R.id.systemMessage)).setText("");
    	}
    }
    
    public void openSmoBoActivity(View view) {
        Intent intent = new Intent(this, DisplaySmoBoActivity.class);
        startActivity(intent);
    }
    
    public void openSettingsActivity(View view) {
        Intent intent = new Intent(this, DisplaySettingsActivity.class);
        startActivity(intent);
    }

    
}
