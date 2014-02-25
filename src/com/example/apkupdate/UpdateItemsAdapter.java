package com.example.apkupdate;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.kitsp.contentsp.IntentSp;
import com.kitsp.httpsp.REQUEST_KEYS;
import com.kitsp.httpsp.REQUEST_MESSAGES;
import com.kitsp.httpsp.RequestSp;
import com.kitsp.widgetsp.MessageBoxSp;

public class UpdateItemsAdapter extends BaseAdapter {
	private List<UpdateItem> _updateItems = null;
	private Context _context = null;
	private CopyOnWriteArrayList<String> _aysncDownloadThreadNames=null;

	public UpdateItemsAdapter(List<UpdateItem> updateItems, Context context) {
		_updateItems = updateItems;
		_context = context;
		_aysncDownloadThreadNames=new CopyOnWriteArrayList<String>();
	}
	
	@Override
	protected void finalize() throws Throwable {
		// TODO Auto-generated method stub
		super.finalize();
		if (_aysncDownloadThreadNames == null
				|| _aysncDownloadThreadNames.size() <= 0) {
			return;
		}

		while (_aysncDownloadThreadNames.size() > 0) {
			String asyncDownloadThreadName = _aysncDownloadThreadNames.get(0);
			RequestSp.AbortAsyncDownload(asyncDownloadThreadName);
			_aysncDownloadThreadNames.remove(0);
		}
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		if (_updateItems.isEmpty()) {
			return 0;
		}
		return _updateItems.size();
	}

