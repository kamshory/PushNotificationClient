package com.planetbiru.pushclient.notification;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.planetbiru.pushclient.config.Config;
import com.planetbiru.pushclient.util.SocketIO;
import com.planetbiru.pushclient.util.Utility;

public class Notification implements Request
{
	public Socket socket = new Socket();
	public boolean stoped = false;
	public boolean connected = false;
	public boolean isConnected = false;
	
	private int serverPort = Config.serverPort;
	private String serverAddress = Config.serverAddress;
	
	public String deviceID = "";
	public String apiKey = "";
	public String password = "";
	
	private boolean debugMode = Config.debugMode;
	private int timeout = Config.timeout;
	private int countDownReconnect = Config.countDownReconnect;
	private long delayRestart = Config.delayRestart;
	private long delayReconnect = Config.delayReconnect;

	public Notification()
	{
		
	}
	public Notification(String apiKey, String password, String deviceID)
	{
		this.apiKey = apiKey;
		this.deviceID = deviceID;
		this.password = password;
	}
	public Notification(String apiKey, String password, String deviceID, String serverAddress, int serverPort)
	{
		this.apiKey = apiKey;
		this.deviceID = deviceID;
		this.password = password;
		this.setServerAddress(serverAddress);
		this.setServerPort(serverPort);
	}
	public void start()
	{
		this.stoped = false;
		if(!this.isConnected)
		{
			this.connect();
		}
		if(this.isConnected)
		{
			SocketIO socketIO = new SocketIO(this.socket);
			boolean success = false;
			while(!this.stoped)
			{
				try 
				{
					success = socketIO.read();
					if(success)
					{
						this.processMessage(socketIO.getHeaders(), socketIO.getBody());
					}
					else
					{
						this.stoped = true;
					}
				} 
				catch (IOException e1) 
				{
					this.stoped = true;
					this.onError(e1);
					if(this.debugMode)
					{
						e1.printStackTrace();
					}
				}			
			}
		}
		this.restart();
	}
	private void restart()
	{
		this.stop();
		this.disconnect();
		this.isConnected = false;
		try 
		{
			Thread.sleep(this.delayRestart );
		} 
		catch (InterruptedException e) 
		{
			this.onError(e);
			if(this.debugMode)
			{
				e.printStackTrace();
			}
		}
		this.connect();
		this.start();		
	}
	public void stop()
	{
		this.stoped = true;
		try 
		{
			this.socket.close();
		} 
		catch (IOException e) 
		{
			this.onError(e);
			if(this.debugMode)
			{
				e.printStackTrace();
			}
		}
	}

