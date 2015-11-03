package com.soundedit.pkg;

import java.io.File;


import com.bin.rngeseekbar.RangeSeekBar;
import com.example.videomixer.R;
import com.ringdroid.soundfile.CheapSoundFile;
import com.soundedit.pkg.WaveformView.WaveformListener;
import com.video.pkg.Welcome;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.VideoView;

/**A very similar class to Editor but handles 
 * after edit events. 
 * 
 * @author Jay Sardhara
 *
 */

public class Aud_Vid_Merger extends Activity implements WaveformListener{
	
	Button reedit,home;
	VideoView mvideoView;
	static MediaPlayer stopper=new MediaPlayer();
	WaveformView mWaveformView;
	String src_aud,src_vid;
	Float mDensity;
	int duration,current_max_pos,current_min_pos,stopPos;
	RangeSeekBar<Integer> seekBar;
	Handler mHandler=new Handler();
	static Boolean initial=true,bVideoIsBeingTouched=false;
	
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.rty);
		Button b = (Button)findViewById(R.id.audExtrctBtn);
		b.setVisibility(View.INVISIBLE);
		
		reedit=(Button)findViewById(R.id.reEdit);
		home=(Button)findViewById(R.id.home);		
		mvideoView=(VideoView)findViewById(R.id.videoView1);		
		mWaveformView=(WaveformView)findViewById(R.id.waveform);
		//mWaveformView.setListener(this);
		
		src_aud=(new AudioScrapper()).AudPth();
		src_vid=(new AudioScrapper()).VidPath();
		
			
		//Set lisntr for sndfile
		final CheapSoundFile.ProgressListener listener =
		new CheapSoundFile.ProgressListener() {
			public boolean reportProgress(double fractionComplete) {
				return true;				
			}
		};
				
		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		mDensity = metrics.density;       	
				
		CheapSoundFile mSoundfile;
		try {	
			mSoundfile=CheapSoundFile.create(src_aud+"/trimed.m4a", listener);
			mWaveformView.setSoundFile(mSoundfile,418);
			mWaveformView.recomputeHeights(mDensity);			
		} catch (Exception e) {
					e.printStackTrace();
		}
		
		
		//Handle rnge seekbar		
		String uri=Uri.fromFile(new File(src_vid)).toString();Log.d("uri",uri);
		MediaPlayer obj1 = MediaPlayer.create(getBaseContext(), Uri.parse(uri));		
		duration = obj1.getDuration();
		obj1.reset();
		obj1.release();
        seekBar = new RangeSeekBar<Integer>(0, (duration/1000), getBaseContext());
        seekBar.setNotifyWhileDragging(true); // <== most imp :)
        seekBar.setOnRangeSeekBarChangeListener(new RangeSeekBar.OnRangeSeekBarChangeListener<Integer>() {       	
        	
            @Override
            public void onRangeSeekBarValuesChanged(RangeSeekBar<?> bar, Integer minValue, Integer maxValue) {
                    
            		//handle start stage
            		if(initial && minValue!=0){
            			initial=false;
            			current_min_pos=minValue;
            			mvideoView.seekTo(current_min_pos*1000);
            			Log.d("setminInitial",String.valueOf(current_min_pos));
            		}
            		else if(initial && maxValue!=(duration/1000)){
            			initial=false;
            			current_max_pos=maxValue;
            			mvideoView.seekTo(current_max_pos*1000);
            			//videoView.pause();            			
            			Log.d("setmaxInitial",String.valueOf(current_max_pos));
            		}
            			
            		//Handle current stage
            		if(current_max_pos==maxValue){
            			current_min_pos=minValue;
            			mvideoView.seekTo(current_min_pos*1000);
            			//videoView.pause();
            			Log.d("setmin",String.valueOf(current_min_pos));
            		}
            		else if(current_min_pos==minValue ){
            			current_max_pos=maxValue;
            			mvideoView.seekTo(current_max_pos*1000);
            			Log.d("setmax",String.valueOf(current_max_pos));
            		}     		
                    Log.d("seekbar1", "User selected new range values: MIN=" + minValue + ", MAX=" + maxValue);
            }
        });        
        ViewGroup viewgrp = (ViewGroup)findViewById(R.id.rngskbr);
        viewgrp.addView(seekBar);
		Log.d("crit",src_vid);
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				Log.d("fnal path",src_aud+"/Final_AudTrimmed.mp4");
				mvideoView.setVideoPath(src_aud+"/Final_AudTrimmed.mp4");
				mvideoView.requestFocus();				
				mvideoView.start();
				
				
				mvideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener()  {
		            @Override
		            public void onPrepared(MediaPlayer mp) {     
		            	stopper=mp;
		            	/*current_max_pos=videoView.getDuration();
		            	
		            	while(videoView.getCurrentPosition() < current_max_pos){
							if(videoView.getCurrentPosition()==current_max_pos){
								videoView.pause();
								Log.d("chk",String.valueOf(videoView.getCurrentPosition()));
							
							}
		            	}*/
		            }
		        });
				
				//Log.d("time chk",String.valueOf(videoView.getCurrentPosition())+String.valueOf(current_max_pos));
				
			}
		}).start();
		  	
    	
    	mvideoView.setOnTouchListener(new OnTouchListener() {
			
    		 @Override
    		    public boolean onTouch(View v, MotionEvent event) {
    		    if (!bVideoIsBeingTouched) {
    		        bVideoIsBeingTouched = true;
    		    if (mvideoView.isPlaying()) {
    		    	Log.d("pase","pse");
    		    	stopPos=mvideoView.getCurrentPosition();
    		        mvideoView.pause();
    		        //playbtn.setVisibility(View.VISIBLE);
    		    } else {
    		    	Log.d("res","res");
    		    	mvideoView.seekTo(stopPos);
    		    	mvideoView.start();
    		    	//playbtn.setVisibility(View.INVISIBLE);
    		        //videoView.resume(); <== resets everytime so replaced by above code
    		    }
    		    mHandler.postDelayed(new Runnable() {
    		        public void run() {Log.d("flg","set false");
    		            bVideoIsBeingTouched = false;
    		            Log.d("touchflg","set");
    		        }
    		        }, 200);
    		    }
				return true;
			}
		});
		
    	//Button Listener
    	reedit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				mvideoView.stopPlayback();
				//stopper.reset();
				//stopper.release();
				Intent i= new Intent(getBaseContext(),Editor.class);
				startActivity(i);				
			}
		});
    	
    	home.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {				
				Intent i = new Intent(getBaseContext(),Welcome.class);
				i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(i);				
			}
	
    	});
    	
    	
	}
	
	
	@Override
	public void waveformTouchStart(float x) {		
	}

	@Override
	public void waveformTouchMove(float x) {		
	}

	@Override
	public void waveformTouchEnd() {				
	}

	@Override
	public void waveformFling(float x) {				
	}

	@Override
	public void waveformDraw() {		
		int strt=mWaveformView.getStart();
		int end=mWaveformView.getEnd();
		int offset=mWaveformView.getOffset();
				
		mWaveformView.setParameters(strt, end, offset);
		Log.d("h",String.valueOf(mWaveformView.getZoomLevel()));
		//mWaveformView.invalidate();
		mWaveformView.zoomOut();		
	}
	
	public int getCurrentMinPos(){
		return current_min_pos;
	}
	
	public int getCurrentMaxPos(){
		return current_max_pos;
	}
		
}


