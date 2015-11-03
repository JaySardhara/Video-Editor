package com.welcome.pkg;

import com.example.videomixer.R;
import com.video.pkg.vid_arranger;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

public class Base extends ActionBarActivity implements ActionBar.TabListener {

	private ViewPager viewPager;
	private TabsPagerAdapter mAdapter;
	private ActionBar actionBar;
	// Tab titles
	private String[] tabs = { "Video", "Audio" };
	//Flags for firing activities from gallery_initiator class 
	public static Boolean isVidConvert=false, isVidRotate=false, isAudConvert=false, isVidEdit=false, isVidAudEdit=false, isSlide=false;
	//Flags for firing activities after Ffmpeg service completion from BaseWizard_Modified class
	public static Boolean startEditor=false, mAddAud=false, startVSEditor=false, slideAudio=false, pass1=true;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.base_view);
		
		// Initilization
		viewPager = (ViewPager) findViewById(R.id.pager);
		actionBar = getSupportActionBar();
		actionBar.show();
		mAdapter = new TabsPagerAdapter(getSupportFragmentManager());
		
		viewPager.setAdapter(mAdapter);
		actionBar.setHomeButtonEnabled(false);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		
		//add tabs
		for(String tab_name:tabs){
			actionBar.addTab(actionBar.newTab().setText(tab_name).setTabListener(this));
		}
		
		/**
		 * on swiping the viewpager make respective tab selected
		 * */
		viewPager.setOnPageChangeListener(new OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int position) {
				// on changing the page
				// make respected tab selected
				actionBar.setSelectedNavigationItem(position);
				
			}
			
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {				
				
			}
			
			@Override
			public void onPageScrollStateChanged(int arg0) {			
			}
		});
		
		
	}
	

	@Override
	public void onTabReselected(android.support.v7.app.ActionBar.Tab arg0,
			android.support.v4.app.FragmentTransaction arg1) {		
		
	}

	@Override
	public void onTabSelected(android.support.v7.app.ActionBar.Tab tab,
			android.support.v4.app.FragmentTransaction arg1) {
		
		// on tab selected
		// show respected fragment view
		viewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(android.support.v7.app.ActionBar.Tab arg0,
			android.support.v4.app.FragmentTransaction arg1) {		
	}

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
		
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.actionbar_overflow_actions, menu);
		
		return super.onCreateOptionsMenu(menu);
	}
		
	public boolean onOptionsItemSelected(MenuItem item) {

		AlertDialog.Builder about = new AlertDialog.Builder(Base.this);
		about.setTitle("About");
		
		LayoutInflater inflater = (LayoutInflater)(Base.this).getSystemService(LAYOUT_INFLATER_SERVICE);
    	View layout = inflater.inflate(R.layout.about, null);
		
		about.setView(layout);
		about.show();
		
		return super.onOptionsItemSelected(item);		
	}
}
