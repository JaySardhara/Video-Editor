package com.video.pkg;

import java.io.File;

import com.bin.rngeseekbar.Rngeseekbar_Modify;
import com.bin.rngeseekbar.Rngeseekbar_Modify.OnRangeSeekBarChangeListener;
import com.example.videomixer.R;
import com.ringdroid.soundfile.CheapSoundFile;
import com.soundedit.pkg.AudioScrapper;
import com.soundedit.pkg.WaveformView;
import com.welcome.pkg.Base;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.VideoView;

public class Vid_SndEdit extends Activity{

	String vidPth=(new AudioScrapper()).VidPath(), audPth=(new AudioScrapper()).AudPth();
	WaveformView wavefrm;
	VideoView mVideoview;
	Rngeseekbar_Modify<Integer> skbar;
	Button prvw,nxt;
	float mDensity;
	int duration, current_min_pos, current_max_pos, prevw_init, prevw_end, stopPos;
	Boolean initial=true, bVideoIsBeingTouched=false;
	Handler mHandler=new Handler();	
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.vid_sndedit);
		
		prvw=(Button)findViewById(R.id.vsedit_prvw);
		nxt=(Button)findViewById(R.id.button2);		
		mVideoview=(VideoView)findViewById(R.id.vsedit_videoview);
		wavefrm=(WaveformView)findViewById(R.id.vsedit_waveform);
		
		CheapSoundFile.ProgressListener listener =
				new CheapSoundFile.ProgressListener() {
					public boolean reportProgress(double fractionComplete) {
						return true;						
					}
		};
		
		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		mDensity = metrics.density;       	
				
		final CheapSoundFile mSoundfile;
		try {	
			mSoundfile=CheapSoundFile.create(audPth+"/out.m4a", listener);
			//mSoundfile=CheapSoundFile.create("sdcard/audio2.wav", listener);
			Log.d("frames!!!!!",String.valueOf(mSoundfile.getNumFrames()));
			wavefrm.setSoundFile(mSoundfile,418);
			wavefrm.recomputeHeights(mDensity);	
			while(wavefrm.canZoomOut())
				wavefrm.zoomOut();
			
		} catch (Exception e) {
					e.printStackTrace();
		}
		
		//Handle rnge seekbar		
		String uri=Uri.fromFile(new File(vidPth)).toString();Log.d("uri",uri);
		MediaPlayer obj = MediaPlayer.create(getBaseContext(), Uri.parse(uri));		
		duration = obj.getDuration();
		obj.reset();
		obj.release();
        skbar = new Rngeseekbar_Modify<Integer>(0, (duration/1000), getBaseContext());
        skbar.setNotifyWhileDragging(true); // <== most imp :)
        skbar.setOnRangeSeekBarChangeListener(new OnRangeSeekBarChangeListener<Integer>() {       	
        	
            @Override
            public void onRangeSeekBarValuesChanged(Rngeseekbar_Modify<?> bar, Integer minValue, Integer maxValue) {
                    
            		//handle start stage
            		if(initial && minValue!=0){
            			initial=false;
            			current_min_pos=minValue;
            			mVideoview.seekTo(current_min_pos*1000);
            			Log.d("setminInitial",String.valueOf(current_min_pos));
            		}
            		else if(initial && maxValue!=(duration/1000)){
            			initial=false;
            			current_max_pos=maxValue;
            			mVideoview.seekTo(current_max_pos*1000);
            			//videoView.pause();            			
            			Log.d("setmaxInitial",String.valueOf(current_max_pos));
            		}
            			
            		//Handle current stage
            		if(current_max_pos==maxValue){
            			current_min_pos=minValue;
            			mVideoview.seekTo(current_min_pos*1000);
            			//videoView.pause();
            			Log.d("setmin",String.valueOf(current_min_pos));
            		}
            		else if(current_min_pos==minValue ){
            			current_max_pos=maxValue;
            			mVideoview.seekTo(current_max_pos*1000);
            			Log.d("setmax",String.valueOf(current_max_pos));
            		}  
            		
                    Log.d("pos val", String.valueOf(current_min_pos)+" "+String.valueOf(current_max_pos)+" "+String.valueOf(wavefrm.getStart())+" "+String.valueOf(wavefrm.getEnd()));
                    wavefrm.setParameters(Scaler(current_min_pos)*3,Scaler(current_max_pos)*3,0);                    
                    wavefrm.invalidate();
                    
                    //Set last End Pos so to stop at that time
                    prevw_end=maxValue;
                    //Same for initial start time
                    prevw_init=minValue;
            }
        });        
        ViewGroup viewgrp = (ViewGroup)findViewById(R.id.vsedit_rngskbr);
        viewgrp.addView(skbar);
				
        new Thread(new Runnable() {
			
			@Override
			public void run() {
				
				mVideoview.setVideoPath(vidPth);
				mVideoview.requestFocus();				
				mVideoview.start();		
				
			}
		}).start();
		  	
    	
    	mVideoview.setOnTouchListener(new OnTouchListener() {
			
    		 @Override
    		    public boolean onTouch(View v, MotionEvent event) {
    		    if (!bVideoIsBeingTouched) {
    		        bVideoIsBeingTouched = true;
    		    if (mVideoview.isPlaying()) {
    		    	Log.d("pase","pse");
    		    	stopPos=mVideoview.getCurrentPosition();
    		        mVideoview.pause();
    		        //playbtn.setVisibility(View.VISIBLE);
    		    } else {
    		    	Log.d("res","res");
    		    	mVideoview.seekTo(stopPos);
    		    	mVideoview.start();
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
        
    	prvw.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				new Preview().execute();
			}
		});
		
    	nxt.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {			
				
				Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
            	startActivityForResult(i,1);				
			}
		});
    	
	}
	
	protected void  onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 1 && resultCode == RESULT_OK) {              
			Uri uri=data.getData();
			Log.d("uri",uri.toString());
			//URI to absolute path
			
			Cursor cursor = null;
			String sng_pth;
			
			try { 
			    String[] proj = { MediaStore.Audio.Media.DATA };
			    cursor = this.getContentResolver().query(uri, proj, null, null, null);
			    int column_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
			    cursor.moveToFirst();
			    sng_pth = cursor.getString(column_index);
			}finally {
			    if (cursor != null) {
			      cursor.close();
			    }
			}
			
			mVideoview.stopPlayback();
									
			//Set selected duration of audio of this video
			int audSlcted = prevw_end - prevw_init ;
			
			//Temporarily set @Base.isVidAudEdit for differentiating Listener in child class
			Base.isVidAudEdit=true;
			Intent i = new Intent(Vid_SndEdit.this,VidMerge_SngAdd.class);
			i.putExtra("duration", audSlcted);
			i.putExtra("sng_pth",sng_pth);
			i.putExtra("vid_pth",vidPth);
			i.putExtra("vidStart",prevw_init);
			i.putExtra("vidEnd",prevw_end);
			startActivity(i);			
			finish();
        }  
	}
	
	private class Preview extends AsyncTask<Void,Void,Void> {		
		
		@Override
		protected void onPreExecute(){					
			
			mVideoview.seekTo(prevw_init*1000);	
			mVideoview.start();		
			Log.d("ccrr",String.valueOf(mVideoview.getCurrentPosition())+" "+String.valueOf(prevw_end));
		}

		
		protected Void doInBackground(Void... params) {
			
			while(mVideoview.getCurrentPosition() < prevw_end*1000 ){
				Log.d("!@!@","in loop");
			}
			
			return null;
		}
		
		@Override		
        protected void onPostExecute(Void v) {
            mVideoview.pause();
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
