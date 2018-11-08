package com.planetbiru.pushclient.application;


import com.planetbiru.pushclient.utility.HTTPClient;
import com.planetbiru.pushclient.utility.HTTPResponse;
import com.planetbiru.pushclient.utility.Utility;

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
		String userID = "08131111111";
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
		
		/**
		 * Get HTTP
		 */
		HTTPClient httpClient = new HTTPClient();
		HTTPResponse response = new HTTPResponse();
		try 
		{
			System.out.println(cookie);
			response = httpClient.get("https://sias.example.com/wali/wali-berita.php?id=27", cookie);
			System.out.println(response.responseCode);
			System.out.println(response.cipherSuite);
			System.out.println(response.header);
			System.out.println(response.cookie);
			System.out.println(response.body);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		
		System.out.println("Here your code after start notification!");	
		System.out.println("The end of the program");
	}
}
