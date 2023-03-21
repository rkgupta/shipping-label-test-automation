package com.pb.testautomation.label;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import com.pb.testautomation.label.constant.CommonConstants;
import com.pb.testautomation.label.exception.ConfigException;
import com.pb.testautomation.label.image.ImageUtil;
import com.pb.testautomation.label.report.ReportHandler;
import com.pb.testautomation.label.util.CommonUtil;
import com.pb.testautomation.label.util.Messages;
import com.pb.testautomation.label.util.LabelUtil;
import com.pb.testautomation.label.util.MD5Encoder;
import com.pb.testautomation.label.util.MaskingUtil;

/**
 * Generic class that can be used to compare labels. It provides a template
 * that can be used by all implementing classes to compare the labels specifically.
 * @author RA013GU
 *
 */
public abstract class LabelComparator {
	protected File master;
	protected File test;
	protected File masterImageDirectory;
	protected File testImageDirectory;
	protected File configFile;
	protected boolean isConfigFileModified;
	protected boolean isMasterLabelModified;
	protected Map<String, String> masterLabelDataCache = new HashMap<String, String>();
	
	private String labelType;
	protected ReportHandler reportHandler;
	protected Map<String, File> commonTestLabelsMap = new HashMap<String, File>();
	
	public static Logger logger = Logger.getLogger(LabelComparator.class);
	
	public LabelComparator(File master, File test, File configFile) {
		this.master = master;
		this.test = test;
		this.configFile = configFile;
		this.masterImageDirectory = getImageDirectory(master);
		this.testImageDirectory = getImageDirectory(test);
	}

	/**
	 * @return the master
	 */
	public File getMaster() {
		return master;
	}

	/**
	 * @return the test
	 */
	public File getTest() {
		return test;
	}

	/**
	 * @return the reportHandler
	 */
	public ReportHandler getReportHandler() {
		return reportHandler;
	}

	/**
	 * @param reportHandler the reportHandler to set
	 */
	public void setReportHandler(ReportHandler reportHandler) {
		this.reportHandler = reportHandler;
	}

	/**
	 * @return the labelType
	 */
	public String getLabelType() {
		return labelType;
	}

	/**
	 * @param labelType the labelType to set
	 */
	public void setLabelType(String labelType) {
		this.labelType = labelType;
	}

	/**
	 * Template method for comparing labels. Subclasses should provide
	 * implementation according to their nature.
	 * @throws ConfigException 
	 */
	public final void compare() throws ConfigException {
		checkConfigFileLastModifiedTime();
		checkMasterLabelLastModifiedTime();
		loadMaskAreaFromPropertyFile();
		loadMasterLabelDataCache();
		processMasterLabels();
		processTestLabels();
		compareLabels();
	}	

	/**
	 * Checks if the config file has been modified or accessed for the first time, in which case the config file timestamp is saved in a file
	 * named <code>test-automation-config.timestamp</code> under the user home directory.. 
	 */
	private void checkConfigFileLastModifiedTime() {		
		String lastModifiedTimeHash = null;
		File configFileTimestamp = LabelUtil.CONFIG_FILE_TIMESTAMP;
		if(configFileTimestamp.exists()){
			try {
				 lastModifiedTimeHash = FileUtils.readFileToString(configFileTimestamp);
				 if(!MD5Encoder.encode(Long.toString(configFile.lastModified()).getBytes()).equals(lastModifiedTimeHash)){
						isConfigFileModified = true;						
						logger.info(Messages.getMessage(CommonConstants.INFO_CONFIG_FILE_TIMESTAMP_MODIFIED));
				 }
			} catch (IOException e) {
				logger.error(Messages.getMessage(CommonConstants.IO_EXCEPTION, "while reading from file: " ,configFileTimestamp.getAbsolutePath(), e.getMessage()));
			}				
		}else{
			isConfigFileModified = true;
			try {
				logger.info("Creating file: test-automation-config.timestamp to store the last modified time of the config file ...");
				FileUtils.writeStringToFile(configFileTimestamp, MD5Encoder.encode(Long.toString(configFile.lastModified()).getBytes()));
			} catch (IOException e) {
				logger.error(Messages.getMessage(CommonConstants.IO_EXCEPTION, "while writing to file: ",configFileTimestamp.getAbsolutePath(), e.getMessage()));
			}
			
		}
	}
	/**
	 * Checks whether the master labels have been modified since last access.
	 */
	private void checkMasterLabelLastModifiedTime() {
		String masterLabelTimestampHash = null;
		File masterLabelTimeStamp = LabelUtil.MASTER_LABEL_TIMESTAMP;
		if(masterLabelTimeStamp.exists()){
			try {
				masterLabelTimestampHash = FileUtils.readFileToString(masterLabelTimeStamp);
				 if(!MD5Encoder.encode((Long.toString(CommonUtil.getLastModifiedTime(master))+this.getLabelType()).getBytes()).equals(masterLabelTimestampHash)){
					 isMasterLabelModified = true;						
					 logger.info(Messages.getMessage(CommonConstants.INFO_MASTER_LABEL_TIMESTAMP_MODIFIED));
				 }
			} catch (IOException e) {
				logger.error(Messages.getMessage(CommonConstants.IO_EXCEPTION, "while reading from file: " ,masterLabelTimeStamp.getAbsolutePath(), e.getMessage()));
			}				
		
		}else{
			isMasterLabelModified = true;			
			try {
				logger.info("Creating file: master-label.timestamp to store the last modified time of the master label file/folder ...");
				FileUtils.writeStringToFile(masterLabelTimeStamp, MD5Encoder.encode((Long.toString(CommonUtil.getLastModifiedTime(master))+this.getLabelType()).getBytes()));
			} catch (IOException e) {
				logger.error(Messages.getMessage(CommonConstants.IO_EXCEPTION, "while writing to file: " ,masterLabelTimeStamp.getAbsolutePath(), e.getMessage()));
			}
		}
		
	}
	
