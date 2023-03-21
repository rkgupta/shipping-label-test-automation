package com.pb.testautomation.label.engine;

import java.io.File;

import org.apache.log4j.Logger;

import com.pb.testautomation.label.LabelComparator;
import com.pb.testautomation.label.LabelType;
import com.pb.testautomation.label.constant.CommonConstants;
import com.pb.testautomation.label.exception.ConfigException;
import com.pb.testautomation.label.layout.LayoutComparator;
import com.pb.testautomation.label.layout.report.LayoutReportHandler;
import com.pb.testautomation.label.report.ComparisonReportHandler;
import com.pb.testautomation.label.report.ReportHandler;
import com.pb.testautomation.label.util.CommonUtil;
import com.pb.testautomation.label.util.LabelUtil;
import com.pb.testautomation.label.util.Messages;
/**
 * Driver class for the application. This class is used for regression as well as 
 * 
 * <p>It creates a comparator instance of specific type as per the specified
 * label type and calls the compare method on it.</p>
 * @author RA013GU
 *
 */
public class LabelComparatorEngine {
	
	protected File master;
	protected File test;
	protected File configFile;
	private String mode;
	private File reportDir;
	private static LabelComparatorEngine engine = null;
	
	private static final String TESTING_MODE_COMPARISON = "compare";
	private static final String TESTING_MODE_LAYOUT = "layout";
	
	private static Logger logger = Logger.getLogger(LabelComparatorEngine.class);
	/**
	 * Constructor for initializing the engine. Used for regression testing.
	 * @param master - master label file/directory.
	 * @param test - test label file/directory.
	 * @param configFile - configuration file.
	 */
	private LabelComparatorEngine(File master, File test, File configFile) {
		this.master = master;
		this.test = test;
		this.configFile = configFile;
	}
	/**
	 * Constructor for initializing the engine. Used for layout testing.
	 * @param test
	 * @param configFile
	 */
	private LabelComparatorEngine (File test, File configFile){
		this.test = test;
		this.configFile = configFile;
	}

	public static LabelComparatorEngine getInstance() {
		return engine;
	}
	
	/**
	 * Compares master and test labels by invoking compare on appropriate comparator
	 * instance based on the <code>labelType</code>.
	 * @param labelType - The type of labels that are being tested.
	 * @throws ConfigException Exception while parsing the config file.
	 */
	public void compare(LabelType labelType) throws ConfigException {
		if(!master.exists()){
			logger.error(Messages.getMessage(CommonConstants.MISSING_FILE_EXCEPTION, master.getAbsolutePath()));
			return;
		}
		if(!test.exists()){
			logger.error(Messages.getMessage(CommonConstants.MISSING_FILE_EXCEPTION, test.getAbsolutePath()));
			return;
		}
		LabelComparator labelComparator = null;
		ReportHandler reportHandler = null;
		if(master.isDirectory() && test.isDirectory()) {
			logger.info(Messages.getMessage(CommonConstants.INFO_COMPARING_LABEL_DIRECTORIES, master.getAbsolutePath(), test.getAbsolutePath()));			
			long startTime = System.currentTimeMillis();
			
			labelComparator = LabelComparatorFactory.getLabelComparator(master, test, configFile, labelType);
			reportHandler = new ComparisonReportHandler(master, test);
			labelComparator.setReportHandler(reportHandler);
			labelComparator.compare();
			
			long endTime = System.currentTimeMillis();
			long executionTime = endTime - startTime;
			reportHandler.setExecutionTime(executionTime);
			
			reportDir = reportHandler.generateReport();
			logger.info(Messages.getMessage(CommonConstants.INFO_COMPARISON_EXECUTION_TIME, CommonUtil.getExecutionTimeFormat(executionTime)));
				
		}else if(master.isFile() && test.isFile()){
			if(!master.getName().equals(test.getName())){
				logger.error(Messages.getMessage(CommonConstants.LABEL_NAME_MISMATCH_EXCEPTION));
				return;
			}
			logger.info(Messages.getMessage(CommonConstants.INFO_COMPARING_LABEL_FILES, master.getAbsolutePath(), test.getAbsolutePath()));
			long startTime = System.currentTimeMillis();
			
			labelComparator = LabelComparatorFactory.getLabelComparator(master, test, configFile, labelType);
			reportHandler = new ComparisonReportHandler(master, test);
			labelComparator.setReportHandler(reportHandler);
			labelComparator.compare();
			
			long endTime = System.currentTimeMillis();
			long executionTime = endTime - startTime;
			reportHandler.setExecutionTime(executionTime);
			
			reportDir = reportHandler.generateReport();		
			logger.info(Messages.getMessage(CommonConstants.INFO_COMPARISON_EXECUTION_TIME, CommonUtil.getExecutionTimeFormat(executionTime)));
		}else{
			logger.error(Messages.getMessage(CommonConstants.INCOMPATIBLE_LABEL_TYPE_EXCEPTION));
			return;
		}

	}
	/**
	 * Tests the test label(s) by invoking compareLayout on appropriate comparator instance.
	 * @param labelType
	 * @throws ConfigException  
	 */
	public void compareLayout(LabelType labelType) throws ConfigException {
		if(!test.exists()) {
			logger.error(Messages.getMessage(CommonConstants.MISSING_FILE_EXCEPTION, test.getAbsolutePath()));
			return;
		}
		LayoutComparator layoutComparator = null;
		ReportHandler reportHandler = null;
		if(test.isDirectory()) {
			logger.info(Messages.getMessage(CommonConstants.INFO_TESTING_LAYOUT, test.getAbsolutePath()));
			long startTime = System.currentTimeMillis();
			
			layoutComparator = LayoutComparatorFactory.getLayoutComparator(test, configFile, labelType);
			reportHandler = new LayoutReportHandler(test);
			layoutComparator.setReportHandler(reportHandler);
			layoutComparator.compare();
			
			long endTime = System.currentTimeMillis();
			long executionTime = endTime - startTime;
			reportHandler.setExecutionTime(executionTime);			
			reportDir = reportHandler.generateReport();
			logger.info(Messages.getMessage(CommonConstants.INFO_LAYOUT_TEST_EXECUTION_TIME, CommonUtil.getExecutionTimeFormat(executionTime)));
		}
	}
	
