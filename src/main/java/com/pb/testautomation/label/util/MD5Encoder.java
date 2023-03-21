package com.pb.testautomation.label.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.log4j.Logger;

import com.pb.testautomation.label.constant.CommonConstants;
/**
 * MD5 encoder class. This class encodes byte array data and returns
 * hexadecimal equivalent of the same.
 * @author RA013GU
 *
 */
public class MD5Encoder {
	
	private static Logger logger = Logger.getLogger(MD5Encoder.class);
	
	/**
	 * Generates MD5 hash for given byte array data.
	 * @param data - byte array data.
	 * @return Hexadecimal equivalent of generated MD5 Hash.
	 */
	public static String encode(byte[] data){
	    MessageDigest md = null;	    
		try {
			md = MessageDigest.getInstance("MD5");		
		} catch (NoSuchAlgorithmException e) {
			logger.error(Messages.getMessage(CommonConstants.NO_SUCH_ALGORITHM_EXCEPTION, e.getMessage()));
		}
		if(data != null){
			md.update(data);
			byte[] hash = md.digest();	    
			return returnHex(hash);
		}
		return null;	   
	}
	/**
	 * 
	 * @param hash - MD5 hash value in the form of byte array. 
	 * @return hexadecimal equivalent value for the given hash.
	 */
	private static String returnHex(byte[] hash) {
		String hexString = "";
        for (int i=0; i < hash.length; i++) { //for loop ID:1
            hexString +=
            Integer.toString((hash[i] & 0xff) + 0x100, 16).substring(1);
        } 
        return hexString;
	}
	
	public static void main(String[] args) {
		System.out.println(MD5Encoder.encode(null));
	}

}
