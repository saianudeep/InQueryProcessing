package com.inqueryprocessing.bluetooth;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.inqueryprocessing.R;

public class ReportList extends Activity {
	List<String> arrayList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list);

		arrayList = new ArrayList<String>();
		Cursor cursor = CommonObjects.database
				.selectQuery("Select Type from ReceiveReport");
		if (cursor.getCount() > 0) {
			if (cursor.moveToFirst()) {
				do {
					arrayList.add(cursor.getString(0));
				} while (cursor.moveToNext());
			}
		}
		ListView list = (ListView) findViewById(R.id.listView);
		list.setAdapter(new ArrayAdapter(getApplicationContext(),
				android.R.layout.simple_list_item_1, arrayList));
	}

}
