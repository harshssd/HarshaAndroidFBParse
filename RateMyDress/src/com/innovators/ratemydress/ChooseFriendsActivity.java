package com.innovators.ratemydress;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.facebook.model.GraphObject;
import com.facebook.model.GraphUser;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.os.Bundle;

public class ChooseFriendsActivity extends ListActivity {

	public static final String TAG = ChooseFriendsActivity.class.getSimpleName();
	
	protected List<ParseUser> mFriends;
	protected ParseRelation<ParseUser> mFriendsRelation;
	protected ParseUser mCurrentUser;

	protected MenuItem mSendMenuItem;
	Bundle savedInstanceState;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		this.savedInstanceState = savedInstanceState; 
		
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_choose_friends);
		
		//getActionBar();
		getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

	}



	@Override
	public void onResume() {
		super.onResume();
	
		ArrayList<String> friendsJSONStringArrayList = savedInstanceState.getStringArrayList("friends");//RateMyDressApplication.getFriendsKey());
		if (friendsJSONStringArrayList != null) {
		ArrayList<GraphUser> friends = new ArrayList<GraphUser>();
		Iterator<String> friendsJSONStringArrayListIterator = friendsJSONStringArrayList.iterator();
		while (friendsJSONStringArrayListIterator.hasNext()) {
		try {
			friends.add(GraphObject.Factory.create(new JSONObject(friendsJSONStringArrayListIterator.next()), GraphUser.class));
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		}
//		((RateMyDressApplication)getApplication()).setFriends(friends);
		}
		
		
		mCurrentUser = ParseUser.getCurrentUser();
		mFriendsRelation = mCurrentUser.getRelation(ParseConstants.KEY_FRIENDS_RELATION);
		
		setProgressBarIndeterminateVisibility(true);
		
		ParseQuery<ParseUser> query = mFriendsRelation.getQuery();
		query.addAscendingOrder(ParseConstants.KEY_USERNAME);
		mFriendsRelation.getQuery().findInBackground(new FindCallback<ParseUser>() {
			@Override
			public void done(List<ParseUser> friends, ParseException e) {
				setProgressBarIndeterminateVisibility(false);
				if(e == null){
					mFriends = friends;
					
					String[] usernames = new String[mFriends.size()];
					int i = 0;
					for(ParseUser user : mFriends){
						usernames[i] = user.getUsername();
						i++;
					}
	
	//				String[] usernames = {"Sample List","Harsha", "Ankur"};
					ArrayAdapter<String> adapter = new ArrayAdapter<String>(getListView().getContext(), 
											android.R.layout.simple_list_item_checked,
											usernames);
					setListAdapter(adapter);
				}else{
					Log.e(TAG, e.getMessage());
					AlertDialog.Builder builder = new AlertDialog.Builder(ChooseFriendsActivity.this);
					builder.setMessage(e.getMessage())
						.setTitle(R.string.error_title)
						.setPositiveButton(android.R.string.ok, null);
					AlertDialog dialog = builder.create(); 
					dialog.show();
				}
				
			}
		});
	}

	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.choose_friends, menu);
		//mSendMenuItem = menu
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
			super.onListItemClick(l, v, position, id);
			
	}

}
