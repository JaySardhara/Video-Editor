package com.video.pkg;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class custom_borders_vidFrame extends View{

	private Paint mBorderLinePaint;
	private float mOffset;
    private float mSelectionStart;
    private float mSelectionEnd;
    private float mHeight;
	
	public custom_borders_vidFrame(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		mBorderLinePaint = new Paint();
        mBorderLinePaint.setAntiAlias(true);
        mBorderLinePaint.setStrokeWidth(4f);        
        mBorderLinePaint.setColor(Color.argb(0xFF, 0x33, 0xB5, 0xE5)); //Ice-cream sandwich blue aka match scrubbers
		
        //Take default height as 65
        mHeight = 65;        
	}
	
	public custom_borders_vidFrame(Context context, AttributeSet attrs, Float height) {
		super(context, attrs);
		
		mBorderLinePaint = new Paint();
        mBorderLinePaint.setAntiAlias(true);
        mBorderLinePaint.setStrokeWidth(4f);        
        mBorderLinePaint.setColor(Color.argb(0xFF, 0x33, 0xB5, 0xE5)); //Ice-cream sandwich blue aka match scrubbers
        
        mHeight = height;		
	}

	public void setParameters(float start, float end, float offset) {
        mSelectionStart = start;
        mSelectionEnd = end;
        mOffset = offset;
    }
	
	
	@Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        
     // Draw borders
        canvas.drawLine(
            mSelectionStart, 0,
            mSelectionStart , mHeight,
            mBorderLinePaint);Log.w("Borders",String.valueOf(getMeasuredHeight())); //Height is matched as per thmbnail height
        canvas.drawLine(
            mSelectionEnd, 0,
            mSelectionEnd, mHeight ,
            mBorderLinePaint);
        
        
	}
	
	
}
