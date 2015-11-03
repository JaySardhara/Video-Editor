package com.video.pkg;

import com.example.videomixer.R;
import com.soundedit.pkg.AudioScrapper;
import com.soundedit.pkg.Initiator;
import com.soundedit.pkg.Aud_Vid_Merger;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class Welcome extends Activity{
	
	//Set flags for behavior of BaseWizard_Aud class 
	//in onServiceFinished method
	public static Boolean startEditor=false,startCombiner=false,isAudioTrimmed=false;	
	
	static String src_path;
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.welcome);
		
		Button snd=(Button)findViewById(R.id.initiateSE);
		Button vm=(Button)findViewById(R.id.initiateVM);
		
		snd.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				
				Intent picker = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
				picker.setType("video/*");				
				startActivityForResult(picker,1);
			}
		});
		
		vm.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				Intent i =new Intent(getBaseContext(),MergeSelector.class);				
				startActivity(i);
			}
		});		
		
	}
	
	protected void  onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 1 && resultCode == RESULT_OK) {              
			Uri uri=data.getData();
			Log.d("uri",uri.toString());
			//URI to absolute path
			
			Cursor cursor = null;
			  try { 
			    String[] proj = { MediaStore.Video.Media.DATA };
			    cursor = this.getContentResolver().query(uri, proj, null, null, null);
			    int column_index = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
			    cursor.moveToFirst();
			    src_path = cursor.getString(column_index);
			  } finally {
			    if (cursor != null) {
			      cursor.close();
			    }
			  }
			
			  Intent lncher=new Intent(this,AudioScrapper.class);
			  lncher.putExtra("pth",src_path);
			  startActivity(lncher);
				/*		
			
				Intent i=new Intent(this,VMixer.class);
				i.putExtra("pth",src_path);
				startActivity(i);*/
			
        }  
	}
	
}
