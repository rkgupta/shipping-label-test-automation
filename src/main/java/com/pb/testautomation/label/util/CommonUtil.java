package com.pb.testautomation.label.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;

import com.pb.testautomation.label.LabelType;
import com.pb.testautomation.label.image.ImageType;

/**
 * This class represents the common utility class. It defines application level
 * common operations.
 * 
 * @author ra013gu
 * @author ta013ba
 */
public class CommonUtil {
	
	/*
	 * Create a DateFormatter object for displaying date information.
	 */
    private static DateFormat formatter = new SimpleDateFormat("dd.MM.yyyy hh.mm.ss.SSS a");
    /**
     * Checks whether the given string is empty.
     * @param str - String that is checked.
     * @return true/ false
     */
	public static boolean isEmpty(String str) {
		return str == null || str.trim().length() == 0;
	}
	
	public static Float parseFloat(String str) {
		if(isEmpty(str)) {
			return null;
		}
		try {
			return Float.parseFloat(str);
		}catch(NumberFormatException e) {
		}
		return null;
	}
	/**
	 * Returns the extension of a given file name without a "."
	 * @param fileName - file name.
	 * @return extension of the fiven file name. 
	 */
	public static String getExtension(String fileName) {
		int dotIndex = fileName.lastIndexOf(".");
		if(dotIndex > -1) {
			return fileName.substring(dotIndex + 1);
		}
		return null;
	}
	
	/**
	 * Returns the extension of a given file name without a "."
	 * @param fileName - file name.
	 * @return extension of the fiven file name. 
	 */
	public static String getNameWithoutExt(String fileName) {
		int dotIndex = fileName.lastIndexOf(".");
		if(dotIndex > -1) {
			return fileName.substring(0, dotIndex);
		}
		return fileName;
	}
	
	/**
	 * Serializes the master label data into the file master-label-data.ser located in user profile directory.
	 * @param masterLabelDataCache
	 * @throws IOException
	 */
	public static void serialize(Map<String, String> masterLabelDataCache)throws IOException {
		if (!masterLabelDataCache.isEmpty()) {
			FileOutputStream fos = new FileOutputStream(LabelUtil.MASTER_LABEL_DATA_CACHE_FILE);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(masterLabelDataCache);
			oos.close();
		}
	}
	/**
	 * De-serializes the given input file and return the deserialized object.
	 * @param serializedFile - the file containing serialized data.
	 * @return The de-serialized object.
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static Object deSerialize(File serializedFile) throws IOException, ClassNotFoundException {
		FileInputStream fis = new FileInputStream(serializedFile);
		ObjectInputStream ois = new ObjectInputStream(fis);
		Object object = ois.readObject();
		ois.close();
		return object;
	}
	/**
	 * Converts the given time in millis into "x mins y secs z millisecs" format.
	 * @param time - time is millis
	 * @return time in (m:s:ms) format.
	 */
	public static String getExecutionTimeFormat(long time){
		long min = time/(60*1000);
		time = time - (min*60*1000);
		long sec = time/1000;
		long millis = time%1000;		
		return String.format("%d m %d s %d ms", min,sec,millis);
	}
	/**
	 * Converts time in millis to Simple date time format.
	 * @param time - time in millis.
	 * @return time in "dd.MM.yyyy hh.mm.ss.SSS a" format.
	 */
	public static String getSimpleDateTimeFormat(long time){
		/*
		 * Create a calendar object that will convert the date and time value 
		 * in milliseconds to date. 
		 */
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        return formatter.format(calendar.getTime());
        
	}

	/**
	 * Returns the most recent modification time of the file/ directory.
	 * @param dir - directory
	 * @return most recent modification time of the directory. 
	 */
	public static long getLastModifiedTime(File dir){
		long max_lastModified = dir.lastModified();
		if(dir.isDirectory()){
			File [] files = dir.listFiles();
			for(File file : files){
				max_lastModified = (max_lastModified > file.lastModified())? max_lastModified : file.lastModified();
			}
		}
		return max_lastModified;
	}
	/**
	 * Checks whether the image with given extension is supported.
	 * @param extension
	 * @return true/ false.
	 */
	public static boolean isSupportedImageExtension(String extension) {
		if(isEmpty(extension)) {
			return false;
		}
		return ImageType.gif.toString().equalsIgnoreCase(extension) 
			|| ImageType.png.toString().equalsIgnoreCase(extension);
	}
	/**
	 * Returns the type of label i.e either PDF or IMAGE
	 * @param labelName - name of the label.
	 * @return the type of label i.e. PDF or IMAGE
	 */
	public static String getLabelType(String labelName){
		String type = getExtension(labelName);
		if(LabelUtil.PDF_EXTENSION.equalsIgnoreCase(type)){
			type = LabelType.PDF.toString();
		}else {
			type = LabelType.IMAGE.toString();
		}
		return type;
	}
}
