package com.planetbiru.pushclient.notification;

public interface Request {
	public void onMessageReceived(RemoteMessage message);
	public void onDataReceived(String[] headers, String command, String body);
	public void onDeletedMessages(String messageID);
	public void onNewToken(String token, String time, int timeZone);
	public void onChangeSetting(String name, String type, Object value);
	public void onError(Exception exception);	
	public void onRegisterDeviceSendSuccess(String deviceID, String message);
	public void onRegisterDeviceSendError(String deviceID, String message, String cause);
	public void onUnregisterDeviceSendSuccess(String deviceID, String message);
	public void onUnregisterDeviceSendError(String deviceID, String message, String cause);
	public void onRegisterDeviceSuccess(String deviceID, String message);
	public void onRegisterDeviceError(String deviceID, String message, String cause);
	public void onUnregisterDeviceSuccess(String deviceID, String message);
	public void onUnregisterDeviceError(String deviceID, String message, String cause);
	
}
