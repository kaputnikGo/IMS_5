package com.cityfreqs.ui;

import org.puredata.core.PdBase;

import com.cityfreqs.ims5.MainActivity;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public class TouchPad extends View implements OnTouchListener {
	// Abstract class to be inherited by pads
	// init vars
	private Paint crossPaint = new Paint();	
	private float xer;
	private float yer;
	private float size;
	private float maxL;
	private float minL;
	private float factor;
	private float scale;
	// var names to send pdbase
	private String toX, toY;
	
	public TouchPad(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setOnTouchListener(this);
	}

	public TouchPad(Context context, AttributeSet attrs) {
		super(context, attrs);
		setOnTouchListener(this);
	}

	public TouchPad(Context context, String toX, String toY) {
	    super(context);
	    setOnTouchListener(this);
	    factor = MainActivity.getDPI(1.0f);
	    scale = MainActivity.getScale();
	    size = 3 * factor;
	    maxL = 100 * factor;
	    minL = 4 * factor;
	    xer = 50 * factor;
	    yer = xer;
	    this.toX = toX;
	    this.toY = toY;	    
	}
    
	public boolean onTouch(View v, MotionEvent event) {
		int action = event.getActionMasked();

		switch (action) {
		case MotionEvent.ACTION_DOWN:
			// bypass
		case MotionEvent.ACTION_MOVE:
	        // bypass
		case MotionEvent.ACTION_UP:
			// getX returns PIXELS....
	        xer = event.getX();
	        yer = event.getY();
	        getPadTouch(xer, yer);
	        invalidate();
			break;
		case MotionEvent.ACTION_CANCEL:
		      break;
		default:
		      break;
		}
		return true;
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		// limit drawcircle
		if (xer < minL) xer = minL;
		else if (xer > maxL) xer = maxL;
		if (yer <minL) yer = minL;
		else if (yer > maxL) yer = maxL;
		
		canvas.drawCircle(xer, yer, size, crossPaint);		
	}

	private void getPadTouch(float xIn, float yIn) {
		// refactor for pixel to pd value range
		xIn = xIn / scale;
		yIn = yIn / scale;
		// disregard out of range values	
		if (xIn < 0) xIn = 0.0f;
		if (xIn > 100) xIn = 100.0f;			
		if (yIn < 5) yIn = 0.0f;
		if (yIn > 100) yIn = 100.0f;
		
		// send values to patch, only 0-100
		PdBase.sendFloat(toX, xIn);
		PdBase.sendFloat(toY, yIn);
	}
	
	// value to UI methods
	public int getXer() {
		return (int)xer;
	}
	
	public int getYer() {
		return (int)yer;
	}
}
