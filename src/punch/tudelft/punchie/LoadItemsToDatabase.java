package punch.tudelft.punchie;

import java.util.ArrayList;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;

public class LoadItemsToDatabase extends AsyncTask<Void, Integer, Void> {
	
	private ProgressDialog progressDialog;
	private Activity loadingScreenActivity;
	private ContentManager cm;
	private ArrayList<String> errorMessages;
	
	public LoadItemsToDatabase(Activity activity, ContentManager contentManager) {
		loadingScreenActivity = activity;
		cm = contentManager;
	}
	
	//Before running code in separate thread  
    @Override  
    protected void onPreExecute()  
    {  
        //Create a new progress dialog  
        progressDialog = new ProgressDialog(loadingScreenActivity);  
        //Set the progress dialog to display a horizontal progress bar  
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);  
        //Set the dialog title to 'Loading...'  
        progressDialog.setTitle("Loading...");  
        //Set the dialog message to 'Loading application View, please wait...'  
        progressDialog.setMessage("Downloaden starten...");  
        //This dialog can't be canceled by pressing the back key  
        progressDialog.setCancelable(false);  
        //This dialog isn't indeterminate  
        progressDialog.setIndeterminate(false);  
        //The maximum number of items is 100  
        progressDialog.setMax(300);  
        //Set the current progress to zero  
        progressDialog.setProgress(0);  
        //Display the progress dialog  
        progressDialog.show();  
    }

	@Override
	protected Void doInBackground(Void... arg0) {
		publishSyncProgress(0, "Downloading members...", 100);
		ArrayList<PunchItem> members = cm.getMembers();
		publishSyncProgress(33, "Downloading teams...", -1);
		ArrayList<PunchItem> teams = cm.getTeams();
		publishSyncProgress(66, "Downloading committees...", -1);
		ArrayList<PunchItem> committees = cm.getCommittees();
		publishSyncProgress(0, "Items aan het opslaan in de database...", members.size() + teams.size() + committees.size());
		int counter = 0;
		
		for(PunchItem member : members) {
			try{
				cm.insertItemIntoDB(member);
			} catch (Exception e) {
				errorMessages.add(e.getMessage());
			}
			counter++;
			publishSyncProgress(counter, null, -1);
		}
		
		for(PunchItem team : teams) {
			try {
				cm.insertItemIntoDB(team);
			} catch (Exception e) {
				errorMessages.add(e.getMessage());
			}
			counter++;
			publishSyncProgress(counter, null, -1);
		}
		
		for(PunchItem com : committees) {
			try {
				cm.insertItemIntoDB(com);
			} catch (Exception e) {
				errorMessages.add(e.getMessage());
			}
			counter++;
			publishSyncProgress(counter, null, -1);
		}
		
		return null;
	}
	
	public void publishSyncProgress(final int progress, final String message, final int max) {
		loadingScreenActivity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if(max != -1) {
					progressDialog.setMax(max);
				}
				if(message != null) {
					progressDialog.setMessage(message);
				}
                progressDialog.setProgress(progress);
			}
		});
	}
	
	//after executing the code in the thread  
    @Override  
    protected void onPostExecute(Void result)  
    {  
    	cm.disableAsync();
        //close the progress dialog  
        progressDialog.dismiss();
    }

}