	private boolean createSocket()
	{
		SocketAddress socketAddress = new InetSocketAddress(this.getServerAddress() , this.getServerPort());
		try 
		{
			this.socket = new Socket();
			this.socket.connect(socketAddress, this.getTimeout());
			if(this.socket.isConnected() && !this.socket.isClosed())
			{
				this.socket.setKeepAlive(true);
				this.connected = true;
				return true;
			}
			else
			{
				this.connected = false;
			}
		}
		catch(UnknownHostException e)
		{
			this.onError(e);
			this.connected = false;			
		}
		catch (IOException e) 
		{
			this.onError(e);
			this.connected = false;
		}
		return false;
	}
	private boolean reconnect(int countDown)
	{
		try 
		{
			Thread.sleep(this.getDelayReconnect());
		} 
		catch (InterruptedException e) 
		{
			this.onError(e);
		}
		if(countDown > 0)
		{
			if(this.connect())
			{
				return true;
			}
			else
			{
				return this.reconnect(countDown--);
			}
		}
		else
		{
			return false;
		}
	}
	public boolean connect(String apiKey)
	{
		this.apiKey = apiKey;
		return this.connect();
	}
	public boolean connect()
	{
		if(this.isConnected)
		{
			return true;
		}
		if(this.apiKey.equals(""))
		{
			return false;
		}
		else
		{
			this.isConnected  = this.createSocket();
			if(this.isConnected)
			{
				long unixTime = Utility.unixTime();
				String token = Utility.sha1(unixTime+this.apiKey);
				String hash = Utility.sha1(Utility.sha1(this.password)+"-"+token+"-"+this.apiKey);
				SocketIO socketIO = new SocketIO(this.socket);
				String data = "";
				socketIO.resetRequestHeader();
				socketIO.addRequestHeader("Command", "singin");
				socketIO.addRequestHeader("Authorization", "key="+this.apiKey+"&token="+token+"&hash="+hash+"&time="+unixTime);
				try 
				{
					data = this.createRequest();
					this.connected = socketIO.write(data);
					if(this.connected)
					{
						boolean success = false;
						success = socketIO.read();
						if(success)
						{
							this.processMessage(socketIO.getHeaders(), socketIO.getBody());
						}
						else
						{
							this.stoped = true;
						}
					}
				} 
				catch (IOException e) 
				{
					this.onError(e);
					if(this.debugMode)
					{
						e.printStackTrace();
					}
					return this.reconnect(this.countDownReconnect);
				}
			}
			else
			{
				return this.reconnect(this.countDownReconnect );
			}
			return this.isConnected;
		}
	}
	public boolean disconnect()
	{
		try 
		{
			this.socket.close();
			this.connected = false;
		} 
		catch (IOException e) 
		{
			this.onError(e);
		}
		return true;
	}
	private void processMessage(String[] headers, String body)
	{
		String command = Utility.getFirst(headers, "Command");
		this.onDataReceived(headers, command, body);
		String messageType = Utility.getFirst(headers, "Content-Type");
		String commandLower = command.toLowerCase().trim();
		if(commandLower.equals("message"))
		{
			RemoteMessage remoteMessage = new RemoteMessage();		
			JSONArray data;
			try
			{
				data = new JSONArray(body);
				int i;
				int j = data.length();
				for(i = 0; i < j; i++)
				{
					remoteMessage = new RemoteMessage(messageType, data.getJSONObject(i));	
					this.onMessageReceived(remoteMessage);
				}			
			}
			catch(JSONException e)
			{
				this.onError(e);
			}
		}
		else if(commandLower.equals("delete-message"))
		{
			JSONArray data;
			try
			{
				JSONObject jo;
				data = new JSONArray(body);
				int i;
				int j = data.length();
				String messageID = "";
				for(i = 0; i < j; i++)
				{
					jo = data.getJSONObject(i);
					messageID = jo.optString("id", "");
					this.onDeletedMessages(messageID);
				}
			}
			catch(JSONException e)
			{
				this.onError(e);
				if(this.debugMode)
				{
					e.printStackTrace();
				}
			}
		}
		else if(commandLower.equals("token"))
		{
			JSONObject jo;
			try
			{
				jo = new JSONObject(body);
				String token = jo.optString("token", "");
				String time = jo.optString("time", "");
				int timeZone = jo.getInt("timeZone");
				this.onNewToken(token);
				this.onNewToken(token, time, timeZone);
			}
			catch(JSONException e)
			{
				this.onError(e);
				if(this.debugMode)
				{
					e.printStackTrace();
				}
			}
		}
		else if(commandLower.equals("unregister-device-success"))
		{
			JSONObject jo;
			try
			{
				jo = new JSONObject(body);
				String message = jo.optString("message", "");
				String deviceID = jo.optString("deviceID", "");
				this.onUnregisterDeviceSuccess(deviceID, message);
			}
			catch(JSONException e)
			{
				this.onError(e);
				if(this.debugMode)
				{
					e.printStackTrace();
				}
			}
		}
		else if(commandLower.equals("register-device-success"))
		{
			JSONObject jo;
			try
			{
				jo = new JSONObject(body);
				String message = jo.optString("message", "");
				deviceID = jo.optString("deviceID", "");
				this.onRegisterDeviceSuccess(deviceID, message);
			}
			catch(JSONException e)
			{
				this.onError(e);
				if(this.debugMode)
				{
					e.printStackTrace();
				}
			}
		}
		else if(commandLower.equals("register-device-error"))
		{
			JSONObject jo;
			try
			{
				jo = new JSONObject(body);
				String message = jo.optString("message", "");
				String deviceID = jo.optString("deviceID", "");
				String cause = jo.optString("cause", "");
				this.onRegisterDeviceError(deviceID, message, cause);
			}
			catch(JSONException e)
			{
				this.onError(e);
				if(this.debugMode)
				{
					e.printStackTrace();
				}
			}
		}
		else if(commandLower.equals("unregister-device-error"))
		{
			JSONObject jo;
			try
			{
				jo = new JSONObject(body);
				String message = jo.optString("message", "");
				String deviceID = jo.optString("deviceID", "");
				String cause = jo.optString("cause", "");
				this.onUnregisterDeviceError(deviceID, message, cause);
			}
			catch(JSONException e)
			{
				this.onError(e);
				if(this.debugMode)
				{
					e.printStackTrace();
				}
			}
		}
		else if(commandLower.equals("setting"))
		{
			JSONArray data;
			JSONObject setting = new JSONObject();
			String name = "";
			String type = "";
			String valueString = "";
			Object value = new Object();
			try
			{
				data = new JSONArray(body);
				int i;
				int j = data.length();
				for(i = 0; i < j; i++)
				{
					setting = data.getJSONObject(i);
					name = setting.optString("name", "").trim();
					type = setting.optString("type", "").trim();
					valueString = setting.optString("value", "").trim();
					if(type.equals("int"))
					{
						if(valueString.equals(""))
						{
							valueString = "0";
						}
						value = Integer.parseInt(valueString);
					}
					else if(type.equals("long"))
					{
						if(valueString.equals(""))
						{
							valueString = "0";
						}
						value = Long.parseLong(valueString);
					}
					else if(type.equals("float"))
					{
						if(valueString.equals(""))
						{
							valueString = "0";
						}
						value = Float.parseFloat(valueString);
					}
					else if(type.equals("double"))
					{
						if(valueString.equals(""))
						{
							valueString = "0";
						}
						value = Double.parseDouble(valueString);
					}
					else
					{
						value = valueString;
					}
					this.applySetting(name, type, value);
					this.onChangeSetting(name, type, value);
				}			
			}
			catch(JSONException e)
			{
				this.onError(e);
			}			
		}
		else if(commandLower.equals("question"))
		{
			JSONObject jo;
			try
			{
				jo = new JSONObject(body);
				String question = jo.optString("question", "").trim();
				String deviceID = jo.optString("deviceID", "").trim();
				if(question.length() > 0)
				{
					this.answerQuestion(deviceID, question);
				}
			}
			catch(JSONException e)
			{
				this.onError(e);
				if(this.debugMode)
				{
					e.printStackTrace();
				}
			}
		}
	}
	private void applySetting(String name, String type, Object value) 
	{
		if(name.equals("delayRestart"))
		{
			this.delayRestart = (long) value;
		}
		else if(name.equals("delayReconnect"))
		{
			this.delayReconnect = (long) value;
		}
		else if(name.equals("timeout"))
		{
			this.timeout = (int) value;
		}
	}
	private void answerQuestion(String deviceID, String question) 
	{
		String hashPassword = Utility.sha1(this.password);
		String answer = Utility.sha1((hashPassword+"-"+question+"-"+deviceID));
		SocketIO socketIO = new SocketIO(this.socket);
		socketIO.resetRequestHeader();
		socketIO.addRequestHeader("Command", "answer");
		socketIO.addRequestHeader("Content-Type", "application/json");
		try 
		{
			JSONObject data = new JSONObject();
			data.put("deviceID", deviceID);
			data.put("answer", answer);
			data.put("question", question);
			String data2sent = data.toString();
			socketIO.write(data2sent);
		}
		catch(IOException e)
		{
		}
	}
	private String createRequest()
	{
		JSONObject jo = new JSONObject();
		String result = "";
		try 
		{
			jo.put("deviceID", this.deviceID );
			jo.put("api_key", this.apiKey);
		} 
		catch (JSONException e)
		{
			this.onError(e);
		}
		result = jo.toString();
		return result;
	}
	public boolean registerDevice(String deviceID)
	{
		if(!this.isConnected)
		{
			this.connect();
		}
		if(this.isConnected)
		{
			SocketIO socketIO = new SocketIO(this.socket);
			socketIO.resetRequestHeader();
			socketIO.addRequestHeader("Command", "register-device");
			socketIO.addRequestHeader("Authorization", "key="+this.apiKey);
			boolean success = false;
			try 
			{
				JSONObject data = new JSONObject();
				data.put("deviceID", deviceID);
				success = socketIO.write(data.toString());
				if(success)
				{
					this.onRegisterDeviceSendSuccess(deviceID, "Data sent to notification server");
					return true;
				}
				else
				{
					this.onRegisterDeviceSendError(deviceID, "Failed", "Data not sent to notification server");
					return false;
				}
			} 
			catch (IOException e) 
			{
				this.onRegisterDeviceSendError(deviceID, "Failed", e.getMessage());
				this.onError(e);
				if(this.debugMode)
				{
					e.printStackTrace();
				}
				try 
				{
					this.socket.close();
				} 
				catch (IOException e1) 
				{
					this.onError(e1);
					if(this.debugMode)
					{
						e1.printStackTrace();
					}
				}
				return false;
			}
		}
		else
		{
			this.onRegisterDeviceSendError(deviceID, "Failed", "Not connected");
			return false;
		}
	}
	public boolean unregisterDevice(String device_id) throws IOException
	{
		if(!this.isConnected)
		{
			this.connect();
		}
		if(this.isConnected)
		{
			SocketIO socketIO = new SocketIO(this.socket);
			socketIO.resetRequestHeader();
			socketIO.addRequestHeader("Command", "unregister-device");
			socketIO.addRequestHeader("Authorization", "key="+this.apiKey);
			boolean success = false;
			try 
			{
				JSONObject data = new JSONObject();
				data.put("deviceID", deviceID);
				success = socketIO.write(data.toString());
				if(success)
				{
					this.onUnregisterDeviceSuccess(deviceID, "Data sent to notification server");
					return true;
				}
				else
				{
					this.onUnregisterDeviceError(deviceID, "Failed", "Data not sent to notification server");
					return false;
				}
			} 
			catch (IOException e) 
			{
				this.onUnregisterDeviceError(deviceID, "Failed", e.getMessage());
				this.onError(e);
				if(this.debugMode)
				{
					e.printStackTrace();
				}
				try 
				{
					this.socket.close();
				} 
				catch (IOException e1) 
				{
					this.onError(e1);
					if(this.debugMode)
					{
						e1.printStackTrace();
					}
				}
				return false;
			}
		}
		else
		{
			this.onUnregisterDeviceError(deviceID, "Failed", "Not connected");
			return false;
		}
	}
	public void setDebugMode(boolean debugMode)
	{
		this.debugMode = debugMode;
	}
	public void onMessageReceived(RemoteMessage message)
	{	
		
	}
	public void onDataReceived(String[] headers, String command, String body)
	{
		
	}
	public void onDeletedMessages(String msgId)
	{		
		
	}
	public void onNewToken(String token)
	{
		
	}
	public void onNewToken(String token, String time, int timeZone)
	{
		
	}
	public void onChangeSetting(String name, String type, Object value)
	{
		
	}
	public void onError(Exception exception)
	{
		
	}
	public void onRegisterDeviceSendSuccess(String deviceID, String message)
	{
		
	}
	public void onRegisterDeviceSendError(String deviceID, String message, String cause)
	{
		
	}
	public void onUnregisterDeviceSendSuccess(String deviceID, String message)
	{
		
	}
	public void onUnregisterDeviceSendError(String deviceID, String message, String cause)
	{
		
	}

	public void onRegisterDeviceSuccess(String deviceID, String message)
	{
		
	}
	public void onRegisterDeviceError(String deviceID, String message, String cause)
	{
		
	}
	public void onUnregisterDeviceSuccess(String deviceID, String message)
	{
		
	}
	public void onUnregisterDeviceError(String deviceID, String message, String cause)
	{
		
	}
	public String getServerAddress() 
	{
		return this.serverAddress;
	}
	public void setServerAddress(String serverAddress) 
	{
		this.serverAddress = serverAddress;
	}
	public int getServerPort() 
	{
		return this.serverPort;
	}
	public void setServerPort(int serverPort) 
	{
		this.serverPort = serverPort;
	}
	public long getDelayRestart() 
	{
		return this.delayRestart;
	}
	public void setDelayRestart(int delayRestart) 
	{
		this.delayRestart = delayRestart;
	}
	public long getDelayReconnect() 
	{
		return this.delayReconnect;
	}
	public void setDelayReconnect(int delayReconnect) 
	{
		this.delayReconnect = delayReconnect;
	}
	public int getTimeout() 
	{
		return timeout;
	}
	public void setTimeout(int timeout) 
	{
		this.timeout = timeout;
	}

}
