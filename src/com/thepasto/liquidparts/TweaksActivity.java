package com.thepasto.liquidparts;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.content.Context;
import android.content.SharedPreferences;
import com.thepasto.liquidparts.BashCommand;


public class TweaksActivity extends Activity{

	  @Override
      public void onCreate(Bundle savedInstanceState) {
		  
		  try {
			System.out.println(checkFS());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
          super.onCreate(savedInstanceState);
          setContentView(R.layout.tweaks);
          setPrefs();
      }

	  public void readConfig() {
		  //TOUCH preferences
		  SharedPreferences settingsTweaks = getSharedPreferences("lpts", Context.MODE_PRIVATE);
		  SharedPreferences.Editor prefEditor = settingsTweaks.edit();
		  int sens=settingsTweaks.getInt("sensitivity", -1);
		  int nois=settingsTweaks.getInt("noise", -1);
		  if (sens == -1){
			  prefEditor.putInt("sensitivity", 75);   
		  }
		  if (nois == -1){
			  prefEditor.putInt("noise", 75); 
		  }
		  //SOFTKEYS preferences
		  try {
			String hf=readFileValue("/sys/module/avr/parameters/vibr");
			if (hf.equals("1")){
				prefEditor.putBoolean("haptic", true);
			} else {
				prefEditor.putBoolean("haptic", false);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		  int skvib=settingsTweaks.getInt("vibdel", -1);
		  int sksens=settingsTweaks.getInt("threshold", -1);
		  if (skvib == -1){
			  prefEditor.putInt("vibdel", 30);   
		  }
		  if (sksens == -1){
			  prefEditor.putInt("threshold", 18); 
		  }
		  prefEditor.commit();
	  }
	  
	  public void setPrefs(){
		  readConfig();
		  SharedPreferences settingsTweaks = getSharedPreferences("lpts", Context.MODE_PRIVATE);
		  EditText sensText=(EditText) this.findViewById(R.id.editText1);
		  EditText noisText=(EditText) this.findViewById(R.id.EditText01);
		  sensText.setText(Integer.toString(settingsTweaks.getInt("sensitivity", -1)));
		  noisText.setText(Integer.toString(settingsTweaks.getInt("noise", -1)));
		  Switch haptick =  (Switch) this.findViewById(R.id.switch01);
		  haptick.setChecked(settingsTweaks.getBoolean("haptic", false));
		  EditText vibText=(EditText) this.findViewById(R.id.EditText02);
		  EditText thresText=(EditText) this.findViewById(R.id.EditText03);
		  vibText.setText(Integer.toString(settingsTweaks.getInt("vibdel", -1)));
		  thresText.setText(Integer.toString(settingsTweaks.getInt("threshold", -1)));
		  final Activity act=this;
		  sensText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

				@Override
				public boolean onEditorAction(TextView v, int actionId,
						KeyEvent event) {
					//Log.v("LiquidParts",Integer.toString(actionId));
					if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT) {
							
							//Toast.makeText(act, v.getText().toString(), 4000);
						Log.v("LiquidParts",v.getText().toString());
		                try {
		                	int nval = Integer.parseInt(v.getText().toString());
		                	if(nval > 24 && nval < 76) {
		                		writeChanges();
		                		act.findViewById(R.id.EditText01).requestFocus();
		                	} else {
		                		if (nval > 75){
		                			v.setText("75");
		                		}
		                		if (nval < 25) {
		                			v.setText("25");
		                		}
		                		v.requestFocus();
		                	}
						} catch (Exception e) {
							e.printStackTrace();
						}
		                
		                return true;
		            }
		            return false;

				}
		    });
		  
		  noisText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

				@Override
				public boolean onEditorAction(TextView v, int actionId,
						KeyEvent event) {
					//Log.v("LiquidParts",Integer.toString(actionId));
					if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT) {
							
							//Toast.makeText(act, v.getText().toString(), 4000);
						Log.v("LiquidParts",v.getText().toString());
		                try {
		                	int nval = Integer.parseInt(v.getText().toString());
		                	if(nval > 24 && nval < 76) {
		                		writeChanges();
		                		act.findViewById(R.id.EditText02).requestFocus();
		                	} else {
		                		if (nval > 75){
		                			v.setText("75");
		                		}
		                		if (nval < 25) {
		                			v.setText("25");
		                		}
		                		v.requestFocus();
		                	}
						} catch (Exception e) {
							e.printStackTrace();
						}
		                
		                return true;
		            }
		            return false;

				}
		    });
		  
		  haptick.setOnCheckedChangeListener(new OnCheckedChangeListener()
		    {
		        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
		        {
		                try {
							writeChanges();
						} catch (Exception e) {
							e.printStackTrace();
						}
		        }
		    });
		  
		  vibText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

				@Override
				public boolean onEditorAction(TextView v, int actionId,
						KeyEvent event) {
					//Log.v("LiquidParts",Integer.toString(actionId));
					if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT) {
							
							//Toast.makeText(act, v.getText().toString(), 4000);
						Log.v("LiquidParts",v.getText().toString());
		                try {
		                	int nval = Integer.parseInt(v.getText().toString());
		                	if(nval > 9 && nval < 101) {
		                		writeChanges();
		                		act.findViewById(R.id.EditText03).requestFocus();
		                	} else {
		                		if (nval > 100){
		                			v.setText("30");
		                		}
		                		if (nval < 10) {
		                			v.setText("10");
		                		}
		                		v.requestFocus();
		                	}
						} catch (Exception e) {
							e.printStackTrace();
						}
		                
		                return true;
		            }
		            return false;

				}
		    });
		  
		  thresText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

				@Override
				public boolean onEditorAction(TextView v, int actionId,
						KeyEvent event) {
					//Log.v("LiquidParts",Integer.toString(actionId));
					if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT) {
							
							//Toast.makeText(act, v.getText().toString(), 4000);
						Log.v("LiquidParts",v.getText().toString());
		                try {
		                	int nval = Integer.parseInt(v.getText().toString());
		                	if(nval > 14 && nval < 21) {
		                		writeChanges();
		                		//act.findViewById(R.id.EditText01).requestFocus();
		                		InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
	                        	imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
		                	} else {
		                		if (nval > 20){
		                			v.setText("20");
		                		}
		                		if (nval < 15) {
		                			v.setText("15");
		                		}
		                		v.requestFocus();
		                	}
						} catch (Exception e) {
							e.printStackTrace();
						}
		                
		                return true;
		            }
		            return false;

				}
		    });
	  }
	  
	  public String readFileValue(String file)throws java.io.IOException{
		 
		  BufferedReader reader = new BufferedReader(new FileReader(file));
		  String line, results="";
		  line = reader.readLine();
		  while(line != null) {
			  results += line;
		      line = reader.readLine();
		  }
		  reader.close();
		  return results;
	  }
	  
	  public void writeChanges() throws Exception{
		  EditText sensText=(EditText) this.findViewById(R.id.editText1);
		  EditText noisText=(EditText) this.findViewById(R.id.EditText01);
		  EditText vibText=(EditText) this.findViewById(R.id.EditText02);
		  EditText thresText=(EditText) this.findViewById(R.id.EditText03);
		  Switch haptick = (Switch) this.findViewById(R.id.switch01);
		  String fcont = "echo "+sensText.getText()+" > "+"/sys/devices/platform/i2c-adapter/i2c-0/0-005c/sensitivity \n";
		  fcont += "echo "+noisText.getText()+" > "+"/sys/devices/platform/i2c-adapter/i2c-0/0-005c/noise \n";
		  fcont += "echo "+vibText.getText()+" > "+"/sys/devices/platform/i2c-adapter/i2c-0/0-0066/vibr \n";
		  fcont += "echo "+thresText.getText()+" > "+"/sys/devices/platform/i2c-adapter/i2c-0/0-0066/threshold \n";
		  int hf = 0;
		  if (haptick.isChecked()){
			  hf = 1;
		  }
		  fcont += "echo "+Integer.toString(hf)+" > "+"/sys/module/avr/parameters/vibr \n";
		  BashCommand.doCmds("su","echo \""+fcont+"\" > /data/local/userinit.d/01LPScripts && chmod 755 /data/local/userinit.d/01LPScripts");
		  SharedPreferences settingsTweaks = getSharedPreferences("lpts", Context.MODE_PRIVATE);
		  SharedPreferences.Editor prefEditor = settingsTweaks.edit();
		  prefEditor.putInt("sensitivity", Integer.parseInt(sensText.getText().toString()));
		  prefEditor.putInt("noise", Integer.parseInt(noisText.getText().toString()));
		  prefEditor.putBoolean("haptic", haptick.isChecked());
		  prefEditor.putInt("vibdel", Integer.parseInt(vibText.getText().toString())); 
		  prefEditor.putInt("threshold", Integer.parseInt(thresText.getText().toString()));
		  prefEditor.commit();
		  BashCommand.doCmds("su","sh /data/local/userinit.d/01LPScripts");
	  }
	  
	  public boolean checkFS() throws Exception{
		  boolean fsready=false;
		  File userinitdir = new File("/data/local/userinit.d");
		   
		  if (userinitdir.getAbsoluteFile().exists()) {
			  fsready=true;
		  } else {
			  BashCommand.doCmds("su","mkdir /data/local/userinit.d && touch /data/local/userinit.d/01LPScripts chmod -R 755 /data/local/userinit.d && chmod 755 /data/local/userinit.d/01LPScripts");
			  fsready=true;
		  }
		  return fsready;
	  }
}
