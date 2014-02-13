package punch.tudelft.punchie;

import java.util.ArrayList;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class DisplaySmoBoActivity extends Activity {
	
	private ContentManager cm;
	private LinearLayout list;
	private Context context;
	private String displayType;
	private View active;

    @Override
    public void onCreate(Bundle savedInstanceState) {
    	setContentView(R.layout.activity_display_smo_bo);
        context = this;
        cm = ((PunchApplication)getApplication()).getContentManager();
        list = (LinearLayout) findViewById(R.id.results);
        initLoader();
    	displayType = "member";
    	ArrayList<PunchItem> initList = cm.getMembers();
        refreshList(initList);
        active = (TextView) findViewById(R.id.memberListTitle);
        super.onCreate(savedInstanceState);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);             
    }
    
    public void searchItem(View view) {
    	EditText queryText = (EditText) findViewById(R.id.search_member);
    	String query = queryText.getText().toString();
    	ArrayList<PunchItem> result = cm.searchItem(query, displayType);
    	refreshList(result);
    }
    
    public void initLoader() {
    	ProgressBar loader = new ProgressBar(this);
    	LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
		lp.setMargins(0, 10, 0, 10);
    	loader.setLayoutParams(lp);
    	list.addView(loader);
    }
    
    View.OnClickListener openItem(final TextView itemText)  
    {
        return new View.OnClickListener() {
            public void onClick(View v) {
               String reference = (String) itemText.getHint();
               String name = (String) itemText.getText();
               Intent intent = null;
               if(displayType.equals("member")) {
            	   intent = new Intent(context, DisplayMember.class);
               } else if (displayType.equals("team")) {
            	   intent = new Intent(context, DisplayTeam.class);
               } else if (displayType.equals("committee")) {
            	   intent = new Intent(context, DisplayCommittee.class);
               }
               if(intent != null) {
                   intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                   intent.putExtra("reference", reference);
                   intent.putExtra("name", name);
                   startActivity(intent);
               }
            }
        };
    }
    
    private void setTitlePadding(View v) {
    	v.setPadding(10, 5, 10, 5);
    }
    
    public void refreshTeamList(View v)
    {
    	initLoader();
		ArrayList<PunchItem> teams = cm.getTeams();
		displayType = "team";
		refreshList(teams);
		active.setBackgroundDrawable(getResources().getDrawable(R.drawable.border_all_sides));
		setTitlePadding(active);
		active = findViewById(R.id.teamListTitle);
		active.setBackgroundDrawable(getResources().getDrawable(R.drawable.border_no_bottom));
		setTitlePadding(active);
    }
    
    public void refreshMemberList(View v)
    {
    	initLoader();
		ArrayList<PunchItem> members = cm.getMembers();
		displayType = "member";
		refreshList(members);
		active.setBackgroundDrawable(getResources().getDrawable(R.drawable.border_all_sides));
		setTitlePadding(active);
		active = findViewById(R.id.memberListTitle);
		active.setBackgroundDrawable(getResources().getDrawable(R.drawable.border_no_bottom));
		setTitlePadding(active);
    }
    
    public void refreshCommitteeList(View v)
    {	
    	initLoader();
		ArrayList<PunchItem> committees = cm.getCommittees();
		displayType = "committee";
		refreshList(committees);
		active.setBackgroundDrawable(getResources().getDrawable(R.drawable.border_all_sides));
		setTitlePadding(active);
		active = findViewById(R.id.committeeListTitle);
		active.setBackgroundDrawable(getResources().getDrawable(R.drawable.border_no_bottom));
		setTitlePadding(active);
    }
    
    public void refreshList(ArrayList<PunchItem> itemList) {
    	list.removeAllViews();
	    LinearLayout.LayoutParams res = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
	    res.setMargins(5, 0, 5, 0);
	    ((TextView)findViewById(R.id.itemListAmount)).setText("(" + itemList.size() + ")");
	    
	    if(itemList.size() > 0) {
	    	LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,1);
			lp.setMargins(2, 2, 2, 2);
	    	for(PunchItem item : itemList) {
		    	TextView result = new TextView(this);
		    	result.setHint(item.getReference());
		    	result.setOnClickListener(openItem(result));
				result.setText(item.getName());
				result.setTextSize(20);
				result.setLayoutParams(res);
				View resultLine = new View(this);
				resultLine.setBackgroundResource(R.color.gray);
				resultLine.setLayoutParams(lp);
				list.addView(result);
				list.addView(resultLine);
	    	}
    	} else {
    		TextView nonFound = new TextView(this);
    		nonFound.setText("Er zijn geen resultaten gevonden die voldoen aan uw criteria.");
    		nonFound.setLayoutParams(res);
    		nonFound.setTextSize(20);
    		list.addView(nonFound);
    	}	    
    }
 
}
