package com.example.wheredidyougo;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class FollowMap extends ListActivity implements TextWatcher {

	AutoCompleteTextView mAutoComplete;
	ParseUser mUser;
	ArrayList<String> dictionary;
	ArrayList<String> followers;
	List<ParseObject> objects;
	UserObjects utility = new UserObjects();
	Button btn_Find;
	static int pos;
	static ArrayAdapter<String> adapter;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.follower_activity);
		
		Intent in = getIntent();
		dictionary = in.getStringArrayListExtra("users");
		followers = in.getStringArrayListExtra("users");
		mUser = ParseUser.getCurrentUser();

		mAutoComplete = (AutoCompleteTextView) findViewById(R.id.follower_search);

		mAutoComplete.addTextChangedListener(this);

		mAutoComplete.setAdapter(new ArrayAdapter<String>(this,
				android.R.layout.simple_dropdown_item_1line, dictionary));

		adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, followers);
		
		setListAdapter(adapter);

		btn_Find = (Button) findViewById(R.id.btn_map);

		btn_Find.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				String search;
				search = mAutoComplete.getText().toString();
				utility.removeFollowers(search, mUser.getUsername(), followers);
				adapter.notifyDataSetChanged();

			}

		});
	}
	@Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		pos = position;
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		 
		
		// set title
		alertDialogBuilder.setTitle("View " + followers.get(position) + "'s locations?")
			.setCancelable(false)
			
			.setNegativeButton("Yes",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,int id) {

					Intent in = new Intent(FollowMap.this, MyMap.class);
					in.putExtra("user", followers.get(pos));
					in.putExtra("count", utility.getCount(followers.get(pos)));

					startActivity(in);					
					
				}
			  })
			.setPositiveButton("No",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,int id) {
					// if this button is clicked, just close
					// the dialog box and do nothing
					
					dialog.dismiss();
					
					
				}
			});

			// create alert dialog
			AlertDialog alertDialog = alertDialogBuilder.create();

			// show it
			alertDialog.show();

//
//		Intent in = new Intent(FollowMap.this, MyMap.class);
//		in.putExtra("user", followers.get(position));
//		startActivity(in);
	} 
        
	@Override
	public void afterTextChanged(Editable arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		// TODO Auto-generated method stub

	}
	
	public ArrayList<String> getObj() throws ParseException {
		ArrayList<String> followers = new ArrayList<String>();
		ParseQuery<ParseObject> query = ParseQuery.getQuery(mUser.getUsername());
		query.whereExists("following");
		query.addAscendingOrder("following");
		objects = query.find();
		Log.i("TAG", mUser.getUsername());
		for (ParseObject obj:objects){
			
			Log.i("TAG", obj.getString("following"));
			followers.add(obj.getString("following"));
		}
		
		return followers;
	}
	  
}
