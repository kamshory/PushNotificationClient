package com.planetbiru.pushclient.application;

import com.planetbiru.pushclient.notification.Notification;
import com.planetbiru.pushclient.notification.RemoteMessage;

/**
 * Notif class derived from Notification
 * <p>Here you can override some methods to get data from the server.</p>
 * @author user
 *
 */
public class Notif extends Notification
{
	/**
	 * Default constructor
	 */
	public Notif()
	{
		super();
	}
	/**
	 * Constructor with API key, API password, device ID and group key
	 * @param apiKey API key of the push notification
	 * @param password API password of the push notification
	 * @param deviceID Device ID
	 * @param groupKey Group key of the push notification
	 */
	public Notif(String apiKey, String password, String deviceID, String groupKey)
	{
		super(apiKey, password, deviceID, groupKey);
	}
	/**
	 * Constructor with API key, API password, device ID, group key, server address and server port
	 * @param apiKey API key of the push notification
	 * @param password API password of the push notification
	 * @param deviceID Device ID
	 * @param groupKey Group key of the push notification
	 * @param serverAddress Server address of the push notification
	 * @param serverPort Server port of the push notification
	 */
	public Notif(String apiKey, String password, String deviceID, String groupKey, String serverAddress, int serverPort)
	{
		super(apiKey, password, deviceID, groupKey, serverAddress, serverPort);
	}
	/**
	 * Constructor with API key, API password, device ID, group key, server address and server port
	 * @param apiKey API key of the push notification
	 * @param password API password of the push notification
	 * @param deviceID Device ID
	 * @param groupKey Group key of the push notification
	 * @param serverAddress Server address of the push notification
	 * @param serverPort Server port of the push notification
	 * @param ssl SSL connection
	 */
	public Notif(String apiKey, String password, String deviceID, String groupKey, String serverAddress, int serverPort, boolean ssl)
	{
		super(apiKey, password, deviceID, groupKey, serverAddress, serverPort, ssl);
	}
	public void onNotificationReceived(RemoteMessage notification)
	{
		// TODO: Add your code here to insert message into local database
		
		System.out.println("\r\n---------------------------------------------\r\n");
		
		System.out.println("Content Type = "+notification.contentType);		
		System.out.println("ID           = "+notification.notification.id);
		System.out.println("TYPE         = "+notification.notification.type);
		System.out.println("TITLE        = "+notification.notification.title);
		System.out.println("SUBTITLE     = "+notification.notification.subtitle);
		System.out.println("CLICK ACTION = "+notification.notification.clickAction);
		System.out.println("URL          = "+notification.notification.uri.toString());
		System.out.println("ICON         = "+notification.notification.icon);
		System.out.println("LARGE ICON   = "+notification.notification.largeIcon);
		System.out.println("MESSAGE      = "+notification.notification.body);
		System.out.println("MISC DATA    = "+notification.notification.miscData);
		System.out.println("VIBRATE      = "+notification.notification.vibrate);
		System.out.println("SOUND        = "+notification.notification.sound);
		System.out.println("BADGE        = "+notification.notification.badge);
		System.out.println("COLOR        = "+notification.notification.color);
		System.out.println("TIME         = "+notification.notification.time);
		System.out.println("TIME ZONE    = "+notification.notification.timeZone);		
	}
	public void onNotificationDeleted(String notificationID)
	{
		// TODO: Add your code here to delete message from local database
		System.out.println("DELETE Notification "+notificationID);
	}
	public void onDataReceived(String[] headers, String command, String body)
	{
	}
	public void onDataSent(String[] headers, String command, String body)
	{
	}
	public void onNewToken(String token, String time, long waitToNext, int timeZone)
	{
		// TODO: Add your code here when token is changed
		System.out.println("\r\n---------------------------------------------\r\n");
		System.out.println("Receive New Token : "+token);
		System.out.println("Time              : "+time);
		System.out.println("Next Token After  : "+waitToNext);
		System.out.println("Time Zone         : "+timeZone);
	}
	public void onChangeSetting(String name, String type, Object value)
	{
		// TODO: Add your code here when setting was changed
		/**
		 * The name of setting can be:
		 * timeout
		 * delayReconnect
		 * delayRestart
		 */
	}
	public void onError(Exception exception)
	{
		// TODO: Add your code here to get error message
		//System.out.println(exception.getMessage());
		//exception.printStackTrace();
	}
	public void onRegisterDeviceSendSuccess(String deviceID, String message)
	{
		// TODO: Add your code here when registration device data sent
	}
	public void onRegisterDeviceSendError(String deviceID, String message, String cause)
	{
		// TODO: Add your code here when registration device data can not be sent
	}
	public void onUnregisterDeviceSendSuccess(String deviceID, String message)
	{
		// TODO: Add your code here when unregistration device data sent
	}
	public void onUnregisterDeviceSendError(String deviceID, String message, String cause)
	{
		// TODO: Add your code here when unregistration device data can not be sent
	}
	public void onRegisterDeviceSuccess(String deviceID, int responseCode, String message)
	{
		// TODO: Add your code here when register device is success
	}
	public void onRegisterDeviceError(String deviceID, int responseCode, String message, String cause)
	{
		// TODO: Add your code here when register device is error
		System.out.println("DEVICE ID         : "+deviceID);
		System.out.println("RESPONSE CODE     : "+responseCode);
		System.out.println("MESSAGE           : "+message);
		System.out.println("CAUSE             : "+cause);
	}
	public void onUnregisterDeviceSuccess(String deviceID, int responseCode, String message)
	{
		// TODO: Add your code here when unregister device is success
	}
	public void onUnregisterDeviceError(String deviceID, int responseCode, String message, String cause)
	{
		// TODO: Add your code here when unregister device is error
	}

}
