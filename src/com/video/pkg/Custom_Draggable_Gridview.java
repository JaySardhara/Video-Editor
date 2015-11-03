package com.video.pkg;

import java.io.File;
import com.animoto.android.views.DraggableGridView;
import com.animoto.android.views.OnRearrangeListener;
import com.example.videomixer.R;
import com.welcome.pkg.Base;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.BitmapFactory.Options;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.NumberPicker.OnValueChangeListener;

/**Class for showing draggable gridview
 * 
 * Library "DraggableGridView" by "thquinn" used
 * 
 * @author Jay Sardhara
 *
 */

public class Custom_Draggable_Gridview extends ActionBarActivity{

	ActionBar actionbar;
	DraggableGridView dgv;
	String[] pic_pths;
	Button nxt;
	ImageView rotate;
	int slideDur, picsCount;
	String resSelected, opName;
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.draggable_gridview);
		
		dgv=(DraggableGridView)findViewById(R.id.dgv);
		nxt=(Button)findViewById(R.id.nxt);
		
		actionbar=getSupportActionBar();
		actionbar.setHomeButtonEnabled(true);
		actionbar.setDisplayHomeAsUpEnabled(true);
		actionbar.setTitle("Create SlideShow");
		actionbar.setIcon(R.drawable.out9);
		actionbar.show();
		
		rotate=new ImageView(this);
		
		picsCount = getIntent().getExtras().getInt("picsCnt");
		//StringTokenizer splitter = new StringTokenizer(getter, "|");
		
		
		/*int i=0;
		pic_pths=new String[splitter.countTokens()];
		while (splitter.hasMoreTokens()) {
			pic_pths[i++] = splitter.nextToken();
		 }*/
		
		for(int j=0;j<picsCount;j++){			
			ImageView temp = new ImageView(this);			
			temp.setImageBitmap(decodeSampledBitmapFromResource("/mnt/sdcard/imgCache/"+j+".jpg", 100, 100));
			dgv.addView(temp);				
		}		
		
		dgv.setOnRearrangeListener(new OnRearrangeListener() {
			
			@Override
			public void onRearrange(int original_Pos, int new_Pos) {
				
				Log.w("critpt",String.valueOf(original_Pos)+" "+String.valueOf(new_Pos));
				
				//Swap Image paths on rearrangement
				
				File rname = new File("/mnt/sdcard/imgCache/"+original_Pos+".jpg");
				rname.renameTo(new File("/mnt/sdcard/imgCache/"+new_Pos+"!"+".jpg"));
				rname.delete();
				
				rname = new File("/mnt/sdcard/imgCache/"+new_Pos+".jpg");
				rname.renameTo(new File(("/mnt/sdcard/imgCache/"+original_Pos+".jpg")));
				rname.delete();
				
				rname = new File("/mnt/sdcard/imgCache/"+new_Pos+"!"+".jpg");
				rname.renameTo(new File("/mnt/sdcard/imgCache/"+new_Pos+".jpg"));
				
			}
		});
		
		/*dgv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				Log.w("indx",String.valueOf(position)+" "+pic_pths[position]);				
											
				try{
					rotate.setImageBitmap(decodeSampledBitmapFromResource(pic_pths[position], 50, 50));
					//rotate.setScaleType(ScaleType.MATRIX);
					//Options o = getSize(pic_pths[0]);
					//Matrix m = new Matrix();
					//m.postRotate(90, rotate.getDrawable().getBounds().centerX(), rotate.getDrawable().getBounds().centerY());					
					//rotate.setImageMatrix(m);
					rotate.setRotation(90);
					/*rotate.setDrawingCacheEnabled(true);
					rotate.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED), 
				            MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
					rotate.layout(0, 0, rotate.getMeasuredWidth(), rotate.getMeasuredHeight()); 
					rotate.buildDrawingCache(true);
					Bitmap b = rotate.getDrawingCache();
					b.compress(CompressFormat.JPEG, 95, new FileOutputStream(new File("/sdcard/test.jpg")));
					
					dgv.removeViewAt(position);					
					dgv.addView(rotate);
					
					//Have to swap paths after Removal
					
				}
				catch(Exception e){
					e.printStackTrace();
				}
				
			}
		});*/
				
		
		nxt.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				
				AlertDialog.Builder alertDialog = new AlertDialog.Builder(Custom_Draggable_Gridview.this);
				 		       
		        alertDialog.setTitle("Add Audio to Slides ?");		 
		        
		        alertDialog.setMessage("Do you want to add Audio or Merge pics directly ? ");		 
		       
		        alertDialog.setIcon(R.drawable.out13);		 
		        
		        alertDialog.setPositiveButton("Add Audio", new DialogInterface.OnClickListener() {
		            public void onClick(DialogInterface dialog,int which) {		 
		            	Base.slideAudio=true;
		            	Base.isSlide=true;
		            	slideDuration();		            	
		            	
		            }
		        });		 
		        
		        alertDialog.setNegativeButton("Merge Directly", new DialogInterface.OnClickListener() {
		            public void onClick(DialogInterface dialog, int which) {		            
		            	slideDuration();	            	
		            		
		            }
		        });		 
		       
		        alertDialog.show();				
			}
		});
	}
	
	
	@Override
	public void onBackPressed() {
		
		//Clean ingCache on release
        File cleaner=new File("/sdcard/imgCache");
        if (cleaner.isDirectory()) {
            String[] children = cleaner.list();
            for (int i = 0; i < children.length; i++) {
                new File(cleaner, children[i]).delete();
            }
        }	
		
		Intent homeIntent = new Intent(this, Base.class);
	    homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	    startActivity(homeIntent);
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
			lncher.putExtra("picCnt", picsCount); Log.w("chk!!",String.valueOf(picsCount)+" "+String.valueOf(slideDur));
			lncher.putExtra("duration", slideDur*picsCount);
			lncher.putExtra("SDuration", slideDur);
			startActivity(lncher);			
        }  
	}
	
	public void slideDuration(){
		
		final Dialog d = new Dialog(Custom_Draggable_Gridview.this);
        d.setTitle("Duration Per Slide");
        d.setContentView(R.layout.numbrpicker);
        Button b1 = (Button) d.findViewById(R.id.np_ok);
        Button b2 = (Button) d.findViewById(R.id.np_cancel);
        final NumberPicker np = (NumberPicker) d.findViewById(R.id.np);
        np.setMaxValue(100);
        np.setMinValue(1);
        np.setWrapSelectorWheel(false);
        np.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        np.setOnValueChangedListener(new OnValueChangeListener() {
			
			@Override
			public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
				slideDur = newVal;
				
			}
		});
		b1.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Log.w("dur",String.valueOf(slideDur));
				
				if(!Base.slideAudio){
					
					AlertDialog.Builder save = new AlertDialog.Builder(Custom_Draggable_Gridview.this);
					save.setTitle("Save");
					save.setMessage("Save Video As:");
					
					final EditText fname = new EditText(getBaseContext());
					fname.setBackgroundColor(Color.GRAY);
					fname.setTextColor(Color.BLACK);
					fname.setCursorVisible(true);
					save.setView(fname);
					
					save.setPositiveButton("Save", new DialogInterface.OnClickListener() {					
						
						public void onClick(DialogInterface dialog, int which) {
							
							opName=fname.getText().toString().trim();							
							
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
				else{
					Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
	            	startActivityForResult(i,1);
				}
				
			}
		});
        b2.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				d.dismiss();
				
			}
		});
		
        d.show();
	}
	
	public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
    // Raw height and width of image
    final int height = options.outHeight;
    final int width = options.outWidth;
    int inSampleSize = 1;

    if (height > reqHeight || width > reqWidth) {

        final int halfHeight = height / 2;
        final int halfWidth = width / 2;

        // Calculate the largest inSampleSize value that is a power of 2 and keeps both
        // height and width larger than the requested height and width.
        while ((halfHeight / inSampleSize) > reqHeight
                && (halfWidth / inSampleSize) > reqWidth) {
            inSampleSize *= 2;
        }
    }

    return inSampleSize;
	}
	
	/**Scale and Insample Bitmap to reduce memory while decoding
	 * 
	 * More details at {@link}http://developer.android.com/training/displaying-bitmaps/load-bitmap.html
	 * Thanks to android.developer for their code
	 * 
	 * @param path
	 * @param reqWidth
	 * @param reqHeight
	 * @return Scaled Bitmap
	 */
	public static Bitmap decodeSampledBitmapFromResource(String path, int reqWidth, int reqHeight) {

	    // First decode with inJustDecodeBounds=true to check dimensions
	    final BitmapFactory.Options options = new BitmapFactory.Options();
	    options.inJustDecodeBounds = true;
	    BitmapFactory.decodeFile(path, options);

	    // Calculate inSampleSize
	    options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

	    // Decode bitmap with inSampleSize set
	    options.inJustDecodeBounds = false;
	    return BitmapFactory.decodeFile(path, options);
	}
	
	private Options getSize(String path){
		
		BitmapFactory.Options o = new BitmapFactory.Options();
		o.inJustDecodeBounds=true;
		BitmapFactory.decodeFile(path, o);
		
		return o;
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
	
	private void createAlertSpinner(){
		AlertDialog.Builder builder;
    	final AlertDialog alertDialog;
    	builder=new AlertDialog.Builder(Custom_Draggable_Gridview.this);
    	
    	builder.setTitle("Select Resolution");
    	builder.setIcon(R.drawable.out13);
    	//builder.setMessage("Resolution you want..");
    	   	
    	builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {		 
            	
            	//alertDialog.dismiss();
              	Intent lncher=new Intent(Custom_Draggable_Gridview.this,Slide_Engine.class);		    			
    			lncher.putExtra("picCnt", picsCount);			    			
    			lncher.putExtra("res", resSelected);
    			lncher.putExtra("SDuration", slideDur);
    			lncher.putExtra("Oputname", opName);
    			startActivity(lncher);									
				finish();   	
            }
        });		
    	
    	
    	LayoutInflater inflater = (LayoutInflater)(Custom_Draggable_Gridview.this).getSystemService(LAYOUT_INFLATER_SERVICE);
    	View layout = inflater.inflate(R.layout.vidmerge_spinner_res, null);
    	
    	String res_holder[] = new String[3];		            	
    	res_holder[0] = "176x144";
    	res_holder[1] = "480x360";
    	res_holder[2] = "640x420";
    	
    	Spinner sp = (Spinner)layout.findViewById(R.id.resSpinner);
    	ArrayAdapter<String> adpter = new ArrayAdapter<String>(Custom_Draggable_Gridview.this,android.R.layout.simple_spinner_item,res_holder);
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
