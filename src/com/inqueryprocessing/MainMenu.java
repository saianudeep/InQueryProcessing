package com.inqueryprocessing;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Random;
import java.util.UUID;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.database.Cursor;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.inqueryprocessing.about.About;
import com.inqueryprocessing.bluetooth.BluetoothChatService;
import com.inqueryprocessing.bluetooth.CommonObjects;
import com.inqueryprocessing.bluetooth.DataBaseManager;
import com.inqueryprocessing.bluetooth.DeviceListActivity;
import com.inqueryprocessing.createreport.CreateReport;
import com.inqueryprocessing.pair.PairDevices;
import com.inqueryprocessing.reports.Reports;

@SuppressLint("NewApi")
public class MainMenu extends Activity {
	public interface Stub {

	}

	Button pairList, pref, about, exit, reports, sendReports, creatReport;
	String myReportQuery = "Select Type,Time,Location,id,rowid from MyReports";
	Cursor cursor;
	String connectedDeviceName = "";
	boolean sendingStart = false;
	// Debugging
	private static final String TAG = "BluetoothChat";
	private static final boolean D = true;

	// Message types sent from the BluetoothChatService Handler
	public static final int MESSAGE_STATE_CHANGE = 1;
	public static final int MESSAGE_READ = 2;
	public static final int MESSAGE_WRITE = 3;
	public static final int MESSAGE_DEVICE_NAME = 4;
	public static final int MESSAGE_TOAST = 5;

	// Key names received from the BluetoothChatService Handler
	public static final String DEVICE_NAME = "device_name";
	public static final String TOAST = "toast";

	// Intent request codes
	private static final int REQUEST_CONNECT_DEVICE = 1;
	private static final int REQUEST_ENABLE_BT = 2;

	private final long startTime = 60 * 60 * 24 * 1000;
	private final long interval = 30 * 1000;

	// // Layout Views
	// private TextView mTitle;
	// private ListView mConversationView;
	// private EditText mOutEditText;
	// private Button mSendButton;

