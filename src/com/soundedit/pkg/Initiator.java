package com.soundedit.pkg;

import android.app.Activity;

/** This is initial Activity which is updated as per requirement.
 * 	Used to call AudioScrapper to get audio from video
 *  Also used to call VMixer for video merging
 *  
 * @author Jay Sardhara
*/

public class Initiator extends Activity{ /**Barren class*/
/*
	//Set flags for behavior of BaseWizard_Aud class 
	//in onServiceFinished method
	static Boolean startEditor=false,startCombiner=false,isAudioTrimmed=false;	
	
	public String src_path;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.initiator);
		
		Button selector=(Button)findViewById(R.id.vidselector);
		
		selector.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent picker = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
				picker.setType("video/*");				
				startActivityForResult(picker,1);			
				
			}
		});
		
	}	
	
	
	
	protected void  onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 1 && resultCode == RESULT_OK) {              
			Uri uri=data.getData();
			
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
			
			Log.d("as",src_path);
			if(Welcome.aud_scrap){
				Intent lncher=new Intent(this,AudioScrapper.class);
				lncher.putExtra("pth",src_path);
				startActivity(lncher);
			}
			else{
				Intent i=new Intent(this,VMixer.class);
				i.putExtra("pth",src_path);
				startActivity(i);
			}
        }  
		
		
		
	}*/
}
