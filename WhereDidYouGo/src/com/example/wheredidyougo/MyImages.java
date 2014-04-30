package com.example.wheredidyougo;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.Config;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Gallery;
import android.widget.TextView;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

//redirected here upon clicking My Images from home screen.
@SuppressWarnings("deprecation")
public class MyImages extends Activity {

	
	private ParseUser mUser = ParseUser.getCurrentUser();
	ParseFile file;
	ArrayList<ParseFile> photos = new ArrayList<ParseFile>();
	ArrayList<String> comments = new ArrayList<String>();
    ParseImageView imgView;
    Gallery gallery;
    Context context = this;
    List<ParseObject> objects;
    TextView text;
    TextView address;
    TextView address2;
    ParseObject initial;

	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.images);
        
        gallery = (Gallery) findViewById(R.id.gallery);
        gallery.setSpacing(1);

        imgView = (ParseImageView) findViewById(R.id.imageView);
        text = (TextView) findViewById(R.id.comment);
        address = (TextView) findViewById(R.id.address);
        address2 = (TextView)findViewById(R.id.address2);
		
        try {
	        ParseQuery<ParseObject> queryView = ParseQuery.getQuery(mUser.getUsername());
			queryView.whereDoesNotExist("following");
			initial = queryView.getFirst();
			setImageView(initial);
		} catch (ParseException e2) {
			e2.printStackTrace();
		}
			try {
	            ParseQuery<ParseObject> query = ParseQuery.getQuery(mUser.getUsername());
				query.whereDoesNotExist("following");

				objects = query.find();
			} catch (ParseException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

        		
        gallery.setAdapter(new GalleryImageAdapter(this, objects));
        
        gallery.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                
            	ParseObject obj = objects.get(position);
            	setImageView(obj);

            }

			
        });

        
    }
	
	public void setImageView(ParseObject obj) {
		String com = obj.getString("comment");
        String add = obj.getString("address");
        int ch =  add.indexOf(",");
        String add2;
        if (add.equalsIgnoreCase("noaddress") || add.equalsIgnoreCase("no address"))
        	add2 = "";
        else{
        	add2 = add.substring(ch, add.length());
        	add2 = add2.replaceFirst(",", "");
        }
        add = add.substring(0,ch);
        
        ParseFile test = obj.getParseFile("image");
        try {
        	

        	BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = false;
            options.inPreferredConfig = Config.RGB_565;
            options.inDither = true;
            
			Bitmap bm = BitmapFactory.decodeByteArray(test.getData(), 0, test.getData().length,options);
			//Log.i("WW", bm.getWidth() + " " + bm.getHeight());
			//int width = bm.getWidth();
	        //int height = bm.getHeight();
	        //int newWidth = 200;
	        //int newHeight = 200;

	        //float scaleWidth = ((float) newWidth) / width;
	        //float scaleHeight = ((float) newHeight) / height;
	        //Log.i("AAAAA", scaleWidth + " " +scaleHeight);
	       // Matrix matrix = new Matrix();
	        //matrix.postScale(scaleWidth, scaleHeight);

	        // recreate the new Bitmap
	       // Bitmap bit = Bitmap.createBitmap(bm, 0, 0,
	        //                  width, height, matrix, true);

	        Bitmap bitm = Bitmap.createBitmap(bm);

			imgView.setImageBitmap(bitm);
            text.setText(com);
            address.setText(add);
            address2.setText(add2);
            Log.i("IMG", imgView.getWidth() + " " + imgView.getMeasuredWidth());
            Log.i("IMG", imgView.getHeight() + " " + imgView.getMeasuredHeight());


		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	


}
