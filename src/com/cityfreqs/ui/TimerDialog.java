package com.cityfreqs.ui;

import org.puredata.core.PdBase;

import com.cityfreqs.ims5.R;

import android.app.Dialog;
import android.content.Context;
import android.view.View.OnClickListener;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;


public class TimerDialog extends Dialog implements OnClickListener {
	private Button dismiss;
	
	public TimerDialog(Context context) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setGravity(Gravity.TOP | Gravity.LEFT);
        setContentView(R.layout.timer);
        
        dismiss = (Button) findViewById(R.id.dismissTimer);


	    dismiss.setOnClickListener(new View.OnClickListener() {
	    	public void onClick(View v) {
	    		delPick();
	    		spdPick();
	    		dismiss();	    		
	    	}
	    });
    }
	
	private void spdPick() {
		// speed value pickers
        IncDecPick spd1 = (IncDecPick) findViewById(R.id.spdPick1);
		int currSpd1 = spd1.getVal();
		
		IncDecPick spd2 = (IncDecPick) findViewById(R.id.spdPick2);
		int currSpd2 = spd2.getVal();
		
		IncDecPick spd3 = (IncDecPick) findViewById(R.id.spdPick3);
		int currSpd3 = spd3.getVal();
		
		int speedy = (currSpd1 * 100) + (currSpd2 * 10) + currSpd3;
		
		// 0 - 2000 max
		PdBase.sendFloat("speed", speedy * 1.0f);
	}
	private void delPick() {
		// delay value pickers
        IncDecPick del1 = (IncDecPick) findViewById(R.id.delPick1);
		int currDel1 = del1.getVal();
		
		IncDecPick del2 = (IncDecPick) findViewById(R.id.delPick2);
		int currDel2 = del2.getVal();
		
		IncDecPick del3 = (IncDecPick) findViewById(R.id.delPick3);
		int currDel3 = del3.getVal();
		
		int delly = (currDel1 * 100) + (currDel2 * 10) + currDel3;
		// 0 - 2000 max
		PdBase.sendFloat("delay_add", delly * 1.0f);
	}

	public void onClick(View v) {
		delPick();
		spdPick();
		dismiss();		
	}
}