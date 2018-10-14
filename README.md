# PushNotificationClient

## Push Notification

Push Notification is a notification that is forcibly sent by the server to the client so that the notification sent to the client without waiting for the client to request it. In order for the notification to be accepted by the client, the client and server must always be connected through socket communication.

The notification server can be part of the application server and can also be provided by third parties.

The application server must know the device ID of each user. When the application server sends notifications to users, the application server sends notifications to the notification server that is addressed to the user's device.

**Notification Flow**
1. Every device that will receive notifications must register the device ID either manually by the user or automatically when the application is first run.
2. Each notification will be sent to the device according to the destination device ID that has been registered previously according to the API key of the notification.
3. The application server cannot send notifications to devices that have not been registered according to the API key of the application.
4. If two or more applications have the same API key and the same device ID is connected to the server when the server sends a notification, the two applications will get the same notification. Notifications will be marked as "sent" after the notification has been successfully sent to the first application.
5. If the connection between the application and the server is disconnected and realized by the server, then the server will discard the connection from the list and if there is no application connected to the server for notifications sent, then the server only stores notifications without sending notifications to the receiving device and will not mark notifications as "sent".
6. If an error occurs when sending a notification, the server will disconnect and discard the connection but will not mark the notification as "sent" if no application actually receives the notification, then the server only stores notifications without sending notifications to the receiving device and will not mark notifications as "sent".
7. When the connection is lost, either forced by the server or due to an error, the application will make a new connection to the server.
8. When the application is connected to the server, the server will checks whether there are notifications that have not been sent or not and whether there is a history of deletion notifications that have not been sent or not. If there is a notification that has not been sent, the server will send the notification and will mark the notification as: "sent". If there is a history of deletion of notifications that have not been sent, the server will send a history of deletion of the notification and will mark the deletion history of the notification as: "sent".

To get push notification, developers can create their own program code and can also use the Application Programming Interface (API) provided by third parties. If the developer wants to build their own notification server, the following API can be used to get push notifications by integrating it with a mobile application.

## Security

Server will check each connection to server with two step authentication.

**Two Steps Authentication**
1. Client send request connection to server with API key, password and device ID (Step 1)
2. Server send question to client
3. Client send answer to server (Step 2)
4. Server check the answer
5. If answer is valid, server send token to client
6. If answer is invalid, server close the connection

After server validate the client, server will evaluate the client every 60 minutes. Client will receive new token. The purpose of the evaluation is to check whether the client is still connected to the server or not.

**Evaluation**
1. Server send question to client
2. Client send answer to server
3. Server check the answer
4. If answer is valid, server send new token to client
5. If answer is invalid, server close the connection

## Using Push Notification Client API

To use the PushNotificationClient API, the developer must first create a class derived from the Notification class.

```java
public class Notif extends Notification
{
	public Notif()
	{
		super();
	}
	public Notif(String apiKey, String password, String deviceID, long interval)
	{
		super(apiKey, password, deviceID, interval);
	}
	public Notif(String apiKey, String password, String deviceID, long interval, String serverAddress, int serverPort)
	{
		super(apiKey, password, deviceID, interval, serverAddress, serverPort);
	}
	public void onMessageReceived(RemoteMessage message)
	{
		// TODO: Add your code here to insert message into local database
	}
	public void onNewToken(String token, String time, int timeZone)
	{
		// TODO: Add your code here when token is changed
	}
	public void onDeletedMessages(String messageID)
	{
		// TODO: Add your code here to delete message from local database
	}
	public void onError(Exception e)
	{
		// TODO: Add your code here to get error message
	}
	public void onRegisterDeviceSentSuccess(String deviceID, String message)
	{
		// TODO: Add your code here when registration device data sent
	}
	public void onRegisterDeviceSentError(String deviceID, String message, String cause)
	{
		// TODO: Add your code here when registration device data can not be sent
	}
	public void onUnregisterDeviceSentSuccess(String deviceID, String message)
	{
		// TODO: Add your code here when unregistration device data sent
	}
	public void onUnregisterDeviceSentError(String deviceID, String message, String cause)
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
```

You can receive notifications in a synchronous and asynchronous process.

## Synchronous Notification

To receive notifications synchronously, create an object from the Notification class that you previously created.

```java
Notif notif = new Notif();
notif.apiKey = "Your-API-Key";
notif.password = "Your-Password";
notif.deviceID = "Device-ID";
notif.interval = 5000;
notif.connect();
notif.registerDevice("Device-ID");
notif.start();
```

## Asynchronous Notification

To receive notifications asynchronously, create an object from the Notification class that you created earlier. Then create a class derived from the Thread class.

```java
public class AsyncNotif extends Thread
{
	public AsyncNotif()
	{
		
	}
	public String apiKey;
	public String password;
	public String deviceID;
	public long interval;
	public AsyncNotif(String apiKey, String hashPassword, String deviceID, long interval)
	{
		this.apiKey = apiKey;
		this.password = hashPassword;
		this.deviceID = deviceID;
		this.interval = interval;
	}
	public void run()
	{
		Notif notif = new Notif();
		notif.apiKey = this.apiKey;
		notif.deviceID = this.deviceID;
		notif.password = this.password;
		notif.interval = this.interval;
		notif.connect();
		notif.registerDevice(this.deviceID);
		notif.start();				
	}
}
```

Create objects from the notif class to run the thread.

