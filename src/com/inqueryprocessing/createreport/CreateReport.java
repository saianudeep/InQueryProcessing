package com.inqueryprocessing.createreport;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.inqueryprocessing.R;
import com.inqueryprocessing.bluetooth.CommonObjects;

public class CreateReport extends Activity {
	EditText application, location, time;
	Button create;
	String appStr, locStr, timeStr;
	Context context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_report);
		context = this;
		application = (EditText) findViewById(R.id.application);
		location = (EditText) findViewById(R.id.location);
		time = (EditText) findViewById(R.id.time);
		create = (Button) findViewById(R.id.create);
		create.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				appStr = application.getText().toString();
				timeStr = time.getText().toString();
				locStr = location.getText().toString();
				BluetoothAdapter mBluetoothAdapter = BluetoothAdapter
						.getDefaultAdapter();

				String id = mBluetoothAdapter.getAddress();
				if (CommonObjects.isWhiteSpace(appStr)
						|| CommonObjects.isWhiteSpace(timeStr)
						|| CommonObjects.isWhiteSpace(locStr)) {
					Toast.makeText(context, "Please Enter All Fields.",
							Toast.LENGTH_LONG).show();

				} else {
					CommonObjects.database
							.insert_update("Insert into MyReports (\"Type\",\"Time\",\"Location\",\"id\""
									+ ") values (\""
									+ appStr
									+ "\",\""
									+ timeStr
									+ "\",\""
									+ locStr
									+ "\",\""
									+ id
									+ "\")");
					Toast.makeText(context, "Report Created.",
							Toast.LENGTH_LONG).show();
					CommonObjects.reportCreated = true;
					finish();
				}
			}
		});
	}
}
