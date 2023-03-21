package com.pb.testautomation.label.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.pb.testautomation.label.constant.CommonConstants;
import com.pb.testautomation.label.exception.LabelException;
import com.pb.testautomation.label.exception.UnknownConversionUnitException;
/**
 * Utility class for Label related operations.
 * @author RA013GU
 *
 */
public class LabelUtil {
	
	private static LabelUtil instance = new LabelUtil();
	public static Properties properties;
	
	private static Logger logger = Logger.getLogger(LabelUtil.class);
	
	public static final String PDF_EXTENSION = "pdf";
	public static final String LABEL_TYPE_FILE_NAME_DELIMITER = "-";
	public static final String DOT = ".";
	public static final String COMMA = ",";
	public static final String RELATIVE_IMAGE_DIR = File.separator + "images" + File.separator;
	public static final int RESOLUTION_DPI = 300;
	
	public static final String SYSTEM_PROPERTIES_FILE_NAME = "label-test-automation.properties";
	public static final File CONFIG_FILE_TIMESTAMP = new File(System.getProperty("user.home")+File.separator+"test-automation-config.timestamp");
	public static final File MASTER_LABEL_TIMESTAMP = new File(System.getProperty("user.home")+File.separator+"master-label.timestamp");
	public static final File MASTER_LABEL_DATA_CACHE_FILE = new File(System.getProperty("user.home")+File.separator+"master-label-data.ser");
	
	/*
	 * Load the system properties when the class is loaded.
	 */
	static{
		loadProperties();
	}
	
	/**
	 * Singleton		
	 */
	private LabelUtil(){
		
	}
	/**
	 * 
	 * @return LabelUtil singleton instance.
	 */
	public static LabelUtil getInstance(){
		return instance;
	}
	
	/**
	 * @param uom
	 * @param resolution
	 * @return the scaling factor.
	 * @throws LabelException
	 * @throws UnknownConversionUnitException
	 */
	public float getScaleFactorForImage(String uom, int resolution)
			throws LabelException, UnknownConversionUnitException {
		float scale = 1.0f;
		if(CommonConstants.UOM_CM.equalsIgnoreCase(uom)) {
        	if(resolution <= 0) {
        		throw new LabelException("Resolution of the image must be specified if the measurement unit is in cm.");
        	}
        	scale = CommonConstants.CONVERSION_FACTOR_CM_TO_INCH * resolution;
        } else if(CommonConstants.UOM_MM.equalsIgnoreCase(uom)) {
        	if(resolution <= 0) {
        		throw new LabelException("Resolution of the image must be specified if the measurement unit is in mm.");
        	}
        	scale = CommonConstants.CONVERSION_FACTOR_MM_TO_INCH * resolution;
        } else if(CommonConstants.UOM_INCH.equalsIgnoreCase(uom)) {
        	if(resolution <= 0) {
        		throw new LabelException("Resolution of the image must be specified if the measurement unit is in inches.");
        	}
        	scale = 1.0f * resolution;
        } else if(CommonUtil.isEmpty(uom) || CommonConstants.UOM_PIXELS.equalsIgnoreCase(uom)) {
        	scale = 1.0f;	//Default is px
        } else {
        	throw new UnknownConversionUnitException("Conversion unit " + uom + " is unknown.");
        }
		return scale;
	}
	
	public float getScalingFactorForPDF(String uom) throws UnknownConversionUnitException {
		float scale = 1.0f;
        if(CommonConstants.UOM_CM.equalsIgnoreCase(uom)) {
        	scale = CommonConstants.CONVERSION_FACTOR_CM_TO_INCH;
        } else if(CommonConstants.UOM_MM.equalsIgnoreCase(uom)) {
        	scale = CommonConstants.CONVERSION_FACTOR_MM_TO_INCH;
        } else if(CommonConstants.UOM_INCH.equalsIgnoreCase(uom)) {
        	scale = 1.0f;
        } else if(CommonUtil.isEmpty(uom)) {
        	scale = 1.0f;	//Default is Inch
        } else {
        	throw new UnknownConversionUnitException("Conversion unit " + uom + " is unknown");
        }
        
        return scale * RESOLUTION_DPI;
	}
	
	/**
	 * Loads the system properties file.
	 */
	public static void loadProperties(){
		logger.info("Loading the system properties file: "+ SYSTEM_PROPERTIES_FILE_NAME);
		
		InputStream is = null;
		String userDir = new File(System.getProperty("user.dir")).getPath();
		File propertiesFile = new File(userDir+File.separator+SYSTEM_PROPERTIES_FILE_NAME);
		
		if(!propertiesFile.exists()) {
			userDir = new File(System.getProperty("user.dir")).getParent();
			propertiesFile = new File (userDir+File.separator+SYSTEM_PROPERTIES_FILE_NAME);
			
			if(!propertiesFile.exists()) {
				is = ClassLoader.getSystemResourceAsStream(SYSTEM_PROPERTIES_FILE_NAME);
			}
		}
		try {
			if(is == null && propertiesFile != null) {
				is = new FileInputStream(propertiesFile);
			}
			// Read properties file.
			properties = new Properties();
			properties.load(is);			
		} catch(Exception e) {
			logger.error(Messages.getMessage(CommonConstants.IO_EXCEPTION, "while loading properties file ", SYSTEM_PROPERTIES_FILE_NAME, e.getMessage()));
		}
	}
	
	/**
	 * Returns the system properties. 
	 * @return
	 */	
	public static Properties getProperties() {
		return properties;
	}

	public static void main(String[] args) {

		System.out.println(System.getProperty("user.dir"));
		String parentDir = new File(System.getProperty("user.dir")).getParent();
		File propertiesFile = new File (parentDir+File.separator+SYSTEM_PROPERTIES_FILE_NAME);
		// Read properties file.
		Properties properties = new Properties();
		try {
		    properties.load(new FileInputStream(propertiesFile));
		} catch (IOException e) {
			logger.error(e.getMessage());
		}

	}
	
}
