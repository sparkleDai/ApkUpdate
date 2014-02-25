package com.kitsp.contentsp;

import java.io.IOException;
import java.io.InputStream;

public class StringSp {
	public static String InputStream2String(InputStream in) throws IOException {

		StringBuffer out = new StringBuffer();
		byte[] b = new byte[4096];
		int n;
		while ((n = in.read(b)) != -1) {
			out.append(new String(b, 0, n));
		}
		//Log.i("StringµÄ³¤¶È", new Integer(out.length()).toString());
		return out.toString();
	}
}
