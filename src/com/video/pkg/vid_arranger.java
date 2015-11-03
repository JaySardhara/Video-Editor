package com.video.pkg;

import java.util.ArrayList;

import com.example.videomixer.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.View.DragShadowBuilder;
import android.view.View.OnDragListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;

/**
 * This class is used to swap the selected area of video to merge. 
 * Also user can select whether to add new audio or continue with
 * same.
 * 
 * Drag and drop feature is used for swapping 
 * 
 * 
 * @author Jay Sardhara
 *
 */

public class vid_arranger extends Activity implements OnTouchListener, OnDragListener{

	Button prvw, nxt;
	Drawable enterShape,normalShape;
	LinearLayout vidfrm1,vidfrm2;
	View viewToReplace,currentChildView;
	ArrayList<Bitmap> thmb_reciever;
	int total_thmbs1,total_thmbs2,stopPos;
	SeekBar progress; 
	int vidpos[];
	Boolean swap=false,swap_cmplete=false,split_point=false;
	Boolean isFirstTime=true,isViewOn=true,vid1=true,vidNotReseted=true;
	String vid_pth1,vid_pth2, resSelected="";
	CustomVideoView setVideoview;
	Handler mHandler=new Handler();	
	static Boolean bVideoIsBeingTouched=false,bVideoIsPaused=false;
	
	
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.selected_vid_arranger);
		
		setVideoview=new CustomVideoView(this);      
        setVideoview=(CustomVideoView)findViewById(R.id.videoView1);
		findViewById(R.id.horizontalScrollView1).setOnTouchListener(this);
		findViewById(R.id.horizontalScrollView2).setOnTouchListener(this);
		findViewById(R.id.TopLeft).setOnDragListener(this);
		findViewById(R.id.BottomRight).setOnDragListener(this);
		
		vidfrm1=(LinearLayout)findViewById(R.id.childThmbnail1);
		vidfrm2=(LinearLayout)findViewById(R.id.childThmbnail2);
		progress=(SeekBar)findViewById(R.id.progress_skbar);
		nxt=(Button)findViewById(R.id.button3);
		
		//Setting custom shapes for dragging effects
		enterShape=getResources().getDrawable(R.drawable.shape_droptarget);
		normalShape = getResources().getDrawable(R.drawable.shape);
		
		//Receive all the selected thumbs from previous activity
		/**Had to use Getter and Setter instead of passing by Intent because of memory constraint of intent passing*/ 		
		thmb_reciever=new ArrayList<Bitmap>();
		thmb_reciever=(new VMixer()).getArrList();
		Log.w("size",String.valueOf(thmb_reciever.size()));
		
		//Get total thmbs so we can know how many we have to handle in Drag View
		total_thmbs1=getIntent().getExtras().getInt("tt1");
		total_thmbs2=getIntent().getExtras().getInt("tt2");		
		Log.w("thmbs",String.valueOf(total_thmbs1)+" "+String.valueOf(total_thmbs2));
		
		//Get the start and end positions of both the selected video
		vidpos=new int[4];
		vidpos=getIntent().getExtras().getIntArray("vidpos");		
		
		//Get the video path from previous activity
		vid_pth1=getIntent().getExtras().getString("vidpath1");
		vid_pth2=getIntent().getExtras().getString("vidpath2");
		
		thmb_populator(total_thmbs1,total_thmbs2);
						
		//Set max duration of seekbar
		//progress.setMax((vidpos[1]-vidpos[0])+(vidpos[3]-vidpos[2]));
		
		startVideo(vid_pth1,vidpos[0]);
		//resetSeekbar();
		
		setVideoview.setDimensions(480,270);
    	setVideoview.getHolder().setFixedSize(480,270);
		
		progress.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				setVideoview.start();
				new SeekBarHandler().execute();
				
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				
				bVideoIsPaused=true;
				setVideoview.pause();
				
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				
				if(fromUser){	
					
					if(progress>=50 && vid1)
						vidNotReseted=true;
					
					if(progress<50 && !vid1)
						vidNotReseted=true;
					
					if(progress==50){
						vidNotReseted=false;
						vid_switcher();
					}
					
					if(progress<50){
						
						//Change video if not changed at progress==50
						if(vidNotReseted){
							vidNotReseted=false;
							vid_switcher();
						}
													
						Log.w("details",String.valueOf(setVideoview.getCurrentPosition()/1000)+" skbarprgress "+String.valueOf(progress));
						Log.w("seebarlstnr",String.valueOf(Scaler(progress,0,50,vidpos[0], vidpos[1])));
						setVideoview.seekTo((int) (Scaler(progress,0,50,vidpos[0], vidpos[1])*1000)); 
					}
					else{
						
						//Change video if not changed at progress==50
						if(vidNotReseted){
							vidNotReseted=false;
							vid_switcher();
						}
						
						Log.w("details",String.valueOf(setVideoview.getCurrentPosition()/1000)+" skbarprgress "+String.valueOf(progress));
						Log.w("seebarlstnr",String.valueOf(Scaler(progress,50,100,vidpos[2], vidpos[3])));
						setVideoview.seekTo((int) (Scaler(progress,50,100,vidpos[2], vidpos[3])*1000)); 
					}
				}
				
			}
		});
		
		setVideoview.setOnTouchListener(new OnTouchListener() {
			
     		 @Override
     		    public boolean onTouch(View v, MotionEvent event) {
     		    if (!bVideoIsBeingTouched) {
     		        bVideoIsBeingTouched = true;
     		    if (setVideoview.isPlaying()) {
     		    	Log.d("pase","pse");
     		    	stopPos=setVideoview.getCurrentPosition();
     		        setVideoview.pause();
     		        bVideoIsPaused=true;
     		        
     		    } else {
     		    	bVideoIsPaused=false;
     		    	Log.d("res","res");
     		    	setVideoview.seekTo(stopPos);
     		    	setVideoview.start();
     		    	new SeekBarHandler().execute();
     		    	//playbtn.setVisibility(View.INVISIBLE);
     		        //videoView.resume(); <== resets everytime so replaced by above code
     		    }
     		    mHandler.postDelayed(new Runnable() {
     		        public void run() {
     		            bVideoIsBeingTouched = false;
     		            Log.d("touchflg","set");
     		        }
     		        }, 200);
     		    }
  				return true;
  			}
  		});
		
		
		setVideoview.setOnPreparedListener(new OnPreparedListener() {
			
			@Override
			public void onPrepared(MediaPlayer mp) {				
				new SeekBarHandler().execute();				
			}
		});
		
		
		nxt.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				
				AlertDialog.Builder alertDialog = new AlertDialog.Builder(vid_arranger.this);
	 		       
		        alertDialog.setTitle("Add Custom Audio ?");		 
		        
		        alertDialog.setMessage("Do you want to add Custom Audio or Merge Videos directly ? ");		 
		       
		        alertDialog.setIcon(R.drawable.out13);		 
		        
		        alertDialog.setPositiveButton("Add Audio", new DialogInterface.OnClickListener() {
		            public void onClick(DialogInterface dialog,int which) {		 
		            	
		            	Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
		            	startActivityForResult(i,1);
		            }
		        });		 
		        
		        alertDialog.setNegativeButton("Merge Directly", new DialogInterface.OnClickListener() {
		            public void onClick(DialogInterface dialog, int which) {		            
		            
		            	AlertDialog.Builder builder;
		            	AlertDialog alertDialog;
		            	builder=new AlertDialog.Builder(vid_arranger.this);
		            	
		            	builder.setTitle("Select Resolution");
		            	builder.setIcon(R.drawable.out13);
		            	//builder.setMessage("Resolution you want..");
		            	
		            	builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
				            public void onClick(DialogInterface dialog,int which) {		 
				            					      
				            	Intent i = new Intent(vid_arranger.this,VidMerge_Engine.class);
				            	i.putExtra("isDirect",true);
				            	i.putExtra("res", resSelected);
				            	i.putExtra("vidpos",vidpos);
				            	i.putExtra("pth1", vid_pth1);
				            	i.putExtra("pth2", vid_pth2);
				            	startActivity(i);
				            	finish();				            	
				            }
				        });		 
		            	
		            	LayoutInflater inflater = (LayoutInflater)(vid_arranger.this).getSystemService(LAYOUT_INFLATER_SERVICE);
		            	View layout = inflater.inflate(R.layout.vidmerge_spinner_res, null);
		            	
		            	String res_holder[] = new String[3];		            	
		            	res_holder[0] = "176x144";
		            	res_holder[1] = "480x360";
		            	res_holder[2] = "640x420";
		            	
		            	Spinner sp = (Spinner)layout.findViewById(R.id.resSpinner);
		            	ArrayAdapter<String> adpter = new ArrayAdapter<String>(vid_arranger.this,android.R.layout.simple_spinner_item,res_holder);
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
					
		        });		 
		       
		        alertDialog.show();				
				
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
			
			setVideoview.stopPlayback();
			
			//Total selected duration of both Videos
			int totalDur = ((vidpos[1]-vidpos[0]) + (vidpos[3]-vidpos[2]));
			Log.w("sendDura",String.valueOf(totalDur));
			
			Intent lncher=new Intent(this,VidMerge_SngAdd.class);
			lncher.putExtra("sng_pth",sng_pth);
			lncher.putExtra("duration", totalDur);
			lncher.putExtra("vidpos",vidpos);
			lncher.putExtra("pth1", vid_pth1);
			lncher.putExtra("pth2", vid_pth2);
			startActivity(lncher);			
        }  
	}
	
	@Override
	public boolean onTouch(View view, MotionEvent motionEvent) {
		if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
	        ClipData data = ClipData.newPlainText("", "");
	        DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
	        view.startDrag(data, shadowBuilder, view, 0);
	        view.setVisibility(View.INVISIBLE);
	        return true;
	      } else {
	        return false;
	      }		
	}

	@SuppressWarnings("deprecation")
	@Override
    public boolean onDrag(View v, DragEvent event) {
      //int action = event.getAction();
      switch (event.getAction()) {
      case DragEvent.ACTION_DRAG_STARTED:
        // do nothing
        break;
      case DragEvent.ACTION_DRAG_ENTERED:
        v.setBackgroundDrawable(enterShape);        
        
        //obtaining childs of both views to swap them
        viewToReplace = (View) event.getLocalState();
        currentChildView = ((ViewGroup)v).getChildAt(0);         
        
        if(!viewToReplace.equals(currentChildView)){        	
        	//obtaining and removing parent of view initiating Dragging
            ViewGroup parent = (ViewGroup) viewToReplace.getParent();            
            parent.removeView(viewToReplace);
            
            //Removing and adding dragged child to current parent
            ((ViewGroup)v).removeAllViews();
            LinearLayout addView1 = (LinearLayout)v;        
            addView1.addView(viewToReplace);
            
            //Completing swap
            LinearLayout addView2 = (LinearLayout)parent;        
            addView2.addView(currentChildView);
            viewToReplace.setVisibility(View.VISIBLE);
            
            //Set flag to indicate swap has started to reset the seekbar ranges
            swap=true;
        }
        else{        	
        	swap=false;
        	Log.w("else","part");
        }
        
        break;
      case DragEvent.ACTION_DRAG_EXITED:
        v.setBackgroundDrawable(normalShape);        
        break;
      case DragEvent.ACTION_DROP:        
    	viewToReplace.setVisibility(View.VISIBLE);  
    	
    	/**@swap_cmplete is set only if the user has swapped and released the drag
    	 * 
    	 * This is to avoid resetting the seekbar while user is still swapping without releasing drag
    	 */
    	if(swap){
    		swap=false;
    		swap_cmplete=true;
    	}
    	
    	//Invoke reset seekbar  => Its still bit buggy as reset will happen when user drags on opposite side and reverts back without dropping
    	if(swap_cmplete){
    		swap_cmplete=false;
    		Log.d("last","tst case");
    		//resetSeekbar();
    	}
    		
    	
        break;
      case DragEvent.ACTION_DRAG_ENDED:
        v.setBackgroundDrawable(normalShape);
        viewToReplace.setVisibility(View.VISIBLE);        
        break;        
      default:
        break;
      }
      Log.w("swp",String.valueOf(swap));
      Log.w("swpcmplete",String.valueOf(swap_cmplete));
      return true;
    }  
	
	View insertPhoto(Bitmap bitmap, int width){
    	//Bitmap bm = decodeSampledBitmapFromUri(bitmap, 66, 65);
    	Bitmap bm = Bitmap.createScaledBitmap(bitmap, width, 65, true); 	
    	LinearLayout layout = new LinearLayout(getApplicationContext());
    	layout.setLayoutParams(new LayoutParams(width, 65));
    	layout.setGravity(Gravity.CENTER);
    	
    	ImageView imageView = new ImageView(getApplicationContext());
    	imageView.setLayoutParams(new LayoutParams(width, 65));
    	imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
    	imageView.setImageBitmap(bm);
    	
    	layout.addView(imageView);
    	return layout;
    }
	
	
	//To populate thumbs as per their quantity, so size is changed as per it
	private void thmb_populator(int t1, int t2){
		
		int i=0;
		//Populate for Frame 1
		if(t1 == 11){
			for(i=0;i<11;i++)
				vidfrm1.addView(insertPhoto(thmb_reciever.get(i),36));
		}
		else if(t1 == 10){
			for(i=0;i<10;i++)
				vidfrm1.addView(insertPhoto(thmb_reciever.get(i),40));			
		}
		else if(t1 == 9){
			for(i=0;i<9;i++)
				vidfrm1.addView(insertPhoto(thmb_reciever.get(i),44));			
		}
		else if(t1 == 8){
			for(i=0;i<8;i++)
				vidfrm1.addView(insertPhoto(thmb_reciever.get(i),50));			
		}
		else if(t1 == 7){
			for(i=0;i<7;i++)
				vidfrm1.addView(insertPhoto(thmb_reciever.get(i),56));			
		}
		else if(t1 == 6){
			for(i=0;i<6;i++)
				vidfrm1.addView(insertPhoto(thmb_reciever.get(i),66));			
		}
		else if(t1 == 5){
			for(i=0;i<5;i++)
				vidfrm1.addView(insertPhoto(thmb_reciever.get(i),79));			
		}
		else if(t1 == 4){
			for(i=0;i<4;i++)
				vidfrm1.addView(insertPhoto(thmb_reciever.get(i),99));			
		}
		else if(t1 == 3){
			for(i=0;i<3;i++)
				vidfrm1.addView(insertPhoto(thmb_reciever.get(i),132));			
		}
		else if(t1 == 2){
			for(i=0;i<2;i++)
				vidfrm1.addView(insertPhoto(thmb_reciever.get(i),198));			
		}
		else if(t1 == 1){
			for(i=0;i<1;i++)
				vidfrm1.addView(insertPhoto(thmb_reciever.get(i),396));			
		}
				
		//Populate for frame 2
		if(t2 == 11){
			for(int j= 0;j<11;j++)
				vidfrm2.addView(insertPhoto(thmb_reciever.get(i++),36));
		}
		else if(t2 == 10){
			for(int j=0;j<10;j++)
				vidfrm2.addView(insertPhoto(thmb_reciever.get(i++),40));			
		}
		else if(t2 == 9){
			for(int j=0;j<9;j++)
				vidfrm2.addView(insertPhoto(thmb_reciever.get(i++),44));			
		}
		else if(t2 == 8){
			for(int j=0;j<8;j++)
				vidfrm2.addView(insertPhoto(thmb_reciever.get(i++),50));			
		}
		else if(t2 == 7){
			for(int j=0;j<7;j++)
				vidfrm2.addView(insertPhoto(thmb_reciever.get(i++),56));			
		}
		else if(t2 == 6){
			for(int j=0;j<6;j++)
				vidfrm2.addView(insertPhoto(thmb_reciever.get(i++),66));			
		}
		else if(t2 == 5){
			for(int j=0;j<5;j++)
				vidfrm2.addView(insertPhoto(thmb_reciever.get(i++),79));			
		}
		else if(t2 == 4){
			for(int j=0;j<4;j++)
				vidfrm2.addView(insertPhoto(thmb_reciever.get(i++),99));			
		}
		else if(t2 == 3){
			for(int j=0;j<3;j++)
				vidfrm2.addView(insertPhoto(thmb_reciever.get(i++),132));			
		}
		else if(t2 == 2){
			for(int j=0;j<2;j++)
				vidfrm2.addView(insertPhoto(thmb_reciever.get(i++),198));			
		}
		else if(t2 == 1){
			for(int j=0;j<1;j++)
				vidfrm2.addView(insertPhoto(thmb_reciever.get(i++),396));			
		}
		
	}
	
	
	//Reset seekbar and its ranges on swap for recalibration
	private void resetSeekbar(){
		
		if(isFirstTime){			
			progress.setMax(vidpos[3]);
			isFirstTime=false;
		}
		else{
			
		}
		
	}
	
	private void startVideo(final String curr_path, int seekTo) {		
		
		setVideoview.stopPlayback();
		Log.w("seektime",String.valueOf(seekTo));
		setVideoview.setVideoPath(curr_path);
		setVideoview.seekTo(seekTo*1000);
		setVideoview.start();	
		
	}
	
	public class SeekBarHandler extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);			
		    Log.d("Seek Bar Handler","Destroyed");	
		    
		    if(!bVideoIsPaused)
		    	vid_switcher();		    
		}

		@Override
		protected void onProgressUpdate(Void... values) {
			super.onProgressUpdate(values);
			
			
			if(vid1){
				
				//Know bug of seekTo resulting in error if Vid changed; Handled via try/catch
				try{
					
					if(vidpos[1]>(setVideoview.getCurrentPosition()/1000)){
						
					    progress.setProgress((int) Scaler(setVideoview.getCurrentPosition()/1000, vidpos[0], vidpos[1],0,50));
						//Log.w("isvid1",String.valueOf(vidpos[0])+" "+String.valueOf(vidpos[1])+" "+String.valueOf(setVideoview.getCurrentPosition()));
					    Log.w("vid1Progress",String.valueOf(progress.getProgress()));
					}
					else{
						//Sync seekbar to progress=50 if not synced
						if(progress.getProgress()!=50)
							progress.setProgress(50);
						
						Log.w("Vid1@@@","over");
						split_point=true;					
					}
				}
				catch(Exception e){
					e.printStackTrace();
				}
			}
			else{
				try{
					
					if(vidpos[3]>(setVideoview.getCurrentPosition()/1000)){
						
					    progress.setProgress((int) (Scaler(setVideoview.getCurrentPosition()/1000, vidpos[2], vidpos[3],50,100)));
						Log.w("isvid2",String.valueOf(vidpos[3])+" "+String.valueOf(setVideoview.getCurrentPosition()));
					    Log.w("vid2Progress",String.valueOf(progress.getProgress()));
					    
					}
					else{	
						//Sync seekbar to progress=100 if not synced
						if(progress.getProgress()!=100)
							progress.setProgress(100);
						
						Log.w("Vid2@@@","over");
						split_point=true;					
					}
					
				}
				catch(Exception e){
					e.printStackTrace();
				}
			}
		    
		}

		@Override
		protected Void doInBackground(Void... arg0) {
			try {				
	            Thread.sleep(200);
	        } catch (InterruptedException e) {		            
	            e.printStackTrace();
	        }	
						
		    while(setVideoview.isPlaying()&&isViewOn==true) {
		        try {
		            Thread.sleep(200);
		        } catch (InterruptedException e) {		            
		            e.printStackTrace();
		        }
		   
		    if(!split_point)
		    	onProgressUpdate();
		    else
		    	break;
		    
		    }
		    Log.w("exit","stamp");
		    return null;
		}

	}
	
	private float Scaler(int val_to_scale,float minimum,float maximum,int range_min,int range_max){
		
		//Method to scale down values in range 0-50/50-100
		//Also used to reverse scale for obtaining seekbar values
		
		/**
		 * Formula used (b-a)(x-min) + a
		 * 				------------	
		 * 				 max - min
		 *
		 * where [min,max] is range to scale and [a,b] is [0,50]/[50-100]
		 * and x is current value to scale.
		 * 
		 * */
		
		float min=minimum,max=maximum,a=range_min,b=range_max,x=val_to_scale;
		
		float scaled_val= (((b-a)*(x-min))/(max-min))+a;
		//Log.d("Scaler",String.valueOf(scaled_val));
		return scaled_val;
		
	}
	
	
	/**This method is used to change the video whenever required so as to calibrate VideoView as per seekbar change*/
	private void vid_switcher(){
		
		if(!(progress.getProgress()==100) && progress.getProgress()>=50 ){Log.w("repeatpoint",String.valueOf(progress.getProgress()));
			vid1=false;
			split_point=false;	
			progress.setProgress(50);
			setVideoview.stopPlayback();
			startVideo(vid_pth2, vidpos[2]);			
		}
		else if(progress.getProgress()<50){Log.w("repeat1point1",String.valueOf(progress.getProgress()));
			vid1=true;
			split_point=false;	
			setVideoview.stopPlayback();
			startVideo(vid_pth1, vidpos[0]);
		}
		else{
			Log.w("vidSwtcher","end reached");
			setVideoview.stopPlayback();
		}
		
	}
}
