package com.planetbiru.pushclient.application;

import com.planetbiru.pushclient.util.Utility;

public class Application 
{
	public static void main(String[] args)
	{
		// Synchronous Notification
		/*
		Notif notif = new Notif();
		notif.apiKey = "PLANETBIRU";
		notif.password = "123456";
		notif.deviceID = "41fda1bcf6486302";
		notif.interval = 5000;
		notif.setDebugMode(true);
		notif.connect();
		notif.registerDevice("151611");
		notif.start();	
		*/
		
		String deviceID = Utility.getFirst(args, "device-id").trim();
		String registerDevice = Utility.getFirst(args, "register-device").toLowerCase().trim();
		if(deviceID.length() == 0)
		{
			deviceID = "41fda1bcf6486302";
		}

		System.out.println("Asynchronous notification");	
		System.out.println("Here your code before start notification!");	
		AsyncNotif asyncNotif = new AsyncNotif("PLANETBIRU", "123456", deviceID, "127.0.0.1", 92);
		
		if(registerDevice.equals("1") || registerDevice.equals("true") || registerDevice.equals("yes"))
		{
			asyncNotif.registerDevice(deviceID);
		}
		
		asyncNotif.start();
		System.out.println("Here your code after start notification!");	
		System.out.println("The end of the program");

		/*
		Notif notif = new Notif("PLANETBIRU", "123456", "41fda1bcf6486302", "127.0.0.1", 92);
		notif.setDelayRestart(10000);
		notif.setDelayReconnect(10000);
		notif.connect();
		notif.start();
		*/
	}

}