```java
System.out.println("Here your code before start notification!");	
AsyncNotif asyncNotif = new AsyncNotif("Your-API-Key", "Your-Password", "Device-ID", 10000);
asyncNotif.start();
System.out.println("Here your code after start notification!");		
```

## Override Methods

You can override methods from interface Request. The methods are

```java
public void onMessageReceived(RemoteMessage message);
public void onDeletedMessages(String messageID);
public void onNewToken(String token, String time, int timeZone);
public void onError(Exception exception);
public void onRegisterDeviceSentSuccess(String deviceID, String message);
public void onRegisterDeviceSentError(String deviceID, String message, String cause);
public void onUnregisterDeviceSentSuccess(String deviceID, String message);
public void onUnregisterDeviceSentError(String deviceID, String message, String cause);
public void onRegisterDeviceSuccess(String deviceID, String message);
public void onRegisterDeviceError(String deviceID, String message, String cause);
public void onUnregisterDeviceSuccess(String deviceID, String message);
public void onUnregisterDeviceError(String deviceID, String message, String cause);
```

### onMessageReceived

This method invoked when device receiving message from notification server. message is object of RemoteMessage. This object contains notification created form Notification.

#### Example

```java
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
	System.out.println("TIME         = "+message.notification.time);
	System.out.println("TIME ZONE    = "+message.notification.timeZone);
}
```	

time is local time when notification was sent. timezone is time zone offset of the server. You must convert server time to device time using the time zone offset.

miscData contains additional data. The miscData format is plaintext by encoding JSON, XML, base 64, or hexadecimal binary data.'

### onDeletedMessages

**onDeletedMessages** is invoked when the notification server deletes notifications that have been sent to the device. The notification server notifies the device that the notification is deleted by the server on the sender's order. The mobile application can delete notifications that have been received if the sending application wants the data to be deleted.

To delete notifications on the mobile application, override **onDeleteMessages** method in the Notification class.

```java
public void onDeletedMessages(String messageID)
{
	// TODO: Add your code here to delete message from local database
}
```

### onNewToken

**onNewToken** is invoked when the notification server sent new token.

```java
public void onNewToken(String token, String time, int timeZone)
{
	// TODO: Add your code here 
}
```

token is new token sent from notification server. time is local time when token was sent. timezone is time zone offset of the server. You must convert server time to device time using the time zone offset.

### onError

**onError** is invoked when error ocuured on API.

```java
public void onError(Exception exception);
{
	// TODO: Add your code here to get error message
	System.out.println(exception.getMessage());
}
```

exception is an error exception thrown from where error occurred. Developer can get error message or invoke printStackTrace method.

### onRegisterDeviceSendSuccess

**onRegisterDeviceSendSuccess** is invoked on device registration when has been sent to notification server successfully. This method does not guarantee the device can be registered successfully. The server will tell whether this device can be registered or not.

```java
public void onRegisterDeviceSendSuccess(String deviceID, String message)
{
	// TODO: Add your code here when registration device data sent
}
```

If the API receives notification of the results of device registration from the server, the API will invoke the **onRegisterDeviceSuccess** method if successful or **onRegisterDeviceError** if it fails.

### onRegisterDeviceSendError

**onRegisterDeviceSendError** is invoked when error occurred while registration device is processed before data sent to the notification server.

```java
public void onRegisterDeviceSendError(String deviceID, String message, String cause)
{
	// TODO: Add your code here when registration device data can not be sent
}
```

### onUnregisterDeviceSendSuccess

**onUnregisterDeviceSendSuccess** is invoked on device unregistration when has been sent to notification server successfully. This method does not guarantee the device can be unregistered successfully. The server will tell whether this device can be unregistered or not.

```java
public void onUnregisterDeviceSendSuccess(String deviceID, String message)
{
	// TODO: Add your code here when unregistration device data sent
}
```

If the API receives notification of the results of device unregistration from the server, the API will invoke the **onUnregisterDeviceSuccess** method if successful or **onUnregisterDeviceError** if it fails.

### onUnregisterDeviceSendError

**onUnregisterDeviceSendError** is invoked when error occurred while unregistration device is processed before data sent to the notification server.

```java
public void onUnregisterDeviceSendError(String deviceID, String message, String cause)
{
	// TODO: Add your code here when unregistration device data can not be sent
}
```

### onRegisterDeviceSuccess

**onRegisterDeviceSuccess** is invoked when API receive information from the server that device registration is success.

```java
public void onRegisterDeviceSuccess(String deviceID, String message)
{
	// TODO: Add your code here when register device is success
}
```

### onRegisterDeviceError

**onRegisterDeviceError** is invoked when API receive information from the server that device registration is failed.

```java
public void onRegisterDeviceError(String deviceID, String message, String cause)
{
	// TODO: Add your code here when register device is error
	System.out.println(message+". "+cause);
}
```

### onUnregisterDeviceSuccess

**onUnregisterDeviceSuccess** is invoked when API receive information from the server that device unregistration is success.

```java
public void onUnregisterDeviceSuccess(String deviceID, String message)
{
	// TODO: Add your code here when register device is success
}
```

### onUnregisterDeviceError

**onUnregisterDeviceError** is invoked when API receive information from the server that device unregistration is failed.

```java
public void onUnregisterDeviceError(String deviceID, String message, String cause)
{
	// TODO: Add your code here when register device is error
	System.out.println(message+". "+cause);
}
```
