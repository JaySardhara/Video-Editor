package com.welcome.pkg;

import com.example.videomixer.R;
import com.soundedit.pkg.Editor;
import com.video.pkg.CustomGallery_Pics;
import com.video.pkg.CustomGallery_VidMerge;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

public class VidFragment extends ListFragment{
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
	    super.onActivityCreated(savedInstanceState);
	    
	    String[] values = new String[] { "Merge Videos", "Convert Videos", "Rotate Videos", "Edit Sound in Video", "Create SlideShow from Photos"};
	    Integer[] imageID = new Integer[]{ R.drawable.out2, R.drawable.out4, R.drawable.out3, R.drawable.out6, R.drawable.out9};
	    
	    VidAudListAdapter adapter = new VidAudListAdapter(getActivity(),values, imageID);	    	    
	    setListAdapter(adapter);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.temp_list_fragment, container, false);
			
		return rootView;
	}
	
	public VidFragment instantiator(){
		VidFragment f=new VidFragment();
		return f;
	}
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		
		switch(position){			
		/*case 0:
			Base.isVidEdit=true;
			Intent i0 = new Intent(getActivity(),Gallery_Intiator.class);
			startActivity(i0);
			break;*/
		case 0:
			Intent i0 =new Intent(getActivity(),CustomGallery_VidMerge.class);
			startActivity(i0);
			break;
		case 1:
			Base.isVidConvert=true;
			Intent i1 = new Intent(getActivity(),Gallery_Intiator.class);
			startActivity(i1);
			break;
		case 2:
			Base.isVidRotate=true;
			Intent i2 = new Intent(getActivity(),Gallery_Intiator.class);
			startActivity(i2);
			break;
		case 3:
			Base.isVidAudEdit=true;
			Intent i3 = new Intent(getActivity(),Gallery_Intiator.class);
			startActivity(i3);
			break;
		case 4:
			Base.isSlide=true;
			Base.pass1=true;
			Intent i4 = new Intent(getActivity(),CustomGallery_Pics.class);
			startActivity(i4);
			break;
		default:
			Toast.makeText(getActivity(),"10", Toast.LENGTH_SHORT).show();
			break;
		}
		
	}
	
}
