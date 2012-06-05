package com.thepasto.liquidparts;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class AdvancedActivity extends Activity {
	
	@Override
    public void onCreate(Bundle savedInstanceState)
    {
                    super.onCreate(savedInstanceState);
                    setContentView(R.layout.advanced);
                    setConf();
    }
	
	public void readConf(){
		SharedPreferences settingsAdvanced = getSharedPreferences("lpadvanced", Context.MODE_PRIVATE);
		Switch swsvs = (Switch) this.findViewById(R.id.switchAd1);
		swsvs.setChecked(settingsAdvanced.getBoolean("svs", false));
		TableLayout table = (TableLayout) findViewById(R.id.TableLayout01);
		final CheckBox cbsob = (CheckBox) findViewById(R.id.checkBox1);
		cbsob.setChecked(settingsAdvanced.getBoolean("sob", false));
		ImageView imgsep = (ImageView) this.findViewById(R.id.ImageView03);
		ImageView imgsep2 = (ImageView) this.findViewById(R.id.ImageView01);
		ImageView imgsep3 = (ImageView) this.findViewById(R.id.ImageView04);
		Button butapp = (Button) this.findViewById(R.id.button1);
		Button butss = (Button) this.findViewById(R.id.button2);
		Button butres = (Button) this.findViewById(R.id.button3);
		
		if (swsvs.isChecked()){
			vddPicker(getFreqs(),getVdds());
			table.setVisibility(View.VISIBLE);
			cbsob.setVisibility(View.VISIBLE);
			imgsep.setVisibility(View.VISIBLE);
			imgsep2.setVisibility(View.VISIBLE);
			imgsep3.setVisibility(View.VISIBLE);
			butapp.setVisibility(View.VISIBLE);
			butss.setVisibility(View.VISIBLE);
			butres.setVisibility(View.VISIBLE);
		} else {
			table.setVisibility(View.GONE);
			cbsob.setVisibility(View.GONE);
			imgsep.setVisibility(View.GONE);
			imgsep2.setVisibility(View.GONE);
			imgsep3.setVisibility(View.GONE);
			butapp.setVisibility(View.GONE);
			butss.setVisibility(View.GONE);
			butres.setVisibility(View.GONE);
		}
		
		butapp.setOnClickListener(new Button.OnClickListener() {  
	        
			@Override
			public void onClick(View v) {
				String vddtable=parsePref();
				SharedPreferences settingsAdvanced = getSharedPreferences("lpadvanced", Context.MODE_PRIVATE);
				SharedPreferences.Editor prefEditor = settingsAdvanced.edit();
				BashCommand.doCmds("su","rm /data/local/userinit.d/03customvdds");
				if (cbsob.isChecked()){
					prefEditor.putBoolean("sob", true);
					BashCommand.doCmds("su","echo \""+vddtable+"\" > /data/local/userinit.d/03customvdds && chmod 755 /data/local/userinit.d/03customvdds");
				} else {
					prefEditor.putBoolean("sob", false);
				}
				prefEditor.commit();
				BashCommand.doCmds("su",vddtable);
			}
         });
		
		butss.setOnClickListener(new Button.OnClickListener() {  
	        
			@Override
			public void onClick(View v) {
				String[] suggestedVdd = {"900","925","975","1025","1200","1250","1300","1300","1300","1325","1425","1475","1525","1600","1600"};			
				vddPicker(getFreqs(),suggestedVdd);
			}
         });
		
		butres.setOnClickListener(new Button.OnClickListener() {  
	        
			@Override
			public void onClick(View v) {
				String[] defaultVdd = {"1000","1000","1075","1025","1150","1250","1300","1300","1300","1300","1300","1300","1300","1300","1300"};			
				vddPicker(getFreqs(),defaultVdd);
			}
         });
	}

	public void setConf(){
		readConf();
		Switch swsvs = (Switch) this.findViewById(R.id.switchAd1);
		swsvs.setOnCheckedChangeListener(new OnCheckedChangeListener()
	    {
	        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
	        {
	        	SharedPreferences settingsAdvanced = getSharedPreferences("lpadvanced", Context.MODE_PRIVATE);
	    		SharedPreferences.Editor prefEditor = settingsAdvanced.edit();
	    		prefEditor.putBoolean("svs",isChecked);
	    		prefEditor.commit();
	    		readConf();
	    		if(!isChecked){
	    			cleanUp();
	    			
	    		}
	        }
	    });
	}
	
	public String[] getFreqs(){
		String freqs="";
		freqs=BashCommand.doCmds("sh","cat /sys/devices/system/cpu/cpu0/cpufreq/vdd_levels | awk -F': ' '{print $1}' | xargs echo -ne");
		
		return(freqs.replace("\n","").split(" "));
	}
	
	public String[] getVdds(){
		String vdds="";
			vdds=BashCommand.doCmds("sh","cat /sys/devices/system/cpu/cpu0/cpufreq/vdd_levels | awk -F': ' '{print $2}' | xargs echo -ne");
		return(vdds.replace("\n","").split(" "));
	}
	
	public void vddPicker(String[] freq, String[] vdd) {
		Log.i("LIQUIDPARTS", String.valueOf(freq.length));
		Log.i("LIQUIDPARTS", String.valueOf(vdd.length));
		
		String[] defaultVdd = {"1000","1000","1075","1025","1150","1250","1300","1300","1300","1300","1300","1300","1300","1300","1300"};
		
		SharedPreferences settingsAdvanced = getSharedPreferences("lpadvanced", Context.MODE_PRIVATE);
		SharedPreferences.Editor prefEditor = settingsAdvanced.edit();
		TableLayout table = (TableLayout) findViewById(R.id.TableLayout01);
		table.removeAllViews();
		
		for (int i=0; i<freq.length; i++){
			final TableRow row = new TableRow(this);
			final TextView cfreq = new TextView(this);
			final TextView cvdd = new TextView(this);
			final Button upvdd = new Button(this);
			final Button downvdd = new Button(this);
			
			prefEditor.putString(freq[i], vdd[i]);
			prefEditor.commit();
			cfreq.setText(freq[i]);
			cvdd.setText(settingsAdvanced.getString(freq[i], defaultVdd[i]));
			upvdd.setText("+");
			downvdd.setText("-");
			
			row.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,TableRow.LayoutParams.WRAP_CONTENT));
			row.addView(cfreq);
			row.addView(downvdd);
			row.addView(cvdd);
			row.addView(upvdd);
			table.addView(row);
			
			upvdd.setOnClickListener(new Button.OnClickListener() {  
		        
				@Override
				public void onClick(View v) {
				try {
					SharedPreferences settingsAdvanced = getSharedPreferences("lpadvanced", Context.MODE_PRIVATE);
					SharedPreferences.Editor prefEditor = settingsAdvanced.edit();
					int fval=Integer.valueOf(cvdd.getText().toString());
					if(fval<1600){
						cvdd.setText(String.valueOf(fval+25));
						prefEditor.putString(cfreq.getText().toString(),cvdd.getText().toString());
						prefEditor.commit();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				}
	         });
			
			downvdd.setOnClickListener(new Button.OnClickListener() {  
		        
				@Override
				public void onClick(View v) {
				try {
					SharedPreferences settingsAdvanced = getSharedPreferences("lpadvanced", Context.MODE_PRIVATE);
					SharedPreferences.Editor prefEditor = settingsAdvanced.edit();
					int fval=Integer.valueOf(cvdd.getText().toString());
					if(fval>800) {
						cvdd.setText(String.valueOf(fval-25));
						prefEditor.putString(cfreq.getText().toString(),cvdd.getText().toString());
						prefEditor.commit();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				}
	         });
		}		
	}
	
	public String parsePref(){
		String fcont ="";
		SharedPreferences settingsAdvanced = getSharedPreferences("lpadvanced", Context.MODE_PRIVATE);
		String [] freqs = getFreqs();
		String [] vdds = getVdds();
		for (int i=0; i<freqs.length; i++){
			fcont+="echo '"+freqs[i]+" ";
			fcont+=settingsAdvanced.getString(freqs[i], vdds[i])+"' > /sys/devices/system/cpu/cpu0/cpufreq/vdd_levels\n";
		}
		Log.i("LIQUIDPARTS", fcont);
		return fcont;
	}
	
	public void cleanUp(){
		SharedPreferences settingsAdvanced = getSharedPreferences("lpadvanced", Context.MODE_PRIVATE);
		SharedPreferences.Editor prefEditor = settingsAdvanced.edit();
		String[] defaultVdd = {"1000","1000","1075","1025","1150","1250","1300","1300","1300","1300","1300","1300","1300","1300","1300"};
		String[] freqs = getFreqs();
		String fcont="";
		for (int i=0; i<freqs.length; i++){
			fcont+="echo '"+freqs[i]+" ";
			fcont+=defaultVdd[i]+"' > /sys/devices/system/cpu/cpu0/cpufreq/vdd_levels\n";
			prefEditor.putString(freqs[i], defaultVdd[i]);
		}
		prefEditor.putBoolean("sob", false);
		prefEditor.commit();
		BashCommand.doCmds("su","rm /data/local/userinit.d/03customvdds");
		BashCommand.doCmds("su",fcont);
		
	}
}

