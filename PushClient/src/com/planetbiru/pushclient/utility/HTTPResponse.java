package com.planetbiru.pushclient.utility;

import java.lang.reflect.Field;
import java.security.cert.Certificate;

public class HTTPResponse 
{
	public String header = "";
	public String cookie = "";
	public String body = "";
	public int responseCode = 200;
	public String cipherSuite = "";
	public Certificate[] serverCertificates = null;
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
