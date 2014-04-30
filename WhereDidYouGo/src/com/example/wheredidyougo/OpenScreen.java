package com.example.wheredidyougo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseUser;

public class OpenScreen extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_open);

		Parse.initialize(this, "OZFLrnJ4mnW0FQJ1cNjzZaZ4Yjk6izn7yluSps0R", "T3cDSXCbqXzrnAYOvCEMaPn3MH8CmLwEcAOzGP0n");
		ParseAnalytics.trackAppOpened(getIntent());
		


		Intent mainIntent;
		ParseUser mUser = ParseUser.getCurrentUser();
		if (mUser != null) {
			mainIntent = new Intent(OpenScreen.this, MainActivity.class);
			startActivity(mainIntent);
			finish();
		}
		else {
			mainIntent = new Intent(OpenScreen.this, Login.class);
			startActivity(mainIntent);
			finish();
		}
				
	}
	
	
}
