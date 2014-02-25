package com.example.apkupdate;

import java.util.List;

import com.kitsp.widgetsp.MessageBoxSp;

import android.app.Activity;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;

public class MainActivity extends Activity {

	private UpdateLoader _updateList = null;
	private Button _checkUpdate_button = null;
	private ListView _updateItems_listView = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Init();
	}

	private void Init() {
		InitParams();
		FetchUIControls();
		BindingEvents();
	}

	private void InitParams() {
		_updateList = new UpdateLoader();

	}

	private void FetchUIControls() {

		_checkUpdate_button = (Button) findViewById(R.id.activity_main_checkUpdate);
		_updateItems_listView = (ListView) findViewById(R.id.activity_main_updateItems);
	}

	private void BindingEvents() {
		if (_checkUpdate_button == null) {
			return;
		}

		_checkUpdate_button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (_updateList == null) {
					return;
				}
				try {
					
					List<UpdateItem> updateItems = _updateList.AnalyUpdate();
					ShowUpdateItems(updateItems);

				} catch (Exception e) {
					MessageBoxSp.Show(MainActivity.this, "No Update");
				}
			}
		});

	}

	private void ShowUpdateItems(List<UpdateItem> updateItems) {
		if (_updateItems_listView == null) {
			return;
		}

		_updateItems_listView.setAdapter(new UpdateItemsAdapter(updateItems,
				MainActivity.this));

	}

}
