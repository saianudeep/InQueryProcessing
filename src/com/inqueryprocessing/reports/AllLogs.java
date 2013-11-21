package com.inqueryprocessing.reports;

import java.util.ArrayList;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.inqueryprocessing.R;
import com.inqueryprocessing.bluetooth.CommonObjects;

public class AllLogs extends Activity {
	ListView listView;
	ArrayList<String> arrayList;
	String type;
	Button delete;
	ArrayAdapter<String> adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list);
		delete = (Button) findViewById(R.id.delete);

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		setList();
		// list.setOnItemClickListener(new OnItemClickListener() {
		//
		// @Override
		// public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
		// long arg3) {
		// // TODO Auto-generated method stub
		// if (!type.equalsIgnoreCase("all")) {
		// String[] sub = listView.getItemAtPosition(arg2).toString()
		// .split("\n");
		// String deciceId = sub[0].substring(10, sub[0].length());
		// String reportId = sub[1].substring(10, sub[1].length());
		// String typeData = sub[2].substring(5, sub[2].length());
		// String time = sub[3].substring(5, sub[3].length());
		// String loc = sub[4].substring(8, sub[4].length());
		// CommonObjects.database.insert_update("Delete from " + type
		// + "  where Type = \"" + typeData + "\", Time = \""
		// + time + "\",Location=\"" + loc + "\", id=\""
		// + deciceId + "\",rowid=\"" + reportId + "\"");
		// }
		// }
		// });
		delete.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (!type.equalsIgnoreCase("all")) {

					CommonObjects.database.insert_update("Delete from " + type
							+ "");

					// adapter.notifyDataSetChanged();
					// listView.notifyAll();
				} else {
					CommonObjects.database
							.insert_update("Delete from SentReports");
					CommonObjects.database
							.insert_update("Delete from ReceivedReports");
					CommonObjects.database
							.insert_update("Delete from MyReports");

				}
				setList();
			}
		});
	}

	void setList() {
		arrayList = new ArrayList<String>();
		type = getIntent().getExtras().getString("type");
		if (type.equalsIgnoreCase("all")) {

			Cursor cursor = CommonObjects.database
					.selectQuery("Select id,rowid,Type,Time,Location from ReceivedReports union all select id,rowid,Type,Time,Location from SentReports union all select id,rowid,Type,Time,Location from MyReports");
			if (cursor.getCount() > 0) {
				if (cursor.moveToFirst()) {
					do {
						arrayList.add("Device id:" + cursor.getString(0)
								+ "\nReport id:" + cursor.getString(1)
								+ "\nType:" + cursor.getString(2) + "\nTime:"
								+ cursor.getString(3) + "\nLocation:"
								+ cursor.getString(4));
					} while (cursor.moveToNext());
				}
			}

		} else {
			Cursor cursor = CommonObjects.database
					.selectQuery("Select id,rowid,Type,Time,Location from "
							+ type);
			if (cursor.getCount() > 0) {
				if (cursor.moveToFirst()) {
					do {
						arrayList.add("Device id:" + cursor.getString(0)
								+ "\nReport id:" + cursor.getString(1)
								+ "\nType:" + cursor.getString(2) + "\nTime:"
								+ cursor.getString(3) + "\nLocation:"
								+ cursor.getString(4));
					} while (cursor.moveToNext());
				}
			}
		}
		ListView list = (ListView) findViewById(R.id.listView);
		adapter = new ArrayAdapter(getApplicationContext(),
				android.R.layout.simple_list_item_1, arrayList);
		list.setAdapter(adapter);
	}
}
