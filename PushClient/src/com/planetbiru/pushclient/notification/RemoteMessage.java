package com.planetbiru.pushclient.notification;

import java.lang.reflect.Field;
import java.net.URI;
import java.net.URISyntaxException;

import org.json.JSONObject;

import com.planetbiru.pushclient.config.Config;
import com.planetbiru.pushclient.util.Utility;

/**
 * RemoteMessage
 * @author Kamshory, S.T,M.T.
 *
 */
public class RemoteMessage {
	/**
	 * Notification
	 */
	public Notification notification = new Notification();
	/**
	 * Content type
	 */
	public String contentType = "";
	JSONObject messageRaw = new JSONObject();
	/**
	 * Default constructor
	 */
	public RemoteMessage()
	{
	}
	/**
	 * Constructor with content type
	 * @param messageType
	 */
	public RemoteMessage(String messageType)
	{
		this.contentType = messageType;
	}
	public RemoteMessage(String messageType, JSONObject data)
	{
		this.contentType = messageType;
		this.buildNotification(data);
	}
	/**
	 * Get notification
	 * @return Notification
	 */
	public Notification getNotification()
	{
		return this.notification;
	}
	/**
	 * Get content type
	 * @return Content type
	 */
	public String getContentType()
	{
		return this.contentType ;
	}
	/**
	 * Get message ID
	 * @return
	 */
	public String getMessageId()
	{
		return this.notification.id ;
	}
	public JSONObject getMessageRaw()
	{
		return this.messageRaw;
	}
	/**
	 * Build notification
	 * @param data Notification
	 */
	private void buildNotification(JSONObject data)
	{
		this.messageRaw = new JSONObject(data.toString());
		this.notification = new Notification();
		this.notification.id = data.optString("id", "");
		this.notification.type = data.optString("type", "");
		this.notification.title = data.optString("title", "");
		this.notification.body = data.optString("message", "");
		this.notification.subtitle  = data.optString("subtitle", "");
		this.notification.smallIcon = data.optString("smallIcon", "");
		this.notification.largeIcon  = data.optString("largeIcon", "");
		this.notification.icon = data.optString("smallIcon", "");
		this.notification.sound = data.optString("sound", "");
		this.notification.vibrate  = data.optString("vibrate", "");
		this.notification.color  = data.optString("color", "");
		this.notification.tickerText  = data.optString("tickerText", "");
		this.notification.time  = data.optString("time", "");
		this.notification.timeZone  = data.optInt("timeZone", 0);
		this.notification.badge  = data.optString("badge", "");
		this.notification.clickAction   = data.optString("clickAction", "");
		this.notification.miscData    = data.optString("miscData", "");
		try 
		{
			this.notification.uri = new URI(data.optString("uri", ""));
		} 
		catch (URISyntaxException e) 
		{
			if(Config.debugMode)
			{
				e.printStackTrace();
			}
			try 
			{
				this.notification.uri = new URI("");
			} 
			catch (URISyntaxException e1) 
			{
				if(Config.debugMode)
				{
					e1.printStackTrace();
				}
			}
		}
	}
	/**
	 * Notification
	 * @author Kamshory, S.T,M.T.
	 *
	 */
	public class Notification 
	{
		/**
		 * Miscellaneous data. You can define yourself
		 */
		public String miscData = "";
		/**
		 * Notification type. It can be the category of the notification
		 */
		public String type = "";
		/**
		 * Creation time of the notification
		 */
		public String time = "";
		/**
		 * Ticket text
		 */
		public String tickerText = "";
		/**
		 * Large icon. You can use local icon, remote icon, or encoded icon data. The format is according to your application
		 */
		public String largeIcon = "";
		/**
		 * Small icon. You can use local icon, remote icon, or encoded icon data. The format is according to your application
		 */
		public String smallIcon = "";
		/**
		 * Subtitle of the notification
		 */
		public String subtitle = "";
		/**
		 * Vibration of the notification. You can customize the vibration
		 */
		public String vibrate = "";
		/**
		 * The body of the message
		 */
		public String body = "";
		
