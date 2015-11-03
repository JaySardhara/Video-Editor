package com.soundedit.pkg;

import java.io.File;

import com.video.pkg.Welcome;

import android.os.Bundle;
import android.util.Log;

public class Combiner extends BaseWizard_Modified{	
	
	static Boolean finish=false;
	int current_min_pos=(new Editor()).getCurrentMinPos();
	int current_max_pos=(new Editor()).getCurrentMaxPos();
	String src_vid=(new AudioScrapper()).VidPath();
	String dest=(new AudioScrapper()).AudPth();
	String formatted_start_pos = String.format("%02d:%02d:%02d", current_min_pos/(60*60), (current_min_pos/60)%60, current_min_pos%60);
	String formatted_Diff_endpos = String.format("%02d:%02d:%02d", (current_max_pos-current_min_pos)/(60*60), (current_max_pos-current_min_pos)/60%60, (current_max_pos-current_min_pos)%60);
	
	public void onCreate(Bundle saBundle){
		super.onCreate(saBundle);
		
		//Clean up for FFmpeg4Android
        File cleaner=new File("/sdcard/videokit");
        if (cleaner.isDirectory()) {
            String[] children = cleaner.list();
            for (int i = 0; i < children.length; i++) {
                new File(cleaner, children[i]).delete();
            }
        }	
		    
        
        if(!Welcome.isAudioTrimmed)
        	Trimmer();
        else
        	Finalizer();
        
		
	}

	private void Finalizer() {
		finish=true;
		//commandStr="ffmpeg -y -strict experimental -i /sdcard/psy.mp4 -itsoffset 00:00:20 -i /sdcard/audio_scrapper/trimed.m4a -map 0:0 -map 1:0 -c:v copy -preset ultrafast -strict -2 /sdcard/Final_AudTrimmed.mp4";
		String temp_out=dest+"/Final_AudTrimmed.mp4";
		String temp_aud_src=dest+"/trimed.m4a";
		String[] mixer={"ffmpeg","-y","-strict","experimental","-i",src_vid,"-itsoffset",formatted_start_pos,"-i",temp_aud_src,"-map","0:0","-map","1:0","-c:v","copy","-preset","ultrafast","-async","1","-strict","-2",temp_out};
		setCommandComplex(mixer);
		setOutputFilePath(dest+"/Final_AudTrimmed.mp4");
		setProgressDialogTitle("Step 2/2");
		setProgressDialogMessage("Finalizing Video with Trimmed Audio");
		runTranscoing();
	}

	private void Trimmer() {	
		//commandStr = "ffmpeg -y -strict -2 -ss 00:00:05 -i /sdcard/psy.mp4 -t 00:00:20 -map 0:1 -b:a 96k -strict -2 /sdcard/audio_scrapper/trimed.m4a";
		String temp_out=dest+"/trimed.m4a";
		String[] selected_aud ={ "ffmpeg","-y","-ss",formatted_start_pos,"-i",src_vid,"-t",formatted_Diff_endpos,"-map","0:1","-b:a","90k","-strict","-2",temp_out};
		Log.d("cmnd",selected_aud.toString());
		setCommandComplex(selected_aud);
		setOutputFilePath(dest+"/trimed.m4a");
		setProgressDialogTitle("Step 1/2");
		setProgressDialogMessage("Trimming the Selected Audio from Video");
		runTranscoing();
		//releaseService();			
		
		Welcome.isAudioTrimmed=true;
		Log.d("frmted",formatted_start_pos);Log.d("frm",formatted_Diff_endpos);
	}	
	
}
