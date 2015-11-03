package com.soundedit.pkg;

import java.io.File;

import com.welcome.pkg.Base;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;


/**This class is used to strip audio from video 
 * as we need only audio for generating Scope 
 * 
 * @author Jay Sardhara
 *
 */
public class AudioScrapper extends BaseWizard_Modified{
	
	static String src_vid,dest;	 
	
	public void onCreate(Bundle bundle){
		super.onCreate(bundle);
		
		if(Base.isVidEdit){
			Base.isVidEdit=false;
			Base.startEditor=true;
		}
		else if(Base.isVidAudEdit){
			Base.isVidAudEdit=false;
			Base.startVSEditor=true;
		}
				
		src_vid=getIntent().getExtras().getString("pth");    
        dest="/sdcard/audio_scrapper"; 
        File mkdest=new File(dest);
        mkdest.mkdirs();
        
        //Clean up for FFmpeg4Android
        File cleaner=new File("/sdcard/videokit");
        if (cleaner.isDirectory()) {
            String[] children = cleaner.list();
            for (int i = 0; i < children.length; i++) {
                new File(cleaner, children[i]).delete();
            }
        }	
        
        copyLicenseAndDemoFilesFromAssetsToSDIfNeeded();
	
        //Scrap Audio from video		
		
      	//String cmd="ffmpeg -y -i "+src_vid+" -b:a 10k -vn "+dest+"/onlysnd.mp3";
      	Thread ffmpeg=new Thread(new Runnable() {
      			
      	@Override
      	public void run() {
      		String temp_dest=dest+"/out.m4a";
      		String[] cmd={"ffmpeg","-y","-i",src_vid,"-vn","-acodec","copy",temp_dest};
      		Log.d("scrapPath",cmd.toString());
      		setCommandComplex(cmd);
      		//setOutputFilePath(dest+"/out.m4a");
      		runTranscoing();
      		}
      	});
      	ffmpeg.setPriority(10);
      	ffmpeg.run();     	
        
	}

	public String AudPth() {
		return dest;
	}
	public String VidPath() {Log.d("adsasd",src_vid);
		return src_vid;
	}
	
}
