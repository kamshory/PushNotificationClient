package com.planetbiru.pushclient.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.json.JSONObject;


public class Utility {
	public Utility()
	{
		
	}
	/**
	 * Get first value of specified header
	 * @param headers Array string contains header
	 * @param key Header key
	 * @return Value of the header
	 */
	public static String getFirst(String[] headers, String key)
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
	/**
	 * Parse query string
	 * @param query String contains query
	 * @return JSONObject contains the query
	 */
	public static JSONObject parseQuery(String query)
    {
    	JSONObject json = new JSONObject();
    	if(query != null)
    	{
        	if(query.length() > 0)
        	{
		    	String[] args;
		    	int i;
		    	String arg = "";
		    	String[] arr;
		    	String key = "";
		    	String value = "";
	    		if(query.contains("&"))
	    		{
	    			args = query.split("&");
	    		}
	    		else
	    		{
	    			args = new String[1];
	    			args[0] = query;
	    		}
	    		for(i = 0; i<args.length; i++)
	    		{
	    			arg = args[i];
	    			if(arg.contains("="))
	    			{
	    				arr = arg.split("=", 2);
	    				key = arr[0];
	    				value = Utility.urlDecode(arr[1]);
	    				json.put(key, value);
	    			}
	    		}
	    	}
    	}
    	return json;
    }
	/**
	 * Encode URI
	 * @param input Raw URI
	 * @return Encoded URI
	 */
	public static String urlEncode(String input) 
	{
	   	String result = "";
		try 
		{
			result = java.net.URLEncoder.encode(input, "UTF-8");
		} 
		catch (UnsupportedEncodingException e) 
		{
			e.printStackTrace();
		}
    	return result;
	}
	/**
	 * Decode URI
	 * @param input Encoded URI
	 * @return Raw URI
	 */
	public static String urlDecode(String input)
    {
    	String result = "";
		try 
		{
			result = java.net.URLDecoder.decode(input, "UTF-8");
		} 
		catch (UnsupportedEncodingException e) 
		{
			e.printStackTrace();
		}
    	return result;
    }
	/**
	 * Escape SQL
	 * @param input Raw data to be escaped
	 * @return Escaped data
	 */
	public static String escapeSQL(String input)
	{
		return Utility.escapeSQL(input, "mysql");
	}
	/**
	 * Escape SQL
	 * @param input Raw data to be escaped
	 * @param databaseType Database type
	 * @return Escaped data
	 */
	public static String escapeSQL(String input, String databaseType)
	{
		String s = input;		
		if(databaseType.equals("mysql"))
		{
			s = s.replaceAll("\\u005c", "\\\\\\\\");
			s = s.replaceAll("\\n", "\\\\n");
		    s = s.replaceAll("\\r", "\\\\r");
		    s = s.replaceAll("\\00", "\\\\0");
		    s = s.replaceAll("'", "\\\\'");
		    s = s.replaceAll("\"", "\\\\\"");
		}
		if(databaseType.equals("postgresql"))
		{
			s = s.replaceAll("\\u005c", "\\\\\\\\");
		    s = s.replaceAll("\\00", "\\\\0");
		    s = s.replaceAll("'", "''");
		    s = s.replaceAll("\"", "\\\\\"");
		}
	    return s;	
	}
	/**
	 * Create header length
	 * @param messageLength Message length (in byte)
	 * @param headerLength Header length (in byte)
	 * @param direction Header direction (true = little endian, false = big endian)
	 * @return Byte represent length of message
	 */
	@SuppressWarnings("unused")
	public static byte[] createISOLength(long messageLength, int headerLength, boolean direction)
	{
		String b;
		String s = "";
		int i;
		long j = 0;
		long k = 0;
		byte[] header = new byte[headerLength];
		if(direction)
		{
			// Little Endian
			j = messageLength;
			for(i = 0; i<headerLength; i++)
			{
				k = j % 256;
				header[i] = (byte) k;
				j = j / 256;
			}
		}
		else
		{
			// Big Endian
			j = messageLength;
			for(i = 0; i<headerLength; i++)
			{
				k = j % 256;
				header[headerLength-i-1] = (byte) k;
				j = j / 256;
			}
		}
		return header;
	}
	/**
	 * Convert integer to byte array
	 * @param i Integer value
	 * @return Byte array
	 */
	public static byte[] integerToByteArray(int i) 
	{
		  byte b[] = new byte[4];		  
		  ByteBuffer buf = ByteBuffer.wrap(b);
		  buf.putInt(i);
		  return b;
	}
	/**
	 * Convert byte array to integer	  
	 * @param b Byte array
	 * @return Integer value
	 */
	public static int byteArrayToInteger(byte[] b) 
	{
		  ByteBuffer buf = ByteBuffer.wrap(b);
		  return buf.getInt();
	}
	/**
	 * Convert array byte to hexadecimal number
	 * @param bytes Byte data
	 * @return Hexadecimal number
	 */
	public static String byteArrayToHexadecimal(byte[] bytes)
	{
		StringBuilder sb = new StringBuilder();
	    for (byte b : bytes) 
	    {
	        sb.append(String.format("%02X ", b & 0xFF));
	    }
	    return sb.toString().trim();
	}
	/**
	 * Get actual message length received from member
	 * @param message Raw message
	 * @param headerLength Header length
	 * @param headerDirectionRequest Header direction. true = LSB left MSB right, false = MSB left LSB right
	 * @return Actual message length
	 */
    public static long getLength(byte[] message, int headerLength, boolean headerDirectionRequest)
    {
      	int i;
    	long result = 0;
    	int x = 0;
    	if(headerDirectionRequest)
    	{
    	   	// Little endian
    		for(i = headerLength-1; i >= 0; i--)
	    	{
	    		result *= 256;
	    		x = (byte) message[i];
	    		if(x < 0)
	    		{
	    			x = x+256;
	    		}
	    		if(x > 256)
	    		{
	    			x = x-256;
	    		}
	    		result += x;
	    	}  		
    	}
    	else
    	{
    		// Big endian
	    	for(i = 0; i < headerLength; i++)
	    	{
	    		result *= 256;
	    		x = (byte) message[i];
	    		if(x < 0)
	    		{
	    			x = x+256;
	    		}
	    		if(x > 256)
	    		{
	    			x = x-256;
	    		}
	    		result += x;
	    	}
    	}
    	return result;
    }
    /**
     * Concate 2 byte array
     * @param firstByte First bytes
     * @param secondByte Second bytes
     * @return Concatenated bytes
     */
	public static byte[] byteConcate(byte[] firstByte, byte[] secondByte)
	{
		byte[] z = new byte[firstByte.length+secondByte.length];
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		try 
		{
			outputStream.write(firstByte);
			outputStream.write(secondByte);
			z = outputStream.toByteArray();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		return z;
	}
    /**
     * Concate 3 byte array
     * @param firstByte First bytes
     * @param secondByte Second bytes
     * @param thirdByte Third bytes
     * @return Concatenated bytes
     */
	public static byte[] byteConcate(byte[] firstByte, byte[] secondByte, byte[] thirdByte)
	{
		byte[] z = new byte[firstByte.length+secondByte.length+thirdByte.length];
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
		try 
		{
			outputStream.write(firstByte);
			outputStream.write(secondByte);
			outputStream.write(thirdByte);
			z = outputStream.toByteArray();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		return z;
	}
    /**
     * Concate 3 byte array
     * @param firstByte First bytes
     * @param secondByte Second bytes
     * @param thirdByte Third bytes
     * @param fourthByte Fourth byte
     * @return Concatenated bytes
     */
	public static byte[] byteConcate(byte[] firstByte, byte[] secondByte, byte[] thirdByte, byte[] fourthByte)
	{
		byte[] z = new byte[firstByte.length+secondByte.length+thirdByte.length];
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
		try 
		{
			outputStream.write(firstByte);
			outputStream.write(secondByte);
			outputStream.write(thirdByte);
			outputStream.write(fourthByte);
			z = outputStream.toByteArray();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		return z;
	}
	/**
	 * Generate SHA-256 hash code from a string
	 * @param input Input string
	 * @return SHA-256 hash code
	 */
	public static String sha256(String input)
	{
		String output = "";
		if(input == null)
		{
			input = "";
		}
		try
		{
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] encodedhash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
			output = Utility.bytesToHex(encodedhash);
			return output;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return "";
		}
	}
	/**
	 * Generate SHA-1 hash code from a string
	 * @param input Input string
	 * @return SHA-1 hash code
	 */
	public static String sha1(String input)
	{
		String output = "";
		if(input == null)
		{
			input = "";
		}
		try
		{
			MessageDigest digest = MessageDigest.getInstance("SHA-1");
			byte[] encodedhash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
			output = Utility.bytesToHex(encodedhash);
			return output;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return "";
		}
	}
	/**
	 * Generate MD5 hash code from a string
	 * @param input Input string
	 * @return MD5 hash code
	 */
	public static String md5(String input)
	{
		String output = "";
		if(input == null)
		{
			input = "";
		}
		try
		{
			MessageDigest digest = MessageDigest.getInstance("MD5");
			byte[] encodedhash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
			output = Utility.bytesToHex(encodedhash);
			return output;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return "";
		}
	}
	/**
	 * Convert byte to hexadecimal number
	 * @param hash Byte to be converted
	 * @return String containing hexadecimal number
	 */
	public static String bytesToHex(byte[] hash) 
	{
	    StringBuffer hexString = new StringBuffer();
	    for (int i = 0; i < hash.length; i++) {
	    String hex = Integer.toHexString(0xff & hash[i]);
	    if(hex.length() == 1) hexString.append('0');
	    	hexString.append(hex);
	    }
	    return hexString.toString();
	}
	/**
	 * Encode string with base 64 encoding
	 * @param input String to be encoded
	 * @return Encoded string
	 */
	public static String base64Encode(String input)
	{
		byte[] encodedBytes = Base64.getEncoder().encode(input.getBytes());
		String output = new String(encodedBytes);
		return output;
	}
	/**
	 * Decode string with base 64 encoding
	 * @param input String to be decoded
	 * @return Decoded string
	 */
	public static String base64Decode(String input)
	{
		byte[] decodedBytes = Base64.getDecoder().decode(input.getBytes());
		String output = new String(decodedBytes);
		return output;
	}
	/**
	 * Get current time with MMddHHmmss format
	 * @return Current time with MMddHHmmss format
	 */
	public static String date10()
	{
		return now("MMddHHmmss");
	}
	public static String date10(String timezone)
	{
		return now("MMddHHmmss", timezone);
	}
	/**
	 * Get current time with MMdd format
	 * @return Current time with MMdd format
	 */
	public static String date4()
	{
		return now("MMdd");
	}
	/**
	 * Get current time with HHmmss format
	 * @return Current time with HHmmss format
	 */
	public static String time6()
	{
		return now("HHmmss");
	}
	/**
	 * Get current time with HHmm format
	 * @return Current time with HHmm format
	 */
	public static String time4()
	{
		return now("HHmm");
	}
	/**
	 * Convert Date10 to MySQL date format (MMddHHmmss to yyyy-MM-dd HH:mm:ss) 
	 * @param datetime Date10
	 * @return MySQL date format
	 */
	public static String date10ToMySQLDate(String datetime)
	{
		return date10ToFullDate(datetime, "yyyy-MM-dd HH:mm:ss");
	}
	/**
	 * Convert Date10 to PgSQL date format (MMddHHmmss to yyyy-MM-dd HH:mm:ss.SSS) 
	 * @param datetime Date10
	 * @return PgSQL date format
	 */
	public static String date10ToPgSQLDate(String datetime)
	{
		return date10ToFullDate(datetime, "yyyy-MM-dd HH:mm:ss.SSS");
	}
	/**
	 * Convert Date10 to full date time
	 * @param datetime Date10
	 * @param format Expected format
	 * @return Full date time format
	 */
	public static String date10ToFullDate(String datetime, String format)
	{
		while(datetime.length() < 10)
		{
			datetime = "0"+datetime;
		}
		String yyyy = now("yyyy");
		DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		String result = "";
	    try 
	    {
	    	// debug year transition
	    	if(datetime.length() > 4)
	    	{
	    		String month1 = datetime.substring(0, 4);
	    		if(month1.equals("1231"))
	    		{
		    		String month2 = now("MMdd");
		    		if(!month2.equals("0101"))
		    		{
		    			int YYYY = Integer.parseInt(yyyy) - 1;
		    			yyyy = YYYY+"";
		    		}
	    		}
	    	}
			Date dateObject = dateFormat.parse(yyyy+datetime);
			dateFormat = new SimpleDateFormat(format);
			result = dateFormat.format(dateObject);
		} 
	    catch (ParseException e) 
	    {
			e.printStackTrace();
			result = MySQLDate();
		}
	    return result;
	}
	/**
	 * Get MySQL format of current time
	 * @return Current time with MySQL format
	 */
	public static String MySQLDate()
	{
		return now("yyyy-MM-dd HH:mm:ss");
	}
	/**
	 * Get PgSQL format of current time
	 * @return Current time with PgSQL format
	 */
	public static String PgSQLDate()
	{
		return now("yyyy-MM-dd HH:mm:ss.SSS");
	}

	/**
	 * Date time
	 * @param format Date time format
	 * @return String contains current date time
	 */
	public static String date(String format)
	{
		String result = "";
		try
		{
			SimpleDateFormat dateFormat = new SimpleDateFormat(format);
			Date dateObject = new Date();
			result = dateFormat.format(dateObject);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return result;
	}
	/**
	 * Date time
	 * @param format Date time format
	 * @param date Date time
	 * @return String contains current date time
	 */
	public static String date(String format, Date date)
	{
		String result = "";
		try
		{
			SimpleDateFormat dateFormat = new SimpleDateFormat(format);
			result = dateFormat.format(date);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return result;
	}
	/**
	 * Date time
	 * @param format Date time format
	 * @param time Unix Timestamp
	 * @return String contains current date time
	 */
	public static String date(String format, long time)
	{
		String result = "";
		try
		{
			SimpleDateFormat dateFormat = new SimpleDateFormat(format);
			Date dateObject = new Date(time);
			result = dateFormat.format(dateObject);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return result;
	}
	/**
	 * Date yesterday
	 * @return Date yesterday
	 */
	public static Date yesterday() 
	{
	    final Calendar cal = Calendar.getInstance();
	    cal.add(Calendar.DATE, -1);
	    return cal.getTime();
	}
	/**
	 * Date tomorrow
	 * @return Date tomorrow
	 */
	public static Date tomorrow()
	{
	    final Calendar cal = Calendar.getInstance();
	    cal.add(Calendar.DATE, +1);
	    return cal.getTime();		
	}
	public static String now()
	{
		String result = "";
		try
		{
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		    Date dateObject = new Date();
		    result = dateFormat.format(dateObject);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return result;
	}
	/**
	 * Get current time with specified format
	 * @param precission Decimal precission
	 * @return Current time with format yyyy-MM-dd
	 */
	public static String now(int precission)
	{
		if(precission > 6)
		{
			precission = 6;
		}
		if(precission < 0)
		{
			precission = 0;
		}
		long decimal = 0;
		long nanoSecond = System.nanoTime();
		if(precission == 6)
		{
			decimal = nanoSecond % 1000000;
		}
		else if(precission == 5)
		{
			decimal = nanoSecond % 100000;
		}
		else if(precission == 4)
		{
			decimal = nanoSecond % 10000;
		}
		else if(precission == 3)
		{
			decimal = nanoSecond % 1000;
		}
		else if(precission == 2)
		{
			decimal = nanoSecond % 100;
		}
		else if(precission == 1)
		{
			decimal = nanoSecond % 10;
		}
		String result = "";
		try
		{
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		    Date dateObject = new Date();
		    result = dateFormat.format(dateObject)+"."+decimal;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return result;
	}
	public static String now(int precission, String timezone)
	{
		if(precission > 6)
		{
			precission = 6;
		}
		if(precission < 0)
		{
			precission = 0;
		}
		long decimal = 0;
		long nanoSecond = System.nanoTime();
		if(precission == 6)
		{
			decimal = nanoSecond % 1000000;
		}
		else if(precission == 5)
		{
			decimal = nanoSecond % 100000;
		}
		else if(precission == 4)
		{
			decimal = nanoSecond % 10000;
		}
		else if(precission == 3)
		{
			decimal = nanoSecond % 1000;
		}
		else if(precission == 2)
		{
			decimal = nanoSecond % 100;
		}
		else if(precission == 1)
		{
			decimal = nanoSecond % 10;
		}
		String result = "";
		try
		{
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			dateFormat.setTimeZone(TimeZone.getTimeZone(timezone));
		    Date dateObject = new Date();
		    result = dateFormat.format(dateObject)+"."+decimal;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return result;
	}
	public static String now3()
	{
		String result = "";
		try
		{
			long miliSecond = System.nanoTime() % 1000;
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		    Date dateObject = new Date();
		    result = dateFormat.format(dateObject)+"."+miliSecond;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return result;
	}
	public static String now6()
	{
		String result = "";
		try
		{
			long microSecond = System.nanoTime() % 1000000;
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		    Date dateObject = new Date();
		    result = dateFormat.format(dateObject)+"."+microSecond;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return result;
	}
	/**
	 * Get current time with specified format
	 * @param format Time format
	 * @return Current time with specified format
	 */
	public static String now(String format)
	{
		String result = "";
		try
		{
			DateFormat dateFormat = new SimpleDateFormat(format);
		    Date dateObject = new Date();
		    result = dateFormat.format(dateObject);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return result;
	}
	public static String now(String format, String timezone)
	{
		String result = "";
		try
		{
			DateFormat dateFormat = new SimpleDateFormat(format);
			dateFormat.setTimeZone(TimeZone.getTimeZone(timezone));
		    Date dateObject = new Date();
		    result = dateFormat.format(dateObject);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return result;
	}
	public static long unixTime()
	{
		return System.currentTimeMillis() / 1000L;
	}
	/**
	 * Remove escape character of the JSON data
	 * @param input Data to be escaped
	 * @return Escaped string
	 */
	public static String escapeJSON(String input)
	{
		String output = "";
		if(input != null)
		{
			output = input.replaceAll("\"", "\\\"");
		}
		else
		{		
		}
		return output;
	}
	/**
	 * Remove escape characters of JSON string
	 * @param input JSON string to be clean up
	 * @return Clean JSON string
	 */
	public static String deescapeJSON(String input)
	{
		String output = input;
		output = output.replaceAll("\\\"", "\"");
		return output;
	}
	/**
	 * Escape HTML characters
	 * @param input HTML to be escaped
	 * @return Escaped HTML
	 */
    public static String escapeHTML(String input)
    {
    	String ret = input;
		ret = ret.replaceAll("&", "&amp;");
		ret = ret.replaceAll("\"", "&quot;");
		ret = ret.replaceAll("<", "&lt;");
		ret = ret.replaceAll(">", "&gt;");
    	return ret;
    }
    /**
     * Remove escape characters of HTML
     * @param input HTML to be clean up
     * @return Clean HTML
     */
    public static String deescapeHTML(String input)
    {
    	String ret = input;
 		ret = ret.replaceAll("&lt;", "<");
		ret = ret.replaceAll("&gt;", ">");
		ret = ret.replaceAll("&quot;", "\"");
		ret = ret.replaceAll("&amp;", "&");
     	return ret;
    }
}
