package com.welcome.pkg;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.videomixer.R;

public class AudFragment extends ListFragment{
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
	    super.onActivityCreated(savedInstanceState);
	    
	    String[] values = new String[] { "Convert Audio", "Extract Audio from Video"};
	    Integer[] imageID = new Integer[]{ R.drawable.out5, R.drawable.out7};
	    
	    VidAudListAdapter adapter = new VidAudListAdapter(getActivity(),values, imageID);	    	    
	    setListAdapter(adapter);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.temp_list_fragment, container, false);
			
		return rootView;
	}
	
	public AudFragment instantiator(){
		AudFragment f=new AudFragment();
		return f;
	}
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		
		switch(position){	
		case 0:
			Base.isAudConvert=true;
			Intent i0 = new Intent(getActivity(), Gallery_Intiator.class);
			startActivity(i0);
			break;
		case 1:
			Base.isVidEdit=true;
			Intent i1 = new Intent(getActivity(),Gallery_Intiator.class);
			startActivity(i1);
			break;			
		}
		
	}
		
	
}