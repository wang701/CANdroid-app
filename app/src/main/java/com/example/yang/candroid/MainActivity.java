package com.example.yang.candroid;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ToggleButton;
import android.content.Intent;

import java.io.IOException;

import org.apache.commons.collections4.queue.CircularFifoQueue;
import de.greenrobot.event.EventBus;
import static android.os.Environment.getExternalStorageDirectory;

public class MainActivity extends Activity {
	private MsgAdapter mLog;
	private boolean mToggleState;
	private Intent mIt;
	private EventBus mBus;
	private ListView mMsgList;

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
		mBus = EventBus.getDefault();
		mBus.register(this);
		mLog = new MsgAdapter(this, 5);
		mMsgList = (ListView) findViewById(R.id.mylist);
    }

	@Override
	protected void onSaveInstanceState(Bundle savedInstanceState) {
		ToggleButton tB = (ToggleButton) findViewById(R.id.toggleButton);

		if (savedInstanceState != null) {
			savedInstanceState.putBoolean("tb_state", tB.isChecked());
		/*	savedInstanceState.putInt("log_size", mLog.getCount());
			int i;
			String[] saveLog = new String[mLog.getCount()];
			for (i = 0; i < mLog.getCount(); i++) {
				saveLog[i] = mLog.getItem(i);
			}
			savedInstanceState.putStringArray("log_data", saveLog);
			if (mMsgLoggerTask != null && mMsgLoggerTask.getStatus() != AsyncTask.Status.FINISHED) {
				mMsgLoggerTask.cancel(true);
			}
		*/
		}

		super.onSaveInstanceState(savedInstanceState);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		mMsgList.setAdapter(mLog);

	/*	int resLogSize = savedInstanceState.getInt("log_size");
		String[] resLog = new String[resLogSize];
		resLog = savedInstanceState.getStringArray("log_data");
		int i;
		mLog = new ArrayAdapter<String>(this, R.layout.message);
		for (i = 0; i < resLogSize; i++) {
			mLog.add(resLog[i]);
		}
		ListView lV = (ListView) findViewById(R.id.mylist);
		lV.setAdapter(mLog);
    */
		ToggleButton tB = (ToggleButton) findViewById(R.id.toggleButton);
		mToggleState = savedInstanceState.getBoolean("tb_state");
		tB.setChecked(mToggleState);
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_delete_log:
                return true;
            case R.id.action_email_log:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void toggleOnOff(View view) throws IOException {
        ToggleButton toggleButton = (ToggleButton) view;

        if(toggleButton.isChecked()){
			mMsgList.setAdapter(mLog);

			mIt = new Intent(this, CandroidLog.class);
			startService(mIt);
        } else {
			stopService(mIt);
		}
    }

	public void onEventMainThread(J1939MsgEvent event){
		mLog.add(event.toString());
	}

	@Override
	protected void onDestroy() {
		mBus.unregister(this);
		super.onDestroy();
	}
}
