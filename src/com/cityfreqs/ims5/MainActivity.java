package com.cityfreqs.ims5;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.puredata.android.service.PdService;
import org.puredata.core.PdBase;
import org.puredata.android.utils.PdUiDispatcher;
import org.puredata.core.utils.IoUtils;

import com.cityfreqs.ui.SeqDialog;
import com.cityfreqs.ui.TimerDialog;
import com.cityfreqs.ui.TouchPad;
import com.cityfreqs.ui.SliderPad;
import com.cityfreqs.ui.PDpatcher;

import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Resources;

public class MainActivity extends Activity implements OnClickListener {
	private static float factorDpi;
	private DisplayMetrics metrics;
	private static float scale;
	
	// vars for pd
	private PdService pdService = null;	
	private PdUiDispatcher dispatcher;
	//private static final String TAG = "IMS5";
	
	// vars for layouts
	private int FACIAWIDE;
	private int FACIAHIGH;
	private int FACIACOL = 0xFFA00032;
	private int GROUPCOL = 0xFF666666;
	private int GROUPWIDE;
	private int GROUPHIGH;
	private int SMALLSIZE;
	private int ROWSIZE;
	private int OCTSIZE;
	private int PADSIZE;
	
	private RelativeLayout titleLayout;	
	private RelativeLayout octVolLayout;
	private RadioGroup octGroup;
	private int oct1;
	private RelativeLayout envLayout;
	private RelativeLayout touchPads;
	private RelativeLayout footLayout;
	private TextView footer;

	private RelativeLayout seqTmrLayout;
	private SeqDialog seqDialog;
	private Button seqButton;
	private TimerDialog tmrDialog;
	private Button tmrButton;
	
	// interface classes
	private TouchPad spfltPad;
	private TouchPad rsrnPad;
	private TouchPad fdpnPad;
	private TouchPad phloPad;
	private SliderPad volSlider;
	private SliderPad atkSlider;
	private SliderPad dcySlider;
	//private TextView spfltTop;
	private PDpatcher PDpatcher;
	
	private final ServiceConnection pdConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName name, IBinder service) {
			pdService = ((PdService.PdBinder)service).getService();
			initPd();
		}

		public void onServiceDisconnected(ComponentName name) {
			// this method will never be called
		}
	};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); 
        
        // call pd service here
     	bindService(new Intent(this, PdService.class), pdConnection, BIND_AUTO_CREATE);
     	// init PD vars
     	PDpatcher = new PDpatcher();

     	//DISPLAY ROUTINE 
        // init display size factoring
        metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		factorDpi = metrics.densityDpi;
		
		scale = getApplicationContext().getResources().getDisplayMetrics().density;
		// refactor layout values
		refactorSizes();
     	
        // init main view layout parent
        requestWindowFeature(Window.FEATURE_NO_TITLE);        
        RelativeLayout main = new RelativeLayout(this);
        main.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        
        // init facia layout container
        RelativeLayout facia = new RelativeLayout(this);
        facia.setBackgroundColor(FACIACOL);
        RelativeLayout.LayoutParams faciaParams = new RelativeLayout.LayoutParams(FACIAWIDE, FACIAHIGH);
        faciaParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        facia.setLayoutParams(faciaParams);
        
        // init the layout groups
        initTitleBar();
        initSeqTmrLayout();
        initOctVolLayout();
        initEnvLayout();
        initTouchPads();
        initFooter();
        
        // finally set layouts within facia
        facia.addView(titleLayout);
        facia.addView(seqTmrLayout);
        facia.addView(octVolLayout);
        facia.addView(envLayout);
        facia.addView(touchPads);
        facia.addView(footLayout);
        
        // add facia to main and assign as contentView for activity
        main.addView(facia);
        setContentView(main);
    }
    
    @Override
	protected void onDestroy() {
		super.onDestroy();
		cleanup();
		PdBase.release();
	}
    
// ******************
//
// puredata functions
//
// ******************    	
    private void initPd() {
    	Resources res = getResources();
    	File patchFile = null;
    	try {
    		// receiver for patch displays
    		dispatcher = new PdUiDispatcher();
    		PdBase.setReceiver(dispatcher);

    		// set res/raw pd patch as input
    		InputStream in = res.openRawResource(R.raw.fifth);
    		patchFile = IoUtils.extractResource(in, "fifth.pd", getCacheDir());		
    		PdBase.openPatch(patchFile);
    		startAudio();
    	} catch (IOException e) {
    		// was a log
    		finish();
    	} finally {
    		if (patchFile != null) patchFile.delete();
    	}
    }
    	
    private void startAudio() {
    	String name = getResources().getString(R.string.app_name);
    	try {
    		// determine settings (default -1) of samprate, ch in, ch out, buffer size
    		// int samRate = AudioParameters.suggestSampleRate();
    		pdService.initAudio(-1, 0, 2, -1);
    			
    		// start audio
    		pdService.startAudio(new Intent(this, MainActivity.class), R.drawable.icon, name, "Return to " + name + ".");
    	} catch (IOException e) {
    		// watch the wall....
    	}
    }

    private void cleanup() {
    	try {
    		unbindService(pdConnection);
    	} catch (IllegalArgumentException e) {
    		// already unbound
    		pdService = null;
    	}
    }	

    
