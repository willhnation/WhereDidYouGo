package com.example.wheredidyougo;

import java.util.HashMap;
import java.util.List;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;



import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
 
public class MyMap extends Activity implements LocationListener {

	private GoogleMap mMap;
	private MarkerOptions mMarker;
    LocationManager locationManager = null;
    Location location;
    double mLat = 0;
	double mLong = 0;
	static String mUser;
    List<ParseObject> objects;
    static HashMap<String,ParseFile> ref = new HashMap<String,ParseFile>();
    private static final long MIN_DISTANCE_UPDATE = 20;
    private static final long MIN_TIME_UPDATE = 1000 * 60 * 2;
    
    ImageView image;
    
    
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent a = getIntent();
        
        setContentView(R.layout.map_activity);
        mUser = a.getStringExtra("user");
        int count = a.getIntExtra("count", 0);
        if (count != 0)
        	initiateMap();
		else {
			Log.i("YYY", "ADASG");
			AlertDialog.Builder builder = new AlertDialog.Builder(this)
					.setTitle("Error: No markers saved to map")
					.setMessage("User must add an image first")
					.setPositiveButton("OK", new OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
							finish();
						}
					});
			
			builder.create().show();
		

		}
    }
	
	public Builder createImageDialog(Marker marker, ParseFile file) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this)
				.setMessage(marker.getSnippet())
				.setTitle(marker.getTitle())
				.setPositiveButton("OK", new OnClickListener() {                     
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                    }
                })
                .setView(image);
		return builder;
				
		
	}
	private void initiateMap() {
        
        mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.marker_map)).getMap();
        mMap.setMyLocationEnabled(true);
        mMap.setOnMarkerClickListener(new OnMarkerClickListener() {
			ParseFile file; 
			
			@Override
			public boolean onMarkerClick(Marker marker) {
		        image = new ImageView(MyMap.this);
		        file = ref.get(marker.getTitle()+marker.getSnippet());
		        try {
					image.setImageBitmap(BitmapFactory.decodeByteArray(file.getData(), 0, file.getData().length));
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		        Builder dialog = createImageDialog(marker, file);
		        dialog.create().show();
		        

				return false;
			}
		});
        try {
            locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
            locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_UPDATE,
                            MIN_DISTANCE_UPDATE, this);
                    if (locationManager != null) {
                        location = locationManager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        
                    }
                }
        catch (Exception e) {
            e.printStackTrace();
        }
        
        
	

		LatLng itemLocation = new LatLng(location.getLatitude(), location.getLongitude());
		mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(itemLocation, 5));
		
		try {
			ParseQuery<ParseObject> query = ParseQuery.getQuery(mUser);
			query.whereDoesNotExist("following");
			objects = query.find();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		String address;
		String comment;
		for (ParseObject obj : objects) {
			mLat = obj.getDouble("lat");
			mLong = obj.getDouble("long");
			
			mMarker = new MarkerOptions().position(
					new LatLng(mLat, mLong));
		
			address = obj.getString("address");
			comment = obj.getString("comment");
			mMarker.title(address);
			mMarker.snippet(comment);
		
			mMap.addMarker(mMarker);
			ref.put(address+comment, obj.getParseFile("image"));
		}
			
			
	}
	
	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}
	
}
