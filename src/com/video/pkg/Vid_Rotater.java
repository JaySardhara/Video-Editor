package com.video.pkg;

import com.example.videomixer.R;
import com.welcome.pkg.Base;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import android.widget.Toast;

public class Vid_Rotater extends ActionBarActivity{
	
	ActionBar actionbar;
	ImageView leftRotate,rightRotate,refresh;
	ImageView vidFrame;
	Button convert;
	private String _background="#d3d3d3";  
	String src_path;
	Boolean refreshPressed=false,right=false,left=false;
	Matrix matrix;
	int degrees;
	int orgnl_hght,orgnl_wdth;
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
				
		setContentView(R.layout.rotate_vid);
		
		DisplayMetrics displaymetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
		orgnl_hght = displaymetrics.heightPixels;
		orgnl_wdth = displaymetrics.widthPixels;
		
		View getHandle = findViewById(R.id.parentView);
		//View root = getHandle.getRootView();
		//root.setBackgroundColor(Color.WHITE);
		getHandle.setBackgroundColor(getResources().getColor(android.R.color.white));
		
		src_path=getIntent().getExtras().getString("pth");
		
		TextView txt = (TextView)findViewById(R.id.textView1);
		txt.setTextColor(Color.BLACK);
		
		actionbar=getSupportActionBar();
		actionbar.setHomeButtonEnabled(true);
		actionbar.setDisplayHomeAsUpEnabled(true);
		actionbar.setTitle("Video Rotater");
		actionbar.setIcon(R.drawable.out3);
		actionbar.show();
		
		leftRotate=(ImageView)findViewById(R.id.leftRotate);
		rightRotate=(ImageView)findViewById(R.id.rightRotate);
		refresh=(ImageView)findViewById(R.id.refresh);
		convert=(Button)findViewById(R.id.convert);
		vidFrame=(ImageView)findViewById(R.id.vidFrame);
		
		vidFrame.setBackgroundColor(Color.parseColor(_background));
		createFrame();
		