	/**
	 * Loads masking areas reading from the config file.
	 * @throws ConfigException
	 */
	private void loadMaskAreaFromPropertyFile() throws ConfigException {
		Object obj = LabelUtil.getProperties().get("validate_config_file");
		boolean validateConfigFile = true;
		if(obj != null && !CommonUtil.isEmpty(obj.toString())) {
			validateConfigFile = Boolean.parseBoolean(obj.toString());
		}
		
		MaskingUtil.getInstance().loadMaskAreaFromPropertyFile(configFile, validateConfigFile);
		
	}
	/**
	 * Loads the cached values of the master label data in case the config file has not changed.
	 * 
	 */
	private void loadMasterLabelDataCache() {
		if(!isConfigFileModified && !isMasterLabelModified){
			try {
				logger.info(Messages.getMessage(CommonConstants.INFO_LOADING_MASTER_LABEL_HASH_DATA_FROM_CACHE));
				masterLabelDataCache.putAll((Map<String, String>)CommonUtil.deSerialize(LabelUtil.MASTER_LABEL_DATA_CACHE_FILE));
			} catch (IOException e) {
				logger.error(Messages.getMessage(
						CommonConstants.IO_EXCEPTION,
						"while reading from file: ",
						LabelUtil.MASTER_LABEL_DATA_CACHE_FILE
								.getAbsolutePath(), e.getMessage()));
			} catch (ClassNotFoundException e) {
				logger.error(Messages.getMessage(CommonConstants.CLASS_NOT_FOUND_EXCEPTION, e.getMessage()));
			}
		}
	}

	/**
	 * Label comparators process the master labels in their own way.
	 */
	abstract void processMasterLabels();	
	/**
	 * Label comparators process the test labels in their own way.
	 */
	abstract void processTestLabels();	
	/**
	 * Label comparators (PDF/Image) compare the labels in their own way.
	 */
	abstract void compareLabels();	
	/**
	 * After the master has been processed, cache the data and reset the config file timestamp and master label timestamp. 
	 */
	protected void cacheMasterImageHashData() {
		File[] masterImages = masterImageDirectory.listFiles();
		for(File masterImage : masterImages){
			masterLabelDataCache.put(masterImage.getName(), MD5Encoder.encode(ImageUtil.getInstance().getBytes(masterImage)));
		}
		try {
			logger.info(Messages.getMessage(CommonConstants.INFO_WRITING_MASTER_LABEL_HASH_DATA_IN_CACHE));
			CommonUtil.serialize(masterLabelDataCache);
			logger.info(Messages.getMessage(CommonConstants.INFO_UPDATING_CONFIG_FILE_TIMESTAMP));
			FileUtils.writeStringToFile(LabelUtil.CONFIG_FILE_TIMESTAMP, MD5Encoder.encode(Long.toString(configFile.lastModified()).getBytes()));
			logger.info(Messages.getMessage(CommonConstants.INFO_UPDATING_MASTER_LABEL_TIMESTAMP));
			FileUtils.writeStringToFile(LabelUtil.MASTER_LABEL_TIMESTAMP, MD5Encoder.encode((Long.toString(CommonUtil.getLastModifiedTime(master))+this.getLabelType()).getBytes()));
		} catch (IOException e) {
			logger.error(Messages.getMessage(CommonConstants.IO_EXCEPTION, "while caching master image hash data", "", e.getMessage()));
		}
	}
	
	/**
	 * Creates image directory relative to the given directory.
	 * @param dir - directory containing the labels relative to which the images directory is created.
	 */
	protected File getImageDirectory(File file) {
		File imageDirectory = null;
		if(file.isDirectory()){
			imageDirectory = new File(file.getAbsolutePath()+ LabelUtil.RELATIVE_IMAGE_DIR);	
		}else{
			imageDirectory = new File(file.getParentFile().getAbsoluteFile()+LabelUtil.RELATIVE_IMAGE_DIR);
		}			
		if(!imageDirectory.exists()){
			imageDirectory.mkdir();		
		}
		return imageDirectory;
	}
}
