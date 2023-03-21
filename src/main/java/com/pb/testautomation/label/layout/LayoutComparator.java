package com.pb.testautomation.label.layout;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.log4j.Logger;

import com.pb.testautomation.label.constant.CommonConstants;
import com.pb.testautomation.label.exception.ConfigException;
import com.pb.testautomation.label.image.ImageType;
import com.pb.testautomation.label.layout.model.LabelTypeConfig;
import com.pb.testautomation.label.report.ReportHandler;
import com.pb.testautomation.label.util.CommonUtil;
import com.pb.testautomation.label.util.LabelUtil;
import com.pb.testautomation.label.util.Messages;

/**
 * Generic class that tests the layout of the labels. All implementing classes should 
 * provide specific implementation.
 * @author ra013gu
 *
 */
public abstract class LayoutComparator {
	private Logger logger = Logger.getLogger(LayoutComparator.class);
	
	// The test label file/directory.
	protected File test;
	// Image directory for test labels.
	protected File testImageDirectory;
	// Configuration file for layout testing. 
	protected File configFile;
	// Report handler for layout testing report.
	protected ReportHandler reportHandler;
	
	protected Map<String, LabelTypeConfig> layoutConfigMappings = new HashMap<String, LabelTypeConfig>();
	
	/**
	 * Constructor for LayoutComparator.
	 * @param test the test label file/directory.
	 * @param configFile the configuration file.
	 */
	public LayoutComparator(File test, File configFile) {
		this.test = test;
		this.testImageDirectory = getImageDirectory(test);
		this.configFile = configFile;
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
	 * Template method used by all layout comparators to test layout.
	 * @throws ConfigException 
	 */
	public final void compare() throws ConfigException {
		loadConfigFile();
		processLabels();
		compareLayout();
	}
	/**
	 * Loads the configuration from the layout config file.
	 * @throws ConfigException
	 */
	private void loadConfigFile() throws ConfigException {
		InputStream is = null;
		try {
			Object obj = LabelUtil.getProperties().get("validate_config_file");
			boolean validateConfigFile = true;
			if(obj != null && !CommonUtil.isEmpty(obj.toString())) {
				validateConfigFile = Boolean.parseBoolean(obj.toString());
			}
			
			LayoutConfigFileParser handler = new LayoutConfigFileParser(configFile, validateConfigFile);

			is = new FileInputStream(configFile);
			SAXParserFactory spf = SAXParserFactory.newInstance();
			spf.setNamespaceAware(validateConfigFile);
			spf.setValidating(validateConfigFile);
			
			SAXParser sp = spf.newSAXParser();
			if(validateConfigFile) {
				sp.setProperty(CommonConstants.JAXP_SCHEMA_LANGUAGE, CommonConstants.W3C_XML_SCHEMA); 
				
				InputStream in = ClassLoader.getSystemResourceAsStream("layout-testing-config.xsd");
				sp.setProperty(CommonConstants.JAXP_SCHEMA_SOURCE, in);				
			}
			
			sp.parse(is, handler);			
			layoutConfigMappings = handler.getConfigFileMappings();			
		} catch(Exception e) {
			if(e instanceof ConfigException) {
				throw (ConfigException)e;
			} else {
				throw new ConfigException(e);				
			}
		} finally {
			if(is != null) {
				try {
					is.close();
				} catch (IOException e) {
					logger.error(Messages.getMessage(CommonConstants.IO_EXCEPTION, "while closing file: ", configFile.getAbsolutePath(), e.getMessage()));
				}
			}
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
	
	/**
	 * Returns all the images from the image directory which matches the given label name.
	 * @param labelName
	 * @return
	 */
	protected File[] getPages(final String labelName){
		return testImageDirectory.listFiles(new FilenameFilter() {			
			public boolean accept(File dir, String name) {
				Pattern pattern = Pattern.compile(labelName + CommonConstants.PDF_PAGE_NUM_TO_IMG_NUM_PREFIX + "\\d{1,}." + ImageType.jpeg);
				Matcher matcher = pattern.matcher(name);
				return matcher.find();
			}

		});
	}
	/**
	 * Abstract method. To be overwritten by all implementing layout comparators.
	 */
	protected abstract void compareLayout();
	/**
	 * Abstract method. To be overwritten by all implementing layout comparators.
	 */
	protected abstract void processLabels();
	

}
