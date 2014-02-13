package punch.tudelft.punchie;

import android.app.Activity;
import android.app.Application;

public class PunchApplication extends Application{
	
	private ContentManager contentManager;
	private Contacts contactManager;
	
	public void initialize(Activity mainActivity) {
		contentManager = new ContentManager(this.getBaseContext(), mainActivity);
        contactManager = new Contacts(mainActivity);
	}
	
	public ContentManager getContentManager() {
		return contentManager;
	}
	
	public Contacts getContactManager() {
		return contactManager;
	}
	
}
