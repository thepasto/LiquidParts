package com.thepasto.liquidparts;

import android.app.Activity;
import android.app.LocalActivityManager;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;


public class LiquidPartsActivity extends Activity {
	
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.main);

		TabHost tabs=(TabHost)findViewById(R.id.tabhost);
		
		LocalActivityManager mLocalActivityManager = new LocalActivityManager(this, false);
		mLocalActivityManager.dispatchCreate(icicle);
		tabs.setup(mLocalActivityManager);
		
		TabHost.TabSpec spec=tabs.newTabSpec("tag1");
		
		Intent intent = new Intent().setClass(this, TweaksActivity.class);
        spec = tabs
                .newTabSpec("tag1")
                .setIndicator("Tweaks")
                .setContent(intent);
        tabs.addTab(spec);
	
        intent = new Intent().setClass(this, A2sdActivity.class);
        spec = tabs.newTabSpec("tab2")
                .setIndicator("A2sd")
                .setContent(intent);
        tabs.addTab(spec);	
		
        intent = new Intent().setClass(this, AdvancedActivity.class);
        spec = tabs.newTabSpec("tab3")
                .setIndicator("Advanced")
                .setContent(intent);
		tabs.addTab(spec);
		
		BashCommand.doCmds("su","");
	}
}
