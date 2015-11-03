package com.welcome.pkg;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Adapter to handle the ViewPager by returning Fragments
 * 
 * @author Jay Sardhara
 *
 */


public class TabsPagerAdapter extends FragmentPagerAdapter{

	public TabsPagerAdapter(FragmentManager fm) {
		super(fm);		
	}

	@Override
	public Fragment getItem(int index) {
		
		switch (index) {
		case 0:
			return (new VidFragment()).instantiator() ;
		case 1:
			return (new AudFragment()).instantiator();
		}
		
		return null;
	}

	@Override
	public int getCount() {		
		return 2;
	}

}
