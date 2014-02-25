package com.kitsp.widgetsp;

import android.content.Context;
import android.widget.Toast;

public class MessageBoxSp {
	
	public static void Show(Context context,String info)
	{
		Toast.makeText(context, info, Toast.LENGTH_LONG).show();
	}
	
	public static void Show(Context context,String info,int duration)
	{
		Toast.makeText(context, info, duration).show();
	}
}
