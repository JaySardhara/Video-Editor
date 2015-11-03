package com.video.pkg;

import java.io.File;
import com.bin.rngeseekbar.Rngeseekbar_Modify;
import com.bin.rngeseekbar.Rngeseekbar_Modify.OnRangeSeekBarChangeListener;
import com.example.videomixer.R;
import com.ringdroid.soundfile.CheapSoundFile;
import com.soundedit.pkg.WaveformView;
import com.welcome.pkg.Base;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

public class VidMerge_SngAdd extends Activity{
	
	String aud_pth,resSelected, op;
	Rngeseekbar_Modify<Integer> skbar;
	WaveformView mWaveformView;
	float mDensity;
	int duration, vidSelectedDuration, current_min_pos, current_max_pos, prevw_init, prevw_end;
	MediaPlayer mp;
	Button reselect_Aud, preview, done;
	Boolean isMin=false, isMax=false, initial=true;
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.audeditor_vid);
		
		mWaveformView=(WaveformView)findViewById(R.id.waveform);
		reselect_Aud=(Button)findViewById(R.id.chngAud);
		preview=(Button)findViewById(R.id.cnvrtAud);
		done=(Button)findViewById(R.id.button2);
		
		aud_pth = getIntent().getExtras().getString("sng_pth");
		
		//If this class calls itself, then remember previous value 
		if(getIntent().getExtras().getBoolean("again"))
			vidSelectedDuration=getIntent().getExtras().getInt("d");
		else
			vidSelectedDuration=getIntent().getExtras().getInt("duration");
		
		Log.w("LockDuration",String.valueOf(vidSelectedDuration));
				
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
			mSoundfile=CheapSoundFile.create(aud_pth, listener);			
			Log.d("frames!!!!!",String.valueOf(mSoundfile.getNumFrames()));
			mWaveformView.setSoundFile(mSoundfile,400);
			mWaveformView.recomputeHeights(mDensity);	
			while(mWaveformView.canZoomOut())
				mWaveformView.zoomOut();				
		} catch (Exception e) {
					e.printStackTrace();
		}
		
		
		try {
			String uri=Uri.fromFile(new File(aud_pth)).toString();
			mp=new MediaPlayer();
			mp=MediaPlayer.create(getBaseContext(),  Uri.parse(uri));			
			duration=mp.getDuration();
			Log.w("duration",String.valueOf(duration));			
		}catch (Exception e) {			
			e.printStackTrace();
		}
		
		skbar = new Rngeseekbar_Modify<Integer>(0,duration/1000,getBaseContext());
		skbar.setNotifyWhileDragging(true);		
		skbar.setOnRangeSeekBarChangeListener(new OnRangeSeekBarChangeListener<Integer>() {

			@Override
			public void onRangeSeekBarValuesChanged(Rngeseekbar_Modify<?> bar, Integer minValue, Integer maxValue) {				
				
				/*//Handle Initial stage
				if(initial && minValue!=0){
        			initial=false;
        			prevMin=minValue;        			
        		}
        		else if(initial && maxValue!=(duration/1000)){
        			initial=false;
        			prevMax=maxValue;        			
        		}				
				
								
				Log.w("crit1",String.valueOf(prevMin)+" "+String.valueOf(minValue)+" "+String.valueOf(prevMax)+" "+String.valueOf(maxValue));
				
				
				if(prevMin == minValue){
					isMax=true;
					prevMax=maxValue;
				}
				else if(prevMax == maxValue){
					isMin=true;
					prevMin=minValue;
				}*/
				if(Rngeseekbar_Modify.minThumbPressed)
					isMin=true;
				else if(Rngeseekbar_Modify.maxThumbPressed)
					isMax=true;
					
				Log.d("flgs",String.valueOf(isMin)+" "+String.valueOf(isMax));
				if((maxValue - minValue) > vidSelectedDuration){
					
					Log.d("pos val", String.valueOf(minValue)+" "+String.valueOf(maxValue)+" "+String.valueOf(vidSelectedDuration));
					if(isMin){
						isMin=false;
						skbar.setSelectedMaxValue(minValue+vidSelectedDuration);						
					}
					else if(isMax){
						isMax=false;
						skbar.setSelectedMinValue(maxValue-vidSelectedDuration);						
					}
				}				
                mWaveformView.setParameters((int)(Scaler(minValue)*2.9f),(int)(Scaler(maxValue)*2.9f),0);
                mWaveformView.invalidate();
                
              //handle start stage
        		if(initial && minValue!=0){
        			initial=false;
        			current_min_pos=minValue;
        			mp.seekTo(current_min_pos*1000);
        			Log.d("setminInitial",String.valueOf(current_min_pos));
        		}
        		else if(initial && maxValue!=(duration/1000)){
        			initial=false;
        			current_max_pos=maxValue;
        			mp.seekTo(current_max_pos*1000);
        			Log.d("setmaxInitial",String.valueOf(current_max_pos));
        		}
        			
        		//Handle current stage
        		if(current_max_pos==maxValue){
        			current_min_pos=minValue;
        			mp.seekTo(current_min_pos*1000);
        			Log.d("setmin",String.valueOf(current_min_pos));
        		}
        		else if(current_min_pos==minValue ){
        			current_max_pos=maxValue;
        			mp.seekTo(current_max_pos*1000);
        			Log.d("setmax",String.valueOf(current_max_pos));
        		}          
                //Set last End Pos so to stop at that time
                prevw_end=maxValue;
                //Same for initial start time
                prevw_init=minValue;
                
                
                
			}
						
		});
		ViewGroup view = (ViewGroup)findViewById(R.id.frameLayout1);
		view.addView(skbar);
		
		reselect_Aud.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {	
				//Release mediaplayer
				mp.reset();
				mp.release();
				
				Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
            	startActivityForResult(i,1);
			}
		});
		
		preview.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {				
				new Preview().execute();
			}
		});
		
		
		//Sets different listeners depending upon the Parent call of this class
		if(Base.isSlide){
			//Base.isSlide=false;
			Base.slideAudio=true;
			
			done.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					
					AlertDialog.Builder save = new AlertDialog.Builder(VidMerge_SngAdd.this);
					save.setTitle("Save");
					save.setMessage("Save Sound As:");
					
					final EditText fname = new EditText(getBaseContext());
					fname.setBackgroundColor(Color.GRAY);
					fname.setTextColor(Color.BLACK);
					fname.setCursorVisible(true);
					save.setView(fname);
					
					save.setPositiveButton("Save", new DialogInterface.OnClickListener() {					
						
						public void onClick(DialogInterface dialog, int which) {
							
							op=fname.getText().toString().trim();							
							
							createAlertSpinner();
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
			
		}
		else if(Base.isVidAudEdit){
			Base.isVidAudEdit=false;
			
			done.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					
					AlertDialog.Builder save = new AlertDialog.Builder(VidMerge_SngAdd.this);
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
							
							Intent lncher = new Intent(VidMerge_SngAdd.this,VidSndEdit_Engine.class);
							lncher.putExtra("Oputname", name);
							lncher.putExtra("duration", getIntent().getExtras().getInt("duration"));
							lncher.putExtra("sng_pth",getIntent().getExtras().getString("sng_pth"));
							lncher.putExtra("vid_pth",getIntent().getExtras().getString("vid_pth"));
							lncher.putExtra("vidStart",getIntent().getExtras().getInt("vidStart"));
							lncher.putExtra("vidEnd",getIntent().getExtras().getInt("vidEnd"));
							lncher.putExtra("sngStart",prevw_init);
							lncher.putExtra("sngEnd",prevw_end);
							startActivity(lncher);
							finish();
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
			
		}
		else{
			
			done.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
									
					if((prevw_end-prevw_init) < vidSelectedDuration){
						
						AlertDialog.Builder alertDialog = new AlertDialog.Builder(VidMerge_SngAdd.this);
			 		       
				        alertDialog.setTitle("Warning !!");		 
				        
				        alertDialog.setMessage("Your selected Audio is smaller than video; so there will be no sound at end of video.");		 
				       
				        alertDialog.setIcon(R.drawable.out13);		 
				        
				        alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
				            public void onClick(DialogInterface dialog,int which) {		 
				            	
				            	createAlertSpinner();			            				            	
				            }
				        });		 
				          
				        alertDialog.show();		
					}
					
					else{
						createAlertSpinner();
					}
				}
			});
		}
		
		
		
	}
	
	@Override
	public void onBackPressed() {
		
		Intent i = new Intent(getBaseContext(),Base.class);
		i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(i);
		finish();
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
						
			Intent lncher=new Intent(this,VidMerge_SngAdd.class);
			lncher.putExtra("sng_pth",sng_pth);
			lncher.putExtra("again", true);
			lncher.putExtra("d", vidSelectedDuration);
			startActivity(lncher);			
			finish();
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
		//Log.d("Scaler",String.valueOf(scaled_val));
		return scaled_val;		
	}
	
	private class Preview extends AsyncTask<Void,Void,Void> {		
		
		@Override
		protected void onPreExecute(){					
			
			mp.seekTo(prevw_init*1000);	
			mp.start();		
			Log.d("ccrr",String.valueOf(mp.getCurrentPosition())+" "+String.valueOf(prevw_end));
		}

		
		protected Void doInBackground(Void... params) {
			
			while(mp.getCurrentPosition() < prevw_end*1000 ){
				Log.d("!@!@","in loop");
			}
			
			return null;
		}
		
		@Override		
        protected void onPostExecute(Void v) {
			mp.pause();
        }		
		
	}
	
	private void createAlertSpinner(){
		AlertDialog.Builder builder;
    	AlertDialog alertDialog;
    	builder=new AlertDialog.Builder(VidMerge_SngAdd.this);
    	
    	builder.setTitle("Select Resolution");
    	builder.setIcon(R.drawable.out13);
    	//builder.setMessage("Resolution you want..");
    	
    	builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {		 
            			
            	mp.release();
            	
            	if(Base.isSlide){
					
					Intent lncher = new Intent(VidMerge_SngAdd.this,Slide_Engine.class);
					lncher.putExtra("Oputname", op);
					lncher.putExtra("res", resSelected);
					//lncher.putExtra("duration", getIntent().getExtras().getInt("duration"));
					lncher.putExtra("sng_pth",getIntent().getExtras().getString("sng_pth"));
					//lncher.putExtra("picCnt",getIntent().getExtras().getString("picCnt"));
					lncher.putExtra("SDuration",getIntent().getExtras().getInt("SDuration"));						
					lncher.putExtra("sngStart",prevw_init);
					lncher.putExtra("sngEnd",prevw_end);
					startActivity(lncher);
					finish();
            	}
            	else{
            		Intent i = new Intent(VidMerge_SngAdd.this,VidMerge_Engine.class);
                	i.putExtra("isDirect",false);
                	i.putExtra("res", resSelected);
                	i.putExtra("vidpos",getIntent().getExtras().getIntArray("vidpos"));
                	i.putExtra("pth1", getIntent().getExtras().getString("pth1"));
                	i.putExtra("pth2", getIntent().getExtras().getString("pth2"));
                	i.putExtra("sng", aud_pth);
                	i.putExtra("sngStart", prevw_init);
                	i.putExtra("sngEnd", prevw_end);
                	startActivity(i);
                	finish();		
            	}
            			            	
            }
        });		 
    	
    	LayoutInflater inflater = (LayoutInflater)(VidMerge_SngAdd.this).getSystemService(LAYOUT_INFLATER_SERVICE);
    	View layout = inflater.inflate(R.layout.vidmerge_spinner_res, null);
    	
    	String res_holder[] = new String[3];		            	
    	res_holder[0] = "176x144";
    	res_holder[1] = "480x360";
    	res_holder[2] = "640x420";
    	
    	Spinner sp = (Spinner)layout.findViewById(R.id.resSpinner);
    	ArrayAdapter<String> adpter = new ArrayAdapter<String>(VidMerge_SngAdd.this,android.R.layout.simple_spinner_item,res_holder);
    	adpter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);		
    	sp.setAdapter(adpter);
    	sp.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent,
					View arg1, int arg2, long arg3) {
				
				String temp = parent.getItemAtPosition(arg2).toString();
				
				resSelected=temp;
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {							
				
				resSelected="176x144";
			}
		});
    	sp.setVisibility(View.VISIBLE);
    	
    	
    	builder.setView(layout);
    	alertDialog=builder.create();
    	alertDialog.show();
	}
	
}
