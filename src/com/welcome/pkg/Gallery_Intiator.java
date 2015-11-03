package com.welcome.pkg;

import com.soundedit.pkg.Aud_Convert;
import com.soundedit.pkg.AudioScrapper;
import com.video.pkg.Vid_Convert;
import com.video.pkg.Vid_Rotater;
import com.video.pkg.Vid_SndEdit;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;

public class Gallery_Intiator extends Activity{

	String src_path;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		if(Base.isAudConvert){
			Intent picker = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);							
			startActivityForResult(picker,1);
		}
		else{
			Intent picker = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
			picker.setType("video/*");				
			startActivityForResult(picker,1);			
		}
				
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
			
			  
			  if(Base.isVidEdit){
				  Base.isVidEdit=true;
				  Intent lncher=new Intent(this,AudioScrapper.class);
				  lncher.putExtra("pth",src_path);
				  startActivity(lncher);
			  }
			  else if(Base.isVidRotate){
				  Base.isVidRotate=false;
				  Intent lncher=new Intent(this,Vid_Rotater.class);
				  lncher.putExtra("pth",src_path);
				  startActivity(lncher);	
			  }
			  else if(Base.isVidConvert){
				  Base.isVidConvert=false;
				  Intent lncher=new Intent(this,Vid_Convert.class);
				  lncher.putExtra("pth",src_path);
				  startActivity(lncher);	
			  }
			  else if(Base.isAudConvert){
				  Base.isAudConvert=false;
				  Intent lncher=new Intent(this,Aud_Convert.class);
				  lncher.putExtra("pth", src_path);
				  startActivity(lncher);
			  }
			  else if(Base.isVidAudEdit){
				  Base.isVidAudEdit=true;
				  Intent lncher=new Intent(this,AudioScrapper.class);
				  lncher.putExtra("pth",src_path);
				  startActivity(lncher);	
			  }
			  
			
        }  
	}
	
}
