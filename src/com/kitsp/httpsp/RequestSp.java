package com.kitsp.httpsp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class RequestSp {
	private final static int HTTP_200 = 200;
	private static ConcurrentHashMap<String, Boolean> _asyncDownloadFlags = new ConcurrentHashMap<String, Boolean>();

	public static InputStream Get(String url) throws Exception {

		HttpEntity httpEntity = GetHttpEntity(url);
		if (httpEntity == null) {
			return null;
		}

		return httpEntity.getContent();
	}

	public static HttpEntity GetHttpEntity(String url) throws Exception {

	
		HttpGet httpGet = new HttpGet(url);

		HttpClient httpClient = new DefaultHttpClient();

		HttpResponse httpResp = httpClient.execute(httpGet);


		if (httpResp.getStatusLine().getStatusCode() == HTTP_200) {
			//Get back data.
			// String result = EntityUtils.toString(httpResp.getEntity(),
			// "UTF-8");
			// return result;
			return httpResp.getEntity();
		} else {
			return null;
		}

	}

	public static boolean DownLoadFile(String httpUrl, String savePath) {

		final File file = new File(savePath);

		try {
			URL url = new URL(httpUrl);
			try {
				HttpURLConnection conn = (HttpURLConnection) url
						.openConnection();

				if (conn.getResponseCode() >= 400) {
					return false;
				}

				InputStream is = conn.getInputStream();
				FileOutputStream fos = new FileOutputStream(file);
				long length = conn.getContentLength();
				byte[] buf = new byte[1024];
				conn.connect();
				int readCount = 0;
				while (true) {

					if (is == null) {
						break;
					}

					readCount = is.read(buf);

					if (readCount <= 0) {
						break;
					}

					fos.write(buf, 0, readCount);
				}

				conn.disconnect();
				fos.close();
				is.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * 
	 * @param httpUrl
	 * @param savePath
	 * @param handler
	 *            :Async handler
	 * @return Handler:Control thread in outer.
	 */
	public static String DownLoadFileAsync(final String httpUrl,
			final String savePath, final Handler handler) {

		if (handler == null) {
			return null;
		}

		final String threadName = UUID.randomUUID().toString();
		Thread downloadThread = new Thread(new Runnable() {
			@Override
			public void run() {
				DownloadDataAsync(httpUrl, savePath, handler, threadName);
			}
		});
		downloadThread.setName(threadName);
		_asyncDownloadFlags.put(threadName, true);
		downloadThread.start();
		return threadName;
	}

	public static void AbortAsyncDownload(String asyncDownloadThreadName) {
		if (asyncDownloadThreadName == null
				|| asyncDownloadThreadName.length() <= 0) {
			return;
		}

		_asyncDownloadFlags.remove(asyncDownloadThreadName);
	}

	private static void DownloadDataAsync(String httpUrl,
			final String savePath, final Handler handler,
			final String threadName) {
		File file = new File(savePath);

		HttpURLConnection conn;
		try {
			final URL url = new URL(httpUrl);
			conn = (HttpURLConnection) url.openConnection();

			if (conn.getResponseCode() >= 400) {
				handler.sendEmptyMessage(REQUEST_MESSAGES.DOWNLOAD_EXCEPTION);
				return;
			}
			InputStream is = conn.getInputStream();
			FileOutputStream fos = new FileOutputStream(file);
			long totalCount = conn.getContentLength();
			byte[] buf = new byte[1024];
			conn.connect();
			int readCount = 0;
			int downloadedCount = 0;
			float percent = 0;
			Message msg = null;
			Bundle bundle = null;
			handler.sendEmptyMessage(REQUEST_MESSAGES.DOWNLOAD_START);
		
			while (true) {

				if(_asyncDownloadFlags.isEmpty()){
					break;
				}
				
				if(!_asyncDownloadFlags.get(threadName)){
					break;
				}
				
				if (is == null) {
					break;
				}
				
				readCount = is.read(buf);
				downloadedCount += readCount;
				percent = (float) (downloadedCount * 1.0 / totalCount * 100);
				msg = new Message();
				msg.what = REQUEST_MESSAGES.DOWNLOAD_PERCENT;
				bundle = new Bundle();
				bundle.putFloat(REQUEST_KEYS.DOWNLOAD_PERCENT, percent);
				msg.setData(bundle);
				handler.sendMessage(msg);

				if (readCount <= 0) {
					break;
				}

				fos.write(buf, 0, readCount);
			}

			conn.disconnect();
			fos.close();
			is.close();

			msg = new Message();
			msg.what = REQUEST_MESSAGES.DOWNLOAD_COMPLETED;
			bundle = new Bundle();
			bundle.putString(REQUEST_KEYS.DOWNLOAD_SAVE_PATH, savePath);
			msg.setData(bundle);
			handler.sendMessage(msg);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			handler.sendEmptyMessage(REQUEST_MESSAGES.DOWNLOAD_EXCEPTION);
			return;
		}
	}
}
