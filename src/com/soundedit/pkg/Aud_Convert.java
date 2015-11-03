package com.soundedit.pkg;

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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.EditText;
import android.widget.TextView;

public class Aud_Convert extends ActionBarActivity implements OnItemSelectedListener{
	
	String aud_pth;
	Spinner bitrate,format,rate;
	List<String> format_holder=new ArrayList<String>();
	List<String> bitrate_holder=new ArrayList<String>();
	List<String> rate_holder=new ArrayList<String>();
	ArrayAdapter<String> fAdapter,bAdapter,rAdapter;
	private int _prematureFiring=0;
	private String _curFormat="";
	String cBitrate, cRate;
	Button cnvrt;
	EditText saveAs;
	Boolean isBitrateDefault=true,isSampleDefault=true;

	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.aud_convert);
		
		this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN); 
		
		aud_pth=getIntent().getExtras().getString("pth");		
				
		TextView sngName = (TextView)findViewById(R.id.songName);
		sngName.setText(stringPuller(aud_pth));
		
		cnvrt = (Button)findViewById(R.id.cnvrtAud);
		
		//Set background for some UI elements
		saveAs = (EditText)findViewById(R.id.editText1);
		saveAs.setBackgroundColor(Color.WHITE);
		saveAs.setTextColor(Color.BLACK);
		saveAs.setImeOptions(EditorInfo.IME_ACTION_DONE);
		saveAs.setSingleLine();
		
		bitrate=(Spinner)findViewById(R.id.audBitrateSpinner);
		format=(Spinner)findViewById(R.id.audFormatSpinner);
		rate=(Spinner)findViewById(R.id.audSamplerSpinner);
		
		format_spinner_populator();		
		fAdapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, format_holder);
		fAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);		
		format.setAdapter(fAdapter);
		format.setOnItemSelectedListener(this);			
		
		bitrate_spinner_populator();		
		bAdapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, bitrate_holder);
		bAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);		
		bitrate.setAdapter(bAdapter);
		bitrate.setOnItemSelectedListener(this);			
		
		rate_spinner_populator();		
		rAdapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, rate_holder);
		rAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);		
		rate.setAdapter(rAdapter);
		rate.setOnItemSelectedListener(this);
		
		cnvrt.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				
				Intent i = new Intent(Aud_Convert.this,AudConvert_Engine.class);
				i.putExtra("audpath",aud_pth);
				i.putExtra("origFormat","mp3");
				i.putExtra("convertTo",_curFormat);
				i.putExtra("saveTo", saveAs.getText().toString());
				i.putExtra("rate",cRate);
				i.putExtra("bitrate",cBitrate);				
				i.putExtra("bRate",isSampleDefault);
				i.putExtra("bBitrate",isBitrateDefault);
				startActivity(i);
				finish();
			}
		});
		
		
	}
	
	
	
	private String stringPuller(String path) {
		String onlyName=""; 
		int i=1;
		while(!path.substring(path.lastIndexOf(".") - i).startsWith("/")){
			onlyName = path.substring(path.lastIndexOf(".") - i);
			i++;			
		}
		return onlyName;
	}



	private void rate_spinner_populator() {
		
		rate_holder.clear();
		if(_curFormat.equals("amr/3gp"))
			rate_holder.add("8000 Hz");
		else{
			rate_holder.add("Keep Current");
			rate_holder.add("22050 Hz");
			rate_holder.add("44100 Hz");
			rate_holder.add("48000 Hz");
		}
	}



	private void bitrate_spinner_populator() {
		
		bitrate_holder.clear();
		if(_curFormat.equals("amr/3gp")){
			bitrate_holder.add("12 kbps");
		}
		else{
			bitrate_holder.add("Keep Current");
			bitrate_holder.add("128 kbps");
			bitrate_holder.add("256 kbps");
			bitrate_holder.add("320 kbps");
		}		
	}

	private void format_spinner_populator() {
		
		format_holder.clear();
		
		format_holder.add("mp3");
		format_holder.add("m4a");
		format_holder.add("aac");
		format_holder.add("amr/3gp");		
		format_holder.add("wmv");
		format_holder.add("wav");		
	}



	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		
		if(_prematureFiring < 3)
			_prematureFiring++;
		
		else{
			if(parent.getId() == R.id.audFormatSpinner){
				_curFormat = parent.getItemAtPosition(position).toString();				
				refreshAdapters();
			}
			else if(parent.getId() == R.id.audBitrateSpinner){
				
				if(parent.getItemAtPosition(position).toString().equals(""))
					isBitrateDefault=true;
				else
					isBitrateDefault=false;
					
				cBitrate = parent.getItemAtPosition(position).toString();				
			}
			else if(parent.getId() == R.id.audSamplerSpinner){
				
				if(parent.getItemAtPosition(position).toString().equals(""))
					isSampleDefault=true;
				else
					isSampleDefault=false;
				
				cRate = parent.getItemAtPosition(position).toString();				
			}
		}
	}

	private void refreshAdapters() {
		
		bitrate_spinner_populator();
		bAdapter.notifyDataSetChanged();
		
		rate_spinner_populator();
		rAdapter.notifyDataSetChanged();		
	}



	@Override
	public void onNothingSelected(AdapterView<?> arg0) {		
		
	}

}
