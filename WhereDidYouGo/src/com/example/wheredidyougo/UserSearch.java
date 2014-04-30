package com.example.wheredidyougo;

import java.util.ArrayList;
import java.util.List;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;

public class UserSearch extends ListActivity implements TextWatcher {
	
	Button btn_Find;
	AutoCompleteTextView mAutoComplete;
	static ArrayAdapter<String> adapter;
	ArrayList<String> usernames;
	ArrayList<String> dictionary;
	
	List<ParseObject> temp;
	ParseUser mUser;
	List<ParseUser> obj;
	UserObjects utility = new UserObjects();
	static int pos;
	
	


	@Override
    protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_search_activity);
		
		Intent in = getIntent();
		dictionary = in.getStringArrayListExtra("users");
		usernames = in.getStringArrayListExtra("users");
		adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, dictionary);
        setListAdapter(adapter);

		mUser = ParseUser.getCurrentUser();
		
		mAutoComplete = (AutoCompleteTextView) findViewById(R.id.search_user);

		mAutoComplete.addTextChangedListener(this);

		mAutoComplete.setAdapter(new ArrayAdapter<String>(this,
				android.R.layout.simple_dropdown_item_1line,dictionary));

		btn_Find = (Button) findViewById(R.id.btn_find);

        btn_Find.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
                 String search;
                 search = mAutoComplete.getText().toString();
                 utility.removeUsers(search, usernames);
         		 adapter.notifyDataSetChanged();

            }
			
	
        });
        
        
	}
	
	
	@Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		
		mUser = ParseUser.getCurrentUser();
		
		ParseQuery<ParseObject> query = ParseQuery.getQuery(mUser.getUsername());
		query.whereEqualTo("following", usernames.get(position));
		try {
			temp = query.find();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		pos = position;
		if (temp.size() == 0) {

			AlertDialog.Builder alertDialogBuilder = followDialog();

			AlertDialog alertDialog = alertDialogBuilder.create();

			alertDialog.show();
		
		} else if(temp.size() == 1) {

			AlertDialog.Builder alertDialogBuilder = removeDialog();

			// create alert dialog
			AlertDialog alertDialog = alertDialogBuilder.create();

			// show it
			alertDialog.show();

		}
                 
     }
	

	private AlertDialog.Builder followDialog() {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

		// set dialogue
		alertDialogBuilder
				.setTitle("Follow user?")
				.setMessage(usernames.get(pos))
				.setCancelable(false)

				.setNegativeButton("Yes",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {

								ParseObject mObject = new ParseObject(mUser.getUsername());
								mObject.put("following", usernames.get(pos));
								mObject.saveInBackground();
								dialog.dismiss();

							}
						})
				.setPositiveButton("No", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {

						dialog.dismiss();

					}
				});
		
		return alertDialogBuilder;

	}
	
	private AlertDialog.Builder removeDialog() { 
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		alertDialogBuilder
				.setTitle("Delete user?")
				.setMessage(usernames.get(pos))
				.setCancelable(false)
				.setNegativeButton("Yes",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {

								temp.get(0).deleteInBackground();
								dialog.dismiss();

							}
						})

				.setPositiveButton("No", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						
						dialog.dismiss();

					}
				});
		
		return alertDialogBuilder;
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

}
