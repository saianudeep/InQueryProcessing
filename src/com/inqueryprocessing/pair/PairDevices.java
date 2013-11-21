package com.inqueryprocessing.pair;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.inqueryprocessing.Bluetooth;
import com.inqueryprocessing.R;

public class PairDevices extends Activity {
	BluetoothAdapter mBluetoothAdapter;
	ListView scanListView, pairListView;
	PairArrayAdaptor adaptor;
	List<String> arrayList;
	Set<BluetoothDevice> pairedDevices;
	Context context;
	static List<BluetoothDevice> devices;

	// BroadcastReceiver mReceiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pair_devices);
		context = this;

		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		scanListView = (ListView) findViewById(R.id.scanList);
		pairListView = (ListView) findViewById(R.id.pairList);
		getList();
		pairListView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				showAlert(arg2);

				return false;
			}
		});
		scanListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				pairDevice(devices.get(arg2));

			}
		});
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (mBluetoothAdapter.isEnabled()) {
			IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
			mBluetoothAdapter.startDiscovery();
			registerReceiver(mReceiver, filter);
			arrayList = new ArrayList<String>();
			devices = new ArrayList<BluetoothDevice>();
			// getList();
			// mReceiver = new BroadcastReceiver() {
			// public void onReceive(Context context, Intent intent) {
			// String action = intent.getAction();
			// getList();
			// }
			// };

		} else {
			Toast.makeText(getApplicationContext(),
					"First Turn On your BlueTooth.", Toast.LENGTH_LONG).show();
			finish();
		}
	}

	private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();

			// When discovery finds a device
			if (BluetoothDevice.ACTION_FOUND.equals(action)) {
				// Get the BluetoothDevice object from the Intent
				BluetoothDevice device = intent
						.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				// Log.e("Device Found", device.getName());
				// Add the name and address to an array adapter to show in a
				// ListView
				// mArrayAdapter.add(device.getName() + "\n" +
				// device.getAddress());
				Log.e("Devices", "" + device.getName());
				if (arrayList.size() <= 0) {
					arrayList.add(device.getName());
					devices.add(device);
				}
				boolean flag = true;
				for (int i = 0; i < arrayList.size(); i++) {

					if ((arrayList.get(i).equals(device.getName()))) {
						flag = false;
						break;
					}

				}
				if (flag) {
					arrayList.add(device.getName());
					devices.add(device);
				}

				scanListView.setAdapter(new PairArrayAdaptor(PairDevices.this,
						arrayList));

				// pairDevice(device);

			}
		}

	};

	@SuppressLint("NewApi")
	void getList() {
		pairedDevices = mBluetoothAdapter.getBondedDevices();

		// If there are paired devices
		if (pairedDevices.size() > 0) {
			// Loop through paired devices
			List<String> list = new ArrayList<String>();
			for (BluetoothDevice device : pairedDevices) {

				Log.e("Devices", ("Name: " + device.getName() + " Address:"
						+ device.getAddress() + " UDID" + device.getUuids()));
				list.add(device.getName());
			}
			pairListView
					.setAdapter(new PairArrayAdaptor(PairDevices.this, list));

		}

	}

	private void pairDevice(BluetoothDevice device) {
		try {

			Method m = device.getClass()
					.getMethod("createBond", (Class[]) null);
			m.invoke(device, (Object[]) null);
			getList();

		} catch (Exception e) {
			Log.e("Exception", e.getMessage());
		}
	}

	private void getPairList() {
		// TODO Auto-generated method stub
		List<String> list = new ArrayList<String>();
		for (int i = 0; i < devices.size(); i++)
			list.add(devices.get(i).getName());
		pairListView.setAdapter(new PairArrayAdaptor(PairDevices.this, list));
	}

	private void unpairDevice(BluetoothDevice device) {
		try {
			Method m = device.getClass()
					.getMethod("removeBond", (Class[]) null);
			m.invoke(device, (Object[]) null);

			getList();
		} catch (Exception e) {
			Log.e("Exception", e.getMessage());
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		// if (mReceiver.isOrderedBroadcast())
		// unregisterReceiver(mReceiver);
	}

	void showAlert(final int pos) {

		Builder dialog = new AlertDialog.Builder(context);
		dialog.setTitle("Are you sure?");
		dialog.setMessage("You Want to Delete this pair?");
		dialog.setPositiveButton("Yes", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				deletePair(pos);
			}
		});

		dialog.setNegativeButton("No", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});

		dialog.show();

	}

	protected void deletePair(int arg2) {
		// TODO Auto-generated method stub
		for (BluetoothDevice device : devices) {
			if (device.getName().equalsIgnoreCase(arrayList.get(arg2)))
				unpairDevice(device);

		}
		getList();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub

		getMenuInflater().inflate(R.menu.activity_pair_devices, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case R.id.menu_settings:
			getList();

			break;

		default:
			break;
		}

		return super.onOptionsItemSelected(item);

	}
}
