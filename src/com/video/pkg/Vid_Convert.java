package com.video.pkg;

import java.util.ArrayList;
import java.util.List;

import com.example.videomixer.R;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class Vid_Convert extends ActionBarActivity implements OnItemSelectedListener{

	String src_path, extension, onlyName;
	private String _curFormat="";
	private int _prematureFiring=0;
	private List<String> format_extensions = new ArrayList<String>();
	private List<String> res_holder = new ArrayList<String>();
	private List<String> fps_holder = new ArrayList<String>();
	private List<String> bitrate_holder = new ArrayList<String>();
	private List<String> channel_holder = new ArrayList<String>();
	private ArrayAdapter<String> pAdapter, fAdapter, cAdapter, rAdapter, fpsAdapter, bAdapter;
	private Spinner resolution, fps, bitrate, channel;
	private Boolean isDefaultProfile=true;
	String cFormat, cRes, cFps, cAC, cBit;
	Boolean DefaultRes=true, DefaultFps=true, DefaultAC=true, DefaultBit=true;
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.vid_convert);
		
		this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN); 
		
		src_path=getIntent().getExtras().getString("pth");
		extensionPuller(src_path);
		
		TextView vidName = (TextView)findViewById(R.id.textView1);
		stringPuller(src_path);
		vidName.setText(onlyName);
		
		//Set background for some UI elements
		FrameLayout vidBifercator = (FrameLayout)findViewById(R.id.vidBifercator);
		vidBifercator.setBackgroundColor(Color.DKGRAY);
		FrameLayout audBifercator = (FrameLayout)findViewById(R.id.audBifercator);
		audBifercator.setBackgroundColor(Color.DKGRAY);
		final EditText fileName = (EditText)findViewById(R.id.fileName);
		fileName.setBackgroundColor(Color.WHITE);
		fileName.setTextColor(Color.BLACK);
		fileName.setImeOptions(EditorInfo.IME_ACTION_DONE);
		fileName.setSingleLine();
		
		resolution = (Spinner)findViewById(R.id.resSpinner);
		fps = (Spinner)findViewById(R.id.fpsSpinner);
		bitrate = (Spinner)findViewById(R.id.bitrateSpinner);
		channel = (Spinner)findViewById(R.id.channelSpinner);
		
		Spinner profile = (Spinner)findViewById(R.id.profileSpinner);		
		List<String> profile_items = new ArrayList<String>();
		profile_items.add("Default");
		profile_items.add("Quality");
		profile_items.add("Custom");		
		pAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, profile_items);
		pAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);		
		profile.setAdapter(pAdapter);
		profile.setOnItemSelectedListener(this);
		
		Spinner format = (Spinner)findViewById(R.id.formatSpinner);				
		format_spinner_populator();				
		fAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, format_extensions);
		fAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);		
		format.setAdapter(fAdapter);
		format.setOnItemSelectedListener(this);				
				
		resolution_spinner_populator();		
		rAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, res_holder);
		rAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		resolution.setAdapter(rAdapter);
		resolution.setOnItemSelectedListener(this);		
		
		fps_spinner_populator();		
		fpsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, fps_holder);
		fpsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		fps.setAdapter(fpsAdapter);
		fps.setOnItemSelectedListener(this);		
		
		bitrate_spinner_populator();		
		bAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, bitrate_holder);
		bAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		bitrate.setAdapter(bAdapter);
		bitrate.setOnItemSelectedListener(this);		
		
		channel_spinner_populator();		
		cAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, channel_holder);
		cAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		channel.setAdapter(cAdapter);
		channel.setOnItemSelectedListener(this);		
		
		//If user has not clicked on any format, then handle default/first format
		if(extension.equals("mp4"))
			cFormat="avi";
		else
			cFormat="mp4";
		
		//Lock other spinner as Default is primary profile
		resetVisiblity("Default");
		
		Button convert = (Button)findViewById(R.id.convertion);
		convert.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {				
								
				Intent i = new Intent(Vid_Convert.this,VidConvert_Engine.class);
				i.putExtra("vidpath",src_path);
				i.putExtra("origFormat",extension);
				i.putExtra("convertTo",cFormat);
				i.putExtra("saveTo", fileName.getText().toString());
				i.putExtra("res",cRes);
				i.putExtra("fps",cFps);
				i.putExtra("bitrate",cBit);
				i.putExtra("channel",cAC);
				i.putExtra("bRes",DefaultRes);
				i.putExtra("bFps",DefaultFps);
				i.putExtra("bBit",DefaultBit);
				i.putExtra("bAc",DefaultAC);
				startActivity(i);
				finish();
			}
		});
		
		
	}

	private void channel_spinner_populator() {
		
		channel_holder.clear();
		Log.w("channelBools",String.valueOf(isDefaultProfile));
		if(isDefaultProfile){
			channel_holder.add("Default Channels");			
		}
		else{
			//If 3gp then only 1(mono) channel is supported
			if(_curFormat.equals("3gp")){
				channel_holder.add("1 (Mono)");
			}
			else{
				channel_holder.add("Default Channels");
				channel_holder.add("1 (Mono)");
				channel_holder.add("2");
			}			
		}		
	}


	private void bitrate_spinner_populator() {
		
		bitrate_holder.clear();
		Log.w("bitsBools",String.valueOf(isDefaultProfile));
		if(isDefaultProfile){
			bitrate_holder.add("Current Bitrate");			
		}
		else{
			bitrate_holder.add("Current Bitrate");
			bitrate_holder.add("24kbps (Low)");
			bitrate_holder.add("128kbps (High)");
			bitrate_holder.add("192kbps (HD)");			
		}
		
	}


	private void fps_spinner_populator() {
		
		fps_holder.clear();
		Log.w("fpsBools",String.valueOf(isDefaultProfile));
		if(isDefaultProfile){
			fps_holder.add("Current FPS");			
		}
		else{
			fps_holder.add("Current FPS");
			fps_holder.add("12 (Low)");
			fps_holder.add("30 (High/HD)");
		}
		
	}


	private void resolution_spinner_populator() {
		
		res_holder.clear();
		Log.w("resBools",String.valueOf(isDefaultProfile));
		if(isDefaultProfile){Log.w("!!","!!");
			res_holder.add("Current Resolution");			
		}
		else{
			//If 3gp then use following sizes only
			if(_curFormat.equals("3gp")){
				res_holder.add("Current Resolution");
				res_holder.add("128x96");
				res_holder.add("352x288");
				res_holder.add("704x576");
				res_holder.add("1408x1152");			
			}
			else{
				res_holder.add("Current Resolution");
				res_holder.add("176x144 (Low)");
				res_holder.add("480x360 (High)");
				res_holder.add("1280x720 (HD)");		
			}			
		}
				
	}


	private void format_spinner_populator() {		
		
		format_extensions.clear();
		Log.w("frmtBools",String.valueOf(isDefaultProfile));
		if(extension.equals("mp4")){
			format_extensions.add("avi");
			format_extensions.add("flv");
			format_extensions.add("mov");
			format_extensions.add("3gp");
		}
		else if(extension.equals("avi")){
			format_extensions.add("mp4");
			format_extensions.add("flv");
			format_extensions.add("mov");
			format_extensions.add("3gp");
		}
		else if(extension.equals("flv")){
			format_extensions.add("mp4");
			format_extensions.add("avi");
			format_extensions.add("mov");			
			format_extensions.add("3gp");
		}
		else if(extension.equals("mov")){
			format_extensions.add("mp4");
			format_extensions.add("avi");
			format_extensions.add("flv");			
			format_extensions.add("3gp");
		}
		else if(extension.equals("3gp")){
			format_extensions.add("mp4");
			format_extensions.add("avi");
			format_extensions.add("flv");		
			format_extensions.add("mov");
		}
		
	}


	private void stringPuller(String path) {
		
		int i=1;
		while(!path.substring(path.lastIndexOf(".") - i).startsWith("/")){
			onlyName = path.substring(path.lastIndexOf(".") - i);
			i++;			
		}
	}


	private void extensionPuller(String path) {		
		extension = path.substring(path.lastIndexOf(".") + 1);		
	}


	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				
		if(_prematureFiring < 6)
			_prematureFiring++;
		
		else{
			if(parent.getId() == R.id.profileSpinner){
				String temp = parent.getItemAtPosition(position).toString();
				
				if(temp.equals("Default")){
					Log.w("bools","set");
					isDefaultProfile=true;
				}	
				else if(temp.equals("Custom")){
					isDefaultProfile=false;
					Log.w("bools!!",String.valueOf(isDefaultProfile));
					refreshAdapters();
				}
				else{
					isDefaultProfile=false;
					Toast.makeText(Vid_Convert.this, "Dummy", Toast.LENGTH_SHORT).show();
				}			
				
				resetVisiblity(temp);
			}
			
			if(parent.getId() == R.id.formatSpinner){
				_curFormat = parent.getItemAtPosition(position).toString();
				Log.w("curProfile",_curFormat);
				
				cFormat = _curFormat;
				
				refreshAdapters();
			}
			
			if(parent.getId() == R.id.resSpinner){
				
				if(isDefaultProfile)
					DefaultRes=true;
				else{
					DefaultRes=false;
					cRes =  parent.getItemAtPosition(position).toString();
				}					
			}		
			
			if(parent.getId() == R.id.fpsSpinner){
				
				if(isDefaultProfile)
					DefaultFps=true;
				else{
					DefaultFps=false;
					cFps =  parent.getItemAtPosition(position).toString();
				}	
			}
			
			if(parent.getId() == R.id.channelSpinner){
				
				if(isDefaultProfile)
					DefaultAC=true;
				else{
					DefaultAC=false;
					cAC =  parent.getItemAtPosition(position).toString();
				}	
			}
			
			if(parent.getId() == R.id.bitrateSpinner){
				
				if(isDefaultProfile)
					DefaultBit=true;
				else{
					DefaultBit=false;
					cBit =  parent.getItemAtPosition(position).toString();
				}	
			}
			
		}	
		
	}

	//Refresh all spinners 
	private void refreshAdapters() {		
		
		format_spinner_populator();
		fAdapter.notifyDataSetChanged();
		
		channel_spinner_populator();
		cAdapter.notifyDataSetChanged();
		
		resolution_spinner_populator();
		rAdapter.notifyDataSetChanged();
		
		fps_spinner_populator();
		fpsAdapter.notifyDataSetChanged();
		
		bitrate_spinner_populator();
		bAdapter.notifyDataSetChanged();
	}


	@Override
	public void onNothingSelected(AdapterView<?> arg0) {		
		
	}
	
	private void resetVisiblity(String profile) {
		
		if(profile.equals("Default")){			
			
			refreshAdapters();
			//Disable all Spinners if profile is "Default"
			resolution.setEnabled(false);
			resolution.setClickable(false);
			fps.setEnabled(false);
			fps.setClickable(false);
			bitrate.setEnabled(false);
			bitrate.setClickable(false);
			channel.setEnabled(false);
			channel.setClickable(false);			
		}
		else{
			resolution.setEnabled(true);
			resolution.setClickable(true);
			fps.setEnabled(true);
			fps.setClickable(true);
			bitrate.setEnabled(true);
			bitrate.setClickable(true);
			channel.setEnabled(true);
			channel.setClickable(true);		
		}		
		
	}	
	
}
