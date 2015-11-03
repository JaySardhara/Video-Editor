package com.soundedit.pkg;


import java.io.File;

import com.bin.rngeseekbar.Rngeseekbar_Modify;
import com.bin.rngeseekbar.Rngeseekbar_Modify.OnRangeSeekBarChangeListener;
import com.example.videomixer.R;

import com.ringdroid.soundfile.CheapSoundFile;
import com.soundedit.pkg.WaveformView.WaveformListener;
import com.welcome.pkg.Base;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.MediaController;
import android.widget.VideoView;

/**This is the main class containing view and 
 * responsible for audio editing based on the 
 * seekbar values.
 * 
 * @author Jay Sardhara
 *
 */


public class Editor extends BaseWizard_Modified implements WaveformListener{
	
	WaveformView mWaveformView;
	Float mDensity;
	VideoView videoView;
	MediaController obj;	
	Rngeseekbar_Modify<Integer> seekBar;
	static Boolean initial=true,bVideoIsBeingTouched=false;
	int duration,stopPos,prevw_end,prevw_init;
	static int current_max_pos,current_min_pos;
	static AudioScrapper pth=new AudioScrapper();
	String src_aud=pth.AudPth(),src_vid=pth.VidPath();
	Handler mHandler=new Handler();
	Button edit,previewer;
	//ImageView playbtn;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.rty);		
		
		Button b=(Button)findViewById(R.id.home);
		b.setVisibility(View.INVISIBLE);
		b=(Button)findViewById(R.id.reEdit);
		b.setVisibility(View.INVISIBLE);
				
		videoView=(VideoView)findViewById(R.id.videoView1);
		//playbtn=(ImageView)findViewById(R.id.playbtn);
		//playbtn.setVisibility(View.INVISIBLE);
		edit=(Button)findViewById(R.id.audExtrctBtn);
		previewer=(Button)findViewById(R.id.previewer);		
		mWaveformView=(WaveformView)findViewById(R.id.waveform);
		
		//mWaveformView.setListener(this);
		
		//src_aud=pth.AudPth();
		//src_vid=pth.VidPath();
		//pth.finish();
		//Rename *.m4a to *.mp3
		//new File(src_aud+"/out.m4a").renameTo(new File(src_aud+"/out.mp3"));
			
		//Set lisntr for sndfile
		CheapSoundFile.ProgressListener listener =
		new CheapSoundFile.ProgressListener() {
			public boolean reportProgress(double fractionComplete) {
				return true;
				/*long now = System.currentTimeMillis();
			    if (now - mLoadingLastUpdateTime > 100) {
			          mProgressDialog.setProgress(
			          (int)(mProgressDialog.getMax() *
			          fractionComplete));
			          mLoadingLastUpdateTime = now;
			    }
			    return mLoadingKeepGoing;*/
			}
		};
				
		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		mDensity = metrics.density;       	
				
		final CheapSoundFile mSoundfile;
		try {	
			mSoundfile=CheapSoundFile.create(src_aud+"/out.m4a", listener);
			//mSoundfile=CheapSoundFile.create("sdcard/audio2.wav", listener);
			Log.d("frames!!!!!",String.valueOf(mSoundfile.getNumFrames()));
			mWaveformView.setSoundFile(mSoundfile,418);
			mWaveformView.recomputeHeights(mDensity);	
			while(mWaveformView.canZoomOut())
				mWaveformView.zoomOut();
			
		} catch (Exception e) {
					e.printStackTrace();
		}
					
