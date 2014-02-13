package punch.tudelft.punchie;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;

import org.apache.commons.codec.binary.Base64;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;

public class PunchConnection extends Thread {
	
	private Activity mainActivity;
	private String username;
	private String password;
	private String authorizationString;
	private static HashMap<String, String> typeUrlMapping;
	
	public PunchConnection(String user, String pass, Activity activity) {
		mainActivity = activity;
		this.username = user;
		this.password = pass;
		String creds = this.username + ":" + this.password;
		byte[] base = creds.getBytes();
		this.authorizationString = "Basic " + new String(new Base64().encode(base));
		typeUrlMapping = new HashMap<String, String>();
        typeUrlMapping.put("member", "http://punch.tudelft.nl/community/smoelenboek.json");
        typeUrlMapping.put("team", "http://punch.tudelft.nl/volleybal/zaalvolleybal/teams.json");
        typeUrlMapping.put("committee", "http://punch.tudelft.nl/vereniging/commissies.json");
	}
	
	private boolean isOnline(Activity activity) {
	    ConnectivityManager cm = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo netInfo = cm.getActiveNetworkInfo();
	    if (netInfo != null && netInfo.isConnectedOrConnecting()) {
	        return true;
	    }
	    return false;
	}
	
	public ApiLoader getAsyncJsonFromCon(String type) {
		ApiLoader loader = new ApiLoader(authorizationString);
		String[] params = new String[1];
		params[0] = type;
		
		if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
			loader.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params);
	    } else {
	    	loader.execute(params);
	    }
		return loader;
	}
	
	public String getSyncJsonFromCon(String type) {
		String result = null;
		try{
			String address = typeUrlMapping.get(type);
			result = getInfo(address);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public LinkedHashMap<String, PunchItem> getItemList(Boolean async) {
		if(!isOnline(mainActivity)) {
			return null;
		}
		
		String memberString = null;
		String teamString = null;
		String committeeString = null;
		
		try {
			if (!async) {
				ApiLoader memberLoader = getAsyncJsonFromCon("member");
				ApiLoader teamLoader = getAsyncJsonFromCon("team");
				ApiLoader committeeLoader = getAsyncJsonFromCon("committee");
				
				memberString = memberLoader.get();
				teamString = teamLoader.get();
				committeeString = committeeLoader.get();
			} else {
				memberString = getSyncJsonFromCon("member");
				teamString = getSyncJsonFromCon("team");
				committeeString = getSyncJsonFromCon("committee");
			}
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		LinkedHashMap<String, PunchItem> itemList = new LinkedHashMap<String, PunchItem>();
		
		if(memberString != null && teamString != null && committeeString != null) {
			itemList = initializeMembers(memberString, itemList);
			itemList = initializeTeams(teamString, itemList);
			itemList = initializeCommittees(committeeString, itemList);
		}
		
		return itemList;
	}
	
	private LinkedHashMap<String, PunchItem> initializeMembers(String memberString, LinkedHashMap<String, PunchItem> itemList) {
		JSONArray jsonMembers = null;
		try {
			jsonMembers = new JSONArray(memberString);
			
			for(int i = 0; i < jsonMembers.length(); i++) {
				JSONObject jsonMember = jsonMembers.getJSONObject(i);
				PunchMember member = new PunchMember(jsonMember.getString("ref"), jsonMember.getString("name"));
				member.setObjection(jsonMember.getBoolean("objection"));
				member.setNevNr(jsonMember.getString("nevobo_number"));
				
				if(!member.getObjection()) {
					member.setAddress(jsonMember.getString("address"));
					member.setEmail(jsonMember.getString("email"));
					member.setMobileNr(jsonMember.getString("mobile_phone"));
				}
				
				try {
					if(jsonMember.has("team") && null != jsonMember.get("team") && jsonMember.getString("team") != "") {
						JSONObject jsonTeam = jsonMember.getJSONObject("team");
					
						PunchItem team = itemList.get(jsonTeam.getString("ref"));
					
						if(null == team) {
							team = new PunchTeam(jsonTeam.getString("ref"), jsonTeam.getString("name"));
							itemList.put(team.getReference(), team);
						}
						member.setTeam((PunchTeam) team);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				
				itemList.put(member.getReference(), member);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return itemList;
	}
	
	private LinkedHashMap<String, PunchItem> initializeTeams(String teamString, LinkedHashMap<String, PunchItem> itemList) {
		JSONObject jsonTeams = null;
		try {
			jsonTeams = new JSONObject(teamString);
			Iterator<String> iter = jsonTeams.keys();
			
			while(iter.hasNext()) {
				String teamName = iter.next();
				JSONObject jsonTeam = jsonTeams.getJSONObject(teamName);
				
				PunchTeam team = (PunchTeam) itemList.get(jsonTeam.get("url"));
				
				if(null == team) {
					team = new PunchTeam(jsonTeam.getString("url"), teamName);
				}
				
				team.setGender(jsonTeam.getString("gender"));
				team.setNumber(jsonTeam.getInt("number"));
				team.setTheme(jsonTeam.getString("theme"));
				team.setYell(jsonTeam.getString("yell"));
				
				JSONArray players = jsonTeam.getJSONArray("players");
				
				for (int i = 0; i < players.length(); i++) {
					JSONObject jsonMember = players.getJSONObject(i);
					
					PunchMember member = (PunchMember) itemList.get(jsonMember.getString("url"));
					
					if(null == member) {
						member = new PunchMember(jsonMember.getString("url"), jsonMember.getString("name"));
						itemList.put(member.getReference(), member);
					}
					
					member.setPosition(jsonMember.getString("position"));
					member.setCaptain(jsonMember.getBoolean("captain"));
					
					if(member.isCaptain()) {
						team.setCaptain(member);
					}
					
					team.addPlayer(member);
				}
				
				JSONArray trainers = jsonTeam.getJSONArray("trainers");
				
				for (int i = 0; i < trainers.length(); i++) {
					JSONObject jsonMember = trainers.getJSONObject(i);
					
					PunchMember member = (PunchMember) itemList.get(jsonMember.getString("url"));
					
					if(null == member) {
						member = new PunchMember(jsonMember.getString("url"), jsonMember.getString("name"));
						itemList.put(member.getReference(), member);
					}
					
					team.addTrainer(member);
				}
				
				JSONArray coaches = jsonTeam.getJSONArray("coaches");
				
				for (int i = 0; i < coaches.length(); i++) {
					JSONObject jsonMember = coaches.getJSONObject(i);
					
					PunchMember member = (PunchMember) itemList.get(jsonMember.getString("url"));
					
					if(null == member) {
						member = new PunchMember(jsonMember.getString("url"), jsonMember.getString("name"));
						itemList.put(member.getReference(), member);
					}
					
					team.addCoach(member);
				}
				
				itemList.put(team.getReference(), team);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return itemList;
	}
	
	private LinkedHashMap<String, PunchItem> initializeCommittees(String committeeString, LinkedHashMap<String, PunchItem> itemList) {
		JSONArray jsonCommittees = null;
		try {
			jsonCommittees = new JSONArray(committeeString);
			
			for (int i = 0; i < jsonCommittees.length(); i++) {
				JSONObject jsonCommittee = jsonCommittees.getJSONObject(i);
				PunchCommittee com = new PunchCommittee(jsonCommittee.getString("ref"), jsonCommittee.getString("name"));
				
				JSONArray jsonComMembers = jsonCommittee.getJSONArray("members");
				
				for (int j = 0; j < jsonComMembers.length(); j++) {
					JSONObject jsonComMember = jsonComMembers.getJSONObject(j);
					PunchMember member = (PunchMember) itemList.get(jsonComMember.getString("ref"));
					
					if (null == member) {
						member = new PunchMember(jsonComMember.getString("ref"), jsonComMember.getString("name"));
						itemList.put(member.getReference(), member);
					}
					
					com.addMember(member);
				}
				
				itemList.put(com.getReference(), com);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return itemList;
	}
	
	public String getInfo(String address) throws Exception {
		URL url = new URL(address);
		URLConnection uc = url.openConnection();
		uc.setRequestProperty ("Authorization", authorizationString);
		InputStream in = uc.getInputStream();
		InputStreamReader inRead = new InputStreamReader(in);
		BufferedReader br = new BufferedReader(inRead);
		String line;
		String result = "";
		while ((line = br.readLine()) != null) {
			result += line;
		}
		return result;
	}
}