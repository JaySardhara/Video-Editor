package com.video.pkg;

import java.io.File;

import android.content.Intent;
import android.os.Bundle;

import com.example.videomixer.R;
import com.netcompss.ffmpeg4android_client.BaseWizard;
import com.welcome.pkg.Base;

public class VidRotate_Engine extends BaseWizard{

	float rotate_angle;
	String src_pth;
	String[] complexCommand;
	
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
		
        rotate_angle = getIntent().getExtras().getInt("rotate");
        src_pth = getIntent().getExtras().getString("pth");
        
        if((int)rotate_angle==-90 || (int)rotate_angle==270){
        	complexCommand=new String[]{"ffmpeg","-y","-i","/mnt/sdcard/Cchk2.mp4", "-vf","transpose=0","-c:a","copy","-preset","ultrafast","/mnt/sdcard/rotated_vid.mp4"};
        }
        else if((int)rotate_angle==90 || (int)rotate_angle==-270){
        	complexCommand=new String[]{"ffmpeg","-y","-i","/mnt/sdcard/Cchk2.mp4", "-vf","transpose=1","-c:a","copy","-preset","ultrafast","/mnt/sdcard/rotated_vid.mp4"};
        }
        else if((int)rotate_angle==-180 || (int)rotate_angle==180){
        	complexCommand=new String[]{"ffmpeg","-y","-i","/mnt/sdcard/Cchk2.mp4", "-vf","vflip,hflip","-c:a","copy","-preset","ultrafast","/mnt/sdcard/rotated_vid.mp4"};
        }
        
        setCommandComplex(complexCommand);

		///optional////
		setOutputFilePath("/mnt/sdcard/rotated_vid.mp4");
		//setProgressDialogTitle("Exporting As MP4 Video");
		//setProgressDialogMessage("Depends on your video size, it can take a few minutes");
		setNotificationIcon(R.drawable.out3);
		setNotificationMessage("Video Rotate is running...");
		setNotificationTitle("Video Rotate");
		setNotificationfinishedMessageTitle("Video Rotating finished");
		setNotificationfinishedMessageDesc("Click to play");
		setNotificationStoppedMessage("Video Rotation stopped");
		runTranscoing();
        
        
        
	}
	
	@Override
	public void onBackPressed() {
		
		Intent homeIntent = new Intent(this, Base.class);
	    homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	    startActivity(homeIntent);
	}
	
	
}
