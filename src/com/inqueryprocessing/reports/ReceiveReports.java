package com.inqueryprocessing.reports;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.inqueryprocessing.R;

public class ReceiveReports extends Fragment {
	ListView listView;
	ArrayList<String> arrayList;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View myFragmentView = inflater.inflate(R.layout.reportlist, container,
				false);
		arrayList = new ArrayList<String>();
		for (int i = 0; i < 20; i++)
			arrayList.add("Recieved Report");
		listView = (ListView) myFragmentView.findViewById(R.id.listView);
		listView.setAdapter(new ArrayAdapter(getActivity(),
				android.R.layout.simple_list_item_1, arrayList));
		return myFragmentView;
	}
}
