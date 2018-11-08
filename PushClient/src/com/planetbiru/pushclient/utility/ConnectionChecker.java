package com.planetbiru.pushclient.utility;

import java.io.IOException;

import com.planetbiru.pushclient.notification.Notification;

public class ConnectionChecker extends Thread
{
	private Notification notification;
	long waitToNext;
	public ConnectionChecker(Notification notification, long waitToNext)
	{
		this.notification = notification;
		this.waitToNext = waitToNext;
	}
	public ConnectionChecker() 
	{
	}
	public void run()
	{
		if(this.waitToNext > 0)
		{
			try 
			{
				Thread.sleep(this.waitToNext);
			} 
			catch (InterruptedException e) 
			{
				e.printStackTrace();
			}
			try 
			{
				this.notification.socket.close();
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
		}
	}
}
