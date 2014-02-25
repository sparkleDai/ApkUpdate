package com.example.apkupdate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.kitsp.httpsp.RequestSp;

public class UpdateLoader {

	private String _updateUrl = "http://192.168.1.5:9000/update.json";

	@SuppressWarnings("finally")
	public List<UpdateItem> AnalyUpdate() {
		List<UpdateItem> updateItems=null;
		try {
			HttpEntity responseEntity = RequestSp.GetHttpEntity(_updateUrl);
			if(responseEntity==null||responseEntity.getContentLength()<=0)
			{
				return null;				
			}
			
			String responseStr = EntityUtils.toString(responseEntity, "UTF-8");
			if(responseStr==null||responseStr.length()<=0)
			{
				return null;				
			}
			
			JSONArray jsonObjs = new JSONObject(responseStr).getJSONArray(UPDATE_KEYS.ITEMS);
			if(jsonObjs==null||jsonObjs.length()<=0)
			{
				return null;				
			}
			
			int itemCount=jsonObjs.length();
			updateItems=new ArrayList<UpdateItem>();
			for (int itemIndex = 0; itemIndex < itemCount; itemIndex++) {	
				
				JSONObject jsonObject = jsonObjs.getJSONObject(itemIndex);
				UpdateItem updateItem=new UpdateItem();	
				updateItem.SetName( jsonObject.getString(UPDATE_KEYS.NAME));
				updateItem.SetFeaturePackage(jsonObject.getString(UPDATE_KEYS.FEATURE_PACKAGE));
				updateItem.SetNewVersion(jsonObject.getString(UPDATE_KEYS.VERSION));
				updateItem.SetUrl(jsonObject.getString(UPDATE_KEYS.URL));		
				
				updateItems.add(updateItem);
			}				
			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();	
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally
		{
			return updateItems;
		}
		
	}
}
