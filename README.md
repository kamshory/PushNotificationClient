# PushNotificationClient

## Push Notification

Push Notification is a notification that is forcibly sent by the server to the client so that the notification sent to the client without waiting for the client to request it. In order for the notification to be accepted by the client, the client and server must always be connected through socket communication.

The notification server can be part of the application server and can also be provided by third parties.

The application server must know the device ID of each user. When the application server sends notifications to users, the application server sends notifications to the notification server that is addressed to the user's device.

## Push Notification Server

To get push notification server, please click https://github.com/kamshory/PushNotificationServer
To get push notification sender, please click https://github.com/kamshory/PushNotificationSender


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
		System.out.println("RECEIVE DATA");
		System.out.println("COMMAND : "+command);
		System.out.println("DATA    : "+body);
	}
	public void onDataSent(String[] headers, String command, String body)
	{
		System.out.println("SEND DATA");
		System.out.println("COMMAND : "+command);
		System.out.println("DATA    : "+body);
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
```

Create objects from the notif class to run the thread.

```java
public class Application 
{
	public static String cookie = "";
	public static void main(String[] args)
	{
		String deviceID = Utility.getFirst(args, "device-id").trim();
		String registerDevice = Utility.getFirst(args, "register-device").toLowerCase().trim();
		if(deviceID.length() == 0)
		{
			deviceID = "41fda1bcf6486301";
		}
		if(registerDevice.length() == 0)
		{
			registerDevice = "true";
		}

		// For notification
		String pushServerAddress = "push.example.com";
		int pushServerPort = 92;
		String apiKey = "PLANETBIRU";
		String apiPassword = "123456";
		String groupKey = "1234567890W";
		/**
		 * Group Key adalah kombinasi antara NPSN+Kode grup pengguna
		 * NPSN+W => Wali
		 * NPSN+S => Siswa
		 */
		
		// For application
		String appServerAddress = "sias.example.com";
		String loginContext = "/api/1.0.0/push/login/";
		String registrationContext = "/api/1.0.0/push/resgitration/";
		String userID = "081311193293";
		String userPassword = "f6ae23a2";
		String userGroup = "W";	
		/**
		 * Grup pengguna
		 * W => Wali
		 * S => Siswa
		 */

		System.out.println("Asynchronous notification");	
		System.out.println("Here your code before start notification!");	
		AsyncNotif asyncNotif = new AsyncNotif(apiKey, apiPassword, deviceID, groupKey, pushServerAddress, pushServerPort);
		
		// Cookie must be saved on local storage
		
		// If cookie is empty, redirect to login page
		// userID is user ID or username
		// password is user password
		// group is user group/user level
		HTTPResponse login = asyncNotif.login("https://"+appServerAddress+loginContext, cookie, userID, userPassword, userGroup);
		
		cookie = login.cookie;
		// Get the cookie and save it into local storage

		System.out.println("Login with username, password and group");
		System.out.println("Cookie : "+login.cookie);
		System.out.println("Body   : "+login.body);
		
		// If cookie is not empty, check the user authentication by send the cookie
		HTTPResponse auth = asyncNotif.auth("https://"+appServerAddress+loginContext, cookie);
		
		cookie = login.cookie;
		// Get the cookie and save it into local storage

		System.out.println("Login with cookie instead of username, password and group");
		System.out.println("Cookie : "+auth.cookie);
		System.out.println("Body   : "+auth.body);
		
		if(registerDevice.equals("1") || registerDevice.equals("true") || registerDevice.equals("yes"))
		{
			asyncNotif.registerDevice(deviceID);
			/**
			 * For registration using username, password and group
			 * HTTPResponse response = asyncNotif.registerDeviceApps(appServerAddress, appServerPort, registrationContext, deviceID, cookie, userID, userPassword, userGroup);
			 */
			/**
			 * For registration with cookie instead of username, password and group
			 */
			HTTPResponse response = asyncNotif.registerDeviceApps("https://"+appServerAddress+registrationContext, deviceID, cookie);
			System.out.println(response.body);
		}
		
		asyncNotif.start();
		
		System.out.println("Here your code after start notification!");	
		System.out.println("The end of the program");
	}
}

```

## Override Methods

You can override methods from interface Request. The methods are

```java
public void onNotificationReceived(RemoteMessage notification);
public void onNotificationDeleted(String notificationID);
public void onMessageReceived(RemoteMessage message);
public void onMessageDeleted(String messageID);
public void onDataReceived(String[] headers, String command, String body);
public void onDataSent(String[] headers, String command, String body);
public void onNewToken(String token, String time, long waitToNext, int timeZone);
public void onChangeSetting(String name, String type, Object value);
public void onError(Exception exception);	
public void onRegisterDeviceSendSuccess(String deviceID, String message);
public void onRegisterDeviceSendError(String deviceID, String message, String cause);
public void onUnregisterDeviceSendSuccess(String deviceID, String message);
public void onUnregisterDeviceSendError(String deviceID, String message, String cause);
public void onRegisterDeviceSuccess(String deviceID, int responseCode, String message);
public void onRegisterDeviceError(String deviceID, int responseCode, String message, String cause);
public void onUnregisterDeviceSuccess(String deviceID, int responseCode, String message);
public void onUnregisterDeviceError(String deviceID, int responseCode, String message, String cause);
```

### onNotificationReceived

This method invoked when device receiving message from notification server. message is object of RemoteMessage. This object contains notification created form Notification.

#### Example

```java
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
```	

time is local time when notification was sent. timezone is time zone offset of the server. You must convert server time to device time using the time zone offset.

miscData contains additional data. The miscData format is plaintext by encoding JSON, XML, base 64, or hexadecimal binary data.'

### onNotificationDeleted

**onNotificationDeleted** is invoked when the notification server deletes notifications that have been sent to the device. The notification server notifies the device that the notification is deleted by the server on the sender's order. The mobile application can delete notifications that have been received if the sending application wants the data to be deleted.

To delete notifications on the mobile application, override **onDeleteMessages** method in the Notification class.

```java
public void onNotificationDeleted(String messageID)
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

### onChangeSetting

**onChangeSetting** is invoked when server change the client settings. Application can get information about the change of settings.

```java
public void onChangeSetting(String name, String type, Object value)
{
	// TODO: Add your code here when setting was changed
	/**
	 * The name of setting can be:
	 * 1. timeout
	 * 2. delayReconnect
	 * 3. delayRestart
	 */
}
```
