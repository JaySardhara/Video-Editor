package com.soundedit.pkg;

import java.io.File;

import com.example.videomixer.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class After_edit_snd extends BaseWizard_Modified{
	
	Context context;
	static Boolean IsSave=false;
	int current_min_pos=(new Editor()).getCurrentMinPos();
	int current_max_pos=(new Editor()).getCurrentMaxPos();
	String src_vid=(new AudioScrapper()).VidPath();
	String dest=(new AudioScrapper()).AudPth();
	String formatted_start_pos = String.format("%02d:%02d:%02d", current_min_pos/(60*60), (current_min_pos/60)%60, current_min_pos%60);
	String formatted_Diff_endpos = String.format("%02d:%02d:%02d", (current_max_pos-current_min_pos)/(60*60), (current_max_pos-current_min_pos)/60%60, (current_max_pos-current_min_pos)%60);
		
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
				
		setContentView(R.layout.after_snd_edit);
		
		context=this;
		
		Button jst_save = (Button)findViewById(R.id.save_snd);
		Button paste_over_vid= (Button)findViewById(R.id.for_vid);
		
		//Clean up for FFmpeg4Android
        File cleaner=new File("/sdcard/videokit");
        if (cleaner.isDirectory()) {
            String[] children = cleaner.list();
            for (int i = 0; i < children.length; i++) {
                new File(cleaner, children[i]).delete();
            }
        }	
		
		copyLicenseAndDemoFilesFromAssetsToSDIfNeeded();		
		
		Log.w("bferformat",String.valueOf(current_min_pos)+"asdasd "+String.valueOf(current_max_pos));
		Log.w("fterfrmat",formatted_start_pos+"    "+formatted_Diff_endpos);
		jst_save.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				
				AlertDialog.Builder save = new AlertDialog.Builder(context);
				save.setTitle("Save");
				save.setMessage("Save Sound As:");
				
				final EditText fname = new EditText(getBaseContext());
				fname.setBackgroundColor(Color.GRAY);
				fname.setTextColor(Color.BLACK);
				fname.setCursorVisible(true);
				save.setView(fname);
				
				save.setPositiveButton("Save", new DialogInterface.OnClickListener() {					
					
					public void onClick(DialogInterface dialog, int which) {
						
						String name=fname.getText().toString().trim();
						IsSave=true;
						
						//Start trim and save audio
						//commandStr = "ffmpeg -y -strict -2 -ss 00:00:05 -i /sdcard/psy.mp4 -t 00:00:20 -map 0:1 -b:a 96k -strict -2 /sdcard/audio_scrapper/trimed.m4a";
						String temp_out="mnt/sdcard/"+name+".m4a";
						String[] selected_aud ={ "ffmpeg","-y","-ss",formatted_start_pos,"-i",src_vid,"-t",formatted_Diff_endpos,"-map","0:1","-b:a","90k","-strict","-2",temp_out};
						Log.d("cmnd",selected_aud.toString());
						setCommandComplex(selected_aud);
						setOutputFilePath(temp_out);
						setProgressDialogTitle("Saving..");
						setProgressDialogMessage("Your selected Audio is being Saved..");
						runTranscoing();
						//releaseService();									
					
						return;
					}
				});
				
				save.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
										
					public void onClick(DialogInterface dialog, int which) {
						
						return;
					}
				});
				
				save.show();
				
			}
		});
		
		
		
	}
	
}
