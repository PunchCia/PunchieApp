package punch.tudelft.punchie;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class PunchDB {
	
	private Context dbContext;
	private String dbName = "punchieDatabase";
	private String settingTable = "punchie_settings";
	private String memberTable = "punchie_members";
	private String teamTable = "punchie_teams";
	private String committeeTable = "punchie_committees";
	private String createSettingTable = "CREATE TABLE IF NOT EXISTS " + settingTable + " (keyWord TEXT, keyValue TEXT);";
	private String createMemberTable = "CREATE TABLE IF NOT EXISTS " + memberTable + " (name TEXT,reference TEXT,nevoboNr TEXT,address TEXT,email TEXT,mobileNr TEXT,objection TEXT,TeamName TEXT,TeamRef TEXT,position TEXT);";
	private String createTeamTable = "CREATE TABLE IF NOT EXISTS " + teamTable + " (name TEXT,reference TEXT,theme TEXT,yell TEXT,players TEXT,trainers TEXT,coaches TEXT);";
	private String createCommitteeTable = "CREATE TABLE IF NOT EXISTS " + committeeTable + " (name TEXT,reference TEXT,members TEXT);";
	
	private SQLiteDatabase db;
	
	public PunchDB(Context context) {
		dbContext = context;
        db = dbContext.openOrCreateDatabase(dbName,1,null);
        db.execSQL(createSettingTable);
	}
	
	public void killDB() {
		dbContext.deleteDatabase(dbName);
	}
	
	public Boolean toggleOfflineSmobo() {
		if(offlineSmobo()) {
			dropTable(memberTable);
			dropTable(teamTable);
			dropTable(committeeTable);
			return insertSetting("offSmobo", "false");
		} else {
			db.execSQL(createMemberTable);
			db.execSQL(createTeamTable);
			db.execSQL(createCommitteeTable);
			return insertSetting("offSmobo", "true");
		}
	}

	private void dropTable(String tableName) {
		db.execSQL("DROP TABLE IF EXISTS " + tableName + ";");
	}
	
	private Boolean tableExists(String tableName) {
		Boolean exists = false;
		Cursor c = db.rawQuery("SELECT DISTINCT tbl_name FROM sqlite_master where tbl_name = '"+ tableName +"'", null);
		if(c != null) {
			if(c.getCount() > 0) {
				exists = true;
			}
		}
		
		Boolean hasRows = false;
		c = db.query(tableName, null, null, null, null, null, null);
		if(c != null) {
			if (c.getCount() > 0) {
				hasRows = true;
			}
		}
		return exists && hasRows;
	}
	
	public Boolean dbAvailable() {
		return (offlineSmobo() && tableExists(memberTable) && tableExists(teamTable) && tableExists(committeeTable));
	}
	
	public Boolean offlineSmobo() {
		return getSetting("offSmobo").equals("true");
	}
	
	private String zipMemberList(ArrayList<PunchMember> itemList) {
		String result = "";
		for (PunchItem item : itemList) {
			result += item.getReference() + ";";
		}
		return result;
	}
	
	private ArrayList<PunchMember> unZipMemberList(String members, LinkedHashMap<String, PunchItem> itemMapping) {
		ArrayList<PunchMember> result = new ArrayList<PunchMember>();
		String[] references = members.split(";");
		for(String reference : references) {
			result.add((PunchMember) itemMapping.get(reference));
		}
		return result;
	}
	
	public Boolean insertSetting(String keyword, String keyvalue) {
		db.delete(settingTable, "keyWord='" + keyword + "'", null);
		ContentValues initialValues = new ContentValues();
        initialValues.put("keyWord", keyword);
        initialValues.put("keyValue", keyvalue);
        return (db.insert(settingTable, null, initialValues) != -1);
	}
	
	private ContentValues constructMemberContentValues(PunchMember member) {
		ContentValues initialValues = new ContentValues();
		
		initialValues.put("name", member.getName());
		initialValues.put("reference", member.getReference());
		
		if(member.getNevNr() != null) {
			initialValues.put("nevoboNr", member.getNevNr());
		}
		
		if(member.getAddress() != null) {
			initialValues.put("address", member.getAddress());
		}
		
		if(member.getEmail() != null) {
			initialValues.put("email", member.getEmail());
		}
		
		if(member.getMobileNr() != null) {
			initialValues.put("mobileNr", member.getMobileNr());
		}
		
		if(member.getObjection() != null) {
			initialValues.put("objection", member.getObjection().toString());
		}
		
		if(member.getTeam() != null) {
			initialValues.put("teamName", member.getTeam().getName());
			initialValues.put("teamRef",member.getTeam().getReference());
		}
		
		if(member.getPosition() != null) {
			initialValues.put("position", member.getPosition());
		}		
		
		return initialValues;
	}
	
	public Boolean insertMember(PunchMember member) {
		db.delete(memberTable, "reference='" + member.getReference() + "'", null);
		ContentValues memberValues = constructMemberContentValues(member);
		return(db.insert(memberTable, null, memberValues) != -1);
	}
	
	private ContentValues constructTeamContentValues(PunchTeam team) {
		ContentValues initialValues = new ContentValues();
		
		initialValues.put("name", team.getName());
		initialValues.put("reference", team.getReference());
		
		if(team.getTheme() != null) {
			initialValues.put("theme", team.getTheme());
		}
		
		if(team.getYell() != null) {
			initialValues.put("yell", team.getYell());
		}
		
		initialValues.put("players", zipMemberList(team.getPlayers()));
		initialValues.put("trainers", zipMemberList(team.getTrainers()));
		initialValues.put("coaches", zipMemberList(team.getCoaches()));
		
		return initialValues;
	}
	
	public Boolean insertTeam(PunchTeam team) {
		db.delete(teamTable, "reference='" + team.getReference() + "'", null);
		ContentValues teamValues = constructTeamContentValues(team);
		return(db.insert(teamTable, null, teamValues) != -1);	
	}
	
	private ContentValues constructCommitteeContentValues(PunchCommittee com) {
		ContentValues initialValues = new ContentValues();
		
		initialValues.put("name", com.getName());
		initialValues.put("reference", com.getReference());
		
		initialValues.put("members", zipMemberList(com.getMembers()));
		
		return initialValues;
	}
	
	public Boolean insertCommittee(PunchCommittee com) {
		db.delete(committeeTable, "reference='" + com.getReference() + "'", null);
		ContentValues committeeValues = constructCommitteeContentValues(com);
		return(db.insert(committeeTable, null, committeeValues) != -1);	
	}
	
	public String getSetting(String keyword) {
		Cursor result = db.rawQuery("SELECT * FROM " + settingTable + " WHERE keyWord='" + keyword + "'",null);
		if(result.moveToFirst() && result.getCount() > 0) {
			int colIndex = result.getColumnIndexOrThrow("keyValue");
			if(colIndex != -1) {
				return result.getString(colIndex);
			} else {
				return null;
			}
		} else {
			return null;
		}
	}
	
	public LinkedHashMap<String, PunchItem> getItemList(final Activity activity, LinkedHashMap<String, PunchItem> itemMapping) {
		if(!itemMapping.isEmpty()) {
			itemMapping.clear();
		}
		itemMapping = loadMembersFromDB(itemMapping);
		itemMapping = loadTeamsFromDB(itemMapping);
		itemMapping = loadCommitteesFromDB(itemMapping);
		
		return itemMapping;
	}
	
	private LinkedHashMap<String, PunchItem> loadTeamsFromDB(LinkedHashMap<String, PunchItem> itemMapping) {
		try {
			final Cursor result = db.rawQuery("SELECT DISTINCT * FROM " + teamTable + " ORDER BY name ASC", null);
			result.moveToFirst();
			int nameCol = result.getColumnIndex("name");
			int refCol = result.getColumnIndex("reference");
			int themeCol = result.getColumnIndex("theme");
			int yellCol = result.getColumnIndex("yell");
			int playersCol = result.getColumnIndex("players");
			int trainersCol = result.getColumnIndex("trainers");
			int coachesCol = result.getColumnIndex("coaches");
			
			for(int i = 0; i < result.getCount(); i++) {
				PunchTeam team = loadTeamFromDB(result,nameCol,refCol,themeCol,yellCol,playersCol,trainersCol,coachesCol, itemMapping);
				itemMapping.put(team.getReference(), team);
				result.moveToNext();
			}
			result.close();
			
			return itemMapping;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private PunchTeam loadTeamFromDB(Cursor result,int nameCol,int refCol,int themeCol,int yellCol,int playersCol,int trainersCol,int coachesCol, LinkedHashMap<String, PunchItem> itemMapping) {
		PunchTeam team = new PunchTeam(result.getString(refCol),result.getString(nameCol));
		team.setTheme(result.getString(themeCol));
		team.setYell(result.getString(yellCol));
		team.setPlayers(unZipMemberList(result.getString(playersCol), itemMapping));
		team.setTrainers(unZipMemberList(result.getString(trainersCol), itemMapping));
		team.setCoaches(unZipMemberList(result.getString(coachesCol), itemMapping));
		return team;
	}
	
	private LinkedHashMap<String, PunchItem> loadCommitteesFromDB(LinkedHashMap<String, PunchItem> itemMapping) {
		try {
			final Cursor result = db.rawQuery("SELECT DISTINCT * FROM " + committeeTable + " ORDER BY name ASC", null);
			result.moveToFirst();
			int nameCol = result.getColumnIndex("name");
			int refCol = result.getColumnIndex("reference");
			int membersCol = result.getColumnIndex("members");
			
			for(int i = 0; i < result.getCount(); i++) {
				PunchCommittee com = loadCommitteeFromDB(result, nameCol, refCol, membersCol, itemMapping);
				itemMapping.put(com.getReference(), com);
				result.moveToNext();
			}
			result.close();
			
			return itemMapping;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private PunchCommittee loadCommitteeFromDB(Cursor result, int nameCol, int refCol, int membersCol, LinkedHashMap<String, PunchItem> itemMapping) {
		PunchCommittee com = new PunchCommittee(result.getString(refCol),result.getString(nameCol));
		com.setMembers(unZipMemberList(result.getString(membersCol), itemMapping));
		return com;
	}
	
	private LinkedHashMap<String, PunchItem> loadMembersFromDB(LinkedHashMap<String, PunchItem> itemMapping) {
		try {
			final Cursor result = db.rawQuery("SELECT DISTINCT * FROM " + memberTable + " ORDER BY name ASC", null);
			result.moveToFirst();
			int nameCol = result.getColumnIndex("name");
			int refCol = result.getColumnIndex("reference");
			int nevoboNrCol = result.getColumnIndex("nevoboNr");
			int addressCol = result.getColumnIndex("address");
			int emailCol = result.getColumnIndex("email");
			int mobileNrCol = result.getColumnIndex("mobileNr");
			int objectionCol = result.getColumnIndex("objection");
			int teamNameCol = result.getColumnIndex("TeamName");
			int teamRefCol = result.getColumnIndex("TeamRef");
			
			for(int i = 0; i < result.getCount(); i++) {
				PunchMember member = loadMemberFromDB(result,nameCol,refCol,nevoboNrCol,addressCol,emailCol,mobileNrCol,objectionCol,teamNameCol,teamRefCol);
				itemMapping.put(member.getReference(), member);
				result.moveToNext();
			}
			
			result.close();
			
			return itemMapping;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public PunchMember loadMemberFromDB(Cursor result,int nameCol,int refCol,int nevoboNrCol,int addressCol,int emailCol,int mobileNrCol,int objectionCol,int teamNameCol,int teamRefCol) {
		PunchMember member = new PunchMember(result.getString(refCol),result.getString(nameCol));
		member.setNevNr(result.getString(nevoboNrCol));
		member.setAddress(result.getString(addressCol));
		member.setEmail(result.getString(emailCol));
		member.setMobileNr(result.getString(mobileNrCol));
		member.setObjection(result.getString(objectionCol).equals("true"));
		member.setTeam(new PunchTeam(result.getString(teamRefCol),result.getString(teamNameCol)));
		return member;
	}
	
	public void close() {
		db.close();
	}
	
}
