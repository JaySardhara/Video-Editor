package com.video.pkg;

import java.io.File;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import com.example.videomixer.R;
import com.soundedit.pkg.BaseWizard_Modified;
import com.welcome.pkg.Base;

public class VidMerge_Engine extends BaseWizard_Modified{

	String res="", pth1, pth2;
	int vidPos[];
	String frmmtedPos[], complexCommand[], sngStart, sngEnd, sng;
	Boolean isDirect;
	
	private SharedPreferences mPrefs;
	
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
        
        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        
        //Initialize only if Activity is fired for 1st time, else reload all data
        if(!Base.mAddAud){
        	res=getIntent().getExtras().getString("res");
            
            vidPos=new int[4];
            vidPos=getIntent().getExtras().getIntArray("vidpos");		
            
            frmmtedPos=new String[4];
            for(int i=0; i<4; i++)
            	frmmtedPos[i]=String.format("%02d:%02d:%02d", vidPos[i]/(60*60), (vidPos[i]/60)%60, vidPos[i]%60);
            
            pth1=getIntent().getExtras().getString("pth1");
            pth2=getIntent().getExtras().getString("pth2");
            
            isDirect=getIntent().getExtras().getBoolean("isDirect");
            
            if(!isDirect){
            	sngStart=String.format("%02d:%02d:%02d", getIntent().getExtras().getInt("sngStart")/(60*60), (getIntent().getExtras().getInt("sngStart")/60)%60, getIntent().getExtras().getInt("sngStart")%60);
            	sngEnd=String.format("%02d:%02d:%02d", getIntent().getExtras().getInt("sngEnd")/(60*60), (getIntent().getExtras().getInt("sngEnd")/60)%60, getIntent().getExtras().getInt("sngEnd")%60);
            
            	sng=getIntent().getExtras().getString("sng");       
            }
            
        }else{
        	
        	isDirect = mPrefs.getBoolean("Bundled_isDirect", false);
        	res = mPrefs.getString("Bundled_res", "");
        	pth1 = mPrefs.getString("Bundled_pth1","");
        	pth2 = mPrefs.getString("Bundled_pth2","");
        	sng = mPrefs.getString("Bundled_sng","");
       	  	sngStart = mPrefs.getString("Bundled_sngStart","");
       	  	sngEnd = mPrefs.getString("Bundled_sngEnd","");
        	
        }
        
        
        if(isDirect){
        	String[] complexCommand={"ffmpeg","-y","-ss",frmmtedPos[0],"-t",frmmtedPos[1],"-i",pth1,"-ss",frmmtedPos[2],"-t",frmmtedPos[3],"-i",pth2,"-filter_complex","[0:v]scale="+res+" [v1];[v1] setsar=16:9 [v11]; [1:v]scale="+res+"[v2];[v2] setsar=16:9 [v22]; [v11][0:a][v22][1:a] concat=n=2:v=1:a=1 [v] [a]","-map","[v]","-map","[a]","-s",res,"-preset","ultrafast","-strict","-2","/mnt/sdcard/mrgeVid.mp4"};
        	setCommandComplex(complexCommand);
        	setNotificationIcon(R.drawable.out2);
            //setProgressDialogTitle("");
            //setProgressDialogMessage("");
            setNotificationMessage("Video Merge is running...");
    		setNotificationTitle("Video Merge");
    		setNotificationfinishedMessageTitle("Video Merge finished");
    		setNotificationfinishedMessageDesc("Click to play");
    		setNotificationStoppedMessage("Video Merge stopped");
        }
        else{
        	if(!Base.mAddAud){
        		Base.mAddAud=true;
        		String[] complexCommand={"ffmpeg","-y","-ss",frmmtedPos[0],"-t",frmmtedPos[1],"-i",pth1,"-ss",frmmtedPos[2],"-t",frmmtedPos[3],"-i",pth2,"-filter_complex","[0:v]scale="+res+" [v1];[v1] setsar=16:9 [v11]; [1:v]scale="+res+"[v2];[v2] setsar=16:9 [v22]; [v11][v22] concat=n=2:v=1:a=0 [v] ","-map","[v]","-an","-s",res,"-preset","ultrafast","-strict","-2","/mnt/sdcard/muteVid.mp4"};
        		setCommandComplex(complexCommand);
        		setProgressDialogTitle("Step (1/2)");
                setProgressDialogMessage("Merging your Videos....");                
        	}
        	else{
        		Base.mAddAud=false;
        		String[] complexCommand={"ffmpeg","-y","-i","/mnt/sdcard/muteVid.mp4","-ss",sngStart,"-t",sngEnd,"-i",sng,"-map","0:0","-map","1:0","-c:v","copy","-preset","ultrafast","-strict","-2","/mnt/sdcard/mrgeVid.mp4"};
        		setCommandComplex(complexCommand);
        		setProgressDialogTitle("Step (2/2)");
                setProgressDialogMessage("Adding Audio to Video...");
                setNotificationIcon(R.drawable.out2);
                //setProgressDialogTitle("");
                //setProgressDialogMessage("");
                setNotificationMessage("Video Merge is running...");
        		setNotificationTitle("Video Merge");
        		setNotificationfinishedMessageTitle("Video Merge finished");
        		setNotificationfinishedMessageDesc("Click to play");
        		setNotificationStoppedMessage("Video Merge stopped");
        	}
        }
        //Log.w("cmmnd",complexCommand.toString());
        
        
		runTranscoing();         
        
	}
	
	public void onBackPressed(){
		Intent i = new Intent(getBaseContext(),Base.class);
		i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(i);
		finish();
	}
	
	protected void onPause() {
        super.onPause();

        SharedPreferences.Editor ed = mPrefs.edit();
        ed.putBoolean("Bundled_isDirect", isDirect);
        //ed.putIntArray("Bundled_vipos",vidPos);
        //ed.putStringArray("Bundled_frmmtedPos",frmmtedPos);
        ed.putString("Bundled_res", res);
        ed.putString("Bundled_pth1", pth1);
        ed.putString("Bundled_pth2", pth2);
        ed.putString("Bundled_sng", sng);
        ed.putString("Bundled_sngStart", sngStart);
        ed.putString("Bundled_sngEnd", sngEnd);	  
        ed.commit();
    }
	
	
}
