package com.kitsp.contentsp;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class IntentSp {

	/**
	 * 
	 * @param activity
	 * @param isSaveActivityToHistory
	 *            true:save activity to history.System may back to the activity
	 *            when other activity finish. false:no save.
	 */
	public static void RestartActivity(Activity activity,
			boolean isSaveActivityToHistory) {
		if (activity == null) {
			return;
		}
		Intent intent = new Intent();
		String packageName = activity.getPackageName();
		String className = activity.getLocalClassName();
		String componentClassName = packageName + "." + className;
		if (className != null && className.split(".").length > 0) {
			componentClassName = className;
		}
		ComponentName componentName = new ComponentName(packageName,
				componentClassName);

		intent.setComponent(componentName);
		if (!isSaveActivityToHistory) {
			intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
		}
		activity.startActivity(intent);
		activity.finish();
		return;
	}

	/**
	 * 
	 * @param context
	 * @param cls
	 * @param isSaveActivityToHistory
	 *            true:save activity to history.System may back to the activity
	 *            when other activity finish. false:no save.
	 */
	public static void StartActivity(Context context, Class<?> cls,
			boolean isSaveActivityToHistory) {
		if (context == null || cls == null) {
			return;
		}

		Intent intent = new Intent();
		if (!isSaveActivityToHistory) {
			intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
		}
		intent.setClass(context, cls);
		context.startActivity(intent);
	}

	/**
	 * 
	 * @param context
	 * @param action
	 * @param isSaveActivityToHistory
	 *            true:save activity to history.System may back to the activity
	 *            when other activity finish. false:no save.
	 */
	public static void StartActivity(Context context, String action,
			boolean isSaveActivityToHistory) {
		if (context == null || action == null) {
			return;
		}

		Intent intent = new Intent(action);
		if (!isSaveActivityToHistory) {
			intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
		}
		context.startActivity(intent);
	}

	/**
	 * 
	 * @param context
	 * @param packageName
	 * @param className
	 * @param isSaveActivityToHistory
	 *            true:save activity to history.System may back to the activity
	 *            when other activity finish. false:no save.
	 */
	public static void StartActivity(Context context, String packageName,
			String className, boolean isSaveActivityToHistory) {
		if (context == null) {
			return;
		}

		if (packageName == null || packageName == "") {
			return;
		}

		if (className == null || className == "") {
			return;
		}

		Intent intent = new Intent();
		if (!isSaveActivityToHistory) {
			intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
		}
		ComponentName cn = new ComponentName(packageName, className);
		if (cn != null) {
			intent.setComponent(cn);
			context.startActivity(intent);
		}
	}

	public static void StartActivity(Context context, Uri data, String type,
			boolean isSaveActivityToHistory) {
		if (context == null) {
			return;
		}
		
		if(data==null)
		{
			return;
		}
		
		if(type==null||type.length()<=0)
		{
			return;
		}

		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setDataAndType(data, type);
		if (!isSaveActivityToHistory) {
			intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
		}
		context.startActivity(intent);
	}

}
