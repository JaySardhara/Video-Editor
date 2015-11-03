package com.video.pkg;

import java.io.File;
import java.util.ArrayList;
import java.util.StringTokenizer;

import com.bin.rngeseekbar.Rngeseekbar_Modify;
import com.example.videomixer.R;

import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;


/**
 * Class for video editing
 * 
 * @author Jay Sardhara
 *
 */



@SuppressLint({ "SdCardPath", "NewApi" })
public class VMixer extends Activity{

	LinearLayout vidfrm1;
	//VideoView videoView1;	
	MediaController obj1;	
	Rngeseekbar_Modify<Integer> seekBar1;
	Button preview,nxt,fnlze;
	int current_min_pos1,current_max_pos1,h,w,stopPos;
	int duration1,duration2,prevw_init,prevw_end,total_thmbs1,total_thmbs2;	
	static String vid_pth1,vid_pth2,dest="/sdcard/vmxr_thmbs";
	Boolean flip,initial=true,init_skbar=true, nxtPressed=false, fnlzePressed=false;
	static Boolean bVideoIsBeingTouched=false;
	CustomVideoView setVideoview;
	custom_borders_vidFrame brders;
	Handler mHandler=new Handler();	
	ArrayList<Bitmap> thmb_store = new ArrayList<Bitmap>();
	ArrayList<Bitmap> arr_bmp;
	static ArrayList<Bitmap> temp;
	int[] vid_positions=new int[4];
	//ProgressBar waiter;
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_vmixer_lyout);
        
        Intent in=getIntent();
        String mltple_pth=in.getExtras().getString("vid_pths");        
        StringTokenizer tokens = new StringTokenizer(mltple_pth, "|");
        vid_pth1 = tokens.nextToken();
        vid_pth2 = tokens.nextToken();        
        Log.d("pthrecvd",mltple_pth+" "+vid_pth1+" "+vid_pth2);
        
        //For FFMPEG4ANDROID
        //copyLicenseAndDemoFilesFromAssetsToSDIfNeeded(); 
        //String ExternalStorageDirectoryPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        //src_vid1="mnt/sdcard/psy.mp4";
        
         
        //File mkdest=new File(dest);
        //mkdest.mkdirs();
        
        //Clean up for FFmpeg4Android
        File cleaner=new File("/sdcard/videokit");
        if (cleaner.isDirectory()) {
            String[] children = cleaner.list();
            for (int i = 0; i < children.length; i++) {
                new File(cleaner, children[i]).delete();
            }
        }
              
        
        setVideoview=new CustomVideoView(this);      
        setVideoview=(CustomVideoView)findViewById(R.id.videoView1);
        vidfrm1=(LinearLayout)findViewById(R.id.childThmbnail);
        brders=(custom_borders_vidFrame)findViewById(R.id.thmb_brders);
        preview=(Button)findViewById(R.id.preview_frVid);
        nxt=(Button)findViewById(R.id.nxt_vid);
        fnlze=(Button)findViewById(R.id.finlize);
        fnlze.setVisibility(View.INVISIBLE);
        
        //edit_vid=(Button)findViewById(R.id.button1);
        
        //obj1 =new MediaController(videoView1.getContext());
        //obj1.requestFocus();
        //obj1.setAnchorView(videoView1);
        //obj2 =new MediaController(videoView2.getContext());
        //obj2.requestFocus();
        //obj2.setAnchorView(videoView2);*/
        /*videoView.setOnPreparedListener(new OnPreparedListener() {
			
			@Override
			public void onPrepared(MediaPlayer arg0) {
				obj.show(0);
				
			}
		});*/       
        
        
        /*videoView1.setVideoPath(src_vid1);
        videoView1.setMediaController(obj1);
        //videoView.requestFocus();
        videoView1.setOnTouchListener(this);
        videoView2.setVideoPath(src_vid2);
        videoView2.setMediaController(obj2);
        //videoView.requestFocus();
        videoView2.setOnTouchListener(this);*/
        /*String ExternalStorageDirectoryPath = Environment
        		.getExternalStorageDirectory()
        		.getAbsolutePath();
        
        String targetPath = ExternalStorageDirectoryPath + "/scroll_test/";
        
        Toast.makeText(getApplicationContext(), targetPath, Toast.LENGTH_LONG).show();*/
        
        
        //waiter = (ProgressBar)findViewById(R.id.progressBar1);
        //waiter=new ProgressBar(getBaseContext());
        //waiter.setVisibility(View.VISIBLE);
        
        getThmbNails(getBaseContext(),vid_pth1);          
        startVideo(vid_pth1); 
        //waiter.setVisibility(View.INVISIBLE);
        
        setVideoview.setDimensions(480,270);
    	setVideoview.getHolder().setFixedSize(480,270);
    	//Log.d("width",String.valueOf(w));
        
        /*File targetDirector = new File(dest);
        	        
        File[] files = targetDirector.listFiles();
        
        for (File file : files){
        	
        		vidfrm1.addView(insertPhoto(file.getAbsolutePath()));
        		
        	
        }  */
                     
    	//Now set Max Min values for seekbar for Vid.1     	
    	Log.w("duratn1",String.valueOf(duration1/1000));
    	seekBar1 = new Rngeseekbar_Modify<Integer>(0, duration1/1000, this);
        initSeekbar();      
        
        setVideoview.setOnTouchListener(new OnTouchListener() {
			
      		 @Override
      		    public boolean onTouch(View v, MotionEvent event) {
      		    if (!bVideoIsBeingTouched) {
      		        bVideoIsBeingTouched = true;
      		    if (setVideoview.isPlaying()) {
      		    	Log.d("pase","pse");
      		    	stopPos=setVideoview.getCurrentPosition();
      		        setVideoview.pause();
      		        //playbtn.setVisibility(View.VISIBLE);
      		    } else {
      		    	Log.d("res","res");
      		    	setVideoview.seekTo(stopPos);
      		    	setVideoview.start();
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
        //Listener for video editing
        /*edit_vid.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String formatted_start_pos = String.format("%02d:%02d:%02d", current_min_pos/(60*60), (current_min_pos/60)%60, current_min_pos%60);
				String formatted_Diff_endpos = String.format("%02d:%02d:%02d", (current_max_pos-current_min_pos)/(60*60), (current_max_pos-current_min_pos)/60%60, (current_max_pos-current_min_pos)%60);
				try{
					String grabber = "ffmpeg -y -ss "+formatted_start_pos+" -i "+src+" -t "+formatted_Diff_endpos+" -vcodec copy -acodec copy /sdcard/outputFile.mp4";
					Log.d("cmnd",grabber);
					setCommand(grabber);
					//setOutputFilePath("/sdcard/outputFile.mp4");
					runTranscoing();
					//releaseService();
					}
					catch(Exception e){
						e.printStackTrace();
					}
				Log.d("frmted",formatted_start_pos);Log.d("frm",formatted_Diff_endpos);
			}
		});*/
        
        
        
        preview.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				new Preview().execute();
			}
		});
        
        //Reset everything for next video
        nxt.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				nxtPressed=true;
				
				nxt.setVisibility(View.INVISIBLE);
				
				//Store thumbs of selected range for later use
				thmbStorer(1);
				
				//Release previous video obj.
				setVideoview.stopPlayback();
				
				//Reset thmbnail viewer
				vidfrm1.removeAllViews();
				
				//waiter.setVisibility(View.VISIBLE);
				getThmbNails(getBaseContext(),vid_pth2);
				
				//Store the current min max position of video for future reference
				vid_positions[0]=current_min_pos1;
				vid_positions[1]=current_max_pos1;
				
				//Reset seekbar and borders for Vid.2
				initial=true;
				current_min_pos1=0;
				current_max_pos1=duration1/1000;				
				seekBar1 = new Rngeseekbar_Modify<Integer>(0, duration1/1000, getBaseContext());
				initSeekbar();
				brders.setParameters(0,(Scaler(duration1/1000)*4.85f),0);
                brders.invalidate();
				
				//waiter.setVisibility(View.INVISIBLE);
				startVideo(vid_pth2);            
				
				fnlze.setVisibility(View.VISIBLE);
				
			}
		});
        
        fnlze.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
			
				fnlzePressed=true;
				
				//Store the current min max position of video for future reference
				vid_positions[2]=current_min_pos1;
				vid_positions[3]=current_max_pos1;
				
				//Release videoview 
				setVideoview.stopPlayback();
				
				//Store thumbs of selected range for later use
				thmbStorer(2);				
				//tester();
				Intent i = new Intent(VMixer.this,vid_arranger.class);
				//Bundle bndle = new Bundle();
				//bndle.putParcelableArrayList("thmbstore", thmb_store);
				Log.d("sizebfre",String.valueOf(thmb_store.size()));
				i.putExtra("tt1", total_thmbs1);
				i.putExtra("tt2", total_thmbs2);
				i.putExtra("vidpos", vid_positions);
				i.putExtra("vidpath1", vid_pth1);
				i.putExtra("vidpath2", vid_pth2);
				setArrLst(thmb_store);
				startActivity(i);
				
			}
		});
        
        Log.d("crit","end");
         
    }
    
    
    
	private void initSeekbar() {
		
		//Handle rnge seekbar        
        		
        seekBar1.setNotifyWhileDragging(true); // <== most imp :)
        seekBar1.setOnRangeSeekBarChangeListener(new Rngeseekbar_Modify.OnRangeSeekBarChangeListener<Integer>() {       	
        	
            @Override
            public void onRangeSeekBarValuesChanged(Rngeseekbar_Modify<?> bar, Integer minValue, Integer maxValue) {
                    
            		//handle start stage
            		if(initial && minValue!=0){
            			initial=false;
            			current_min_pos1=minValue;
            			setVideoview.seekTo(current_min_pos1*1000);
            			Log.d("setminInitial",String.valueOf(current_min_pos1));
            		}
            		else if(initial && maxValue!=(duration1/1000)){
            			initial=false;
            			current_max_pos1=maxValue;
            			setVideoview.seekTo(current_max_pos1*1000);
            			Log.d("setmaxInitial",String.valueOf(current_max_pos1));
            		}
            			
            		//Handle current stage
            		if(current_max_pos1==maxValue){
            			current_min_pos1=minValue;
            			setVideoview.seekTo(current_min_pos1*1000);
            			Log.d("setmin",String.valueOf(current_min_pos1));
            		}
            		else if(current_min_pos1==minValue ){
            			current_max_pos1=maxValue;
            			setVideoview.seekTo(current_max_pos1*1000);
            			Log.d("setmax",String.valueOf(current_max_pos1));
            		}     		
                    Log.d("seekbar1", "User selected new range values: MIN=" + minValue + ", MAX=" + maxValue);
                    
                    //Moveborders on thmbnails
                    brders.setParameters((Scaler(current_min_pos1)*4.85f),(Scaler(current_max_pos1)*4.85f),0);
                    brders.invalidate();
                    
                    //Set last End Pos so to stop at that time
                    prevw_end=maxValue;
                    //Same for initial start time
                    prevw_init=minValue;
                    
            }
        });        
        
        ViewGroup viewgrp = (ViewGroup)findViewById(R.id.rngeskbar);
        if(init_skbar){
        	//ViewGroup viewgrp = (ViewGroup)findViewById(R.id.rngeskbar);
        	viewgrp.addView(seekBar1);
        	init_skbar=false;
        }
        else{
        	viewgrp.removeAllViews();
        	viewgrp.addView(seekBar1);
        }
		
	}



	private void startVideo(final String curr_path) {		
		
		setVideoview.setVideoPath(curr_path);
		setVideoview.start();	
		
	}



	private void getThmbNails(Context obj,String curr_vid_pth) {
		Log.w("crit",curr_vid_pth);
    	MediaPlayer obj1 = MediaPlayer.create(obj, Uri.parse(curr_vid_pth));
		duration1 = obj1.getDuration();		Log.d("duration",String.valueOf(duration1));
		int fetch_every = duration1*1000/12;
    	h=obj1.getVideoHeight();
    	w=obj1.getVideoWidth();
    	
    	MediaMetadataRetriever temp = new MediaMetadataRetriever();
        temp.setDataSource(curr_vid_pth);
        arr_bmp = new ArrayList<Bitmap>();
        arr_bmp.clear();
               
        for (long i = 0 ; i < (duration1*1000) ; i=i+(fetch_every)) {
        	arr_bmp.add(temp.getFrameAtTime(i, MediaMetadataRetriever.OPTION_CLOSEST_SYNC));
        	Log.d("i",String.valueOf(i));
        }
                
        int i=0;
        while(i!=11){
        	
        	Bitmap bmp = Bitmap.createBitmap(arr_bmp.get(i));     
        	
        	vidfrm1.addView(insertPhoto(bmp));
        	
            /*try {           	
    			bmp.compress(CompressFormat.PNG, 95, new FileOutputStream(new File(dest+"/thmb"+i+".png")));
    		} catch (FileNotFoundException e) {    			
    			e.printStackTrace();
    		}*/
        	i++;
        }        
       
        temp.release();
    	obj1.reset();
    	obj1.release();   	
		
	}


	View insertPhoto(Bitmap bitmap){
    	//Bitmap bm = decodeSampledBitmapFromUri(bitmap, 66, 65);
    	Bitmap bm = Bitmap.createScaledBitmap(bitmap, 66, 65, true); 	
    	LinearLayout layout = new LinearLayout(getApplicationContext());
    	layout.setLayoutParams(new LayoutParams(66, 65));
    	layout.setGravity(Gravity.CENTER);
    	
    	ImageView imageView = new ImageView(getApplicationContext());
    	imageView.setLayoutParams(new LayoutParams(66, 65));
    	imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
    	imageView.setImageBitmap(bm);
    	
    	layout.addView(imageView);
    	return layout;
    }
    
	
	/* -----Barren method-------
    public Bitmap decodeSampledBitmapFromUri(Bitmap bmp, int reqWidth, int reqHeight) {
    	//Bitmap bm = null;    	
    	/*
    	// First decode with inJustDecodeBounds=true to check dimensions
    	final BitmapFactory.Options options = new BitmapFactory.Options();
    	options.inJustDecodeBounds = true;
    	BitmapFactory.decodeFile(path, options);    	
    	// Calculate inSampleSize
    	options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);    	
    	// Decode bitmap with inSampleSize set
    	options.inJustDecodeBounds = false;
    	bm = BitmapFactory.decodeFile(path, options);   	
    	return Bitmap.createScaledBitmap(bmp, reqWidth, reqHeight, true); 	
    }*/
    
    public int calculateInSampleSize(
    		
    	BitmapFactory.Options options, int reqWidth, int reqHeight) {
    	// Raw height and width of image
    	final int height = options.outHeight;
    	final int width = options.outWidth;
    	int inSampleSize = 1;
        
    	if (height > reqHeight || width > reqWidth) {
    		if (width > height) {
    			inSampleSize = Math.round((float)height / (float)reqHeight);  	
    		} else {
    			inSampleSize = Math.round((float)width / (float)reqWidth);  	
    		}  	
    	}
    	
    	return inSampleSize;  	
    }


	/*@Override
	public boolean onTouch(View v, MotionEvent event) {
		
		/*if(flip){
			videoView.pause();
			flip=false;
		}
		else{
			videoView.start();
			flip=true;			
		}
		
		return false;
	}*/
	
	private float Scaler(int val_to_scale){
		
		//Method to scale down values in range 0-150
		/**
		 * Formula used (b-a)(x-min) + a
		 * 				------------	
		 * 				 max - min
		 *
		 * where [min,max] is range to scale and [a,b] is [0,150]
		 * and x is current value to scale.
		 * 
		 * */
		
		float min=0,max=duration1/1000,a=0,b=150,x=val_to_scale;
		
		float scaled_val= (((b-a)*(x-min))/(max-min))+a;
		Log.d("Scaler",String.valueOf(scaled_val));
		return scaled_val;
		
	}
	
	private class Preview extends AsyncTask<Void,Void,Void> {		
		
		@Override
		protected void onPreExecute(){					
			
			setVideoview.seekTo(prevw_init*1000);	
			setVideoview.start();		
			Log.d("ccrr",String.valueOf(setVideoview.getCurrentPosition())+" "+String.valueOf(prevw_end));
		}

		
		protected Void doInBackground(Void... params) {
			
			while(setVideoview.getCurrentPosition() < prevw_end*1000 ){
				
				if(nxtPressed || fnlzePressed)
					break;
					
				Log.d("!@!@","in loop");
			}
			
			return null;
		}
		
		@Override		
        protected void onPostExecute(Void v) {
            setVideoview.pause();
        }		
		
	}
	
	//Fetch and store thumbs of selected range for future reference
	private void thmbStorer(int vid_no){
		
		int fetch_from,fetch_till;
		fetch_from=(int) Scaler(current_min_pos1);
		fetch_till=(int) Scaler(current_max_pos1);
		
		//Will check for ranges and reset values to use as Index in bitmap array
		
		if(fetch_from < 14 )
			fetch_from = 0;  
		else if(fetch_from < 27 )
			fetch_from = 1;
		else if(fetch_from < 41 )
			fetch_from = 2;
		else if(fetch_from < 55 )
			fetch_from = 3;
		else if(fetch_from < 69 )
			fetch_from = 4;
		else if(fetch_from < 82 )
			fetch_from = 5;
		else if(fetch_from < 96 )
			fetch_from = 6;
		else if(fetch_from < 109 )
			fetch_from = 7;
		else if(fetch_from < 123 )
			fetch_from = 8;
		else if(fetch_from < 137 )
			fetch_from = 9;
		else 
			fetch_from = 10;
		
		//Repeat the same for end 
		if(fetch_till < 14 )
			fetch_till = 0; 
		else if(fetch_till < 27 )
			fetch_till = 1; 
		else if(fetch_till < 41 )
			fetch_till = 2; 
		else if(fetch_till < 55 )
			fetch_till = 3; 
		else if(fetch_till < 69 )
			fetch_till = 4; 
		else if(fetch_till < 82 )
			fetch_till = 5; 
		else if(fetch_till < 96 )
			fetch_till = 6; 
		else if(fetch_till < 109 )
			fetch_till = 7; 
		else if(fetch_till < 123 )
			fetch_till = 8;
		else if(fetch_till < 137 )
			fetch_till = 9; 
		else 
			fetch_till = 10; 
		
		if(vid_no==1){
			
			thmb_store.clear();
			
			for(int i=fetch_from; i<(fetch_till+1);i++){					
				
				thmb_store.add(arr_bmp.get(i));			
				total_thmbs1++;				
			}
		}
		
		if(vid_no==2){
			
			for(int i=fetch_from; i<(fetch_till+1);i++){			
				
				thmb_store.add(arr_bmp.get(i));					
				total_thmbs2++;
			}
		}
		
	}
	
	/*private void tester(){
		int ij=0;
		
		Log.w("tester",String.valueOf(total_thmbs));		
		
		while(ij<total_thmbs){				
			
			Bitmap bmp = Bitmap.createBitmap(thmb_store.get(ij));    
		
			try {           	
				bmp.compress(CompressFormat.PNG, 95, new FileOutputStream(new File("/mnt/sdcard/store"+ij+".png")));
			} 
			catch (FileNotFoundException e) {    			
			e.printStackTrace();
			}	
		
			ij=ij+1; 
		}
	}*/
	
	public void setArrLst(ArrayList<Bitmap> i){
		temp=new ArrayList<Bitmap>();
		temp=i;
	}
	
	public ArrayList<Bitmap> getArrList(){
		return temp;
	}
}