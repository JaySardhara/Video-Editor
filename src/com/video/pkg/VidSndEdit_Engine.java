package com.video.pkg;

import java.io.File;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.videomixer.R;
import com.soundedit.pkg.BaseWizard_Modified;
import com.welcome.pkg.Base;

public class VidSndEdit_Engine extends BaseWizard_Modified{

	String vidPth, audPth, opPth;
	int vidStart, vidEnd, sngStart, sngEnd, duration;
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		//Clean up for FFmpeg4Android
        File cleaner=new File("/sdcard/videokit");
        if (cleaner.isDirectory()) {
            String[] children = cleaner.list();
            for (int i = 0; i < children.length; i++) {
                new File(cleaner, children[i]).delete();
            }
        }	
		
        copyLicenseAndDemoFilesFromAssetsToSDIfNeeded();
        
        vidPth=getIntent().getExtras().getString("vid_pth");
        audPth=getIntent().getExtras().getString("sng_pth");
        opPth=getIntent().getExtras().getString("Oputname");
        vidStart=getIntent().getExtras().getInt("vidStart");
        vidEnd=getIntent().getExtras().getInt("vidEnd");
        sngStart=getIntent().getExtras().getInt("sngStart");
        sngEnd=getIntent().getExtras().getInt("sngEnd");
        duration=getIntent().getExtras().getInt("duration");
        
        Log.e("!!!!",vidPth+" "+audPth+" "+opPth+" "+String.valueOf(vidStart)+" "+String.valueOf(vidEnd)+" "+String.valueOf(sngStart)+" "+String.valueOf(sngEnd)+" "+String.valueOf(duration));
        
        int temp = vidStart + duration;
        
        String[] complexCommand={"ffmpeg","-y","-i",vidPth,"-i",audPth,"-filter_complex","[0:a]atrim=end="+vidStart+",asetpts=PTS-STARTPTS[aud1];[1:a]atrim="+sngStart+":"+sngEnd+",asetpts=PTS-STARTPTS[aud2];[0:a]atrim=start="+temp+",asetpts=PTS-STARTPTS[aud3];[aud1][aud2][aud3]concat=n=3:v=0:a=1[aout]","-map","0:v","-map","[aout]","-c:v","copy","-strict","-2","/mnt/sdcard/"+opPth+".mp4"};
        
        setCommandComplex(complexCommand);
		setProgressDialogTitle("Processing...");
        setProgressDialogMessage("Editing Audio in Video...");
        setNotificationIcon(R.drawable.out6);
        //setProgressDialogTitle("");
        //setProgressDialogMessage("");
        setNotificationMessage("Edit Sound in Video is running...");
		setNotificationTitle("Edit Sound in Video");
		setNotificationfinishedMessageTitle("Edit Sound in Video finished");
		setNotificationfinishedMessageDesc("Click to play");
		setNotificationStoppedMessage("Edit Sound in Video stopped");
		runTranscoing();
	}
	
	public void onBackPressed(){
		Intent i = new Intent(getBaseContext(),Base.class);
		i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(i);
		finish();
	}
	
}

