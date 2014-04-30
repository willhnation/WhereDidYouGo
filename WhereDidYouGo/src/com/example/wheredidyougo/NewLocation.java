package com.example.wheredidyougo;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;



//redirected here upon clicking New Location from the home screen.
public class NewLocation extends Activity implements LocationListener {
	
	private GoogleMap mMap;
	private ParseObject mObject;
	private MarkerOptions mMarker;
    LocationManager locationManager = null;
    Location location;
    
	ImageButton btn_Camera = null;
	Button btn_Save = null;
	Button btn_Find = null;
	
	private ParseFile mPhoto;
	private ParseUser mUser = ParseUser.getCurrentUser();
	static byte[] byteArray=null;
	Context context = this;
	String mComment = "";
	private static String mAddress = "";
	
	Bitmap bm;
	
	
	//used to populate map
	private static double mLat = 0;
	private static double mLong = 0;

	String user;
	Geocoder gc;
	
	private static final int CAMERA_REQUEST = 1000;
	private static final int REQUEST_CODE = 2000;

    private static final long MIN_DISTANCE_UPDATE = 15;
    private static final long MIN_TIME_UPDATE = 1000 * 60 * 1;
    
    //used for current location
	String current;
	double cLat;
	double cLong;
	



	@Override
    protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        
        initiateMap();
        
        
        btn_Find = (Button) findViewById(R.id.btn_find);

        btn_Find.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// Getting reference to EditText to get the user input location
                EditText editLocation = (EditText) findViewById(R.id.search_location);
 
                // Getting user input location
                String location = editLocation.getText().toString();
 