		leftRotate.setBackgroundColor(Color.parseColor(_background));
		leftRotate.setImageResource(R.drawable.out11);
		leftRotate.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent arg1) {
				switch (arg1.getAction()) {
                case MotionEvent.ACTION_DOWN: {
                	leftRotate.setImageResource(R.drawable.out11_pressed);               	
                	
                    break;
                }
                case MotionEvent.ACTION_CANCEL:{
                	leftRotate.setImageResource(R.drawable.out11);                	
                    break;
                }
                case MotionEvent.ACTION_UP:{
                	leftRotate.setImageResource(R.drawable.out11);                	
                    break;
                }
                }
                return false;
			}
		});
		leftRotate.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {				
				left=true;Log.d("left","clcked");
				rotate();
			}
		});
		
		rightRotate.setBackgroundColor(Color.parseColor(_background));
		rightRotate.setImageResource(R.drawable.out10);
		rightRotate.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent arg1) {
				switch (arg1.getAction()) {
                case MotionEvent.ACTION_DOWN: {
                	rightRotate.setImageResource(R.drawable.out10_pressed);               	
                	
                    break;
                }
                case MotionEvent.ACTION_CANCEL:{
                	rightRotate.setImageResource(R.drawable.out10);                	
                    break;
                }
                case MotionEvent.ACTION_UP:{
                	rightRotate.setImageResource(R.drawable.out10);                	
                    break;
                }
                }
                return false;
			}
		});
		rightRotate.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				right=true;Log.d("rght","clcked");
				rotate();
				
			}
		});
		
		//If video Frame is black; then refresh tries to load other frame from video
		refresh.setBackgroundColor(Color.parseColor(_background));
		refresh.setImageResource(R.drawable.out12);
		refresh.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent arg1) {
				switch (arg1.getAction()) {
                case MotionEvent.ACTION_DOWN: {
                	refresh.setImageResource(R.drawable.out12_pressed);                   	
                    break;
                }
                case MotionEvent.ACTION_CANCEL:{
                	refresh.setImageResource(R.drawable.out12);                	
                    break;
                }
                case MotionEvent.ACTION_UP:{
                	refresh.setImageResource(R.drawable.out12);
                	break;
                }
                }
                return false;
			}
		});
		refresh.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				refreshPressed=true;Log.d("rfrsh","clcked");
				createFrame();				
			}
		});
		
		//convert.setBackgroundColor(Color.DKGRAY);
		convert.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				Intent i = new Intent(getBaseContext(),VidRotate_Engine.class);
				i.putExtra("rotate", degrees);
				i.putExtra("pth", src_path);
				startActivity(i);
				finish();
			}
		});
		
	}

	
	private void rotate(){
		Log.w("start","entry");
		if(left){
			left=false;
			Log.w("left", "enrted");
			degrees-=90;
			if(degrees<=-360)
				degrees=degrees%360;
			
			matrix=new Matrix();
			vidFrame.setScaleType(ScaleType.MATRIX);
			//matrix.postTranslate(-vidFrame.getWidth()/2, -vidFrame.getHeight()/2);			
			matrix.postRotate(degrees,vidFrame.getDrawable().getBounds().centerX(),vidFrame.getDrawable().getBounds().centerY());			
			vidFrame.setImageMatrix(matrix);		
			Log.w("left",String.valueOf(degrees));
			//vidFrame.setScaleType(ScaleType.FIT_XY);			
			
		}
		
		if(right){
			right=false;
			Log.w("rght", "enrted");
			degrees+=90;
			if(degrees>360)
				degrees=degrees%360;
			
			matrix=new Matrix();
			vidFrame.setScaleType(ScaleType.MATRIX);
			matrix.postRotate(degrees,vidFrame.getDrawable().getBounds().centerX(),vidFrame.getDrawable().getBounds().centerY());			
			vidFrame.setVisibility(View.INVISIBLE);
			vidFrame.setImageMatrix(matrix);	
			
			BitmapDrawable drawable = (BitmapDrawable) vidFrame.getDrawable();
			Bitmap bitmap = drawable.getBitmap();
			if(degrees == 90 || degrees == 270)
				bitmap = Bitmap.createScaledBitmap(bitmap, 295, 295, false);
			else
				bitmap = Bitmap.createScaledBitmap(bitmap, 480, 295, false);
			
			vidFrame.setImageBitmap(bitmap);
			vidFrame.setVisibility(View.VISIBLE);
			//bitmap.recycle();
			Log.w("rghtrrrr",String.valueOf(vidFrame.getDrawable().getBounds().width())+String.valueOf(vidFrame.getDrawable().getBounds().height()));
			
			Log.w("right",String.valueOf(degrees));
		}
	}
	
	private void createFrame() {
		
		if(!refreshPressed){
			
			MediaMetadataRetriever getFrame = new MediaMetadataRetriever();
			getFrame.setDataSource(src_path);
			Bitmap bmp;
			bmp=getFrame.getFrameAtTime(1000000, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
			vidFrame.setImageBitmap(bmp);
			vidFrame.setScaleType(ScaleType.FIT_XY);
		}
		else{
			refreshPressed=false;
			
			MediaMetadataRetriever getFrame = new MediaMetadataRetriever();
			getFrame.setDataSource(src_path);
			Bitmap bmp;
			
			String time=getFrame.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
			long timeInmillisec = Long.parseLong( time );
			long duration = timeInmillisec / 1000;
			long hours = duration / 3600;
			long minutes = (duration - hours * 3600) / 60;
			long seconds = duration - (hours * 3600 + minutes * 60);
			
			//Check if video length is not less than 5 seconds
			if(!(seconds < 5)){
				bmp=getFrame.getFrameAtTime(5000000, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
				vidFrame.setImageBitmap(bmp);
			}
			else{
				Toast.makeText(getApplicationContext(), "Your video has too many Black frames !", Toast.LENGTH_LONG).show();
			}
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem menuItem) {
	    switch (menuItem.getItemId()) {
	    case android.R.id.home:
	      Intent homeIntent = new Intent(this, Base.class);
	      homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	      startActivity(homeIntent);
	    }
	  return (super.onOptionsItemSelected(menuItem));
	}
	
	@Override
	public void onBackPressed() {
		
		Intent homeIntent = new Intent(this, Base.class);
	    homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	    startActivity(homeIntent);
	}
	
	@Override
	public void onWindowFocusChanged(boolean hasFocus){
	    int width=vidFrame.getWidth();
	    int height=vidFrame.getHeight();
	    Log.w("ViewHW",String.valueOf(width)+" "+String.valueOf(height));
	}
	
}