		//Handle rnge seekbar		
		String uri=Uri.fromFile(new File(src_vid)).toString();Log.d("uri",uri);
		MediaPlayer obj1 = MediaPlayer.create(getBaseContext(), Uri.parse(uri));		
		duration = obj1.getDuration();
		obj1.reset();
		obj1.release();
        seekBar = new Rngeseekbar_Modify<Integer>(0, (duration/1000), getBaseContext());
        seekBar.setNotifyWhileDragging(true); // <== most imp :)
        seekBar.setOnRangeSeekBarChangeListener(new OnRangeSeekBarChangeListener<Integer>() {       	
        	
            @Override
            public void onRangeSeekBarValuesChanged(Rngeseekbar_Modify<?> bar, Integer minValue, Integer maxValue) {
                    
            		//handle start stage
            		if(initial && minValue!=0){
            			initial=false;
            			current_min_pos=minValue;
            			videoView.seekTo(current_min_pos*1000);
            			Log.d("setminInitial",String.valueOf(current_min_pos));
            		}
            		else if(initial && maxValue!=(duration/1000)){
            			initial=false;
            			current_max_pos=maxValue;
            			videoView.seekTo(current_max_pos*1000);
            			//videoView.pause();            			
            			Log.d("setmaxInitial",String.valueOf(current_max_pos));
            		}
            			
            		//Handle current stage
            		if(current_max_pos==maxValue){
            			current_min_pos=minValue;
            			videoView.seekTo(current_min_pos*1000);
            			//videoView.pause();
            			Log.d("setmin",String.valueOf(current_min_pos));
            		}
            		else if(current_min_pos==minValue ){
            			current_max_pos=maxValue;
            			videoView.seekTo(current_max_pos*1000);
            			Log.d("setmax",String.valueOf(current_max_pos));
            		}  
            		
                    Log.d("pos val", String.valueOf(current_min_pos)+" "+String.valueOf(current_max_pos)+" "+String.valueOf(mWaveformView.getStart())+" "+String.valueOf(mWaveformView.getEnd()));
                    mWaveformView.setParameters(Scaler(current_min_pos)*3,Scaler(current_max_pos)*3,0);//mWaveformView.zoomOut();
                    //mWaveformView.setParameters(Scaler(minValue)*3,Scaler(maxValue)*3,0);	//mWaveformView.zoomOut();
                    mWaveformView.invalidate();
                    
                    //Set last End Pos so to stop at that time
                    prevw_end=maxValue;
                    //Same for initial start time
                    prevw_init=minValue;
            }
        });        
        ViewGroup viewgrp = (ViewGroup)findViewById(R.id.rngskbr);
        viewgrp.addView(seekBar);
		Log.d("crit",src_vid);
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				
				videoView.setVideoPath(src_vid);
				videoView.requestFocus();				
				videoView.start();
				
				//Handle initial value of end pos.
				videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener()  {
		            @Override
		            public void onPrepared(MediaPlayer mp) {                         
		            	waveformDraw();
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
		  	
    	
    	videoView.setOnTouchListener(new OnTouchListener() {
			
    		 @Override
    		    public boolean onTouch(View v, MotionEvent event) {
    		    if (!bVideoIsBeingTouched) {
    		        bVideoIsBeingTouched = true;
    		    if (videoView.isPlaying()) {
    		    	Log.d("pase","pse");
    		    	stopPos=videoView.getCurrentPosition();
    		        videoView.pause();
    		        //playbtn.setVisibility(View.VISIBLE);
    		    } else {
    		    	Log.d("res","res");
    		    	videoView.seekTo(stopPos);
    		    	videoView.start();
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
		
    	//Button Listeners
    	edit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				videoView.pause();
				//Welcome.startCombiner=true;
				//Intent i = new Intent(getBaseContext(),After_edit_snd.class);
				//startActivity(i);
				AlertDialog.Builder save = new AlertDialog.Builder(Editor.this);
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
						String formatted_start_pos = String.format("%02d:%02d:%02d", current_min_pos/(60*60), (current_min_pos/60)%60, current_min_pos%60);
						String formatted_Diff_endpos = String.format("%02d:%02d:%02d", (current_max_pos-current_min_pos)/(60*60), (current_max_pos-current_min_pos)/60%60, (current_max_pos-current_min_pos)%60);
														
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
    	
    	previewer.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				new Preview().execute();
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

	public void waveformDraw() {		
		
		//Initialize all params
		mWaveformView.setParameters(0,(videoView.getDuration()/1000)*2,0);
		//mWaveformView.setZoomLevel(6);Log.d("inwave",String.valueOf(videoView.getDuration()));		
		//mWaveformView.invalidate();			
		
		
	}
	
	public int getCurrentMinPos(){
		return current_min_pos;
	}
	
	public int getCurrentMaxPos(){
		return current_max_pos;
	}
			
	
	private class Preview extends AsyncTask<Void,Void,Void> {		
		
		@Override
		protected void onPreExecute(){					
			
			videoView.seekTo(prevw_init*1000);	
			videoView.start();		
			Log.d("ccrr",String.valueOf(videoView.getCurrentPosition())+" "+String.valueOf(prevw_end));
		}

		
		protected Void doInBackground(Void... params) {
			
			while(videoView.getCurrentPosition() < prevw_end*1000 ){
				Log.d("!@!@","in loop");
			}
			
			return null;
		}
		
		@Override		
        protected void onPostExecute(Void v) {
            videoView.pause();
        }		
		
	}
	
	private int Scaler(int val_to_scale){
		
		//Method to scale down values in range 0-140
		/**
		 * Formula used (b-a)(x-min) + a
		 * 				------------	
		 * 				  max - min
		 *
		 * where [min,max] is range to scale and [a,b] is [0,140]
		 * and x is current value to scale.
		 * 
		 * */
		
		int min=0,max=duration/1000,a=0,b=140,x=val_to_scale;
		
		int scaled_val= (((b-a)*(x-min))/(max-min))+a;
		Log.d("Scaler",String.valueOf(scaled_val));
		return scaled_val;
		
	}
}

