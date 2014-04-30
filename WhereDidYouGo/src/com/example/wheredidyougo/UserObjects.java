package com.example.wheredidyougo;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class UserObjects {
	
	ParseUser mUser;
	List<ParseObject> objects;
	List<ParseUser> objs;
	int count;

	
	public UserObjects () {
		mUser = ParseUser.getCurrentUser();
		try {
            ParseQuery<ParseObject> query = ParseQuery.getQuery(mUser.getUsername());
			objects = query.find();
			
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
	}
	

	int getCount(){
		ParseQuery<ParseObject> query = ParseQuery.getQuery(mUser.getUsername());
		query.whereExists("image");
			try {
				return query.find().size();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return 0;
			}
	}
	
	int getCount(String user){
		ParseQuery<ParseObject> query = ParseQuery.getQuery(user);
		query.whereExists("image");
			try {
				return query.find().size();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return 0;
			}
	}
	ArrayList<String> getUsers() {
		ArrayList<String> users = new ArrayList<String>();
		ParseQuery<ParseUser> query = ParseQuery.getQuery("_User");
		query.addAscendingOrder("user");
		try {
			objs = query.find();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		for (ParseUser name : objs) {
			users.add(name.getUsername());
		}

		users.remove(mUser.getUsername());

		return users;
	}
	
	public ArrayList<String> getUsers(String search) {
		ArrayList<String> users = new ArrayList<String>();
		ParseQuery<ParseUser> query = ParseQuery.getQuery("_User");
		query.addAscendingOrder("user");
		try {
			objs = query.find();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		for (ParseUser name : objs) {
			if (name.getUsername().startsWith(search))
				users.add(name.getUsername());
		}
		users.remove(mUser.getUsername());
		return users;

	}
	
	public void removeUsers(String search, ArrayList<String> users) {
		ParseQuery<ParseUser> query = ParseQuery.getQuery("_User");
		query.addAscendingOrder("user");
		try {
			objs = query.find();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		for (ParseUser name : objs) {
			if (!(name.getUsername().startsWith(search)))
				users.remove(name.getUsername());
			else if(name.getUsername().startsWith(search)) {
				if (!(users.contains(name.getUsername())))
					users.add(name.getUsername());
			}
		}
		users.remove(mUser.getUsername());


	}
	
	public ArrayList<String> getFollowing(String user) {
		ArrayList<String> followers = new ArrayList<String>();
		ParseQuery<ParseObject> query = ParseQuery.getQuery(user);
		query.whereExists("following");
		try {
			objects = query.find();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Log.i("TAG", mUser.getUsername());
		for (ParseObject obj:objects){
			
			Log.i("TAG", obj.getString("following"));
			followers.add(obj.getString("following"));
		}
		
		return followers;
	}
	
	public void removeFollowers(String search,String user, ArrayList<String> users) {
		ParseQuery<ParseObject> query = ParseQuery.getQuery(user);
		query.whereExists("following");
		try {
			objects = query.find();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for (ParseObject obj:objects){
		
			if (!(obj.getString("following").startsWith(search)))
				users.remove(obj.getString("following"));
			else if(obj.getString("following").startsWith(search)) {
				if(!(users.contains(obj.getString("following"))))
						users.add(obj.getString("following"));
			}
		}
		

	}
}
