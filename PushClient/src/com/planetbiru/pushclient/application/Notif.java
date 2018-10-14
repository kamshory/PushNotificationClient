package com.planetbiru.pushclient.application;

import com.planetbiru.pushclient.notification.Notification;
import com.planetbiru.pushclient.notification.RemoteMessage;

public class Notif extends Notification
{
	public Notif()
	{
		super();
	}
	public Notif(String apiKey, String password, String deviceID)
	{
		super(apiKey, password, deviceID);
	}
	public Notif(String apiKey, String password, String deviceID, String serverAddress, int serverPort)
	{
		super(apiKey, password, deviceID, serverAddress, serverPort);
	}
	public void onMessageReceived(RemoteMessage message)
	{
		// TODO: Add your code here to insert message into local database
		
		System.out.println("\r\n---------------------------------------------\r\n");

		System.out.println("Content Type = "+message.contentType);

		System.out.println("ID           = "+message.notification.id);
		System.out.println("TYPE         = "+message.notification.type);
		System.out.println("TITLE        = "+message.notification.title);
		System.out.println("SUBTITLE     = "+message.notification.subtitle);
		System.out.println("CLICK ACTION = "+message.notification.clickAction);
		System.out.println("URL          = "+message.notification.uri.toString());
		System.out.println("ICON         = "+message.notification.icon);
		System.out.println("LARGE ICON   = "+message.notification.largeIcon);
		System.out.println("MESSAGE      = "+message.notification.body);
		System.out.println("MISC DATA    = "+message.notification.miscData);
		System.out.println("VIBRATE      = "+message.notification.vibrate);
		System.out.println("SOUND        = "+message.notification.sound);
		System.out.println("BADGE        = "+message.notification.badge);
		System.out.println("COLOR        = "+message.notification.color);
		System.out.println("TIME         = "+message.notification.time);
		System.out.println("TIME ZONE    = "+message.notification.timeZone);
		
	}
	public void onDataReceived(String[] headers, String command, String body)
	{
		//System.out.println("COMMAND : "+command);
		//System.out.println("DATA    : "+body);
	}
	public void onNewToken(String token, String time, int timeZone)
	{
		// TODO: Add your code here when token is changed
		System.out.println("Receive New Token : "+token);
		System.out.println("Time              : "+time);
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
	public void onDeletedMessages(String messageID)
	{
		// TODO: Add your code here to delete message from local database
		System.out.println("DELETE MESSAGE "+messageID);
	}
	public void onError(Exception exception)
	{
		// TODO: Add your code here to get error message
		System.out.println(exception.getMessage());
		exception.printStackTrace();
	}
	public void onRegisterDeviceSendSuccess(String deviceID, String message)
	{
		// TODO: Add your code here when registration device data sent
		System.out.println("Register send "+deviceID);
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
	public void onRegisterDeviceSuccess(String deviceID, String message)
	{
		// TODO: Add your code here when register device is success
	}
	public void onRegisterDeviceError(String deviceID, String message, String cause)
	{
		// TODO: Add your code here when register device is error
		System.out.println(message+". "+cause);
	}
	public void onUnregisterDeviceSuccess(String deviceID, String message)
	{
		// TODO: Add your code here when unregister device is success
	}
	public void onUnregisterDeviceError(String deviceID, String message, String cause)
	{
		// TODO: Add your code here when unregister device is error
	}

}
