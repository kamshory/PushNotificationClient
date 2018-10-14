package com.planetbiru.pushclient.application;

public class AsyncNotif extends Thread
{
	Notif notif = new Notif();
	public String apiKey;
	public String password;
	public String deviceID;
	public long interval;
	public boolean registerDevice = false;
	public boolean unregisterDevice = false;
	public AsyncNotif()
	{
		
	}
	public AsyncNotif(String apiKey, String hashPassword, String deviceID)
	{
		this.notif = new Notif(apiKey, password, deviceID);
	}
	public AsyncNotif(String apiKey, String password, String deviceID, String serverAddress, int serverPort)
	{
		this.notif = new Notif(apiKey, password, deviceID, serverAddress, serverPort);
	}
	public AsyncNotif(String apiKey, String password, String deviceID, String serverAddress, int serverPort, int timeout)
	{
		this.notif = new Notif(apiKey, password, deviceID, serverAddress, serverPort);
		this.notif.setTimeout(timeout);
	}
	public void setDebugMode(boolean debugMode)
	{
		this.setDebugMode(debugMode);
	}
	public void run()
	{
		notif.connect();
		if(this.registerDevice)
		{
			notif.registerDevice(this.notif.deviceID);
		}
		if(this.unregisterDevice )
		{
			notif.registerDevice(this.notif.deviceID);
		}
		notif.start();				
	}
	public void registerDevice(String deviceID) 
	{
		this.registerDevice = true;
	}
	public void unregisterDevice(String deviceID) 
	{
		this.unregisterDevice = true;
	}
}
