package com.planetbiru.pushclient.notification;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.planetbiru.pushclient.config.Config;
import com.planetbiru.pushclient.utility.ConnectionChecker;
import com.planetbiru.pushclient.utility.Encryption;
import com.planetbiru.pushclient.utility.HTTPClient;
import com.planetbiru.pushclient.utility.HTTPResponse;
import com.planetbiru.pushclient.utility.SocketIO;
import com.planetbiru.pushclient.utility.SocketIOSSL;
import com.planetbiru.pushclient.utility.Utility;

public class Notification implements Request
{
	/**
	 * Client socket
	 */
	public Socket socket = new Socket();
	/**
	 * Client socket
	 */
	public SSLSocket socketSSL = null;
	/**
	 * Indicate that process is stopped
	 */
	public boolean stoped = false;
	/**
	 * Indicate that the client is connected to the server
	 */
	public boolean connected = false;
	/**
	 * Indicate that the client is connected to the server
	 */
	public boolean isConnected = false;
	/**
	 * Server port
	 */
	private int serverPort = Config.serverPort;
	/**
	 * Server address
	 */
	private String serverAddress = Config.serverAddress;
	/**
	 * Device ID
	 */
	public String deviceID = "";
	/**
	 * API key
	 */
	public String apiKey = "";
	/**
	 * Group key
	 */
	public String groupKey = "";
	/**
	 * Password
	 */
	public String password = "";
	/**
	 * Indicate that client run in debug mode
	 */
	private boolean debugMode = Config.debugMode;
	/**
	 * Time out
	 */
	private int timeout = Config.timeout;
	/**
	 * Count down to reconnect
	 */
	private int countDownReconnect = Config.countDownReconnect;
	/**
	 * Delay to restart the client
	 */
	private long delayRestart = Config.delayRestart;
	/**
	 * Delay to reconnect to the server
	 */
	private long delayReconnect = Config.delayReconnect;
	/**
	 * Last error
	 */
	private Exception lastError = new Exception();
	/**
	 * Connection checker
	 */
	private ConnectionChecker connectionChecker = new ConnectionChecker();
	/**
	 * Flag to force stop
	 */
	private boolean forceStop = false;
	public boolean ssl = false;
	/**
	 * Default constructor
	 */
	public Notification()
	{		
	}
	/**
	 * Constructor with initialization
	 * @param apiKey API key of the push notification
	 * @param password API password of the push notification
	 * @param deviceID Device ID
	 * @param groupKey Group key of the push notification
	 */
	public Notification(String apiKey, String password, String deviceID, String groupKey)
	{
		this.apiKey = apiKey;
		this.deviceID = deviceID;
		this.password = password;
		this.groupKey = groupKey;
		Config.apiKey = apiKey;
		Config.password = password;
		Config.groupKey = groupKey;
	}
	/**
	 * Constructor with initialization
	 * @param apiKey API Key
	 * @param password Password
	 * @param deviceID Device ID
	 * @param groupKey Group Key
	 * @param serverAddress Server address
	 * @param serverPort Server port
	 * @param ssl 
	 */
	public Notification(String apiKey, String password, String deviceID, String groupKey, String serverAddress, int serverPort)
	{
		this.apiKey = apiKey;
		this.deviceID = deviceID;
		this.password = password;
		this.groupKey = groupKey;
		Config.apiKey = apiKey;
		Config.password = password;
		Config.groupKey = groupKey;
		this.ssl = false;
		this.setServerAddress(serverAddress);
		this.setServerPort(serverPort);
	}
	/**
	 * Constructor with initialization
	 * @param apiKey API Key
	 * @param password Password
	 * @param deviceID Device ID
	 * @param groupKey Group Key
	 * @param serverAddress Server address
	 * @param serverPort Server port
	 * @param ssl SSL connection
	 */
	public Notification(String apiKey, String password, String deviceID, String groupKey, String serverAddress, int serverPort, boolean ssl)
	{
		this.apiKey = apiKey;
		this.deviceID = deviceID;
		this.password = password;
		this.groupKey = groupKey;
		Config.apiKey = apiKey;
		Config.password = password;
		Config.groupKey = groupKey;
		this.ssl  = ssl;
		this.setServerAddress(serverAddress);
		this.setServerPort(serverPort);
	}
	/**
	 * Start notification client
	 */
	public void start()
	{
		this.stoped = false;
		this.forceStop = false;
		if(!this.isConnected)
		{
			this.connect();
		}
		if(this.ssl)
		{
			if(this.isConnected)
			{
				SocketIOSSL socketIO = new SocketIOSSL(this.socketSSL);
				boolean success = false;
				while(!this.stoped && !this.forceStop)
				{
					try 
					{
						success = socketIO.read();
						if(success)
						{
							try 
							{
								this.processPush(socketIO.getResponseHeaderArray(), socketIO.getBody());
							} 
							catch (NotificationException e) 
							{
								this.sendError(e);
								if(this.debugMode)
								{
									e.printStackTrace();
								}
							} 
							catch (InvalidKeyException e) 
							{
								this.sendError(e);
								if(this.debugMode)
								{
									e.printStackTrace();
								}
							} 
							catch (NoSuchAlgorithmException e) 
							{
								this.sendError(e);
								if(this.debugMode)
								{
									e.printStackTrace();
								}
							} 
							catch (NoSuchPaddingException e) 
							{
								this.sendError(e);
								if(this.debugMode)
								{
									e.printStackTrace();
								}
							} 
							catch (IllegalBlockSizeException e) 
							{
								this.sendError(e);
								if(this.debugMode)
								{
									e.printStackTrace();
								}
							} 
							catch (BadPaddingException e) 
							{
								this.sendError(e);
								if(this.debugMode)
								{
									e.printStackTrace();
								}
							}
							catch (NegativeArraySizeException e)
							{
								this.sendError(e);
								if(Config.debugMode)
								{
									e.printStackTrace();
								}
							}
							catch (NullPointerException e)
							{
								this.sendError(e);
								if(Config.debugMode)
								{
									e.printStackTrace();
								}
							}
						}
						else
						{
							this.stoped = true;
						}
					} 
					catch (IOException e) 
					{
						this.stoped = true;
						this.sendError(e);
						if(this.debugMode)
						{
							e.printStackTrace();
						}
					}			
				}
			}
		}
		else
		{
			if(this.isConnected)
			{
				SocketIO socketIO = new SocketIO(this.socket);
				boolean success = false;
				while(!this.stoped && !this.forceStop)
				{
					try 
					{
						success = socketIO.read();
						if(success)
						{
							try 
							{
								this.processPush(socketIO.getResponseHeaderArray(), socketIO.getBody());
							} 
							catch (NotificationException e) 
							{
								this.sendError(e);
								if(this.debugMode)
								{
									e.printStackTrace();
								}
							} 
							catch (InvalidKeyException e) 
							{
								this.sendError(e);
								if(this.debugMode)
								{
									e.printStackTrace();
								}
							} 
							catch (NoSuchAlgorithmException e) 
							{
								this.sendError(e);
								if(this.debugMode)
								{
									e.printStackTrace();
								}
							} 
							catch (NoSuchPaddingException e) 
							{
								this.sendError(e);
								if(this.debugMode)
								{
									e.printStackTrace();
								}
							} 
							catch (IllegalBlockSizeException e) 
							{
								this.sendError(e);
								if(this.debugMode)
								{
									e.printStackTrace();
								}
							} 
							catch (BadPaddingException e) 
							{
								this.sendError(e);
								if(this.debugMode)
								{
									e.printStackTrace();
								}
							}
							catch (NegativeArraySizeException e)
							{
								this.sendError(e);
								if(Config.debugMode)
								{
									e.printStackTrace();
								}
							}
							catch (NullPointerException e)
							{
								this.sendError(e);
								if(Config.debugMode)
								{
									e.printStackTrace();
								}
							}
						}
						else
						{
							this.stoped = true;
						}
					} 
					catch (IOException e) 
					{
						this.stoped = true;
						this.sendError(e);
						if(this.debugMode)
						{
							e.printStackTrace();
						}
					}			
				}
			}
		}
		if(!this.forceStop)
		{
			this.restart();
		}
	}
	/**
	 * Restart notification client 
	 */
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
			this.sendError(e);
		}
		this.connect();
		this.start();		
	}
	/**
	 * Stop notification client
	 */
	public void stop()
	{
		this.forceStop = true;
		this.stoped = true;
		if(this.ssl)
		{
			try 
			{
				this.socketSSL.close();
			} 
			catch (IOException e) 
			{
				this.sendError(e);
				if(this.debugMode)
				{
					e.printStackTrace();
				}
			}
		}
		else
		{
			try 
			{
				this.socket.close();
			} 
			catch (IOException e) 
			{
				this.sendError(e);
				if(this.debugMode)
				{
					e.printStackTrace();
				}
			}			
		}
	}
	/**
	 * Create socket
	 * @return true if success and false if failed
	 * @throws IOException 
	 */
	private boolean createSocket() throws IOException, SocketException, IllegalArgumentException, SecurityException 
	{
		if(this.ssl)
		{
			SSLSocketFactory factory = (SSLSocketFactory)SSLSocketFactory.getDefault();
			this.socketSSL = (SSLSocket)factory.createSocket(this.getServerAddress(), this.getServerPort());
			if(this.socketSSL.isConnected() && !this.socketSSL.isClosed())
			{
				this.socketSSL.setKeepAlive(true);
				this.connected = true;
				return true;
			}
			else
			{
				this.connected = false;
			}			
		}
		else
		{
			SocketAddress socketAddress = new InetSocketAddress(this.getServerAddress(), this.getServerPort());
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
		return false;
	}
	/**
	 * Reconnect client
	 * @param countDown Count down
	 * @return true if success and false if failed
	 */
	private boolean reconnect(int countDown)
	{
		try 
		{
			Thread.sleep(this.getDelayReconnect());
		} 
		catch (InterruptedException e) 
		{
			this.sendError(e);
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
	/**
	 * Connect to notification server
	 * @param apiKey API key
	 * @param password API password
	 * @param groupKey API group key
	 * @return true if success and false if failed
	 */
	public boolean connect(String apiKey, String password, String groupKey)
	{
		this.apiKey = apiKey;
		this.password = password;
		this.groupKey = groupKey;
		return this.connect();
	}
	/**
	 * Connect to notification server
	 * @return true if success and false if failed
	 */
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
			try 
			{
				this.isConnected  = this.createSocket();
			} 
			catch (IllegalArgumentException e)
			{
				if(Config.debugMode)
				{
					e.printStackTrace();
				}
			} 
			catch (SecurityException e) 
			{
				if(Config.debugMode)
				{
					e.printStackTrace();
				}
			} 
			catch(SocketException e)
			{
				if(Config.debugMode)
				{
					e.printStackTrace();
				}
			}
			catch (IOException e) 
			{
				if(Config.debugMode)
				{
					e.printStackTrace();
				}
			}			
			if(this.isConnected)
			{
				String command = "singin";
				long unixTime = Utility.unixTime();
				String token = "";
				try 
				{
					token = Utility.sha1(unixTime+this.apiKey);
				} 
				catch (NoSuchAlgorithmException e2) 
				{
					if(Config.debugMode)
					{
						e2.printStackTrace();
					}
				}
				String hash =  "";
				try 
				{
					hash = Utility.sha1(Utility.sha1(this.password)+"-"+token+"-"+this.apiKey);
				} 
				catch (NoSuchAlgorithmException e2) 
				{
					if(Config.debugMode)
					{
						e2.printStackTrace();
					}
				}
				String groupKey;
				try 
				{
					groupKey = Utility.urlEncode(this.groupKey);
				} 
				catch (UnsupportedEncodingException e1) 
				{
					groupKey = this.groupKey;
					this.sendError(e1);
					if(Config.debugMode)
					{
						e1.printStackTrace();
					}
				}
				if(this.ssl)
				{
					SocketIOSSL socketIO = new SocketIOSSL(this.socketSSL);
					String data = "";
					socketIO.resetRequestHeader();
					socketIO.addRequestHeader("Command", command);
					socketIO.addRequestHeader("Authorization", "key="+this.apiKey+"&token="+token+"&hash="+hash+"&time="+unixTime+"&group="+groupKey);
					try 
					{
						data = this.createRequest();
						String[] headers = socketIO.getRequestHeaderArray();
						this.onDataSent(headers, command, data);
						this.connected = socketIO.write(data);
						if(this.connected)
						{
							boolean success = false;
							success = socketIO.read();
							if(success)
							{
								try 
								{
									this.processPush(socketIO.getResponseHeaderArray(), socketIO.getBody());
								} 
								catch (NotificationException e) 
								{
									this.sendError(e);
									if(Config.debugMode)
									{
										e.printStackTrace();
									}
								} 
								catch (InvalidKeyException e) 
								{
									this.sendError(e);
									if(Config.debugMode)
									{
										e.printStackTrace();
									}
								} 
								catch (NoSuchAlgorithmException e) 
								{
									this.sendError(e);
									if(Config.debugMode)
									{
										e.printStackTrace();
									}
								} 
								catch (NoSuchPaddingException e) 
								{
									this.sendError(e);
									if(Config.debugMode)
									{
										e.printStackTrace();
									}
								} 
								catch (IllegalBlockSizeException e) 
								{
									this.sendError(e);
									if(Config.debugMode)
									{
										e.printStackTrace();
									}
								} 
								catch (BadPaddingException e) 
								{
									this.sendError(e);
									if(Config.debugMode)
									{
										e.printStackTrace();
									}
								}
								catch (NegativeArraySizeException e)
								{
									this.sendError(e);
									if(Config.debugMode)
									{
										e.printStackTrace();
									}
								}
								catch (NullPointerException e)
								{
									this.sendError(e);
									if(Config.debugMode)
									{
										e.printStackTrace();
									}
								}
							}
							else
							{
								this.stoped = true;
							}
						}
					} 
					catch (IOException e) 
					{
						this.sendError(e);
						if(this.debugMode)
						{
							e.printStackTrace();
						}
						return this.reconnect(this.countDownReconnect);
					}
				}
				else
				{
					SocketIO socketIO = new SocketIO(this.socket);
					String data = "";
					socketIO.resetRequestHeader();
					socketIO.addRequestHeader("Command", command);
					socketIO.addRequestHeader("Authorization", "key="+this.apiKey+"&token="+token+"&hash="+hash+"&time="+unixTime+"&group="+groupKey);
					try 
					{
						data = this.createRequest();
						String[] headers = socketIO.getRequestHeaderArray();
						this.onDataSent(headers, command, data);
						this.connected = socketIO.write(data);
						
						if(this.connected)
						{
							boolean success = false;
							success = socketIO.read();
							if(success)
							{
								try 
								{
									this.processPush(socketIO.getResponseHeaderArray(), socketIO.getBody());
								} 
								catch (NotificationException e) 
								{
									this.sendError(e);
									if(Config.debugMode)
									{
										e.printStackTrace();
									}
								} 
								catch (InvalidKeyException e) 
								{
									this.sendError(e);
									if(Config.debugMode)
									{
										e.printStackTrace();
									}
								} 
								catch (NoSuchAlgorithmException e) 
								{
									this.sendError(e);
									if(Config.debugMode)
									{
										e.printStackTrace();
									}
								} 
								catch (NoSuchPaddingException e) 
								{
									this.sendError(e);
									if(Config.debugMode)
									{
										e.printStackTrace();
									}
								} 
								catch (IllegalBlockSizeException e) 
								{
									this.sendError(e);
									if(Config.debugMode)
									{
										e.printStackTrace();
									}
								} 
								catch (BadPaddingException e) 
								{
									this.sendError(e);
									if(Config.debugMode)
									{
										e.printStackTrace();
									}
								}
								catch (NegativeArraySizeException e)
								{
									this.sendError(e);
									if(Config.debugMode)
									{
										e.printStackTrace();
									}
								}
								catch (NullPointerException e)
								{
									this.sendError(e);
									if(Config.debugMode)
									{
										e.printStackTrace();
									}
								}
							}
							else
							{
								this.stoped = true;
							}
						}
					} 
					catch (IOException e) 
					{
						this.sendError(e);
						if(this.debugMode)
						{
							e.printStackTrace();
						}
						return this.reconnect(this.countDownReconnect);
					}
				}
			}
			else
			{

				return this.reconnect(this.countDownReconnect);
			}		
			return this.isConnected;
		}
	}
	/**
	 * Disconnect from notification server
	 * @return true if success and false if failed
	 */
	public boolean disconnect()
	{
		if(this.ssl)
		{
			try 
			{
				this.socketSSL.close();
				this.connected = false;
			} 
			catch (IOException e) 
			{
				this.sendError(e);
			}
		}
		else
		{
			try 
			{
				this.socket.close();
				this.connected = false;
			} 
			catch (IOException e) 
			{
				this.sendError(e);
			}			
		}
		return true;
	}
	/**
	 * Process incoming data
	 * @param headers Headers
	 * @param body Body
	 * @throws NotificationException 
	 * @throws NoSuchPaddingException 
	 * @throws NoSuchAlgorithmException 
	 * @throws InvalidKeyException 
	 * @throws BadPaddingException 
	 * @throws IllegalBlockSizeException 
	 * @throws IOException 
	 */
	private void processPush(String[] headers, String body) throws NotificationException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException, NegativeArraySizeException, NullPointerException, IOException
	{
		String command = Utility.getFirst(headers, "Command");
		String secure = Utility.getFirst(headers, "Content-Secure");
		boolean encrypted = false;
		if(secure.length() > 0)
		{
			if(secure.toLowerCase().equals("yes"))
			{
				encrypted = true;
				String encryptionKey1 = Config.encryptionKey;
				String encryptionKey2 = "";
				encryptionKey2 = Utility.sha1(Config.password);
				Encryption encryption = new Encryption(encryptionKey1+encryptionKey2);
				body = encryption.decrypt(body, true);
			}
		}
		this.onDataReceived(headers, command, body);
		String messageType = Utility.getFirst(headers, "Content-Type");
		String commandLower = command.toLowerCase().trim();
		if(commandLower.equals("notification"))
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
					this.onNotificationReceived(remoteMessage);
				}			
			}
			catch(JSONException e)
			{
				this.sendError(e);
				if(encrypted)
				{
					throw new InvalidKeyException("Data is encrypted. Make sure the key is valid.");
				}
				else
				{
					throw new NotificationException("Data must be a valid JSONArray");
				}
			}
		}
		else if(commandLower.equals("delete-notification"))
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
					this.onNotificationDeleted(messageID);
				}
			}
			catch(JSONException e)
			{
				this.sendError(e);
				if(encrypted)
				{
					throw new InvalidKeyException("Data is encrypted. Make sure the key is valid.");
				}
				else
				{
					throw new NotificationException("Data must be a valid JSONArray");
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
				long waitToNext = jo.optLong("waitToNext", 0);
				int timeZone = jo.getInt("timeZone");
				this.onNewToken(token, time, waitToNext, timeZone);
				// If within waitToNext milliseconds the client does not receive the token, the client will reconnect to the server
				if(Config.recheckConnection && waitToNext > 0)
				{
					this.connectionChecker = new ConnectionChecker(this, waitToNext);
					this.connectionChecker.start();
				}
			}
			catch(JSONException e)
			{
				this.sendError(e);
				if(encrypted)
				{
					throw new InvalidKeyException("Data is encrypted. Make sure the key is valid.");
				}
				else
				{
					throw new NotificationException("Data must be a valid JSONArray");
				}
			}
		}
		else if(commandLower.equals("unregister-device-success"))
		{
			JSONObject jo;
			try
			{
				jo = new JSONObject(body);
				int responseCode = jo.optInt("responseCode", 0);
				String message = jo.optString("message", "");
				String deviceID = jo.optString("deviceID", "");
				this.onUnregisterDeviceSuccess(deviceID, responseCode, message);
			}
			catch(JSONException e)
			{
				this.sendError(e);
				if(encrypted)
				{
					throw new InvalidKeyException("Data is encrypted. Make sure the key is valid.");
				}
				else
				{
					throw new NotificationException("Data must be a valid JSONArray");
				}
			}
		}
		else if(commandLower.equals("register-device-success"))
		{
			JSONObject jo;
			try
			{
				jo = new JSONObject(body);
				int responseCode = jo.optInt("responseCode", 0);
				String message = jo.optString("message", "");
				String deviceID = jo.optString("deviceID", "");
				this.onRegisterDeviceSuccess(deviceID, responseCode, message);
			}
			catch(JSONException e)
			{				
				this.sendError(e);
				if(encrypted)
				{
					throw new InvalidKeyException("Data is encrypted. Make sure the key is valid.");
				}
				else
				{
					throw new NotificationException("Data must be a valid JSONArray");
				}
			}
		}
		else if(commandLower.equals("register-device-error"))
		{
			JSONObject jo;
			try
			{
				jo = new JSONObject(body);
				int responseCode = jo.optInt("responseCode", 0);
				String message = jo.optString("message", "");
				String deviceID = jo.optString("deviceID", "");
				String cause = jo.optString("cause", "");
				this.onRegisterDeviceError(deviceID, responseCode, message, cause);
			}
			catch(JSONException e)
			{
				this.sendError(e);
				if(encrypted)
				{
					throw new InvalidKeyException("Data is encrypted. Make sure the key is valid.");
				}
				else
				{
					throw new NotificationException("Data must be a valid JSONArray");
				}
			}
		}
		else if(commandLower.equals("unregister-device-error"))
		{
			JSONObject jo;
			try
			{
				jo = new JSONObject(body);
				int responseCode = jo.optInt("responseCode", 0);
				String message = jo.optString("message", "");
				String deviceID = jo.optString("deviceID", "");
				String cause = jo.optString("cause", "");
				this.onUnregisterDeviceError(deviceID, responseCode, message, cause);
			}
			catch(JSONException e)
			{
				this.sendError(e);
				if(encrypted)
				{
					throw new InvalidKeyException("Data is encrypted. Make sure the key is valid.");
				}
				else
				{
					throw new NotificationException("Data must be a valid JSONArray");
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
				this.sendError(e);
				if(encrypted)
				{
					throw new InvalidKeyException("Data is encrypted. Make sure the key is valid.");
				}
				else
				{
					throw new NotificationException("Data must be a valid JSONArray");
				}
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
				this.sendError(e);
				throw new NotificationException("Data must be a valid JSONObject");
			}
		}
		else if(commandLower.equals("key"))
		{
			JSONObject jo;
			try
			{
				jo = new JSONObject(body);
				String key = jo.optString("key", "").trim();
				if(key.length() > 0)
				{
					Config.encryptionKey = key;
				}
			}
			catch(JSONException e)
			{
				this.sendError(e);
				throw new NotificationException("Data must be a valid JSONObject");
			}
		}
	}
	/**
	 * Apply new setting sent from the server
	 * @param name Setting name
	 * @param type Data type
	 * @param value Setting value
	 */
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
	/**
	 * Answer the question sent from the server 
	 * @param deviceID Device ID
	 * @param question Question
	 */
	private void answerQuestion(String deviceID, String question) 
	{
		String command = "answer";
		String hashPassword = "";
		try 
		{
			hashPassword = Utility.sha1(this.password);
		} 
		catch (NoSuchAlgorithmException e) 
		{
			if(Config.debugMode)
			{
				e.printStackTrace();
			}
		}
		String answer = "";
		try 
		{
			answer = Utility.sha1((hashPassword+"-"+question+"-"+deviceID));
		} 
		catch (NoSuchAlgorithmException e) 
		{
			if(Config.debugMode)
			{
				e.printStackTrace();
			}
		}
		if(this.ssl)
		{
			SocketIOSSL socketIO = new SocketIOSSL(this.socketSSL);
			socketIO.resetRequestHeader();
			socketIO.addRequestHeader("Command", command);
			socketIO.addRequestHeader("Content-Type", "application/json");
			try 
			{
				JSONObject data = new JSONObject();
				data.put("deviceID", deviceID);
				data.put("answer", answer);
				data.put("question", question);
				String data2sent = data.toString();
				String[] headers = socketIO.getRequestHeaderArray();
				this.onDataSent(headers, command, data2sent);
				socketIO.write(data2sent);
			}
			catch(IOException e)
			{
				this.sendError(e);
			}
		}
		else
		{
			SocketIO socketIO = new SocketIO(this.socket);
			socketIO.resetRequestHeader();
			socketIO.addRequestHeader("Command", command);
			socketIO.addRequestHeader("Content-Type", "application/json");
			try 
			{
				JSONObject data = new JSONObject();
				data.put("deviceID", deviceID);
				data.put("answer", answer);
				data.put("question", question);
				String data2sent = data.toString();
				String[] headers = socketIO.getRequestHeaderArray();
				this.onDataSent(headers, command, data2sent);
				socketIO.write(data2sent);
			}
			catch(IOException e)
			{
				this.sendError(e);
			}			
		}
	}
	/**
	 * Create request
	 * @return Request to the notification server
	 */
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
			this.sendError(e);
		}
		result = jo.toString();
		return result;
	}
	/**
	 * Register device
	 * @param deviceID Device ID
	 * @return true if success and false if failed
	 */
	public boolean registerDevice(String deviceID)
	{
		if(!this.isConnected)
		{
			this.connect();
		}
		if(this.isConnected)
		{
			String command = "register-device";
			SocketIO socketIO = new SocketIO(this.socketSSL);
			socketIO.resetRequestHeader();
			socketIO.addRequestHeader("Command", command);
			socketIO.addRequestHeader("Authorization", "key="+this.apiKey);
			boolean success = false;
			try 
			{
				JSONObject data = new JSONObject();
				data.put("deviceID", deviceID);
				success = socketIO.write(data.toString());
				String[] headers = socketIO.getRequestHeaderArray();
				this.onDataSent(headers, command, data.toString());
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
				this.sendError(e);
				if(this.debugMode)
				{
					e.printStackTrace();
				}
				try 
				{
					this.socketSSL.close();
				} 
				catch (IOException e1) 
				{
					this.sendError(e1);
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
	/**
	 * Unregister device
	 * @param deviceID Device ID
	 * @return true if success and false if failed
	 * @throws IOException if any IO errors
	 */
	public boolean unregisterDevice(String deviceID)
	{
		if(!this.isConnected)
		{
			this.connect();
		}
		if(this.isConnected)
		{
			String command = "unregister-device";
			SocketIO socketIO = new SocketIO(this.socketSSL);
			socketIO.resetRequestHeader();
			socketIO.addRequestHeader("Command", command);
			socketIO.addRequestHeader("Authorization", "key="+this.apiKey);
			boolean success = false;
			try 
			{
				JSONObject data = new JSONObject();
				data.put("deviceID", deviceID);
				String[] headers = socketIO.getRequestHeaderArray();
				this.onDataSent(headers, command, data.toString());
				success = socketIO.write(data.toString());
				if(success)
				{
					this.onUnregisterDeviceSendSuccess(deviceID, "Data sent to notification server");
					return true;
				}
				else
				{
					this.onUnregisterDeviceSendError(deviceID, "Failed", "Data not sent to notification server");
					return false;
				}
			} 
			catch (IOException e) 
			{
				this.onUnregisterDeviceSendError(deviceID, "Failed", e.getMessage());
				this.sendError(e);
				if(this.debugMode)
				{
					e.printStackTrace();
				}
				try 
				{
					this.socketSSL.close();
				} 
				catch (IOException e1) 
				{
					this.sendError(e1);
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
			this.onUnregisterDeviceSendError(deviceID, "Failed", "Not connected");
			return false;
		}
	}
	/**
	 * Register device ID and user ID to application server
	 * @param url URL of the application server
	 * @param cookie Cookie
	 * @param deviceID Device ID
	 * @param userID User ID
	 * @param password User password
	 * @param group User group
	 * @return HTTPResponse contains server response
	 */
	public HTTPResponse registerDeviceApps(String url, String deviceID, String group, String cookie, String userID, String password) 
	{
		String postData = "";
		try 
		{
			deviceID = Utility.urlEncode(deviceID);
		} 
		catch (UnsupportedEncodingException e) 
		{
			this.sendError(e);
			if(Config.debugMode)
			{
				e.printStackTrace();
			}			
		}
		try 
		{
			userID = Utility.urlEncode(userID);
		} 
		catch (UnsupportedEncodingException e) 
		{
			this.sendError(e);
			if(Config.debugMode)
			{
				e.printStackTrace();
			}			
		} 
		try 
		{
			password = Utility.urlEncode(password);
		} 
		catch (UnsupportedEncodingException e) 
		{
			this.sendError(e);
			if(Config.debugMode)
			{
				e.printStackTrace();
			}			
		}
		try 
		{
			group = Utility.urlEncode(group);
		} 
		catch (UnsupportedEncodingException e) 
		{
			this.sendError(e);
			if(Config.debugMode)
			{
				e.printStackTrace();
			}			
		}
		postData += "device_id="+deviceID+"&";
		if(userID.length() > 0)
		{
			postData += "user_id="+userID+"&";
		}
		if(password.length() > 0)
		{
			postData += "password="+password+"&";
		}
		if(group.length() > 0)
		{
			postData += "group="+group+"&";
		}
		postData += "action=register";
		HTTPResponse response = new HTTPResponse();
		HTTPClient httpClient = new HTTPClient();
		try 
		{
			response = httpClient.post(url, postData, cookie);
		} 
		catch (IOException e) 
		{
			this.sendError(e);
		}
		return response;
	}
	/**
	 * Unregister device ID and user ID to application server
	 * @param url URL of the application server
	 * @param cookie Cookie
	 * @param deviceID Device ID
	 * @param userID User ID
	 * @param password User password
	 * @param group User group
	 * @return HTTPResponse contains server response
	 */
	public HTTPResponse unregisterDeviceApps(String url, String deviceID, String group, String cookie, String userID, String password) 
	{
		String postData = "";
		try 
		{
			deviceID = Utility.urlEncode(deviceID);
		} 
		catch (UnsupportedEncodingException e) 
		{
			this.sendError(e);
			if(Config.debugMode)
			{
				e.printStackTrace();
			}			
		}
		try 
		{
			userID = Utility.urlEncode(userID);
		} 
		catch (UnsupportedEncodingException e) 
		{
			this.sendError(e);
			if(Config.debugMode)
			{
				e.printStackTrace();
			}			
		} 
		try 
		{
			password = Utility.urlEncode(password);
		} 
		catch (UnsupportedEncodingException e) 
		{
			this.sendError(e);
			if(Config.debugMode)
			{
				e.printStackTrace();
			}			
		}
		try 
		{
			group = Utility.urlEncode(group);
		} 
		catch (UnsupportedEncodingException e) 
		{
			this.sendError(e);
			if(Config.debugMode)
			{
				e.printStackTrace();
			}			
		}
		postData += "device_id="+deviceID+"&";
		postData += "user_id="+userID+"&";
		postData += "password="+password+"&";
		postData += "group="+group+"&";
		postData += "action=unregister";
		HTTPResponse response = new HTTPResponse();
		HTTPClient httpClient = new HTTPClient();
		try 
		{
			response = httpClient.post(url, postData, cookie);
		} 
		catch (IOException e) 
		{
			this.sendError(e);
		}
		return response;
	}
	/**
	 * Check user authentication without user ID, password and group. The server will use session data saved according to cookie from the client.
	 * @param url URL of the application server
	 * @param cookie Cookie
	 * @return HTTPResponse contains header, body and cookie
	 */
	public HTTPResponse auth(String url, String cookie) 
	{
		String postData = "";
		postData += "action=login";
		HTTPResponse response = new HTTPResponse();
		HTTPClient httpClient = new HTTPClient();
		try 
		{
			response = httpClient.post(url, postData, cookie);
		} 
		catch (IOException e) 
		{
			this.sendError(e);
		}	
		return response;
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
		String postData = "";
		try 
		{
			userID = Utility.urlEncode(userID);
		} 
		catch (UnsupportedEncodingException e) 
		{
			this.sendError(e);
			if(Config.debugMode)
			{
				e.printStackTrace();
			}			
		} 
		try 
		{
			password = Utility.urlEncode(password);
		} 
		catch (UnsupportedEncodingException e) 
		{
			this.sendError(e);
			if(Config.debugMode)
			{
				e.printStackTrace();
			}			
		}
		try 
		{
			group = Utility.urlEncode(group);
		} 
		catch (UnsupportedEncodingException e) 
		{
			this.sendError(e);
			if(Config.debugMode)
			{
				e.printStackTrace();
			}			
		}
		
		postData += "user_id="+userID+"&";
		postData += "password="+password+"&";
		postData += "group="+group+"&";
		postData += "action=login";
		HTTPResponse response = new HTTPResponse();
		HTTPClient httpClient = new HTTPClient();
		try 
		{
			response = httpClient.post(url, postData, cookie);
		} 
		catch (IOException e) 
		{
			this.sendError(e);
		}	
		return response;
	}
	/**
	 * Logout by destroying session data on the server
	 * @param url URL of the application server
	 * @param cookie Cookie
	 * @return HTTPResponse contains header, body and cookie
	 */
	public HTTPResponse logout(String url, String cookie) 
	{
		String postData = "";
		postData += "action=logout";
		HTTPResponse response = new HTTPResponse();
		HTTPClient httpClient = new HTTPClient();
		try 
		{
			response = httpClient.post(url, postData, cookie);
		} 
		catch (IOException e) 
		{
			this.sendError(e);
		}	
		return response;
	}
	/**
	 * Get last error
	 * @return Last exception sent via sendError
	 */
	public Exception getLastError()
	{
		return this.lastError;
	}
	/**
	 * Send error
	 * @param exception Exception
	 */
	private void sendError(Exception exception)
	{
		this.lastError = exception;
		this.onError(exception);
	}
	/**
	 * Get server address
	 * @return Server address
	 */
	public String getServerAddress() 
	{
		return this.serverAddress;
	}
	/*
	 * Set server address
	 */
	public void setServerAddress(String serverAddress) 
	{
		this.serverAddress = serverAddress;
	}
	/**
	 * Get server port
	 * @return Server port
	 */
	public int getServerPort() 
	{
		return this.serverPort;
	}
	/**
	 * Set server port
	 * @param serverPort Server port
	 */
	public void setServerPort(int serverPort) 
	{
		this.serverPort = serverPort;
	}
	/**
	 * Get delay to restart the client
	 * @return Delay to restart the client
	 */
	public long getDelayRestart() 
	{
		return this.delayRestart;
	}
	/**
	 * Set delay to restart the client
	 * @param delayRestart Delay to restart the client
	 */
	public void setDelayRestart(int delayRestart) 
	{
		this.delayRestart = delayRestart;
	}
	/**
	 * Get delay to reconnect to the server
	 * @return Delay to reconnect to the server
	 */
	public long getDelayReconnect() 
	{
		return this.delayReconnect;
	}
	/**
	 * Set delay to reconnect to the server
	 * @param delayReconnect Delay to reconnect to the server
	 */
	public void setDelayReconnect(int delayReconnect) 
	{
		this.delayReconnect = delayReconnect;
	}
	/**
	 * Get time out
	 * @return Time out
	 */
	public int getTimeout() 
	{
		return timeout;
	}
	/**
	 * Set time out
	 * @param timeout Time out
	 */
	public void setTimeout(int timeout) 
	{
		this.timeout = timeout;
	}
	/**
	 * Set API to run on debug mode
	 * @param debugMode Debug mode
	 */
	public void setDebugMode(boolean debugMode)
	{
		this.debugMode = debugMode;
	}
	public void onNotificationReceived(RemoteMessage notification)
	{	
		
	}
	public void onNotificationDeleted(String notificationID)
	{		
		
	}
	public void onMessageReceived(RemoteMessage message)
	{	
		
	}
	public void onMessageDeleted(String messageID)
	{		
		
	}
	public void onDataReceived(String[] headers, String command, String body)
	{
		
	}
	public void onDataSent(String[] headers, String command, String body)
	{
		
	}
	public void onNewToken(String token, String time, long waitToNext, int timeZone)
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
	public void onRegisterDeviceSuccess(String deviceID, int responseCode, String message)
	{
		
	}
	public void onRegisterDeviceError(String deviceID, int responseCode, String message, String cause)
	{
		
	}
	public void onUnregisterDeviceSuccess(String deviceID, int responseCode, String message)
	{
		
	}
	public void onUnregisterDeviceError(String deviceID, int responseCode, String message, String cause)
	{
		
	}
}
