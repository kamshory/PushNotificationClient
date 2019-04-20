package com.planetbiru.pushclient.utility;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * CryptoTool is class to encrypt and decrypt data.
 * @author Kamshory, MT
 *
 */
public class Encryption 
{
	/**
	 * Encryptor
	 */
    Cipher ecipher;
    /**
     * Decryptor
     */
    Cipher dcipher;
    /**
     * Constructor with key as String
     * @param key Key
     * @throws NoSuchPaddingException if padding is invalid
     * @throws NoSuchAlgorithmException if algorithm is not found
     * @throws InvalidKeyException if key is invalid
     */
    public Encryption(String key) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, NegativeArraySizeException, NullPointerException 
    {
    	 this.init(key);
    }
    /**
     * Constructor with key as SecretKey
     * @param key String key to encrypt or decrypt data
     * @throws NoSuchPaddingException if padding is invalid
     * @throws NoSuchAlgorithmException if algorithm is not found
     * @throws InvalidKeyException if keys is invalid
     */
    public Encryption(SecretKey key) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, NegativeArraySizeException, NullPointerException 
    {
    	this.init(key);
    }
    /**
     * Init key
     * @param key SecretKey to encrypt or decrypt data
     * @return true if success and false if failed
     * @throws NoSuchPaddingException if padding is invalid
     * @throws NoSuchAlgorithmException if algorith is not found
     * @throws InvalidKeyException if key is invalid
     */
    public boolean init(SecretKey key) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, NegativeArraySizeException, NullPointerException
    {
        ecipher = Cipher.getInstance("AES");
        dcipher = Cipher.getInstance("AES");
        ecipher.init(Cipher.ENCRYPT_MODE, key);
        dcipher.init(Cipher.DECRYPT_MODE, key);
        return true;
    }
    /**
     * Init key
     * @param key Key
     * @return true if success and false if failed
     * @throws NoSuchPaddingException if padding is invalid
     * @throws NoSuchAlgorithmException if algorithm is not found
     * @throws InvalidKeyException if key is invalid
     */
    public boolean init(String key) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, NegativeArraySizeException, NullPointerException
    {
    	while( key.length() < 16)
    	{
    		 key += "&";
    	}  	
    	byte[] bkey = (key).getBytes();
    	bkey = Arrays.copyOf(bkey, 16); // use only first 128 bit
    	SecretKeySpec skey2 = new SecretKeySpec(bkey, "AES");
        ecipher = Cipher.getInstance("AES");
        dcipher = Cipher.getInstance("AES");
        ecipher.init(Cipher.ENCRYPT_MODE, skey2);
        dcipher.init(Cipher.DECRYPT_MODE, skey2); 
        return true;   	
    }
    /**
     * Encrypt plain text into cipher text
     * @param input Plain text to be encrypted
     * @return String containing cipher text
     * @throws UnsupportedEncodingException if encoding is not supported
     * @throws BadPaddingException if padding is invalid
     * @throws IllegalBlockSizeException if block size is invalid
     */
    public String encrypt(byte[] input) throws UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException 
    {
        // Encrypt
        byte[] enc = ecipher.doFinal(input);  
        // Encode bytes to base64 to get a string
        return new String(Base64.getEncoder().encode(enc));            
    }
    /**
     * Decrypt cipher text into plain text
     * @param input Cipher text
     * @param base64 indicate that input is base 64 encoded
     * @return Plain text
     * @throws BadPaddingException if padding is invalid
     * @throws IllegalBlockSizeException if block size is invalid
     * @throws UnsupportedEncodingException if encoding is not supported
     */
    public String decrypt(byte[] input, boolean base64) throws IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException 
    {
        // Decode base64 to get bytes
        byte[] dec;
        // Decrypt
        byte[] utf8;
        if(base64)
        {
        	dec = Base64.getDecoder().decode(input); 
        }
        else
        {
        	dec = input;
        }
        utf8 = dcipher.doFinal(dec);
        // Decode using utf-8       
        return new String(utf8);
    }
    public String decrypt(byte[] input) throws IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException 
    {
        byte[] utf8 = dcipher.doFinal(input);
        // Decode using utf-8
        return new String(utf8);
    }
   /**
     * Encrypt plain text into cipher text
     * @param input Plain text to be encrypted
     * @param base64 indicate that output is base 64 encoded
     * @return String containing cipher text
     * @throws UnsupportedEncodingException if encoding is not supported
     * @throws BadPaddingException if padding is invalid
     * @throws IllegalBlockSizeException if block size is invalid
     */
    public String encrypt(String input, boolean base64) throws UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException 
    {
        // Encode the string into bytes using utf-8
        byte[] utf8 = input.getBytes();  
        // Encrypt
        byte[] enc = ecipher.doFinal(utf8);  
        // Encode bytes to base64 to get a string
        if(base64)
        {
            return new String(Base64.getEncoder().encode(enc));                   	
        }
        else
        {
        	return new String(enc);
        }
    }
    /**
     * Decrypt cipher text into plain text
     * @param input Cipher text
     * @param base64 indicate that input is base 64 encoded
     * @return Plain text
     * @throws BadPaddingException if padding is invalid
     * @throws IllegalBlockSizeException if block size is invalid
     * @throws UnsupportedEncodingException if encoding is not supported
     */
    public String decrypt(String input, boolean base64) throws IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException 
    {
    	byte[] dec;
    	byte[] utf8;
    	if(base64)
    	{
	        // Decode base64 to get bytes
	        dec = Base64.getDecoder().decode(input); 
    	}
    	else
    	{
    		dec = input.getBytes();
    	}
        // Decrypt
        utf8 = dcipher.doFinal(dec);
        // Decode using utf-8
        return new String(utf8);
    }
    /**
     * Base64 encoding
     * @param input String to be encoded
     * @return Encoded string
     */
    public String base64Encode(String input) throws NullPointerException
    {
		byte[] encodedBytes = Base64.getEncoder().encode(input.getBytes());
    	return new String(encodedBytes);
    }
    /**
     * Base64 decoding
     * @param input String to be decoded
     * @return Decoded string
     */
    public String base64Decode(String input) throws NullPointerException
    {
        byte[] decodedBytes = Base64.getDecoder().decode(input);
        return new String(decodedBytes);
    }
}