		public String[] bodyLocalizationArgs;
		public String bodyLocalizationKey = "";
		/**
		 * Action doing when user click the notification
		 */
		public String clickAction = "";
		/**
		 * Color of the notification. You can customize the color
		 */
		public String color = "";
		/**
		 * Small icon. You can use local icon, remote icon, or encoded icon data. The format is according to your application
		 */
		public String icon = "";
		/**
		 * The related URI to the notification
		 */
		public URI uri;
		/**
		 * Sound of the notification. You can customize the sound
		 */
		public String sound = "";
		/**
		 * Tag of the notification
		 */
		public String tag = "";
		/**
		 * Title of the notification
		 */
		public String title = "";
		public String[] titleLocalizationArgs;
		public String titleLocalizationKey = "";
		/**
		 * Message ID
		 */
		public String id = "";
		/**
		 * Badge of the notification. You can customize the badge
		 */
		public String badge = "";
		/**
		 * Time zone offset of the server. All time on the message is in local. You can convert it in to local time according to the device time zone
		 */
		public int timeZone = 0;
		/**
		 * Get body
		 * @return
		 */
		public String getBody()
		{
			return this.body;
		}
		public String[] getBodyLocalizationArgs()
		{
			return this.bodyLocalizationArgs;
		}
		public String getBodyLocalizationKey()
		{
			return this.bodyLocalizationKey;
		}
		/**
		 * Get click action
		 * @return Click action
		 */
		public String getClickAction()
		{
			return this.clickAction;
		}
		/**
		 * Get color 
		 * @return Color
		 */
		public String getColor()
		{
			return this.color;
		}
		/**
		 * Get icon
		 * @return Icon
		 */
		public String getIcon()
		{
			return this.icon;
		}
		/**
		 * Get small icon
		 * @return Small icon
		 */
		public String getSmallIcon()
		{
			return this.smallIcon;
		}
		/**
		 * Get large icon
		 * @return Large icon
		 */
		public String getLargeIcon()
		{
			return this.largeIcon;
		}
		/**
		 * Get link
		 * @return URI link
		 */
		public URI getLink()
		{
			return this.uri;
		}
		/**
		 * Get sound
		 * @return Sound
		 */
		public String getSound()
		{
			return this.sound;
		}
		/**
		 * Get vibrate
		 * @return Vibrate
		 */
		public String getVibrate()
		{
			return this.vibrate;
		}
		/**
		 * Get tag
		 * @return Tag
		 */
		public String getTag()
		{
			return this.tag;
		}
		/**
		 * Get time
		 * @return Time
		 */
		public String getTime()
		{
			return this.time;
		}
		/**
		 * Get time zone
		 * @return Time zone
		 */
		public int getTimeZone()
		{
			return this.timeZone;
		}
		/**
		 * Get title
		 * @return Title
		 */
		public String getTitle()
		{
			return this.title;
		}
		/**
		 * Get subtitle
		 * @return Subtitle
		 */
		public String getSubtitle()
		{
			return this.subtitle;
		}
		/**
		 * Get type
		 * @return Type
		 */
		public String getType()
		{
			return this.type;
		}
		/**
		 * Get miscellaneous data 
		 * @return Miscellaneous data
		 */
		public String getMiscData()
		{
			return this.miscData;
		}
		public String[] getTitleLocalizationArgs()
		{
			return this.titleLocalizationArgs;
		}
		public String getTitleLocalizationKey()
		{
			return this.titleLocalizationKey;
		}
		public String toString()
		{
			Field[] fields = this.getClass().getDeclaredFields();
			int i, max = fields.length;
			String fieldName = "";
			String fieldType = "";
			String ret = "";
			String value = "";
			boolean skip = false;
			int j = 0;
			for(i = 0; i < max; i++)
			{
				fieldName = fields[i].getName().toString();
				fieldType = fields[i].getType().toString();
				if(i == 0)
				{
					ret += "{";
				}
				if(fieldType.equals("int") || fieldType.equals("long") || fieldType.equals("float") || fieldType.equals("double") || fieldType.equals("boolean"))
				{
					try 
					{
						value = fields[i].get(this).toString();
					}  
					catch (Exception e) 
					{
						value = "0";
					}
					skip = false;
				}
				else if(fieldType.contains("String"))
				{
					try 
					{
						value = "\""+Utility.escapeJSON((String) fields[i].get(this))+"\"";
					} 
					catch (Exception e) 
					{
						value = "\""+"\"";
					}
					skip = false;
				}
				else if(fieldType.contains("URI"))
				{
					try 
					{
						value = "\""+Utility.escapeJSON(fields[i].get(this).toString())+"\"";
					} 
					catch (Exception e) 
					{
						value = "\""+"\"";
					}
					skip = false;
				}
				else
				{
					value = "\""+"\"";
					skip = true;
				}
				if(!skip)
				{
					if(j > 0)
					{
						ret += ",";
					}
					j++;
					ret += "\r\n\t\""+fieldName+"\":"+value;
				}
				if(i == max-1)
				{
					ret += "\r\n}";
				}
			}
			return ret;
		}
	}
}
