package com.soundedit.pkg;

import java.io.File;

import android.os.Bundle;
import android.util.Log;

import com.example.videomixer.R;
import com.netcompss.ffmpeg4android_client.BaseWizard;

public class AudConvert_Engine extends BaseWizard{

	//These booleans are set only if user has specified any of them
	Boolean isSample=false, isBit=false;
	String sample="",bitrate="", audPath, savePath;
	String[] complexCommand;
	
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		String orgnlFrmt = getIntent().getExtras().getString("origFormat"); //currently only mp3
		String fnlFrmt = getIntent().getExtras().getString("convertTo");
		audPath = getIntent().getExtras().getString("audpath");
		savePath=getIntent().getExtras().getString("saveTo");
		
		//Modify savePath...Make change here to add to specific folder
		savePath="/mnt/sdcard/"+savePath+"."+fnlFrmt;
		Log.w("chkPath",savePath);
		
		//Only if Booleans are set, initialize; else take default
		if(!getIntent().getExtras().getBoolean("bRate")){
			isSample=true;
			sample = getIntent().getExtras().getString("sample");
		}		
		
		if(!getIntent().getExtras().getBoolean("bBitrate")){
			isBit=true;
			bitrate = getIntent().getExtras().getString("bitrate");
			bitrate = bitrate + "k" ;
		}
		
		Log.w("intentBools",String.valueOf(isSample)+String.valueOf(isBit));
		Log.w("crit",orgnlFrmt+" "+fnlFrmt+" "+sample+" "+bitrate);
		
		//Clean up for FFmpeg4Android
        File cleaner=new File("/sdcard/videokit");
        if (cleaner.isDirectory()) {
            String[] children = cleaner.list();
            for (int i = 0; i < children.length; i++) {
                new File(cleaner, children[i]).delete();
            }
        }	
		
        copyLicenseAndDemoFilesFromAssetsToSDIfNeeded();
        
        if(orgnlFrmt.equals("mp3")){
        	if(fnlFrmt.equals("m4a"))
        		mp3Tomp4a();
        	else if(fnlFrmt.equals("aac"))
        		mp3Toaac();
        	else if(fnlFrmt.equals("amr/3gp"))
        		mp3Toamr();
        	else if(fnlFrmt.equals("wmv"))
        		mp3Towmv();
        	else if(fnlFrmt.equals("wav"))
        		mp3Towav();
        }        
       
        
        
        
        setCommandComplex(complexCommand);
        setNotificationIcon(R.drawable.out5);
        setNotificationMessage("Audio Conversion is running...");
		setNotificationTitle("Audio Conversion");
		setNotificationfinishedMessageTitle("Audio Conversion finished");
		setNotificationfinishedMessageDesc("Click to play");
		setNotificationStoppedMessage("Audio Conversion stopped");
		runTranscoing();        
		
	
	}


	private void mp3Towav() {
		if(!isBit && !isSample)
			commandComplex =new String[]{"ffmpeg","-y","-i",audPath,"-strict","-2",savePath};
		else if(!isBit && isSample)
			commandComplex =new String[]{"ffmpeg","-y","-i",audPath,"-vn","-ar",sample,"-strict","-2",savePath};
		else if(isBit && !isSample)
			commandComplex =new String[]{"ffmpeg","-y","-i",audPath,"-vn","-ab",bitrate,"-strict","-2",savePath};
		else if(isBit && isSample)
			commandComplex =new String[]{"ffmpeg","-y","-i",audPath,"-vn","-ab",bitrate,"-ar",sample,"-strict","-2",savePath};
		
		
	}


	private void mp3Towmv() {
		
		if(!isBit && !isSample)
			commandComplex =new String[]{"ffmpeg","-y","-i",audPath,"-vn","-strict","-2",savePath};
		else if(!isBit && isSample)
			commandComplex =new String[]{"ffmpeg","-y","-i",audPath,"-vn","-ar",sample,"-strict","-2",savePath};
		else if(isBit && !isSample)
			commandComplex =new String[]{"ffmpeg","-y","-i",audPath,"-vn","-ab",bitrate,"-strict","-2",savePath};
		else if(isBit && isSample)
			commandComplex =new String[]{"ffmpeg","-y","-i",audPath,"-vn","-ab",bitrate,"-ar",sample,"-strict","-2",savePath};
		
	}


	private void mp3Toamr() {
		
		commandComplex =new String[]{"ffmpeg","-y","-i",audPath,"-vn","-ab","12k","-ar","8000","-strict","-2",savePath};	
	}


	private void mp3Toaac() {
		if(!isBit && !isSample)
			commandComplex =new String[]{"ffmpeg","-y","-i",audPath,"-vn","-strict","-2",savePath};
		else if(!isBit && isSample)
			commandComplex =new String[]{"ffmpeg","-y","-i",audPath,"-vn","-ar",sample,"-strict","-2",savePath};
		else if(isBit && !isSample)
			commandComplex =new String[]{"ffmpeg","-y","-i",audPath,"-vn","-ab",bitrate,"-strict","-2",savePath};
		else if(isBit && isSample)
			commandComplex =new String[]{"ffmpeg","-y","-i",audPath,"-vn","-ab",bitrate,"-ar",sample,"-strict","-2",savePath};
		
	}


	private void mp3Tomp4a() {
		if(!isBit && !isSample)
			commandComplex =new String[]{"ffmpeg","-y","-i",audPath,"-vn","-strict","-2",savePath};
		else if(!isBit && isSample)
			commandComplex =new String[]{"ffmpeg","-y","-i",audPath,"-vn","-ar",sample,"-strict","-2",savePath};
		else if(isBit && !isSample)
			commandComplex =new String[]{"ffmpeg","-y","-i",audPath,"-vn","-ab",bitrate,"-strict","-2",savePath};
		else if(isBit && isSample)
			commandComplex =new String[]{"ffmpeg","-y","-i",audPath,"-vn","-ab",bitrate,"-ar",sample,"-strict","-2",savePath};
	}
	
	
}
