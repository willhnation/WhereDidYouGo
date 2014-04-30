package com.example.wheredidyougo;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.ParseObject;
import com.parse.ParseUser;

public class MainActivity extends ListActivity {

	static final String[] ACTIVITY_CHOICES = new String[]
		{ "New Location", "My Images", "My Map", "User Search","User Maps", "Logout" };
	ParseObject mObject;
	ParseUser mUser;
	ConnectionDetector cd;
	TextView view;
	UserObjects utility;
	boolean connected;
	
	
	public static String TAG="tag";

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, ACTIVITY_CHOICES));
		
		cd = new ConnectionDetector(getApplicationContext());
		utility = new UserObjects();
		Log.i("TAG", utility.mUser.getUsername());
		
		getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		getListView().setTextFilterEnabled(true);
		getListView().setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				mUser = ParseUser.getCurrentUser();
				AsyncTaskName task = new AsyncTaskName(arg1, arg2);
				task.execute();

			}
			
		});
		


		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	
	private class AsyncTaskName extends AsyncTask<Void, Void, Void> {
        private int mPosition;
        private ProgressDialog mDialog;
		

		public AsyncTaskName(View view, int position) {
			mPosition = position;
			
			mDialog = new ProgressDialog(view.getContext());
			mDialog.setCancelable(true);
			mDialog.setMessage("Please wait");
			mDialog.setTitle("Loading...");
			mDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			mDialog.setProgress(0);
			mDialog.setMax(100);
			mDialog.show();
		}

		@Override
		protected Void doInBackground(Void... params) {

			Intent in;
			connected = cd.isConnectingToInternet();
			if (connected) {
				switch (mPosition) {
				case 0:
					in = new Intent(MainActivity.this, NewLocation.class);
					startActivity(in);
					break;
				case 1:
					in = new Intent(MainActivity.this, MyImages.class);
					startActivity(in);
					break;

				case 2:
					in = new Intent(MainActivity.this, MyMap.class);
					in.putExtra("user", mUser.getUsername());
					in.putExtra("count", utility.getCount());
					startActivity(in);
					break;

				case 3:
					in = new Intent(MainActivity.this, UserSearch.class);
					in.putStringArrayListExtra("users", utility.getUsers());
					startActivity(in);
					break;

				case 4:

					in = new Intent(MainActivity.this, FollowMap.class);
					in.putStringArrayListExtra("users",
							utility.getFollowing(utility.mUser.getUsername()));

					startActivity(in);

					break;

				case 5:
					ParseUser.logOut();
					mUser = ParseUser.getCurrentUser();
					in = new Intent(MainActivity.this, Login.class);
					startActivity(in);
					break;

				default:
					break;
				}

			} else {
				Log.i("Connection", "Is connected:" + connected);
			}
			return null;
		}

     protected void onPostExecute(Void result) {
    	 if (mDialog.isShowing()) {
             mDialog.dismiss();
             
         }
     }

 }


}
