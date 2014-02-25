package com.example.apkupdate;


public class UpdateItem {
	
	private String _name="";
	private String _featurePackage="";
	private String _oldVersion=UPDATE_BEHAVIORS.NOT_INSTALL;
	private String _newVersion="";
	private String _url="";
	private String _behavior=UPDATE_BEHAVIORS.NOT_INSTALL;
	private String _savePath="";

	
	public void SetName(String name)
	{
		_name=name;
	}
	
	public String GetName()
	{
		return _name;
	}
	
	public void SetFeaturePackage(String featurePackage)
	{
		_featurePackage=featurePackage;
	}
	
	public String GetFeaturePackage()
	{
		return _featurePackage;
	}
	
	public void SetOldVersion(String oldVersion)
	{
		_oldVersion=oldVersion;
	}
	
	public String GetOldVersion()
	{
		return _oldVersion;
	}
	
	public void SetNewVersion(String newVersion)
	{
		_newVersion=newVersion;
	}
	
	public String GetNewVersion()
	{
		return _newVersion;
	}
	
	public void SetUrl(String url)
	{
		_url=url;
	}
	
	public String GetUrl()
	{
		return _url;
	}
	
	public void SetBehavior(String behavior)
	{
		_behavior=behavior;
	}
	
	public String GetBehavior()
	{
		return _behavior;
	}
	
	public void SetSavePath(String savePath)
	{
		_savePath=savePath;
	}
	
	public String GetSavePath()
	{
		return _savePath;
	}
}
