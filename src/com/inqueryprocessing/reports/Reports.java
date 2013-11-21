package com.inqueryprocessing.reports;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

import com.inqueryprocessing.Bluetooth;
import com.inqueryprocessing.R;

public class Reports extends TabActivity {
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.reportlist);

		TabHost tabHost = getTabHost();

		// Tab for All Logs
		TabSpec spec1 = tabHost.newTabSpec("All Logs");
		spec1.setIndicator("All Logs",
				getResources().getDrawable(R.drawable.icon_photos_tab));
		Intent intent1 = new Intent(this, AllLogs.class)
				.putExtra("type", "All");
		spec1.setContent(intent1);
		// Tab for Sent Logs
		TabSpec spec2 = tabHost.newTabSpec("Sent");
		// setting Title and Icon for the Tab
		spec2.setIndicator("Sent",
				getResources().getDrawable(R.drawable.icon_photos_tab));
		Intent intent2 = new Intent(this, AllLogs.class).putExtra("type",
				"SentReports");
		spec2.setContent(intent2);
		// Tab for Receive Logs
		TabSpec spec3 = tabHost.newTabSpec("Receive");
		spec3.setIndicator("Receive",
				getResources().getDrawable(R.drawable.icon_photos_tab));
		Intent intent3 = new Intent(this, AllLogs.class).putExtra("type",
				"ReceivedReports");
		spec3.setContent(intent3);
		// Tab for Queries Logs
		TabSpec spec4 = tabHost.newTabSpec("Queries");
		spec4.setIndicator("Queries",
				getResources().getDrawable(R.drawable.icon_photos_tab));
		Intent intent4 = new Intent(this, AllLogs.class).putExtra("type",
				"SentReports");
		spec4.setContent(intent4);

		// Adding all TabSpec to TabHost
		tabHost.addTab(spec1); // Adding photos tab
		tabHost.addTab(spec2); // Adding songs tab
		tabHost.addTab(spec3); // Adding videos tab
		tabHost.addTab(spec4);
	}
}