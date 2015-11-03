package com.video.pkg;

import java.io.File;

import android.R.string;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.example.videomixer.R;
import com.soundedit.pkg.BaseWizard_Modified;
import com.welcome.pkg.Base;

public class Slide_Engine extends BaseWizard_Modified{

	int Sdur;
	String opPath, sngPth, sngStart, sngEnd;
	private SharedPreferences mPrefs;
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		
		//Load data from Intent only first time
		if(Base.pass1){
			Sdur = getIntent().getExtras().getInt("SDuration");
			opPath = getIntent().getExtras().getString("Oputname");
			
			if(Base.slideAudio){
				sngStart=String.format("%02d:%02d:%02d", getIntent().getExtras().getInt("sngStart")/(60*60), (getIntent().getExtras().getInt("sngStart")/60)%60, getIntent().getExtras().getInt("sngStart")%60);
            	sngEnd=String.format("%02d:%02d:%02d", getIntent().getExtras().getInt("sngEnd")/(60*60), (getIntent().getExtras().getInt("sngEnd")/60)%60, getIntent().getExtras().getInt("sngEnd")%60);
            
            	sngPth=getIntent().getExtras().getString("sng_pth");     
				
			}
			
		}
		else{
			Sdur=mPrefs.getInt("SDuration", 0);
			opPath=mPrefs.getString("Oputname", null);
			
			if(Base.slideAudio){
				sngStart=mPrefs.getString("sngStart", null);
				sngEnd=mPrefs.getString("sngEnd", null);
				sngPth=mPrefs.getString("sng_pth",null);
			}
			
		}
		
		
		
		//Clean up for FFmpeg4Android
        File cleaner=new File("/sdcard/videokit");
        if (cleaner.isDirectory()) {
            String[] children = cleaner.list();
            for (int i = 0; i < children.length; i++) {
                new File(cleaner, children[i]).delete();
            }
        }	
		
        copyLicenseAndDemoFilesFromAssetsToSDIfNeeded();
        
        
		
        if(Base.pass1){        	
        	String scaleDimensions = getIntent().getExtras().getString("res");
        	
        	String[] complexCommand={"ffmpeg","-y","-i","/mnt/sdcard/imgCache/%d.jpg", "-vf", "scale="+scaleDimensions,"-pix_fmt","yuvj420p","mnt/sdcard/imgCache/!%01d.jpg"};
        	setCommandComplex(complexCommand);
        	setProgressDialogTitle("(Step 1/2) Processing Slides...");
            setProgressDialogMessage("Your Slides are being resized...");
        }
        else{
        	if(Base.slideAudio){
        		String[] complexCommand={"ffmpeg","-y","-r","1/"+Sdur,"-i","/mnt/sdcard/imgCache/!%01d.jpg","-ss",sngStart,"-t",sngEnd,"-i",sngPth,"-shortest","-acodec","copy", "-c:v", "libx264","-vf","fps=30", "-pix_fmt", "yuv420p","-preset","ultrafast","/mnt/sdcard/"+opPath+".mp4"};
            	setCommandComplex(complexCommand);
            	setProgressDialogTitle("(Step 2/2) Processing Slides...");
                setProgressDialogMessage("Your Slides are being converted in video...");
                setNotificationIcon(R.drawable.out9);            
                setNotificationMessage("Slides is running...");
        		setNotificationTitle("Slide Conversion");
        		setNotificationfinishedMessageTitle("Slide Conversion finished");
        		setNotificationfinishedMessageDesc("Click to play");
        		setNotificationStoppedMessage("Slide Conversion stopped");
        	}
        	else{
        		String[] complexCommand={"ffmpeg","-y","-r","1/"+Sdur,"-i","/mnt/sdcard/imgCache/!%01d.jpg", "-c:v", "libx264", "-r","25", "-pix_fmt", "yuv420p","-preset","ultrafast","/mnt/sdcard/"+opPath+".mp4"};
            	setCommandComplex(complexCommand);
            	setProgressDialogTitle("(Step 2/2) Processing Slides...");
                setProgressDialogMessage("Your Slides are being converted in video...");
                setNotificationIcon(R.drawable.out9);            
                setNotificationMessage("Slides is running...");
        		setNotificationTitle("Slide Conversion");
        		setNotificationfinishedMessageTitle("Slide Conversion finished");
        		setNotificationfinishedMessageDesc("Click to play");
        		setNotificationStoppedMessage("Slide Conversion stopped");
        	}
        	
        	
        }
            
           	
        
        runTranscoing();
	}
	
	@Override
	public void onBackPressed() {
		
		//Clean ingCache on release
        File cleaner=new File("/sdcard/imgCache");
        if (cleaner.isDirectory()) {
            String[] children = cleaner.list();
            for (int i = 0; i < children.length; i++) {
                new File(cleaner, children[i]).delete();
            }
        }	
		
		Intent homeIntent = new Intent(this, Base.class);
	    homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	    startActivity(homeIntent);
	    finish();
	}
	
	protected void onPause() {
        super.onPause();

        SharedPreferences.Editor ed = mPrefs.edit();
        
        if(Base.slideAudio){
        	ed.putString("sng_pth", sngPth);
        	ed.putString("sngEnd", sngEnd);
        	ed.putString("sngStart", sngStart);
        }
        
        //ed.putIntArray("Bundled_vipos",vidPos);
        //ed.putStringArray("Bundled_frmmtedPos",frmmtedPos);
        ed.putString("Oputname", opPath);
        ed.putInt("SDuration", Sdur);        
        ed.commit();
    }
	
}
