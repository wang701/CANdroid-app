package com.example.yang.candroid;

import java.io.IOException;
import java.util.Queue;

import android.widget.BaseAdapter;
import android.widget.TextView;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import org.apache.commons.collections4.queue.CircularFifoQueue;

public class MsgAdapter extends BaseAdapter {
	private CircularFifoQueue<String> msgQueue;
	private final Context context;

	public MsgAdapter(Context context, final int size) {
		this.context = context;
		this.msgQueue = new CircularFifoQueue<String>(size);
	}

	public void add(String msg) {
		msgQueue.add(msg);
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return msgQueue.size();
	}

	@Override
	public String getItem(int position) {
		return msgQueue.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = new TextView(context);
		}
		((TextView) convertView).setText(getItem(position));

		return convertView;
	}

	@Override
	public boolean isEmpty() {
		return msgQueue.isEmpty();
	}
}
