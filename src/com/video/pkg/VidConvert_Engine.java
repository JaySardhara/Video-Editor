package com.video.pkg;

import java.io.File;

import android.os.Bundle;
import android.util.Log;

import com.example.videomixer.R;
import com.netcompss.ffmpeg4android_client.BaseWizard;

public class VidConvert_Engine extends BaseWizard{

	//These booleans are set only if user has specified any of them
	Boolean isRes=false, isFps=false, isBit=false, isChan=false;
	String res="",fps="",bitrate="",channel="", vidPath, savePath;
	String[] complexCommand;
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		String orgnlFrmt = getIntent().getExtras().getString("origFormat");
		String fnlFrmt = getIntent().getExtras().getString("convertTo");
		vidPath = getIntent().getExtras().getString("vidpath");
		savePath=getIntent().getExtras().getString("saveTo");
		//Modify savePath...Make change here to add to specific folder
		savePath="/mnt/sdcard/"+savePath+"."+fnlFrmt;
		Log.w("chkPath",savePath);
		
		//Only if Booleans are set, initialize; else take default
		if(!getIntent().getExtras().getBoolean("bRes")){
			isRes=true;
			res = getIntent().getExtras().getString("res");
		}		
		if(!getIntent().getExtras().getBoolean("bFps")){
			isFps=true;
			fps = getIntent().getExtras().getString("fps");
		}
		if(!getIntent().getExtras().getBoolean("bBit")){
			isBit=true;
			bitrate = getIntent().getExtras().getString("bitrate");
			bitrate = bitrate + "k" ;
		}
		if(!getIntent().getExtras().getBoolean("bAc")){
			isChan=true;
			channel = getIntent().getExtras().getString("channel");
		}
		Log.w("intentBools",String.valueOf(isRes)+String.valueOf(isFps)+String.valueOf(isBit)+String.valueOf(isChan));
		Log.w("crit",orgnlFrmt+" "+fnlFrmt+" "+res+" "+fps+" "+bitrate+" "+channel);
		
		//Clean up for FFmpeg4Android
        File cleaner=new File("/sdcard/videokit");
        if (cleaner.isDirectory()) {
            String[] children = cleaner.list();
            for (int i = 0; i < children.length; i++) {
                new File(cleaner, children[i]).delete();
            }
        }	
		
        copyLicenseAndDemoFilesFromAssetsToSDIfNeeded();
        
        if(orgnlFrmt.equals("mp4")){
        	if(fnlFrmt.equals("avi"))
        		mp4Toavi();
        	else if(fnlFrmt.equals("flv"))
        		mp4Toflv();
        	else if(fnlFrmt.equals("mov"))
        		mp4Tomov();
        	else if(fnlFrmt.equals("3gp"))
        		mp4To3gp();
        }        
        else if(orgnlFrmt.equals("mov")){
        	if(fnlFrmt.equals("mp4"))
        		movTomp4();
        	else if(fnlFrmt.equals("flv"))
        		movToflv();
        	else if(fnlFrmt.equals("avi"))
        		movToavi();
        	else if(fnlFrmt.equals("3gp"))
        		movTo3gp();
        }        
        else if(orgnlFrmt.equals("avi")){
        	if(fnlFrmt.equals("mp4"))
        		aviTomp4();
        	else if(fnlFrmt.equals("flv"))
        		aviToflv();
        	else if(fnlFrmt.equals("3gp"))
        		aviTo3gp();
        	else if(fnlFrmt.equals("mov"))
        		aviTomov();            
        }       
        else if(orgnlFrmt.equals("flv")){
        	if(fnlFrmt.equals("mp4"))
        		flvTomp4();
        	else if(fnlFrmt.equals("3gp"))
        		flvTo3gp();
        	else if(fnlFrmt.equals("mov"))
        		flvTomov();
        	else if(fnlFrmt.equals("avi"))
        		flvToavi();
        }
        else{
        	if(fnlFrmt.equals("mp4"))
        		gpTomp4();
        	else if(fnlFrmt.equals("flv"))
        		gpToflv();
        	else if(fnlFrmt.equals("mov"))
        		gpTomov();
        	else if(fnlFrmt.equals("avi"))
        		gpToavi();
        }
        
        
        
