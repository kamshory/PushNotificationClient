package com.planetbiru.pushclient.util;

import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class SocketIO {
	public Socket socket;
	public String header = "";
	public String body = "";
	public long contentLength = 0;
	public Map<String, String> responseHeader = new HashMap<String, String>();
	public Map<String, String> requestHeader = new HashMap<String, String>();
	public String rawRequestHeader = "";
	public SocketIO()
	{
		
	}
	public SocketIO(Socket socket)
	{
		this.socket = socket;
	}
	
	public void resetResponseHeader()
	{
		this.responseHeader = new HashMap<String, String>();
	}
	public void addResponseHeader(String key, String value)
	{
		this.responseHeader.put(key, value);
	}
	public Map<String, String> getResponseHeader()
	{
		return this.responseHeader;
	}
	public String getResponseHeader(String key)
	{
		return this.responseHeader.getOrDefault(key, "");
	}
	public String getResponseHeader(String key, String defaultValue)
	{
		return this.responseHeader.getOrDefault(key, defaultValue);
	}
	
	public void resetRequestHeader()
	{
		this.requestHeader = new HashMap<String, String>();
	}
	public void addRequestHeader(String key, long value)
	{
		this.requestHeader.put(key, value+"");
	}
	public void addRequestHeader(String key, String value)
	{
		this.requestHeader.put(key, value);
	}
	public Map<String, String> getRequestHeader()
	{
		return this.requestHeader;
	}
	public String getRequestHeader(String key)
	{
		return this.requestHeader.getOrDefault(key, "");
	}
	public String getRequestHeader(String key, String defaultValue)
	{
		return this.requestHeader.getOrDefault(key, defaultValue);
	}
	public String last(String data, int length)
	{
		if(data.length() < length)
		{
			return "";
		}
		byte[] b = data.getBytes();
		int dataLength = b.length;
		int offset = dataLength - length;
		String result = "";
		int i;
		for(i = offset; i<dataLength; i++ )
		{
			result += String.format("%c", b[i]);
		}
		return result;
	}
	public String readResponseHeader() throws IOException
	{
		int buf;
		String data = "";
		do
		{
			buf = this.socket.getInputStream().read();
			if(buf >= 0)
			{
				data += String.format("%c", buf);
			}
		}
		while(!this.last(data, 4).equals("\r\n\r\n") && buf > -1);
		data = data.trim();
		this.rawRequestHeader = data;

		this.header = data;
		return data;
	}
	public String getFirst(String[] headers, String key)
	{
		int i;
		String value = "";
		String line = "";
		String[] arr;
		String key2 = "";
		for(i = 0; i<headers.length; i++)
		{
			line = headers[i].trim();
			if(line.contains(":"))
			{
				arr = line.split("\\:", 2);
				key2 = arr[0].trim();
				if(key2.compareToIgnoreCase(key) == 0)
				{
					value = arr[1].trim();
				}
			}
		}
		return value;
	}
	public Map<String, String> readRequestHeader() throws IOException
	{
		String data = this.readResponseHeader();
	    String[] lines = data.split("\\r\\n"); // split on new lines
	    Map<String, String> header = new HashMap<String, String>();
	    int i;
	    String line = "";
	    String[] arr;
	    for(i = 0; i < lines.length; i++)
	    {
	    	line = lines[i].trim();
	    	if(line.contains(":"))
	    	{
	    		arr = line.split("\\:", 2);
	    		header.put(arr[0].trim(), arr[1].trim());
	    	}
	    }
	    this.requestHeader = header;
		return header;
	}
	public long getRequestDataLength()
	{
		String header = this.rawRequestHeader;
		long length = 0;
		if(header.length() > 1)
		{
			String[] lines = header.split("\\r?\\n");
			int i;
			String line;
			String x;
			for(i = 0; i<lines.length; i++)
			{
				line = lines[i];
				if(line.toLowerCase().contains("content-length") && line.toLowerCase().contains(":"))
				{
					x = line.trim();
					x = x.toLowerCase();
					x = x.replace("content-length", "").replace(":", "").trim();
					length = Long.parseLong(x.replaceAll("\\\r", "").replaceAll("\\\n", "").trim());
				}
			}
			this.contentLength = length;
		}
		else
		{
			length = -1;
		}
		return length;
	}
	private String getBody(long length)
	{
		int buf;
		long i;
		String data = "";
		try 
		{
			for(i = 0; i < length; i++)
			{
				buf = this.socket.getInputStream().read();
				data += String.format("%c", buf);
			}
			this.contentLength = length;
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		return data;
	}
	public String[] getHeaders()
	{
		String[] lines = this.header.split("\\r\\n");
		return lines;
	}
	public String getBody()
	{
		return this.body;
	}
	public boolean read() throws IOException
	{
		return this.read(this.socket);
	}
	public boolean read(Socket socket) throws IOException
	{
		this.socket = socket;
		this.readResponseHeader();
		long length = this.getRequestDataLength();
		if(length > -1)
		{
			this.body = this.getBody(length);
		}
		else
		{
			this.body = "";
			this.contentLength = 0;
			return false;
		}
		return true;
	}
	public boolean write(String data) throws IOException
	{
		return this.write(this.socket, data);
	}
	public boolean write(Socket socket, String data) throws IOException
	{
		if(socket.isConnected() && !socket.isClosed())
		{
			String hdr = "";
			this.addRequestHeader("Content-Length", data.length());
			String data2sent = "";
			if(!this.requestHeader.isEmpty())
			{
				 Set<Map.Entry<String, String>> entrySet = this.requestHeader.entrySet();
				 for (Entry<String, String> entry : entrySet) 
				 {
				    hdr = entry.getKey().toString().trim()+": "+entry.getValue().toString().trim()+"\r\n";
				    data2sent += hdr;
				 }
			}
			data2sent += "\r\n";
			data2sent += data;
			socket.getOutputStream().write(data2sent.getBytes());

			return true;
		}
		else
		{
			return false;
		}
	}
}