	// Name of the connected device
	private String mConnectedDeviceName = null;
	// Array adapter for the conversation thread
	// private ArrayAdapter<String> mConversationArrayAdapter;
	// String buffer for outgoing messages
	private StringBuffer mOutStringBuffer;
	// Local Bluetooth adapter
	private BluetoothAdapter mBluetoothAdapter = null;
	// Member object for the chat services
	private BluetoothChatService mChatService = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_menu);

		setLayout();
		try {
			CommonObjects.database = new DataBaseManager(this);
			CommonObjects.database.createDataBase();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// Get local Bluetooth adapter
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

		// If the adapter is null, then Bluetooth is not supported
		if (mBluetoothAdapter == null) {
			Toast.makeText(this, "Bluetooth is not available",
					Toast.LENGTH_LONG).show();
			finish();
			return;
		}
	}

	@Override
	public void onStart() {
		super.onStart();

		// If BT is not on, request that it be enabled.
		// setupChat() will then be called during onActivityResult
		if (!mBluetoothAdapter.isEnabled()) {
			Intent enableIntent = new Intent(
					BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
			// Otherwise, setup the chat session
		} else {
			if (mChatService == null)
				setupChat();
		}
	}

	@Override
	public synchronized void onResume() {
		super.onResume();

		// Performing this check in onResume() covers the case in which BT was
		// not enabled during onStart(), so we were paused to enable it...
		// onResume() will be called when ACTION_REQUEST_ENABLE activity
		// returns.
		if (CommonObjects.reportCreated) {

			CommonObjects.reportCreated = false;
		}
		if (mChatService != null) {
			// Only if the state is STATE_NONE, do we know that we haven't
			// started already
			if (mChatService.getState() == BluetoothChatService.STATE_NONE) {
				// Start the Bluetooth chat services
				mChatService.start();
			}
		}

	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub

	}

	void startSending() {
		while (sendingStart) {
			try {

				// sendMyReport();
				// getRandomReport();
				Thread.sleep(10000); // sleep for 10 seconds
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
	}

	@SuppressWarnings("unused")
	private void checkLimit() {
		PackageManager pm = getPackageManager();

		Method getPackageSizeInfo;
		try {
			getPackageSizeInfo = pm.getClass().getMethod("getPackageSizeInfo",
					String.class, MainMenu.class);

			getPackageSizeInfo.invoke(pm, "com.android.sqlite",
					new MainMenu.Stub() {

						public void onGetStatsCompleted(PackageStats pStats,
								boolean succeeded) throws RemoteException {

							Log.i(TAG, "codeSize: " + pStats.codeSize);
						}
					});

		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		catch (NoSuchMethodException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	private void sendMyReport() {
		// TODO Auto-generated method stub
		// sendMyCreatedReports();
		send(getRandomReport());

	}

	private boolean isReportExist(Intent intent) {
		BluetoothDevice device = intent
				.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
		Method m = null;
		try {
			m = device.getClass().getMethod("createRfcommSocket",
					new Class[] { int.class });
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			m.invoke(device, 1);
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}

	@SuppressWarnings("unused")
	private class ConnectThread extends Thread {
		private final BluetoothSocket mmSocket;
		private final BluetoothDevice mmDevice;
		BluetoothSocket tmp = null;

		public ConnectThread(BluetoothDevice device) {
			mmDevice = device;

			// Get a BluetoothSocket for a connection with the
			// given BluetoothDevice
			try {
				tmp = device.createRfcommSocketToServiceRecord(UUID
						.fromString("0001-0"));
				Log.d(TAG, "tmp = " + tmp.toString());
			} catch (IOException e) {
				Log.e(TAG, "create() failed", e);
			}
			mmSocket = tmp;
		}

		public void run() {
			Log.i(TAG, "BEGIN mConnectThread");
			setName("ConnectThread");

			BluetoothAdapter mAdapter = null;
			// Always cancel discovery because it will slow down a connection
			mAdapter.cancelDiscovery();

			// Make a connection to the BluetoothSocket
			try {
				// This is a blocking call and will only return on a
				// successful connection or an exception
				Log.d(TAG, " before connecting mmsocket.connect()");
				mmSocket.connect();
				Log.d(TAG, " after connecting mmsocket.connect()");
			} catch (IOException e) {
				Log.d(TAG, " before connectionFailed()");
				connectionFailed();
				Log.d(TAG, " after connectionFailed()");
				Log.d(TAG,
						"after connectiofailed exception is: " + e.toString());
				// Close the socket
				try {
					mmSocket.close();
				} catch (IOException e2) {
					Log.d(TAG,
							"unable to close() socket during connection failure: "
									+ e2.toString());
				}

				// // Start the service over to restart listening mode
				// BluetoothChatService.this.start();
				return;
			}

			// Reset the ConnectThread because we're done
			synchronized (MainMenu.this) {
				Object mConnectThread = null;
			}
			Log.d(TAG, " before connected mmsocket and mmdevice");

			// Start the connected thread
			connected(mmSocket, mmDevice);
			Log.d(TAG, "after connected mmsocket and mmdevice");
		}

		private void connected(BluetoothSocket mmSocket2,
				BluetoothDevice mmDevice2) {
			// TODO Auto-generated method stub

		}

		private void connectionFailed() {
			// TODO Auto-generated method stub

		}

		public void cancel() {
			try {
				mmSocket.close();
			} catch (IOException e) {
				Log.e(TAG, "close() of connect socket failed", e);
			}
		}
	}

	private void sendMyCreatedReports() {
		cursor = CommonObjects.database.selectQuery(myReportQuery);

		if (cursor.getCount() > 0) {
			if (cursor.moveToFirst()) {
				do {
					String send = cursor.getString(0) + ","
							+ cursor.getString(1) + "," + cursor.getString(2)
							+ "," + cursor.getString(3) + ","
							+ cursor.getString(4);
					send(send);
					Toast.makeText(MainMenu.this, send, Toast.LENGTH_SHORT)
							.show();
				} while (cursor.moveToNext());
			}
		}
	}

	private String getRandomReport() {
		// TODO Auto-generated method stub
		// Select Type,Time,Location,id,rowid from MyReports
		// int random_integer = new Random(1).nextInt(3 - 1) + 1;
		Random r = new Random();
		int random_integer = r.nextInt(4 - 1) + 1;
		Log.e("Rand", random_integer + "");
		String id = mBluetoothAdapter.getAddress();
		switch (random_integer) {
		case 1:
			return "Bus,1:00,BusStand," + id + ",1";

		case 2:
			return "Car,2:00,Parking," + id + ",2";
		case 3:
			return "Bike,3:00,Parking," + id + ",3";
		default:
			return "Car,4:00,Home," + id + ",4";
		}

	}

	private void setupChat() {
		Log.d(TAG, "setupChat()");

		// Initialize the array adapter for the conversation thread
		/*
		 * mConversationArrayAdapter = new ArrayAdapter<String>(this,
		 * R.layout.message);
		 */

		// CommonObjects.database.selectQuery("Select Type from ReceiveReport");

		// Initialize the compose field with a listener for the return key

		// Initialize the send button with a listener that for click events

		sendReports.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// Send a message using content of the edit text widget

				// sendMessage("Taxi,Available");
				sendMyReport();
			}
		});

		// Initialize the BluetoothChatService to perform bluetooth connections
		mChatService = new BluetoothChatService(this, mHandler);

		// Initialize the buffer for outgoing messages
		mOutStringBuffer = new StringBuffer("");
	}

	@SuppressLint("NewApi")
	@SuppressWarnings("unused")
	private void checkSupply(String message, BroadcastReceiver mReceiver,
			Intent intent) {
		if (mChatService.getState() != BluetoothChatService.STATE_CONNECTED) {
			Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show();
			return;
		}

		// Check that there's actually something to send
		if (message.length() > 0) {
			// Get the message bytes and tell the BluetoothChatService to write
			byte[] send = message.getBytes();
			mChatService.write(send);

			int plugged = Bluetooth.TRIM_MEMORY_UI_HIDDEN;
			IntentFilter filter1 = new IntentFilter(
					BluetoothDevice.ACTION_ACL_CONNECTED);
			IntentFilter filter2 = new IntentFilter(
					BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED);
			IntentFilter filter3 = new IntentFilter(
					BluetoothDevice.ACTION_ACL_DISCONNECTED);

			this.registerReceiver(mReceiver, filter1);
			this.registerReceiver(mReceiver, filter2);
			this.registerReceiver(mReceiver, filter3);
			BluetoothDevice device = intent
					.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
			try {
				// Constant value for udid
				device.createInsecureRfcommSocketToServiceRecord(UUID
						.fromString("00001101"));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	void checkDemand(String message, BroadcastReceiver mReceiver, Intent intent) {
		if (mChatService.getState() != BluetoothChatService.STATE_CONNECTED) {
			Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show();
			return;
		}

		// Check that there's actually something to send
		if (message.length() > 0) {
			// Get the message bytes and tell the BluetoothChatService to write
			byte[] send = message.getBytes();
			mChatService.write(send);

			int plugged = Bluetooth.TRIM_MEMORY_UI_HIDDEN;
			IntentFilter filter1 = new IntentFilter(
					BluetoothDevice.ACTION_ACL_CONNECTED);
			IntentFilter filter2 = new IntentFilter(
					BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED);
			IntentFilter filter3 = new IntentFilter(
					BluetoothDevice.ACTION_ACL_DISCONNECTED);

			this.registerReceiver(mReceiver, filter1);
			this.registerReceiver(mReceiver, filter2);
			this.registerReceiver(mReceiver, filter3);
			BluetoothDevice device = intent
					.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
			try {
				// Constant value for udid
				device.createInsecureRfcommSocketToServiceRecord(UUID
						.fromString("11101000"));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	public synchronized void onPause() {
		super.onPause();
		if (D)
			Log.e(TAG, "- ON PAUSE -");
	}

	@Override
	public void onStop() {
		super.onStop();
		if (D)
			Log.e(TAG, "-- ON STOP --");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		// Stop the Bluetooth chat services
		if (mChatService != null)
			mChatService.stop();
		if (D)
			Log.e(TAG, "--- ON DESTROY ---");
	}

	private void ensureDiscoverable() {
		if (D)
			Log.d(TAG, "ensure discoverable");
		if (mBluetoothAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
			Intent discoverableIntent = new Intent(
					BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
			discoverableIntent.putExtra(
					BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 1000);
			startActivity(discoverableIntent);
		}
	}

	/**
	 * Sends a message.
	 * 
	 * @param message
	 *            A string of text to send.
	 */
	private void sendMessage(String message) {
		// Check that we're actually connected before trying anything
		if (mChatService.getState() != BluetoothChatService.STATE_CONNECTED) {
			Toast.makeText(this, "Not Connected", Toast.LENGTH_SHORT).show();
			return;
		}

		// Check that there's actually something to send
		if (message.length() > 0) {
			// Get the message bytes and tell the BluetoothChatService to write
			byte[] send = message.getBytes();
			mChatService.write(send);	
			String str[] = message.split(",");
			CommonObjects.database
					.insert_update("Insert into SentReports (\"Type\",\"Time\",\"Location\",\"id\") values (\""
							+ str[0]
							+ "\",\""
							+ str[1]
							+ "\",\""
							+ str[2]
							+ "\",\"" + str[3] + "\"" + ")");
			// Reset out string buffer to zero and clear the edit text field
			mOutStringBuffer.setLength(0);

		}
	}

	private final Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MESSAGE_STATE_CHANGE:
				if (D)
					Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
				switch (msg.arg1) {
				case BluetoothChatService.STATE_CONNECTED:
					// mConversationArrayAdapter.clear();
					break;
				case BluetoothChatService.STATE_CONNECTING:

					break;
				case BluetoothChatService.STATE_LISTEN:
				case BluetoothChatService.STATE_NONE:

					break;
				}
				break;
			case MESSAGE_WRITE:
				byte[] writeBuf = (byte[]) msg.obj;
				// construct a string from the buffer
				String writeMessage = new String(writeBuf);
				// mConversationArrayAdapter.add("Me:  " + writeMessage);
				break;
			case MESSAGE_READ:
				byte[] readBuf = (byte[]) msg.obj;
				// construct a string from the valid bytes in the buffer
				String readMessage = new String(readBuf, 0, msg.arg1);
				/*
				 * mConversationArrayAdapter.add(mConnectedDeviceName + ":  " +
				 * readMessage);
				 */
				Log.e("Message Read", readMessage);
				// Toast.makeText(getApplicationContext(), readMessage,
				// Toast.LENGTH_SHORT).show();
				cursor = CommonObjects.database
						.selectQuery("Select Type,Time,Location,id,rowid from ReceivedReports");
				if (cursor.getCount() > 0) {
					boolean flag = false;
					if (cursor.moveToFirst()) {
						do {
							if ((cursor.getString(0) + ","
									+ cursor.getString(1) + "," + cursor
									.getString(2))
									.equalsIgnoreCase(readMessage)) {
								flag = true;
								break;
							}
						} while (cursor.moveToNext());
					}
					if (!flag) {
						String str[] = readMessage.split(",");
						CommonObjects.database
								.insert_update("Insert into ReceivedReports (\"Type\",\"Time\",\"Location\",\"id\") values (\""
										+ str[0]
										+ "\",\""
										+ str[1]
										+ "\",\""
										+ str[2]
										+ "\",\""
										+ str[3]
										+ "\""
										+ ")");
					}
					// CommonObjects.database
					// .insert_update("insert into ReceiveReport (Type) values (\""
					// + readMessage + "\") ");
				} else {
					String str[] = readMessage.split(",");
					CommonObjects.database
							.insert_update("Insert into ReceivedReports  (\"Type\",\"Time\",\"Location\",\"id\") values (\""
									+ str[0]
									+ "\",\""
									+ str[1]
									+ "\",\""
									+ str[2] + "\",\"" + str[3] + "\"" + ")");
				}
				break;
			case MESSAGE_DEVICE_NAME:
				// save the connected device's name
				mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
				Toast.makeText(getApplicationContext(),
						"Connected to " + mConnectedDeviceName,
						Toast.LENGTH_SHORT).show();
				connectedDeviceName = mConnectedDeviceName;
				sendingStart = true;

				// startSending();
				sendMyCreatedReports();
				CountDownTimer countDownTimer = new MyCountDownTimer(startTime,
						interval);
				countDownTimer.start();

				break;
			case MESSAGE_TOAST:
				// Toast.makeText(getApplicationContext(),
				// msg.getData().getString(TOAST), Toast.LENGTH_SHORT)
				// .show();
				break;
			}
		}
	};

	private BluetoothSocket socket;
	private InputStream inputStream;
	private Object SamplesUtils;
	private Object hexString;

	public class MyCountDownTimer extends CountDownTimer {
		public MyCountDownTimer(long startTime, long interval) {
			super(startTime, interval);
		}

		@Override
		public void onFinish() {

		}

		@Override
		public void onTick(long millisUntilFinished) {
			sendMyReport();
		}
	}

	void queryProcess(boolean isChecked, Object obj1, CharSequence str)
			throws IOException {
		Object outputStream = null;
		Object obj2 = null;
		if (isChecked) {
			String relay1 = "c51";
			if (outputStream != null) {
				synchronized (obj2) {
					((BluetoothChatService) outputStream).write(relay1
							.getBytes());
				}
			} else {
				Toast.makeText(getBaseContext(), "failed to send ... 5",
						Toast.LENGTH_SHORT).show();
			}
		} else {
			String relay1 = "c50";
			if (outputStream != null) {
				synchronized (obj2) {
					((BluetoothChatService) outputStream).write(relay1
							.getBytes());
				}
			} else {
				Toast.makeText(getBaseContext(), "failed to send ... 5",
						Toast.LENGTH_SHORT).show();
			}
		}
		Object device = null;
		Method m = null;
		try {
			m = device.getClass().getMethod("createRfcommSocket",
					new Class[] { int.class });
		} catch (NoSuchMethodException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			socket = (BluetoothSocket) m.invoke(device, 1);
		} catch (IllegalArgumentException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IllegalAccessException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (InvocationTargetException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		socket.connect();
		Log.d(TAG, ">>Client connectted");
		inputStream = socket.getInputStream();
		outputStream = socket.getOutputStream();
		int read = -1;
		final byte[] bytes = new byte[2048];
		while (true) {
			synchronized (obj1) {
				read = inputStream.read(bytes);
				Log.d(TAG, "read:" + read);
				if (read > 0) {
					final int count = read;
					// Log.d(TAG, "test1:" + str);
					String hex = hexString.toString();
					if (hex == "") {
						((TextView) hexString).append("<--");
					} else {
						if (hex.lastIndexOf("<--") < hex.lastIndexOf("-->")) {
							((TextView) hexString).append("\n<--");
						}
					}
					((TextView) hexString).append(str);
					hex = hexString.toString();
					int maxlength = 0;
					// Log.d(TAG, "test2:" + hex);
					if (hex.length() > maxlength) {
						try {
							hex = hex.substring(hex.length() - maxlength,
									hex.length());
							hex = hex.substring(hex.indexOf(" "));
							hex = "<--" + hex;
							hexString = new StringBuffer();
							((TextView) hexString).append(hex);
						} catch (Exception e) {
							e.printStackTrace();
							Log.e(TAG, "e", e);
						}
					}
					if (socket != null) {
						try {
							Log.d(TAG, ">>Client Socket Close");
							socket.close();
							socket = null;
							// this.finish();
							return;
						} catch (IOException e) {
							Log.e(TAG, ">>", e);

						}
					}
					mHandler.post(new Runnable() {
						public void run() {
						}
					});
				}
			}
		}

	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		checkSize();
		if (D)
			Log.d(TAG, "onActivityResult " + resultCode);
		switch (requestCode) {
		case REQUEST_CONNECT_DEVICE:
			// When DeviceListActivity returns with a device to connect
			if (resultCode == Activity.RESULT_OK) {
				// Get the device MAC address
				String address = data.getExtras().getString(
						DeviceListActivity.EXTRA_DEVICE_ADDRESS);
				// Get the BLuetoothDevice object
				BluetoothDevice device = mBluetoothAdapter
						.getRemoteDevice(address);
				// Attempt to connect to the device
				mChatService.connect(device);
			}
			break;
		case REQUEST_ENABLE_BT:
			// When the request to enable Bluetooth returns
			if (resultCode == Activity.RESULT_OK) {
				// Bluetooth is now enabled, so set up a chat session
				setupChat();
			} else {
				// User did not enable Bluetooth or an error occured
				Log.d(TAG, "BT not enabled");
				// Toast.makeText(this, R.string.bt_not_enabled_leaving,
				// Toast.LENGTH_SHORT).show();
				finish();
			}
		}
	}

	protected void send(String string) {
		// TODO Auto-generated method stub
		sendMessage(string);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.option_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.scan:
			// Launch the DeviceListActivity to see devices and do scan
			Intent serverIntent = new Intent(this, DeviceListActivity.class);
			startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
			return true;
		case R.id.discoverable:
			// Ensure this device is discoverable by others
			ensureDiscoverable();
			return true;

			// case R.id.showList:
			// startActivity(new Intent(MainMenu.this, ReportList.class));
			// return true;
		}
		return false;
	}

	private void setLayout() {
		// TODO Auto-generated method stub
		pairList = (Button) findViewById(R.id.pairList);
		pref = (Button) findViewById(R.id.preferences);
		about = (Button) findViewById(R.id.about);
		exit = (Button) findViewById(R.id.exit);
		reports = (Button) findViewById(R.id.reports);
		sendReports = (Button) findViewById(R.id.sendReports);
		creatReport = (Button) findViewById(R.id.createReport);
		reports.setOnClickListener(new btnListener());
		pref.setOnClickListener(new btnListener());
		pairList.setOnClickListener(new btnListener());
		about.setOnClickListener(new btnListener());
		exit.setOnClickListener(new btnListener());
		creatReport.setOnClickListener(new btnListener());
	}

	public class btnListener implements View.OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.preferences:
				// startActivity(new Intent(getApplicationContext(),
				Intent intentBluetooth = new Intent();
				intentBluetooth
						.setAction(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS);
				startActivity(intentBluetooth);

				break;
			case R.id.pairList:
				startActivity(new Intent(getApplicationContext(),
						PairDevices.class));
				break;
			case R.id.about:
				startActivity(new Intent(getApplicationContext(), About.class));
				break;
			case R.id.reports:
				startActivity(new Intent(getApplicationContext(), Reports.class));
				break;
			case R.id.sendReports:
				sendMyReport();
				break;
			case R.id.exit:
				finish();
				break;
			case R.id.createReport:
				startActivity(new Intent(getApplicationContext(),
						CreateReport.class));
				break;
			default:
				break;
			}
		}
	}

	private void checkSize() {
		checkLimit();
	}

}
