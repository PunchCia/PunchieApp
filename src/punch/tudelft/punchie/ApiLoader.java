package punch.tudelft.punchie;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;

import android.os.AsyncTask;

public class ApiLoader extends AsyncTask<String, Integer, String> {
	
	private String authorization;
	private ArrayList<String> exceptions;
	private static HashMap<String, String> typeUrlMapping;
	
	public ApiLoader(String authorizationString) {
		typeUrlMapping = new HashMap<String, String>();
        typeUrlMapping.put("member", "http://punch.tudelft.nl/community/smoelenboek.json");
        typeUrlMapping.put("team", "http://punch.tudelft.nl/volleybal/zaalvolleybal/teams.json");
        typeUrlMapping.put("committee", "http://punch.tudelft.nl/vereniging/commissies.json");
		authorization = authorizationString;
		exceptions = new ArrayList<String>();
	}

	@Override
	protected String doInBackground(String... type) {
		String result = null;
		try{
			String address = typeUrlMapping.get(type[0]);
			result = getInfo(address);
		} catch (Exception e) {
			exceptions.add(e.getMessage());
		}
		return result;
	}
	
	public ArrayList<String> getExceptions() {
		return exceptions;
	}
	
	private String getInfo(String address) throws Exception {
		URL url = new URL(address);
		URLConnection uc = url.openConnection();
		uc.setRequestProperty ("Authorization", authorization);
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
