package com.inqueryprocessing.pair;

import java.util.List;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.inqueryprocessing.R;

public class PairArrayAdaptor extends ArrayAdapter<String> {

	Activity context;
	List<String> list;
	ViewHolder holder;

	public PairArrayAdaptor(Activity context, List<String> arrayList) {
		super(context, R.layout.pairlist_row, arrayList);
		// TODO Auto-generated constructor stub
		this.context = context;
		this.list = arrayList;
	}
	
	class ViewHolder {
		protected TextView name;
	}

	public View getView(int position, View convertView, ViewGroup arg2) {
		// TODO Auto-generated method stub
		View view = null;
		if (convertView == null) {
			LayoutInflater inflator = context.getLayoutInflater();
			view = inflator.inflate(R.layout.pairlist_row, null);
			final ViewHolder viewHolder = new ViewHolder();
			viewHolder.name = (TextView) view.findViewById(R.id.name);
			view.setTag(viewHolder);
		} else {
			view = convertView;
		}
		holder = (ViewHolder) view.getTag();
		holder.name.setText(list.get(position));

		return view;
	}

}
