package punch.tudelft.punchie;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;

public class ContentManager {

	private Activity mainActivity;
	private Context cmContext;
	private PunchDB db;
	private PunchConnection connection;
	private String username;
	private String password;
	
	private Boolean async = false;
	private LinkedHashMap<String,PunchItem> itemMapping;
	private ArrayList<PunchItem> members;
	private ArrayList<PunchItem> teams;
	private ArrayList<PunchItem> committees;
	
	public ContentManager(Context context, Activity activity) {
		cmContext = context;
		mainActivity = activity;
		db = new PunchDB(cmContext);
		username = db.getSetting("username");
		password = db.getSetting("password");
		connection = new PunchConnection(username, password, mainActivity);
		itemMapping = new LinkedHashMap<String,PunchItem>();
	}
	
	public PunchConnection getConnection() {
		return connection;
	}
	
	public String getSetting(String key) {
		return db.getSetting(key);
	}
	
	public Boolean insertSetting(String key, String value) {
		return db.insertSetting(key, value);
	}
	
	public Boolean saveSettings(DisplaySettingsActivity activity, HashMap<String, String> settings) {
		Iterator<Entry<String, String>> iterator = settings.entrySet().iterator();
		
		Boolean noErrors = true;
		
		while(iterator.hasNext()) {
			Entry<String, String> setting = iterator.next();
			if(!db.getSetting(setting.getKey()).equals(setting.getValue())) {
				if(setting.getKey().equals("offSmobo")) {
					noErrors = (noErrors && toggleOfflineSmobo(activity));
				}
				noErrors = (noErrors && db.insertSetting(setting.getKey(), setting.getValue()));
			}
		}
		
		username = getSetting("username");
		password = getSetting("password");
		
		return noErrors;
	}
	
	public Boolean authenticationFilled() {
		return (username != null && !username.equals("") && password != null && !password.equals(""));
	}
	
	private Boolean toggleOfflineSmobo(DisplaySettingsActivity activity) {
		Boolean toggleSucceeded = db.toggleOfflineSmobo();
		
		if (db.offlineSmobo()) {
			downloadSmobo(activity);
		}
		
		return toggleSucceeded;
	}
	
	private void downloadSmobo(DisplaySettingsActivity activity) {
		LoadItemsToDatabase loader = new LoadItemsToDatabase(activity, this);
		async = true;
		if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
			loader.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	    } else {
	    	loader.execute();
	    }
	}
	
	public void disableAsync() {
		async = false;
	}
	
	public Boolean databaseAvailable() {
		return db.dbAvailable();
	}
	
	public void insertItemIntoDB(PunchItem item) throws Exception {
		if(item.getType() == "member") {
			db.insertMember((PunchMember) item);
		} else if(item.getType() == "team") {
			db.insertTeam((PunchTeam) item);
		} else if(item.getType() == "committee") {
			db.insertCommittee((PunchCommittee) item);
		} else {
			throw new Exception("Unknown item type encountered in " + item.getReference());
		}
	}
	
	private void fillItemList() {
		if(db.dbAvailable()) {
			itemMapping = db.getItemList(mainActivity, itemMapping);
		} else {
			LinkedHashMap<String, PunchItem> result;
			if((result = connection.getItemList(async)) != null) {
				itemMapping = result;
			}
		}
	}
	
	public ArrayList<PunchItem> getMembers() {
		if(members == null || members.isEmpty()) {
			members = new ArrayList<PunchItem>();
			if(itemMapping.isEmpty()) {
				fillItemList();
			}
		
			Iterator<Entry<String, PunchItem>> it = itemMapping.entrySet().iterator();
		
			while(it.hasNext()) {
				Entry<String, PunchItem> pair = it.next();
				PunchItem item = pair.getValue();
				if(item.getType().equals("member")) {
					members.add((PunchMember) item);
				}
			}
		}
				
		return members;
	}
	
	public ArrayList<PunchItem> getTeams() {
		if(teams == null || teams.isEmpty()) {
			teams = new ArrayList<PunchItem>();
			if(itemMapping.isEmpty()) {
				fillItemList();
			}
			
			Iterator<Entry<String, PunchItem>> it = itemMapping.entrySet().iterator();
			
			while(it.hasNext()) {
				Entry<String, PunchItem> pair = it.next();
				PunchItem item = pair.getValue();
				if(item.getType().equals("team")) {
					teams.add((PunchTeam) item);
				}
			}
		}
		
		return teams;
	}
	
	public ArrayList<PunchItem> getCommittees() {
		if(committees == null || committees.isEmpty()) {
			committees = new ArrayList<PunchItem>();
			if(itemMapping.isEmpty()) {
				fillItemList();
			}
			
			Iterator<Entry<String, PunchItem>> it = itemMapping.entrySet().iterator();
			
			while(it.hasNext()) {
				Entry<String, PunchItem> pair = it.next();
				PunchItem item = pair.getValue();
				if(item.getType().equals("committee")) {
					committees.add((PunchCommittee) item);
				}
			}
		}
		
		return committees;
	}
	
	public ArrayList<PunchItem> searchItem(String query, String type) {
		ArrayList<PunchItem> result = new ArrayList<PunchItem>();
		Iterator<Entry<String, PunchItem>> it = itemMapping.entrySet().iterator();
		
		while(it.hasNext()) {
			Entry<String, PunchItem> pair = it.next();
			PunchItem item = pair.getValue();
			if(item.getType().equals(type) && item.getName().toLowerCase().contains(query.toLowerCase())) {
				result.add(item);
			}
		}
		
		return result;
	}
	
	public PunchItem getItem(String reference) {
		return itemMapping.get(reference);
	}
}
