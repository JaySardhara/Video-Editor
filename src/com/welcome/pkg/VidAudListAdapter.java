package com.welcome.pkg;


import com.example.videomixer.R;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class VidAudListAdapter extends ArrayAdapter<String>{
	
	private final Activity context;
	private final String[] values;
	private final Integer[] imageId;
	
	public VidAudListAdapter(Activity context, String[] values,Integer[] imageId) {
		super(context, R.layout.vid_aud_frag_adapter ,values);
		this.context = context;
	    this.values = values;		
	    this.imageId = imageId;
	}
	
	@Override
	public View getView(int position, View view, ViewGroup parent) {
		LayoutInflater inflater = context.getLayoutInflater();
		
		View v = inflater.inflate(R.layout.vid_aud_frag_adapter, null, true);
		TextView txtTitle = (TextView) v.findViewById(R.id.list_description);
		ImageView imageView = (ImageView) v.findViewById(R.id.list_image);
		
		txtTitle.setText(values[position]);
		imageView.setImageResource(imageId[position]);
		
		return v;
	}
}
