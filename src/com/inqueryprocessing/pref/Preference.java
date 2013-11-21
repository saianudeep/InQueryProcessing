package com.inqueryprocessing.pref;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Switch;
import android.widget.ToggleButton;

import com.inqueryprocessing.R;

public class Preference extends Activity {
	ToggleButton bluetoothBtn;
	BluetoothAdapter mBluetoothAdapter;
	int REQUEST_ENABLE_BT = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_preference);
		bluetoothBtn = (ToggleButton) findViewById(R.id.bluetoothBtn);
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

		bluetoothBtn.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				if (isChecked) {
					Intent enableBtIntent = new Intent(
							BluetoothAdapter.ACTION_REQUEST_ENABLE);
					startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
					bluetoothBtn.setChecked(true);
				} else {
					bluetoothBtn.setChecked(false);
					mBluetoothAdapter.disable();

				}

			}
		});
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (mBluetoothAdapter.isEnabled())
			bluetoothBtn.setChecked(true);
		else
			bluetoothBtn.setChecked(false);
	}

}
