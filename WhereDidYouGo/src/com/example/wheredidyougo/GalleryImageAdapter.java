package com.example.wheredidyougo;

import java.util.ArrayList;
import java.util.List;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseObject;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;

@SuppressWarnings("deprecation")
public class GalleryImageAdapter extends BaseAdapter {
    private Context mContext;
    public ArrayList<ParseFile> photos = new ArrayList<ParseFile>();
	LayoutInflater inflater;
    List<ParseObject> objects;


    
   
    public GalleryImageAdapter(Context context, List<ParseObject> objects) 
    {
        mContext = context;
        for(ParseObject obj:objects) {
        	photos.add(obj.getParseFile("image"));
        }
        Log.i("SIZE", " " + photos.size());
  
    }

    public int getCount() {
        return photos.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }


    public View getView(int index, View view, ViewGroup viewGroup) 
    {
        // TODO Auto-generated method stub
        ParseImageView image = new ParseImageView(mContext);
        ParseFile test = photos.get(index);
        try {

			
			Bitmap bm = BitmapFactory.decodeByteArray(test.getData(), 0, test.getData().length);
			image.setImageBitmap(bm);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
        image.setLayoutParams(new Gallery.LayoutParams(150, 150));
    
        image.setScaleType(ParseImageView.ScaleType.FIT_XY);

        return image;
    }
    
   
}
