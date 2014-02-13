package punch.tudelft.punchie;

import java.util.HashMap;

import android.os.Bundle;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

public class DisplaySettingsActivity extends Activity {

	private ContentManager cm;
	private TextView settingsMessage;
	private EditText userField;
	private EditText passField;
	private CheckBox offSmobo;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_display_settings);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        cm = ((PunchApplication)getApplication()).getContentManager();
        settingsMessage = (TextView) findViewById(R.id.settingsMessage);
        userField = (EditText) findViewById(R.id.usernameSet);
        passField = (EditText) findViewById(R.id.passwordSet);
        offSmobo = (CheckBox) findViewById(R.id.smoboOffline);
        fillFields();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_display_settings, menu);
        return true;
    }
    
    private void fillFields() {
        String username = cm.getSetting("username"); 
        String password = cm.getSetting("password");
        String offlineSmobo = cm.getSetting("offSmobo");
        if(username != null) {
        	userField.setText(username);
        }
        if(password != null) {
        	passField.setText(password);
        }
        offSmobo.setChecked(offlineSmobo.equals("true"));
    }
    
    public void saveSettings(View view) {
    	// retrieve field values
    	String username = userField.getText().toString();
    	String password = passField.getText().toString();
    	String offlineSmobo = "false";
    	Boolean isChecked = this.offSmobo.isChecked();
    	if(isChecked) {
    		offlineSmobo = "true";
    	}
    	
    	HashMap<String, String> settings = new HashMap<String, String>();
    	
    	settings.put("username", username);
    	settings.put("password", password);
    	settings.put("offSmobo", offlineSmobo);
    	
    	if(cm.saveSettings(this, settings)) {
    		settingsMessage.setText("Instellingen opgeslagen!");
    	} else {
    		settingsMessage.setText("Er is een fout opgetreden tijdens het opslaan van de instellingen!");
    	}
    	fillFields();
    }

    
}