                if(location!=null && !location.equals("")){
                    new GeocoderTask().execute(location);
                }
            }
        });
        	
        
        btn_Save = (Button) findViewById(R.id.btn_save);
        
        btn_Save.setOnClickListener(new OnClickListener() {
        	
			@Override
			public void onClick(View v) {
				if (byteArray == null) {

					 AlertDialog.Builder ad = new AlertDialog.Builder(context);
						ad.setTitle("Error: No image to save");
						ad.setMessage("Please try again");
						ad.setNeutralButton("OK", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
							}
						});

						AlertDialog a = ad.create();
						a.show();

				}
				else {
	                Log.i("TAG", "save" + mLat + " " + mLong);
	                Log.i("TAG", "save" + cLat + " " + cLong);

	                //set to current location
					if (mAddress.equals(""))
						mAddress = current;
					if (mLat == 0) 
						mLat = cLat;
					if (mLong == 0)
						mLong = cLong;
					
					
					user = mUser.getUsername();
					Log.i("TAG", mAddress);
					String temp = mAddress.replace(" ", "");
					temp = temp.replace(",", "");
					temp = temp.replace("&", "");
					//temp = temp.replace("-","");
					mPhoto = new ParseFile(temp + ".JPEG", byteArray);
					mObject = new ParseObject(user);
				
				mObject.put("address", mAddress);
				mObject.put("image", mPhoto);
				mObject.put("lat", mLat);
				mObject.put("long", mLong);
				
				
				final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
		 
					
					final EditText input = new EditText(context);
					alertDialogBuilder.setView(input)
				    	.setTitle("Add a comment?")
						.setCancelable(false)
						.setNegativeButton("Yes",new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,int id) {
					
								mComment = input.getText().toString();
								mObject.put("comment", mComment);

								Log.i("TAG", mComment);
								mObject.saveInBackground();
								NewLocation.this.finish();
								
							}
						  })
						.setPositiveButton("No",new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,int id) {
								
								mObject.put("comment", "");
								mObject.saveInBackground();
								NewLocation.this.finish();
							}
						});
		 
						AlertDialog alertDialog = alertDialogBuilder.create();
						alertDialog.show();
						
						//reset address
						mAddress = "";

				}
				//reset byteArray
				byteArray = null;
			}
        	
        });

        btn_Camera = (ImageButton) findViewById(R.id.btn_camera);
        
		btn_Camera.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				selectImage();	
			}
		});
		
	
		
		mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
			
			@Override
			public void onMapClick(LatLng point) {
				// TODO Auto-generated method stub
				if (mMarker == null) {
					mMarker = new MarkerOptions().position(
						new LatLng(point.latitude, point.longitude));
					mAddress = getAddress(point.latitude, point.longitude);
					mMarker.title(mAddress);
				
					mMap.addMarker(mMarker);
					mLat = point.latitude;
					mLong = point.longitude;
					
				}
				else {
					mMap.clear();
					mMarker = new MarkerOptions().position(
							new LatLng(point.latitude, point.longitude));
						mAddress = getAddress(point.latitude, point.longitude);
						Log.i("ADD", "address after click" + mAddress);
						mMarker.title(mAddress);
					
						mMap.addMarker(mMarker);
						mLat = point.latitude;
						mLong = point.longitude;
				}

				
			}
		});
           
    }
	
	public void initiateMap() {
        
        mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
        mMap.setMyLocationEnabled(true);
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
		mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(itemLocation, 16));
		
		mMarker = new MarkerOptions().position(
				new LatLng(location.getLatitude(), location.getLongitude()));
		
			current = getAddress(location.getLatitude(), location.getLongitude());
			mMarker.title(current);
		
			mMap.addMarker(mMarker);
			cLat = location.getLatitude();
			cLong = location.getLongitude();
			
			
	}
	// An AsyncTask class for accessing the GeoCoding Web Service
    private class GeocoderTask extends AsyncTask<String, Void, List<Address>>{
 
        @Override
        protected List<Address> doInBackground(String... locationName) {
            // Creating an instance of Geocoder class
            gc = new Geocoder(getBaseContext());
            List<Address> addresses = null;
 
            try {
            	//get the first address it finds
                addresses = gc.getFromLocationName(locationName[0], 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return addresses;
        }
 
        @Override
        protected void onPostExecute(List<Address> addresses) {
 
            if(addresses==null || addresses.size()==0){
                Toast.makeText(getBaseContext(), "No Location found", Toast.LENGTH_SHORT).show();
            }
 
            // Clears all the existing markers on the map
            mMap.clear();
 
            // Adding Markers on Google Map for each matching address
            for (int i = 0;i < addresses.size(); i++) {
 
                Address address = (Address) addresses.get(i);
 
                // Creating an instance of GeoPoint, to display in Google Map
                LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
 
                mAddress = getAddress(address.getLatitude(), address.getLongitude());
 
                mMarker = new MarkerOptions();
                mMarker.position(latLng);
                mMarker.title(mAddress);
 
                mMap.addMarker(mMarker);
                mLat = address.getLatitude();
                mLong = address.getLongitude();
                Log.i("TAG", mLat + " " + mLong);
                Log.i("TAG", cLat + " " + cLong);

                // Locate the first location
                if (i == 0)
                    mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
            }
        }
    }


	protected void onActivityResult(int requestCode, int resultCode, Intent data) {  
		if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {  
//			Uri g = data.getData();
//			Bitmap bmap = (Bitmap) data.getExtras().get("data");
//			ByteArrayOutputStream stream = new ByteArrayOutputStream();
//			Log.i("AA", bmap.getWidth() + " " + bmap.getHeight());
//			//Bitmap bmap = Bitmap.createScaledBitmap(photo,320,480,false);
//			bmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
//
//			byteArray = stream.toByteArray();
			int orientation = -1;
			Uri galleryImage = data.getData();
			orientation = getOrientation(this,galleryImage);
			Log.i("IMAGE", " " + orientation);
			String path = getPath(galleryImage, NewLocation.this);
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = false;
            options.inPreferredConfig = Config.RGB_565;
            options.inDither = true;
            options.inSampleSize = 4;

			Bitmap bm = BitmapFactory.decodeFile(path, options);
			
			int width = bm.getWidth();
	        int height = bm.getHeight();
//	        Log.i("AAA", width + " " + height);
//	        
//	        
//	        int newWidth = 200;
//	        int newHeight = 200;
//
//	        float scaleWidth = ((float) newHeight) / width;
//	        float scaleHeight = ((float) newWidth) / height;
//	        Matrix matrix = new Matrix();
//
//	        matrix.postScale(scaleWidth, scaleHeight);
//	        
	        Matrix matrix = new Matrix();

	        if (orientation != -1)
	        	matrix.postRotate(360 - (360-orientation));
	        
	        Bitmap bitm = Bitmap.createBitmap(bm, 0, 0,
	                          width, height, matrix, true);



			Log.i("TaaAG", bitm.getWidth() +  " " + bitm.getHeight());

			Bitmap bmap = Bitmap.createScaledBitmap(bitm, 320, 480, false);
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			
			bmap.compress(CompressFormat.JPEG, 50, stream);
			byteArray = stream.toByteArray();
			try {
				stream.flush();
			
				stream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			
			
		}
		
		else if(requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
			int orientation = -1;
			Uri galleryImage = data.getData();
			orientation = getOrientation(this,galleryImage);
			Log.i("IMAGE", " " + orientation);
			String path = getPath(galleryImage, NewLocation.this);
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = false;
            options.inPreferredConfig = Config.RGB_565;
            options.inDither = true;
            options.inSampleSize = 4;
            
            
            
			Bitmap bm = BitmapFactory.decodeFile(path, options);
			
			int width = bm.getWidth();
	        int height = bm.getHeight();


	        Matrix matrix = new Matrix();
	        
	        if (orientation != -1)
	        	matrix.postRotate(360 - (360-orientation));
	        
	        Bitmap bitm = Bitmap.createBitmap(bm, 0, 0,
	                          width, height, matrix, true);




			Bitmap bmap = Bitmap.createScaledBitmap(bitm, 320, 480, false);

			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			
			bmap.compress(CompressFormat.JPEG, 50, stream);
			byteArray = stream.toByteArray();
			try {
				stream.flush();
			
				stream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			
		}
	
	}



	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public String getAddress(double lat, double lon) {
		StringBuilder currentAdd = null;

		Geocoder gc = new Geocoder(this);
		try{
			currentAdd = new StringBuilder();

			Address address = null;
			List<Address> addresses = gc.getFromLocation(lat,lon,1);
			for(int index=0; index<addresses.size(); ++index) {
				address = addresses.get(index);
				currentAdd.append(address.getAddressLine(0) + ", " + address.getAddressLine(1));

			}
			return currentAdd.toString();
		}
		catch(Exception e){
			return "No address";
		}
	
	}
	
	private void selectImage() {
	        final CharSequence[] items = { "Take Photo", "Choose from Gallery",
	                "Cancel" };

	        AlertDialog.Builder dialog = new AlertDialog.Builder(NewLocation.this);
	        dialog.setTitle("Add Photo");
	        dialog.setItems(items, new DialogInterface.OnClickListener() {
	            @Override
	            public void onClick(DialogInterface dialog, int item) {
	                if (items[item].equals("Take Photo")) {
	                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
	                    
	                    startActivityForResult(cameraIntent, CAMERA_REQUEST);
	                } else if (items[item].equals("Choose from Gallery")) {
	                    Intent intent = new Intent();
	                    intent.setAction(Intent.ACTION_GET_CONTENT);
	                            
	                    intent.setType("image/*");
	                    intent.addCategory(Intent.CATEGORY_OPENABLE);

	                    startActivityForResult(
	                            intent,
	                            REQUEST_CODE);
	                } else if (items[item].equals("Cancel")) {
	                    dialog.dismiss();
	                }
	            }
	        });
	        dialog.show();
	    }
	
	public String getPath(Uri uri, Activity activity) {
        String[] projection = { MediaColumns.DATA };
        @SuppressWarnings("deprecation")
		Cursor cursor = activity
                .managedQuery(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaColumns.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
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
	
	public static int getOrientation(Context context, Uri photoUri) {
	    Cursor cursor = context.getContentResolver().query(photoUri,
	            new String[] { MediaStore.Images.ImageColumns.ORIENTATION },
	            null, null, null);

	    try {
	        if (cursor.moveToFirst()) {
	            return cursor.getInt(0);
	        } else {
	            return -1;
	        }
	    } finally {
	        cursor.close();
	    }
	}


}

