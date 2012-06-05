package com.thepasto.liquidparts;

import java.io.File;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.CheckBox;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import com.thepasto.liquidparts.BashCommand;

public class A2sdActivity extends Activity{
	

	@Override
    public void onCreate(Bundle savedInstanceState)
    {
                    super.onCreate(savedInstanceState);
                    setContentView(R.layout.a2sd);
                    init();
                    swapInit();
                    setConf();
                    
    }
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void init(){
		String sdext="Not Found";
		sdext = BashCommand.doCmds("su","fdisk -l /dev/block/mmcblk0 | grep \"83 Linux\" | awk '{print $1}'").replaceAll ("\n", "");
		
		SharedPreferences settingsA2sd = getSharedPreferences("lpa2sd", Context.MODE_PRIVATE);
		SharedPreferences.Editor prefEditor = settingsA2sd.edit();
		prefEditor.putString("sdext", sdext);
		String sdahead=readaHead();
		prefEditor.putString("sdahead", sdahead);
		prefEditor.commit();
		
		Spinner spsdah = (Spinner) findViewById(R.id.spinner1);
		ArrayAdapter adapter = ArrayAdapter.createFromResource(
	            this, R.array.sdaheads, android.R.layout.simple_spinner_item);
	    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	    spsdah.setAdapter(adapter);
	    spsdah.setSelection(adapter.getPosition(sdahead));
	    
	    spsdah.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				SharedPreferences settingsA2sd = getSharedPreferences("lpa2sd", Context.MODE_PRIVATE);
				SharedPreferences.Editor prefEditor = settingsA2sd.edit();
				//Log.v("LiquidParts",arg0.getAdapter().getItem(arg2).toString());
				String ahead = arg0.getAdapter().getItem(arg2).toString();
				try {
					BashCommand.doCmds("su","echo "+ahead+" > /sys/block/mmcblk0/bdi/read_ahead_kb");
					prefEditor.putString("sdahead", ahead);
					prefEditor.commit();
					writeChanges();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				
			}

	    });
		
	}
	
	public void swapInit(){
		String sdswap="Not Found";
		String swappi="";
		System.out.println("SWAPINIT");
		sdswap = BashCommand.doCmds("su","fdisk -l /dev/block/mmcblk0 | grep \"82 Linux swap\" | awk '{print $1}'").replaceAll ("\n", "");
		swappi = BashCommand.doCmds("sh","cat /proc/sys/vm/swappiness").replaceAll ("\n", "");
		SharedPreferences settingsA2sd = getSharedPreferences("lpa2sd", Context.MODE_PRIVATE);
		SharedPreferences.Editor prefEditor = settingsA2sd.edit();
		prefEditor.putString("sdswap", sdswap);
		prefEditor.putString("swappiness", swappi);
		prefEditor.commit();
	}
	
	public void readConf(){
		String a2sd = "/sd-ext/.a2sd";
		String ad2sd = "/sd-ext/.ad2sd";
		String dc2sd = "/sd-ext/.dc2sd";
		String xdata = "/sd-ext/.xdata";
		String ext4form = "/sd-ext/.ext4";
		String lowmemset = "/data/local/userinit.d/04lowmem";
		SharedPreferences settingsA2sd = getSharedPreferences("lpa2sd", Context.MODE_PRIVATE);
		SharedPreferences.Editor prefEditor = settingsA2sd.edit();
		prefEditor.putBoolean("a2sd", checkSet(a2sd));
		prefEditor.putBoolean("ad2sd", checkSet(ad2sd));
		prefEditor.putBoolean("dc2sd", checkSet(dc2sd));
		prefEditor.putBoolean("xdata", checkSet(xdata));
		prefEditor.putBoolean("ext4", checkSet(ext4form));
		prefEditor.putBoolean("swapen", swapSet(settingsA2sd.getString("sdswap", "Not Found")));
		prefEditor.putBoolean("lowmem", checkSet(lowmemset));
		prefEditor.commit();
		
	}
	
	public void setConf(){
		readConf();
		SharedPreferences settingsA2sd = getSharedPreferences("lpa2sd", Context.MODE_PRIVATE);
		CheckBox swa2sd = (CheckBox) this.findViewById(R.id.CheckBox1);
		CheckBox swad2sd = (CheckBox) this.findViewById(R.id.CheckBox2);
		CheckBox swdc2sd = (CheckBox) this.findViewById(R.id.CheckBox3);
		CheckBox swxdata = (CheckBox) this.findViewById(R.id.CheckBox4);
		Button butformat = (Button) this.findViewById(R.id.button1);
		CheckBox swswap = (CheckBox) this.findViewById(R.id.CheckBox5);
		EditText etswappi = (EditText) this.findViewById(R.id.EditTextA2);
		CheckBox swlowmem = (CheckBox) this.findViewById(R.id.CheckBox01);
		swa2sd.setChecked(settingsA2sd.getBoolean("a2sd", false));
		swad2sd.setChecked(settingsA2sd.getBoolean("ad2sd", false));
		swdc2sd.setChecked(settingsA2sd.getBoolean("dc2sd", false));
		swxdata.setChecked(settingsA2sd.getBoolean("xdata", false));
		swswap.setChecked(settingsA2sd.getBoolean("swapen", false));
		swlowmem.setChecked(settingsA2sd.getBoolean("lowmem", false));
		etswappi.setText(settingsA2sd.getString("swappiness", "-1"));
		
		if(settingsA2sd.getString("sdswap", "Not Found").equals("Not Found")){
			swswap.setEnabled(false);
		}
		
		if (settingsA2sd.getString("sdext", "Not Found").equals("Not Found")){
			swa2sd.setEnabled(false);
			swad2sd.setEnabled(false);
			swdc2sd.setEnabled(false);
			swxdata.setEnabled(false);
			butformat.setVisibility(View.GONE);
		}
		
		if(swa2sd.isChecked()){
			swad2sd.setEnabled(true);
			swdc2sd.setEnabled(true);
			swxdata.setEnabled(false);
			butformat.setEnabled(false);
		} else {
			swad2sd.setEnabled(false);
			swdc2sd.setEnabled(false);
		}
		if (!settingsA2sd.getBoolean("ext4",false)){
			swa2sd.setEnabled(false);
			swad2sd.setEnabled(false);
			swdc2sd.setEnabled(false);
			swxdata.setEnabled(false);
			butformat.setEnabled(true);
		}else {
			butformat.setVisibility(View.GONE);
			this.findViewById(R.id.ImageView08).setVisibility(View.GONE);

		}
		
		swa2sd.setOnCheckedChangeListener(new OnCheckedChangeListener()
	    {
	        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
	        {
	                	if (isChecked) {
	                		BashCommand.doCmds("su","a2sd install\nn\nn");
	                	} else {
	                		BashCommand.doCmds("su","a2sd remove");
	                	}
	                	rebootConf();
	                	setConf();
	        }
	    });
		
		swad2sd.setOnCheckedChangeListener(new OnCheckedChangeListener()
	    {
	        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
	        {
	        	if (isChecked) {
	        		BashCommand.doCmds("su","a2sd install\nn\ny");
                	} else {
                		BashCommand.doCmds("su","a2sd nodatasd");
                	}
                	rebootConf();
                	setConf();
	        }
	    });
		
		swdc2sd.setOnCheckedChangeListener(new OnCheckedChangeListener()
	    {
	        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
	        {
	        	if (isChecked) {
	        			BashCommand.doCmds("su","a2sd install\ny\nn");
                	} else {
                		BashCommand.doCmds("su","a2sd nocachesd");
                	}
                	rebootConf();
                	setConf();
	        }
	    });
		
		swxdata.setOnCheckedChangeListener(new OnCheckedChangeListener()
	    {
	        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
	        {
	        	if (isChecked) {
	        			BashCommand.doCmds("su","a2sd xdata");
                	} else {
                		BashCommand.doCmds("su","a2sd noxdata");
                	}
                	rebootConf();
                	setConf();
	        }
	    });
		
		swswap.setOnCheckedChangeListener(new OnCheckedChangeListener()
	    {
	        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
	        {
	        	SharedPreferences settingsA2sd = getSharedPreferences("lpa2sd", Context.MODE_PRIVATE);
	        	String sw = settingsA2sd.getString("sdswap", "Not Found");
	        	if (isChecked) {
	        		BashCommand.doCmds("su","swapon "+sw);
                	} else {
                		BashCommand.doCmds("su","swapoff "+sw);
                	}
                	writeChanges();
                	setConf();
	        }
	    });
		
		etswappi.setOnEditorActionListener(new TextView.OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				//Log.v("LiquidParts",Integer.toString(actionId));
				if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT) {
						
						//Toast.makeText(act, v.getText().toString(), 4000);
					Log.v("LiquidParts",v.getText().toString());
	                	int nval = Integer.parseInt(v.getText().toString());
	                	if(nval >= 0 && nval < 101) {
	                		BashCommand.doCmds("su","echo "+nval+" > /proc/sys/vm/swappiness");
	                        writeChanges();
	                        swapInit();
	                        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
	                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
	                	} else {
	                		if (nval > 100){
	                			v.setText("100");
	                		}
	                		
	                		v.requestFocus();
	                	}	                
	                return true;
	            }
	            return false;

			}
	    });
		
		butformat.setOnClickListener(new Button.OnClickListener() {  
        
			@Override
			public void onClick(View v) {
				BashCommand.doCmds("su","a2sd formatext\ny\nn");
				BashCommand.doCmds("su","mount -t ext4 -o rw /dev/block/mmcblk0p2 /sd-ext/ && echo x > /sd-ext/.ext4 && umount /sd-ext/");
				rebootConf();
			
			}
         });
		
		swlowmem.setOnCheckedChangeListener(new OnCheckedChangeListener()
	    {
	        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
	        {
	        	if (isChecked) {
	        		String line = "echo 2048,3072,5120,5632,5888,6400 > /sys/module/lowmemorykiller/parameters/minfree";
	        		
	        		BashCommand.doCmds("su",line+" && echo \"echo 2048,3072,5120,5632,5888,6400 > /sys/module/lowmemorykiller/parameters/minfree\" > /data/local/userinit.d/04lowmem && chmod 755 /data/local/userinit.d/04lowmem");
                	} else {
                		String line = "echo 2048,3072,4096,6144,7168,8192 > /sys/module/lowmemorykiller/parameters/minfree";
                		BashCommand.doCmds("su",line+" && rm /data/local/userinit.d/04lowmem");
                	}
                	writeChanges();
                	setConf();
	        }
	    });
	}
	
	public boolean checkSet(String file){
		File check = new File(file);
		//Log.v("LiquidParts",String.valueOf(check.exists()));
		return check.exists();
	}
	
	public boolean swapSet(String swap){
		String swapen="";
		swapen=BashCommand.doCmds("sh","cat /proc/swaps | grep "+swap);
		if (!swapen.equals("")){
			return true;
		}
		return false;
	}
	
	public String readaHead(){
		String sdahead="";
		sdahead = BashCommand.doCmds("sh","cat /sys/block/mmcblk0/bdi/read_ahead_kb").replaceAll ("\n", "");
		return sdahead;
	}
	
	public void writeChanges(){
		String swcont="";
		SharedPreferences settingsA2sd = getSharedPreferences("lpa2sd", Context.MODE_PRIVATE);
    	String sw = settingsA2sd.getString("sdswap", "Not Found");
    	String sdreadahead = settingsA2sd.getString("sdahead", "1024"); 
		if (((CheckBox)this.findViewById(R.id.CheckBox5)).isChecked()){
			swcont+="swapon ";
		}else {
			swcont+="swapoff ";
		}
		swcont+=sw+" 2> /dev/null\n";
		swcont+="echo "+((EditText) this.findViewById(R.id.EditTextA2)).getText()+ " > /proc/sys/vm/swappiness\n";
		swcont+="echo "+sdreadahead+" > /sys/devices/virtual/bdi/179:0/read_ahead_kb";
		BashCommand.doCmds("su","echo \""+swcont+"\" > /data/local/userinit.d/02LPswap && chmod 755 /data/local/userinit.d/02LPswap");
		
	}
	
	public void rebootConf(){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Reboot Now?")
		       .setCancelable(false)
		       .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		                A2sdActivity.this.finish();
		                BashCommand.doCmds("su","reboot");
		           }
		       })
		       .setNegativeButton("No", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		                dialog.cancel();
		           }
		       });
		AlertDialog alert = builder.create();
		alert.show();
	}
}