        setCommandComplex(complexCommand);
        setNotificationIcon(R.drawable.out4);
        setNotificationMessage("Video Conversion is running...");
		setNotificationTitle("Video Conversion");
		setNotificationfinishedMessageTitle("Video Conversion finished");
		setNotificationfinishedMessageDesc("Click to play");
		setNotificationStoppedMessage("Video Conversion stopped");
		runTranscoing();        
		
	}

	private void gpToavi() {
		// TODO Auto-generated method stub
		
	}

	private void gpTomov() {
		// TODO Auto-generated method stub
		
	}

	private void gpToflv() {
		// TODO Auto-generated method stub
		
	}

	private void gpTomp4() {
		// TODO Auto-generated method stub
		
	}

	private void flvToavi() {
		// TODO Auto-generated method stub
		
	}

	private void flvTomov() {
		// TODO Auto-generated method stub
		
	}

	private void flvTo3gp() {
		// TODO Auto-generated method stub
		
	}

	private void flvTomp4() {
		// TODO Auto-generated method stub
		
	}

	private void aviTomov() {
		// TODO Auto-generated method stub
		
	}

	private void aviTo3gp() {
		// TODO Auto-generated method stub
		
	}

	private void aviToflv() {
		// TODO Auto-generated method stub
		
	}

	private void aviTomp4() {
		// TODO Auto-generated method stub
		
	}

	private void movTo3gp() {
		// TODO Auto-generated method stub
		
	}

	private void movToavi() {
		// TODO Auto-generated method stub
		
	}

	private void movToflv() {
		// TODO Auto-generated method stub
		
	}

	private void movTomp4() {
		//Same as mp4Tomov() as mov and mp4 are similar containers
		mp4Tomov();
		
	}

	private void mp4To3gp() {
		if(!isBit && !isChan && !isFps && !isRes)
			complexCommand=new String[]{"ffmpeg","-y","-i",vidPath, "-s","352x288","-ac","1","-b:a","12k","-ar","8000" ,"-strict","-2",savePath};
		else if(!isBit && !isChan && !isFps && isRes)
			complexCommand=new String[]{"ffmpeg","-y","-i",vidPath, "-s",res,"-ac","1","-b:a","12k","-ar","8000" ,"-strict","-2",savePath};
		
	}

	private void mp4Tomov() {
		if(!isBit && !isChan && !isFps && !isRes)
			complexCommand=new String[]{"ffmpeg","-y","-i",vidPath, "-acodec","copy","-vcodec","copy","-strict","-2",savePath};
		else if(!isBit && !isChan && !isFps && isRes)
			complexCommand=new String[]{"ffmpeg","-y","-i",vidPath, "-s",res ,"-acodec","copy","-strict","-2",savePath};
		else if(!isBit && !isChan && isFps && isRes)
			complexCommand=new String[]{"ffmpeg","-y","-i",vidPath, "-s",res ,"-r",fps ,"-acodec","copy","-preset","ultrafast","-strict","-2",savePath};
		else if(!isBit && isChan && !isFps && isRes)
			complexCommand=new String[]{"ffmpeg","-y","-i",vidPath, "-s",res ,"-ac",channel,"-preset","ultrafast","-strict","-2",savePath};
		else if(isBit && !isChan && !isFps && isRes)
			complexCommand=new String[]{"ffmpeg","-y","-i",vidPath, "-s",res ,"-ab",bitrate,"-strict","-2",savePath};
		else if(!isBit && !isChan && isFps && !isRes)
			complexCommand=new String[]{"ffmpeg","-y","-i",vidPath, "-r",fps ,"-acodec","copy","-preset","ultrafast","-strict","-2",savePath};
		else if(!isBit && isChan && isFps && !isRes)
			complexCommand=new String[]{"ffmpeg","-y","-i",vidPath, "-r",fps ,"-ac",channel,"-preset","ultrafast","-strict","-2",savePath};
		else if(isBit && !isChan && isFps && !isRes)
			complexCommand=new String[]{"ffmpeg","-y","-i",vidPath, "-r",fps ,"-ab",bitrate,"-preset","ultrafast","-strict","-2",savePath};
		else if(!isBit && isChan && !isFps && !isRes)
			complexCommand=new String[]{"ffmpeg","-y","-i",vidPath, "-ac",channel,"-vcodec","copy","-strict","-2",savePath};
		else if(isBit && isChan && !isFps && !isRes)
			complexCommand=new String[]{"ffmpeg","-y","-i",vidPath, "-ab",bitrate,"-ac",channel,"-vcodec","copy","-strict","-2",savePath};
		else if(isBit && !isChan && !isFps && !isRes)
			complexCommand=new String[]{"ffmpeg","-y","-i",vidPath,"-ab",bitrate,"-vcodec","copy","-strict","-2",savePath};
		else if(isBit && isChan && isFps && isRes)
			complexCommand=new String[]{"ffmpeg","-y","-i",vidPath,"-ab",bitrate,"-ac",channel, "-s",res , "-r",fps ,"-strict","-2",savePath};
		
		
	}

	private void mp4Toflv() {
		if(!isBit && !isChan && !isFps && !isRes)
			complexCommand=new String[]{"ffmpeg","-y","-i",vidPath, "-c:v","libx264","-strict","-2",savePath};
		else if(!isBit && !isChan && !isFps && isRes)
			complexCommand=new String[]{"ffmpeg","-y","-i",vidPath, "-s",res ,"-c:v","libx264","-strict","-2",savePath};
		else if(!isBit && !isChan && isFps && !isRes)
			complexCommand=new String[]{"ffmpeg","-y","-i",vidPath, "-r",fps ,"-c:v","libx264","-strict","-2",savePath};
		else if(!isBit && isChan && !isFps && !isRes)
			complexCommand=new String[]{"ffmpeg","-y","-i",vidPath, "-c:v","libx264","-ac",channel,"-strict","-2",savePath};
		else if(isBit && !isChan && !isFps && !isRes)
			complexCommand=new String[]{"ffmpeg","-y","-i",vidPath, "-c:v","libx264","-ab",bitrate,"-strict","-2",savePath};
		else if(!isBit && !isChan && isFps && isRes)
			complexCommand=new String[]{"ffmpeg","-y","-i",vidPath, "-s",res ,"-r",fps ,"-c:v","libx264","-strict","-2",savePath};
		else if(!isBit && isChan && !isFps && isRes)
			complexCommand=new String[]{"ffmpeg","-y","-i",vidPath, "-s",res ,"-c:v","libx264","-ac",channel,"-strict","-2",savePath};
		else if(isBit && !isChan && !isFps && isRes)
			complexCommand=new String[]{"ffmpeg","-y","-i",vidPath, "-s",res ,"-c:v","libx264","-ab",bitrate,"-strict","-2",savePath};
		else if(!isBit && isChan && isFps && !isRes)
			complexCommand=new String[]{"ffmpeg","-y","-i",vidPath, "-r",fps ,"-c:v","libx264","-ac",channel,"-strict","-2",savePath};
		else if(isBit && !isChan && isFps && !isRes)
			complexCommand=new String[]{"ffmpeg","-y","-i",vidPath, "-r",fps ,"-c:v","libx264","-ab",bitrate,"-strict","-2",savePath};
		else if(isBit && isChan && !isFps && !isRes)
			complexCommand=new String[]{"ffmpeg","-y","-i",vidPath,"-c:v","libx264","-ab",bitrate,"-ac",channel,"-strict","-2",savePath};
		else if(isBit && isChan && isFps && isRes)
			complexCommand=new String[]{"ffmpeg","-y","-i",vidPath, "-s",res ,"-r",fps ,"-c:v","libx264","-ab",bitrate,"-ac",channel,"-strict","-2",savePath};
		
		
	}

	private void mp4Toavi() {
		if(!isBit && !isChan && !isFps && !isRes)
			complexCommand=new String[]{"ffmpeg","-y","-i",vidPath, "-vcodec","copy",savePath};
		else if(!isBit && !isChan && !isFps && isRes)
			complexCommand=new String[]{"ffmpeg","-y","-i",vidPath,"-s",res ,"-vcodec","h264","-preset","ultrafast","-strict","-2",savePath};
		else if(!isBit && !isChan && isFps && !isRes)
			complexCommand=new String[]{"ffmpeg","-y","-i",vidPath,"-r",fps ,"-vcodec","h264","-preset","ultrafast","-strict","-2",savePath};
		else if(!isBit && isChan && !isFps && !isRes)
			complexCommand=new String[]{"ffmpeg","-y","-i",vidPath,"-vcodec","copy","-ac",channel,"-strict","-2",savePath};
		else if(isBit && !isChan && !isFps && !isRes)
			complexCommand=new String[]{"ffmpeg","-y","-i",vidPath,"-vcodec","copy","-ab",bitrate,"-strict","-2",savePath};
		else if(!isBit && !isChan && isFps && isRes)
			complexCommand=new String[]{"ffmpeg","-y","-i",vidPath,"-s",res ,"-r",fps ,"-vcodec","h264","-preset","ultrafast","-strict","-2",savePath};
		else if(!isBit && isChan && !isFps && isRes)
			complexCommand=new String[]{"ffmpeg","-y","-i",vidPath,"-s",res ,"-ac",channel,"-vcodec","h264","-preset","ultrafast","-strict","-2",savePath};
		else if(isBit && !isChan && !isFps && isRes)
			complexCommand=new String[]{"ffmpeg","-y","-i",vidPath,"-s",res ,"-ab",bitrate,"-vcodec","h264","-preset","ultrafast","-strict","-2",savePath};
		else if(!isBit && isChan && isFps && !isRes)
			complexCommand=new String[]{"ffmpeg","-y","-i",vidPath,"-r",fps ,"-ac",channel,"-vcodec","h264","-preset","ultrafast","-strict","-2",savePath};
		else if(isBit && !isChan && isFps && !isRes)
			complexCommand=new String[]{"ffmpeg","-y","-i",vidPath,"-r",fps,"-ab",bitrate,"-vcodec","h264","-preset","ultrafast","-strict","-2",savePath};
		else if(isBit && isChan && !isFps && !isRes)
			complexCommand=new String[]{"ffmpeg","-y","-i",vidPath,"-ab",bitrate,"-ac",channel,"-vcodec","copy","-strict","-2",savePath};
		else if(isBit && isChan && isFps && isRes)
			complexCommand=new String[]{"ffmpeg","-y","-i",vidPath,"-s",res ,"-r",fps ,"-ab",bitrate,"-ac",channel,"-vcodec","h264","-preset","ultrafast","-strict","-2",savePath};
		
		
	}
	
	
}