	/**
	 * Application entry point. User inputs are captured in <code>args</code>.
	 * @param args
	 */
	public static void main(String[] args) {
		String master = getParamValue(args, "-master");
		String test = getParamValue(args, "-test");
		String config = getParamValue(args, "-config");
		String type = getParamValue(args, "-type");
		String mode = getParamValue(args, "-mode");
		
		LabelType labelType = LabelType.ALL;
		
		
		if(!CommonUtil.isEmpty(master) || !CommonUtil.isEmpty(test) || !CommonUtil.isEmpty(config)||!CommonUtil.isEmpty(mode)) {
			if(!CommonUtil.isEmpty(mode) && mode.equalsIgnoreCase(TESTING_MODE_LAYOUT)){
				//Use params from commandLine
				if(CommonUtil.isEmpty(test) || CommonUtil.isEmpty(config)) {
					usage();
				}
			}else{
				//Use params from commandLine
				if(CommonUtil.isEmpty(master) || CommonUtil.isEmpty(test) || CommonUtil.isEmpty(config)) {
					usage();
				}
			}			
		} else {	//No command line param is specified for any of master, test or config file. In this case, use values from the properties file.
			master = LabelUtil.getProperties().getProperty("master");
			test = LabelUtil.getProperties().getProperty("test");
			config = LabelUtil.getProperties().getProperty("config");
			mode = LabelUtil.getProperties().getProperty("mode");
			
			if(CommonUtil.isEmpty(master) || CommonUtil.isEmpty(test) 
					|| CommonUtil.isEmpty(config)) {
				logger.error("Please provide the master, test and config file location in the properties file or supply the command line arguments.");
				System.exit(1);
			}
		}
		
		if(!CommonUtil.isEmpty(type)) {
			if(LabelType.PDF.toString().equalsIgnoreCase(type)) {
				labelType = LabelType.PDF;
			} else if(LabelType.IMAGE.toString().equalsIgnoreCase(type)) {
				labelType = LabelType.IMAGE;
			} else if(LabelType.ALL.toString().equalsIgnoreCase(type)) {
				labelType = LabelType.ALL;
			} else {
				usage();					
			}
		}
		
		if(CommonUtil.isEmpty(mode)){
			mode = TESTING_MODE_COMPARISON;
		}else if(!mode.equalsIgnoreCase(TESTING_MODE_LAYOUT)&& !mode.equalsIgnoreCase(TESTING_MODE_COMPARISON)) {
			usage();
		}
		
		try {
			if(mode.equalsIgnoreCase(TESTING_MODE_COMPARISON)){
				engine = new LabelComparatorEngine(new File(master).getCanonicalFile(), new File(test).getCanonicalFile(), new File(config).getCanonicalFile());
				engine.mode = TESTING_MODE_COMPARISON;
				engine.compare(labelType);
			}else{
				engine = new LabelComparatorEngine(new File(test).getCanonicalFile(), new File(config).getCanonicalFile());
				engine.mode = TESTING_MODE_LAYOUT;
				engine.compareLayout(labelType);
			}			
		} catch (ConfigException e) {
			logger.error(Messages.getMessage(CommonConstants.CONFIG_EXCEPTION, config, e.getMessage()));
		} catch (Exception e){
			logger.error(Messages.getMessage(CommonConstants.INVOCATION_EXCEPTION, e.getMessage()));
		}
	}
	
	/*
	 * This method returns the value corresponding to a particular key supplied as a command line parameter
	 */
	private static String getParamValue(String[] args, String key) {
		for(int i = 0; i < args.length; i++) {
			if(key.equals(args[i])) {
				if(i+1 < args.length) {
					return args[i+1];
				}
			}
		}
		return null;
	}

	/**
	 * This method suggests the commands to invoke the application.
	 */
	private static void usage() {
	     System.err.println( "Usage: java -jar LabelTestAutomation.jar\n" +
	             "  -master <master label>	Master label file/directory\n" +
	             "  -test	<test label>		Test label file/directory\n" +
	             "  -config <config file>		The configuration file\n" +
	             "  -type <label type>		Label type (pdf/image/all)\n" +
	             "  -mode <testing mode>		Testing mode (compare/layout)\n"
	             );
	         System.exit( 1 );
	}
	/**
	 * @return the mode
	 */
	public String getMode() {
		return mode;
	}
	/**
	 * @param mode the mode to set
	 */
	public void setMode(String mode) {
		this.mode = mode;
	}
	
	public boolean isInComparisonMode() {
		return TESTING_MODE_COMPARISON.equalsIgnoreCase(mode);
	}
	
	public File getReportDirectory() {
		return reportDir;
	}
}