// ******************
//
// interface functions
//
// ******************
    // move layout functions to separate class...
    private void initTitleBar() {
    	LayoutInflater inflaterTit = getLayoutInflater();
    	titleLayout = new RelativeLayout(this);
    	titleLayout.setId(2);
    	titleLayout.setBackgroundColor(GROUPCOL);
    	titleLayout = (RelativeLayout) inflaterTit.inflate(R.layout.title_top, titleLayout, false);
        RelativeLayout.LayoutParams titleLayoutParams = new RelativeLayout.LayoutParams(GROUPWIDE, SMALLSIZE);
        titleLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        titleLayoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        titleLayout.setLayoutParams(titleLayoutParams);
    }
    
    private void initSeqTmrLayout() {
    	//ENVELOPE LAYOUT
    	LayoutInflater inflaterStl = getLayoutInflater();
        seqTmrLayout = new RelativeLayout(this);
        seqTmrLayout.setId(3);
        seqTmrLayout.setBackgroundColor(GROUPCOL);
        seqTmrLayout = (RelativeLayout) inflaterStl.inflate(R.layout.seq_tmr_buttons, seqTmrLayout, false);        
        RelativeLayout.LayoutParams seqTmrParams = new RelativeLayout.LayoutParams(GROUPWIDE, SMALLSIZE);
        seqTmrParams.addRule(RelativeLayout.BELOW, titleLayout.getId());
        seqTmrParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        seqTmrLayout.setLayoutParams(seqTmrParams);
        
        	
     	seqButton = (Button) seqTmrLayout.findViewById(R.id.seqOpen);
     	tmrButton = (Button) seqTmrLayout.findViewById(R.id.timerOpen);
     	tmrDialog = new TimerDialog(this);
     	seqDialog = new SeqDialog(this);
     	
     	seqButton.setOnClickListener(new View.OnClickListener() {
     		public void onClick(View v) {
                 seqDialog.show();
            }
        });
     	
     	tmrButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	tmrDialog.show();
            }
        });
    }
    
    private void initOctVolLayout() {
    	//OCT VOL LAYOUT
        octVolLayout = new RelativeLayout(this);
        octVolLayout.setId(4);
        octVolLayout.setBackgroundColor(GROUPCOL);
        RelativeLayout.LayoutParams octVolLayoutParams = new RelativeLayout.LayoutParams(GROUPWIDE, SMALLSIZE);
        octVolLayoutParams.addRule(RelativeLayout.BELOW, seqTmrLayout.getId());
        octVolLayoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        octVolLayout.setLayoutParams(octVolLayoutParams);
        
        // OCT CONTROL
        LayoutInflater inflaterOct = getLayoutInflater();
        RelativeLayout octLayout = new RelativeLayout(this);
        octLayout.setBackgroundColor(GROUPCOL);
        octLayout = (RelativeLayout) inflaterOct.inflate(R.layout.oct_top, octLayout, false);        
        RelativeLayout.LayoutParams octParams = new RelativeLayout.LayoutParams(OCTSIZE, SMALLSIZE);
        octParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        octLayout.setLayoutParams(octParams);
        
        // octave radio group
     	octGroup = (RadioGroup) octLayout.findViewById(R.id.octGroup);
     	octGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
     		public void onCheckedChanged (RadioGroup group, int checkedId) {
     			oct1 = group.indexOfChild(group.findViewById(checkedId));
     		    // account for octave range
     			oct1 = oct1 + 2;
     			octSelector(oct1);
     		}
     	});
        
        // VOL CONTROL
        LayoutInflater inflaterVol = getLayoutInflater();
        RelativeLayout volLayout = new RelativeLayout(this);
        volLayout.setBackgroundColor(GROUPCOL);
        volLayout = (RelativeLayout) inflaterVol.inflate(R.layout.vol_top, volLayout, false);        
        RelativeLayout.LayoutParams volParams = new RelativeLayout.LayoutParams(ROWSIZE, SMALLSIZE);
        volParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        volLayout.setLayoutParams(volParams);        
        
        // sliderpad class
        volSlider = new SliderPad(this, PDpatcher.VOL);
        volLayout.addView(volSlider);
        
        // add views to layout
        octVolLayout.addView(octLayout);
        octVolLayout.addView(volLayout);
        
    }
    
    private void initEnvLayout() {
    	//ENVELOPE LAYOUT
        envLayout = new RelativeLayout(this);
        envLayout.setId(5);
        envLayout.setBackgroundColor(GROUPCOL);
        RelativeLayout.LayoutParams envLayoutParams = new RelativeLayout.LayoutParams(GROUPWIDE, SMALLSIZE);
        envLayoutParams.addRule(RelativeLayout.BELOW, octVolLayout.getId());
        envLayoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        envLayout.setLayoutParams(envLayoutParams);
        
        // atk slider layout       
        LayoutInflater inflaterAtk = getLayoutInflater();
        RelativeLayout atkLayout = new RelativeLayout(this);
        atkLayout.setBackgroundColor(GROUPCOL);
        atkLayout = (RelativeLayout) inflaterAtk.inflate(R.layout.env_left_top, atkLayout, false);        
        RelativeLayout.LayoutParams atkParams = new RelativeLayout.LayoutParams(ROWSIZE, SMALLSIZE);
        atkParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        atkLayout.setLayoutParams(atkParams);        
        
        // sliderpad class
        atkSlider = new SliderPad(this, PDpatcher.ATK);
        atkLayout.addView(atkSlider);
        
        // dcy slider layout       
        LayoutInflater inflaterDcy = getLayoutInflater();
        RelativeLayout dcyLayout = new RelativeLayout(this);
        dcyLayout.setBackgroundColor(GROUPCOL);
        dcyLayout = (RelativeLayout) inflaterDcy.inflate(R.layout.env_right_top, dcyLayout, false);        
        RelativeLayout.LayoutParams dcyParams = new RelativeLayout.LayoutParams(ROWSIZE, SMALLSIZE);
        dcyParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        dcyLayout.setLayoutParams(dcyParams);        
        
        // sliderpad class
        dcySlider = new SliderPad(this, PDpatcher.DCY);
        dcyLayout.addView(dcySlider);
        
        envLayout.addView(atkLayout);
        envLayout.addView(dcyLayout);
    }
    
    private void initTouchPads() {
    	//TOUCHPAD LAYOUT
        touchPads = new RelativeLayout(this);
        touchPads.setId(6);
        touchPads.setBackgroundColor(GROUPCOL);
        RelativeLayout.LayoutParams touchPadsParams = new RelativeLayout.LayoutParams(GROUPWIDE, GROUPHIGH);
        touchPadsParams.addRule(RelativeLayout.BELOW, envLayout.getId());
        touchPadsParams.addRule(RelativeLayout.CENTER_HORIZONTAL); 
        touchPads.setLayoutParams(touchPadsParams);
        
        // top row layout
        RelativeLayout topFilters = new RelativeLayout(this);
        RelativeLayout.LayoutParams topFiltersParams = new RelativeLayout.LayoutParams(GROUPHIGH, ROWSIZE);
        topFiltersParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        topFiltersParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        topFilters.setLayoutParams(topFiltersParams);
      
        // top left filter layout       
        LayoutInflater inflaterLeftT = getLayoutInflater();
        RelativeLayout fLeftT = new RelativeLayout(this);
        fLeftT = (RelativeLayout) inflaterLeftT.inflate(R.layout.fltr_left_top, fLeftT, false);        
        RelativeLayout.LayoutParams fLeftParamsT = new RelativeLayout.LayoutParams(PADSIZE, PADSIZE);
        fLeftParamsT.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        fLeftT.setLayoutParams(fLeftParamsT);        

        //  touchpad class
        spfltPad = new TouchPad(this, PDpatcher.SHI, PDpatcher.LIM);
        fLeftT.addView(spfltPad);
        
        // get pad values to textview
        //spfltTop = (TextView) fLeftT.findViewById(R.id.s_hi_text);
        //spfltTop.setText(String.valueOf(spfltPad.getXer()));
        
        // top right filter layout
        LayoutInflater inflaterRightT = getLayoutInflater();
        RelativeLayout fRightT = new RelativeLayout(this);
        fRightT = (RelativeLayout) inflaterRightT.inflate(R.layout.fltr_right_top, fRightT, false);
        RelativeLayout.LayoutParams fRightParamsT = new RelativeLayout.LayoutParams(PADSIZE, PADSIZE);
        fRightParamsT.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        fRightT.setLayoutParams(fRightParamsT);        
        
        // touchpad class
        rsrnPad = new TouchPad(this, PDpatcher.RSN, PDpatcher.RNG);
        fRightT.addView(rsrnPad);
        
        // btm row layout
        RelativeLayout btmFilters = new RelativeLayout(this);
        RelativeLayout.LayoutParams btmFiltersParams = new RelativeLayout.LayoutParams(GROUPHIGH, ROWSIZE);
        btmFiltersParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        btmFiltersParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        btmFilters.setLayoutParams(btmFiltersParams);
        
        // btm left filter layout       
        LayoutInflater inflaterLeftB = getLayoutInflater();
        RelativeLayout fLeftB = new RelativeLayout(this);
        fLeftB = (RelativeLayout) inflaterLeftB.inflate(R.layout.fltr_left_btm, fLeftB, false);        
        RelativeLayout.LayoutParams fLeftParamsB = new RelativeLayout.LayoutParams(PADSIZE, PADSIZE);
        fLeftParamsB.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        fLeftB.setLayoutParams(fLeftParamsB);       
        
        // touchpad class 
        fdpnPad = new TouchPad(this, PDpatcher.PAN, PDpatcher.FBK);
        fLeftB.addView(fdpnPad);
        
        // btm right filter layout       
        LayoutInflater inflaterRightB = getLayoutInflater();
        RelativeLayout fRightB = new RelativeLayout(this);
        fRightB = (RelativeLayout) inflaterRightB.inflate(R.layout.fltr_right_btm, fRightB, false);        
        RelativeLayout.LayoutParams fRightParamsB = new RelativeLayout.LayoutParams(PADSIZE, PADSIZE);
        fRightParamsB.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        fRightB.setLayoutParams(fRightParamsB);      
        
        // touchpad class
        phloPad = new TouchPad(this, PDpatcher.PHS, PDpatcher.PLO);
        fRightB.addView(phloPad);
        
        // add top touchpads to layout row
        topFilters.addView(fLeftT);
        topFilters.addView(fRightT);
        // add btm touchpads to layout row
        btmFilters.addView(fLeftB);
        btmFilters.addView(fRightB);
        
        // add rows to layouts to touchpad
        touchPads.addView(topFilters);
        touchPads.addView(btmFilters);
    }
    
    // get octave checkboxes values and send to pd patch
 	private void octSelector(int octIn) {
 		// octaves 2 - 7
 		PdBase.sendFloat(PDpatcher.OCT, octIn * 1.0f);
 	}
 	
 	private void initFooter() {
 		LayoutInflater inflaterFoot = getLayoutInflater();
    	footLayout = new RelativeLayout(this);
    	footLayout.setId(7);
    	footLayout.setBackgroundColor(GROUPCOL);
    	footLayout = (RelativeLayout) inflaterFoot.inflate(R.layout.footer, titleLayout, false);
        RelativeLayout.LayoutParams footLayoutParams = new RelativeLayout.LayoutParams(GROUPWIDE, SMALLSIZE);
        footLayoutParams.addRule(RelativeLayout.BELOW, touchPads.getId());
        footLayoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        footLayout.setLayoutParams(footLayoutParams);
        
        // footer listener
     	footer = (TextView) footLayout.findViewById(R.id.footText);
        footer.setOnClickListener(this);     
 	}
 	
 	public void onClick(View target) {
    	if (target == footer) {
    		openWebsite();
    	}
    }
 	
 	public static float getDPI(float size) { 
 	    return (size * factorDpi) / DisplayMetrics.DENSITY_DEFAULT;   
 	 }
 	
 	public static float getScale() {
 		return scale;
 	}
 	
 	public void refactorSizes() {
 		// vars for facia layout
 		FACIAWIDE = (int)getDPI(320);
 		FACIAHIGH = (int)getDPI(600);
 		// vars for layout groups
 	 	GROUPWIDE = (int)getDPI(300);
 	 	GROUPHIGH = (int)getDPI(260);
 	 	SMALLSIZE = (int)getDPI(60);
 	 	ROWSIZE = (int)getDPI(140);
 		OCTSIZE = (int)getDPI(130);				
 		PADSIZE = (int)getDPI(120); 		

 	}
 	
 	private void openWebsite() {
		Intent siteIntent = new Intent();
		siteIntent.setAction(Intent.ACTION_VIEW);
		siteIntent.addCategory(Intent.CATEGORY_BROWSABLE);
		siteIntent.setData(Uri.parse("http://www.cityfreqs.com.au"));
		startActivity(siteIntent);
	}
}
