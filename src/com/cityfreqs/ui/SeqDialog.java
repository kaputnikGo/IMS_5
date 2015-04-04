package com.cityfreqs.ui;

import org.puredata.core.PdBase;

import com.cityfreqs.ims5.R;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.RadioGroup;

public class SeqDialog extends Dialog implements OnClickListener {
	private Button dismiss;
	
	private RadioGroup noteGroup1;
	private int note1;
	private RadioGroup noteGroup2;
	private int note2;
	private RadioGroup noteGroup3;
	private int note3;
	private RadioGroup noteGroup4;
	private int note4;
	private RadioGroup noteGroup5;
	private int note5;
	private RadioGroup noteGroup6;
	private int note6;
	private RadioGroup noteGroup7;
	private int note7;
	private RadioGroup noteGroup8;
	private int note8;	
	
	
    public SeqDialog(Context context) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setGravity(Gravity.TOP | Gravity.LEFT);
        setContentView(R.layout.sequencer);
        
        dismiss = (Button)findViewById(R.id.dismiss);


	    dismiss.setOnClickListener(new View.OnClickListener() {
	    	public void onClick(View v) {
	    		// capture notes here
	    		noteCapture();
	    		dismiss();
	    		
	    	}
	    });
    }
    
	private void noteCapture() {
		// note sequencer groups
		noteGroup1 = (RadioGroup) findViewById(R.id.noteGroup1);
		// null pointer here
		noteGroup1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
		      public void onCheckedChanged (RadioGroup group, int checkedId) {
		    	  note1 = group.indexOfChild(group.findViewById(checkedId));
		          noteSelector(1, note1);
		      }
		});		
		//noteSelector(1, note1);
		noteGroup2 = (RadioGroup) findViewById(R.id.noteGroup2);
		noteGroup2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
		      public void onCheckedChanged (RadioGroup group, int checkedId) {
		    	  note2 = group.indexOfChild(group.findViewById(checkedId));
		          noteSelector(2, note2);
		      }
		});
		noteGroup3 = (RadioGroup) findViewById(R.id.noteGroup3);
		noteGroup3.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
		      public void onCheckedChanged (RadioGroup group, int checkedId) {
		    	  note3 = group.indexOfChild(group.findViewById(checkedId));
		          noteSelector(3, note3);
		      }
		});
		noteGroup4 = (RadioGroup) findViewById(R.id.noteGroup4);
		noteGroup4.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
		      public void onCheckedChanged (RadioGroup group, int checkedId) {
		    	  note4 = group.indexOfChild(group.findViewById(checkedId));
		          noteSelector(4, note4);
		      }
		});
		noteGroup5 = (RadioGroup) findViewById(R.id.noteGroup5);
		noteGroup5.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
		      public void onCheckedChanged (RadioGroup group, int checkedId) {
		    	  note5 = group.indexOfChild(group.findViewById(checkedId));
		          noteSelector(5, note5);
		      }
		});
		noteGroup6 = (RadioGroup) findViewById(R.id.noteGroup6);
		noteGroup6.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
		      public void onCheckedChanged (RadioGroup group, int checkedId) {
		    	  note6 = group.indexOfChild(group.findViewById(checkedId));
		          noteSelector(6, note6);
		      }
		});
		noteGroup7 = (RadioGroup) findViewById(R.id.noteGroup7);
		noteGroup7.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
		      public void onCheckedChanged (RadioGroup group, int checkedId) {
		    	  note7 = group.indexOfChild(group.findViewById(checkedId));
		          noteSelector(7, note7);
		      }
		});
		noteGroup8 = (RadioGroup) findViewById(R.id.noteGroup8);
		noteGroup8.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
		      public void onCheckedChanged (RadioGroup group, int checkedId) {
		    	  note8 = group.indexOfChild(group.findViewById(checkedId));
		          noteSelector(8, note8);
		      }
		});		
	}

	public void onClick(View v) {
		// capture notes here
		noteCapture();
		dismiss();		
	}
	
	// get note checkboxes values and send to pd patch
	private void noteSelector(int group, int note) {
		PdBase.sendFloat("u" + group, note);
	}
}