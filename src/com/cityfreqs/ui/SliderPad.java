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

public class SliderPad extends View implements OnTouchListener {
	
	// init vars
	private Paint crossPaint = new Paint();	
	private float xer;
	private float yer;
	private float size;
	private float maxL;
	private float minL;
	private float factor;
	private float scale;
	// var name to send pdbase
	private String toX;
	
	public SliderPad(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setOnTouchListener(this);
	}

	public SliderPad(Context context, AttributeSet attrs) {
		super(context, attrs);
		setOnTouchListener(this);
	}

	public SliderPad(Context context, String toX) {
	    super(context);
	    setOnTouchListener(this);
	    factor = MainActivity.getDPI(1.0f);
	    scale = MainActivity.getScale();
	    size = 9 * factor;
	    maxL = 90 * factor;
	    minL = 10 * factor;
	    xer = minL;
	    yer = minL;
	    this.toX = toX;
	}
    
	public boolean onTouch(View v, MotionEvent event) {
		int action = event.getActionMasked();

		switch (action) {
		case MotionEvent.ACTION_DOWN:
			//
		case MotionEvent.ACTION_MOVE:
			//
		case MotionEvent.ACTION_UP:
			xer = event.getX();
	        getSliderTouch(xer);
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
		
		canvas.drawCircle(xer, yer, size, crossPaint);
	}
	
	// get touch inputs and send to pd patch
	private void getSliderTouch(float xIn) {
		// refactor for pixel to pd value range
		xIn = xIn / scale;
		// disregard out of range values & no Y
		if (xIn < 0) xIn = 0.0f;
		if (xIn > 100) xIn = 100.0f;			

		// send values to patch	
		// 0 -1 (0-100)
		PdBase.sendFloat(toX, xIn);
	}
	
	// value to UI method
	public int getXer() {
		return (int)xer;
	}
	
}