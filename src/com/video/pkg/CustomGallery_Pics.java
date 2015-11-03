package com.video.pkg;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.StringTokenizer;

import com.example.videomixer.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory.Options;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

public class CustomGallery_Pics extends Activity{

	private int count;
	private Bitmap[] thumbnails;
	private boolean[] thumbnailsselection;
	private String[] arrPath;
	private ImageAdapter imageAdapter;
	String selectImages = "";
	ImageView temp;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_custom_gallery);
		
		final String[] columns = { MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID };
		final String orderBy = MediaStore.Images.Media._ID;
		Cursor imagecursor = getContentResolver().query(
				MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null,
				null, orderBy);
		int image_column_index = imagecursor.getColumnIndex(MediaStore.Images.Media._ID);
		this.count = imagecursor.getCount();
		this.thumbnails = new Bitmap[this.count];
		this.arrPath = new String[this.count];
		this.thumbnailsselection = new boolean[this.count];
		for (int i = 0; i < this.count; i++) {
			imagecursor.moveToPosition(i);
			int id = imagecursor.getInt(image_column_index);
			int dataColumnIndex = imagecursor.getColumnIndex(MediaStore.Images.Media.DATA);
			thumbnails[i] = MediaStore.Images.Thumbnails.getThumbnail(
					getApplicationContext().getContentResolver(), id,
					MediaStore.Images.Thumbnails.MICRO_KIND, null);
			arrPath[i]= imagecursor.getString(dataColumnIndex);
		}
		GridView imagegrid = (GridView) findViewById(R.id.PhoneImageGrid);
		imageAdapter = new ImageAdapter();
		imagegrid.setAdapter(imageAdapter);
		//imagecursor.close();
		
		final Button selectBtn = (Button) findViewById(R.id.selectBtn);
		selectBtn.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				
				final int len = thumbnailsselection.length;
				int cnt = 0;
				
				for (int i =0; i<len; i++)
				{
					if (thumbnailsselection[i]){
						cnt++;
						selectImages = selectImages + arrPath[i] + "|";
					}
				}
				if (cnt < 2 ){
					Toast.makeText(getApplicationContext(),	"Please select atleat 2 pics..!!", Toast.LENGTH_LONG).show();
				} 
				else{
					Log.d("SelectedImages", selectImages);
					Toast.makeText(getApplicationContext(), "You selected "+cnt+" images", Toast.LENGTH_LONG).show();
					
					//Handle rotation of pics automatically
					picsRotater();
					
					Intent i=new Intent(getBaseContext(),Custom_Draggable_Gridview.class);
					i.putExtra("picsCnt", cnt);
					startActivity(i);		
					finish();
				}
			}
		});
	}

	protected void picsRotater() {
		
		StringTokenizer splitter = new StringTokenizer(selectImages, "|");	
		int i=0;
		String []pic_pths=new String[splitter.countTokens()];
		while (splitter.hasMoreTokens()) {
			pic_pths[i++] = splitter.nextToken();
		 }
		
		
		for(int j=0; j<i; j++){
			try {
				ExifInterface exif = new ExifInterface(pic_pths[j]);
				String orntn = exif.getAttribute(ExifInterface.TAG_ORIENTATION);
				//Log.w("cnt",String.valueOf(j));
				
				if(orntn.equals("6")){
					//Log.w("orntn",orntn);
					
					Bitmap cache = decodeSampledBitmapFromResource(pic_pths[j], 200, 200);					
					File mkCache = new File("/mnt/sdcard/imgCache");
					mkCache.mkdirs();
					
					Matrix matrix = new Matrix();
					matrix.postRotate(90);
					Bitmap roBitmap = Bitmap.createBitmap( cache, 0, 0, cache.getWidth(), cache.getHeight(), matrix, true);
					roBitmap.compress(CompressFormat.JPEG, 95, new FileOutputStream(new File(mkCache,j+".jpg")));
					roBitmap.recycle();
					cache.recycle();
					
				}
				else if(orntn.equals("3")){
					//Log.w("orntn",orntn);
					
					Bitmap cache = decodeSampledBitmapFromResource(pic_pths[j], 200, 200);					
					File mkCache = new File("/mnt/sdcard/imgCache");
					mkCache.mkdirs();
					
					Matrix matrix = new Matrix();
					matrix.postRotate(180);
					Bitmap roBitmap = Bitmap.createBitmap( cache, 0, 0, cache.getWidth(), cache.getHeight(), matrix, true);
					roBitmap.compress(CompressFormat.JPEG, 95, new FileOutputStream(new File(mkCache,j+".jpg")));
					roBitmap.recycle();
					cache.recycle();
				}
				else if(orntn.equals("8")){
					//Log.w("orntn",orntn);
					
					Bitmap cache = decodeSampledBitmapFromResource(pic_pths[j], 200, 200);					
					File mkCache = new File("/mnt/sdcard/imgCache");
					mkCache.mkdirs();
					
					Matrix matrix = new Matrix();
					matrix.postRotate(-90);
					Bitmap roBitmap = Bitmap.createBitmap( cache, 0, 0, cache.getWidth(), cache.getHeight(), matrix, true);
					roBitmap.compress(CompressFormat.JPEG, 95, new FileOutputStream(new File(mkCache,j+".jpg")));
					roBitmap.recycle();
					cache.recycle();
					
				}
				else if(orntn.equals("0")){
					
					/**This is android bug where EXIF tags are not properly stored/read
					 * I did not handle this bug and just kept same image 
					 * Edit and handle here 
					 */
					Bitmap cache = decodeSampledBitmapFromResource(pic_pths[j], 200, 200);					
					File mkCache = new File("/mnt/sdcard/imgCache");
					mkCache.mkdirs();
					
					//Matrix matrix = new Matrix();
					//matrix.postRotate(-90);
					Bitmap roBitmap = Bitmap.createBitmap( cache);
					roBitmap.compress(CompressFormat.JPEG, 95, new FileOutputStream(new File(mkCache,j+".jpg")));
					roBitmap.recycle();
					cache.recycle();
					
					//Uri uri = Uri.fromFile(new File(pic_pths[j]));
					//int masd = getCameraPhotoOrientation(getBaseContext(), uri, pic_pths[j]);
					//Log.w("!!!",String.valueOf(masd));
					/*String[] orntnColmn = {MediaStore.Images.ImageColumns.ORIENTATION};
					String uri = Uri.fromFile(new File(pic_pths[j])).toString();Log.w("uri",uri);
					Cursor getorntn = getBaseContext().getContentResolver().query(Uri.parse(uri),orntnColmn , null, null, null);
					int orientation = -1;
					//int count = getorntn.getCount();
	                for (int z=0; i<count; i++) {
	                	getorntn.moveToPosition(z);
	                    orientation = getorntn.getInt(getorntn.getColumnIndex(orntnColmn[0]));
	                }  
	                Log.w("!@!",String.valueOf(orientation)+" "+String.valueOf(count));
					
	                getContentResolver().n*/
	                
					/*
	                Bitmap cache = decodeSampledBitmapFromResource(pic_pths[j], 200, 200);					
					File mkCache = new File("/mnt/sdcard/imgCache");
					mkCache.mkdirs();
					
					//Matrix matrix = new Matrix();
					//matrix.postRotate(-90);
					Bitmap roBitmap = Bitmap.createBitmap( cache);
					roBitmap.compress(CompressFormat.JPEG, 95, new FileOutputStream(new File(mkCache,j+".jpg")));
					roBitmap.recycle();
					cache.recycle();*/
				}
				else{
					Bitmap cache = decodeSampledBitmapFromResource(pic_pths[j], 200, 200);					
					File mkCache = new File("/mnt/sdcard/imgCache");
					mkCache.mkdirs();
					
					//Matrix matrix = new Matrix();
					//matrix.postRotate(-90);
					Bitmap roBitmap = Bitmap.createBitmap( cache);
					roBitmap.compress(CompressFormat.JPEG, 95, new FileOutputStream(new File(mkCache,j+".jpg")));
					roBitmap.recycle();
					cache.recycle();
				}
				
				
			} catch (Exception e) {			
				e.printStackTrace();
			}
		}
		
	}

	public class ImageAdapter extends BaseAdapter {
		private LayoutInflater mInflater;

		public ImageAdapter() {
			mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		public int getCount() {
			return count;
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = mInflater.inflate(
						R.layout.galleryitem, null);
				holder.imageview = (ImageView) convertView.findViewById(R.id.thumbImage);
				holder.checkbox = (CheckBox) convertView.findViewById(R.id.itemCheckBox);
				
				convertView.setTag(holder);
			}
			else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.checkbox.setId(position);
			holder.imageview.setId(position);
			holder.checkbox.setOnClickListener(new OnClickListener() {
				
				public void onClick(View v) {
					
					CheckBox cb = (CheckBox) v;
					int id = cb.getId();
					if (thumbnailsselection[id]){
						cb.setChecked(false);
						thumbnailsselection[id] = false;
					} else {
						cb.setChecked(true);
						thumbnailsselection[id] = true;
					}
				}
			});
			/*holder.imageview.setOnClickListener(new OnClickListener() {
				
				public void onClick(View v) {
					
					int id = v.getId();
					Intent intent = new Intent();
					intent.setAction(Intent.ACTION_VIEW);
					intent.setDataAndType(Uri.parse("file://" + arrPath[id]), "image/*");
					startActivity(intent);
				}
			});*/
			holder.imageview.setImageBitmap(thumbnails[position]);
			holder.checkbox.setChecked(thumbnailsselection[position]);
			holder.id = position;
			return convertView;
		}
	}
	class ViewHolder {
		ImageView imageview;
		CheckBox checkbox;
		int id;
	}
	
	
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
	
	
	
}