	@Override
	public Object getItem(int position) {
		if (_updateItems.isEmpty()) {
			return null;
		}
		return _updateItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		if (_updateItems.isEmpty()) {
			return 0;
		}
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (_context == null) {
			return null;
		}

		UpdateItem updateItem = (UpdateItem) getItem(position);
		if (updateItem == null) {
			return null;
		}

		View updateItemLayout = View.inflate(_context, R.layout.update_item,
				null);
		if (updateItemLayout == null) {
			return null;
		}

		ImageView app_imageView = (ImageView) updateItemLayout
				.findViewById(R.id.update_item_app_image);
		TextView appName_textView = (TextView) updateItemLayout
				.findViewById(R.id.update_item_app_name);
		TextView appOldVersion_textView = (TextView) updateItemLayout
				.findViewById(R.id.update_item_app_old_version);
		TextView appNewVersion_textView = (TextView) updateItemLayout
				.findViewById(R.id.update_item_new_version);
		final Button behavior_button = (Button) updateItemLayout
				.findViewById(R.id.update_item_behavior);
	
		String oldVersion=FetchPackageVersion(updateItem.GetFeaturePackage());
		if(oldVersion!=null&&oldVersion.length()>0)
		{
			updateItem.SetOldVersion(oldVersion);
		}
		
		appName_textView.setText(updateItem.GetName());		
		appOldVersion_textView.setText(updateItem.GetOldVersion());
		appNewVersion_textView.setText(updateItem.GetNewVersion());
		boolean isNewVersion = IsNewVersion(updateItem.GetOldVersion(),
				updateItem.GetNewVersion());

		updateItem.SetBehavior(isNewVersion ? UPDATE_BEHAVIORS.UPDATE
				: UPDATE_BEHAVIORS.NO_UPDATE);

		behavior_button.setEnabled(isNewVersion);
		behavior_button.setText(updateItem.GetBehavior());
		behavior_button.setTag(updateItem);

		behavior_button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ExecuteBehavior(behavior_button);
			}
		});
		return updateItemLayout;
	}
	
	private String FetchPackageVersion(String packageName) {

		if (packageName == null || packageName.length() <= 0) {
			return null;
		}

		List<PackageInfo> packages = _context.getPackageManager()
				.getInstalledPackages(0);
		for (int i = 0; i < packages.size(); i++) {
			PackageInfo packageInfo = packages.get(i);
			if (packageInfo.packageName.equals(packageName)) {
				return packageInfo.versionName;
			}
		}

		return null;
	}

	private boolean IsNewVersion(String oldVersion, String newVersion) {
		if (newVersion == null || newVersion.length() <= 0) {
			return false;
		}

		if (oldVersion == null || oldVersion.length() <= 0
				|| oldVersion.equals(UPDATE_BEHAVIORS.NOT_INSTALL)) {
			return true;
		}

		String[] olds = oldVersion.split("\\.");
		String[] news = newVersion.split("\\.");

		int compareLength = (olds.length > news.length) ? news.length
				: olds.length;

		for (int index = 0; index < compareLength; index++) {
			if (Integer.parseInt(olds[index]) > Integer.parseInt(news[index])) {
				return false;
			} else if (Integer.parseInt(olds[index])< Integer
					.parseInt(news[index])) {
				return true;
			}
		}

		if (news.length > olds.length) {
			return true;
		}
		return false;
	}

	private void ExecuteBehavior(final Button behavior_button) {
		try {

			UpdateItem updateItem = (UpdateItem) behavior_button.getTag();
			if (updateItem == null) {
				return;
			}

			if (updateItem.GetBehavior() == UPDATE_BEHAVIORS.INSTALL) {
				if (updateItem.GetSavePath() == null
						|| updateItem.GetSavePath().length() <= 0) {
					return;
				}
				InstallApk(updateItem.GetSavePath());
				return;
			} else if (updateItem.GetBehavior() == UPDATE_BEHAVIORS.NO_UPDATE) {
				return;
			}

			final String url = updateItem.GetUrl();
			final String savePath = FetchSavePath(url);
			
			final Handler downloadHandler =InitDownloadHandler(behavior_button);

			String aysncDownloadThreadName = RequestSp.DownLoadFileAsync(url, savePath, downloadHandler);
			if (aysncDownloadThreadName != null
					&& aysncDownloadThreadName.length() > 0) {
				_aysncDownloadThreadNames.add(aysncDownloadThreadName);
			}

		} catch (Exception e) {
			behavior_button.setEnabled(true);
		}
	}

	private Handler InitDownloadHandler(final Button behavior_button)
	{
		Handler _downloadHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				UpdateItem updateItem = (UpdateItem) behavior_button
						.getTag();
				switch (msg.what) {
				case REQUEST_MESSAGES.DOWNLOAD_START: {
					behavior_button.setEnabled(false);
					break;
				}
				case REQUEST_MESSAGES.DOWNLOAD_PERCENT: {
					Bundle bundle = msg.getData();
					float downloadPercent = bundle
							.getFloat(REQUEST_KEYS.DOWNLOAD_PERCENT);
					behavior_button.setText(String.format("%1$.2f",
							downloadPercent) + "%");
					break;
				}
				case REQUEST_MESSAGES.DOWNLOAD_COMPLETED: {
					Bundle bundle = msg.getData();
					String savePath = bundle
							.getString(REQUEST_KEYS.DOWNLOAD_SAVE_PATH);
					behavior_button.setEnabled(true);
					behavior_button
							.setText(UPDATE_BEHAVIORS.INSTALL);
					if (updateItem != null) {
						updateItem.SetBehavior(UPDATE_BEHAVIORS.INSTALL);
						updateItem.SetSavePath(savePath);
					}
					break;
				}
				case REQUEST_MESSAGES.DOWNLOAD_EXCEPTION: {
					behavior_button.setEnabled(true);
					String info = "Download " + updateItem.GetUrl() + " Fail";
					MessageBoxSp.Show(_context, info);
					break;
				}
				default: {
					behavior_button.setEnabled(true);
					String info = "Download " + updateItem.GetUrl() + " Fail";
					MessageBoxSp.Show(_context, info);
					break;
				}

				}
				behavior_button.setTag(updateItem);
			}
		};
		
		return _downloadHandler;
	}
	
	
	private String FetchSavePath(String url) {

		String saveDir = Environment.getExternalStorageDirectory()
				+ "/download/";
		File saveDirfile = new File(saveDir);

		if (!saveDirfile.exists()) {
			saveDirfile.mkdirs();
		}

		int fileNameStart = url.lastIndexOf("/");
		String fileName = url.substring(fileNameStart + 1);

		return saveDir + fileName;
	}

	private void InstallApk(String filePath) {

		IntentSp.StartActivity(_context, Uri.fromFile(new File(filePath)),
				"application/vnd.android.package-archive", false);
	}

}
