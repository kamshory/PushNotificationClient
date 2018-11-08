package com.planetbiru.pushclient.application;

import com.planetbiru.pushclient.utility.HTTPResponse;

/**
 * Asynchronous notification
 * <p>This class is useful to get asynchronous notification. So that the application can run normally hanged.</p>
 * @author Kamshory, MT
 *
 */
public class AsyncNotif extends Thread
{
	Notif notif = new Notif();
	public String apiKey;
	public String password;
	public String deviceID;
	public long interval;
	public boolean registerDevice = false;
	public boolean unregisterDevice = false;
	/**
	 * Constructor
	 * @param apiKey API key of the push notification
	 * @param password API password of the push notification
	 * @param deviceID Device ID
	 * @param groupKey Group key of the push notification
	 */
	public AsyncNotif(String apiKey, String password, String deviceID, String groupKey)
	{
		this.notif = new Notif(apiKey, password, deviceID, groupKey);
		this.notif.connect();
	}
	/**
	 * Constructor
	 * @param apiKey API key of the push notification
	 * @param password API password of the push notification
	 * @param deviceID Device ID
	 * @param groupKey Group key of the push notification
	 * @param serverAddress Server address of the push notification
	 * @param serverPort Server port of the push notification
	 */
	public AsyncNotif(String apiKey, String password, String deviceID, String groupKey, String serverAddress, int serverPort)
	{
		this.notif = new Notif(apiKey, password, deviceID, groupKey, serverAddress, serverPort);
		this.notif.connect();
	}
	/**
	 * Constructor
	 * @param apiKey API key of the push notification
	 * @param password API password of the push notification
	 * @param deviceID Device ID
	 * @param groupKey Group key of the push notification
	 * @param serverAddress Server address of the push notification
	 * @param serverPort Server port of the push notification
	 * @param timeout Timeout of the push notification
	 */
	public AsyncNotif(String apiKey, String password, String deviceID, String groupKey, String serverAddress, int serverPort, int timeout)
	{
		this.notif = new Notif(apiKey, password, deviceID, groupKey, serverAddress, serverPort);
		this.notif.setTimeout(timeout);
		this.notif.connect();
	}
	/**
	 * Set debug mode
	 * @param debugMode Debug mode
	 */
	public void setDebugMode(boolean debugMode)
	{
		this.setDebugMode(debugMode);
	}
	/**
	 * Override run method
	 */
	public void run()
	{		
		this.notif.start();				
	}
	/**
	 * Register device ID to the push server
	 * @param deviceID Device ID
	 */
	public void registerDevice(String deviceID) 
	{
		this.notif.registerDevice(deviceID);
	}
	/**
	 * Unregister device from the push server
	 * @param deviceID Device ID
	 */
	public void unregisterDevice(String deviceID) 
	{
		this.notif.registerDevice(deviceID);
	}
	/**
	 * Register device to application server
	 * @param url URL of the application server
	 * @param deviceID Device ID
	 * @param cookie Cookie
	 * @return HTTPResponse contains server response
	 */
	public HTTPResponse registerDeviceApps(String url, String deviceID, String cookie) 
	{
		return this.notif.registerDeviceApps(url, deviceID, cookie, "", "", "");
	}
	/**
	 * Register device to application server
	 * @param url URL of the application server
	 * @param deviceID Device ID
	 * @param cookie Cookie
	 * @param userID User ID
	 * @param password User password
	 * @param group User group
	 * @return HTTPResponse contains server response
	 */
	public HTTPResponse registerDeviceApps(String url, String deviceID, String cookie, String userID, String password, String group) 
	{
		return this.notif.registerDeviceApps(url, deviceID, cookie, userID, password, group);
	}
	/**
	 * Register device to application server
	 * @param url URL of the application server
	 * @param cookie Cookie
	 * @param userID User ID
	 * @param password User password
	 * @param group User group
	 * @param deviceID Device ID
	 * @return HTTPResponse contains server response
	 */
	public HTTPResponse unregisterDeviceApps(String url, String cookie, String userID, String password, String group, String deviceID) 
	{
		return this.notif.unregisterDeviceApps(url, cookie, userID, password, group, deviceID);
	}
	/**
	 * Check user authentication without user ID, password and group. The server will use session data saved according to cookie from the client.
	 * @param url URL of the application server
	 * @param cookie Cookie
	 * @return HTTPResponse contains header, body and cookie
	 */
	public HTTPResponse auth(String url, String cookie) 
	{
		return this.notif.auth(url, cookie);
	}
	/**
	 * Check user authentication with user ID, password and group
	 * @param url URL of the application server
	 * @param cookie Cookie
	 * @param userID User ID
	 * @param password Password
	 * @param group Group
	 * @return HTTPResponse contains header, body and cookie
	 */
	public HTTPResponse login(String url, String cookie, String userID, String password, String group) 
	{
		return this.notif.login(url, cookie, userID, password, group);
	}
	/**
	 * Check user authentication with user ID, password and group
	 * @param url URL of the application server
	 * @param cookie Cookie
	 * @return HTTPResponse contains header, body and cookie
	 */
	public HTTPResponse logout(String url, String cookie) 
	{
		return this.notif.logout(url, cookie);
	}
}
