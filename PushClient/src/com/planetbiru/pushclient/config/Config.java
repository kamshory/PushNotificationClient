package com.planetbiru.pushclient.config;

/**
 * Config
 * @author Kamshory, MT
 *
 */
public class Config {
	/**
	 * Server address. Change this address according to production server address
	 */
	public static String serverAddress = "127.0.0.1";
	/**
	 * Server port number. Change this port number according to production server port number
	 */
	public static int serverPort = 92;
	/**
	 * Connection timeout
	 */
	public static int timeout = 30000;
	/**
	 * API Key. Change this key with your own key
	 */
	public static String apiKey = "THE-API-KEY";
	/**
	 * Group key
	 */
	public static String groupKey = "THE-GROUP-KEY";
	/**
	 * API Password
	 */
	public static String password = "THE-API-PASSWORD";
	/**
	 * Wait to reconnect to the server
	 */
	public static long delayReconnect = 20000;
	/**
	 * Wait to restart API
	 */
	public static long delayRestart = 20000;
	/**
	 * Count down reconnect
	 */
	public static int countDownReconnect = 5;
	/**
	 * Debug mode
	 */
	public static boolean debugMode = true;
	/**
	 * Re check connection
	 */
	public static boolean recheckConnection = false;
	/**
	 * User agent
	 */
	public static String userAgent = "Push-Notification-Client version 1.0.0";
}
