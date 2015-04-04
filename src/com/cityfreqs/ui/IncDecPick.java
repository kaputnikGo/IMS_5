package com.cityfreqs.ui;

import com.cityfreqs.ims5.R;

import android.content.Context;
import android.graphics.Color;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class IncDecPick extends LinearLayout {
	// thanks to http://technologichron.net/?p=42
	
	private final int MIN = 0;
	private final int MAX = 9;
	// text size for gui
	private final int SIZE = 16;
	private final int DIMEN = 36;
	
	public Integer val;
	
	// change button to own xml
	Button dec;
	Button inc;
	public TextView valText;
	
	public IncDecPick(Context ctx, AttributeSet attSet) {
		super(ctx, attSet);
		
		this.setLayoutParams(new LinearLayout.LayoutParams
				(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
	
		LayoutParams params = new LinearLayout.LayoutParams(DIMEN, DIMEN);

		initIncButton(ctx);
		initDecButton(ctx);
		initValText(ctx);
		
		// vertical alignment
		addView(inc, params);
		addView(valText, params);
		addView(dec, params);
	}
	
	private void initIncButton(Context ctx) {
		inc = new Button(ctx);
		inc.setTextSize(SIZE);
		inc.setText("+");
		inc.setBackgroundResource(R.drawable.def_button);
		// get single touch
		inc.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	inc();
            }
        });
	}
	
	private void initDecButton(Context ctx){
		dec = new Button(ctx);
		dec.setTextSize(SIZE);
		dec.setText("-");
		dec.setBackgroundResource(R.drawable.def_button);
		// get single touch
		dec.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	dec();
            }
        });
	}
	
	private void initValText(Context ctx) {
		val = Integer.valueOf(0);
		
		valText = new TextView(ctx);
		valText.setTextSize(SIZE);
		valText.setTextColor(Color.parseColor("#000000"));

		valText.setOnKeyListener(new View.OnKeyListener() {
			public boolean onKey(View v, int arg1, KeyEvent event) {
				int candidate = val;
				try {
					// parse text value to int value
					val = Integer.parseInt(((TextView)v).getText().toString());
				} catch(NumberFormatException nfe){
					// if fail to parse, use this
					val = candidate;
				}
				return false;
			}
		});
		
		valText.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
		valText.setText(val.toString());
		valText.setInputType(InputType.TYPE_CLASS_NUMBER);
	}
	
	public void inc() {
		if(val < MAX) {
			val = val + 1;
			valText.setText(val.toString());
		}
	}

	public void dec() {
		if(val > MIN) {
			val = val - 1;
			valText.setText(val.toString());
		}
	}
	
	public int getVal() {
		return val;
	}
	
	public void setValue(int val) {
		if(val > MAX) val = MAX;
		if(val >= 0) {
			this.val = val;
			valText.setText(this.val.toString());
		}
	}
	
}
