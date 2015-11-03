package com.video.pkg;

import com.example.videomixer.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MergeSelector extends Activity{

	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.vid_edit_moduler);
		
		Button merger2=(Button)findViewById(R.id.initiateSE);
		
		merger2.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				
				//getContentResolver().delete(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,MediaColumns.DATA+"='"+, null);
				
				Intent i=new Intent(getBaseContext(),CustomGallery_VidMerge.class);
				startActivity(i);
			}
		});
		
		
	}
	
}
